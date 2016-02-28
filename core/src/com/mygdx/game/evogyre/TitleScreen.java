package com.mygdx.game.evogyre;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.evogyre.Utils.DrawingUtils;

/**
 * Created by Jay on 1/12/2016.
 */
public class TitleScreen extends InputAdapter implements Screen {
    private static final String TAG = TitleScreen.class.getName();
    EvoGyreGame game;
    FitViewport actionViewport;
    SpriteBatch batch;
    MyShapeRenderer myRenderer;

    TextureAtlas atlas;
    TextureRegion planet;
    TextureRegion titleText;

    public TitleScreen(EvoGyreGame game) {
        Gdx.input.setInputProcessor(this);
        Gdx.input.setCatchBackKey(true);
        this.game = game;
        actionViewport = new FitViewport(Constants.DISPLAY_SIZE, Constants.DISPLAY_SIZE);
        actionViewport.apply(true);

        batch = new SpriteBatch();
        myRenderer = new MyShapeRenderer(batch);
        myRenderer.setAutoShapeType(true);
        myRenderer.setProjectionMatrix(actionViewport.getCamera().combined);
        myRenderer.translate(Constants.DISPLAY_SIZE / 2f, Constants.DISPLAY_SIZE / 2f, 0);

        /** LOAD ASSETS **/
        atlas = game.assets.get(Constants.MAIN_ATLAS);
        planet = atlas.createSprite("lavender");
        titleText = atlas.createSprite("title_EvoGyre");
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
        Gdx.app.log(TAG, "called 'resize()'");
        actionViewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        // Background color fill
        DrawingUtils.clearScreen();

        /** DRAW STARS **/
        VisualEffects.drawStars(myRenderer, 0f, new Vector2(0,0));

        /** DRAW PLANET **/
        myRenderer.batch.begin();
        myRenderer.batch.draw(planet,
                -Constants.DISPLAY_SIZE*.25f - planet.getRegionWidth() / 2,
                Constants.DISPLAY_SIZE*.25f - planet.getRegionHeight() / 2,
                planet.getRegionWidth() / 2, planet.getRegionWidth() / 2,
                planet.getRegionWidth(), planet.getRegionHeight(),
                1.2f, 1.2f, 0);
        myRenderer.batch.end();

        /** DRAW TITLE */
        myRenderer.batch.begin();
        myRenderer.batch.draw(
                titleText,
                -titleText.getRegionWidth() / 2 - 20, 80,
                titleText.getRegionWidth(), titleText.getRegionHeight(),
                titleText.getRegionWidth(), titleText.getRegionHeight(),
                0.8f, 0.8f,
                0f
        );
        myRenderer.batch.end();
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
