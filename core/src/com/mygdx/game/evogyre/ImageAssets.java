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

/**
 * Created by Jay on 1/12/2016.
 */
public class ImageAssets {

    public static Sprite getSpaceship() {
        // TODO: if spaceship doesn't exist then create it.
        return new Sprite();
    }

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
        float x0 = 0f;
        float y0 = -Constants.SHIELD_EFFECT_OFFSET;
        Vector2 inner = new Vector2(x0,y0);
        Vector2 circle = new Vector2(SHIELD_RADIUS, 0f);
        Vector2 position = new Vector2(center.x, center.y);
        float angle = angleInward ? position.angle()+90f : position.angle()-90f;
        float r0 = SHIELD_RADIUS-y0;
        for (double i=0; i<Math.PI+Math.PI; i+=0.03f) {
            circle.setAngleRad((float)i);
            Vector2 combo = new Vector2(circle.x-inner.x, circle.y-inner.y);
            float rlength = combo.len();
            float rphase = r0 * phase;
            if (rlength > rphase) {
                combo.setLength(rphase).add(inner).rotate(angle);
                circle.rotate(angle);
                renderer.line(center.x + combo.x, center.y + combo.y,
                        center.x + circle.x, center.y + circle.y,
                        col1, new Color(0.8f, 0.8f, 1f, alpha * rlength / r0));
            }
        }
        renderer.end();
        if (!blend_enabled) {
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    private static Tween.SineOut sineOut;
    public static void drawTunnelInit() {
//        float interval = Constants.MAP_SIZE_X / ringDistances.length;
//        for (int i = 0; i < ringDistances.length; i++) {
//            ringDistances[i] = -interval * i;
//        }
        sineOut = new Tween.SineOut(-Constants.MAP_SIZE_X, 0f, 0.4f);
    }
    public static void drawTunnel(float delta, ShapeRenderer renderer,
                                  float mapRotation, Vector2 vanishingPoint) {
        if (sineOut == null) {
            drawTunnelInit();
        }
        float ringDistances[] = new float[20];
        float interval = Constants.MAP_SIZE_X / ringDistances.length;
        float ringDisplacement = sineOut.next(delta);
        for (int i = 0; i < ringDistances.length; i++) {
            ringDistances[i] = i * interval + ringDisplacement;
        }
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Constants.CYLINDER_COLOR);
        for (float ringX: ringDistances) {
            float i = Constants.MAP_SIZE_X - ringX;
            Vector3 tmpV1 = ProjectionUtils.projectPoint(new Vector2(i, 0), mapRotation, vanishingPoint);
            Vector3 tmpV2 = ProjectionUtils.projectPoint(new Vector2(i, 180), mapRotation, vanishingPoint);
            renderer.circle((tmpV1.x + tmpV2.x) / 2, (tmpV1.y + tmpV2.y) / 2, tmpV1.dst(tmpV2) / 2, 100);
        }
        renderer.end();
    }
}
