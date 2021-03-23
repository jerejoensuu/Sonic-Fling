package fi.tuni.tiko.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.concurrent.TimeUnit;

public class Sonic extends Sprite {
    private Texture stretchSheet;
    private Texture crouchSheet;
    private Texture crouchStatic;
    private TextureRegion crouchStaticRegion;
    private Texture hurt;
    private TextureRegion hurtTR;
    private Texture death1;
    private TextureRegion death1TR;
    private Texture death2;
    private TextureRegion death2TR;
    private Texture rollSheet;
    private Animation<TextureRegion> stretchAnimation;
    private Animation<TextureRegion> crouchAnimation;
    private Animation<TextureRegion> rollAnimation;
    private Sound hurtSound;

    private float stateTime = 0;
    private TextureRegion currentFrame;
    public float width;
    public float height;
    private float radius;
    private float speedX = 4f;
    private float speedY = 2f;
    public static boolean RIGHT = true;
    public static boolean LEFT = false;
    private boolean direction = RIGHT;
    public float velocityX;
    public float velocityY;
    public boolean inAir = false;
    public boolean hitEnemy = false;
    private float gravity = 0.05f;
    private float drag = 0.0005f;
    private Rectangle sonicHitbox;
    public int health = 5;
    public long lastHit = 0;
    private boolean hitGround = false;
    private boolean iFrames = false;
    private long iFrameCycle = 0;

    public Sonic() {
        stretchSheet = new Texture("Sonic stretch.png");
        crouchSheet = new Texture("Sonic crouch.png");
        crouchStatic = new Texture("Sonic crouch static.png");
        crouchStaticRegion = new TextureRegion(crouchStatic);
        rollSheet = new Texture("Sonic roll.png");
        hurt = new Texture("Sonic hurt.png");
        hurtTR = new TextureRegion(hurt);
        death1 = new Texture("Sonic death 1.png");
        death1TR = new TextureRegion(death1);
        death2 = new Texture("Sonic death 2.png");
        death2TR = new TextureRegion(death2);
        hurtSound = Gdx.audio.newSound(Gdx.files.internal("hurt.mp3"));

        stretchAnimation = AnimationUtil.createAnimation(stretchSheet, 7, 1, 10);
        crouchAnimation = AnimationUtil.createAnimation(crouchSheet,2, 1, 10);
        rollAnimation = AnimationUtil.createAnimation(rollSheet, 4, 1, 1);
        currentFrame = stretchAnimation.getKeyFrame(stateTime, true);

        // pixels -> meters
        width = currentFrame.getRegionWidth() / 25f;
        height = currentFrame.getRegionHeight() / 25f;
        radius = width / 2;

        sonicHitbox = new Rectangle(8 - getWidth()/2, 0.8f, radius*2,radius*2);
    }

    public void stretchAnimation() {
        stateTime += Gdx.graphics.getDeltaTime();
        currentFrame = stretchAnimation.getKeyFrame(stateTime, true);
    }

    public void crouchAnimation() {
        stateTime += Gdx.graphics.getDeltaTime();
        currentFrame = crouchAnimation.getKeyFrame(stateTime, true);
    }

    public void hurt() {
        currentFrame = hurtTR;
    }

    public void deathAnimation(int i) {
        if (i == 1) {
            currentFrame = death1TR;
        } else {
            currentFrame = death2TR;
        }

    }

    public void crouch() {
        currentFrame = crouchStaticRegion;
    }

    public void move() {
        stateTime += Gdx.graphics.getDeltaTime();

        if(direction == RIGHT) {
            setX(getX() + speedX * Gdx.graphics.getDeltaTime());
            setRotation(getRotation() - 10);
        } else {
            setX(getX() - speedX * Gdx.graphics.getDeltaTime());
            setRotation(getRotation() + 10);
        }

        if (getX() <= 0) {
            setX(0);
        } else if (getX() >= 16 - currentFrame.getRegionWidth() / 21f) {
            setX(16 - currentFrame.getRegionWidth() / 21f);
        }

        currentFrame = rollAnimation.getKeyFrame(stateTime, true);
    }

    public void moveAndroid(float accX) {
        stateTime += Gdx.graphics.getDeltaTime();

        if(direction == RIGHT) {
            setX(getX() + speedX * accX * Gdx.graphics.getDeltaTime());
            setRotation(getRotation() - 10);
        } else {
            setX(getX() + speedX * accX * Gdx.graphics.getDeltaTime());
            setRotation(getRotation() + 10);
        }

        if (getX() <= 0) {
            setX(0);
        } else if (getX() >= 16 - currentFrame.getRegionWidth() / 25f) {
            setX(16 - currentFrame.getRegionWidth() / 25f);
        }

        currentFrame = rollAnimation.getKeyFrame(stateTime, true);
    }

    public void fling() {
        if (TimeUnit.MILLISECONDS.convert(System.nanoTime() - lastHit, TimeUnit.NANOSECONDS) > 2000
            || hitGround) {
            gravity = 0.05f;
            drag = 0.0005f;
            currentFrame = rollAnimation.getKeyFrame(stateTime, true);
        } else if (!hitGround) {
            currentFrame = hurtTR;
            setRotation(0);
            setScale(1.25f);
        }
        stateTime += Gdx.graphics.getDeltaTime();
        float maxVelocity = 1;
        if (velocityX > maxVelocity) {
            velocityX = maxVelocity;
        } else if (velocityX < -maxVelocity) {
            velocityX = -maxVelocity;
        } else if (velocityY > maxVelocity) {
            velocityY = maxVelocity;
        } else if (velocityY < -maxVelocity) {
            velocityY = -maxVelocity;
        }

        if (velocityX < 0) {
            changeDirection(LEFT);
            drag *=  -1;
        } else {
            changeDirection(RIGHT);
            drag = Math.abs(drag);
        }

        if (getY() + velocityY > 0.8) {
            setY(getY() + velocityY);
            velocityY -= gravity;
            setX(getX() + velocityX);
            velocityX -= drag;
        } else {
            inAir = false;
            hitGround = true;
            hitEnemy = false;
            setScale(1f);
            setY(0.8f);
        }

        if (getX() <= 0) {
            setX(0);
            velocityX *= -0.5;
        } else if (getX() >= 16 - currentFrame.getRegionWidth() / 25f) {
            setX(16 - currentFrame.getRegionWidth() / 25f);
            velocityX *= -0.5;
        } else if (getY() > 9 - currentFrame.getRegionHeight() / 21f) {
            setY(9 - currentFrame.getRegionHeight() / 21f);
            velocityY = 0;
        }
    }

    public void calculateCollision(float enemyX, float enemyY, float enemyRadius) {
        hitEnemy = true;
        float velocity = velocityY + velocityX;
        velocityX = (Math.abs((enemyX + enemyRadius) - (getX() + width/2))
                    / (Math.abs((enemyX + enemyRadius) - (getX() + width/2)) + Math.abs((enemyY + enemyRadius) - (getY() + height/2))))
                    * velocity * -0.6f;
        velocityY = (Math.abs((enemyY + enemyRadius) - (getY() + height/2))
                     / (Math.abs((enemyX + enemyRadius) - (getX() + width/2)) + Math.abs((enemyY + enemyRadius) - (getY() + height/2))))
                     * velocity * -0.6f;
        if (getY() + height/2 < enemyY + enemyRadius) {
            velocityY = Math.abs(velocityY) * -1;
        }
    }

    public void changeDirection(boolean dir) {
        if (dir != direction) {
            direction = dir;
            AnimationUtil.flip(rollAnimation);
            crouchStaticRegion.flip(true, false);
            hurtTR.flip(true, false);
        }
    }

    public boolean hitBullet(float bulletX) {
        if (TimeUnit.MILLISECONDS.convert(System.nanoTime() - lastHit, TimeUnit.NANOSECONDS) > 2000) {
            hitGround = false;
            health--;
            lastHit = System.nanoTime();
            inAir = true;
            setVelocityX(0.1f);
            if (getX() + getHitbox().width/2 < bulletX) {
                setVelocityX(velocityX * -1);
                changeDirection(LEFT);
            } else {
                changeDirection(RIGHT);
            }
            setVelocityY(0.2f);
            drag *= 0.075;
            gravity *= 0.125;

            fling();
            if (health <= 0) {
                return true;
            } else {
                hurtSound.play(0.05f);
            }
        }
        return false;
    }

    public void draw(SpriteBatch batch) {
        if (TimeUnit.MILLISECONDS.convert(System.nanoTime() - lastHit, TimeUnit.NANOSECONDS) > 2000
            && hitGround) {
            gravity = 0.05f;
            drag = 0.0005f;
        }
        if (TimeUnit.MILLISECONDS.convert(System.nanoTime() - lastHit, TimeUnit.NANOSECONDS) < 2000) {
            if (TimeUnit.MILLISECONDS.convert(System.nanoTime() - iFrameCycle, TimeUnit.NANOSECONDS) > 150) {
                iFrames = !iFrames;
                iFrameCycle = System.nanoTime();
            }
        } else {
            iFrames = false;
        }
        if (!iFrames || health == 0) {
            batch.draw(currentFrame,
                    getX(),
                    getY(),
                    width/2,
                    height/2,
                    width,
                    height,
                    getScaleX(),
                    getScaleX(),
                    getRotation());
        }
    }

    public void setVelocityX(float velocity) {
        velocityX = velocity;
    }

    public void setVelocityY(float velocity) {
        velocityY = velocity;
    }

    @Override
    public void setX(float x) {
        sonicHitbox.x = x;
    }

    @Override
    public float getX() {
        return sonicHitbox.x;
    }

    @Override
    public void setY(float y) {
        sonicHitbox.y = y;
    }

    @Override
    public float getY() {
        return sonicHitbox.y;
    }

    public Rectangle getHitbox() {
        return sonicHitbox;
    }

    public void dispose() {
        stretchSheet.dispose();
        crouchSheet.dispose();
        crouchStatic.dispose();
        rollSheet.dispose();
    }
}
