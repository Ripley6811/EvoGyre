package com.mygdx.game.evogyre;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Jay on 1/12/2016.
 */
public class ImageAssets {

    public Sprite getSpaceship() {
        // TODO: if spaceship doesn't exist then create it.
        return new Sprite();
    }

    public Sprite createSpaceship() {
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
}
