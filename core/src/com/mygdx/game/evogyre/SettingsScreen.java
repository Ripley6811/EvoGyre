package com.mygdx.game.evogyre;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;

/**
 * Created by Jay on 1/14/2016.
 */
public class SettingsScreen extends InputAdapter implements Screen {
    EvoGyreGame game;

    public SettingsScreen(EvoGyreGame game) {
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
