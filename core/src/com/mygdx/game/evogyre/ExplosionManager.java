package com.mygdx.game.evogyre;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Jay on 3/3/2016.
 */
public class ExplosionManager {
    private static final String TAG = ExplosionManager.class.getName();

    private Array<Explosion> explosions;
    private Animation explosionAnimation;

    public ExplosionManager(TextureAtlas atlas) {
        explosionAnimation = new Animation(0.06f, atlas.findRegions("explosion"),
                Animation.PlayMode.NORMAL);
        explosions = new Array<Explosion>();
    }


    public void add(Vector2 mapPosition) {
        explosions.add(new Explosion(mapPosition));

        Gdx.app.debug(TAG, "Explosions Count - " + explosions.size);
    }

    public void update(float delta) {
        // Dequeue finished object
        if (explosions.size > 0 && explosions.get(0).isFinished()) explosions.removeIndex(0);
        // Increment object animation timers
        for (Explosion e: explosions) e.elapsedTime += delta;
    }

    public void render(GameScreen screen) {
        Vector3 placement;
        screen.batch.begin();
        for (Explosion ex: explosions) {
            placement = ProjectionUtils.projectPoint3D(screen, ex.mapPosition);

            TextureRegion texture = explosionAnimation.getKeyFrame(ex.elapsedTime);
            int width = texture.getRegionWidth();
            int height = texture.getRegionHeight();
            screen.batch.draw(texture,
                    placement.x - 0.5f * width,
                    placement.y - 0.5f * height,
                    0.5f * width, 0.5f * height,
                    width, height,
                    placement.z, placement.z,  // Scale
                    0f);
        }
        screen.batch.end();
    }

    private class Explosion {
        float elapsedTime;
        Vector2 mapPosition;

        public Explosion(Vector2 mapPosition) {
            this.mapPosition = new Vector2(mapPosition);
            this.elapsedTime = 0f;
        }

        public boolean isFinished() {
            return explosionAnimation.isAnimationFinished(elapsedTime);
        }
    }
}
