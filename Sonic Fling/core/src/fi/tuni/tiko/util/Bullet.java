package fi.tuni.tiko.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;

public class Bullet extends Sprite {
    private ArrayList<Bullet> Bullets = new ArrayList<Bullet>();
    private Texture bullet;
    private Rectangle bulletHitbox;
    private float velocityX;
    private float velocityY;
    private float speed = 3f;

    public Bullet(float enemyX, float enemyY, int rotation) {
        bullet = new Texture("Bullet.png");
        bulletHitbox = new Rectangle(enemyX,
                                    enemyY,
                                    bullet.getWidth()/60f,
                                    bullet.getHeight()/60f);
        setRotation(rotation);
        velocityX = speed * (float)Math.cos(Math.toRadians(rotation - 90));
        velocityY = speed * (float)Math.sin(Math.toRadians(rotation - 90));
    }

    public void newBullet(float enemyX, float enemyY, int bulletNumber) {
        int rotation = 0;
        switch (bulletNumber) {
            case 1: rotation = 50;
                    break;
            case 2: rotation = 0;
                    break;
            case 3: rotation = -50;
        }
        Bullets.add(new Bullet(enemyX, enemyY, rotation));
    }

    public void move() {
        for (int i = 0; i < Bullets.size(); i++) {
            Bullets.get(i).bulletHitbox.setX(Bullets.get(i).bulletHitbox.getX()
                                            + Bullets.get(i).velocityX * Gdx.graphics.getDeltaTime());
            Bullets.get(i).bulletHitbox.setY(Bullets.get(i).bulletHitbox.getY()
                    + Bullets.get(i).velocityY * Gdx.graphics.getDeltaTime());

            if (Bullets.get(i).bulletHitbox.getX() < 0 - bulletHitbox.width
                || Bullets.get(i).bulletHitbox.getX() > 16
                || Bullets.get(i).bulletHitbox.getY() < 0.8f) {
                Bullets.remove(i);
            }
        }
    }

    public void draw(SpriteBatch batch) {
        for (int i = 0; i < Bullets.size(); i++) {
            batch.draw(Bullets.get(i).bullet,
                    Bullets.get(i).bulletHitbox.getX(),
                    Bullets.get(i).bulletHitbox.getY(),
                    Bullets.get(i).bulletHitbox.width / 2,
                    Bullets.get(i).bulletHitbox.height / 2,
                    Bullets.get(i).bulletHitbox.width,
                    Bullets.get(i).bulletHitbox.height,
                    1,
                    1,
                    Bullets.get(i).getRotation(),
                    0,
                    0,
                    11,
                    48,
                    false,
                    false);
        }
    }

    public ArrayList<Bullet> getList() {
        return Bullets;
    }

    public Rectangle getHitbox() {
        return bulletHitbox;
    }
}
