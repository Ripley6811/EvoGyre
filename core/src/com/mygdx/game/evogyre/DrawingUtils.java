package com.mygdx.game.evogyre;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Jay on 1/26/2016.
 */
public class DrawingUtils {
    private static final String TAG = DrawingUtils.class.getName();

    public static void initGLSettings() {
        Color BG_COLOR = Constants.BACKGROUND_COLOR;
        Gdx.gl.glClearColor(BG_COLOR.r, BG_COLOR.g, BG_COLOR.b, 1f);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void clearScreen() {
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public static boolean isBlendEnabled() {
        return Gdx.gl.glIsEnabled(GL20.GL_BLEND);
    }

    public static void enableBlend() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
    }

    public static void disableBlend() {
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    /**
     * Converts a polygon vector array (Array<Vector2>) to a float array.
     * This can be used in `Intersector` or `ShapeRenderer` methods that take a
     * float[] polygon parameter.
     * @param vectors Corners of a collisionPolygon
     * @return Corner coordinates as an array of float values
     */
    public static float[] vectors2floats(Array<Vector2> vectors) {
        float[] retList = new float[vectors.size * 2];
        int index = 0;
        for (Vector2 v: vectors) {
            retList[index++] = v.x;
            retList[index++] = v.y;
        }
        return retList;
    }

    public static void drawDebugPolygon(MyShapeRenderer renderer, float[] polygon) {
        DrawingUtils.enableBlend();
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Constants.COLLISION_DEBUG_COLOR);
        renderer.polygon(polygon);
        renderer.end();
        DrawingUtils.disableBlend();
    }
}
