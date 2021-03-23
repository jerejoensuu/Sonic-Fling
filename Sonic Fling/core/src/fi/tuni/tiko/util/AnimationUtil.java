package fi.tuni.tiko.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimationUtil {

    public static Animation<TextureRegion> createAnimation (Texture spriteSheet, int columns, int rows, int fps) {
        int tileWidth = spriteSheet.getWidth() / columns;
        int tileHeight = spriteSheet.getHeight() / rows;

        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, tileWidth, tileHeight);
        TextureRegion[] allFrames = AnimationUtil.transformTo1D(tmp, columns, rows);

        return new Animation(fps / 60f, allFrames);
    }

    public static TextureRegion[] transformTo1D( TextureRegion [][]tr, int cols, int rows ) {
        TextureRegion [] frames
                = new TextureRegion[cols * rows];

        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                frames[index++] = tr[i][j];
            }
        }

        return frames;
    }

    public static void flip(Animation<TextureRegion> animation) {
        TextureRegion[] regions = animation.getKeyFrames();
        for(TextureRegion r : regions) {
            r.flip(true, false);
        }
    }

}
