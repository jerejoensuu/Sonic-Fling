package fi.tuni.tiko.util;

import fi.tuni.tiko.util.Explosion;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Eggman extends Sprite {
    private ArrayList<Eggman> Enemies = new ArrayList<Eggman>();
    private Texture eggman;
    private TextureRegion eggmanTR;
    private Bullet bullet = new Bullet(0, 0, 0);

    public static boolean RIGHT = true;
    public static boolean LEFT = false;
    private boolean direction = RIGHT;
    public float radius;
    private Rectangle eggmanHitbox;
    public boolean hitPlayer;
    private float velocityY;
    private float initialVelocityY;
    private float velocityX;
    private float initialVelocityX;
    private boolean velocityYNeg;
    private boolean velocityXNeg;
    private float xRatio;
    private float yRatio;
    private int health = 3;
    public int hitUnit;
    public float pulseValue = 1;
    public boolean pulseCycle = true;
    private float speed = 2f;
    public int explosionCountdown = 31;
    public long lastShot;
    private int randomDelay;
    private Sound cannonSound;

    public Eggman(float x) {
        eggman = new Texture("Eggman.png");
        eggmanTR = new TextureRegion(eggman);
        cannonSound = Gdx.audio.newSound(Gdx.files.internal("cannon.mp3"));

        radius = eggmanTR.getRegionHeight() / 20f / 2;
        eggmanHitbox = new Rectangle(x, 9 - radius * 2 - 0.2f, radius*2,radius*2);

        lastShot = System.nanoTime();
    }

    public void newEnemy(float x) {
        Enemies.add(new Eggman(x));
        if ((int)(Math.random()*2) == 0) {
            changeDirection(RIGHT, Enemies.size()-1);
        } else {
            changeDirection(LEFT, Enemies.size()-1);
        }
    }

    public void move() {
        for (int i = 0; i < Enemies.size(); i++) {
            if (Enemies.get(i).direction == RIGHT) {
                Enemies.get(i).setX(Enemies.get(i).getX() + Enemies.get(i).speed * Gdx.graphics.getDeltaTime());
            } else {
                Enemies.get(i).setX(Enemies.get(i).getX() - Enemies.get(i).speed * Gdx.graphics.getDeltaTime());
            }

            if (Enemies.get(i).getX() <= 0.5) {
                Enemies.get(i).setX(0.5f);
                if (Enemies.get(i).direction == LEFT) {
                    changeDirection(RIGHT, i);
                }
            } else if (Enemies.get(i).getX() >= 16 - radius*2 - 0.5f) {
                Enemies.get(i).setX(16 - radius*2 - 0.5f);
                if (Enemies.get(i).direction == RIGHT) {
                    changeDirection(LEFT, i);
                }
            }

            // pulse
            if (Enemies.get(i).pulseCycle) {
                Enemies.get(i).pulseValue += 0.002;
                Enemies.get(i).pulseCycle = Enemies.get(i).pulseValue <= 1.05;
            } else {
                Enemies.get(i).pulseValue -= 0.002;
                Enemies.get(i).pulseCycle = Enemies.get(i).pulseValue <= 0.95;
            }

            try {
                if (Enemies.get(hitUnit).hitPlayer) {
                    Enemies.get(hitUnit).handleCollision();
                }
            } catch (Exception e) { }

            // shooting
            if (TimeUnit.MILLISECONDS.convert(System.nanoTime() - Enemies.get(i).lastShot, TimeUnit.NANOSECONDS)
                > 2000 + randomDelay) {
                for (int bulletNumber = 1; bulletNumber <= 3; bulletNumber++) {
                    bullet.newBullet(Enemies.get(i).getX()+radius, Enemies.get(i).getY(), bulletNumber);
                }
                Gdx.app.log("Enemy", "Shot after " + TimeUnit.MILLISECONDS.convert(System.nanoTime() - lastShot, TimeUnit.NANOSECONDS));
                Enemies.get(i).lastShot = System.nanoTime();
                randomDelay = (int)(Math.random() * 500);
                cannonSound.play(0.1f);
            }
        }
        bullet.move();
    }

    public void handleCollision() {
        float driftValue = 0.04f;
        setY(getY() + velocityY);
        setX(getX() + velocityX);

        if (velocityYNeg) {
            if (velocityY <= initialVelocityY * -1) {
                velocityY += driftValue * yRatio;
            } else {
                hitPlayer = false;
            }
        } else {
            if (velocityY >= initialVelocityY * -1) {
                velocityY -= driftValue * yRatio;
            } else {
                hitPlayer = false;
            }
        }

        if (velocityXNeg) {
            if (velocityX <= initialVelocityX * -1) {
                velocityX += driftValue * xRatio;
            } else {
                hitPlayer = false;
            }
        } else {
            if (velocityX >= initialVelocityX * -1) {
                velocityX -= driftValue * xRatio;
            } else {
                hitPlayer = false;
            }
        }

        if (!hitPlayer) {
            setY(9 - radius * 2 - 0.2f);
        }
    }

    public boolean calculateCollision(float playerX, float playerY, float playerRadius, float playerVelocityX, float playerVelocityY, int index) {
        hitPlayer = true;
        float velocity = (Math.abs(playerVelocityY) + Math.abs(playerVelocityX)) * 0.3f;
        xRatio = Math.abs((playerX + playerRadius) - (getX() + radius))
                / (Math.abs((playerX + playerRadius) - (getX() + radius)) + Math.abs((playerY + playerRadius) - (getY() + radius)));
        velocityX = xRatio * velocity * 0.6f;
        Gdx.app.log("", " " + velocityX);
        yRatio = Math.abs((playerY + playerRadius) - (getY() + radius))
                / (Math.abs((playerX + playerRadius) - (getX() + radius)) + Math.abs((playerY + playerRadius) - (getY() + radius)));
        velocityY = yRatio * velocity * 0.6f;
        if (getY() + radius < playerY + playerRadius) {
            velocityY = Math.abs(velocityY) * -1;
        }
        if (getX() + radius < playerX + playerRadius) {
            velocityX = Math.abs(velocityX) * -1;
        }

        initialVelocityY = velocityY;
        initialVelocityX = velocityX;

        velocityYNeg = velocityY < 0;
        velocityXNeg = velocityX < 0;

        health--;

        if (health > 0) {
            return true;
        } else {
            speed = 0;
            explosionCountdown--;
            hitPlayer = false;
            return false;
        }
    }

    public void changeDirection(boolean dir, int eggmanIndex) {
        if (dir != Enemies.get(eggmanIndex).direction) {
            Enemies.get(eggmanIndex).direction = dir;
            Enemies.get(eggmanIndex).eggmanTR.flip(true, false);
        }
    }

    public void draw(SpriteBatch batch) {
        for (int i = 0; i < Enemies.size(); i++) {
            batch.draw(Enemies.get(i).eggmanTR,
                    Enemies.get(i).getX(),
                    Enemies.get(i).getY(),
                    radius,
                    radius,
                    radius * 2,
                    radius * 2,
                    Enemies.get(i).pulseValue,
                    Enemies.get(i).pulseValue,
                    0);
        }
        bullet.draw(batch);
    }

    @Override
    public void setX(float x) {
        eggmanHitbox.x = x;
    }

    @Override
    public float getX() {
        return eggmanHitbox.x;
    }

    @Override
    public void setY(float y) {
        eggmanHitbox.y = y;
    }

    @Override
    public float getY() {
        return eggmanHitbox.y;
    }

    public Rectangle getHitbox() {
        return eggmanHitbox;
    }

    public ArrayList<Eggman> getList() {
        return Enemies;
    }

    public ArrayList<Bullet> getBulletList() {
        return bullet.getList();
    }

    public void dispose() {
        eggman.dispose();
    }
}
