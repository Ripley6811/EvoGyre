package com.mygdx.game.evogyre;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.evogyre.Utils.DrawingUtils;

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
    TextureRegion planet;
    TextureRegion textShields;
    TextureRegion textWeapons;

    float timerGame;
    float timerDebris;
    float dspRotation = 0f;
    boolean vesselFixed = false;
    boolean accelAvailable = Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer);
    Vector2 accelBalancer = new Vector2();  // For centering device in any mapPosition
    boolean pause = false;

    public GameScreen(EvoGyreGame game) {
        Gdx.input.setInputProcessor(this);
        Gdx.input.setCatchBackKey(true);
        this.game = game;
        actionViewport = new FitViewport(Constants.DISPLAY_SIZE, Constants.DISPLAY_SIZE);
        actionViewport.apply(true);

        timerGame = 0f;
        timerDebris = 0f;
        setAccelerometerBalanced();

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
        enemies = new EnemyManager(atlas, enemyBullets);
        planet = atlas.createSprite("lavender");
        textShields = atlas.createSprite("text_shields");
        textWeapons = atlas.createSprite("text_weapons");

        DrawingUtils.initGLSettings();

        init();
    }

    /**
     * Initialize new game. Loads level enemies and attributes from JSON.
     */
    public void init() {
        dspRotation = 0f;
        vessels.clear();
        vessels.add(new Vessel(Constants.MAP_SIZE_X, 300f, atlas));
        vanishingPoint.setAngle(vessels.get(0).positionAngle() + 180f);
        VisualEffects.drawTunnelInit(Constants.ANIMATE_FUNNEL_DURATION);
        timerGame = 0f;

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
    }

    @Override
    public void show() {
        Gdx.app.log(TAG, "called 'show()'");
        pause = false;
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

        enemies.update(timerGame);

        playerBullets.update(delta);

        enemyBullets.update(delta);
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
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) vessels.get(0).weaponLevel = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) vessels.get(0).weaponLevel = 1;
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) vessels.get(0).weaponLevel = 2;
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_4)) vessels.get(0).weaponLevel = 3;
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
                float distMoved = actor.update(delta);
                if (vesselFixed) {
                    dspRotation -= distMoved;
                }
            }
            vanishingPoint.setAngle(vessels.get(0).positionAngle() + 180f + dspRotation);
        }
    }

    public void updateCollision() {
        for (Actor enemy: enemies.enemies) {
            if (!enemy.isDead) {
                int hits = playerBullets.checkForCollisions(this, enemy);
            }
        }
        for (Actor vessel: vessels) {
            if (!vessel.isDead) {
                int hits = enemyBullets.checkForCollisions(this, vessel);
            }
        }
    }

    @Override
    public void render(float delta) {
        if (delta > 0.05f) return;  // Avoids spikes in delta value.

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

        drawHUD();
    }

    public void drawHUD() {
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
        myRenderer.batch.end();
        myRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i=0; i<vessels.get(0).getShieldHitPoints(); i++) {
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
        for (int i=0; i<vessels.get(0).weaponLevel+1; i++) {
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
