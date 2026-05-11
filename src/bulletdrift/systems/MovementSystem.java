package bulletdrift.systems;

import bulletdrift.entities.Enemy;
import bulletdrift.entities.KeyObjective;
import bulletdrift.entities.Player;
import bulletdrift.entities.PowerUp;

import java.util.ArrayList;

public class MovementSystem {
    public void updateEnemies(ArrayList<Enemy> enemies, KeyObjective keyObjective, int panelWidth, int panelHeight) {
        for (Enemy enemy : enemies) {
            if (enemy.getType() == Enemy.Type.KEY_HUNTER && keyObjective != null) {
                enemy.moveToward(keyObjective.getCenterX(panelWidth, panelHeight), keyObjective.getCenterY(panelWidth, panelHeight), panelWidth, panelHeight);
            } else {
                enemy.moveDownEnemy(panelHeight);
            }
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
