package com.mygdx.game.evogyre;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

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

    public static void radialGradientFill(ShapeRenderer renderer,
                                   float angle, float phase, float alpha) {
        // TODO: include position as parameter and employ angle of strike
        Color col1 = new Color(0,0,0,0);
//        Color col1 = new Color(1,0,0,.5f);
        Color col2 = new Color(0.86f,0.86f,1f,.8f);
        float rat1 = .25f;
        float rat2 = 1f;

        boolean blend_enabled = Gdx.gl.glIsEnabled(GL20.GL_BLEND);
        if (!blend_enabled) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }
        renderer.begin(ShapeRenderer.ShapeType.Line);

//        renderer.setColor(col2);
//        renderer.setColor(new Color(0.86f,0.86f,1f,1f));
        float drawRadius = 20f;
        float x0 = 0f;
        float y0 = -10f;
        Vector2 inner = new Vector2(x0,y0);
        Vector2 circle = new Vector2(drawRadius, 0f);
        float r0 = drawRadius-y0;
        for (double i=0; i<Math.PI+Math.PI; i+=0.01f) {
            circle.setAngleRad((float)i);
            Vector2 combo = new Vector2(circle.x-inner.x, circle.y-inner.y);
            float rlength = combo.len();
            float rphase = r0 * phase;
            if (rlength > rphase) {
                combo.setLength(rphase);
                renderer.line(combo.x, combo.y+inner.y,
                        circle.x, circle.y,
                        col1, new Color(0.86f, 0.86f, 1f, alpha * rlength / r0));
            }
//            float a = (float)Math.sqrt(10000+y0*y0 - 200*y0 * Math.cos(i));
//
//            renderer.line((float) (70 * Math.sin(i)), (float) (70 * Math.cos(i)),
//                    (float) (100 * Math.sin(i)), (float) (100 * Math.cos(i)),
//                    col1, new Color(0.86f, 0.86f, 1f, .8f * a / r0));
        }

//        double a = Math.sqrt(12500 - 10000 * Math.cos(Math.PI/2));
//        System.out.println("len: " + a);
        renderer.end();
        if (!blend_enabled) {
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }
}
