package com.mygdx.game.evogyre;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.evogyre.Utils.DrawingUtils;

/**
 * Created by Jay on 3/2/2016.
 */
public class EnemyBoss extends Actor {
    private static final String TAG = EnemyBoss.class.getName();
    private final Animation boss_whole;
    private final Animation boss_damage;
    private final Animation boss_core;
    private final TextureRegion turret;

    public EnemyBoss(float x, float y, Constants.Flight_Patterns pattern, TextureAtlas atlas) {
        super(x, y);
        subClass = TAG;
        this.pattern = pattern;
        boss_whole = animateLoop(atlas.findRegions("boss"));
        boss_damage = animateLoop(atlas.findRegions("bossdamage"));
        boss_core = animateLoop(atlas.findRegions("bosscore"));
        turret = atlas.createSprite("turret");
        weaponLevel = 2;
        collisionPolygon = Constants.BOSS_POLYGON;
        hitPoints = Constants.BOSS_HIT_POINTS;
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


        TextureRegion texture = boss_whole.getKeyFrame(elapsedTime);
        int pWidth = texture.getRegionWidth();
        int pHeight = texture.getRegionHeight();

        screen.batch.begin();
        screen.batch.draw(texture,
                dspPosition.x - pWidth / 2,
                dspPosition.y - pHeight / 2,
                pWidth / 2, pHeight / 2,
                pWidth, pHeight,
                2.6f * dspPosition.z, 2f * dspPosition.z,  // Scale
                rotation + 90f);
        screen.batch.draw(turret,
                dspPosition.x - pWidth / 2,
                dspPosition.y - pHeight / 2,
                pWidth / 2, pHeight / 2,
                pWidth, pHeight,
                0.4f* dspPosition.z, 0.6f* dspPosition.z,  // Scale
                rotation + 90f);
        screen.batch.draw(turret,
                dspPosition.x - pWidth / 2 + 210f,
                dspPosition.y - pHeight / 2 - 40f,
                pWidth / 2 - 210f, pHeight / 2 + 40f,
                pWidth, pHeight,
                0.4f* dspPosition.z, 0.6f* dspPosition.z,  // Scale
                rotation + 95f);
        screen.batch.draw(turret,
                dspPosition.x - pWidth / 2 - 210f,
                dspPosition.y - pHeight / 2 - 40f,
                pWidth / 2 + 210f, pHeight / 2 + 40f,
                pWidth, pHeight,
                0.4f* dspPosition.z, 0.6f* dspPosition.z,  // Scale
                rotation + 85f);
        screen.batch.draw(turret,
                dspPosition.x - pWidth / 2 + 290f,
                dspPosition.y - pHeight / 2 - 100f,
                pWidth / 2 - 290f, pHeight / 2 + 100f,
                pWidth, pHeight,
                0.4f* dspPosition.z, 0.6f* dspPosition.z,  // Scale
                rotation + 115f);
        screen.batch.draw(turret,
                dspPosition.x - pWidth / 2 - 290f,
                dspPosition.y - pHeight / 2 - 100f,
                pWidth / 2 + 290f, pHeight / 2 + 100f,
                pWidth, pHeight,
                0.4f* dspPosition.z, 0.6f* dspPosition.z,  // Scale
                rotation + 65f);
        screen.batch.end();


        // Show collision polygon in debug mode
        if (Constants.LOG_LEVEL == Application.LOG_DEBUG) {
            DrawingUtils.drawDebugPolygon(screen.myRenderer,
                    DrawingUtils.vectors2floats(dspPolygon));
        }
    }
}
