package bulletdrift.spawning;

import bulletdrift.entities.PowerUps;

import java.util.ArrayList;
import java.util.Random;

public class PowerUpSpawner {
    private static final int MAX_POWER_UPS = 3;
    private static final int POWER_UP_SPAWN_CHANCE = 500;
    private static final int POWER_UP_SPAWN_MARGIN = 50;
    private static final int POWER_UP_TOP_MARGIN = 220;

    private static final String[] POWER_UP_TYPES = {
        PowerUps.TYPE_LIFE,
        PowerUps.TYPE_HEALING,
        PowerUps.TYPE_SHIELD,
        PowerUps.TYPE_RAPID_FIRE,
        PowerUps.TYPE_INVULNERABILITY,
        PowerUps.TYPE_SPEED,
        PowerUps.TYPE_BOMB,
        PowerUps.TYPE_BOMB_SHOT,
        PowerUps.TYPE_FIRE_SHOT,
        PowerUps.TYPE_COIN,
        PowerUps.TYPE_MEGA_MUSH,
        PowerUps.TYPE_MYSTERY_BOX,
        PowerUps.TYPE_MAGNET
    };

    private Random rand;

    public PowerUpSpawner(Random rand) {
        this.rand = rand;
    }

    public void generatePowerUp(ArrayList<PowerUps> powerUps, int panelWidth, int panelHeight) {
        if (panelWidth <= POWER_UP_SPAWN_MARGIN || panelHeight <= POWER_UP_TOP_MARGIN + POWER_UP_SPAWN_MARGIN) return;
        if (powerUps.size() >= MAX_POWER_UPS || rand.nextInt(POWER_UP_SPAWN_CHANCE) != 0) return;

        int x = rand.nextInt(panelWidth - POWER_UP_SPAWN_MARGIN);
        int availableHeight = Math.max(1, panelHeight - POWER_UP_TOP_MARGIN - POWER_UP_SPAWN_MARGIN);
        int y = POWER_UP_TOP_MARGIN + rand.nextInt(availableHeight);
        String type = POWER_UP_TYPES[rand.nextInt(POWER_UP_TYPES.length)];

        powerUps.add(new PowerUps(x, y, type));
    }
}
