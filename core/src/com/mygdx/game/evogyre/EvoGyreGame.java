package com.mygdx.game.evogyre;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class EvoGyreGame extends Game {

    AssetManager assets;
    GameSettings settings;

    TitleScreen titleScreen;
    SettingsScreen settingsScreen;
    StoryScreen storyScreen;
    GameScreen gameScreen;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Constants.LOG_LEVEL);
        assets = new AssetManager();
        assets.load(Constants.MAIN_ATLAS, TextureAtlas.class);
        assets.finishLoading();
        settings = new GameSettings();

        titleScreen = new TitleScreen(this);
        settingsScreen = new SettingsScreen(this);
        storyScreen = new StoryScreen(this);
        gameScreen = new GameScreen(this);
        setScreen(gameScreen);
    }


}
