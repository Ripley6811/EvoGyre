package com.mygdx.game.evogyre;

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
            phase -= delta * Constants.PHASE_MULTIPLIER;
            if (phase < 0f) {
                phase = 0f;
            }
            // TODO: add curve like createjs ease.quadOut for alpha
            alpha -= delta * Constants.ALPHA_FADE_MULTIPLIER;
            if (alpha <= 0f) {
                isDone = true;
            }
        }
    }
}
