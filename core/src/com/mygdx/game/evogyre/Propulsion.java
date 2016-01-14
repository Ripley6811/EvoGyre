package com.mygdx.game.evogyre;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Jay on 1/14/2016.
 */
public interface Propulsion {
    void accelerate(Vector2 vector);

    void decelerate();
}
