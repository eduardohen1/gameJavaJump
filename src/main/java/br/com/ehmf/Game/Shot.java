package br.com.ehmf.Game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Shot {
    private Texture texture;
    private Vector2 position;
    private float speed = 10f;
    private Rectangle bounds;

    public Shot(float x, float y) {
        texture = new Texture("shot.png");
        position = new Vector2(x, y);
        bounds = new Rectangle(position.x, position.y, texture.getWidth(), texture.getHeight());
    }

    public void update() {
        position.x += speed;  // Move o tiro para a direita
        bounds.setPosition(position.x, position.y);
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float getX() {
        return position.x;
    }

    public void dispose() {
        texture.dispose();
    }

}
