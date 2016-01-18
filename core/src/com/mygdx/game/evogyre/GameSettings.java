package com.mygdx.game.evogyre;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Created by Jay on 1/15/2016.
 */
public class GameSettings {
    public Preferences prefs = Gdx.app.getPreferences("evogyre.prefs");

    public boolean DRAW_RINGS() {
        return prefs.getBoolean("DRAW_RINGS", Constants.DRAW_RINGS);
    }

    public void DRAW_RINGS(boolean newVal) {
        prefs.putBoolean("DRAW_RINGS", newVal);
        prefs.flush();
    }
}
