package com.mygdx.game.evogyre.Utils;

/**
 * Created by Jay on 1/18/2016.
 *
 * Tween equations taken from "http://gizma.com/easing/" by Robert Penner.
 */
public class Tween {
    private static final String TAG = Tween.class.getName();

    public static class SineOut {
        private float timePassed;
        private float startValue;
        private float endValue;
        private float changeValue;
        private float duration;

        /**
         * Constructor for SineOut tween equation.
         * @param b Start value
         * @param c Finish value
         * @param d Duration of tween
         */
        public SineOut(float b, float c, float d) {
            timePassed = 0f;
            startValue = b;
            endValue = c;
            changeValue = c-b;
            duration = d;
        }

        /**
         * Returns the next updated value.
         * @param delta Time passed since last call.
         * @return
         */
        public float next(float delta) {
            timePassed += delta/10f;
            if (timePassed > duration) return endValue;
            return changeValue * (float)Math.sin(timePassed / duration * (Math.PI / 2)) + startValue;
        }
    }
}
