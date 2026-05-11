package bulletdrift.systems;

import bulletdrift.entities.Enemy;
import bulletdrift.entities.Player;
import bulletdrift.entities.PowerUp;

import java.util.ArrayList;

public class MovementSystem {
    public void updateEnemies(ArrayList<Enemy> enemies, int panelHeight) {
        for (Enemy enemy : enemies) {
            enemy.moveDownEnemy(panelHeight);
        }
        enemies.removeIf(enemy -> enemy.getY() > panelHeight);
    }

    public void updatePowerUps(ArrayList<PowerUp> powerUps, Player player, int panelWidth, int panelHeight) {
        if (!player.isMagnetActive()) return;

        for (PowerUp powerUp : powerUps) {
            powerUp.moveToward(player.getCenterX(), player.getCenterY(), panelWidth, panelHeight);
        }
    }
}
