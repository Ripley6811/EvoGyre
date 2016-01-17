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
    Array<Actor> vessels;  // Possible multiple spaceships, powerup
    Array<ShieldBlast> blasts;
    Random random = new Random();

    float timer;
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

        timer = 0f;
        timerDebris = 0f;
        setAccelerometerBalanced();

        debris = new Array<Actor>();
        vessels = new Array<Actor>();
        blasts = new Array<ShieldBlast>();
        // Unit vector giving direction of vanishing point transposition.
        // TODO: change this to be based on player position
        vanishingPoint = new Vector2(0f, 1f);

        renderer = new ShapeRenderer();
        renderer.setAutoShapeType(true);
        renderer.setProjectionMatrix(actionViewport.getCamera().combined);
        renderer.translate(Constants.DISPLAY_SIZE / 2f, Constants.DISPLAY_SIZE / 2f, 0);
//        Gdx.gl.glEnable(GL20.GL_BLEND);
//        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        init();
    }

    public void init() {
        mapRotation = 0f;
        vessels.clear();
        vessels.add(new Actor(Constants.MAP_SIZE_X, 270f));
        vanishingPoint.setAngle(vessels.get(0).positionAngle() + 180f);
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
        timer += delta;

        // Add debris every 2 seconds
        float rate = 0.2f;
        if (timer > rate + timerDebris) {
            timerDebris += rate;
            float r = Constants.MAP_SIZE_Y * random.nextFloat();
            debris.add(new Actor(0, r));
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


        if (random.nextFloat() > 0.99f) {
            blasts.add(new ShieldBlast());
        }
        // TODO: Remove finished effects
        for (ShieldBlast blast: blasts) {
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
            for (Actor actor : vessels) {
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


//        renderer.begin(ShapeRenderer.ShapeType.Line);
//        renderer.setColor(Constants.CYLINDER_COLOR);
//        for (float i=1f; i>=0.5f; i -= 0.1f) {
//            float vi = ProjectionUtils.vanishingPower(i);
//            Vector2 tmpV = new Vector2(vanishingPoint);
//            tmpV.setLength((1f-vi) * Constants.CENTER_DISPLACEMENT);
//            renderer.circle(tmpV.x, tmpV.y, vi * Constants.PROJECTION_RADIUS, 100);
//        }
//        renderer.end();

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.BLUE);
        renderer.circle(vanishingPoint.x * Constants.CENTER_DISPLACEMENT, vanishingPoint.y * Constants.CENTER_DISPLACEMENT, 20, 100);
        renderer.end();

        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Constants.CYLINDER_COLOR);
        for (float i=Constants.MAP_SIZE_X; i>0f && i>Constants.MAP_SIZE_X-500*timer; i -= 50f) {
            Vector3 tmpV1 = ProjectionUtils.projectPoint(new Vector2(i, 0), mapRotation, vanishingPoint);
            Vector3 tmpV2 = ProjectionUtils.projectPoint(new Vector2(i, 180), mapRotation, vanishingPoint);
            renderer.circle((tmpV1.x + tmpV2.x) / 2, (tmpV1.y + tmpV2.y) / 2, tmpV1.dst(tmpV2) / 2, 100);
        }
        renderer.end();

        // Draw all debris
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.GOLD);
        for (Actor d: debris) {
            Vector3 placement = ProjectionUtils.projectPoint(d.position, mapRotation, vanishingPoint);
            renderer.circle(placement.x, placement.y, 5f*placement.z);
        }

        // Draw player vessels
        renderer.setColor(Color.GREEN);
        for (Actor actor: vessels) {
            Vector3 placement = ProjectionUtils.projectPoint(actor.position, mapRotation, vanishingPoint);
            renderer.circle(placement.x, placement.y, 6f);
        }
        renderer.end();

        for (ShieldBlast blast: blasts) {
            if (!blast.isDone) {
                ImageAssets.radialGradientFill(renderer, 90f, blast.phase, blast.alpha);
            }
        }
//        ImageAssets.radialGradientFill(renderer, 90f, .8f, .8f);
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
