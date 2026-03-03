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
package com.xenoamess.cyan_potion.towerdefence.game;

import com.xenoamess.cyan_potion.towerdefence.TowerDefenceScene;
import com.xenoamess.cyan_potion.towerdefence.entities.Enemy;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages enemy waves.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
@Getter
@Setter
public class WaveManager {

    private final TowerDefenceScene scene;
    private int currentWave = 0;
    private int enemiesSpawnedInWave = 0;
    private int enemiesToSpawn = 0;
    private long lastSpawnTime = 0;
    private long spawnInterval = 1000; // ms between spawns
    private boolean waveInProgress = false;
    private boolean waveComplete = true;
    
    private static final int WAVES_COUNT = 10;

    /**
     * Constructor for WaveManager.
     *
     * @param scene the tower defence scene
     */
    public WaveManager(TowerDefenceScene scene) {
        this.scene = scene;
    }

    /**
     * Update the wave manager.
     */
    public void update() {
        if (!waveInProgress) {
            // Check if we should start next wave
            if (waveComplete && currentWave < WAVES_COUNT) {
                startNextWave();
            }
            return;
        }

        // Spawn enemies
        if (enemiesSpawnedInWave < enemiesToSpawn) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastSpawnTime >= spawnInterval) {
                spawnEnemy();
                lastSpawnTime = currentTime;
            }
        } else {
            // Check if wave is complete (all enemies dead)
            if (scene.getEnemies().isEmpty()) {
                completeWave();
            }
        }
    }

    /**
     * Start the next wave.
     */
    private void startNextWave() {
        currentWave++;
        enemiesSpawnedInWave = 0;
        waveInProgress = true;
        waveComplete = false;
        
        // Calculate wave difficulty
        enemiesToSpawn = 5 + currentWave * 2;
        spawnInterval = Math.max(300, 1000 - currentWave * 50);
        
        log.info("Starting wave {} with {} enemies", currentWave, enemiesToSpawn);
    }

    /**
     * Spawn an enemy.
     */
    private void spawnEnemy() {
        int[] startPos = scene.getMapGrid().getStartPos();
        float worldX = scene.getMapGrid().gridToWorldX(startPos[0]);
        float worldY = scene.getMapGrid().gridToWorldY(startPos[1]);
        
        // Determine enemy type based on wave
        Enemy.EnemyType type = getEnemyTypeForWave();
        
        Enemy enemy = new Enemy(scene, worldX, worldY, type);
        scene.addEnemy(enemy);
        enemiesSpawnedInWave++;
        
        log.debug("Spawned enemy {} of wave {}", enemiesSpawnedInWave, currentWave);
    }

    /**
     * Get the enemy type for the current wave.
     *
     * @return the enemy type
     */
    private Enemy.EnemyType getEnemyTypeForWave() {
        int roll = (int) (Math.random() * 100);
        
        if (currentWave >= 8 && roll < 10) {
            return Enemy.EnemyType.BOSS;
        } else if (currentWave >= 5 && roll < 20) {
            return Enemy.EnemyType.TANK;
        } else if (currentWave >= 3 && roll < 30) {
            return Enemy.EnemyType.FAST;
        }
        return Enemy.EnemyType.NORMAL;
    }

    /**
     * Complete the current wave.
     */
    private void completeWave() {
        waveInProgress = false;
        waveComplete = true;
        
        // Bonus money for completing wave
        int bonus = 20 + currentWave * 5;
        scene.setPlayerMoney(scene.getPlayerMoney() + bonus);
        
        log.info("Wave {} complete! Bonus: ${}", currentWave, bonus);
        
        if (currentWave >= WAVES_COUNT) {
            log.info("All waves complete! Victory!");
            // TODO: Victory condition
        }
    }

    /**
     * Reset the wave manager.
     */
    public void reset() {
        currentWave = 0;
        enemiesSpawnedInWave = 0;
        enemiesToSpawn = 0;
        lastSpawnTime = 0;
        waveInProgress = false;
        waveComplete = true;
    }

    /**
     * Get the current wave number.
     *
     * @return the current wave
     */
    public int getCurrentWave() {
        return currentWave;
    }

    /**
     * Get the total number of waves.
     *
     * @return the total waves count
     */
    public int getTotalWaves() {
        return WAVES_COUNT;
    }

    /**
     * Check if all waves are complete.
     *
     * @return true if all waves complete
     */
    public boolean isAllWavesComplete() {
        return currentWave >= WAVES_COUNT && waveComplete;
    }
}
