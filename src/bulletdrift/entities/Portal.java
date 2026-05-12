package bulletdrift.entities;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

public class Portal {
    private static final int BASE_WIDTH = 205;
    private static final int BASE_HEIGHT = 180;
    private static final int TOP_MARGIN = 120;
    private static final double HITBOX_SCALE = 0.72;
    private static final int REFERENCE_PANEL_WIDTH = 1920;
    private static final int REFERENCE_PANEL_HEIGHT = 1080;

    private int x;
    private int y;
    private Image portalImage;

    public Portal(int panelWidth, int panelHeight) {
        double scale = getPanelScale(panelWidth, panelHeight);
        int width = getScaledSize(BASE_WIDTH, scale);
        this.x = panelWidth / 2 - width / 2;
        this.y = getScaledSize(TOP_MARGIN, scale);
        this.portalImage = new ImageIcon("src/Files/BossAcces/Portal.png").getImage();
    }

    public void paint(Graphics g, int panelWidth, int panelHeight) {
        double scale = getPanelScale(panelWidth, panelHeight);
        int width = getScaledSize(BASE_WIDTH, scale);
        int height = getScaledSize(BASE_HEIGHT, scale);
        g.drawImage(portalImage, x, y, width, height, null);
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

    private double getPanelScale(int panelWidth, int panelHeight) {
        double scaleX = panelWidth / (double) REFERENCE_PANEL_WIDTH;
        double scaleY = panelHeight / (double) REFERENCE_PANEL_HEIGHT;
        return Math.max(0.1, Math.min(scaleX, scaleY));
    }

    private int getScaledSize(int baseSize, double scale) {
        return Math.max(1, (int) Math.round(baseSize * scale));
    }
}
