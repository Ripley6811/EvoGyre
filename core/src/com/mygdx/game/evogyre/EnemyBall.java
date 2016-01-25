package com.mygdx.game.evogyre;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Jay on 1/22/2016.
 */
public class EnemyBall extends Actor {
    private final TextureRegion large_ball;
    private final TextureRegion inner_ball;
    private float elapsedTime;

    public EnemyBall(float x, float y, Constants.Flight_Patterns pattern, TextureAtlas atlas) {
        super(x, y);
        this.pattern = pattern;
        large_ball = atlas.findRegion("ball_outer");
        inner_ball = atlas.findRegion("ball_inner");
        elapsedTime = 0f;
    }

    public void damage(int amount) {

    }

    public void recharge(int amount) {

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
    public void render(MyShapeRenderer renderer, float delta, float mapRotation, Vector2 vanishingPoint) {
        // Update fire cool-down
        fireCooldown = Math.max(0f, fireCooldown-delta);
        elapsedTime += delta;

        TextureRegion texture = large_ball;
        int pWidth = texture.getRegionWidth();
        int pHeight = texture.getRegionHeight();
        Vector3 placement = ProjectionUtils.projectPoint(position, mapRotation, vanishingPoint);

        renderer.batch.begin();
        renderer.batch.draw(inner_ball,
                placement.x - 0.5f * pWidth,
                placement.y - 0.5f * pHeight,
                0.5f * pWidth, 0.5f * pHeight,
                pWidth, pHeight,
                1.1f * placement.z, 1.1f * placement.z,  // Scale
                positionAngle() + mapRotation + 800 * elapsedTime);
        renderer.batch.draw(texture,
                placement.x - 0.5f * pWidth,
                placement.y - 0.5f * pHeight,
                0.5f * pWidth, 0.5f * pHeight,
                pWidth, pHeight,
                0.6f * placement.z, 0.6f * placement.z,  // Scale
                positionAngle() + mapRotation + 90f);
        renderer.batch.end();
    }
}
