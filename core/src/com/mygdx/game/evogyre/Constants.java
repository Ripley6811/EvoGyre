package com.mygdx.game.evogyre;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Created by Jay on 1/12/2016.
 */
public class Constants {
    /** LOGGING LEVELS */
    // Application logging levels from lowest to highest. Choose one.
    public static final int LOG_LEVEL = Application.LOG_NONE;
//    public static final int LOG_LEVEL = Application.LOG_ERROR;
//    public static final int LOG_LEVEL = Application.LOG_INFO;
//    public static final int LOG_LEVEL = Application.LOG_DEBUG;

    /** DISPLAYED TEXT */
    public static final String GAME_TITLE = "EvoGyre";
    public static final String STORY = "In the year 2143, we lost Ganymede to an alien invasion. " +
            "It had held a colony of over 30,000 humans and was unprepared for war. Now, 22 years later, " +
            "we will take Ganymede back and solidify our dominion over the Solar System! With recent discoveries in tranverse photolithic " +
            "shielding and improved armaments, those invaders don't " +
            "stand a chance!";
    public static final String KEY_CONTROLS = "Space or W  --  Fire lasers\n" +
            "Left/Right Arrow  --  Strafe left/right (rotate)\n" +
            "Down Arrow  --  Toggle fixed or rotating ship\n" +
            "\n" +
            "Collect blue and red tiles to strengthen shields and improve weapons.";
    public static final String TOUCH_CONTROLS = "Lasers fire continuously\n" +
            "Bottom blue arrows  --  Strafe left/right (rotate)\n" +
            "Top right icon --  Toggle fixed or rotating ship\n" +
            "\n" +
            "Collect blue and red tiles to strengthen shields and improve weapons.";

    /** GAME SETTINGS */
    public static final String[] DIFFICULTY_NAMES = {"Easy", "Hard", "Insane!"};
    public static final Array<String> DIFFICULTY = new Array<String>(DIFFICULTY_NAMES);
    public static final Color BACKGROUND_COLOR = Color.BLACK;
    public static final String MAIN_ATLAS = "images/evogyre.pack.atlas";
    public static final float MUSIC_FADE_MULTIPLIER = 0.3f;
    public static final float INTRO_GRAPHICS_TWEEN_TIME = 4f; // seconds

    /** WORLD SETTINGS */
    public static final float GAMEPLAY_SIZE = 600f;  // X and Y dimensions
    public static final float PROJECTION_RADIUS = 280f;
    // Affects curvature of cylinder/funnel. 1 = cylinder, over 1 increases curve
    public static final int FUNNEL_POWER = 3;
    public static final float MAP_SIZE_X = 3*360f;
    public static final float MAP_SIZE_Y_360 = 360f;  // DO NOT CHANGE
    public static final double VANISHING_STRETCH = 3.0;
    public static final float CENTER_DISPLACEMENT = 0.7f * PROJECTION_RADIUS;
    public static final Color FUNNEL_COLOR = new Color(0.05f,0.4f,0.05f,0.7f);
    public static final boolean DRAW_RINGS = true;
    public static final boolean FIXED_VESSEL = false;
    public static final int NUMBER_OF_RINGS = 20;
    public static final float RING_INTERVAL = MAP_SIZE_X / NUMBER_OF_RINGS;
    public static final float ANIMATE_FUNNEL_DURATION = 0.2f;
    public static final float PLAYER_START_ANGLE = 290f;
    public static final int NUMBER_OF_STARS = 420;
    public static final Color STAR_COLOR = new Color(1f,1f,0.8f,0.5f);
    public static final Color COLLISION_DEBUG_COLOR = new Color(0f, 1f, 1f, 0.8f);
    public static final int HALF_SHIP = 20;
    public static final int HALF_BALLSHIP = 24;

    /** WEAPONS */
    public static final JsonValue PRIMARY_WEAPON_SETUP =
            new JsonReader().parse(Gdx.files.internal("json/primary_weapon_setup.json"));
    public static final JsonValue ENEMY_WEAPON_SETUP =
            new JsonReader().parse(Gdx.files.internal("json/enemy_weapon_setup.json"));
    // Cutoff where bullets are removed.
    public static final float BULLET_CUTOFF = 0.4f * Constants.MAP_SIZE_X;
    // Reduce and randomize enemy firing rate
    public static final float ENEMY_FIRE_REDUCTION = 0.005f;  // 0.06 = 6% success rate
    // MISSILES NOT IMPLEMENTED
//    public static final float MISSILE_RIGHT_OFFSET = 0.5f;
//    public static final float MISSILE_LEFT_OFFSET = -1.1f;
//    public static final float MISSILE_RIGHT_LEAN_RIGHT = 0.28f;
//    public static final float MISSILE_RIGHT_LEAN_LEFT = 0.4f;
//    public static final float MISSILE_LEFT_LEAN_RIGHT = -1.0f;
//    public static final float MISSILE_LEFT_LEAN_LEFT = -0.88f;

    /** ACTOR */
    public static final float ACCELERATION_RATE = 300f;
    public static final float MAX_VELOCITY = 150f;
    public enum Flight_Patterns {
        SNAKE_ZIGZAG ("snake-zigzag"),
        ABREAST_ZIGZAG ("abreast-zigzag"),
        SNAKE_SPIRAL ("snake-spiral"),
        BOSS ("boss");
        public final String string;
        Flight_Patterns(String str) {
            this.string = str;
        }
    }
    public static final JsonValue ATTACK_PLAN_1 =
            new JsonReader().parse(Gdx.files.internal("json/level_1.json")).get("enemies");
    public static final float ABREAST_DISTANCE = 12f;  // Degrees

    /** SHIELDS */
    public static final float SHIELD_RADIUS = 20f;
    public static final float SHIELD_WIDTH_MULTIPLIER = 1.3f;
    public static final float SHIELD_EFFECT_OFFSET = 15f;
    public static final float PHASE_MAX = 1f;  // [0f, 1f]
    public static final float PHASE_MULTIPLIER = 2f;  // Effect spread rate
    public static final float ALPHA_FADE_MAX = 1f;  // [0f, 1f]
    public static final float ALPHA_FADE_MULTIPLIER = 1.5f;  // Effect fade rate
    public static final int STARTING_SHIELD_POINTS = 4;
    public static final int VESSEL_HIT_POINTS = 1;
    public static final int TRIDENT_HIT_POINTS = 4;
    public static final int BALLSHIP_HIT_POINTS = 2;
    public static final int BOSS_HIT_POINTS = 160;

    /** ACCELEROMETER */
    public static final float G = 9.8f;  // Gravitational constant
    // Multiply to velocity to slow down
    public static final float ACTOR_MOTION_FRICTION = 0.8f;
    // ACCELEROMETER CONTROLS NOT IMPLEMENTED
//    // Used for sticky movement when using accelerometer
//    public static final float ACTOR_STATIC_THRESHOLD = 0.4f;
//    // No motion if angle between ship and device acceleration is below a minimum
//    public static final float ACTOR_ANGLE_THRESHOLD = 5f;
//    public static final float ACTOR_LEN_THRESHOLD = 0.5f;

    /** COLLISION POLYGONS FOR ACTORS */
    // Trident = The green T-shaped enemy craft
    private static Vector2[] tp_corners = {
            new Vector2(12f, 20f),  // Tip
            new Vector2(12f, -20f),  // left wing
            new Vector2(-2, -20f),  // right wing
            new Vector2(-2, 20f)  // right wing
    };
    public static final Array<Vector2> TRIDENT_POLYGON = new Array<Vector2>(tp_corners);
    // Ball = The yellow ball-shaped enemy craft
    private static Vector2[] bp_corners = {
            new Vector2(12, 6),  // Tip
            new Vector2(12, -6),  // left wing
            new Vector2(0, -13f),  // right wing
            new Vector2(0, 13f)  // right wing
    };
    public static final Array<Vector2> BALLSHIP_POLYGON = new Array<Vector2>(bp_corners);
    // Boss = The large green ship
    private static Vector2[] boss_corners = {
            new Vector2(50, 40),  // Tip
            new Vector2(50, -40),  // left wing
            new Vector2(25, -140),
            new Vector2(0, -140f),  // right wing
            new Vector2(0, 140f),  // right wing
            new Vector2(25, 140)
    };
    public static final Array<Vector2> BOSS_POLYGON = new Array<Vector2>(boss_corners);
    // Vessel = The player craft
    private static Vector2[] vp_corners = {
            new Vector2(17f, 0),  // Tip
            new Vector2(-1f, -20),  // left wing
            new Vector2(-4f, -20),
            new Vector2(-8f, 0),  // Tip
            new Vector2(-4f, 20),
            new Vector2(-1f, 20)  // right wing
    };
    public static final Array<Vector2> VESSEL_POLYGON = new Array<Vector2>(vp_corners);

    /** POWERUPS */
    public enum PowerUp_Types {
        SHIELD,
        WEAPON
    }
    public static final float POWER_UP_VELOCITY = 100f;
    public static final float POWER_UP_INTERVAL = 10f;
    public static final long POWER_UP_RANDOM_SEED = 121;

    /** HUD */
    public static final Color SHIELD_STRENGTH_COLOR_TOP = new Color(0.6f, 0.7f, 1f, 1f);
    public static final Color SHIELD_STRENGTH_COLOR_BOTTOM = new Color(0.2f, 0.3f, 1f, 1f);
    public static final Color WEAPON_STRENGTH_COLOR_TOP = new Color(1f, 0.5f, 0.7f, 1f);
    public static final Color WEAPON_STRENGTH_COLOR_BOTTOM = new Color(1f, 0.1f, 0.3f, 1f);
    public static final float PADDING = 10f;
    public static final float GAUGE_TEXT_WIDTH = 65f;
    public static final float GAUGE_TEXT_HEIGHT = 16f;
    public static final float WEAPON_BLOCKS_XOFFSET = -3f;
    public static final float WEAPON_BLOCKS_YOFFSET = -21f;
    public static final float PANEL_TWEEN_TIME = 0.9f;
    public static final Rectangle buttonRect1 = new Rectangle(
            Constants.GAMEPLAY_SIZE *.1f,
            Constants.GAMEPLAY_SIZE *.43f,
            Constants.GAMEPLAY_SIZE *.25f,
            65
    );
    public static final Rectangle buttonRect2 = new Rectangle(
            buttonRect1.x + Constants.GAMEPLAY_SIZE *.275f,
            buttonRect1.y,
            buttonRect1.width,
            buttonRect1.height
    );
    public static final Rectangle buttonRect3 = new Rectangle(
            buttonRect2.x + Constants.GAMEPLAY_SIZE *.275f,
            buttonRect2.y,
            buttonRect2.width,
            buttonRect2.height
    );
    public static final Rectangle rotateButtonRect = new Rectangle(
            200, 204,
            105 * .9f,
            95 * .9f
    );
    public static final Rectangle buttonGotoMenu = new Rectangle(
            0 - Constants.GAMEPLAY_SIZE *.125f,
            -65,
            Constants.GAMEPLAY_SIZE *.25f,
            65
    );
    public static final Rectangle buttonStartOver = new Rectangle(
            buttonGotoMenu.x,
            buttonGotoMenu.y - buttonGotoMenu.height * 1.1f,
            buttonGotoMenu.width,
            buttonGotoMenu.height
    );
}
