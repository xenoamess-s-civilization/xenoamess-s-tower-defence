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

import com.xenoamess.cyan_potion.coordinate.AbstractEntityScene;
import com.xenoamess.cyan_potion.coordinate.entity.AbstractDynamicEntity;
import com.xenoamess.cyan_potion.coordinate.physic.shapes.Circle;
import com.xenoamess.cyan_potion.towerdefence.TowerDefenceScene;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector3f;

/**
 * Projectile fired by towers.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
@Getter
@Setter
public class Projectile extends AbstractDynamicEntity {

    private final TowerDefenceScene towerDefenceScene;
    private final Enemy target;
    private final int damage;
    private final float speed;
    private boolean destroyed = false;

    /**
     * Constructor for Projectile.
     *
     * @param scene the scene
     * @param startX the starting x position
     * @param startY the starting y position
     * @param target the target enemy
     * @param damage the damage amount
     * @param speed the projectile speed
     */
    public Projectile(TowerDefenceScene scene, float startX, float startY, 
                      Enemy target, int damage, float speed) {
        super(
                scene,
                startX, startY,
                8, 8,
                3,
                null,
                null
        );
        this.towerDefenceScene = scene;
        this.target = target;
        this.damage = damage;
        this.speed = speed;
        
        // Initialize shape after super() call
        this.setShape(new Circle(this, new Vector3f(startX, startY, 0), new Vector3f(8, 8, 0)));
    }

    @Override
    public void update() {
        if (destroyed || target == null || target.isDead()) {
            destroyed = true;
            return;
        }

        // Move towards target
        float dx = target.getCenterPosX() - getCenterPosX();
        float dy = target.getCenterPosY() - getCenterPosY();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance <= speed) {
            // Hit target
            forceMove(dx, dy);
            hit();
        } else {
            // Move towards target
            float moveX = (dx / distance) * speed;
            float moveY = (dy / distance) * speed;
            forceMove(moveX, moveY);
        }
    }

    /**
     * Hit the target and deal damage.
     */
    private void hit() {
        if (target != null && !target.isDead()) {
            target.takeDamage(damage);
            log.debug("Projectile hit enemy for {} damage!", damage);
        }
        destroyed = true;
    }

    /**
     * Check if projectile is destroyed.
     *
     * @return true if destroyed
     */
    public boolean isDestroyed() {
        return destroyed;
    }
}
