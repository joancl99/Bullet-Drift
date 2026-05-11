package bulletdrift.rendering;

import bulletdrift.entities.Enemy;
import bulletdrift.entities.Player;
import bulletdrift.entities.PowerUp;
import bulletdrift.entities.Projectile;

import java.awt.*;
import java.util.ArrayList;

public class HudRenderer {
    private static final int DESIGN_WIDTH = 1920;
    private static final int DESIGN_HEIGHT = 1080;
    private static final int HUD_X = 40;
    private static final int HUD_SCORE_Y = 150;
    private static final int HUD_LIVES_Y = 200;
    private static final int HEALTH_BAR_Y = 220;
    private static final int HEALTH_BAR_WIDTH = 260;
    private static final int HEALTH_BAR_HEIGHT = 26;
    private static final int HUD_POWER_UP_Y = 285;
    private static final int HUD_LINE_HEIGHT = 40;
    private static final int WAVE_HUD_Y = 70;
    private static final int WAVE_FEEDBACK_Y_OFFSET = 120;
    private static final int PAUSE_BUTTON_WIDTH = 300;
    private static final int PAUSE_BUTTON_HEIGHT = 60;
    private static final double MIN_HUD_SCALE = 0.65;
    private static final double MAX_HUD_SCALE = 1.25;

    public void draw(
        Graphics g,
        int panelWidth,
        int panelHeight,
        Player player,
        ArrayList<Enemy> enemies,
        ArrayList<PowerUp> powerUps,
        int score,
        int wave,
        boolean debugHitboxes,
        boolean paused,
        boolean gameOver,
        String powerUpFeedbackText,
        Color powerUpFeedbackColor,
        long powerUpFeedbackEndTime,
        String waveFeedbackText,
        long waveFeedbackEndTime
    ) {
        int hudX = scaleHud(HUD_X, panelWidth, panelHeight);
        int hudLineHeight = scaleHud(HUD_LINE_HEIGHT, panelWidth, panelHeight);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, scaleFont(40, panelWidth, panelHeight)));
        g.drawString("Puntos: " + score, hudX, scaleHud(HUD_SCORE_Y, panelWidth, panelHeight));
        g.drawString("Vidas: " + player.getLives(), hudX, scaleHud(HUD_LIVES_Y, panelWidth, panelHeight));
        drawPlayerHealthBar(g, panelWidth, panelHeight, player);
        drawWaveHud(g, panelWidth, panelHeight, wave);

        g.setFont(new Font("Arial", Font.BOLD, scaleFont(28, panelWidth, panelHeight)));
        int powerUpTextY = scaleHud(HUD_POWER_UP_Y + HUD_LINE_HEIGHT, panelWidth, panelHeight);
        if (player.hasShield()) {
            g.drawString("Escudo: " + player.getShieldSecondsLeft() + "s", hudX, powerUpTextY);
            powerUpTextY += hudLineHeight;
        }
        if (player.isRapidFire()) {
            g.drawString("Disparo rapido: " + player.getRapidFireSecondsLeft() + "s", hudX, powerUpTextY);
            powerUpTextY += hudLineHeight;
        }
        if (player.isSpeedBoost()) {
            g.drawString("Velocidad: " + player.getSpeedBoostSecondsLeft() + "s", hudX, powerUpTextY);
            powerUpTextY += hudLineHeight;
        }
        if (player.isMagnetActive()) {
            g.drawString("Iman: " + player.getMagnetSecondsLeft() + "s", hudX, powerUpTextY);
        }

        if (debugHitboxes) {
            g.drawString("Hitboxes: ON", hudX, panelHeight - hudLineHeight);
            drawHitboxes(g, panelWidth, panelHeight, player, enemies, powerUps);
        }

        drawPowerUpFeedback(g, panelWidth, panelHeight, powerUpFeedbackText, powerUpFeedbackColor, powerUpFeedbackEndTime);
        drawWaveFeedback(g, panelWidth, panelHeight, waveFeedbackText, waveFeedbackEndTime);

        if (paused) {
            drawPauseMenu(g, panelWidth, panelHeight);
        }

        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, scaleFont(30, panelWidth, panelHeight)));
            drawCenteredString(g, panelWidth, "¡Has perdido!", panelHeight / 2 - scaleHud(50, panelWidth, panelHeight));
            drawCenteredString(g, panelWidth, "Presiona ENTER para reiniciar", panelHeight / 2);
        }
    }

    public Rectangle getResumeButtonBounds(int panelWidth, int panelHeight) {
        int buttonWidth = scaleHud(PAUSE_BUTTON_WIDTH, panelWidth, panelHeight);
        int buttonHeight = scaleHud(PAUSE_BUTTON_HEIGHT, panelWidth, panelHeight);
        return new Rectangle(panelWidth / 2 - buttonWidth / 2, panelHeight / 2 - scaleHud(10, panelWidth, panelHeight), buttonWidth, buttonHeight);
    }

    public Rectangle getExitButtonBounds(int panelWidth, int panelHeight) {
        int buttonWidth = scaleHud(PAUSE_BUTTON_WIDTH, panelWidth, panelHeight);
        int buttonHeight = scaleHud(PAUSE_BUTTON_HEIGHT, panelWidth, panelHeight);
        return new Rectangle(panelWidth / 2 - buttonWidth / 2, panelHeight / 2 + scaleHud(70, panelWidth, panelHeight), buttonWidth, buttonHeight);
    }

    private double getHudScale(int panelWidth, int panelHeight) {
        double scaleX = panelWidth / (double) DESIGN_WIDTH;
        double scaleY = panelHeight / (double) DESIGN_HEIGHT;
        return Math.max(MIN_HUD_SCALE, Math.min(MAX_HUD_SCALE, Math.min(scaleX, scaleY)));
    }

    private int scaleHud(int value, int panelWidth, int panelHeight) {
        return Math.max(1, (int) Math.round(value * getHudScale(panelWidth, panelHeight)));
    }

    private int scaleFont(int size, int panelWidth, int panelHeight) {
        return Math.max(12, scaleHud(size, panelWidth, panelHeight));
    }

    private void drawCenteredString(Graphics g, int panelWidth, String text, int y) {
        FontMetrics metrics = g.getFontMetrics();
        int textX = (panelWidth - metrics.stringWidth(text)) / 2;
        g.drawString(text, textX, y);
    }

    private void drawWaveHud(Graphics g, int panelWidth, int panelHeight, int wave) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(new Font("Arial", Font.BOLD, scaleFont(46, panelWidth, panelHeight)));
        String waveText = "Oleada " + wave;
        FontMetrics metrics = g2d.getFontMetrics();
        int textX = (panelWidth - metrics.stringWidth(waveText)) / 2;
        int waveY = scaleHud(WAVE_HUD_Y, panelWidth, panelHeight);
        int shadowOffset = scaleHud(3, panelWidth, panelHeight);

        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawString(waveText, textX + shadowOffset, waveY + shadowOffset);
        g2d.setColor(Color.WHITE);
        g2d.drawString(waveText, textX, waveY);
    }

    private void drawPlayerHealthBar(Graphics g, int panelWidth, int panelHeight, Player player) {
        Graphics2D g2d = (Graphics2D) g;
        int health = player.getHealth();
        int maxHealth = player.getMaxHealth();
        int hudX = scaleHud(HUD_X, panelWidth, panelHeight);
        int healthBarY = scaleHud(HEALTH_BAR_Y, panelWidth, panelHeight);
        int healthBarWidth = scaleHud(HEALTH_BAR_WIDTH, panelWidth, panelHeight);
        int healthBarHeight = scaleHud(HEALTH_BAR_HEIGHT, panelWidth, panelHeight);
        int filledWidth = (int) (healthBarWidth * (health / (double) maxHealth));

        g2d.setColor(new Color(0, 0, 0, 170));
        g2d.fillRect(hudX, healthBarY, healthBarWidth, healthBarHeight);
        g2d.setColor(new Color(220, 45, 45));
        g2d.fillRect(hudX, healthBarY, filledWidth, healthBarHeight);
        g2d.setColor(Color.WHITE);
        g2d.drawRect(hudX, healthBarY, healthBarWidth, healthBarHeight);

        g2d.setFont(new Font("Arial", Font.BOLD, scaleFont(18, panelWidth, panelHeight)));
        String healthText = health + " / " + maxHealth + " HP";
        FontMetrics metrics = g2d.getFontMetrics();
        int textX = hudX + (healthBarWidth - metrics.stringWidth(healthText)) / 2;
        int textY = healthBarY + ((healthBarHeight - metrics.getHeight()) / 2) + metrics.getAscent();
        g2d.drawString(healthText, textX, textY);
    }

    private void drawWaveFeedback(Graphics g, int panelWidth, int panelHeight, String waveFeedbackText, long waveFeedbackEndTime) {
        if (waveFeedbackText.isEmpty() || System.currentTimeMillis() > waveFeedbackEndTime) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(new Font("Arial", Font.BOLD, scaleFont(74, panelWidth, panelHeight)));
        FontMetrics metrics = g2d.getFontMetrics();
        int textX = (panelWidth - metrics.stringWidth(waveFeedbackText)) / 2;
        int textY = panelHeight / 2 - scaleHud(WAVE_FEEDBACK_Y_OFFSET, panelWidth, panelHeight);
        int shadowOffset = scaleHud(4, panelWidth, panelHeight);

        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.drawString(waveFeedbackText, textX + shadowOffset, textY + shadowOffset);
        g2d.setColor(new Color(255, 210, 80));
        g2d.drawString(waveFeedbackText, textX, textY);
    }

    private void drawPowerUpFeedback(Graphics g, int panelWidth, int panelHeight, String text, Color color, long endTime) {
        if (text.isEmpty() || System.currentTimeMillis() > endTime) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(new Font("Arial", Font.BOLD, scaleFont(42, panelWidth, panelHeight)));
        FontMetrics metrics = g2d.getFontMetrics();
        int textX = (panelWidth - metrics.stringWidth(text)) / 2;
        int textY = scaleHud(120, panelWidth, panelHeight);
        int shadowOffset = scaleHud(3, panelWidth, panelHeight);

        g2d.setColor(new Color(0, 0, 0, 160));
        g2d.drawString(text, textX + shadowOffset, textY + shadowOffset);
        g2d.setColor(color);
        g2d.drawString(text, textX, textY);
    }

    private void drawHitboxes(Graphics g, int panelWidth, int panelHeight, Player player, ArrayList<Enemy> enemies, ArrayList<PowerUp> powerUps) {
        Graphics2D g2d = (Graphics2D) g;
        Stroke previousStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(scaleHud(2, panelWidth, panelHeight)));

        g2d.setColor(Color.GREEN);
        Rectangle playerHitbox = player.getHitBox();
        g2d.drawRect(playerHitbox.x, playerHitbox.y, playerHitbox.width, playerHitbox.height);

        g2d.setColor(Color.RED);
        for (Enemy enemy : enemies) {
            Rectangle enemyHitbox = enemy.getHitBoxEnemy(panelWidth, panelHeight);
            g2d.drawRect(enemyHitbox.x, enemyHitbox.y, enemyHitbox.width, enemyHitbox.height);
        }

        g2d.setColor(Color.YELLOW);
        for (Projectile projectile : player.getProjectiles()) {
            Rectangle projectileHitbox = projectile.getHitBox();
            g2d.drawRect(projectileHitbox.x, projectileHitbox.y, projectileHitbox.width, projectileHitbox.height);
        }

        g2d.setColor(Color.CYAN);
        for (PowerUp powerUp : powerUps) {
            Rectangle powerUpHitbox = powerUp.getHitBox(panelWidth, panelHeight);
            g2d.drawRect(powerUpHitbox.x, powerUpHitbox.y, powerUpHitbox.width, powerUpHitbox.height);
        }

        g2d.setStroke(previousStroke);
    }

    private void drawPauseMenu(Graphics g, int panelWidth, int panelHeight) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(0, 0, 0, 170));
        g2d.fillRect(0, 0, panelWidth, panelHeight);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, scaleFont(56, panelWidth, panelHeight)));
        drawCenteredString(g2d, panelWidth, "PAUSA", panelHeight / 2 - scaleHud(90, panelWidth, panelHeight));

        drawMenuButton(g2d, panelWidth, panelHeight, getResumeButtonBounds(panelWidth, panelHeight), "Reanudar");
        drawMenuButton(g2d, panelWidth, panelHeight, getExitButtonBounds(panelWidth, panelHeight), "Salir");

        g2d.setFont(new Font("Arial", Font.BOLD, scaleFont(22, panelWidth, panelHeight)));
        drawCenteredString(g2d, panelWidth, "ESC/ENTER: reanudar   Q: salir", panelHeight / 2 + scaleHud(170, panelWidth, panelHeight));
    }

    private void drawMenuButton(Graphics2D g2d, int panelWidth, int panelHeight, Rectangle bounds, String text) {
        g2d.setColor(new Color(40, 40, 40, 220));
        g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        g2d.setColor(Color.WHITE);
        g2d.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        g2d.setFont(new Font("Arial", Font.BOLD, scaleFont(30, panelWidth, panelHeight)));

        FontMetrics metrics = g2d.getFontMetrics();
        int textX = bounds.x + (bounds.width - metrics.stringWidth(text)) / 2;
        int textY = bounds.y + ((bounds.height - metrics.getHeight()) / 2) + metrics.getAscent();
        g2d.drawString(text, textX, textY);
    }
}
