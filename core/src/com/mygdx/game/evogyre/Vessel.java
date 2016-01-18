package com.mygdx.game.evogyre;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Jay on 1/18/2016.
 */
public class Vessel extends Actor implements Propulsion, ShieldControls {
    public int shieldHitPoints;

    public Vessel(float x, float y) {
        super(x, y);
        shieldHitPoints = Constants.STARTING_SHIELD_POINTS;
    }

    @Override
    public void accelerate(Vector2 accelVector) {
        if (accelVector.isZero()) decelerate();
        if (velocity.hasOppositeDirection(accelVector)) velocity.scl(0f);
        acceleration.add(accelVector);
        acceleration.setLength(Constants.ACCELERATION_RATE);
    }

    @Override
    public void decelerate() {
        acceleration.scl(0f);
        velocity.scl(Constants.ACTOR_MOTION_FRICTION);
    }

    @Override
    public void damage(int amount) {
        shieldHitPoints = Math.max(shieldHitPoints - amount, 0);
    }

    @Override
    public void recharge(int amount) {
        shieldHitPoints = amount;
    }
}
