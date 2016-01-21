package com.mygdx.game.evogyre;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.Animation;

/**
 * Created by Jay on 1/18/2016.
 */
public class Vessel extends Actor implements Propulsion, ShieldInterface {
    private Animation fly_level;
    private Animation fly_right;
    private Animation fly_left;
    private Animation fly_level_fire;
    private Animation fly_right_fire;
    private Animation fly_left_fire;
    private TextureRegion missile;
    private Shield shield;
    private boolean justFired = false;
    private float fireCooldown = 0f;
    public int weaponLevel = 0;
    private int nMissiles = 1;

    public Vessel(float x, float y, TextureAtlas atlas) {
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
    }

    private Animation animateLoop(Array<TextureAtlas.AtlasRegion> textures) {
        return new Animation(ACTOR_FRAME_RATE, textures, Animation.PlayMode.LOOP);
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
        if (!shield.takeDamage()) {
            // TODO: ship itself takes damage without shielding
        }
    }

    @Override
    public void recharge(int amount) {

    }

    public void addMissiles(int amount) {
        this.nMissiles += amount;
    }

    public boolean canFire() {
        return fireCooldown == 0f;
    }

    public void fire() {
        if (canFire()) {
            fireCooldown = Constants.PRIMARY_WEAPON_SETUP.get(weaponLevel).getFloat("fireRate");
            justFired = true;
        }
    }

    public Vector3 render(MyShapeRenderer renderer, float delta, float mapRotation, Vector2 vanishingPoint) {
        // Update fire cool-down
        fireCooldown = Math.max(0f, fireCooldown-delta);
        float missileRightOffset = Constants.MISSILE_RIGHT_OFFSET;
        float missileLeftOffset = Constants.MISSILE_LEFT_OFFSET;

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
        Vector3 placement = ProjectionUtils.projectPoint(position, mapRotation, vanishingPoint);

        // Draw missiles under wings
        if (nMissiles > 0) {
            int mWidth = missile.getRegionWidth();
            int mHeight = missile.getRegionHeight();
            renderer.batch.begin();
            renderer.batch.draw(missile,
                    placement.x + missileRightOffset * pWidth,
                    placement.y - 0.4f * pHeight,
                    -missileRightOffset * pWidth, 0.4f * pHeight,  // Origin for rotation, scale
                    mWidth, mHeight,
                    0.5f, 0.5f,  // Scale
                    positionAngle() + mapRotation + 90f);
            renderer.batch.draw(missile,
                    placement.x + missileLeftOffset * pWidth,
                    placement.y - 0.4f * pHeight,
                    -missileLeftOffset * pWidth, 0.4f * pHeight,  // Origin for rotation, scale
                    mWidth, mHeight,
                    0.5f, 0.5f,  // Scale
                    positionAngle() + mapRotation + 90f);
            renderer.batch.end();
        }

        renderer.batch.begin();
        renderer.batch.draw(texture,
                placement.x - 0.5f * pWidth,
                placement.y - 0.5f * pHeight,
                0.5f * pWidth, 0.5f * pHeight,
                pWidth, pHeight,
                1.28f, 0.8f,  // Scale
                positionAngle() + mapRotation + 90f);
        renderer.batch.end();

        shield.render(renderer, delta, placement, positionAngle() + mapRotation);
        return placement;
    }
}
