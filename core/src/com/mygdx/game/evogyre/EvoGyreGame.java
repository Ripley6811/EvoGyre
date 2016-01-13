package com.mygdx.game.evogyre;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public class EvoGyreGame extends Game {
    TitleScreen titleScreen;
    StoryScreen storyScreen;
    GameScreen gameScreen;
    @Override
    public void create() {
        Gdx.app.setLogLevel(Constants.LOG_LEVEL);

        titleScreen = new TitleScreen(this);
        storyScreen = new StoryScreen(this);
        gameScreen = new GameScreen(this);
        setScreen(gameScreen);
    }


}
