package com.mygdx.game.evogyre;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Jay on 1/13/2016.
 */
public class Actor implements Propulsion {
    Vector2 position,
            velocity,
            acceleration;

    public Actor(float x, float y) {
        position = new Vector2(x, y);
        velocity = new Vector2(0, 0);
        acceleration = new Vector2(0, 0);
    }

    public float positionAngle() {
        return position.y;
    }

    @Override
    public void accelerate(Vector2 accelVector) {
        if (accelVector.isZero()) decelerate();
        if (velocity.hasOppositeDirection(accelVector)) velocity.scl(0f);
        acceleration.add(accelVector);
        acceleration.setLength(Constants.ACCELERATION_RATE);
    }

    /**
     *
     * @param delta
     * @return Distance moved along y-axis.
     */
    public float update(float delta) {
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

    @Override
    public void decelerate() {
        acceleration.scl(0f);
        velocity.scl(Constants.ACTOR_MOTION_FRICTION);
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
