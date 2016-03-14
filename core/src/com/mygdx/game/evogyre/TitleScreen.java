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
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mygdx.game.evogyre.Utils.DrawingUtils;
import com.mygdx.game.evogyre.Utils.Tween;

/**
 * Created by Jay on 1/12/2016.
 */
public class TitleScreen extends InputAdapter implements Screen {
    private static final String TAG = TitleScreen.class.getName();
    EvoGyreGame game;
    ExtendViewport actionViewport;
    SpriteBatch batch;
    MyShapeRenderer myRenderer;

    BitmapFont font;

    TextureAtlas atlas;
    TextureRegion planet;
    TextureRegion titleText;
    NinePatch bluePatch;
    NinePatch bluePatchDark;
    Tween.QuadInOut bluePatchTween;
    Tween.QuadInOut blueButtonTween;

    int storyOrControls = 0;
    boolean isAndroid = Gdx.app.getType() == Application.ApplicationType.Android;
    boolean hasAccelerometer = Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer);
    float elapsedTime;

    public TitleScreen(EvoGyreGame game) {
        Gdx.input.setCatchBackKey(true);

        this.game = game;
        elapsedTime = 0f;
        actionViewport = new ExtendViewport(Constants.GAMEPLAY_SIZE, Constants.GAMEPLAY_SIZE);
        actionViewport.apply(true);

        batch = new SpriteBatch();
        myRenderer = new MyShapeRenderer(batch);
        myRenderer.setAutoShapeType(true);
        myRenderer.setProjectionMatrix(actionViewport.getCamera().combined);

        /** LOAD ASSETS **/
        atlas = game.assets.get(Constants.MAIN_ATLAS);
        planet = atlas.createSprite("lavender");
        titleText = atlas.createSprite("title_EvoGyre");
        bluePatch = new NinePatch(
                atlas.createSprite("bluepane"),
                15,15,15,15
        );
        bluePatchDark = new NinePatch(
                atlas.createSprite("bluepanedark"),
                15,15,15,15
        );
        bluePatchTween = new Tween.QuadInOut(
                bluePatch.getTotalWidth(),
                Constants.GAMEPLAY_SIZE *.8f,
                Constants.PANEL_TWEEN_TIME
        );
        blueButtonTween = new Tween.QuadInOut(
                bluePatch.getTotalWidth(),
                Constants.buttonRect1.width,
                Constants.PANEL_TWEEN_TIME
        );

        /** SETUP FONT */
        font = new BitmapFont();
        font.setColor(Color.CYAN);
        font.getData().setScale(1.4f);
        font.getRegion().getTexture().setFilter(
                Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        /** PLAY INTRO MUSIC */
        AudioAssets.INTRO_MUSIC.setLooping(true);
//        AudioAssets.INTRO_MUSIC.play();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
        bluePatchTween = new Tween.QuadInOut(
                bluePatch.getTotalWidth(),
                Constants.GAMEPLAY_SIZE *.8f,
                Constants.PANEL_TWEEN_TIME
        );
        blueButtonTween = new Tween.QuadInOut(
                bluePatch.getTotalWidth(),
                Constants.buttonRect1.width,
                Constants.PANEL_TWEEN_TIME
        );
        AudioAssets.INTRO_MUSIC.setVolume(1.0f);
        AudioAssets.INTRO_MUSIC.play();
    }

    @Override
    public void hide() {

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector2 pt = actionViewport.unproject(new Vector2(screenX, screenY));

        if (Constants.buttonRect1.contains(pt)) storyOrControls = 0;
        if (Constants.buttonRect2.contains(pt)) storyOrControls = 1;
        if (Constants.buttonRect3.contains(pt)) game.setScreen(game.gameScreen);

        Gdx.app.debug(TAG, "Screen: " + screenX + ", " + screenY);
        Gdx.app.debug(TAG, "Unproj: " + pt.x + ", " + pt.y);

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
        Gdx.app.log(TAG, "called 'resize()'");
        actionViewport.update(width, height, true);
        actionViewport.getCamera().position.set(300f,300f,0f);
        actionViewport.getCamera().update();
        myRenderer.setProjectionMatrix(actionViewport.getCamera().combined);
    }

    @Override
    public void render(float delta) {
        elapsedTime += delta;

        /** CAMERA PANS UPWARD IN BEGINNING */
        actionViewport.getCamera().position.set(
                300f,
                Interpolation.sineOut.apply(100f, 300f, Math.min(1f, elapsedTime/Constants.INTRO_GRAPHICS_TWEEN_TIME)),
                0f
        );
        actionViewport.getCamera().update();
        myRenderer.setProjectionMatrix(actionViewport.getCamera().combined);

        // Background color fill
        DrawingUtils.clearScreen();

        /** DRAW STARS **/
        VisualEffects.drawStars(myRenderer, 0f, new Vector2(0, 0));

        /** DRAW PLANET **/
        myRenderer.batch.begin();
        myRenderer.batch.draw(planet,
                Constants.GAMEPLAY_SIZE * .25f - planet.getRegionWidth() / 2,
                Interpolation.circleOut.apply(Constants.GAMEPLAY_SIZE, 0, Math.min(1f, elapsedTime/Constants.INTRO_GRAPHICS_TWEEN_TIME)) +
                Constants.GAMEPLAY_SIZE * .75f - planet.getRegionHeight() / 2,
                planet.getRegionWidth() / 2, planet.getRegionWidth() / 2,
                planet.getRegionWidth(), planet.getRegionHeight(),
                1.2f, 1.2f, 0);
        myRenderer.batch.end();

        /** DRAW TITLE */
        myRenderer.batch.begin();
        myRenderer.batch.draw(
                titleText,
                Interpolation.circleOut.apply(Constants.GAMEPLAY_SIZE, 0, Math.min(1f, elapsedTime/Constants.INTRO_GRAPHICS_TWEEN_TIME)) +
                Constants.GAMEPLAY_SIZE * .1f,
                Interpolation.circleOut.apply(-Constants.GAMEPLAY_SIZE, 0, Math.min(1f, elapsedTime/Constants.INTRO_GRAPHICS_TWEEN_TIME)) +
                Constants.GAMEPLAY_SIZE * .65f,
                titleText.getRegionWidth()/2, titleText.getRegionHeight()/2,
                titleText.getRegionWidth(), titleText.getRegionHeight(),
                0.8f, 0.8f,
                Interpolation.circleOut.apply(Constants.GAMEPLAY_SIZE * 5f, 0, Math.min(1f, elapsedTime/Constants.INTRO_GRAPHICS_TWEEN_TIME)) +
                0f
        );
        myRenderer.batch.end();

        /** DRAW NINEPATCH PANE */
        if (elapsedTime > Constants.INTRO_GRAPHICS_TWEEN_TIME) {
            myRenderer.batch.begin();
            float bluePatchWidth = bluePatchTween.next(delta);
            float blueButtonWidth = blueButtonTween.next(delta);
            Vector2 pt = actionViewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));

            NinePatch buttonPatch1, buttonPatch2, buttonPatch3;
            if (Constants.buttonRect1.contains(pt)) {
                buttonPatch1 = bluePatch;
            } else {
                buttonPatch1 = bluePatchDark;
            }
            if (Constants.buttonRect2.contains(pt)) {
                buttonPatch2 = bluePatch;
            } else {
                buttonPatch2 = bluePatchDark;
            }
            if (Constants.buttonRect3.contains(pt)) {
                buttonPatch3 = bluePatch;
            } else {
                buttonPatch3 = bluePatchDark;
            }
            buttonPatch1.draw(myRenderer.batch,
                    Constants.buttonRect1.x,
                    Constants.buttonRect1.y,
                    blueButtonWidth,
                    Constants.buttonRect1.height
            );
            buttonPatch2.draw(myRenderer.batch,
                    Constants.buttonRect2.x,
                    Constants.buttonRect2.y,
                    blueButtonWidth,
                    Constants.buttonRect2.height
            );
            buttonPatch3.draw(myRenderer.batch,
                    Constants.buttonRect3.x,
                    Constants.buttonRect3.y,
                    blueButtonWidth,
                    Constants.buttonRect3.height
            );
            bluePatchDark.draw(myRenderer.batch,
                    Constants.GAMEPLAY_SIZE * .1f,
                    Constants.GAMEPLAY_SIZE * .05f,
                    bluePatchWidth,
                    220
            );
//        bluePatchWidth = Math.min(bluePatchWidth+800*delta, Constants.GAMEPLAY_SIZE*.8f);
            myRenderer.batch.end();
        }

        /** DRAW TEXT AFTER PANES EXPAND */
        if (bluePatchTween.isDone()) {
            myRenderer.batch.begin();
            float width = Constants.buttonRect1.width/2 + 2;
            float height = Constants.buttonRect1.height/2 + 10;
            font.draw(myRenderer.batch,
                    "Story",
                    Constants.buttonRect1.x + width,
                    Constants.buttonRect1.y + height,
                    0,
                    Align.center, false);
            font.draw(myRenderer.batch,
                    "Controls",
                    Constants.buttonRect2.x + width,
                    Constants.buttonRect2.y + height,
                    0,
                    Align.center, false);
            font.draw(myRenderer.batch,
                    "New Game",
                    Constants.buttonRect3.x + width,
                    Constants.buttonRect3.y + height,
                    0,
                    Align.center, false);
            // Text for large bottom pane
            String controlText = isAndroid ? Constants.TOUCH_CONTROLS : Constants.KEY_CONTROLS;
            font.draw(myRenderer.batch,
                    storyOrControls == 0 ? Constants.STORY : controlText,
                    Constants.GAMEPLAY_SIZE * .15f,
                    Constants.GAMEPLAY_SIZE * .4f,
                    Constants.GAMEPLAY_SIZE * .7f,
                    Align.left, true);
            myRenderer.batch.end();
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
