package bulletdrift.systems;

import bulletdrift.core.GameSession;
import bulletdrift.entities.Enemy;
import bulletdrift.entities.Player;
import bulletdrift.entities.PowerUp;
import bulletdrift.spawning.EnemySpawner;
import bulletdrift.spawning.PowerUpSpawner;

import java.util.ArrayList;

public class GameUpdateSystem {
    private EnemySpawner enemySpawner;
    private PowerUpSpawner powerUpSpawner;
    private MovementSystem movementSystem;
    private CollisionManager collisionManager;

    public GameUpdateSystem(
        EnemySpawner enemySpawner,
        PowerUpSpawner powerUpSpawner,
        MovementSystem movementSystem,
        CollisionManager collisionManager
    ) {
        this.enemySpawner = enemySpawner;
        this.powerUpSpawner = powerUpSpawner;
        this.movementSystem = movementSystem;
        this.collisionManager = collisionManager;
    }

    public boolean update(
        Player player,
        ArrayList<Enemy> enemies,
        ArrayList<PowerUp> powerUps,
        GameSession session,
        int panelWidth,
        int panelHeight,
        long damageInvulnerabilityMs
    ) {
        enemySpawner.generateEnemy(enemies, panelWidth, session.getWave());
        powerUpSpawner.generatePowerUp(powerUps, panelWidth, panelHeight);
        movementSystem.updateEnemies(enemies, panelHeight);
        movementSystem.updatePowerUps(powerUps, player, panelWidth, panelHeight);
        player.updateProjectiles(panelWidth, panelHeight);

        CollisionManager.CollisionResult result = collisionManager.checkCollisions(
            player,
            enemies,
            powerUps,
            panelWidth,
            panelHeight,
            damageInvulnerabilityMs
        );

        if (result.isPlayerLifeLost()) {
            return true;
        }

        applyCollisionResult(result, session);
        return false;
    }

    private void applyCollisionResult(CollisionManager.CollisionResult result, GameSession session) {
        if (result.getScoreToAdd() > 0) {
            session.addScore(result.getScoreToAdd());
        }

        if (result.getCoinsToAdd() > 0) {
            session.addCoins(result.getCoinsToAdd());
        }

        if (result.hasFeedback()) {
            session.showPowerUpFeedback(result.getFeedbackText(), result.getFeedbackColor());
        }
    }
}
