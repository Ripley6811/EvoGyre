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
    private static final String TAG = EnemyManager.class.getName();
    TextureAtlas atlas;
    Array<Actor> enemies;  // In play
    Array<Float> queueTimes;  // Times for queued enemies to enter play
    Array<Actor> enemyQueue;  // Waiting to enter at specified time (queueTimes)
    BulletManager bulletManager;
    Random random;

    public EnemyManager(TextureAtlas atlas, BulletManager bulletManager) {
        this.atlas = atlas;
        this.enemies = new Array<Actor>();
        this.queueTimes = new Array<Float>();
        this.enemyQueue = new Array<Actor>();
        this.bulletManager = bulletManager;
        random = new Random(12345L);
    }

    public boolean allKilled() {
        if (enemies.size == 0 && enemyQueue.size == 0) return true;
        return false;
    }

    public void enqueue(String type, float startTime, float startY, String pattern) {
        Constants.Flight_Patterns efp = Constants.Flight_Patterns.valueOf(pattern.toUpperCase());
        Actor enemy = null;
        if (type.equals("trident")) enemy = new EnemyTrident(0f, startY, efp, atlas);
        if (type.equals("ball")) enemy = new EnemyBallship(0f, startY, efp, atlas);
        if (type.equals("boss")) enemy = new EnemyBoss(0f, startY, efp, atlas);
        enemy.velocity = new Vector2(70f, 50f);
        queueTimes.add(startTime);
        enemyQueue.add(enemy);

    }

    public void update(float elapsedTime, float playerY) {
        // Move enemies from queue and into play when start time is reached.
        while (enemyQueue.size > 0 && queueTimes.get(0) < elapsedTime) {
            queueTimes.removeIndex(0);
            enemies.add(enemyQueue.removeIndex(0));
        }

        // Update flight paths
        // TODO: Maybe move patterns to another file or JSON file
        for (Actor enemy: enemies) {
            switch (enemy.pattern) {
                case SNAKE_SPIRAL:
                    if (enemy.isEntering) enemy.velocity.setAngle(45f);
                    if (enemy.mapPosition.x > Constants.MAP_SIZE_X - 5 * Constants.RING_INTERVAL && enemy.isEntering) {
                        enemy.isEntering = false;
                    } else if (!enemy.isEntering) {
                        if (enemy.mapPosition.x > Constants.MAP_SIZE_X - 5 * Constants.RING_INTERVAL)
                            enemy.velocity.x -= 0.3f;
                        else enemy.velocity.x += 0.3f;
                    }
                    break;
                case SNAKE_ZIGZAG:
                    enemy.velocity.y = 40f * (float)Math.sin(2f*enemy.elapsedTime);
                    if (enemy.mapPosition.x > Constants.MAP_SIZE_X - 2.2 * Constants.RING_INTERVAL)
                        enemy.velocity.x = -Math.abs(enemy.velocity.x);
                    if (enemy.mapPosition.x < Constants.MAP_SIZE_X - 8 * Constants.RING_INTERVAL)
                        enemy.velocity.x = Math.abs(enemy.velocity.x);
                    break;
                case ABREAST_ZIGZAG:
                    if (enemy.mapPosition.x > Constants.MAP_SIZE_X - 5 * Constants.RING_INTERVAL && enemy.isEntering) {
                        enemy.isEntering = false;
                    } else if (!enemy.isEntering) {
                        enemy.velocity.y = 40f * (float) Math.sin(2f * elapsedTime);
                        if (enemy.mapPosition.x > Constants.MAP_SIZE_X - 5 * Constants.RING_INTERVAL)
                            enemy.velocity.x *= 0.8f;
                    }
                    break;
                case BOSS:
                    if (enemy.mapPosition.x > Constants.MAP_SIZE_X - 6 * Constants.RING_INTERVAL && enemy.isEntering) {
                        enemy.isEntering = false;
                    } else if (!enemy.isEntering) {
                        if (enemy.mapPosition.x > Constants.MAP_SIZE_X - 6 * Constants.RING_INTERVAL)
                            enemy.velocity.x *= 0.8f;
                    }
                    float playerDistance = 0f;
                    if (enemy.mapPosition.y > playerY) {
                        float difference = enemy.mapPosition.y - playerY;
                        if (difference < 180f) playerDistance = -difference;
                        else playerDistance = 360f - difference;
                    } else {
                        float difference = playerY - enemy.mapPosition.y;
                        if (difference < 180f) playerDistance = difference;
                        else playerDistance = -(360f - difference);
                    }
                    enemy.velocity.y = playerDistance;
                    break;
            }
        }

        // Ensure dead enemies are deleted from array
        for (int i=enemies.size-1; i>=0; i--) {
            if (enemies.get(i).isDead) enemies.removeIndex(i);
        }

        // Fire enemy bullets
        for (Actor enemy: enemies) {
            int weaponLevel = enemy.weaponLevel;
            if ((weaponLevel == 2 && random.nextFloat() < 10 * Constants.ENEMY_FIRE_REDUCTION)
                    || random.nextFloat() < Constants.ENEMY_FIRE_REDUCTION ) {
                if (!enemy.isDead && enemy.fire()) {
                    float xPos = enemy.mapPosition.x;
                    float yPos = enemy.mapPosition.y;
                    bulletManager.add(weaponLevel, xPos, yPos);
                }
            }
        }
    }

    public void render(GameScreen screen, float delta) {
        for (Actor enemy: enemies) {
            if (!enemy.isDead) {
                enemy.update(screen, delta);
                enemy.render(screen, delta);
            }
        }

        bulletManager.render(screen);
    }
}
