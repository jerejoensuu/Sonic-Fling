package fi.tuni.tiko.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;

public class Explosion {
    private ArrayList<Explosion> Explosions = new ArrayList<Explosion>();
    private Texture explosionSheet;
    private Animation<TextureRegion> explosionAnimation;
    private float stateTime = 0;
    private TextureRegion currentFrame;
    private float scale;
    private float x;
    private float y;
    private float width;
    private int countdown = 120;

    public Explosion(float enemyX, float enemyY, float enemyRadius, float enemyScale) {
        explosionSheet = new Texture("Explosion.png");
        explosionAnimation = AnimationUtil.createAnimation(explosionSheet, 12, 1, 10);
        scale = enemyScale * 1.75f;
        width = enemyRadius * 2;
        x = enemyX + enemyRadius - width;
        y = enemyY + enemyRadius - width;
    }

    public void newExplosion(float enemyX, float enemyY, float enemyRadius, float enemyScale) {
        Explosions.add(new Explosion(enemyX, enemyY, enemyRadius, enemyScale));
    }

    public void explosionAnimation() {
        stateTime += Gdx.graphics.getDeltaTime();
        currentFrame = explosionAnimation.getKeyFrame(stateTime, false);
    }

    public void draw(SpriteBatch batch) {
        for (int i = 0; i < Explosions.size(); i++) {
            Explosions.get(i).explosionAnimation();
            batch.draw(Explosions.get(i).currentFrame,
                    Explosions.get(i).x,
                    Explosions.get(i).y,
                    0,
                    0,
                    Explosions.get(i).width,
                    Explosions.get(i).width,
                    Explosions.get(i).scale,
                    Explosions.get(i).scale,
                    0);
            Explosions.get(i).countdown--;
            if (Explosions.get(i).countdown == 0) {
                Explosions.remove(i);
            }
        }
    }
}
