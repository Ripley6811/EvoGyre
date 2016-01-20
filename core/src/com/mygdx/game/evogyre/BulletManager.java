package com.mygdx.game.evogyre;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Jay on 1/20/2016.
 */
public class BulletManager {
    private Array<TextureRegion> bulletTextures;

    Array<Bullet> bullets;

    public BulletManager(TextureAtlas atlas) {
        bulletTextures = new Array<TextureRegion>();
        for (Constants.BulletType type: Constants.BulletType.values()) {
            bulletTextures.add(atlas.findRegion(type.textureName));
        }
        bullets = new Array<Bullet>();
        // TODO: Delete this
        System.out.println(Constants.BulletType.values()[1]);
    }

    public void add(int type, float x, float y, float vx, float vy) {
        bullets.add(new Bullet(Constants.BulletType.values()[type],
                new Vector2(x,y),
                new Vector2(vx, vy).setLength(Constants.BulletType.values()[type].velocity)));
    }

    public void update(float delta) {
        // Remove finished bullets from array
        while (bullets.size > 0 && bullets.first().isFinished) {
            bullets.removeIndex(0);
        }
        // Update positions
        for (Bullet bullet: bullets) {
            bullet.lastPosition.set(bullet.position);
            bullet.position.x += delta * bullet.velocity.x;
            bullet.position.y += delta * bullet.velocity.y;
            if (bullet.position.x <= Constants.BULLET_CUTOFF) bullet.isFinished = true;
        }
    }

    public void render(MyShapeRenderer renderer, float mapRotation, Vector2 vanishingPoint) {
        Vector3 placement;
        Vector3 placement2;
        renderer.batch.begin();
        for (Bullet b: bullets) {
            placement = ProjectionUtils.projectPoint(b.position, mapRotation, vanishingPoint);
            placement2 = ProjectionUtils.projectPoint(b.lastPosition, mapRotation, vanishingPoint);
            float angle = new Vector2(placement.x-placement2.x, placement.y-placement2.y).angle();
            TextureRegion texture = bulletTextures.get(b.type.ordinal());
            int width = texture.getRegionWidth();
            int height = texture.getRegionHeight();
            renderer.batch.draw(texture,
                    placement.x - 0.5f * width,
                    placement.y - 0.5f * height,
                    0.5f * width, 0.5f * height,
                    width, height,
                    placement.z/2f, placement.z*placement.z,  // Scale
                    angle + 90f);
        }
        renderer.batch.end();
    }

    private class Bullet {
        Constants.BulletType type;
        Vector2 position;
        Vector2 velocity;
        Vector2 lastPosition;  // For aligning bullet image
        boolean isFinished = false;

        public Bullet(Constants.BulletType type, Vector2 position, Vector2 velocity) {
            this.type = type;
            this.position = new Vector2(position);
            this.velocity = new Vector2(velocity);
            this.lastPosition = new Vector2(position);
        }
    }
}
