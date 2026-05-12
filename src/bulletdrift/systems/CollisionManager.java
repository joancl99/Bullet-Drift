package bulletdrift.systems;

import bulletdrift.entities.Boss;
import bulletdrift.entities.Enemy;
import bulletdrift.entities.KeyObjective;
import bulletdrift.entities.Player;
import bulletdrift.entities.Portal;
import bulletdrift.entities.PowerUp;
import bulletdrift.entities.Projectile;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;

public class CollisionManager {
    private static final int SCORE_PER_ENEMY = 10;
    private static final int ENEMY_COLLISION_DAMAGE = 20;
    private static final int BOSS_PROJECTILE_DAMAGE = 20;
    private static final int BOSS_COLLISION_DAMAGE = 20;
    private static final int BOMB_EXPLOSION_RADIUS = 160;

    private PowerUpSystem powerUpSystem;

    public CollisionManager(PowerUpSystem powerUpSystem) {
        this.powerUpSystem = powerUpSystem;
    }

    public CollisionResult checkCollisions(
        Player player,
        ArrayList<Enemy> enemies,
        ArrayList<PowerUp> powerUps,
        KeyObjective keyObjective,
        Portal portal,
        Boss boss,
        boolean portalActive,
        boolean keyCollected,
        int panelWidth,
        int panelHeight,
        long damageInvulnerabilityMs
    ) {
        CollisionResult result = new CollisionResult();
        Rectangle playerHitbox = player.getHitBox();

        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();

            if (enemy.getType() == Enemy.Type.KEY_HUNTER && keyObjective != null
                && enemy.getHitBoxEnemy(panelWidth, panelHeight).intersects(keyObjective.getHitBox(panelWidth, panelHeight))) {
                enemyIterator.remove();
                keyObjective.takeHit();
                result.setFeedback("LLAVE -" + KeyObjective.HIT_DAMAGE + " HP", new Color(255, 90, 90));
                if (keyObjective.isDestroyed()) {
                    result.setKeyDestroyed(true);
                    return result;
                }
                continue;
            }

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
                powerUpIterator.remove();
            }
        }

        if (keyObjective != null && playerHitbox.intersects(keyObjective.getHitBox(panelWidth, panelHeight))) {
            if (portalActive) {
                result.setKeyCollected(true);
                result.setFeedback("LLAVE RECOGIDA", new Color(180, 220, 255));
            } else {
                result.setFeedback("DEFIENDE LA LLAVE", new Color(180, 220, 255));
            }
        }

        if (portal != null && portalActive && keyCollected && playerHitbox.intersects(portal.getHitBox(panelWidth, panelHeight))) {
            result.setPortalUsed(true);
            result.setFeedback("PORTAL LISTO", new Color(180, 120, 255));
        }

        if (boss != null && !boss.isDefeated()) {
            if (playerHitbox.intersects(boss.getHitBox(panelWidth, panelHeight))) {
                applyPlayerDamage(player, result, BOSS_COLLISION_DAMAGE, damageInvulnerabilityMs);
                if (result.isPlayerLifeLost()) {
                    return result;
                }
            }

            for (Projectile projectile : new ArrayList<>(player.getProjectiles())) {
                if (projectile.getHitBox().intersects(boss.getHitBox(panelWidth, panelHeight))) {
                    player.getProjectiles().remove(projectile);
                    boss.takeProjectileHit();
                    if (boss.isDefeated()) {
                        result.setBossDefeated(true);
                    }
                    break;
                }
            }

            for (Projectile projectile : new ArrayList<>(boss.getProjectiles())) {
                if (projectile.getHitBox().intersects(playerHitbox)) {
                    boss.getProjectiles().remove(projectile);
                    applyPlayerDamage(player, result, BOSS_PROJECTILE_DAMAGE, damageInvulnerabilityMs);
                    if (result.isPlayerLifeLost()) return result;
                    break;
                }
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

    private void applyPlayerDamage(Player player, CollisionResult result, int damage, long damageInvulnerabilityMs) {
        if (player.isInvulnerable()) {
            return;
        }

        if (player.hasShield()) {
            player.deactivateShield();
            player.activateInvulnerability(damageInvulnerabilityMs);
            return;
        }

        player.takeDamage(damage);
        if (player.isDead()) {
            result.setPlayerLifeLost(true);
            return;
        }

        result.setFeedback("-" + damage + " HP", new Color(255, 90, 90));
        player.activateInvulnerability(damageInvulnerabilityMs);
    }

    public static class CollisionResult {
        private int scoreToAdd;
        private boolean playerLifeLost;
        private boolean keyDestroyed;
        private boolean keyCollected;
        private boolean portalUsed;
        private boolean bossDefeated;
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

        public boolean isKeyDestroyed() {
            return keyDestroyed;
        }

        public void setKeyDestroyed(boolean keyDestroyed) {
            this.keyDestroyed = keyDestroyed;
        }

        public boolean isKeyCollected() {
            return keyCollected;
        }

        public void setKeyCollected(boolean keyCollected) {
            this.keyCollected = keyCollected;
        }

        public boolean isPortalUsed() {
            return portalUsed;
        }

        public void setPortalUsed(boolean portalUsed) {
            this.portalUsed = portalUsed;
        }

        public boolean isBossDefeated() {
            return bossDefeated;
        }

        public void setBossDefeated(boolean bossDefeated) {
            this.bossDefeated = bossDefeated;
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
