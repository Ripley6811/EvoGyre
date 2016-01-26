package com.mygdx.game.evogyre;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Jay on 1/13/2016.
 */
public class Actor {
    private static final String TAG = Actor.class.getName();
    Vector2 position,
            velocity,
            acceleration;
    public float ACTOR_FRAME_RATE = 0.1f;
    public float elapsedTime = 0f;
    public boolean justFired = false;
    public float fireCooldown = 0f;
    public int weaponLevel = 0;
    public boolean isDead = false;
    public boolean isEntering = true;
    public Constants.Flight_Patterns pattern;
//    public // TODO: Save ship/shield shape to Actor for collision checking

    public Actor(float x, float y) {
        position = new Vector2(x, y);
        velocity = new Vector2(0, 0);
        acceleration = new Vector2(0, 0);
        pattern = Constants.Flight_Patterns.SNAKE_SPIRAL;
    }

    public float positionAngle() {
        return position.y;
    }

    public boolean fire() {
        return false;
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
        if (position.y < 0f) position.y += Constants.MAP_SIZE_Y_360;
        if (position.y >= Constants.MAP_SIZE_Y_360) position.y -= Constants.MAP_SIZE_Y_360;
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

    public Animation animateLoop(Array<TextureAtlas.AtlasRegion> textures) {
        return new Animation(ACTOR_FRAME_RATE, textures, Animation.PlayMode.LOOP);
    }

    public void render(MyShapeRenderer renderer, float delta, float mapRotation, Vector2 vanishingPoint) {

    }
}
