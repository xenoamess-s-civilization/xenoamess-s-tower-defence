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
import com.xenoamess.cyan_potion.coordinate.entity.StaticEntity;
import com.xenoamess.cyan_potion.coordinate.physic.shapes.Circle;
import com.xenoamess.cyan_potion.towerdefence.TowerDefenceScene;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Tower entity that attacks enemies.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
@Getter
@Setter
public class Tower extends StaticEntity {

    public enum TowerType {
        BASIC(50, 100f, 1000, 10, 0xff0000ff),
        SNIPER(150, 200f, 2000, 50, 0x00ff00ff),
        RAPID(100, 80f, 200, 5, 0xffff00ff),
        SPLASH(200, 120f, 1500, 20, 0xff00ffff);

        private final int cost;
        private final float range;
        private final int cooldownMs;
        private final int damage;
        private final int color;

        TowerType(int cost, float range, int cooldownMs, int damage, int color) {
            this.cost = cost;
            this.range = range;
            this.cooldownMs = cooldownMs;
            this.damage = damage;
            this.color = color;
        }

        public int getCost() { return cost; }
        public float getRange() { return range; }
        public int getCooldownMs() { return cooldownMs; }
        public int getDamage() { return damage; }
        public int getColor() { return color; }
    }

    private final TowerDefenceScene towerDefenceScene;
    private final TowerType towerType;
    private final float range;
    private final int cooldownMs;
    private final int damage;
    
    private long lastAttackTime = 0;
    private Enemy currentTarget = null;

    /**
     * Constructor for Tower.
     *
     * @param scene the scene
     * @param centerX the center x position
     * @param centerY the center y position
     * @param width the width
     * @param height the height
     * @param towerType the tower type
     */
    public Tower(TowerDefenceScene scene, float centerX, float centerY, 
                 float width, float height, TowerType towerType) {
        super(
                scene,
                centerX, centerY,
                width, height,
                2,
                null,
                null
        );
        this.towerDefenceScene = scene;
        this.towerType = towerType;
        this.range = towerType.range;
        this.cooldownMs = towerType.cooldownMs;
        this.damage = towerType.damage;
    }

    /**
     * Update the tower - find targets and attack.
     */
    public void update() {
        long currentTime = System.currentTimeMillis();
        
        // Check if can attack
        if (currentTime - lastAttackTime < cooldownMs) {
            return;
        }

        // Find target
        Enemy target = findTarget();
        if (target != null) {
            attack(target);
            lastAttackTime = currentTime;
        }
    }

    /**
     * Find the best target for this tower.
     *
     * @return the target enemy, or null if none found
     */
    private Enemy findTarget() {
        // Priority: closest to end (highest path index, closest to next waypoint)
        Enemy bestTarget = null;
        int bestPathIndex = -1;
        float bestDistanceToNext = Float.MAX_VALUE;
        
        List<Enemy> enemiesInRange = towerDefenceScene.getEnemiesInRange(
                getCenterPosX(), getCenterPosY(), range);
        
        for (Enemy enemy : enemiesInRange) {
            if (enemy.isDead()) continue;
            
            // Simple targeting: closest enemy
            if (bestTarget == null) {
                bestTarget = enemy;
            }
        }
        
        return bestTarget;
    }

    /**
     * Attack the target enemy.
     *
     * @param target the enemy to attack
     */
    private void attack(Enemy target) {
        // Create projectile
        Projectile projectile = new Projectile(
                towerDefenceScene,
                getCenterPosX(), getCenterPosY(),
                target,
                damage,
                8f
        );
        towerDefenceScene.addProjectile(projectile);
        log.debug("Tower fired at enemy!");
    }

    /**
     * Get the range of this tower.
     *
     * @return the range
     */
    public float getRange() {
        return range;
    }
}
