package com.mygdx.game.evogyre;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;

/**
 * Created by Jay on 1/15/2016.
 */
public class GameSettings {
    public Preferences prefs = Gdx.app.getPreferences("evogyre.prefs");

    /**
     * Saves settings and game data to persistent storage.
     * @return True if saved successfully
     */
    public boolean savePrefs() {
        // TODO: save data
        return false;
    }
}
