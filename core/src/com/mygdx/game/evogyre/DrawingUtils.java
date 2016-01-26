package com.mygdx.game.evogyre;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

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

    /**
     * Converts a polygon vector array (Array<Vector2>) to a float array.
     * This can be used in `Intersector` methods that take a polygon float[] parameter.
     * @param vectors Corners of a polygon
     * @param offset Translation the polygon
     * @return Corner coordinates as an array of float values
     */
    public static float[] vectors2floats(Array<Vector2> vectors, Vector3 offset) {
        float[] retList = new float[vectors.size * 2];
        int index = 0;
        for (Vector2 v: vectors) {
            retList[index++] = offset.x + v.x;
            retList[index++] = offset.y + v.y;
        }
        return retList;
    }

    public static void drawDebugPolygon(MyShapeRenderer renderer, Actor actor) {
        float rotation = actor.position.y + GameScreen.mapRotation;

        DrawingUtils.enableBlend();
        Array<Vector2> displayPolygon = new Array<Vector2>();
        for (Vector2 v : actor.polygon) {
            displayPolygon.add(new Vector2(v).rotate(rotation).scl(actor.display.z));
        }
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Constants.COLLISION_DEBUG_COLOR);
        renderer.polygon(
                vectors2floats(displayPolygon, actor.display)
        );
        renderer.end();
        DrawingUtils.disableBlend();
    }
}
