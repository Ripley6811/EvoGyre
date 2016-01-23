package com.mygdx.game.evogyre;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Jay on 1/22/2016.
 *
 * This class manages the flight paths and entrances of enemies.
 */
public class EnemyManager {
    TextureAtlas atlas;
    Array<Enemy> enemies;
    Array<Float> queueTimes;
    Array<Enemy> enemyQueue;

    // TODO: maybe make a max cap for number of enemies on screen and wait for room before adding more.

    public EnemyManager(TextureAtlas atlas) {
        this.atlas = atlas;
        this.enemies = new Array<Enemy>();
        this.queueTimes = new Array<Float>();
        this.enemyQueue = new Array<Enemy>();
    }

    public void enqueue(int type, float startTime, float startY, String pattern) {
        Constants.Flight_Patterns efp = Constants.Flight_Patterns.valueOf(pattern.toUpperCase());
        Enemy enemy = new Enemy(type, 0f, startY, efp, atlas);
        enemy.velocity = new Vector2(70f, 50f);
        queueTimes.add(startTime);
        enemyQueue.add(enemy);

    }

    public void update(float elapsedTime) {
        // Move enemies from queue and into play when start time is reached.
        if (enemyQueue.size > 0) {
            if (queueTimes.get(0) < elapsedTime) {
                queueTimes.removeIndex(0);
                enemies.add(enemyQueue.removeIndex(0));
            }
        }

        // Update flight paths
        for (Enemy enemy: enemies) {
            switch (enemy.pattern) {
                case SNAKE_SPIRAL:
                    if (enemy.position.x > Constants.MAP_SIZE_X - 2.2 * Constants.RING_INTERVAL)
                        enemy.velocity.x = -Math.abs(enemy.velocity.x);
                    if (enemy.position.x < Constants.MAP_SIZE_X - 8 * Constants.RING_INTERVAL)
                        enemy.velocity.x = Math.abs(enemy.velocity.x);
                    break;
                case SNAKE_ZIGZAG:
                    // TODO: make plans
                    break;
                case ABREAST_ZIGZAG:

                    break;
            }
        }
        // TODO: Ensure dead enemies are deleted from array
    }

    public void render(MyShapeRenderer renderer, float delta, float mapRotation, Vector2 vanishingPoint) {
        for (Enemy enemy: enemies) {
            if (!enemy.isDead) {
                enemy.update(delta);
                enemy.render(renderer, delta, mapRotation, vanishingPoint);
            }
        }
    }
}
