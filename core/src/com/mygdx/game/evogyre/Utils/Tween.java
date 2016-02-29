package com.mygdx.game.evogyre.Utils;

/**
 * Created by Jay on 1/18/2016.
 *
 * Tween equations taken from "http://gizma.com/easing/" by Robert Penner.
 */
public class Tween {
    private static final String TAG = Tween.class.getName();

    public static class TweenBase {
        public float timePassed;
        public float startValue;
        public float endValue;
        public float changeValue;
        public float duration;

        /**
         * Constructor for SineOut tween equation.
         * @param b Start value
         * @param c Finish value
         * @param d Duration of tween
         */
        public TweenBase(float b, float c, float d) {
            timePassed = 0f;
            startValue = b;
            endValue = c;
            changeValue = c-b;
            duration = d;
        }

        public boolean isDone() {
            if (timePassed >= duration) return true;
            return false;
        }
    }

    public static class SineOut extends TweenBase {
        /**
         * Constructor for SineOut tween equation.
         * @param b Start value
         * @param c Finish value
         * @param d Duration of tween
         */
        public SineOut(float b, float c, float d) {
            super(b, c, d);
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

    public static class QuadInOut extends TweenBase {
        /**
         * Constructor for SineOut tween equation.
         * @param b Start value
         * @param c Finish value
         * @param d Duration of tween
         */
        public QuadInOut(float b, float c, float d) {
            super(b, c, d);
        }

        /**
         * Returns the next updated value.
         * @param delta Time passed since last call.
         * @return
         */
        public float next(float delta) {
            timePassed += delta;
            if (timePassed > duration) return endValue;
            float t = timePassed / (duration / 2);
            if (t < 1) return changeValue / 2 * t * t + startValue;
            t--;
            return -changeValue/2 * (t * (t - 2) - 1) + startValue;
        }
    }
}
