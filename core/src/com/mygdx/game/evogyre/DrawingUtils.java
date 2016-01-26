package com.mygdx.game.evogyre;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

/**
 * Created by Jay on 1/26/2016.
 */
public class DrawingUtils {
    private static final String TAG = DrawingUtils.class.getName();

    public static boolean isBlendEnabled() {
        return Gdx.gl.glIsEnabled(GL20.GL_BLEND);
    }

    public static void enableBlend() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void disableBlend() {
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
}
