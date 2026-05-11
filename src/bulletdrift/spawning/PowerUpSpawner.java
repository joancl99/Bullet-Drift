package bulletdrift.spawning;

import bulletdrift.entities.PowerUp;

import java.util.ArrayList;
import java.util.Random;

public class PowerUpSpawner {
    private static final int MAX_POWER_UPS = 3;
    private static final int POWER_UP_SPAWN_CHANCE = 500;
    private static final int POWER_UP_SPAWN_MARGIN = 50;
    private static final int POWER_UP_TOP_MARGIN = 220;

    private static final String[] POWER_UP_TYPES = {
        PowerUp.TYPE_LIFE,
        PowerUp.TYPE_HEALING,
        PowerUp.TYPE_SHIELD,
        PowerUp.TYPE_RAPID_FIRE,
        PowerUp.TYPE_INVULNERABILITY,
        PowerUp.TYPE_SPEED,
        PowerUp.TYPE_BOMB,
        PowerUp.TYPE_BOMB_SHOT,
        PowerUp.TYPE_FIRE_SHOT,
        PowerUp.TYPE_COIN,
        PowerUp.TYPE_MEGA_MUSH,
        PowerUp.TYPE_MYSTERY_BOX,
        PowerUp.TYPE_MAGNET
    };

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
        String type = POWER_UP_TYPES[rand.nextInt(POWER_UP_TYPES.length)];

        powerUps.add(new PowerUp(x, y, type));
    }
}
