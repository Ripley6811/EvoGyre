package com.mygdx.game.evogyre;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.evogyre.Utils.DrawingUtils;

import java.util.Random;

/**
 * Created by Jay on 2/28/2016.
 */
public class PowerupManager {
    private static final String TAG = PowerupManager.class.getName();

    private float elapsedTime = 0f;
    private Array<PowerUp> powerups;
    private TextureRegion shieldTileTexture;
    private TextureRegion weaponTileTexture;
    private Random random;

    public PowerupManager(TextureAtlas atlas) {
        powerups = new Array<PowerUp>();
        shieldTileTexture = atlas.createSprite("extralife");
        weaponTileTexture = atlas.createSprite("triplelaser");
        random = new Random(Constants.POWER_UP_RANDOM_SEED);
    }

    /**
     * Checks vessel collision with powerup and increments accordingly.
     * @param vessels
     */
    public void checkCollision(GameScreen screen, Vessel ship) {
        if (powerups.size > 0) {
            for (PowerUp b : powerups) {
                if (!b.isFinished && ship.dspPolygon.size > 0) {
                    Vector2 currPos = ProjectionUtils.projectPoint2D(screen, b.position);
                    Vector2 lastPos = ProjectionUtils.projectPoint2D(screen, b.lastPosition);
                    Polygon polygon = new Polygon(DrawingUtils.vectors2floats(ship.dspPolygon));
                    // `intersectSegmentPolygon` prevents point skipping over polygon
                    if (Intersector.intersectSegmentPolygon(currPos, lastPos, polygon)) {
                        if (b.type.equals(Constants.PowerUp_Types.SHIELD)) {
                            ship.recharge(1);
                        } else {
                            ship.incrementWeaponLevel();
                        }
                        b.isFinished = true;
                    }
                }
            }
        }
    }

    public void update(float delta) {
        elapsedTime += delta;
        if (elapsedTime > Constants.POWER_UP_INTERVAL) {
            elapsedTime -= Constants.POWER_UP_INTERVAL;
            powerups.add(new PowerUp(0, random.nextFloat() * 360f,
                    random.nextBoolean() ?
                            Constants.PowerUp_Types.SHIELD : Constants.PowerUp_Types.WEAPON));
        }
        for (PowerUp p: powerups) {
            p.lastPosition.set(p.position);
            p.position.x += delta * Constants.POWER_UP_VELOCITY;
        }
        if (powerups.size > 0) {
            PowerUp p = powerups.get(0);
            if (p.position.x > Constants.MAP_SIZE_X || p.isFinished) {
                powerups.removeIndex(0);
            }
        }
    }

    private class PowerUp {
        Vector2 position;
        Vector2 lastPosition;
        Constants.PowerUp_Types type;
        boolean isFinished;

        public PowerUp(float x, float y, Constants.PowerUp_Types type) {
            this.position = new Vector2(x, y);
            this.lastPosition = new Vector2(x, y);
            this.type = type;
            this.isFinished = false;
        }
    }

    public void render(GameScreen screen) {
        screen.batch.begin();
        for (PowerUp powerUp: powerups) {
            Vector3 dspPosition = ProjectionUtils.projectPoint3D(screen, powerUp.position);
            TextureRegion texture = powerUp.type.equals(Constants.PowerUp_Types.SHIELD) ?
                    shieldTileTexture : weaponTileTexture;
            float width = texture.getRegionWidth();
            float height = texture.getRegionHeight();
            screen.batch.draw(texture,
                    dspPosition.x - 0.5f * width,
                    dspPosition.y - 0.5f * height,
                    0.5f * width, 0.5f * height,  // Origin for rotation, scale
                    texture.getRegionWidth(), texture.getRegionHeight(),
                    dspPosition.z, dspPosition.z,  // Scale
                    screen.dspRotation + powerUp.position.y + 90f);
        }
        screen.batch.end();
    }
}
