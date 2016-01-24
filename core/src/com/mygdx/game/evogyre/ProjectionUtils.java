package com.mygdx.game.evogyre;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Jay on 1/13/2016.
 */
public class ProjectionUtils {
    private static final String TAG = ProjectionUtils.class.getName();

    /**
     * Maps a point from the game map to the displayed position.
     * The z-component is used for scaling [0.0 - 1.0] image according to distance.
     * @param mapPt Game map coordinate
     * @param rotation Rotation positionAngle
     * @return Vector for rendering to screen
     */
    public static Vector3 projectPoint(Vector2 mapPt, float rotation) {
        // Distant objects move slower. F(x) = a * x^2
        Vector2 vector2 = new Vector2(Constants.PROJECTION_RADIUS * vanishingPower(mapPt.x / Constants.MAP_SIZE_X), 0f);
        // Y-component becomes the degrees from x-axis.
        vector2.rotate(mapPt.y + rotation);
        return new Vector3(vector2, vanishingPower(mapPt.x / Constants.MAP_SIZE_X));
//        return new Vector3(vector2, vector2.x / PROJECTION_RADIUS);
    }

    /**
     * Maps a point from the game map to the display position.
     * Adjusts display position according to vanishing pt vector.
     * @param mapPt Game map coordinate
     * @param rotation Rotation positionAngle
     * @param transposition Vanishing point of display cylinder
     * @return
     */
    public static Vector3 projectPoint(Vector2 mapPt, float rotation, Vector2 transposition) {
        return transposeCenter(projectPoint(mapPt, rotation), transposition);
    }

    /**
     * Transposes position based on vanishing point
     * @param displayPt
     * @param centerTransposition Vanishing point of display cylinder
     * @return
     */
    public static Vector3 transposeCenter(Vector3 displayPt, Vector2 centerTransposition) {
        Vector2 scaledV = new Vector2(centerTransposition);
        scaledV.setLength(Constants.CENTER_DISPLACEMENT);
        scaledV.scl(1.0f - (float) Math.pow(displayPt.z, 1f/Constants.FUNNEL_POWER));
        return displayPt.add(scaledV.x, scaledV.y, 0f);
    }

    public static float vanishingPower(float x) throws IllegalArgumentException {
//        if (x > 1f || x < 0f) throw new IllegalArgumentException("Parameter must be in range [0, 1].");
        return (float) Math.pow(x, Constants.VANISHING_STRETCH);
    }
}
