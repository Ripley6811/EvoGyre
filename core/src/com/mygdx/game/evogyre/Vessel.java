package com.mygdx.game.evogyre;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Jay on 1/18/2016.
 */
public class Vessel extends Actor implements Propulsion, ShieldInterface {
    private Array<Sprite> player_straight;
    private Array<Sprite> player_lean_right;
    private Array<Sprite> player_lean_left;
    private Shield shield;

    public Vessel(float x, float y, TextureAtlas atlas) {
        super(x, y);
        this.player_straight = atlas.createSprites("player");
        this.player_lean_right = atlas.createSprites("playerturn");
        this.player_lean_left = atlas.createSprites("playerturn");
        for (Sprite each: player_lean_left) each.flip(true, false);
        shield = new Shield(2f*Constants.SHIELD_RADIUS,
                2f*Constants.SHIELD_WIDTH_MULTIPLIER*Constants.SHIELD_RADIUS,
                Constants.STARTING_SHIELD_POINTS);
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

    public Vector3 render(MyShapeRenderer renderer, float delta, float mapRotation, Vector2 vanishingPoint) {
        int frame = getFrame(player_straight.size);
        TextureRegion texture = player_straight.get(frame);
        if (acceleration.y > 0f) {
            texture = player_lean_right.get(frame);
        } else if (acceleration.y < 0f) {
            texture = player_lean_left.get(frame);
        }
        int width = texture.getRegionWidth();
        int height = texture.getRegionHeight();

        Vector3 placement = ProjectionUtils.projectPoint(position, mapRotation, vanishingPoint);
        renderer.batch.begin();
        renderer.batch.draw(texture,
                placement.x - 0.5f * width,
                placement.y - 0.5f * height,
                0.5f * width, 0.5f * height,
                width, height,
                1.28f, 0.8f,  // Scale
                positionAngle() + mapRotation + 90f);
        renderer.batch.end();

        shield.render(renderer, delta, placement, positionAngle() + mapRotation);
        return placement;
    }
}
