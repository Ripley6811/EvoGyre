package com.mygdx.game.evogyre;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.evogyre.Utils.DrawingUtils;

/**
 * Created by Jay on 1/22/2016.
 */
public class EnemyBallship extends Actor {
    private static final String TAG = EnemyBallship.class.getName();
    private final TextureRegion large_ball;
    private final TextureRegion inner_ball;
    private float elapsedTime;

    private float[] vertices;

    public EnemyBallship(float x, float y, Constants.Flight_Patterns pattern, TextureAtlas atlas) {
        super(x, y);
        this.pattern = pattern;
        large_ball = atlas.findRegion("ball_outer");
        inner_ball = atlas.findRegion("ball_inner");
        elapsedTime = 0f;
        collisionPolygon = Constants.BALLSHIP_POLYGON;
        hitPoints = Constants.BALLSHIP_HIT_POINTS;
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
        elapsedTime += delta;
        float rotation = positionAngle() + screen.dspRotation;
        dspPosition = ProjectionUtils.projectPoint3D(screen, mapPosition);

        // Update collision polygon
        dspPolygon.clear();
        for (Vector2 v : collisionPolygon) {
            dspPolygon.add(new Vector2(v).rotate(rotation).scl(dspPosition.z).add(dspPosition.x, dspPosition.y));
        }

        TextureRegion texture = large_ball;
        int pWidth = texture.getRegionWidth();
        int pHeight = texture.getRegionHeight();

        screen.batch.begin();
        screen.batch.draw(inner_ball,
                dspPosition.x - Constants.HALF_BALLSHIP,
                dspPosition.y - Constants.HALF_BALLSHIP,
                0.5f * pWidth, Constants.HALF_BALLSHIP,
                pWidth, pHeight,
                1.1f * dspPosition.z, 1.1f * dspPosition.z,  // Scale
                rotation + 800 * elapsedTime);
        screen.batch.draw(texture,
                dspPosition.x - Constants.HALF_BALLSHIP,
                dspPosition.y - Constants.HALF_BALLSHIP,
                Constants.HALF_BALLSHIP, Constants.HALF_BALLSHIP,
                pWidth, pHeight,
                0.6f * dspPosition.z, 0.6f * dspPosition.z,  // Scale
                rotation + 90f);
        screen.batch.end();


        // Show collision collisionPolygon in debug mode
        if (Constants.LOG_LEVEL == Application.LOG_DEBUG) {
            DrawingUtils.drawDebugPolygon(screen.myRenderer,
                    DrawingUtils.vectors2floats(dspPolygon));
        }
    }
}
