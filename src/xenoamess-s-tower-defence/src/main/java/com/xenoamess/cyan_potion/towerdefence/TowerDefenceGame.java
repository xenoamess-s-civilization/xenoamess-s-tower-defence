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
package com.xenoamess.cyan_potion.towerdefence;

import com.xenoamess.cyan_potion.base.GameManager;

import java.io.File;
import java.util.Map;

/**
 * XenoAmess's Tower Defence module entry point.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
public class TowerDefenceGame {

    /**
     * Main entry point for the tower defence game.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        // Ensure running from project root (where settings/ directory exists)
        String cwd = System.getProperty("user.dir");
        File settingsFile = new File(cwd, "settings/TowerDefenceSettings.x8l");
        
        if (!settingsFile.exists()) {
            // Try to find project root by looking for settings directory
            File current = new File(cwd);
            while (current != null && current.getParentFile() != null) {
                File parent = current.getParentFile();
                File testSettings = new File(parent, "settings/TowerDefenceSettings.x8l");
                if (testSettings.exists()) {
                    System.setProperty("user.dir", parent.getAbsolutePath());
                    System.out.println("Changed working directory to: " + parent.getAbsolutePath());
                    break;
                }
                current = parent;
            }
        }
        
        Map<String, String> argsMap = GameManager.generateArgsMap(args);
        argsMap.put("SettingFilePath", "settings/TowerDefenceSettings.x8l");
        GameManager gameManager = new GameManager(argsMap);
        gameManager.startup();
    }
}
