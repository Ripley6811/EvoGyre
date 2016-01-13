package com.mygdx.game.evogyre;

import com.badlogic.gdx.Gdx;
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
    EvoGyreGame game;
    FitViewport actionViewport;
    ShapeRenderer renderer;

    Array<Actor> debris;
    Vector2 vanishingPoint;
    Array<Actor> vessels;  // Possible multiple spaceships, powerup

    float timer;

    public GameScreen(EvoGyreGame game) {
        this.game = game;
        actionViewport = new FitViewport(Constants.DISPLAY_SIZE, Constants.DISPLAY_SIZE);
        actionViewport.apply(true);

        timer = 0f;

        debris = new Array<Actor>();
        vessels = new Array<Actor>();
        // Unit vector giving direction of vanishing point transposition.
        vanishingPoint = new Vector2(0f, 1f);

        renderer = new ShapeRenderer();
        renderer.setAutoShapeType(true);
        renderer.setProjectionMatrix(actionViewport.getCamera().combined);
    }

    public void init() {
        vessels.add(new Actor(Constants.MAP_SIZE, 270f));
    }

    @Override
    public void show() {

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
        // TODO: Same as touchDown
        return super.keyDown(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        // TODO: Same as touchUp
        return super.keyUp(keycode);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render(float delta) {
        /* UPDATE ASSETS */

        // Update timer
        timer += delta;

        // Add debris every 2 seconds
        float rate = 0.1f;
        if (timer > rate) {
            timer -= rate;
            Random random = new Random();
            debris.add(new Actor(0, Constants.MAP_SIZE * random.nextFloat()));
            debris.add(new Actor(0, 0));
        }
        vanishingPoint.rotate(delta);
        // Update debris position
        for (int i = debris.size-1; i >= 0; i--) {
            debris.get(i).position.x += 0.5f;
            if (debris.get(i).position.x >= Constants.MAP_SIZE) {
                debris.removeIndex(i);
                continue;
            }
        }

        /* RENDER DISPLAY */
        renderer.identity();
        renderer.translate(Constants.DISPLAY_SIZE/2f, Constants.DISPLAY_SIZE/2f, 0);

        // Background color fill
        Color BG_COLOR = Constants.BACKGROUND_COLOR;
        Gdx.gl.glClearColor(BG_COLOR.r, BG_COLOR.g, BG_COLOR.b, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw all debris
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.YELLOW);
        for (Actor d: debris) {
            Vector3 placement = ProjectionUtils.projectPoint(d.position, vanishingPoint);
            renderer.circle(placement.x, placement.y, 1f);
        }
        renderer.end();
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
