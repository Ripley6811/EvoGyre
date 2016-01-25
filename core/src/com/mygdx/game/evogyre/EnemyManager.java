package com.mygdx.game.evogyre;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

/**
 * Created by Jay on 1/22/2016.
 *
 * This class manages the flight paths and entrances of enemies.
 */
public class EnemyManager {
    TextureAtlas atlas;
    Array<Actor> enemies;
    Array<Float> queueTimes;
    Array<Actor> enemyQueue;
    BulletManager bulletManager;
    Random random;

    // TODO: maybe make a max cap for number of enemies on screen and wait for room before adding more.

    public EnemyManager(TextureAtlas atlas, BulletManager bulletManager) {
        this.atlas = atlas;
        this.enemies = new Array<Actor>();
        this.queueTimes = new Array<Float>();
        this.enemyQueue = new Array<Actor>();
        this.bulletManager = bulletManager;
        random = new Random(12345L);
    }

    public void enqueue(String type, float startTime, float startY, String pattern) {
        Constants.Flight_Patterns efp = Constants.Flight_Patterns.valueOf(pattern.toUpperCase());
        Actor enemy = new Actor(0,0);
        if (type.equals("trident")) enemy = new EnemyTrident(0f, startY, efp, atlas);
        if (type.equals("ball")) enemy = new EnemyBall(0f, startY, efp, atlas);
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
        for (Actor enemy: enemies) {
            switch (enemy.pattern) {
                case SNAKE_SPIRAL:
                    // TODO: Make better paths
                    if (enemy.position.x > Constants.MAP_SIZE_X - 5 * Constants.RING_INTERVAL && enemy.isEntering) {
                        enemy.isEntering = false;
                    } else if (!enemy.isEntering) {
                        if (enemy.position.x > Constants.MAP_SIZE_X - 5 * Constants.RING_INTERVAL)
                            enemy.velocity.x -= 0.3f;
                        else enemy.velocity.x += 0.3f;
                    }
                    break;
                case SNAKE_ZIGZAG:
                    enemy.velocity.y = 40f * (float)Math.sin(2f*enemy.elapsedTime);
                    if (enemy.position.x > Constants.MAP_SIZE_X - 2.2 * Constants.RING_INTERVAL)
                        enemy.velocity.x = -Math.abs(enemy.velocity.x);
                    if (enemy.position.x < Constants.MAP_SIZE_X - 8 * Constants.RING_INTERVAL)
                        enemy.velocity.x = Math.abs(enemy.velocity.x);
                    // TODO: make plans
                    break;
                case ABREAST_ZIGZAG:
                    if (enemy.position.x > Constants.MAP_SIZE_X - 5 * Constants.RING_INTERVAL && enemy.isEntering) {
                        enemy.isEntering = false;
                    } else if (!enemy.isEntering) {
                        enemy.velocity.y = 40f * (float) Math.sin(2f * elapsedTime);
                        if (enemy.position.x > Constants.MAP_SIZE_X - 5 * Constants.RING_INTERVAL)
                            enemy.velocity.x *= 0.8f;
                    }
                    break;
            }
        }
        // TODO: Ensure dead enemies are deleted from array


        // Fire enemy bullets
        for (Actor enemy: enemies) {
            if (random.nextFloat() < Constants.ENEMY_FIRE_REDUCTION) {
                if (enemy.fire()) {
                    int weaponLevel = enemy.weaponLevel;
                    float xPos = enemy.position.x;
                    float yPos = enemy.position.y;
                    bulletManager.add(weaponLevel, xPos, yPos);
                }
            }
        }
    }

    public void render(MyShapeRenderer renderer, float delta, float mapRotation, Vector2 vanishingPoint) {
        for (Actor enemy: enemies) {
            if (!enemy.isDead) {
                enemy.update(delta);
                enemy.render(renderer, delta, mapRotation, vanishingPoint);
            }
        }

        bulletManager.render(renderer, mapRotation, vanishingPoint);
    }
}
