package Files;

import java.util.ArrayList;
import java.util.Random;

public class EnemySpawner {
    private static final int BASE_MAX_ENEMIES = 6;
    private static final int MAX_ENEMIES_LIMIT = 40;
    private static final int ENEMIES_PER_WAVE = 4;
    private static final int ENEMY_SPAWN_MARGIN = 50;
    private static final int ENEMY_START_Y = -50;
    private static final int BASE_MIN_ENEMY_SPEED = 2;
    private static final int BASE_ENEMY_SPEED_VARIATION = 4;
    private static final int MAX_ENEMY_SPEED_LIMIT = 22;
    private static final int BASE_ENEMY_SPAWN_CHANCE = 55;
    private static final int MIN_ENEMY_SPAWN_CHANCE = 6;
    private static final int ENEMY_SPAWN_CHANCE_REDUCTION_PER_WAVE = 7;
    private static final int FAST_ENEMY_START_WAVE = 2;
    private static final int FAST_ENEMY_BASE_CHANCE_PERCENT = 20;
    private static final int FAST_ENEMY_CHANCE_PER_WAVE_PERCENT = 8;
    private static final int FAST_ENEMY_MAX_CHANCE_PERCENT = 65;
    private static final int FAST_ENEMY_SPEED_BONUS = 4;
    private static final int TANK_ENEMY_START_WAVE = 4;
    private static final int TANK_ENEMY_BASE_CHANCE_PERCENT = 12;
    private static final int TANK_ENEMY_CHANCE_PER_WAVE_PERCENT = 5;
    private static final int TANK_ENEMY_MAX_CHANCE_PERCENT = 35;
    private static final int TANK_ENEMY_SPEED_PENALTY = 3;

    private Random rand;

    public EnemySpawner(Random rand) {
        this.rand = rand;
    }

    public void generateEnemy(ArrayList<Enemy> enemies, int panelWidth, int wave) {
        if (panelWidth <= ENEMY_SPAWN_MARGIN) return;

        int maxEnemies = getMaxEnemies(wave);
        int spawnChance = getSpawnChance(wave);
        if (enemies.size() >= maxEnemies || rand.nextInt(spawnChance) != 0) return;

        int x = rand.nextInt(panelWidth - ENEMY_SPAWN_MARGIN);
        int speed = getEnemySpeed(wave);
        Enemy.Type enemyType = getEnemyType(wave);
        if (enemyType == Enemy.Type.FAST) {
            speed = Math.min(MAX_ENEMY_SPEED_LIMIT, speed + FAST_ENEMY_SPEED_BONUS);
        } else if (enemyType == Enemy.Type.TANK) {
            speed = Math.max(1, speed - TANK_ENEMY_SPEED_PENALTY);
        }

        enemies.add(new Enemy(x, ENEMY_START_Y, speed, enemyType));
    }

    private int getMaxEnemies(int wave) {
        return Math.min(MAX_ENEMIES_LIMIT, BASE_MAX_ENEMIES + wave * ENEMIES_PER_WAVE);
    }

    private int getSpawnChance(int wave) {
        return Math.max(MIN_ENEMY_SPAWN_CHANCE, BASE_ENEMY_SPAWN_CHANCE - wave * ENEMY_SPAWN_CHANCE_REDUCTION_PER_WAVE);
    }

    private int getEnemySpeed(int wave) {
        int minSpeed = Math.min(MAX_ENEMY_SPEED_LIMIT - 1, BASE_MIN_ENEMY_SPEED + wave);
        int speedVariation = BASE_ENEMY_SPEED_VARIATION + wave * 2;
        return Math.min(MAX_ENEMY_SPEED_LIMIT, rand.nextInt(speedVariation) + minSpeed);
    }

    private Enemy.Type getEnemyType(int wave) {
        if (wave < FAST_ENEMY_START_WAVE) return Enemy.Type.NORMAL;

        if (wave >= TANK_ENEMY_START_WAVE) {
            int tankChance = Math.min(
                TANK_ENEMY_MAX_CHANCE_PERCENT,
                TANK_ENEMY_BASE_CHANCE_PERCENT + (wave - TANK_ENEMY_START_WAVE) * TANK_ENEMY_CHANCE_PER_WAVE_PERCENT
            );
            if (rand.nextInt(100) < tankChance) return Enemy.Type.TANK;
        }

        int fastChance = Math.min(
            FAST_ENEMY_MAX_CHANCE_PERCENT,
            FAST_ENEMY_BASE_CHANCE_PERCENT + (wave - FAST_ENEMY_START_WAVE) * FAST_ENEMY_CHANCE_PER_WAVE_PERCENT
        );
        return rand.nextInt(100) < fastChance ? Enemy.Type.FAST : Enemy.Type.NORMAL;
    }
}
