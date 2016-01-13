package com.mygdx.game.evogyre;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Jay on 1/13/2016.
 */
public class Actor {
    Vector2 position;
    Vector2 velocity;

    public Actor(float x, float y) {
        position = new Vector2(x, y);
        velocity = new Vector2(0, 0);
    }
}
