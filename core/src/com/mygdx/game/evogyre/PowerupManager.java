package com.mygdx.game.evogyre;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Jay on 2/28/2016.
 */
public class PowerupManager {
    public void checkCollision(Array<Vessel> vessels) {

    }

    private class Powerup {
        Vector2 position;

        public void init(Vector2 position) {
            this.position = position;
        }
    }
}
