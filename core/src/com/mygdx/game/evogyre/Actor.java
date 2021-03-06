package com.mygdx.game.evogyre;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Jay on 1/13/2016.
 */
public class Actor {
    private static final String TAG = Actor.class.getName();
    public String subClass = "";
    Vector2 mapPosition,
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
    public Vector3 dspPosition;
    public Array<Vector2> collisionPolygon;  // Non-transposed final collision shape
    public Array<Vector2> dspPolygon;  // Transposed shape for collision calculations
    public int damage = 0;
    public int hitPoints;

    public Actor(float x, float y) {
        mapPosition = new Vector2(x, y);
        velocity = new Vector2(0, 0);
        acceleration = new Vector2(0, 0);
        pattern = Constants.Flight_Patterns.SNAKE_SPIRAL;
        dspPolygon = new Array<Vector2>();
    }

    public float positionAngle() {
        return mapPosition.y;
    }

    public boolean fire() {
        return false;
    }

    /**
     *
     * @param delta
     * @return Distance moved along y-axis.
     */
    public float update(GameScreen screen, float delta) {
        float volume = 0.1f + (float) Math.pow(this.mapPosition.x / Constants.MAP_SIZE_X, 2);
        // Update hit points and damage
        if (damage > 0) {
            hitPoints -= damage;
            damage = 0;
            AudioAssets.BULLET_FWAP.play(volume * 0.8f);
        }
        if (!isDead && hitPoints <= 0) {
            isDead = true;
            AudioAssets.SMALL_EXPLOSION.play(volume);
            screen.explosionManager.add(mapPosition);
            return 0f;
        }

        // Update animation
        elapsedTime += delta;

        // Update and constrain velocity
        velocity.x += acceleration.x * delta;
        velocity.y += acceleration.y * delta;
        velocity.limit(Constants.MAX_VELOCITY);
        // Update and constrain map mapPosition
        mapPosition.x += velocity.x * delta;
        mapPosition.y += velocity.y * delta;
        if (mapPosition.y < 0f) mapPosition.y += Constants.MAP_SIZE_Y_360;
        if (mapPosition.y >= Constants.MAP_SIZE_Y_360) mapPosition.y -= Constants.MAP_SIZE_Y_360;
        // Return change in y
        return velocity.y * delta;
    }

    public void damage(int amount) {
        damage += amount;
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

    public void render(GameScreen screen, float delta) {

    }
}
