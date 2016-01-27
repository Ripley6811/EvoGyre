package com.mygdx.game.evogyre;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

/**
 * NOTE: Creating gradient effects with ShapeRenderer is limited to the "line",
 * "rectangle" and "triangle" methods. PixMap is more flexible but the disadvantage
 * is that the result is a raster image. ShapeRenderer creates vector images.
 * Created by Jay on 1/12/2016.
 */
public class VisualEffects {
    private static final String TAG = VisualEffects.class.getName();

    /**
     * Creates a shield absorption/dissipating effect around spaceship from a hit.
     * Unlike the funnel rings effect, there can be multiple instances of this
     * effect, so the "phase" and "alpha" is maintained separately. It also depends
     * on the vehicle mapPosition which changes independently.
     * @param renderer
     * @param center
     * @param angleInward True for player and false for enemies
     * @param phase
     * @param alpha
     */
    public static void shieldGradientEffect(ShapeRenderer renderer,
                                            Vector3 center,
                                            boolean angleInward, float phase, float alpha) {
        // TODO: Create a less computationally intensive alternate version
        Color col1 = new Color(0,0,0,0);

        DrawingUtils.enableBlend();
        renderer.begin(ShapeRenderer.ShapeType.Line);

        float SHIELD_RADIUS = Constants.SHIELD_RADIUS;
        float SHIELD_WIDTH_MULTIPLIER = Constants.SHIELD_WIDTH_MULTIPLIER;
        float x0 = 0f;
        float y0 = -Constants.SHIELD_EFFECT_OFFSET;
        Vector2 inner = new Vector2(x0,y0);
        Vector2 circle = new Vector2(SHIELD_RADIUS, 0f);
        Vector2 position = new Vector2(center.x, center.y);
        float angle = angleInward ? position.angle()+90f : position.angle()-90f;
        float r0 = SHIELD_RADIUS-y0;
        // Draws shields as a series of gradient lines
        for (float i=0; i<2*Math.PI; i+=0.03f) {
            circle.setLength(SHIELD_RADIUS).setAngleRad(i);
            Vector2 combo = new Vector2(circle.x-inner.x, circle.y-inner.y);
            float rLength = combo.len();
            float rPhase = r0 * phase;
            if (rLength > rPhase) {
                combo.setLength(rPhase).add(inner).rotate(angle);
                circle.setLength(SHIELD_RADIUS*(float)(SHIELD_WIDTH_MULTIPLIER/Math.sqrt(Math.pow(SHIELD_WIDTH_MULTIPLIER*Math.sin(i),2f)+Math.pow(Math.cos(i),2f)))).rotate(angle);
                renderer.line(center.x + combo.x, center.y + combo.y,
                        center.x + circle.x, center.y + circle.y,
                        col1, new Color(0.8f, 0.8f, 1f, alpha * rLength / r0));
            }
        }
        renderer.end();
        DrawingUtils.disableBlend();
    }

    private static Tween.SineOut sineOut;
    private static float funnelAlpha;
    public static void drawTunnelInit(float duration) {
        sineOut = new Tween.SineOut(-Constants.MAP_SIZE_X, 0f, duration);
        funnelAlpha = 0.1f;
    }
    public static void drawTunnel(float delta, ShapeRenderer renderer,
                                  float mapRotation, Vector2 vanishingPoint,
                                  boolean showDrawing) {
        if (sineOut == null) drawTunnelInit(Constants.ANIMATE_FUNNEL_DURATION);

        // Funnel rings fade in/out.
        Color funnelColor = new Color(Constants.FUNNEL_COLOR);
        if (showDrawing) {
            funnelAlpha = Math.min(funnelAlpha + delta, funnelColor.a);
        } else {
            funnelAlpha = Math.max(funnelAlpha - delta, 0f);
        }
        funnelColor.a = funnelAlpha;
        if (funnelAlpha == 0f) return;

        float ringDistances[] = new float[Constants.NUMBER_OF_RINGS];
        float ringDisplacement = sineOut.next(delta);
        for (int i = 0; i < ringDistances.length; i++) {
            ringDistances[i] = i * Constants.RING_INTERVAL + ringDisplacement;
        }
        DrawingUtils.enableBlend();
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(funnelColor);
        for (float ringX: ringDistances) {
            float i = Constants.MAP_SIZE_X - ringX;
            Vector3 tmpV1 = ProjectionUtils.projectPoint3D(new Vector2(i, 0));
            Vector3 tmpV2 = ProjectionUtils.projectPoint3D(new Vector2(i, 180));
            renderer.circle((tmpV1.x + tmpV2.x) / 2, (tmpV1.y + tmpV2.y) / 2, tmpV1.dst(tmpV2) / 2, 100);
        }
        renderer.end();
        DrawingUtils.disableBlend();
    }

    private static Array<Vector2> stars;
    public static void drawStars(ShapeRenderer renderer, float mapRotation, Vector2 vanishingPoint) {
        if (stars == null) {
            stars = new Array<Vector2>(Constants.NUMBER_OF_STARS);
            Random random = new Random();
            float DISPLAY_SIZE = Constants.DISPLAY_SIZE;
            for (int i=0; i<Constants.NUMBER_OF_STARS; i++) {
                stars.add(new Vector2(2f * DISPLAY_SIZE * random.nextFloat() - DISPLAY_SIZE,
                        2f * DISPLAY_SIZE * random.nextFloat() - DISPLAY_SIZE));
            }
        }

        DrawingUtils.enableBlend();
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Constants.STAR_COLOR);
        for (Vector2 star: stars) {
            Vector2 rotateStar = new Vector2(star);
            rotateStar.rotate(mapRotation).add(vanishingPoint.x*Constants.CENTER_DISPLACEMENT,
                    vanishingPoint.y*Constants.CENTER_DISPLACEMENT);
            renderer.circle(rotateStar.x, rotateStar.y, 1f);
        }
        renderer.end();
        DrawingUtils.disableBlend();
    }
}
