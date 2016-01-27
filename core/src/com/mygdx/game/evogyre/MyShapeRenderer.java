package com.mygdx.game.evogyre;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;

/**
 * Created by Jay on 1/19/2016.
 */
public class MyShapeRenderer extends ShapeRenderer {
    private static final String TAG = MyShapeRenderer.class.getName();
    public SpriteBatch spriteBatch;

    public MyShapeRenderer(SpriteBatch batch) {
        super();
        this.spriteBatch = batch;
    }

    @Override
    public void setProjectionMatrix(Matrix4 matrix) {
        super.setProjectionMatrix(matrix);
        spriteBatch.setProjectionMatrix(matrix);
    }

    @Override
    public void translate(float x, float y, float z) {
        super.translate(x, y, z);
        spriteBatch.setTransformMatrix(getTransformMatrix());
    }

    @Override
    public void rotate(float axisX, float axisY, float axisZ, float degrees) {
        super.rotate(axisX, axisY, axisZ, degrees);
        spriteBatch.setTransformMatrix(getTransformMatrix());
    }

    @Override
    public void scale(float scaleX, float scaleY, float scaleZ) {
        super.scale(scaleX, scaleY, scaleZ);
        spriteBatch.setTransformMatrix(getTransformMatrix());
    }
}
