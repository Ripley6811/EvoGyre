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
    public static final int LOG_LEVEL = Application.LOG_NONE;
//    public static final int LOG_LEVEL = Application.LOG_ERROR;
//    public static final int LOG_LEVEL = Application.LOG_INFO;
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
    public static final boolean FUNNEL_SHAPE = true;  // Else straight cylinder flow
    public static final float MAP_SIZE_X = 3*360f;
    public static final float MAP_SIZE_Y = 360f;  // IMPORTANT: Y-axis must be 360
    public static final double VANISHING_STRETCH = 3.0;
    public static final float CENTER_DISPLACEMENT = 0.8f * PROJECTION_RADIUS;
    public static final Color FUNNEL_COLOR = new Color(0.01f,0.25f,0.01f,0.1f);
    public static final boolean DRAW_FUNNEL = true;
    public static final float ANIMATE_FUNNEL_DURATION = 0.4f;
    public static final int NUMBER_OF_STARS = 160;
    public static final Color STAR_COLOR = new Color(1f,1f,1f,0.5f);

    /* ACTOR */
    public static final float ACCELERATION_RATE = 300f;
    public static final float MAX_VELOCITY = 150f;

    /* SHIELDS */
    public static final float SHIELD_RADIUS = 20f;
    public static final float SHIELD_WIDTH_MULTIPLIER = 1.6f;
    public static final float SHIELD_EFFECT_OFFSET = 15f;
    public static final float PHASE_MAX = 1f;  // [0f, 1f]
    public static final float PHASE_MULTIPLIER = 2f;  // Effect spread rate
    public static final float ALPHA_FADE_MAX = 0.9f;  // [0f, 1f]
    public static final float ALPHA_FADE_MULTIPLIER = 1.5f;  // Effect fade rate
    public static final int STARTING_SHIELD_POINTS = 5;

    /* UNCATEGORIZED (PHYSICS) */
    public static final float G = 9.8f;  // Gravitational constant
    // Used for sticky movement when using accelerometer
    public static final float ACTOR_STATIC_THRESHOLD = 0.08f;
    // Multiply to velocity to slow down
    public static final float ACTOR_MOTION_FRICTION = 0.8f;
}
