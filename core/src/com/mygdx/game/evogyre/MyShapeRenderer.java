package com.mygdx.game.evogyre;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;

/**
 * Created by Jay on 1/19/2016.
 */
public class MyShapeRenderer extends ShapeRenderer {
    public SpriteBatch batch;

    public MyShapeRenderer() {
        super();
        batch = new SpriteBatch();
    }

    @Override
    public void setProjectionMatrix(Matrix4 matrix) {
        super.setProjectionMatrix(matrix);
        batch.setProjectionMatrix(matrix);
    }

    @Override
    public void translate(float x, float y, float z) {
        super.translate(x, y, z);
        batch.setTransformMatrix(getTransformMatrix());
    }

    @Override
    public void rotate(float axisX, float axisY, float axisZ, float degrees) {
        super.rotate(axisX, axisY, axisZ, degrees);
        batch.setTransformMatrix(getTransformMatrix());
    }

    @Override
    public void scale(float scaleX, float scaleY, float scaleZ) {
        super.scale(scaleX, scaleY, scaleZ);
        batch.setTransformMatrix(getTransformMatrix());
    }
}
