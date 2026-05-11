package bulletdrift.entities;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

public class KeyObjective {
    public static final int MAX_HEALTH = 100;
    public static final int HIT_DAMAGE = 10;

    private static final int BASE_WIDTH = 64;
    private static final int BASE_HEIGHT = 64;
    private static final double HITBOX_SCALE = 0.72;
    private static final int REFERENCE_PANEL_WIDTH = 1920;
    private static final int REFERENCE_PANEL_HEIGHT = 1080;

    private int x;
    private int y;
    private int health;
    private Image keyImage;

    public KeyObjective(int x, int y) {
        this.x = x;
        this.y = y;
        this.health = MAX_HEALTH;
        this.keyImage = new ImageIcon("Images/Key.png").getImage();
    }

    public void paint(Graphics g, int panelWidth, int panelHeight) {
        double scale = getPanelScale(panelWidth, panelHeight);
        int width = getScaledSize(BASE_WIDTH, scale);
        int height = getScaledSize(BASE_HEIGHT, scale);

        g.drawImage(keyImage, x, y, width, height, null);
        drawHealthText(g, width, panelWidth, panelHeight);
    }

    private void drawHealthText(Graphics g, int width, int panelWidth, int panelHeight) {
        Graphics2D g2d = (Graphics2D) g;
        int fontSize = Math.max(12, getScaledSize(22, getPanelScale(panelWidth, panelHeight)));
        String text = "Llave " + health + "/" + MAX_HEALTH;
        g2d.setFont(new Font("Arial", Font.BOLD, fontSize));
        FontMetrics metrics = g2d.getFontMetrics();
        int textX = x + (width - metrics.stringWidth(text)) / 2;
        int textY = y - getScaledSize(8, getPanelScale(panelWidth, panelHeight));

        g2d.setColor(new Color(0, 0, 0, 170));
        g2d.drawString(text, textX + 2, textY + 2);
        g2d.setColor(Color.WHITE);
        g2d.drawString(text, textX, textY);
    }

    public Rectangle getHitBox(int panelWidth, int panelHeight) {
        double scale = getPanelScale(panelWidth, panelHeight);
        int width = getScaledSize(BASE_WIDTH, scale);
        int height = getScaledSize(BASE_HEIGHT, scale);
        int hitboxWidth = (int) (width * HITBOX_SCALE);
        int hitboxHeight = (int) (height * HITBOX_SCALE);
        int hitboxX = x + (width - hitboxWidth) / 2;
        int hitboxY = y + (height - hitboxHeight) / 2;
        return new Rectangle(hitboxX, hitboxY, hitboxWidth, hitboxHeight);
    }

    public void takeHit() {
        health = Math.max(0, health - HIT_DAMAGE);
    }

    public boolean isDestroyed() {
        return health <= 0;
    }

    public int getCenterX(int panelWidth, int panelHeight) {
        return x + getScaledSize(BASE_WIDTH, getPanelScale(panelWidth, panelHeight)) / 2;
    }

    public int getCenterY(int panelWidth, int panelHeight) {
        return y + getScaledSize(BASE_HEIGHT, getPanelScale(panelWidth, panelHeight)) / 2;
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
