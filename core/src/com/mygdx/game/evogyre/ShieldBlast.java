package com.mygdx.game.evogyre;

/**
 * Created by Jay on 1/18/2016.
 */
public class ShieldBlast {
    float phase;
    float alpha;
    boolean isDone;

    public ShieldBlast() {
        phase = 1f;
        alpha = 0.9f;
        isDone = false;
    }

    public void update(float delta) {
        if (!isDone) {
            // TODO: add curve like createjs ease.circOut for phase
            phase -= delta;
            if (phase < 0.5f) {
                phase = 0.5f;
            }
            // TODO: add curve like createjs ease.quadOut for alpha
            alpha -= delta*2f;
            if (alpha <= 0f) {
                isDone = true;
            }
        }
    }
}
