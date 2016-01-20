package com.mygdx.game.evogyre;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Jay on 1/13/2016.
 */
public class Actor {
    Vector2 position,
            velocity,
            acceleration;
    public float ACTOR_FRAME_RATE = 0.1f;
    public float elapsedTime = 0f;

    public Actor(float x, float y) {
        position = new Vector2(x, y);
        velocity = new Vector2(0, 0);
        acceleration = new Vector2(0, 0);
    }

    public float positionAngle() {
        return position.y;
    }

    /**
     *
     * @param delta
     * @return Distance moved along y-axis.
     */
    public float update(float delta) {
        // Update animation
        elapsedTime += delta;

        // Update and constrain velocity
        velocity.x += acceleration.x * delta;
        velocity.y += acceleration.y * delta;
        velocity.limit(Constants.MAX_VELOCITY);
        // Update and constrain position
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;
        if (position.y < 0f) position.y += Constants.MAP_SIZE_Y;
        if (position.y >= Constants.MAP_SIZE_Y) position.y -= Constants.MAP_SIZE_Y;
        // Return change in y
        return velocity.y * delta;
    }

    /**
     * Returns a unit vector of the heading (velocity).
     * @return Vector2
     */
    public Vector2 headingVector2() {
        Vector2 heading = new Vector2(velocity);
        return heading.nor();
    }

    /**
     * Returns the heading (velocity direction).
     * @return Float value for heading angle
     */
    public float heading() {
        return velocity.angle();
    }
}
