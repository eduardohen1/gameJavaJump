package br.com.ehmf.Game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Objects;

@Component
public class GameScreen extends ApplicationAdapter implements ApplicationListener {

    private SpriteBatch batch;
    private Texture background;
    private Player player;
    private Array<Obstacle> obstacles;
    private long lastObstacleTime;
    private int score;
    private int powers; //poder
    private Array<Shot> shots; //tiros
    private Texture explosionTexture;
    private BitmapFont font;
    private Sound shotSound;

    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture(Gdx.files.internal("city_background.png"));
        explosionTexture = new Texture(Gdx.files.internal("explosion.png")); //
        shotSound = Gdx.audio.newSound(Gdx.files.internal("shot16bits.wav"));
        player = new Player();
        obstacles = new Array<>();
        spawnObstacle();

        score = 0;
        powers = 0;
        shots = new Array<>();

        font = new BitmapFont();
    }

    @Override
    public void render() {
        // Limpa a tela
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Atualiza a lógica do jogo
        update();

        // Inicia o desenho dos elementos
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        player.draw(batch);

        for (Obstacle obstacle : obstacles) {
            obstacle.draw(batch);
        }

        for(Shot shot : shots) {
            shot.draw(batch);
        }

        font.draw(batch, "Score: " + score, 10, Gdx.graphics.getHeight() - 10);
        font.draw(batch, "Powers: " + powers, 10, Gdx.graphics.getHeight() - 30);

        batch.end();
    }

    private void update(){
        // Verifica se a tecla de espaço foi pressionada
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            player.jump();
        }

        //usar o poder
        if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && powers > 0) {
            userPower();
        }

        player.update();

        // Verifica se é hora de gerar um novo obstáculo
        if (TimeUtils.nanoTime() - lastObstacleTime > 2000000000) {  // A cada 2 segundos, cria um obstáculo
            spawnObstacle();
        }

        // Atualiza a posição de cada obstáculo e verifica colisões
        for (Obstacle obstacle : obstacles) {
            obstacle.update();
            if (obstacle.getBounds().overlaps(player.getBounds())) {
                // Lógica de colisão (reiniciar ou finalizar o jogo)
                Gdx.app.log("Game", "Game Over! Player hit an obstacle.");
                resetGame();
                break;
            }

            if(obstacle.getPosition().x < -obstacle.getWidth()) {
                score++;
                if(score % 2 == 0)
                    powers++;
            }
        }

        //atualiza a posição de cada tiro e verifica colisões com os obstáculos
        for(Shot shot : shots) {
            shot.update();
            for(Obstacle obstacle : obstacles) {
                if(shot.getBounds().overlaps(obstacle.getBounds())) {
                    batch.begin();
                    batch.draw(explosionTexture, obstacle.getPosition().x, obstacle.getPosition().y);
                    batch.end();
                    shots.removeValue(shot, true);
                    obstacles.removeValue(obstacle, true);
                    break;
                }
            }
        }

        // Remover obstáculos fora da tela
        for (Iterator<Obstacle> iterator = obstacles.iterator(); iterator.hasNext(); ) {
            Obstacle obstacle = iterator.next();
            if (obstacle.getPosition().x < -obstacle.getWidth()) {
                iterator.remove();
            }
        }

        //remover tiros fora da tela
        for(Iterator<Shot> iterator = shots.iterator(); iterator.hasNext(); ) {
            Shot shot = iterator.next();
            if(shot.getX() > Gdx.graphics.getWidth()) {
                iterator.remove();
            }
        }
    }

    private void spawnObstacle() {
        Obstacle obstacle = new Obstacle(Gdx.graphics.getWidth(), 100);  // Cria o obstáculo fora da tela (direita)
        obstacles.add(obstacle);
        lastObstacleTime = TimeUtils.nanoTime();
    }

    private void resetGame() {
        obstacles.clear();
        player.reset();
        score = 0;
        spawnObstacle();
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        player.dispose();
        font.dispose();
        for (Obstacle obstacle : obstacles) {
            obstacle.dispose();
        }
        explosionTexture.dispose();
        for(Shot shot : shots) {
            shot.dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    private void userPower(){
        if(powers > 0){
            Shot shot = new Shot(player.getPosition().x + player.getBounds().width, player.getPosition().y + player.getBounds().height / 2);
            shots.add(shot);
            shotSound.play();
            powers--;
            Gdx.app.log("Game", "Power used! Powers left: " + powers);
        }
    }
}
