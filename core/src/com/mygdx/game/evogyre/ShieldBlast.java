package com.mygdx.game.evogyre;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by Jay on 1/18/2016.
 */
public class ShieldBlast {
    float phase;
    float alpha;
    boolean isDone;

    public ShieldBlast() {
        phase = Constants.PHASE_MAX;
        alpha = Constants.ALPHA_FADE_MAX;
        isDone = false;
    }

    public void update(float delta) {
        if (!isDone) {
            // TODO: add curve like createjs ease.circOut for phase
            phase = Math.max(0f, phase - delta * Constants.PHASE_MULTIPLIER);
            // TODO: add curve like createjs ease.quadOut for alpha
            alpha = Math.max(0f, alpha - delta * Constants.ALPHA_FADE_MULTIPLIER);
            if (alpha == 0f) {
                isDone = true;
            }
        }
    }
}
