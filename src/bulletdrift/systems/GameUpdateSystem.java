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

    public UpdateResult update(
        Player player,
        ArrayList<Enemy> enemies,
        ArrayList<PowerUp> powerUps,
        GameSession session,
        int panelWidth,
        int panelHeight,
        long damageInvulnerabilityMs
    ) {
        session.updateFinalProgression(panelWidth, panelHeight);

        if (session.isPortalActive()) {
            enemies.clear();
            powerUps.clear();
        }

        if (session.shouldSpawnEnemies()) {
            enemySpawner.generateEnemy(enemies, panelWidth, session.getWave(), session.hasDefendableKey());
        }

        if (session.shouldSpawnPowerUps()) {
            powerUpSpawner.generatePowerUp(powerUps, panelWidth, panelHeight, session.getWave(), session.hasDefendableKey(), session.isBossActive());
        }

        movementSystem.updateEnemies(enemies, player, session.getKeyObjective(), panelWidth, panelHeight);
        movementSystem.updatePowerUps(powerUps, player, panelWidth, panelHeight);
        if (session.getBoss() != null) {
            session.getBoss().update(panelWidth, panelHeight);
        }
        player.updateProjectiles(panelWidth, panelHeight);

        CollisionManager.CollisionResult result = collisionManager.checkCollisions(
            player,
            enemies,
            powerUps,
            session.getKeyObjective(),
            session.getPortal(),
            session.getBoss(),
            session.isPortalActive(),
            session.hasKeyCollected(),
            panelWidth,
            panelHeight,
            damageInvulnerabilityMs
        );

        if (result.isPlayerLifeLost()) {
            return UpdateResult.playerLifeLost();
        }

        if (result.isKeyDestroyed()) {
            return UpdateResult.keyDestroyed();
        }

        applyCollisionResult(result, player, session, enemies, panelWidth, panelHeight);
        return UpdateResult.none();
    }

    private void applyCollisionResult(
        CollisionManager.CollisionResult result,
        Player player,
        GameSession session,
        ArrayList<Enemy> enemies,
        int panelWidth,
        int panelHeight
    ) {
        if (result.getScoreToAdd() > 0) {
            session.addScore(result.getScoreToAdd());
        }

        if (result.hasFeedback()) {
            session.showPowerUpFeedback(result.getFeedbackText(), result.getFeedbackColor());
        }

        if (session.consumeWaveChanged()) {
            enemies.clear();
        }

        if (result.isKeyCollected()) {
            session.collectKey();
        }

        if (result.isPortalUsed()) {
            session.usePortal(panelWidth, panelHeight);
            player.prepareForBossFight(panelWidth, panelHeight);
        }

        if (result.isBossDefeated()) {
            session.defeatBoss();
        }
    }

    public static class UpdateResult {
        private boolean playerLifeLost;
        private boolean keyDestroyed;

        private static UpdateResult none() {
            return new UpdateResult();
        }

        private static UpdateResult playerLifeLost() {
            UpdateResult result = new UpdateResult();
            result.playerLifeLost = true;
            return result;
        }

        private static UpdateResult keyDestroyed() {
            UpdateResult result = new UpdateResult();
            result.keyDestroyed = true;
            return result;
        }

        public boolean isPlayerLifeLost() {
            return playerLifeLost;
        }

        public boolean isKeyDestroyed() {
            return keyDestroyed;
        }
    }
}
