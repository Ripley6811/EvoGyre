package com.mygdx.game.evogyre;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;

/**
 * Text spirals into the distance and towards a planet as if scrolling along the
 * inside of a cylinder.
 * Created by Jay on 1/12/2016.
 */
public class StoryScreen extends InputAdapter implements Screen {
    EvoGyreGame game;

    public StoryScreen(EvoGyreGame game) {
        this.game = game;
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return super.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public boolean keyDown(int keycode) {
        return super.keyDown(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        return super.keyUp(keycode);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render(float delta) {

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
