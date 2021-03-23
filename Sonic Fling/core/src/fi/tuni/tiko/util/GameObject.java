package fi.tuni.tiko.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public abstract class GameObject {
    private Texture texture;
    private Rectangle rectangle;

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture t) {
        texture = t;
    }

    public float getX() {
        return rectangle.x;
    }

    public void setX(float x) {
        rectangle.x = x;
    }

    public void setY(float y) {
        rectangle.y = y;
    }

    public float getY() {
        return rectangle.y;
    }

    public float getWidth() {
        return rectangle.width;
    }

    public float getHeight() {
        return rectangle.height;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle r) {
        rectangle = r;
    }

    abstract void move();

    abstract void draw(SpriteBatch batch);

    abstract void dispose();
}
