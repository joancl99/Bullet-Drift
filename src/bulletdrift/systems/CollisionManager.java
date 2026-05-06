package bulletdrift.systems;

import bulletdrift.entities.Enemy;
import bulletdrift.entities.Player;
import bulletdrift.entities.PowerUps;
import bulletdrift.entities.Projectile;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;

public class CollisionManager {
    private static final int SCORE_PER_ENEMY = 10;
    private static final int ENEMY_COLLISION_DAMAGE = 20;

    private PowerUpSystem powerUpSystem;

    public CollisionManager(PowerUpSystem powerUpSystem) {
        this.powerUpSystem = powerUpSystem;
    }

    public CollisionResult checkCollisions(
        Player player,
        ArrayList<Enemy> enemies,
        ArrayList<PowerUps> powerUps,
        int panelWidth,
        int panelHeight,
        long damageInvulnerabilityMs
    ) {
        CollisionResult result = new CollisionResult();
        Rectangle playerHitbox = player.getHitBox();

        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            if (playerHitbox.intersects(enemy.getHitBoxEnemy(panelWidth, panelHeight))) {
                if (player.isInvulnerable()) {
                    continue;
                }

                enemyIterator.remove();

                if (player.hasShield()) {
                    player.deactivateShield();
                    continue;
                }

                player.takeDamage(ENEMY_COLLISION_DAMAGE);
                if (player.isDead()) {
                    result.setPlayerLifeLost(true);
                    return result;
                }

                result.setFeedback("-" + ENEMY_COLLISION_DAMAGE + " HP", new Color(255, 90, 90));
                player.activateInvulnerability(damageInvulnerabilityMs);
                continue;
            }

            for (Projectile projectile : new ArrayList<>(player.getProjectiles())) {
                if (projectile.getHitBox().intersects(enemy.getHitBoxEnemy(panelWidth, panelHeight))) {
                    player.getProjectiles().remove(projectile);
                    if (enemy.takeHit()) {
                        enemyIterator.remove();
                        result.addScore(SCORE_PER_ENEMY);
                    }
                    break;
                }
            }
        }

        Iterator<PowerUps> powerUpIterator = powerUps.iterator();
        while (powerUpIterator.hasNext()) {
            PowerUps powerUp = powerUpIterator.next();
            if (playerHitbox.intersects(powerUp.getHitBoxPowerUps(panelWidth, panelHeight))) {
                PowerUpSystem.PowerUpFeedback feedback = powerUpSystem.apply(powerUp, player);
                if (feedback != null) {
                    result.setFeedback(feedback.getText(), feedback.getColor());
                }
                powerUpIterator.remove();
            }
        }

        return result;
    }

    public static class CollisionResult {
        private int scoreToAdd;
        private boolean playerLifeLost;
        private String feedbackText;
        private Color feedbackColor;

        public void addScore(int score) {
            scoreToAdd += score;
        }

        public int getScoreToAdd() {
            return scoreToAdd;
        }

        public boolean isPlayerLifeLost() {
            return playerLifeLost;
        }

        public void setPlayerLifeLost(boolean playerLifeLost) {
            this.playerLifeLost = playerLifeLost;
        }

        public void setFeedback(String feedbackText, Color feedbackColor) {
            this.feedbackText = feedbackText;
            this.feedbackColor = feedbackColor;
        }

        public boolean hasFeedback() {
            return feedbackText != null;
        }

        public String getFeedbackText() {
            return feedbackText;
        }

        public Color getFeedbackColor() {
            return feedbackColor;
        }
    }
}
