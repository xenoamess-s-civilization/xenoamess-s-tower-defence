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
package com.xenoamess.cyan_potion.towerdefence.plugins;

import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.game_window_components.GameWindowComponentTreeNode;
import com.xenoamess.cyan_potion.towerdefence.TowerDefenceScene;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

/**
 * Code plugins for Tower Defence module.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
@SuppressWarnings("unused")
public class CodePlugins {

    private CodePlugins() {
        // shall never instantiate
    }

    /**
     * Plugin to initialize the Tower Defence scene after game window init.
     */
    public static final Function<GameManager, Void> PLUGIN_TOWER_DEFENCE_SCENE_INIT = (GameManager gameManager) -> {
        log.info("Initializing Tower Defence scene...");
        
        TowerDefenceScene scene = new TowerDefenceScene(gameManager.getGameWindow());
        scene.addToGameWindowComponentTree(gameManager.getGameWindowComponentTree().getRoot());
        
        log.info("Tower Defence scene initialized successfully");
        return null;
    };
}
