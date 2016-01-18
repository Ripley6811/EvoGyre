package com.mygdx.game.evogyre;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.Random;

/**
 * Created by Jay on 1/12/2016.
 */
public class GameScreen extends InputAdapter implements Screen {
    private static final String TAG = GameScreen.class.getName();

    EvoGyreGame game;
    FitViewport actionViewport;
    ShapeRenderer renderer;

    Array<Actor> debris;
    Vector2 vanishingPoint;
    Array<Vessel> vessels;  // Possible multiple spaceships, powerup
    Array<ShieldBlast> sheild_effects;
    Random random = new Random();

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
        sheild_effects = new Array<ShieldBlast>();
        // Unit vector giving direction of vanishing point transposition.
        // TODO: change this to be based on player position
        vanishingPoint = new Vector2(0f, 1f);

        renderer = new ShapeRenderer();
        renderer.setAutoShapeType(true);
        renderer.setProjectionMatrix(actionViewport.getCamera().combined);
        renderer.translate(Constants.DISPLAY_SIZE / 2f, Constants.DISPLAY_SIZE / 2f, 0);

        init();
    }

    public void init() {
        mapRotation = 0f;
        vessels.clear();
        vessels.add(new Vessel(Constants.MAP_SIZE_X, 300f));
        vanishingPoint.setAngle(vessels.get(0).positionAngle() + 180f);
        VisualEffects.drawTunnelInit(Constants.ANIMATE_FUNNEL_DURATION);
        timerGame = 0f;
    }

    @Override
    public void show() {

    }

    public void setAccelerometerBalanced() {
        float xa = Gdx.input.getAccelerometerX(),
              ya = Gdx.input.getAccelerometerY();
        accelBalancer.set(xa, ya);
    }

    @Override
    public void hide() {

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // TODO: Create a boolean for dealing with continuous touch, use for firing weapons
        VisualEffects.drawTunnelInit(Constants.ANIMATE_FUNNEL_DURATION);
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
        actionViewport.update(width, height, true);
    }

    public void update(float delta) {
        // Update timer
        timerGame += delta;

        // Add debris every 2 seconds
        float rate = 0.2f;
        if (timerGame > rate + timerDebris) {
            timerDebris += rate;
            float r = Constants.MAP_SIZE_Y * random.nextFloat();
            debris.add(new Vessel(0, r));
//            debris.add(new Actor(0, 0));
//            debris.add(new Actor(0, 90));
//            debris.add(new Actor(0, 200));
        }

        // Update debris position
        for (int i = debris.size-1; i >= 0; i--) {
            debris.get(i).position.x += 1f;
            if (debris.get(i).position.x >= Constants.MAP_SIZE_X * 1.2) {
                debris.removeIndex(i);
                continue;
            }
        }

        updateRotation(delta);


        if (random.nextFloat() > 0.98f) {
            sheild_effects.add(new ShieldBlast());
        }
        // TODO: Remove finished effects
        for (ShieldBlast blast: sheild_effects) {
            blast.update(delta);
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
            // TODO: include acceleration like Parallax game
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
        update(delta);

        // Background color fill
        Color BG_COLOR = Constants.BACKGROUND_COLOR;
        Gdx.gl.glClearColor(BG_COLOR.r, BG_COLOR.g, BG_COLOR.b, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        /** DRAW STARS **/
        VisualEffects.drawStars(renderer, mapRotation, vanishingPoint);

        /** DRAW PLANET **/
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.BLUE);
        renderer.circle(vanishingPoint.x * Constants.CENTER_DISPLACEMENT, vanishingPoint.y * Constants.CENTER_DISPLACEMENT, 20, 100);
        renderer.end();

        /** DRAW FUNNEL **/
        if (game.settings.prefs.getBoolean("draw funnel", Constants.DRAW_FUNNEL)) {
            VisualEffects.drawTunnel(delta, renderer, mapRotation, vanishingPoint);
        }

        // Draw all debris
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.GOLD);
        for (Actor d: debris) {
            Vector3 placement = ProjectionUtils.projectPoint(d.position, mapRotation, vanishingPoint);
            renderer.circle(placement.x, placement.y, 5f*placement.z);
        }

        /** Draw player vessels **/
        renderer.setColor(Color.GREEN);
        Vector3 placement = new Vector3(0,0,0);
        for (Vessel actor: vessels) {
            placement = ProjectionUtils.projectPoint(actor.position, mapRotation, vanishingPoint);
            renderer.circle(placement.x, placement.y, 10f);
        }
        renderer.end();

        /** DRAW SHIELD EFFECTS **/
        for (ShieldBlast effect: sheild_effects) {
            if (!effect.isDone) {
                VisualEffects.shieldGradientEffect(renderer, placement, true, effect.phase, effect.alpha);
            }
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
