package bulletdrift.spawning;

import bulletdrift.entities.PowerUp;

import java.util.ArrayList;
import java.util.Random;

public class PowerUpSpawner {
    private static final int BASE_MAX_POWER_UPS = 3;
    private static final int MAX_POWER_UPS_LIMIT = 7;
    private static final int BASE_POWER_UP_SPAWN_CHANCE = 280;
    private static final int MIN_POWER_UP_SPAWN_CHANCE = 110;
    private static final int BOSS_FIGHT_MAX_POWER_UPS = 2;
    private static final int BOSS_FIGHT_POWER_UP_SPAWN_CHANCE = 360;
    private static final int POWER_UP_SPAWN_CHANCE_REDUCTION_PER_WAVE = 20;
    private static final int POWER_UP_SPAWN_MARGIN = 50;
    private static final int POWER_UP_TOP_MARGIN = 220;
    private static final int PADLOCK_SPAWN_CHANCE = 160;
    private static final int FIRST_PADLOCK_CHANCE_PERCENT = 60;
    private static final int SECOND_PADLOCK_CHANCE_PERCENT = 30;

    private static final WeightedPowerUp[] POWER_UP_TABLE = {
        new WeightedPowerUp(PowerUp.TYPE_HEALING, 25),
        new WeightedPowerUp(PowerUp.TYPE_SPEED, 18),
        new WeightedPowerUp(PowerUp.TYPE_RAPID_FIRE, 14),
        new WeightedPowerUp(PowerUp.TYPE_SHIELD, 13),
        new WeightedPowerUp(PowerUp.TYPE_MAGNET, 10),
        new WeightedPowerUp(PowerUp.TYPE_BOMB_SHOT, 8),
        new WeightedPowerUp(PowerUp.TYPE_FIRE_SHOT, 6),
        new WeightedPowerUp(PowerUp.TYPE_INVULNERABILITY, 3),
        new WeightedPowerUp(PowerUp.TYPE_LIFE, 3),
        new WeightedPowerUp(PowerUp.TYPE_MYSTERY_BOX, 1),
        new WeightedPowerUp(PowerUp.TYPE_MEGA_MUSH, 1)
    };
    private static final int TOTAL_POWER_UP_WEIGHT = calculateTotalPowerUpWeight();

    private Random rand;
    private boolean previousKeyDefendable;
    private int availablePadlocks;
    private int spawnedPadlocks;

    public PowerUpSpawner(Random rand) {
        this.rand = rand;
    }

    public void generatePowerUp(ArrayList<PowerUp> powerUps, int panelWidth, int panelHeight, int wave, boolean keyDefendable, boolean bossFight) {
        if (panelWidth <= POWER_UP_SPAWN_MARGIN || panelHeight <= POWER_UP_TOP_MARGIN + POWER_UP_SPAWN_MARGIN) return;

        updatePadlockAvailability(keyDefendable);
        if (!bossFight && keyDefendable && tryGeneratePadlock(powerUps, panelWidth, panelHeight)) return;

        if (powerUps.size() >= getMaxPowerUps(wave, bossFight) || rand.nextInt(getSpawnChance(wave, bossFight)) != 0) return;

        addPowerUp(powerUps, panelWidth, panelHeight, getRandomPowerUpType());
    }

    private boolean tryGeneratePadlock(ArrayList<PowerUp> powerUps, int panelWidth, int panelHeight) {
        if (spawnedPadlocks >= availablePadlocks || hasPadlockOnScreen(powerUps)) return false;
        if (powerUps.size() >= MAX_POWER_UPS_LIMIT || rand.nextInt(PADLOCK_SPAWN_CHANCE) != 0) return false;

        addPowerUp(powerUps, panelWidth, panelHeight, PowerUp.TYPE_PADLOCK);
        spawnedPadlocks++;
        return true;
    }

    private void updatePadlockAvailability(boolean keyDefendable) {
        if (keyDefendable && !previousKeyDefendable) {
            availablePadlocks = 0;
            spawnedPadlocks = 0;
            if (rand.nextInt(100) < FIRST_PADLOCK_CHANCE_PERCENT) availablePadlocks++;
            if (rand.nextInt(100) < SECOND_PADLOCK_CHANCE_PERCENT) availablePadlocks++;
        }
        previousKeyDefendable = keyDefendable;
    }

    private boolean hasPadlockOnScreen(ArrayList<PowerUp> powerUps) {
        for (PowerUp powerUp : powerUps) {
            if (PowerUp.TYPE_PADLOCK.equals(powerUp.getType())) return true;
        }
        return false;
    }

    private void addPowerUp(ArrayList<PowerUp> powerUps, int panelWidth, int panelHeight, String type) {
        int x = rand.nextInt(panelWidth - POWER_UP_SPAWN_MARGIN);
        int availableHeight = Math.max(1, panelHeight - POWER_UP_TOP_MARGIN - POWER_UP_SPAWN_MARGIN);
        int y = POWER_UP_TOP_MARGIN + rand.nextInt(availableHeight);
        powerUps.add(new PowerUp(x, y, type));
    }

    private int getMaxPowerUps(int wave, boolean bossFight) {
        if (bossFight) return BOSS_FIGHT_MAX_POWER_UPS;
        return Math.min(MAX_POWER_UPS_LIMIT, BASE_MAX_POWER_UPS + wave / 3);
    }

    private int getSpawnChance(int wave, boolean bossFight) {
        if (bossFight) return BOSS_FIGHT_POWER_UP_SPAWN_CHANCE;
        return Math.max(
            MIN_POWER_UP_SPAWN_CHANCE,
            BASE_POWER_UP_SPAWN_CHANCE - wave * POWER_UP_SPAWN_CHANCE_REDUCTION_PER_WAVE
        );
    }

    private String getRandomPowerUpType() {
        int roll = rand.nextInt(TOTAL_POWER_UP_WEIGHT);
        int accumulatedWeight = 0;

        for (WeightedPowerUp powerUp : POWER_UP_TABLE) {
            accumulatedWeight += powerUp.weight;
            if (roll < accumulatedWeight) {
                return powerUp.type;
            }
        }

        return PowerUp.TYPE_HEALING;
    }

    private static int calculateTotalPowerUpWeight() {
        int totalWeight = 0;
        for (WeightedPowerUp powerUp : POWER_UP_TABLE) {
            totalWeight += powerUp.weight;
        }
        return totalWeight;
    }

    private static class WeightedPowerUp {
        private String type;
        private int weight;

        private WeightedPowerUp(String type, int weight) {
            this.type = type;
            this.weight = weight;
        }
    }
}
