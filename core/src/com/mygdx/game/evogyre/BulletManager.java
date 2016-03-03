package com.mygdx.game.evogyre;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.math.Polygon;
import com.mygdx.game.evogyre.Utils.DrawingUtils;

/**
 * Created by Jay on 1/20/2016.
 */
public class BulletManager {
    private static final String TAG = BulletManager.class.getName();
    private Array<TextureRegion> bulletTextures;
    private JsonValue weaponSet;

    private Array<Bullet> bullets;

    public BulletManager(TextureAtlas atlas, JsonValue weaponSetup) {
        bulletTextures = new Array<TextureRegion>();
        weaponSet = weaponSetup;
        for (JsonValue setup: weaponSet) {
            bulletTextures.add(atlas.findRegion(setup.getString("texture")));
        }
        bullets = new Array<Bullet>();
    }

    public void add(int index, float x, float y) {
        JsonValue setup = weaponSet.get(index);
        for (int i=0; i<setup.get("positionOffset").size; i++) {
            JsonValue offset = setup.get("positionOffset").get(i);
            JsonValue heading = setup.get("heading").get(i);
            bullets.add(new Bullet(index,
                    new Vector2(x + offset.getFloat("x"), y + offset.getFloat("y")),
                    new Vector2(heading.getFloat("x"), heading.getFloat("y")).setLength(setup.getFloat("velocity")),
                    setup.getString("texture").startsWith("round")
            ));
        }
    }

    public int weaponsCount() {
        return bulletTextures.size;
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
            // Keep bullets on game map
            if (bullet.position.y < 0f) bullet.position.y += 360f;
            if (bullet.position.y >= 360f) bullet.position.y -= 360f;
            // Kill off distant bullets. Don't let them run to end of game map
            if (bullet.position.x <= Constants.BULLET_CUTOFF) bullet.isFinished = true;
            if (bullet.position.x > Constants.MAP_SIZE_X + Constants.RING_INTERVAL) bullet.isFinished = true;
        }
    }

    public void render(GameScreen screen) {
        Vector3 placement;
        Vector2 placement2;  // Z-depth not needed
        screen.batch.begin();
        for (Bullet b: bullets) {
            if (b.isFinished) continue;
            placement = ProjectionUtils.projectPoint3D(screen, b.position);
            placement2 = ProjectionUtils.projectPoint2D(screen, b.lastPosition);
            float angle = new Vector2(placement.x-placement2.x, placement.y-placement2.y).angle();
            TextureRegion texture = bulletTextures.get(b.type);
            int width = texture.getRegionWidth();
            int height = texture.getRegionHeight();
            screen.batch.draw(texture,
                    placement.x - 0.5f * width,
                    placement.y - 0.5f * height,
                    0.5f * width, 0.5f * height,
                    width, height,
                    placement.z * (b.isRound ? 0.8f : 0.5f),
                    placement.z * (b.isRound ? 0.8f : placement.z),  // Scale
                    angle + 90f);
        }
        screen.batch.end();
    }

    private class Bullet {
        int type;
        Vector2 position;
        Vector2 velocity;
        Vector2 lastPosition;  // For aligning bullet image
        boolean isFinished = false;
        boolean isRound;

        public Bullet(int type, Vector2 position, Vector2 velocity, boolean isRound) {
            this.type = type;
            this.position = new Vector2(position);
            this.velocity = new Vector2(velocity);
            this.lastPosition = new Vector2(position);
            this.isRound = isRound;
        }
    }

    public int checkForCollisions(GameScreen screen, Actor ship) {
        int hits = 0;
        if (bullets.size > 0) {
            for (Bullet b : bullets) {
                if (!b.isFinished && ship.dspPolygon.size > 0) {
                    Vector2 currPos = ProjectionUtils.projectPoint2D(screen, b.position);
                    Vector2 lastPos = ProjectionUtils.projectPoint2D(screen, b.lastPosition);
                    Polygon polygon = new Polygon(DrawingUtils.vectors2floats(ship.dspPolygon));
                    // `intersectSegmentPolygon` prevents bullet skipping over enemy
                    if (Intersector.intersectSegmentPolygon(currPos, lastPos, polygon)) {
                        b.isFinished = true;
                        ship.damage(1);
                        hits += 1;
                        if (ship.subClass.contains("Vessel")) {
                            ship.weaponLevel = Math.max(ship.weaponLevel - 1, 0);
                        }
                    }
                }
            }
        }
        return hits;
    }
}
