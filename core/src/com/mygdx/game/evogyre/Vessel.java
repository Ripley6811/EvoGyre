package com.mygdx.game.evogyre;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.mygdx.game.evogyre.Utils.DrawingUtils;

/**
 * Created by Jay on 1/18/2016.
 */
public class Vessel extends Actor implements Propulsion, ShieldInterface {
    private static final String TAG = Vessel.class.getName();
    private final Animation fly_level;
    private final Animation fly_right;
    private final Animation fly_left;
    private final Animation fly_level_fire;
    private final Animation fly_right_fire;
    private final Animation fly_left_fire;
    private final TextureRegion missile;
    private final Shield shield;
    private int nMissiles = 1;
    private int maxWeaponLevel;

    public Vessel(float x, float y, TextureAtlas atlas, int maxWeaponLevel) {
        super(x, y);
        fly_level = animateLoop(atlas.findRegions("player"));
        fly_right = animateLoop(atlas.findRegions("playerturn"));
        fly_left = animateLoop(atlas.findRegions("playerturn"));
        for (TextureRegion each: fly_left.getKeyFrames()) each.flip(true, false);
        fly_level_fire = animateLoop(atlas.findRegions("playershoot"));
        fly_right_fire = animateLoop(atlas.findRegions("playerturnshoot"));
        fly_left_fire = animateLoop(atlas.findRegions("playerturnshoot"));
        for (TextureRegion each: fly_left_fire.getKeyFrames()) each.flip(true, false);
        missile = atlas.createSprite("missile");
        shield = new Shield(2f*Constants.SHIELD_RADIUS,
                2f*Constants.SHIELD_WIDTH_MULTIPLIER*Constants.SHIELD_RADIUS,
                Constants.STARTING_SHIELD_POINTS);
        collisionPolygon = Constants.VESSEL_POLYGON;
        hitPoints = Constants.VESSEL_HIT_POINTS;
        this.maxWeaponLevel = maxWeaponLevel;
    }

    public int getShieldHitPoints() {
        return shield.getHitPoints();
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
        super.damage(shield.takeDamage(amount));
    }

    @Override
    public void recharge(int amount) {
        shield.recharge(amount);
    }

    public void incrementWeaponLevel() {
        weaponLevel = Math.min(weaponLevel + 1, maxWeaponLevel);
    }

    public void addMissiles(int amount) {
        this.nMissiles += amount;
    }

    /**
     * Returns true if after firing cool down cycle is initialized or false if
     * still in cool down from last firing.
     * @return Boolean of weapon fire success.
     */
    @Override
    public boolean fire() {
        if (fireCooldown == 0f) {
            fireCooldown = Constants.PRIMARY_WEAPON_SETUP.get(weaponLevel).getFloat("fireRate");
            justFired = true;
            return true;
        }
        return false;
    }

    @Override
    public void render(GameScreen screen, float delta) {
        // Update fire cool-down
        fireCooldown = Math.max(0f, fireCooldown-delta);
        float missileRightOffset = Constants.MISSILE_RIGHT_OFFSET;
        float missileLeftOffset = Constants.MISSILE_LEFT_OFFSET;
        float rotation = positionAngle() + screen.dspRotation + 180f;
        dspPosition = ProjectionUtils.projectPoint3D(screen, mapPosition);

        dspPolygon.clear();
        for (Vector2 v : collisionPolygon) {
            dspPolygon.add(new Vector2(v).rotate(rotation).scl(dspPosition.z).add(dspPosition.x, dspPosition.y));
        }

        TextureRegion texture = fly_level.getKeyFrame(elapsedTime);
        if (justFired) {
            if (acceleration.y == 0f) {
                texture = fly_level_fire.getKeyFrame(elapsedTime);
            } else if (acceleration.y > 0f) {
                texture = fly_right_fire.getKeyFrame(elapsedTime);
                missileRightOffset = Constants.MISSILE_RIGHT_LEAN_RIGHT;
                missileLeftOffset = Constants.MISSILE_LEFT_LEAN_RIGHT;
            } else if (acceleration.y < 0f) {
                texture = fly_left_fire.getKeyFrame(elapsedTime);
                missileRightOffset = Constants.MISSILE_RIGHT_LEAN_LEFT;
                missileLeftOffset = Constants.MISSILE_LEFT_LEAN_LEFT;
            }
            justFired = false;
        } else {
            if (acceleration.y > 0f) {
                texture = fly_right.getKeyFrame(elapsedTime);
                missileRightOffset = Constants.MISSILE_RIGHT_LEAN_RIGHT;
                missileLeftOffset = Constants.MISSILE_LEFT_LEAN_RIGHT;
            } else if (acceleration.y < 0f) {
                texture = fly_left.getKeyFrame(elapsedTime);
                missileRightOffset = Constants.MISSILE_RIGHT_LEAN_LEFT;
                missileLeftOffset = Constants.MISSILE_LEFT_LEAN_LEFT;
            }
        }
        int pWidth = texture.getRegionWidth();
        int pHeight = texture.getRegionHeight();

        // Draw missiles under wings
        if (nMissiles > 0) {
            int mWidth = missile.getRegionWidth();
            int mHeight = missile.getRegionHeight();
            screen.batch.begin();
            screen.batch.draw(missile,
                    dspPosition.x + missileRightOffset * pWidth,
                    dspPosition.y - 0.4f * pHeight,
                    -missileRightOffset * pWidth, 0.4f * pHeight,  // Origin for rotation, scale
                    mWidth, mHeight,
                    0.5f, 0.5f,  // Scale
                    rotation - 90f);
            screen.batch.draw(missile,
                    dspPosition.x + missileLeftOffset * pWidth,
                    dspPosition.y - 0.4f * pHeight,
                    -missileLeftOffset * pWidth, 0.4f * pHeight,  // Origin for rotation, scale
                    mWidth, mHeight,
                    0.5f, 0.5f,  // Scale
                    rotation - 90f);
            screen.batch.end();
        }

        // Draw vessel
        screen.batch.begin();
        screen.batch.draw(texture,
                dspPosition.x - Constants.HALF_SHIP,
                dspPosition.y - Constants.HALF_SHIP,
                Constants.HALF_SHIP, Constants.HALF_SHIP,
                pWidth, pHeight,
                1.28f, 0.8f,  // Scale
                rotation - 90f);
        screen.batch.end();

        // Show collision collisionPolygon in debug mode
        if (Constants.LOG_LEVEL == Application.LOG_DEBUG) {
            DrawingUtils.drawDebugPolygon(screen.myRenderer,
                    DrawingUtils.vectors2floats(dspPolygon));
        }

        shield.render(screen.myRenderer, delta, dspPosition, rotation + 180f);
    }
}
