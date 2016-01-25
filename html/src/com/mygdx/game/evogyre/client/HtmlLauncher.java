package com.mygdx.game.evogyre.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.mygdx.game.evogyre.Constants;
import com.mygdx.game.evogyre.EvoGyreGame;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(800,800);
        }

        @Override
        public ApplicationListener getApplicationListener () {
                return new EvoGyreGame();
        }
}