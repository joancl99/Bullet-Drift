package bulletdrift.systems;

import bulletdrift.entities.Enemy;
import bulletdrift.entities.Player;
import bulletdrift.entities.PowerUp;
import bulletdrift.entities.Projectile;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;

public class CollisionManager {
    private static final int SCORE_PER_ENEMY = 10;
    private static final int ENEMY_COLLISION_DAMAGE = 20;
    private static final int BOMB_EXPLOSION_RADIUS = 160;

    private PowerUpSystem powerUpSystem;

    public CollisionManager(PowerUpSystem powerUpSystem) {
        this.powerUpSystem = powerUpSystem;
    }

    public CollisionResult checkCollisions(
        Player player,
        ArrayList<Enemy> enemies,
        ArrayList<PowerUp> powerUps,
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
                    if (projectile.getType() != Projectile.Type.FIRE) {
                        player.getProjectiles().remove(projectile);
                    }

                    boolean enemyDestroyed = projectile.getType() == Projectile.Type.FIRE ? enemy.takeHit() || enemy.takeHit() : enemy.takeHit();
                    if (enemyDestroyed) {
                        enemyIterator.remove();
                        result.addScore(SCORE_PER_ENEMY);

                        if (projectile.getType() == Projectile.Type.BOMB) {
                            result.addScore(removeEnemiesInExplosion(enemies, enemy, panelWidth, panelHeight) * SCORE_PER_ENEMY);
                            return result;
                        }
                    }
                    break;
                }
            }
        }

        Iterator<PowerUp> powerUpIterator = powerUps.iterator();
        while (powerUpIterator.hasNext()) {
            PowerUp powerUp = powerUpIterator.next();
            if (playerHitbox.intersects(powerUp.getHitBox(panelWidth, panelHeight))) {
                PowerUpSystem.PowerUpFeedback feedback = powerUpSystem.apply(powerUp, player);
                if (feedback != null) {
                    result.setFeedback(feedback.getText(), feedback.getColor());
                }
                if (PowerUp.TYPE_BOMB.equals(powerUp.getType())) {
                    result.addScore(enemies.size() * SCORE_PER_ENEMY);
                    enemies.clear();
                } else if (PowerUp.TYPE_COIN.equals(powerUp.getType())) {
                    result.addCoins(1);
                }
                powerUpIterator.remove();
            }
        }

        return result;
    }

    private int removeEnemiesInExplosion(ArrayList<Enemy> enemies, Enemy sourceEnemy, int panelWidth, int panelHeight) {
        Rectangle sourceHitbox = sourceEnemy.getHitBoxEnemy(panelWidth, panelHeight);
        int sourceX = sourceHitbox.x + sourceHitbox.width / 2;
        int sourceY = sourceHitbox.y + sourceHitbox.height / 2;
        int removedCount = 0;

        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            Rectangle hitbox = enemy.getHitBoxEnemy(panelWidth, panelHeight);
            int enemyX = hitbox.x + hitbox.width / 2;
            int enemyY = hitbox.y + hitbox.height / 2;
            double distance = Math.hypot(enemyX - sourceX, enemyY - sourceY);
            if (distance <= BOMB_EXPLOSION_RADIUS) {
                iterator.remove();
                removedCount++;
            }
        }

        return removedCount;
    }

    public static class CollisionResult {
        private int scoreToAdd;
        private int coinsToAdd;
        private boolean playerLifeLost;
        private String feedbackText;
        private Color feedbackColor;

        public void addScore(int score) {
            scoreToAdd += score;
        }

        public int getScoreToAdd() {
            return scoreToAdd;
        }

        public void addCoins(int coins) {
            coinsToAdd += coins;
        }

        public int getCoinsToAdd() {
            return coinsToAdd;
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
