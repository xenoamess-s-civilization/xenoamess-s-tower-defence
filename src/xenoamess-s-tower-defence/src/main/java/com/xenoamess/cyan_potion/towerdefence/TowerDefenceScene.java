/**
 * Copyright (C) 2020 XenoAmess
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.xenoamess.cyan_potion.towerdefence;

import com.xenoamess.cyan_potion.base.GameManager;
import com.xenoamess.cyan_potion.base.GameWindow;
import com.xenoamess.cyan_potion.base.game_window_components.GameWindowComponentTreeNode;
import com.xenoamess.cyan_potion.base.io.input.key.Key;
import com.xenoamess.cyan_potion.base.io.input.mouse.MouseButtonEvent;
import com.xenoamess.cyan_potion.base.render.Model;
import com.xenoamess.cyan_potion.base.render.Texture;
import org.lwjgl.glfw.GLFW;
import com.xenoamess.cyan_potion.coordinate.AbstractEntityScene;
import com.xenoamess.cyan_potion.coordinate.entity.AbstractEntity;
import com.xenoamess.cyan_potion.coordinate.entity.StaticEntity;
import com.xenoamess.cyan_potion.towerdefence.entities.Enemy;
import com.xenoamess.cyan_potion.towerdefence.entities.Projectile;
import com.xenoamess.cyan_potion.towerdefence.entities.Tower;
import com.xenoamess.cyan_potion.towerdefence.game.MapGrid;
import com.xenoamess.cyan_potion.towerdefence.game.WaveManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import static com.xenoamess.cyan_potion.base.render.Texture.STRING_PURE_COLOR;

/**
 * Tower Defence game scene.
 *
 * @author XenoAmess
 * @version 0.167.3-SNAPSHOT
 */
@Slf4j
@Getter
@Setter
public class TowerDefenceScene extends AbstractEntityScene {

    private final GameManager gameManager;
    private final MapGrid mapGrid;
    private final WaveManager waveManager;

    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Tower> towers = new ArrayList<>();
    private final List<Projectile> projectiles = new ArrayList<>();

    private int playerHealth = 20;
    private int playerMoney = 100;
    private int score = 0;
    private boolean gameOver = false;
    private boolean paused = false;

    private Tower.TowerType selectedTowerType = Tower.TowerType.BASIC;
    private int[] hoverGridPos = null;

    private static final Model MODEL = Model.COMMON_MODEL;
    private static final Vector4f COLOR_PATH = new Vector4f(0.6f, 0.4f, 0.2f, 1f);
    private static final Vector4f COLOR_EMPTY = new Vector4f(0.2f, 0.6f, 0.2f, 1f);
    private static final Vector4f COLOR_START = new Vector4f(0f, 1f, 0f, 1f);
    private static final Vector4f COLOR_END = new Vector4f(1f, 0f, 0f, 1f);
    private static final Vector4f COLOR_TOWER = new Vector4f(0.3f, 0.3f, 0.8f, 1f);
    private static final Vector4f COLOR_HOVER = new Vector4f(1f, 1f, 1f, 0.3f);
    private static final Vector4f COLOR_ENEMY = new Vector4f(1f, 0f, 0f, 1f);
    private static final Vector4f COLOR_PROJECTILE = new Vector4f(1f, 1f, 0f, 1f);

    /**
     * Constructor for TowerDefenceScene.
     *
     * @param gameWindow the game window
     */
    public TowerDefenceScene(GameWindow gameWindow) {
        super(gameWindow);
        this.gameManager = gameWindow.getGameManager();
        this.mapGrid = new MapGrid(12, 8);
        this.waveManager = new WaveManager(this);

        // Generate a default path
        this.mapGrid.generateDefaultPath();
        this.getCamera().setPosX(gameWindow.getWidth() / 2);
        this.getCamera().setPosY(gameWindow.getHeight() / 2);
    }

    @Override
    public TowerDefenceScene init(float posX, float posY, float width, float height) {
        super.init(posX, posY, width, height);
        log.info("TowerDefenceScene initialized");
        return this;
    }

    @Override
    protected void initProcessors() {
        super.initProcessors();
    }

    @Override
    public void addToGameWindowComponentTree(GameWindowComponentTreeNode gameWindowComponentTreeNode) {
        super.addToGameWindowComponentTree(gameWindowComponentTreeNode);
    }

    @Override
    public boolean update() {
        super.update();
        if (gameOver || paused) {
            return true;
        }

        // Handle input
        handleInput();

        // Update wave manager
        waveManager.update();

        // Update all enemies
        for (Enemy enemy : enemies) {
            enemy.update();
        }

        // Update all towers
        for (Tower tower : towers) {
            tower.update();
        }

        // Update all projectiles
        Iterator<Projectile> projectileIter = projectiles.iterator();
        while (projectileIter.hasNext()) {
            Projectile projectile = projectileIter.next();
            projectile.update();
            if (projectile.isDestroyed()) {
                projectileIter.remove();
                this.getDynamicEntityList().remove(projectile);
            }
        }

        // Remove dead enemies
        Iterator<Enemy> enemyIter = enemies.iterator();
        while (enemyIter.hasNext()) {
            Enemy enemy = enemyIter.next();
            if (enemy.isDead()) {
                if (enemy.isReachedEnd()) {
                    playerHealth--;
                    log.info("Enemy reached end! Health: {}", playerHealth);
                    if (playerHealth <= 0) {
                        gameOver = true;
                        log.info("Game Over!");
                    }
                } else {
                    playerMoney += enemy.getReward();
                    score += enemy.getReward() * 10;
                    log.info("Enemy killed! Money: {}, Score: {}", playerMoney, score);
                }
                enemyIter.remove();
                this.getDynamicEntityList().remove(enemy);
            }
        }

        return true;
    }

    /**
     * TODO 改成事件监听机制
     * <p>
     * Handle user input.
     */
    private void handleInput() {
        // Handle tower selection keys
        if (gameManager.getKeymap().isKeyDown(new Key(GLFW.GLFW_KEY_1))) {
            selectedTowerType = Tower.TowerType.BASIC;
            log.info("Selected tower: BASIC");
        }
        if (gameManager.getKeymap().isKeyDown(new Key(GLFW.GLFW_KEY_2))) {
            selectedTowerType = Tower.TowerType.SNIPER;
            log.info("Selected tower: SNIPER");
        }
        if (gameManager.getKeymap().isKeyDown(new Key(GLFW.GLFW_KEY_3))) {
            selectedTowerType = Tower.TowerType.RAPID;
            log.info("Selected tower: RAPID");
        }
        if (gameManager.getKeymap().isKeyDown(new Key(GLFW.GLFW_KEY_4))) {
            selectedTowerType = Tower.TowerType.SPLASH;
            log.info("Selected tower: SPLASH");
        }

        // Handle pause
        if (gameManager.getKeymap().isKeyDown(new Key(GLFW.GLFW_KEY_P))) {
            paused = !paused;
            log.info(paused ? "Game paused" : "Game resumed");
        }

        // Handle restart
        if (gameManager.getKeymap().isKeyDown(new Key(GLFW.GLFW_KEY_R))) {
            restart();
        }
    }

    @Override
    public MouseButtonEvent processMouseButtonEventsInside(MouseButtonEvent mouseButtonEvent) {
        if (mouseButtonEvent == null) {
            return null;
        }

        if (mouseButtonEvent.getKey() == GLFW.GLFW_MOUSE_BUTTON_LEFT &&
                mouseButtonEvent.getAction() == GLFW.GLFW_PRESS) {

            // Get mouse position in world coordinates
            float mouseX = mouseButtonEvent.getMousePosX();
            float mouseY = mouseButtonEvent.getMousePosY();

            // Convert to grid coordinates
            int gridX = mapGrid.worldToGridX(mouseX);
            int gridY = mapGrid.worldToGridY(mouseY);

            // Try to build tower
            if (tryBuildTower(gridX, gridY, selectedTowerType)) {
                log.info("Built tower at ({}, {})", gridX, gridY);
            }
        }

        return mouseButtonEvent;
    }

    @Override
    public boolean draw() {
        super.draw();
        // Draw grid
        drawGrid();

        // Draw towers
        for (Tower tower : towers) {
            drawTower(tower);
        }

        // Draw enemies
        for (Enemy enemy : enemies) {
            drawEnemy(enemy);
        }

        // Draw projectiles
        for (Projectile projectile : projectiles) {
            drawProjectile(projectile);
        }

        // Draw UI
        drawUI();

        return true;
    }

    /**
     * Draw the game grid.
     */
    private void drawGrid() {
        float cellSize = mapGrid.getCellSize();
        float offsetX = 50f;
        float offsetY = 50f;

        for (int x = 0; x < mapGrid.getWidth(); x++) {
            for (int y = 0; y < mapGrid.getHeight(); y++) {
                float worldX = mapGrid.gridToWorldX(x);
                float worldY = mapGrid.gridToWorldY(y);

                MapGrid.CellType cellType = mapGrid.getCell(x, y);
                Vector4f color;

                switch (cellType) {
                    case PATH:
                        color = COLOR_PATH;
                        break;
                    case START:
                        color = COLOR_START;
                        break;
                    case END:
                        color = COLOR_END;
                        break;
                    case TOWER:
                        color = COLOR_TOWER;
                        break;
                    case EMPTY:
                    default:
                        color = COLOR_EMPTY;
                        break;
                }

                // Draw cell background
                drawRect(worldX - cellSize / 2, worldY - cellSize / 2, cellSize, cellSize, color);
            }
        }

        // Draw hover effect
        if (hoverGridPos != null) {
            float hoverX = mapGrid.gridToWorldX(hoverGridPos[0]);
            float hoverY = mapGrid.gridToWorldY(hoverGridPos[1]);
            drawRect(hoverX - cellSize / 2, hoverY - cellSize / 2, cellSize, cellSize, COLOR_HOVER);
        }
    }

    /**
     * Draw a tower.
     */
    private void drawTower(Tower tower) {
        float x = tower.getCenterPosX();
        float y = tower.getCenterPosY();
        float width = tower.getWidth();
        float height = tower.getHeight();

        // Draw tower body
        Vector4f color = getTowerColor(tower.getTowerType());
        drawRect(x - width / 2, y - height / 2, width, height, color);

        // Draw range indicator (semi-transparent)
        float range = tower.getRange();
        drawCircleOutline(x, y, range, new Vector4f(1f, 1f, 1f, 0.2f));
    }

    /**
     * Draw an enemy.
     */
    private void drawEnemy(Enemy enemy) {
        float x = enemy.getCenterPosX();
        float y = enemy.getCenterPosY();
        float width = enemy.getWidth();
        float height = enemy.getHeight();

        // Draw enemy body
        Vector4f color = getEnemyColor(enemy.getEnemyType());
        drawRect(x - width / 2, y - height / 2, width, height, color);

        // Draw health bar
        float healthPercent = enemy.getHealthPercent();
        float barWidth = width;
        float barHeight = 4;
        float barY = y - height / 2 - barHeight - 2;

        // Health bar background (red)
        drawRect(x - barWidth / 2, barY, barWidth, barHeight, new Vector4f(1f, 0f, 0f, 1f));
        // Health bar fill (green)
        drawRect(x - barWidth / 2, barY, barWidth * healthPercent, barHeight, new Vector4f(0f, 1f, 0f, 1f));
    }

    /**
     * Draw a projectile.
     */
    private void drawProjectile(Projectile projectile) {
        float x = projectile.getCenterPosX();
        float y = projectile.getCenterPosY();
        float width = projectile.getWidth();
        float height = projectile.getHeight();

        drawRect(x - width / 2, y - height / 2, width, height, COLOR_PROJECTILE);
    }

    /**
     * Draw the UI.
     */
    private void drawUI() {
        // Draw game status
        // TODO: Add text rendering for health, money, score, wave
    }

    private Texture createTexture(String color) {
        return this.getResourceManager().fetchResource(
                Texture.class,
                STRING_PURE_COLOR,
                "",
                color
        );
    }

    /**
     * Draw a rectangle.
     */
    private void drawRect(float x, float y, float width, float height, Vector4f color) {
        Texture texture = createTexture(color.x + "," + color.y + "," + color.z + "," + color.w);
        this.drawBindableAbsolute(
                this.getCamera(),
                1f,
                texture,
                x + width / 2,
                y + height / 2,
                width,
                height
        );
    }

    /**
     * Draw a circle outline.
     */
    private void drawCircleOutline(float centerX, float centerY, float radius, Vector4f color) {
        // Simplified circle drawing using multiple lines
        int segments = 32;
        for (int i = 0; i < segments; i++) {
            float angle1 = (float) (2 * Math.PI * i / segments);
            float angle2 = (float) (2 * Math.PI * (i + 1) / segments);

            float x1 = centerX + (float) Math.cos(angle1) * radius;
            float y1 = centerY + (float) Math.sin(angle1) * radius;
            float x2 = centerX + (float) Math.cos(angle2) * radius;
            float y2 = centerY + (float) Math.sin(angle2) * radius;

            // Draw line (simplified as thin rectangle)
            float dx = x2 - x1;
            float dy = y2 - y1;
            float len = (float) Math.sqrt(dx * dx + dy * dy);
            float angle = (float) Math.atan2(dy, dx);

            // This is a simplified approach - in a real implementation you'd use line drawing
            drawRect(x1, y1, len, 1, color);
        }
    }

    /**
     * Get color for tower type.
     */
    private Vector4f getTowerColor(Tower.TowerType type) {
        int colorInt = type.getColor();
        return intToVector4f(colorInt);
    }

    /**
     * Get color for enemy type.
     */
    private Vector4f getEnemyColor(Enemy.EnemyType type) {
        int colorInt = type.getColor();
        return intToVector4f(colorInt);
    }

    /**
     * Convert int color to Vector4f.
     */
    private Vector4f intToVector4f(int color) {
        float r = ((color >> 24) & 0xFF) / 255f;
        float g = ((color >> 16) & 0xFF) / 255f;
        float b = ((color >> 8) & 0xFF) / 255f;
        float a = (color & 0xFF) / 255f;
        return new Vector4f(r, g, b, a);
    }

    /**
     * Add an enemy to the scene.
     *
     * @param enemy the enemy to add
     */
    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
        this.getDynamicEntityList().add(enemy);
    }

    /**
     * Add a tower to the scene.
     *
     * @param tower the tower to add
     */
    public void addTower(Tower tower) {
        towers.add(tower);
        this.getStaticEntitySetList().add(tower);
    }

    /**
     * Add a projectile to the scene.
     *
     * @param projectile the projectile to add
     */
    public void addProjectile(Projectile projectile) {
        projectiles.add(projectile);
        this.getDynamicEntityList().add(projectile);
    }

    /**
     * Try to build a tower at the specified grid position.
     *
     * @param gridX the grid x coordinate
     * @param gridY the grid y coordinate
     * @param towerType the tower type
     * @return true if tower was built successfully
     */
    public boolean tryBuildTower(int gridX, int gridY, Tower.TowerType towerType) {
        if (!mapGrid.canBuild(gridX, gridY)) {
            return false;
        }

        int cost = towerType.getCost();
        if (playerMoney < cost) {
            return false;
        }

        // Create tower
        float worldX = mapGrid.gridToWorldX(gridX);
        float worldY = mapGrid.gridToWorldY(gridY);
        float cellSize = mapGrid.getCellSize();

        Tower tower = new Tower(this, worldX, worldY, cellSize * 0.8f, cellSize * 0.8f, towerType);
        addTower(tower);
        mapGrid.setTower(gridX, gridY, tower);

        playerMoney -= cost;
        log.info("Tower built at ({}, {}), remaining money: {}", gridX, gridY, playerMoney);
        return true;
    }

    /**
     * Get all enemies within range of a position.
     *
     * @param centerX the center x position
     * @param centerY the center y position
     * @param range the range
     * @return list of enemies in range
     */
    public List<Enemy> getEnemiesInRange(float centerX, float centerY, float range) {
        List<Enemy> result = new ArrayList<>();
        float rangeSquared = range * range;

        for (Enemy enemy : enemies) {
            if (!enemy.isDead()) {
                float dx = enemy.getCenterPosX() - centerX;
                float dy = enemy.getCenterPosY() - centerY;
                if (dx * dx + dy * dy <= rangeSquared) {
                    result.add(enemy);
                }
            }
        }
        return result;
    }

    /**
     * Get the closest enemy to a position.
     *
     * @param centerX the center x position
     * @param centerY the center y position
     * @param range the maximum range
     * @return the closest enemy, or null if none in range
     */
    public Enemy getClosestEnemy(float centerX, float centerY, float range) {
        Enemy closest = null;
        float closestDistSquared = range * range;

        for (Enemy enemy : enemies) {
            if (!enemy.isDead()) {
                float dx = enemy.getCenterPosX() - centerX;
                float dy = enemy.getCenterPosY() - centerY;
                float distSquared = dx * dx + dy * dy;
                if (distSquared <= closestDistSquared) {
                    closestDistSquared = distSquared;
                    closest = enemy;
                }
            }
        }
        return closest;
    }

    /**
     * Restart the game.
     */
    public void restart() {
        playerHealth = 20;
        playerMoney = 100;
        score = 0;
        gameOver = false;
        paused = false;

        // Clear all entities
        enemies.clear();
        towers.clear();
        projectiles.clear();
        this.getDynamicEntityList().clear();
        this.getStaticEntitySetList().clear();

        // Reset map and wave manager
        mapGrid.reset();
        mapGrid.generateDefaultPath();
        waveManager.reset();

        log.info("Game restarted");
    }
}
