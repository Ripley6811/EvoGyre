package com.mygdx.game.evogyre;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

/**
 * Created by Jay on 1/12/2016.
 */
public class VisualEffects {

    public static Sprite createSpaceship() {
        int W = 64;
        int H = 16;
        // NOTE: Coordinate origin for Pixmap is top-left.
        Pixmap pixmap = new Pixmap(W, H, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.LIGHT_GRAY);
        pixmap.fillTriangle(0, 15, 31, 15, 31, 0);
        pixmap.setColor(Color.DARK_GRAY);
        pixmap.fillTriangle(31, 15, 63, 15, 31, 0);
        return new Sprite(new Texture(pixmap));
    }

    public static void shieldGradientEffect(ShapeRenderer renderer,
                                            Vector3 center,
                                            boolean angleInward, float phase, float alpha) {
        // TODO: include position as parameter and employ angle of strike
        Color col1 = new Color(0,0,0,0);
        Color col2 = new Color(0.8f,0.8f,1f,.8f);

        boolean blend_enabled = Gdx.gl.glIsEnabled(GL20.GL_BLEND);
        if (!blend_enabled) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }
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
        if (!blend_enabled) {
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    private static Tween.SineOut sineOut;
    public static void drawTunnelInit(float duration) {
        sineOut = new Tween.SineOut(-Constants.MAP_SIZE_X, 0f, duration);
    }
    public static void drawTunnel(float delta, ShapeRenderer renderer,
                                  float mapRotation, Vector2 vanishingPoint) {
        if (sineOut == null) drawTunnelInit(Constants.ANIMATE_FUNNEL_DURATION);

        float ringDistances[] = new float[20];
        float interval = Constants.MAP_SIZE_X / ringDistances.length;
        float ringDisplacement = sineOut.next(delta);
        for (int i = 0; i < ringDistances.length; i++) {
            ringDistances[i] = i * interval + ringDisplacement;
        }
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Constants.FUNNEL_COLOR);
        for (float ringX: ringDistances) {
            float i = Constants.MAP_SIZE_X - ringX;
            Vector3 tmpV1 = ProjectionUtils.projectPoint(new Vector2(i, 0), mapRotation, vanishingPoint);
            Vector3 tmpV2 = ProjectionUtils.projectPoint(new Vector2(i, 180), mapRotation, vanishingPoint);
            renderer.circle((tmpV1.x + tmpV2.x) / 2, (tmpV1.y + tmpV2.y) / 2, tmpV1.dst(tmpV2) / 2, 100);
        }
        renderer.end();
    }

    private static Array<Vector2> stars;
    public static void drawStars(ShapeRenderer renderer, float mapRotation, Vector2 vanishingPoint) {
        if (stars == null) {
            stars = new Array<Vector2>(Constants.NUMBER_OF_STARS);
            System.out.println("INSIDE");
            Random random = new Random();
            float DISPLAY_SIZE = Constants.DISPLAY_SIZE;
            for (int i=0; i<Constants.NUMBER_OF_STARS; i++) {
                stars.add(new Vector2(2f * DISPLAY_SIZE * random.nextFloat() - DISPLAY_SIZE,
                        2f * DISPLAY_SIZE * random.nextFloat() - DISPLAY_SIZE));
                System.out.println(stars.peek());
            }
        }

        boolean blend_enabled = Gdx.gl.glIsEnabled(GL20.GL_BLEND);
        if (!blend_enabled) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Constants.STAR_COLOR);
        for (Vector2 star: stars) {
            Vector2 rotateStar = new Vector2(star);
            rotateStar.rotate(mapRotation).add(vanishingPoint.x*Constants.CENTER_DISPLACEMENT,
                    vanishingPoint.y*Constants.CENTER_DISPLACEMENT);
            renderer.circle(rotateStar.x, rotateStar.y, 1f);
        }
        renderer.end();
        if (!blend_enabled) {
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }
}