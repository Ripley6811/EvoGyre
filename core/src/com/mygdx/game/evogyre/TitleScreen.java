package com.mygdx.game.evogyre;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.game.evogyre.Utils.DrawingUtils;
import com.mygdx.game.evogyre.Utils.Tween;

/**
 * Created by Jay on 1/12/2016.
 */
public class TitleScreen extends InputAdapter implements Screen {
    private static final String TAG = TitleScreen.class.getName();
    EvoGyreGame game;
    FitViewport actionViewport;
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

    public TitleScreen(EvoGyreGame game) {
        Gdx.input.setCatchBackKey(true);
        this.game = game;
        actionViewport = new FitViewport(Constants.DISPLAY_SIZE, Constants.DISPLAY_SIZE);
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
                Constants.DISPLAY_SIZE*.8f,
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
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
        bluePatchTween = new Tween.QuadInOut(
                bluePatch.getTotalWidth(),
                Constants.DISPLAY_SIZE*.8f,
                Constants.PANEL_TWEEN_TIME
        );
        blueButtonTween = new Tween.QuadInOut(
                bluePatch.getTotalWidth(),
                Constants.buttonRect1.width,
                Constants.PANEL_TWEEN_TIME
        );
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
        VisualEffects.drawStars(myRenderer, 0f, new Vector2(0, 0));

        /** DRAW PLANET **/
        myRenderer.batch.begin();
        myRenderer.batch.draw(planet,
                Constants.DISPLAY_SIZE * .25f - planet.getRegionWidth() / 2,
                Constants.DISPLAY_SIZE * .75f - planet.getRegionHeight() / 2,
                planet.getRegionWidth() / 2, planet.getRegionWidth() / 2,
                planet.getRegionWidth(), planet.getRegionHeight(),
                1.2f, 1.2f, 0);
        myRenderer.batch.end();

        /** DRAW TITLE */
        myRenderer.batch.begin();
        myRenderer.batch.draw(
                titleText,
                Constants.DISPLAY_SIZE * .01f,
                Constants.DISPLAY_SIZE * .65f,
                titleText.getRegionWidth(), titleText.getRegionHeight(),
                titleText.getRegionWidth(), titleText.getRegionHeight(),
                0.8f, 0.8f,
                0f
        );
        myRenderer.batch.end();

        /** DRAW NINEPATCH PANE */
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
                Constants.DISPLAY_SIZE*.1f,
                Constants.DISPLAY_SIZE * .05f,
                bluePatchWidth,
                220
        );
//        bluePatchWidth = Math.min(bluePatchWidth+800*delta, Constants.DISPLAY_SIZE*.8f);
        myRenderer.batch.end();

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
            font.draw(myRenderer.batch,
                    storyOrControls == 0 ? Constants.STORY : Constants.CONTROLS,
                    Constants.DISPLAY_SIZE * .15f,
                    Constants.DISPLAY_SIZE * .4f,
                    Constants.DISPLAY_SIZE * .7f,
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
