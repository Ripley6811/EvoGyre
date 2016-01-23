package com.mygdx.game.evogyre;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Jay on 1/22/2016.
 */
public class Enemy extends Actor {
    private final Animation fly_level;
    private final Animation fly_right;
    private final Animation fly_left;
    private final Animation fly_level_fire;
    private final Animation fly_right_fire;
    private final Animation fly_left_fire;
    private boolean justFired = false;
    private float fireCooldown = 0f;
    public int weaponLevel = 0;
    public boolean isDead = false;
    public final int type;
    public final Constants.Flight_Patterns pattern;

    public Enemy(int type, float x, float y, Constants.Flight_Patterns pattern, TextureAtlas atlas) {
        super(x, y);
        this.type = type;
        this.pattern = pattern;
        fly_level = animateLoop(atlas.findRegions("enemy"));
        fly_right = animateLoop(atlas.findRegions("enemyturn"));
        fly_left = animateLoop(atlas.findRegions("enemyturn"));
        for (TextureRegion each: fly_left.getKeyFrames()) each.flip(true, false);
        fly_level_fire = animateLoop(atlas.findRegions("enemyshoot"));
        fly_right_fire = animateLoop(atlas.findRegions("enemyturnshoot"));
        fly_left_fire = animateLoop(atlas.findRegions("enemyturnshoot"));
        for (TextureRegion each: fly_left_fire.getKeyFrames()) each.flip(true, false);
    }

    public void damage(int amount) {
        // TODO: ship itself takes damage without shielding
    }

    public void recharge(int amount) {

    }

    public boolean fire() {
        if (fireCooldown == 0f) {
            fireCooldown = Constants.PRIMARY_WEAPON_SETUP.get(weaponLevel).getFloat("fireRate");
            justFired = true;
            return true;
        }
        return false;
    }

    public void render(MyShapeRenderer renderer, float delta, float mapRotation, Vector2 vanishingPoint) {
        // Update fire cool-down
        fireCooldown = Math.max(0f, fireCooldown-delta);

        TextureRegion texture = fly_level.getKeyFrame(elapsedTime);
        if (justFired) {
            if (acceleration.y == 0f) {
                texture = fly_level_fire.getKeyFrame(elapsedTime);
            } else if (acceleration.y > 0f) {
                texture = fly_right_fire.getKeyFrame(elapsedTime);
            } else if (acceleration.y < 0f) {
                texture = fly_left_fire.getKeyFrame(elapsedTime);
            }
            justFired = false;
        } else {
            if (acceleration.y > 0f) {
                texture = fly_right.getKeyFrame(elapsedTime);
            } else if (acceleration.y < 0f) {
                texture = fly_left.getKeyFrame(elapsedTime);
            }
        }
        int pWidth = texture.getRegionWidth();
        int pHeight = texture.getRegionHeight();
        Vector3 placement = ProjectionUtils.projectPoint(position, mapRotation, vanishingPoint);

        renderer.batch.begin();
        renderer.batch.draw(texture,
                placement.x - 0.5f * pWidth,
                placement.y - 0.5f * pHeight,
                0.5f * pWidth, 0.5f * pHeight,
                pWidth, pHeight,
                1.28f*placement.z, 0.8f*placement.z,  // Scale
                positionAngle() + mapRotation + 90f);
        renderer.batch.end();
    }
}
