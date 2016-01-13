package com.mygdx.game.evogyre;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Jay on 1/12/2016.
 */
public class Constants {
    /* LOGGING LEVELS */
    // Application logging levels from lowest to highest. Choose one.
//    public static final int LOG_LEVEL = Application.LOG_NONE;
//    public static final int LOG_LEVEL = Application.LOG_ERROR;
    public static final int LOG_LEVEL = Application.LOG_INFO;
//    public static final int LOG_LEVEL = Application.LOG_DEBUG;

    /* DISPLAYED TEXT */
    public static final String GAME_TITLE = "EvoGyre";
    public static final String STORY = "In the year 2143, we lost Earth to the Xenovulpe. " +
            "It was the last human stronghold in the Solar system. Now, 32 years later, " +
            "we will take the system back, planet by planet! With recent discoveries in tranverse photolithic " +
            "shielding, time-dilation engines and numerous armaments, those invaders don't " +
            "stand a chance.";

    /* GAME SETTINGS */
    public static final String[] DIFFICULTY_NAMES = {"Easy", "Hard", "Insane!"};
    public static final Array<String> DIFFICULTY = new Array<String>(DIFFICULTY_NAMES);
    public static final Color BACKGROUND_COLOR = Color.BLACK;

    /* WORLD SETTINGS */
    public static final float DISPLAY_SIZE = 600f;  // X and Y dimensions
    public static final float PROJECTION_RADIUS = 290f;
    public static final float MAP_SIZE = 360f;
    public static final float PROJ_X_SCALING = PROJECTION_RADIUS / (MAP_SIZE * MAP_SIZE);
    public static final float CENTER_DISPLACEMENT = 0.6f * PROJECTION_RADIUS;
}
