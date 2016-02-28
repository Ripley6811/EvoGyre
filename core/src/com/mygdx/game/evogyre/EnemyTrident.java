package com.mygdx.game.evogyre;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.evogyre.Utils.DrawingUtils;

/**
 * Created by Jay on 1/22/2016.
 */
public class EnemyTrident extends Actor {
    private static final String TAG = EnemyTrident.class.getName();
    private final Animation fly_level;
    private final Animation fly_right;
    private final Animation fly_left;
    private final Animation fly_level_fire;
    private final Animation fly_right_fire;
    private final Animation fly_left_fire;

    public EnemyTrident(float x, float y, Constants.Flight_Patterns pattern, TextureAtlas atlas) {
        super(x, y);
        this.pattern = pattern;
        fly_level = animateLoop(atlas.findRegions("enemy"));
        fly_right = animateLoop(atlas.findRegions("enemyturn"));
        fly_left = animateLoop(atlas.findRegions("enemyturn"));
        for (TextureRegion each: fly_left.getKeyFrames()) each.flip(true, false);
        fly_level_fire = animateLoop(atlas.findRegions("enemyshoot"));
        fly_right_fire = animateLoop(atlas.findRegions("enemyturnshoot"));
        fly_left_fire = animateLoop(atlas.findRegions("enemyturnshoot"));
        for (TextureRegion each: fly_left_fire.getKeyFrames()) each.flip(true, false);
        weaponLevel = 1;
        collisionPolygon = Constants.TRIDENT_POLYGON;
        hitPoints = Constants.TRIDENT_HIT_POINTS;
    }

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
        float rotation = positionAngle() + screen.dspRotation;
        dspPosition = ProjectionUtils.projectPoint3D(screen, mapPosition);

        // Update collision polygon
        dspPolygon.clear();
        for (Vector2 v : collisionPolygon) {
            dspPolygon.add(new Vector2(v).rotate(rotation).scl(dspPosition.z).add(dspPosition.x, dspPosition.y));
        }


        TextureRegion texture = fly_level.getKeyFrame(elapsedTime);
        float imageLeanThreshold = 10f;
        if (justFired) {
            if (Math.abs(velocity.y) < imageLeanThreshold) {
                texture = fly_level_fire.getKeyFrame(elapsedTime);
            } else if (velocity.y > imageLeanThreshold) {
                texture = fly_right_fire.getKeyFrame(elapsedTime);
            } else if (velocity.y < -imageLeanThreshold) {
                texture = fly_left_fire.getKeyFrame(elapsedTime);
            }
            justFired = false;
        } else {
            if (velocity.y > imageLeanThreshold) {
                texture = fly_right.getKeyFrame(elapsedTime);
            } else if (velocity.y < -imageLeanThreshold) {
                texture = fly_left.getKeyFrame(elapsedTime);
            }
        }
        int pWidth = texture.getRegionWidth();
        int pHeight = texture.getRegionHeight();

        screen.batch.begin();
        screen.batch.draw(texture,
                dspPosition.x - Constants.HALF_SHIP,
                dspPosition.y - Constants.HALF_SHIP,
                Constants.HALF_SHIP, Constants.HALF_SHIP,
                pWidth, pHeight,
                1.28f* dspPosition.z, 0.8f* dspPosition.z,  // Scale
                rotation + 90f);
        screen.batch.end();


        // Show collision polygon in debug mode
        if (Constants.LOG_LEVEL == Application.LOG_DEBUG) {
            DrawingUtils.drawDebugPolygon(screen.myRenderer,
                    DrawingUtils.vectors2floats(dspPolygon));
        }
    }
}
