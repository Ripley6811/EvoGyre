package com.mygdx.game.evogyre;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector2;

/**
 * Loads and saves game settings and data.
 *
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

    public boolean FIXED_VESSEL() {
        return prefs.getBoolean("FIXED_VESSEL", Constants.FIXED_VESSEL);
    }

    public void FIXED_VESSEL(boolean newVal) {
        prefs.putBoolean("FIXED_VESSEL", newVal);
        prefs.flush();
    }
}
