package com.mygdx.game.evogyre;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Jay on 1/19/2016.
 */
public class Shield {
    private static final String TAG = Shield.class.getName();
    private int hitPoints;
    private float width;
    private float height;
    private Array<ShieldBlast> effects;
    private Texture texture;
    private int maxHitPoints;

    public Shield(float width, float height, int hitPoints) {
        this.width = width;
        this.height = height;
        this.hitPoints = hitPoints;
        this.maxHitPoints = hitPoints;
        this.texture = createTexture();
        this.effects = new Array<ShieldBlast>();
    }

    public int getHitPoints() {
        return hitPoints;
    }

    /**
     * Attempts to reduce shield points.
     * @return Remaining damage after shield absorption.
     */
    public int takeDamage(int amount) {
        while (hitPoints > 0 && amount > 0) {
            hitPoints -= 1;
            amount -= 1;
            effects.add(new ShieldBlast());
        }

        // Remove finished effects
        for (int i=effects.size-1; i>=0; i--) {
            if (effects.get(i).isDone) effects.removeIndex(i);
        }

        return amount;
    }

    public void recharge(int amount) {
        hitPoints = Math.min(hitPoints + amount, maxHitPoints);
    }

    public void render(MyShapeRenderer myRenderer, float delta, Vector3 center, float angle) {
        if (hitPoints > 0) {
            myRenderer.batch.begin();
            myRenderer.batch.draw(texture,
                    center.x - 0.5f * width,
                    center.y - 0.5f * height,
                    0.5f * width, 0.5f * height,
                    width, height,
                    1f, 1f,
                    angle,
                    0, 0,  // texel space coordinate (offset image within drawing area)
                    texture.getWidth(), texture.getHeight(),  // texel
                    false, false);
            myRenderer.batch.end();
        }

        for (ShieldBlast effect: effects) {
            if (!effect.isDone) {
                // TODO: Bring the following method into this class (?)
                VisualEffects.shieldGradientEffect(myRenderer, center, true, effect.phase, effect.alpha);
            }
            effect.update(delta);
        }
    }

    /**
     * Creates the texture for the shield. Call once during initialization.
     * @return Shield texture image
     */
    private Texture createTexture() {
        int rasterSize = 256;
        int radius = rasterSize/2;
        float FADE_RATE = 0.95f;
        Pixmap pixmap = new Pixmap(rasterSize, rasterSize, Pixmap.Format.RGBA8888);
        Color shieldColor = new Color(0.5f,0.5f,1f,0.5f);
        pixmap.setColor(shieldColor);
        for (int i=radius; i>0; i--) {
            // Reduce alpha going inward.
            shieldColor.a = FADE_RATE*shieldColor.a;
            pixmap.setColor(shieldColor);
            pixmap.drawCircle(radius, radius, i);
        }
        return new Texture(pixmap);
    }

    /**
     * Class for producing a shield hit effect. Maintains its own variables for
     * the tweening effect. Multiple instances can overlap in the dspPosition.
     */
    private class ShieldBlast {
        float phase;  // Gradient offset
        float alpha;  // Transparency
        boolean isDone;

        public ShieldBlast() {
            phase = Constants.PHASE_MAX;
            alpha = Constants.ALPHA_FADE_MAX;
            isDone = false;
        }

        public void update(float delta) {
            if (!isDone) {
                phase = Math.max(0f, phase - delta * Constants.PHASE_MULTIPLIER);
                alpha = Math.max(0f, alpha - delta * Constants.ALPHA_FADE_MULTIPLIER);
                if (alpha == 0f) {
                    isDone = true;
                }
            }
        }
    }
}
