package bulletdrift.spawning;

import bulletdrift.entities.PowerUp;

import java.util.ArrayList;
import java.util.Random;

public class PowerUpSpawner {
    private static final int MAX_POWER_UPS = 3;
    private static final int POWER_UP_SPAWN_CHANCE = 500;
    private static final int POWER_UP_SPAWN_MARGIN = 50;
    private static final int POWER_UP_TOP_MARGIN = 220;

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

    public PowerUpSpawner(Random rand) {
        this.rand = rand;
    }

    public void generatePowerUp(ArrayList<PowerUp> powerUps, int panelWidth, int panelHeight) {
        if (panelWidth <= POWER_UP_SPAWN_MARGIN || panelHeight <= POWER_UP_TOP_MARGIN + POWER_UP_SPAWN_MARGIN) return;
        if (powerUps.size() >= MAX_POWER_UPS || rand.nextInt(POWER_UP_SPAWN_CHANCE) != 0) return;

        int x = rand.nextInt(panelWidth - POWER_UP_SPAWN_MARGIN);
        int availableHeight = Math.max(1, panelHeight - POWER_UP_TOP_MARGIN - POWER_UP_SPAWN_MARGIN);
        int y = POWER_UP_TOP_MARGIN + rand.nextInt(availableHeight);
        String type = getRandomPowerUpType();

        powerUps.add(new PowerUp(x, y, type));
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
