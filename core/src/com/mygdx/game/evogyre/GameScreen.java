package com.mygdx.game.evogyre;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.evogyre.Utils.DrawingUtils;
import com.mygdx.game.evogyre.Utils.Tween;

import java.util.Random;

/**
 * Created by Jay on 1/12/2016.
 */
public class GameScreen extends InputAdapter implements Screen {
    private static final String TAG = GameScreen.class.getName();

    EvoGyreGame game;
    FitViewport actionViewport;
    SpriteBatch batch;
    MyShapeRenderer myRenderer;
    BitmapFont font;

    TextureAtlas atlas;
    Array<Actor> debris;
    // Unit vector giving direction of vanishing point transposition.
    Vector2 vanishingPoint = new Vector2(0f, 1f);
    // Player vessel array. Allows possibly multiple vessels as power-up
    Array<Vessel> vessels;
    Random random = new Random();
    BulletManager playerBullets;
    BulletManager enemyBullets;
    EnemyManager enemies;
    PowerupManager powerupManager;
    ExplosionManager explosionManager;
    TextureRegion planet;
    TextureRegion textShields;
    TextureRegion textWeapons;
    TextureRegion textScore;
    TextureRegion shipFixedButton;
    TextureRegion shipRotateButton;
    NinePatch bluePatch;
    NinePatch bluePatchDark;
    Tween.QuadInOut blueButtonTween;

    float timerGame;
    float timerDebris;
    float dspRotation = 0f;
    float playerAngle = Constants.PLAYER_START_ANGLE;
    long score = 0;
    boolean vesselFixed = false;
    boolean accelAvailable = Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer);
    Vector2 accelBalancer = new Vector2();  // For centering device in any mapPosition
    boolean pause = false;
    boolean gameOver = false;

    public GameScreen(EvoGyreGame game) {
        Gdx.input.setCatchBackKey(true);
        this.game = game;
        actionViewport = new FitViewport(Constants.DISPLAY_SIZE, Constants.DISPLAY_SIZE);
        actionViewport.apply(true);

        debris = new Array<Actor>();
        vessels = new Array<Vessel>();

        batch = new SpriteBatch();
        myRenderer = new MyShapeRenderer(batch);
        myRenderer.setAutoShapeType(true);
        myRenderer.setProjectionMatrix(actionViewport.getCamera().combined);
        myRenderer.translate(Constants.DISPLAY_SIZE / 2f, Constants.DISPLAY_SIZE / 2f, 0);

        /** LOAD ASSETS **/
        atlas = game.assets.get(Constants.MAIN_ATLAS);
        playerBullets = new BulletManager(atlas, Constants.PRIMARY_WEAPON_SETUP);
        enemyBullets = new BulletManager(atlas, Constants.ENEMY_WEAPON_SETUP);
        planet = atlas.createSprite("lavender");
        textShields = atlas.createSprite("text_shields");
        textWeapons = atlas.createSprite("text_weapons");
        textScore = atlas.createSprite("text_score");
        shipFixedButton = atlas.createSprite("ship_fixed_button");
        shipRotateButton = atlas.createSprite("ship_rotate_button");

        /** SETUP FONT */
        font = new BitmapFont();
        font.setColor(Color.CYAN);
        font.getData().setScale(3f);
        font.getRegion().getTexture().setFilter(
                Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        bluePatch = new NinePatch(
                atlas.createSprite("bluepane"),
                15,15,15,15
        );
        bluePatchDark = new NinePatch(
                atlas.createSprite("bluepanedark"),
                15,15,15,15
        );

        DrawingUtils.initGLSettings();

        init();
    }

    /**
     * Initialize new game. Loads level enemies and attributes from JSON.
     */
    public void init() {
        dspRotation = 0f;
        score = 0;
        debris.clear();
        vessels.clear();
        vessels.add(new Vessel(Constants.MAP_SIZE_X, playerAngle,
                atlas, playerBullets.weaponsCount() - 1));
        vanishingPoint.setAngle(playerAngle + 180f);
        VisualEffects.drawTunnelInit(Constants.ANIMATE_FUNNEL_DURATION);
        timerGame = 0f;
        timerDebris = 0f;
        setAccelerometerBalanced();

        powerupManager = new PowerupManager(atlas);
        explosionManager = new ExplosionManager(atlas);
        enemies = new EnemyManager(atlas, enemyBullets);

        // Load level enemies
        for (JsonValue group: Constants.ATTACK_PLAN_1) {
            boolean isAbreast = group.getString("pattern").toLowerCase().startsWith("abreast");
            float enterTime = group.getFloat("enterTime");
            float interval = group.getFloat("interval");
            float startY = group.getFloat("enterYPos");
            String type = group.getString("type");
            for (int i=0; i<group.getInt("quantity"); i++) {
                // TODO: change enemy type into Enum
                enemies.enqueue(type, enterTime, startY, group.getString("pattern"));
                if (isAbreast) startY = (startY + Constants.ABREAST_DISTANCE) % 360f;
                else enterTime += interval;
            }
        }


        blueButtonTween = new Tween.QuadInOut(
                bluePatch.getTotalWidth(),
                Constants.buttonRect1.width,
                Constants.PANEL_TWEEN_TIME
        );
    }

    @Override
    public void show() {
        Gdx.app.log(TAG, "called 'show()'");
        Gdx.input.setInputProcessor(this);
        pause = false;
        init();
    }

    public void setAccelerometerBalanced() {
        float xa = Gdx.input.getAccelerometerX(),
              ya = Gdx.input.getAccelerometerY();
        accelBalancer.set(xa, ya);
    }

    @Override
    public void hide() {
        Gdx.app.log(TAG, "called 'hide()'");
        pause = true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // TODO: Create a boolean for dealing with continuous touch, use for firing weapons
        Vector2 pt = actionViewport.unproject(new Vector2(screenX, screenY))
                .sub(Constants.DISPLAY_SIZE / 2, Constants.DISPLAY_SIZE / 2);
        if (Constants.rotateButtonRect.contains(pt)) vesselFixed = !vesselFixed;

        if (gameOver) {
            if (Constants.buttonGotoMenu.contains(pt))
                game.setScreen(game.titleScreen);
            if (Constants.buttonStartOver.contains(pt)) this.init();
        }
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // TODO: End continuous fire
        return super.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean keyDown(int keycode) {
        // TODO: Same as touchDown with firing
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            game.settings.DRAW_RINGS(!game.settings.DRAW_RINGS());
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            vesselFixed = !vesselFixed;
        }
        return super.keyDown(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        // TODO: Same as touchUp when firing
        return super.keyUp(keycode);
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log(TAG, "called 'resize()'");
        actionViewport.update(width, height, true);
    }

    public void updateAssets(float delta) {
        // Update timer
        timerGame += delta;

        // Add debris
        float rate = 0.8f;
        if (timerGame > rate + timerDebris) {
            timerDebris += rate;
            float r = Constants.MAP_SIZE_Y_360 * random.nextFloat();
            debris.add(new Actor(0, r));
            debris.peek().velocity.x = 1000;
        }

        // Update debris mapPosition
        for (int i = debris.size - 1; i >= 0; i--) {
            debris.get(i).mapPosition.x += 1f;
            if (debris.get(i).mapPosition.x >= Constants.MAP_SIZE_X * 1.2) {
                debris.removeIndex(i);
                continue;
            }
        }

        if (vessels.size > 0 && vessels.get(0).isDead) vessels.removeIndex(0);
        else if (vessels.size == 0) {
            endGame();
        }

        enemies.update(timerGame, playerAngle);

        playerBullets.update(delta);

        enemyBullets.update(delta);

        powerupManager.update(delta);

        explosionManager.update(delta);
    }

    public void endGame() {
        gameOver = true;
    }

    public void updateInput(float delta) {
        // Getting pressed keys
        if (Gdx.input.isKeyPressed(Input.Keys.W)
                || Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            // Fire primary weapon
            for (Vessel vessel: vessels) {
                if (!vessel.isDead && vessel.fire()){
                    int weaponLevel = vessel.weaponLevel;
                    float xPos = vessel.mapPosition.x;
                    float yPos = vessel.mapPosition.y;
                    playerBullets.add(weaponLevel, xPos, yPos);
                }
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            // Fire secondary weapon
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            // Fire tertiary weapon
        }

        // TODO: For testing, switch weapons with number keys
        if (Constants.LOG_LEVEL != Application.LOG_NONE) {
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_1))
                vessels.get(0).weaponLevel = 0;
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_2))
                vessels.get(0).weaponLevel = 1;
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_3))
                vessels.get(0).weaponLevel = 2;
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_4))
                vessels.get(0).weaponLevel = 3;
        }
    }

    public void updateRotation(float delta) {
        if (accelAvailable) {
            float accelX = Gdx.input.getAccelerometerX() - accelBalancer.x,
                  accelY = Gdx.input.getAccelerometerY() - accelBalancer.y;
            Vector2 deviceAccel = new Vector2(accelX, accelY);
            deviceAccel.nor();
            // TODO: Implement accelerometer motion
            if (vesselFixed) {
                // TODO: Vessel moves with X-axis leaning
            } else {
                // TODO: Move towards deviceAccel positionAngle
            }
        } else {
            // Key input
            float accelY = 0f;
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                accelY -= 1f;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                accelY += 1f;
            }
            for (Vessel actor : vessels) {
                actor.accelerate(new Vector2(0, accelY));
                float distMoved = actor.update(this, delta);
                if (vesselFixed) {
                    dspRotation -= distMoved;
                }
            }
            vanishingPoint.setAngle(playerAngle + 180f + dspRotation);
        }
    }

    public void updateCollision() {
        for (Actor enemy: enemies.enemies) {
            if (!enemy.isDead) {
                score += playerBullets.checkForCollisions(this, enemy);
            }
        }
        for (Actor vessel: vessels) {
            if (!vessel.isDead) {
                enemyBullets.checkForCollisions(this, vessel);
            }
        }
        for (Vessel vessel: vessels) {
            if (!vessel.isDead) {
                powerupManager.checkCollision(this, vessel);
            }
        }
    }

    @Override
    public void render(float delta) {
        if (delta > 0.05f) return;  // Avoids spikes in delta value.

        if (vessels.size > 0) playerAngle = vessels.get(0).positionAngle();

        if (!pause) {
            // TODO: keep all updates out of the render methods
            // TODO: Enemy mapPosition update remove from render methods
            updateInput(delta);
            updateAssets(delta);
            updateRotation(delta);
            updateCollision();
        }

        // Background color fill
        DrawingUtils.clearScreen();

        /** DRAW STARS **/
        VisualEffects.drawStars(myRenderer, dspRotation, vanishingPoint);

        /** DRAW PLANET **/
        myRenderer.batch.begin();
        Vector3 pos = ProjectionUtils.projectPoint3D(this, new Vector2(0, 0));
        myRenderer.batch.draw(planet,
                pos.x - planet.getRegionWidth() / 2, pos.y - planet.getRegionHeight() / 2,
                planet.getRegionWidth() / 2, planet.getRegionWidth() / 2,
                planet.getRegionWidth(), planet.getRegionHeight(),
                0.5f, 0.5f,
                dspRotation);
        myRenderer.batch.end();

        /** DRAW FUNNEL **/
        VisualEffects.drawTunnel(this, delta, game.settings.DRAW_RINGS());

        /** DRAW TEMP DEBRIS **/
        DrawingUtils.enableBlend();
        myRenderer.begin(ShapeRenderer.ShapeType.Filled);
        myRenderer.setColor(new Color(.8f, .9f, 1f, 0.3f));
        for (Actor d: debris) {
            Vector3 placement = ProjectionUtils.projectPoint3D(this, d.mapPosition);
            myRenderer.circle(placement.x, placement.y, 1f * placement.z);
        }
        myRenderer.end();
        DrawingUtils.disableBlend();

        /** Draw player vessels **/
        for (Vessel vessel: vessels) {
            vessel.render(this, delta);
        }

        /** Draw enemies **/
        enemies.render(this, delta);

        playerBullets.render(this);

        powerupManager.render(this);

        explosionManager.render(this);

        drawHUD(delta);
    }

    public void drawHUD(float delta) {
        float PADDING = Constants.PADDING;
        float HALFDISP = Constants.DISPLAY_SIZE/2;
        float DX = Constants.WEAPON_BLOCKS_XOFFSET;
        float DY = Constants.WEAPON_BLOCKS_YOFFSET;

        /** Shield/Health bars **/
        myRenderer.batch.begin();
        myRenderer.batch.draw(
                textShields,
                PADDING - HALFDISP,
                -Constants.GAUGE_TEXT_HEIGHT - PADDING + HALFDISP,
                Constants.GAUGE_TEXT_WIDTH,
                Constants.GAUGE_TEXT_HEIGHT
        );
        myRenderer.batch.draw(
                textWeapons,
                DX + PADDING - HALFDISP,
                DY - Constants.GAUGE_TEXT_HEIGHT - PADDING + HALFDISP,
                Constants.GAUGE_TEXT_WIDTH,
                Constants.GAUGE_TEXT_HEIGHT
        );
        myRenderer.batch.draw(
                textScore,
                2*DX + PADDING - HALFDISP,
                2*DY - Constants.GAUGE_TEXT_HEIGHT - PADDING + HALFDISP,
                Constants.GAUGE_TEXT_WIDTH,
                Constants.GAUGE_TEXT_HEIGHT
        );
        myRenderer.batch.end();
        myRenderer.begin(ShapeRenderer.ShapeType.Filled);
        int hitPoints = vessels.size > 0 ? vessels.get(0).getShieldHitPoints() : 0;
        for (int i=0; i<hitPoints; i++) {
            myRenderer.triangle(
                    64 + PADDING - HALFDISP + i * 21f, HALFDISP - PADDING - 16,
                    84 + PADDING - HALFDISP + i * 21f, HALFDISP - PADDING - 16,
                    67 + PADDING - HALFDISP + i * 21f, HALFDISP - PADDING,
                    Constants.SHIELD_STRENGTH_COLOR_BOTTOM,
                    Constants.SHIELD_STRENGTH_COLOR_BOTTOM,
                    Constants.SHIELD_STRENGTH_COLOR_TOP
            );
            myRenderer.triangle(
                    67 + PADDING - HALFDISP + i * 21f, HALFDISP - PADDING,
                    84 + PADDING - HALFDISP + i * 21f, HALFDISP - PADDING - 16,
                    87 + PADDING - HALFDISP + i * 21f, HALFDISP - PADDING,
                    Constants.SHIELD_STRENGTH_COLOR_TOP,
                    Constants.SHIELD_STRENGTH_COLOR_BOTTOM,
                    Constants.SHIELD_STRENGTH_COLOR_TOP
            );
        }
        int wLevel = vessels.size > 0 ? vessels.get(0).weaponLevel+1 : 0;
        for (int i=0; i<wLevel; i++) {
            myRenderer.triangle(
                    DX + 64 + PADDING - HALFDISP + i * 21f, DY + HALFDISP - PADDING - 16,
                    DX + 84 + PADDING - HALFDISP + i * 21f, DY + HALFDISP - PADDING - 16,
                    DX + 67 + PADDING - HALFDISP + i * 21f, DY + HALFDISP - PADDING,
                    Constants.WEAPON_STRENGTH_COLOR_BOTTOM,
                    Constants.WEAPON_STRENGTH_COLOR_BOTTOM,
                    Constants.WEAPON_STRENGTH_COLOR_TOP
            );
            myRenderer.triangle(
                    DX + 67 + PADDING - HALFDISP + i * 21f, DY + HALFDISP - PADDING,
                    DX + 84 + PADDING - HALFDISP + i * 21f, DY + HALFDISP - PADDING - 16,
                    DX + 87 + PADDING - HALFDISP + i * 21f, DY + HALFDISP - PADDING,
                    Constants.WEAPON_STRENGTH_COLOR_TOP,
                    Constants.WEAPON_STRENGTH_COLOR_BOTTOM,
                    Constants.WEAPON_STRENGTH_COLOR_TOP
            );
        }
        myRenderer.end();

        /** DRAW SHIP ROTATION BUTTON */
        myRenderer.batch.begin();
        TextureRegion texture = vesselFixed ? shipFixedButton : shipRotateButton;
        myRenderer.batch.draw(texture,
                Constants.rotateButtonRect.x,
                Constants.rotateButtonRect.y,
                Constants.rotateButtonRect.width,
                Constants.rotateButtonRect.height
        );
        myRenderer.batch.end();

        /** DRAW SCORE */
        font.getData().setScale(1.2f);
        myRenderer.batch.begin();
        for (int i=0; i<2; i++) {
            font.draw(myRenderer.batch,
                    "" + score,
                    Constants.DISPLAY_SIZE * -0.375f,
                    Constants.DISPLAY_SIZE * 0.41f,
                    Constants.DISPLAY_SIZE * 1f,
                    Align.left, false);
        }
        myRenderer.batch.end();

        /** DRAW GAME OVER AND BUTTONS */
        if (gameOver) {
            font.getData().setScale(3f);
            myRenderer.batch.begin();
            for (int i=0; i<3; i++) {
                font.draw(myRenderer.batch,
                        "Game Over",
                        Constants.DISPLAY_SIZE * 0f,
                        Constants.DISPLAY_SIZE * 0.1f,
                        Constants.DISPLAY_SIZE * 0f,
                        Align.center, true);
            }
            myRenderer.batch.end();


            /** DRAW NINEPATCH PANE */
            myRenderer.batch.begin();
            float blueButtonWidth = blueButtonTween.next(delta);
            Vector2 pt = actionViewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            pt.sub(Constants.DISPLAY_SIZE / 2, Constants.DISPLAY_SIZE / 2);
            NinePatch buttonPatch1, buttonPatch2;
            if (Constants.buttonGotoMenu.contains(pt)) {
                buttonPatch1 = bluePatch;
            } else {
                buttonPatch1 = bluePatchDark;
            }
            if (Constants.buttonStartOver.contains(pt)) {
                buttonPatch2 = bluePatch;
            } else {
                buttonPatch2 = bluePatchDark;
            }
            buttonPatch1.draw(myRenderer.batch,
                    Constants.buttonGotoMenu.x,
                    Constants.buttonGotoMenu.y,
                    blueButtonWidth,
                    Constants.buttonGotoMenu.height
            );
            buttonPatch2.draw(myRenderer.batch,
                    Constants.buttonStartOver.x,
                    Constants.buttonStartOver.y,
                    blueButtonWidth,
                    Constants.buttonStartOver.height
            );
            myRenderer.batch.end();

            /** DRAW TEXT AFTER PANES EXPAND */
            font.getData().setScale(1.4f);
            if (blueButtonTween.isDone()) {
                myRenderer.batch.begin();
//                float width = Constants.buttonGotoMenu.width/2 + 2;
                float height = Constants.buttonGotoMenu.height/2 + 10;
                font.draw(myRenderer.batch,
                        "Menu",
                        Constants.buttonGotoMenu.x,
                        Constants.buttonGotoMenu.y + height,
                        Constants.buttonGotoMenu.width,
                        Align.center, false);
                font.draw(myRenderer.batch,
                        "Retry",
                        Constants.buttonStartOver.x,
                        Constants.buttonStartOver.y + height,
                        Constants.buttonStartOver.width,
                        Align.center, false);
                myRenderer.batch.end();
            }
        }
    }

    @Override
    public void pause() {
        Gdx.app.log(TAG, "called 'pause()'");
        pause = true;
    }

    @Override
    public void resume() {
        Gdx.app.log(TAG, "called 'resume()'");
        pause = false;
    }

    @Override
    public void dispose() {

    }
}
