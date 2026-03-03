/**
 * Copyright (C) 2020 XenoAmess
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.xenoamess.cyan_potion.towerdefence.entities;

import com.xenoamess.cyan_potion.base.render.Bindable;
import com.xenoamess.cyan_potion.coordinate.AbstractEntityScene;
import com.xenoamess.cyan_potion.coordinate.entity.AbstractDynamicEntity;
import com.xenoamess.cyan_potion.coordinate.physic.shapes.Circle;
import org.joml.Vector3f;
import com.xenoamess.cyan_potion.towerdefence.TowerDefenceScene;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Enemy entity that moves along the path.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
@Getter
@Setter
public class Enemy extends AbstractDynamicEntity {

    public enum EnemyType {
        NORMAL(100, 1.0f, 10, 5, 0xff0000ff),
        FAST(50, 2.0f, 5, 8, 0x00ff00ff),
        TANK(300, 0.5f, 30, 10, 0x0000ffff),
        BOSS(1000, 0.3f, 100, 50, 0xff00ffff);

        private final int maxHealth;
        private final float speed;
        private final int reward;
        private final int damage;
        private final int color;

        EnemyType(int maxHealth, float speed, int reward, int damage, int color) {
            this.maxHealth = maxHealth;
            this.speed = speed;
            this.reward = reward;
            this.damage = damage;
            this.color = color;
        }

        public int getMaxHealth() { return maxHealth; }
        public float getSpeed() { return speed; }
        public int getReward() { return reward; }
        public int getDamage() { return damage; }
        public int getColor() { return color; }
    }

    private final TowerDefenceScene towerDefenceScene;
    private final EnemyType enemyType;
    private int health;
    private float speed;
    private int reward;
    private boolean dead = false;
    private boolean reachedEnd = false;
    
    private int currentPathIndex = 0;
    private List<int[]> path;
    private float targetX;
    private float targetY;

    /**
     * Constructor for Enemy.
     *
     * @param scene   the scene
     * @param startX  the starting x position
     * @param startY  the starting y position
     * @param enemyType the enemy type
     */
    public Enemy(TowerDefenceScene scene, float startX, float startY, EnemyType enemyType) {
        super(
                scene,
                startX, startY,
                24, 24,
                1,
                null,
                null
        );
        this.towerDefenceScene = scene;
        this.enemyType = enemyType;
        this.health = enemyType.getMaxHealth();
        this.speed = enemyType.getSpeed();
        this.reward = enemyType.getReward();

        // Initialize shape after super() call
        this.setShape(new Circle(this, new Vector3f(startX, startY, 0), new Vector3f(24, 24, 0)));

        // Initialize path
        this.path = scene.getMapGrid().getPath();
        this.currentPathIndex = 0;
        updateTarget();
    }

    private void updateTarget() {
        if (currentPathIndex < path.size()) {
            int[] gridPos = path.get(currentPathIndex);
            this.targetX = towerDefenceScene.getMapGrid().gridToWorldX(gridPos[0]);
            this.targetY = towerDefenceScene.getMapGrid().gridToWorldY(gridPos[1]);
        }
    }

    @Override
    public void update() {
        if (dead || reachedEnd) {
            return;
        }

        // Move towards target
        float dx = targetX - getCenterPosX();
        float dy = targetY - getCenterPosY();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance <= speed) {
            // Reached waypoint
            forceMove(dx, dy);
            currentPathIndex++;
            
            if (currentPathIndex >= path.size()) {
                // Reached end of path
                reachedEnd = true;
                dead = true;
                log.debug("Enemy reached end of path");
            } else {
                updateTarget();
            }
        } else {
            // Move towards target
            float moveX = (dx / distance) * speed;
            float moveY = (dy / distance) * speed;
            forceMove(moveX, moveY);
        }
    }

    /**
     * Take damage from a tower.
     *
     * @param damage the amount of damage
     */
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
            dead = true;
            log.debug("Enemy killed!");
        }
    }

    /**
     * Get the current health percentage.
     *
     * @return health percentage (0.0 - 1.0)
     */
    public float getHealthPercent() {
        return (float) health / enemyType.maxHealth;
    }

    /**
     * Check if enemy is dead.
     *
     * @return true if dead
     */
    public boolean isDead() {
        return dead;
    }

    /**
     * Check if enemy reached the end of the path.
     *
     * @return true if reached end
     */
    public boolean isReachedEnd() {
        return reachedEnd;
    }

    public int getReward() {
        return reward;
    }
}
