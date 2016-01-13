package com.mygdx.game.evogyre;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Jay on 1/13/2016.
 */
public class ProjectionUtils extends Constants {
    /**
     * Maps a point from the game map to the displayed position.
     * The z-component is used for scaling [0.0 - 1.0] image according to distance.
     * @param mapPt Game map coordinate
     * @return Vector for rendering to screen
     */
    public static Vector3 projectPoint(Vector2 mapPt) {
        // Distant objects move slower. F(x) = a * x^2
        Vector2 vector2 = new Vector2(PROJ_X_SCALING * mapPt.x * mapPt.x, 0f);
        // Y-component becomes the degrees from x-axis.
        vector2.rotate(mapPt.y);
        return new Vector3(vector2, mapPt.x / MAP_SIZE);
//        return new Vector3(vector2, vector2.x / PROJECTION_RADIUS);
    }

    /**
     * Maps a point from the game map to the display position.
     * Adjusts display position according to vanishing pt vector.
     * @param mapPt Game map coordinate
     * @param transposition Vanishing point of display cylinder
     * @return
     */
    public static Vector3 projectPoint(Vector2 mapPt, Vector2 transposition) {
        return transposeCenter(projectPoint(mapPt), transposition);
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
        // Straight cylinder view
        scaledV.scl(1.0f - displayPt.z * displayPt.z);
        // Curving funnel view
//        scaledV.scl(1.0f - (float) Math.sqrt(displayPt.z));
        return displayPt.add(scaledV.x, scaledV.y, 0f);
    }
}
