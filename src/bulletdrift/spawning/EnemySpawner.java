package bulletdrift.spawning;

import bulletdrift.entities.Enemy;

import java.util.ArrayList;
import java.util.Random;

public class EnemySpawner {
    private static final int BASE_MAX_ENEMIES = 8;
    private static final int MAX_ENEMIES_LIMIT = 40;
    private static final int ENEMIES_PER_WAVE = 5;
    private static final int ENEMY_SPAWN_MARGIN = 50;
    private static final int ENEMY_START_Y = -50;
    private static final int BASE_MIN_ENEMY_SPEED = 2;
    private static final int BASE_ENEMY_SPEED_VARIATION = 3;
    private static final int SPEED_WAVE_CAP = 9;
    private static final int MAX_ENEMY_SPEED_LIMIT = 22;
    private static final int BASE_ENEMY_SPAWN_CHANCE = 42;
    private static final int MIN_ENEMY_SPAWN_CHANCE = 5;
    private static final int ENEMY_SPAWN_CHANCE_REDUCTION_PER_WAVE = 6;
    private static final int FAST_ENEMY_START_WAVE = 1;
    private static final int FAST_ENEMY_BASE_CHANCE_PERCENT = 20;
    private static final int FAST_ENEMY_CHANCE_PER_WAVE_PERCENT = 8;
    private static final int FAST_ENEMY_MAX_CHANCE_PERCENT = 65;
    private static final int FAST_ENEMY_SPEED_BONUS = 4;
    private static final int ZIGZAG_ENEMY_START_WAVE = 3;
    private static final int ZIGZAG_ENEMY_CHANCE_PERCENT = 16;
    private static final int ZIGZAG_ENEMY_SPEED_BONUS = 1;
    private static final int CHASER_ENEMY_START_WAVE = 4;
    private static final int CHASER_ENEMY_CHANCE_PERCENT = 12;
    private static final int CHASER_ENEMY_MIN_SPEED = 4;
    private static final int CHASER_ENEMY_MAX_SPEED = 7;
    private static final int TANK_ENEMY_START_WAVE = 2;
    private static final int TANK_ENEMY_BASE_CHANCE_PERCENT = 12;
    private static final int TANK_ENEMY_CHANCE_PER_WAVE_PERCENT = 5;
    private static final int TANK_ENEMY_MAX_CHANCE_PERCENT = 35;
    private static final int TANK_ENEMY_SPEED_PENALTY = 3;
    private static final int KEY_HUNTER_START_WAVE = 6;
    private static final int KEY_HUNTER_CHANCE_PERCENT = 10;
    private static final int KEY_HUNTER_MIN_SPEED = 6;
    private static final int KEY_HUNTER_MAX_SPEED = 9;

    private Random rand;

    public EnemySpawner(Random rand) {
        this.rand = rand;
    }

    public void generateEnemy(ArrayList<Enemy> enemies, int panelWidth, int wave, boolean keyDefendable) {
        if (panelWidth <= ENEMY_SPAWN_MARGIN) return;

        int maxEnemies = getMaxEnemies(wave);
        int spawnChance = getSpawnChance(wave);
        if (enemies.size() >= maxEnemies || rand.nextInt(spawnChance) != 0) return;

        int speed = getEnemySpeed(wave);
        Enemy.Type enemyType = getEnemyType(wave, keyDefendable);
        int x = rand.nextInt(panelWidth - ENEMY_SPAWN_MARGIN);
        if (enemyType == Enemy.Type.FAST) {
            speed = Math.min(MAX_ENEMY_SPEED_LIMIT, speed + FAST_ENEMY_SPEED_BONUS);
        } else if (enemyType == Enemy.Type.TANK) {
            speed = Math.max(1, speed - TANK_ENEMY_SPEED_PENALTY);
        } else if (enemyType == Enemy.Type.ZIGZAG) {
            speed = Math.min(MAX_ENEMY_SPEED_LIMIT, speed + ZIGZAG_ENEMY_SPEED_BONUS);
        } else if (enemyType == Enemy.Type.CHASER) {
            speed = CHASER_ENEMY_MIN_SPEED + rand.nextInt(CHASER_ENEMY_MAX_SPEED - CHASER_ENEMY_MIN_SPEED + 1);
        } else if (enemyType == Enemy.Type.KEY_HUNTER) {
            speed = KEY_HUNTER_MIN_SPEED + rand.nextInt(KEY_HUNTER_MAX_SPEED - KEY_HUNTER_MIN_SPEED + 1);
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
        int cappedWave = Math.min(SPEED_WAVE_CAP, wave);
        int minSpeed = Math.min(MAX_ENEMY_SPEED_LIMIT - 1, BASE_MIN_ENEMY_SPEED + cappedWave / 3);
        int speedVariation = BASE_ENEMY_SPEED_VARIATION + cappedWave / 2;
        return Math.min(MAX_ENEMY_SPEED_LIMIT, rand.nextInt(speedVariation) + minSpeed);
    }

    private Enemy.Type getEnemyType(int wave, boolean keyDefendable) {
        if (keyDefendable && wave >= KEY_HUNTER_START_WAVE && rand.nextInt(100) < KEY_HUNTER_CHANCE_PERCENT) {
            return Enemy.Type.KEY_HUNTER;
        }

        if (wave >= CHASER_ENEMY_START_WAVE && rand.nextInt(100) < CHASER_ENEMY_CHANCE_PERCENT) {
            return Enemy.Type.CHASER;
        }

        if (wave >= ZIGZAG_ENEMY_START_WAVE && rand.nextInt(100) < ZIGZAG_ENEMY_CHANCE_PERCENT) {
            return Enemy.Type.ZIGZAG;
        }

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
