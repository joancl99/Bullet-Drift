package bulletdrift.entities;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

public class Boss {
    public static final int MAX_HEALTH = 500;
    public static final int PROJECTILE_DAMAGE = 10;

    private static final int BASE_SIZE = 340;
    private static final int CENTER_Y_OFFSET = 150;
    private static final double HITBOX_SCALE = 0.76;
    private static final int REFERENCE_PANEL_WIDTH = 1920;
    private static final int REFERENCE_PANEL_HEIGHT = 1080;
    private static final int HEALTH_BAR_WIDTH = 920;
    private static final int HEALTH_BAR_HEIGHT = 30;
    private static final int HEALTH_BAR_BOTTOM_MARGIN = 55;

    private int x;
    private int y;
    private int health;
    private Image bossImage;

    public Boss(int panelWidth, int panelHeight) {
        int size = getScaledSize(BASE_SIZE, getPanelScale(panelWidth, panelHeight));
        this.x = panelWidth / 2 - size / 2;
        this.y = panelHeight / 2 - size / 2 - getScaledSize(CENTER_Y_OFFSET, getPanelScale(panelWidth, panelHeight));
        this.health = MAX_HEALTH;
        this.bossImage = new ImageIcon("src/Files/Enemies/Boss.png").getImage();
    }

    public void paint(Graphics g, int panelWidth, int panelHeight) {
        int size = getScaledSize(BASE_SIZE, getPanelScale(panelWidth, panelHeight));
        g.drawImage(bossImage, x, y, size, size, null);
    }

    public void paintHealthBar(Graphics g, int panelWidth, int panelHeight) {
        Graphics2D g2d = (Graphics2D) g;
        double scale = getPanelScale(panelWidth, panelHeight);
        int barWidth = Math.min(panelWidth - getScaledSize(120, scale), getScaledSize(HEALTH_BAR_WIDTH, scale));
        int barHeight = getScaledSize(HEALTH_BAR_HEIGHT, scale);
        int barX = panelWidth / 2 - barWidth / 2;
        int barY = panelHeight - getScaledSize(HEALTH_BAR_BOTTOM_MARGIN, scale) - barHeight;
        int filledWidth = (int) Math.round(barWidth * (health / (double) MAX_HEALTH));

        g2d.setColor(new Color(0, 0, 0, 190));
        g2d.fillRect(barX, barY, barWidth, barHeight);
        g2d.setColor(new Color(145, 25, 25));
        g2d.fillRect(barX, barY, filledWidth, barHeight);
        g2d.setColor(new Color(225, 205, 155));
        g2d.drawRect(barX, barY, barWidth, barHeight);

        String text = "BOSS FINAL  " + health + " / " + MAX_HEALTH;
        g2d.setFont(new Font("Arial", Font.BOLD, Math.max(14, getScaledSize(22, scale))));
        FontMetrics metrics = g2d.getFontMetrics();
        int textX = barX + (barWidth - metrics.stringWidth(text)) / 2;
        int textY = barY - getScaledSize(8, scale);
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.drawString(text, textX + 2, textY + 2);
        g2d.setColor(new Color(225, 205, 155));
        g2d.drawString(text, textX, textY);
    }

    public Rectangle getHitBox(int panelWidth, int panelHeight) {
        int size = getScaledSize(BASE_SIZE, getPanelScale(panelWidth, panelHeight));
        int hitboxSize = (int) Math.round(size * HITBOX_SCALE);
        int hitboxX = x + (size - hitboxSize) / 2;
        int hitboxY = y + (size - hitboxSize) / 2;
        return new Rectangle(hitboxX, hitboxY, hitboxSize, hitboxSize);
    }

    public void takeProjectileHit() {
        health = Math.max(0, health - PROJECTILE_DAMAGE);
    }

    public boolean isDefeated() {
        return health <= 0;
    }

    private double getPanelScale(int panelWidth, int panelHeight) {
        double scaleX = panelWidth / (double) REFERENCE_PANEL_WIDTH;
        double scaleY = panelHeight / (double) REFERENCE_PANEL_HEIGHT;
        return Math.max(0.1, Math.min(scaleX, scaleY));
    }

    private int getScaledSize(int baseSize, double scale) {
        return Math.max(1, (int) Math.round(baseSize * scale));
    }
}
