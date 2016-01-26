package com.mygdx.game.evogyre;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Jay on 1/22/2016.
 */
public class EnemyBall extends Actor {
    private static final String TAG = EnemyBall.class.getName();
    private final TextureRegion large_ball;
    private final TextureRegion inner_ball;
    private float elapsedTime;

    private float[] vertices;

    public EnemyBall(float x, float y, Constants.Flight_Patterns pattern, TextureAtlas atlas) {
        super(x, y);
        this.pattern = pattern;
        large_ball = atlas.findRegion("ball_outer");
        inner_ball = atlas.findRegion("ball_inner");
        elapsedTime = 0f;
        polygon = Constants.BALLSHIP_POLYGON;
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
        float rotation = positionAngle() + mapRotation;

        TextureRegion texture = large_ball;
        int pWidth = texture.getRegionWidth();
        int pHeight = texture.getRegionHeight();
        display = ProjectionUtils.projectPoint(position, mapRotation, vanishingPoint);

        renderer.batch.begin();
        renderer.batch.draw(inner_ball,
                display.x - 0.5f * pWidth,
                display.y - 0.5f * pHeight,
                0.5f * pWidth, 0.5f * pHeight,
                pWidth, pHeight,
                1.1f * display.z, 1.1f * display.z,  // Scale
                rotation + 800 * elapsedTime);
        renderer.batch.draw(texture,
                display.x - 0.5f * pWidth,
                display.y - 0.5f * pHeight,
                0.5f * pWidth, 0.5f * pHeight,
                pWidth, pHeight,
                0.6f * display.z, 0.6f * display.z,  // Scale
                rotation + 90f);
        renderer.batch.end();


        // Show collision polygon in debug mode
        if (Constants.LOG_LEVEL == Application.LOG_DEBUG) {
            DrawingUtils.enableBlend();
            Array<Vector2> displayPolygon = new Array<Vector2>();
            for (Vector2 v: Constants.BALLSHIP_POLYGON) {
                displayPolygon.add(new Vector2(v).rotate(rotation).scl(display.z));
            }
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.setColor(Constants.COLLISION_DEBUG_COLOR);
            renderer.polygon(
                    DrawingUtils.vectors2floats(displayPolygon, display)
            );
            renderer.end();
            DrawingUtils.disableBlend();
        }
    }
}
