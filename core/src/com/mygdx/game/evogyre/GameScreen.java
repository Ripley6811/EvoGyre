package com.mygdx.game.evogyre;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.Random;

/**
 * Created by Jay on 1/12/2016.
 */
public class GameScreen extends InputAdapter implements Screen {
    private static final String TAG = GameScreen.class.getName();

    EvoGyreGame game;
    FitViewport actionViewport;
    MyShapeRenderer renderer;

    TextureAtlas atlas;
    Array<Actor> debris;
    Vector2 vanishingPoint;
    Array<Vessel> vessels;  // Possible multiple spaceships, powerup
    Random random = new Random();
    BulletManager playerBullets;
    BulletManager enemyBullets;
    EnemyManager enemies;
    TextureRegion planet;

    float timerGame;
    float timerDebris;
    float mapRotation;
    boolean vesselFixed = false;
    boolean accelAvailable = Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer);
    Vector2 accelBalancer = new Vector2();  // For centering device in any position

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
        // Unit vector giving direction of vanishing point transposition.
        // TODO: change this to be based on player position
        vanishingPoint = new Vector2(0f, 1f);

        renderer = new MyShapeRenderer();
        renderer.setAutoShapeType(true);
        renderer.setProjectionMatrix(actionViewport.getCamera().combined);
        renderer.translate(Constants.DISPLAY_SIZE / 2f, Constants.DISPLAY_SIZE / 2f, 0);

        /** LOAD ASSETS **/
        atlas = game.assets.get(Constants.MAIN_ATLAS);
        playerBullets = new BulletManager(atlas, Constants.PRIMARY_WEAPON_SETUP);
        enemyBullets = new BulletManager(atlas, Constants.ENEMY_WEAPON_SETUP);
        enemies = new EnemyManager(atlas, enemyBullets);
        planet = atlas.createSprite("lavender");

        init();
    }

    public void init() {
        mapRotation = 0f;
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
    }

    public void setAccelerometerBalanced() {
        float xa = Gdx.input.getAccelerometerX(),
              ya = Gdx.input.getAccelerometerY();
        accelBalancer.set(xa, ya);
    }

    @Override
    public void hide() {
        Gdx.app.log(TAG, "called 'hide()'");
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
        }

        // Update debris position
        for (int i = debris.size - 1; i >= 0; i--) {
            debris.get(i).position.x += 1f;
            if (debris.get(i).position.x >= Constants.MAP_SIZE_X * 1.2) {
                debris.removeIndex(i);
                continue;
            }
        }

        enemies.update(timerGame);

        playerBullets.update(delta);

        enemyBullets.update(delta);
        // TODO: Check bullet collision and resolve
    }

    public void updateInput(float delta) {
        // Getting pressed keys
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            // Fire primary weapon
            for (Vessel vessel: vessels) {
                if (vessel.fire()){
                    int weaponLevel = vessel.weaponLevel;
                    float xPos = vessel.position.x;
                    float yPos = vessel.position.y;
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
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
            vessels.get(0).weaponLevel = 0;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
            vessels.get(0).weaponLevel = 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
            vessels.get(0).weaponLevel = 2;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_4)) {
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
                float distMoved = actor.update(delta);
                if (vesselFixed) {
                    mapRotation -= distMoved;
                }
            }
            vanishingPoint.setAngle(vessels.get(0).positionAngle() + 180f + mapRotation);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.app.log(TAG, "delta = " + delta);
        if (delta > 0.05f) return;  // Avoids spikes in delta value.

        updateInput(delta);
        updateAssets(delta);
        updateRotation(delta);

        // Background color fill
        Color BG_COLOR = Constants.BACKGROUND_COLOR;
        Gdx.gl.glClearColor(BG_COLOR.r, BG_COLOR.g, BG_COLOR.b, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        /** DRAW STARS **/
        VisualEffects.drawStars(renderer, mapRotation, vanishingPoint);

        /** DRAW PLANET **/
        renderer.batch.begin();
        Vector3 pos = ProjectionUtils.projectPoint(new Vector2(0,0), mapRotation, vanishingPoint);
        renderer.batch.draw(planet,
                pos.x-planet.getRegionWidth()/2, pos.y-planet.getRegionHeight()/2,
                planet.getRegionWidth()/2, planet.getRegionWidth()/2,
                planet.getRegionWidth(), planet.getRegionHeight(),
                0.5f, 0.5f,
                mapRotation);
        renderer.batch.end();

        /** DRAW FUNNEL **/
        VisualEffects.drawTunnel(delta, renderer, mapRotation, vanishingPoint, game.settings.DRAW_RINGS());

        /** DRAW TEMP DEBRIS **/
        boolean blend_enabled = Gdx.gl.glIsEnabled(GL20.GL_BLEND);
        if (!blend_enabled) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(new Color(1f, .9f, 0.4f, 0.4f));
        for (Actor d: debris) {
            Vector3 placement = ProjectionUtils.projectPoint(d.position, mapRotation, vanishingPoint);
            renderer.circle(placement.x, placement.y, 1f*placement.z);
        }
        renderer.end();
        if (!blend_enabled) {
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }

        /** Draw player vessels **/
        for (Vessel vessel: vessels) {
            vessel.render(renderer, delta, mapRotation, vanishingPoint);
        }

        /** Draw enemies **/
        enemies.render(renderer, delta, mapRotation, vanishingPoint);

        playerBullets.render(renderer, mapRotation, vanishingPoint);
    }

    @Override
    public void pause() {
        Gdx.app.log(TAG, "called 'pause()'");
    }

    @Override
    public void resume() {
        Gdx.app.log(TAG, "called 'resume()'");
    }

    @Override
    public void dispose() {

    }
}
