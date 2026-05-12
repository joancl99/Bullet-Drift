package bulletdrift.entities;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

public class Portal {
    private static final int BASE_SIZE = 92;
    private static final int TOP_MARGIN = 120;
    private static final double HITBOX_SCALE = 0.72;
    private static final int REFERENCE_PANEL_WIDTH = 1920;
    private static final int REFERENCE_PANEL_HEIGHT = 1080;

    private int x;
    private int y;
    private Image portalImage;

    public Portal(int panelWidth, int panelHeight) {
        double scale = getPanelScale(panelWidth, panelHeight);
        int size = getScaledSize(BASE_SIZE, scale);
        this.x = panelWidth / 2 - size / 2;
        this.y = getScaledSize(TOP_MARGIN, scale);
        this.portalImage = new ImageIcon("src/Files/BossAcces/Portal.png").getImage();
    }

    public void paint(Graphics g, int panelWidth, int panelHeight) {
        double scale = getPanelScale(panelWidth, panelHeight);
        int size = getScaledSize(BASE_SIZE, scale);
        Graphics2D g2d = (Graphics2D) g;
        int centerX = x + size / 2;
        int centerY = y + size / 2;

        g2d.rotate(Math.toRadians(90), centerX, centerY);
        g2d.drawImage(portalImage, x, y, size, size, null);
        g2d.rotate(-Math.toRadians(90), centerX, centerY);
    }

    public Rectangle getHitBox(int panelWidth, int panelHeight) {
        int size = getScaledSize(BASE_SIZE, getPanelScale(panelWidth, panelHeight));
        int hitboxSize = (int) (size * HITBOX_SCALE);
        int hitboxX = x + (size - hitboxSize) / 2;
        int hitboxY = y + (size - hitboxSize) / 2;
        return new Rectangle(hitboxX, hitboxY, hitboxSize, hitboxSize);
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
