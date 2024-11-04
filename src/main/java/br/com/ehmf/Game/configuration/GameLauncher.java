package br.com.ehmf.Game.configuration;

import br.com.ehmf.Game.GameScreen;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import org.springframework.boot.CommandLineRunner;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import org.springframework.stereotype.Component;

@Component
public class GameLauncher implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Spring Game");
        config.setWindowedMode(800, 600); // Tamanho da janela do jogo
        config.useVsync(true);

        new Lwjgl3Application(new GameScreen(), config); // Inicia o jogo com a tela principal

    }
}
