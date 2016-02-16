package com.mygdx.game.evogyre;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Jay on 1/13/2016.
 */
public class ProjectionUtils {
    private static final String TAG = ProjectionUtils.class.getName();

    /**
     * Maps a point from the game map to the displayed mapPosition including depth.
     * The z-component is used for scaling [0.0 - 1.0] image according to distance.
     * @param mapPt Game map coordinate
     * @return Vector for rendering to screen
     */
    public static Vector3 projectPoint3D(GameScreen screen, Vector2 mapPt) {
        // Distant objects move slower. F(x) = a * x^2
        Vector2 dspPt = new Vector2(Constants.PROJECTION_RADIUS * vanishingPower(mapPt.x / Constants.MAP_SIZE_X), 0f);
        // Y-component becomes the degrees away from positive x-axis.
        dspPt.rotate(mapPt.y + screen.dspRotation);
        float depth = vanishingPower(mapPt.x / Constants.MAP_SIZE_X);
        transposeCenter(screen, dspPt, depth);
        return new Vector3(dspPt, depth);
    }

    /**
     * Maps a point from the game map position to the display position.
     * The z-component (depth) is not returned. See `projectPoint3D` for depth.
     * @param mapPt Game map coordinate
     * @return Vector for rendering to screen
     */
    public static Vector2 projectPoint2D(GameScreen screen, Vector2 mapPt) {
        // Distant objects move slower. F(x) = a * x^2
        Vector2 dspPt = new Vector2(Constants.PROJECTION_RADIUS * vanishingPower(mapPt.x / Constants.MAP_SIZE_X), 0f);
        // Y-component becomes the degrees away from positive x-axis.
        dspPt.rotate(mapPt.y + screen.dspRotation);
        float depth = vanishingPower(mapPt.x / Constants.MAP_SIZE_X);
        transposeCenter(screen, dspPt, depth);
        return dspPt;
    }

    /**
     * Transposes mapPosition based on vanishing point
     * @param displayPt
     * @param depth Z-depth for displaying point
     * @return
     */
    public static Vector2 transposeCenter(GameScreen screen, Vector2 displayPt, float depth) {
        Vector2 scaledV = new Vector2(screen.vanishingPoint);
        scaledV.setLength(Constants.CENTER_DISPLACEMENT);
        // "Funnel power" affects funnel curvature
        scaledV.scl(1.0f - (float) Math.pow(depth, 1f/Constants.FUNNEL_POWER));
        return displayPt.add(scaledV.x, scaledV.y);
    }

    public static float vanishingPower(float x) {
        return (float) Math.pow(x, Constants.VANISHING_STRETCH);
    }
}
