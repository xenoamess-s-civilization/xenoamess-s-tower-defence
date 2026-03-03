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

import com.xenoamess.cyan_potion.towerdefence.entities.Tower;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Grid-based map for tower defence.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Getter
@Setter
public class MapGrid {

    public enum CellType {
        EMPTY,      // Can build towers
        PATH,       // Enemy path
        TOWER,      // Has tower
        START,      // Enemy spawn point
        END         // Enemy goal
    }

    private final int width;
    private final int height;
    private final float cellSize = 48f;
    private final float offsetX = 50f;
    private final float offsetY = 50f;
    
    private CellType[][] grid;
    private Tower[][] towers;
    private List<int[]> path;
    private int[] startPos;
    private int[] endPos;

    /**
     * Constructor for MapGrid.
     *
     * @param width the grid width
     * @param height the grid height
     */
    public MapGrid(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new CellType[width][height];
        this.towers = new Tower[width][height];
        this.path = new ArrayList<>();
        
        // Initialize grid
        reset();
    }

    /**
     * Reset the grid to empty state.
     */
    public void reset() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                grid[x][y] = CellType.EMPTY;
                towers[x][y] = null;
            }
        }
        path.clear();
    }

    /**
     * Generate a default path.
     */
    public void generateDefaultPath() {
        reset();
        
        // Create a simple winding path
        startPos = new int[]{0, 4};
        endPos = new int[]{width - 1, 4};
        
        // Horizontal path with some turns
        int[][] waypoints = {
            {0, 4},
            {3, 4},
            {3, 2},
            {6, 2},
            {6, 6},
            {9, 6},
            {9, 4},
            {11, 4}
        };
        
        // Build path between waypoints
        int currentX = waypoints[0][0];
        int currentY = waypoints[0][1];
        grid[currentX][currentY] = CellType.START;
        path.add(new int[]{currentX, currentY});
        
        for (int i = 1; i < waypoints.length; i++) {
            int targetX = waypoints[i][0];
            int targetY = waypoints[i][1];
            
            // Move horizontally
            while (currentX != targetX) {
                currentX += (targetX > currentX) ? 1 : -1;
                grid[currentX][currentY] = CellType.PATH;
                path.add(new int[]{currentX, currentY});
            }
            
            // Move vertically
            while (currentY != targetY) {
                currentY += (targetY > currentY) ? 1 : -1;
                grid[currentX][currentY] = CellType.PATH;
                path.add(new int[]{currentX, currentY});
            }
        }
        
        // Mark end
        grid[currentX][currentY] = CellType.END;
        endPos = new int[]{currentX, currentY};
    }

    /**
     * Check if a cell can have a tower built on it.
     *
     * @param x the grid x coordinate
     * @param y the grid y coordinate
     * @return true if tower can be built
     */
    public boolean canBuild(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return false;
        }
        return grid[x][y] == CellType.EMPTY;
    }

    /**
     * Place a tower on the grid.
     *
     * @param x the grid x coordinate
     * @param y the grid y coordinate
     * @param tower the tower to place
     */
    public void setTower(int x, int y, Tower tower) {
        if (canBuild(x, y)) {
            grid[x][y] = CellType.TOWER;
            towers[x][y] = tower;
        }
    }

    /**
     * Convert grid x to world x.
     *
     * @param gridX the grid x coordinate
     * @return the world x coordinate
     */
    public float gridToWorldX(int gridX) {
        return offsetX + gridX * cellSize + cellSize / 2;
    }

    /**
     * Convert grid y to world y.
     *
     * @param gridY the grid y coordinate
     * @return the world y coordinate
     */
    public float gridToWorldY(int gridY) {
        return offsetY + gridY * cellSize + cellSize / 2;
    }

    /**
     * Convert world x to grid x.
     *
     * @param worldX the world x coordinate
     * @return the grid x coordinate
     */
    public int worldToGridX(float worldX) {
        return (int) ((worldX - offsetX) / cellSize);
    }

    /**
     * Convert world y to grid y.
     *
     * @param worldY the world y coordinate
     * @return the grid y coordinate
     */
    public int worldToGridY(float worldY) {
        return (int) ((worldY - offsetY) / cellSize);
    }

    /**
     * Get the cell type at a position.
     *
     * @param x the grid x coordinate
     * @param y the grid y coordinate
     * @return the cell type
     */
    public CellType getCell(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return null;
        }
        return grid[x][y];
    }

    /**
     * Get the path for enemies to follow.
     *
     * @return the path as a list of grid coordinates
     */
    public List<int[]> getPath() {
        return new ArrayList<>(path);
    }

    /**
     * Get the start position.
     *
     * @return the start position as [x, y]
     */
    public int[] getStartPos() {
        return startPos;
    }

    /**
     * Get the end position.
     *
     * @return the end position as [x, y]
     */
    public int[] getEndPos() {
        return endPos;
    }
}
