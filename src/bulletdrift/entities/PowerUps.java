package bulletdrift.entities;

import java.awt.*;
import javax.swing.ImageIcon;

public class PowerUps {
    public static final String TYPE_LIFE = "vida";
    public static final String TYPE_SHIELD = "escudo";
    public static final String TYPE_RAPID_FIRE = "disparoRapido";

    private static final int WIDTH = 55;
    private static final int HEIGHT = 60;
    private static final double HITBOX_SCALE = 0.70;
    private static final int REFERENCE_PANEL_WIDTH = 1920;
    private static final int REFERENCE_PANEL_HEIGHT = 1080;

    private int x, y;
    private Image powerUpImage;
    private String type;

    public PowerUps(int x, int y, String type) {
        this.x = x;
        this.y = y;
        this.type = type;

        switch (type) {
            case TYPE_LIFE:
                this.powerUpImage = new ImageIcon("Images/1.png").getImage();
                break;
            case TYPE_SHIELD:
                this.powerUpImage = new ImageIcon("Images/2.png").getImage();
                break;
            case TYPE_RAPID_FIRE:
                this.powerUpImage = new ImageIcon("Images/3.png").getImage();
                break;
            default:
                this.powerUpImage = new ImageIcon("Images/4.png").getImage();
        }
    }

    public void paint(Graphics g, boolean debug, int panelWidth, int panelHeight) {
        double scale = getPanelScale(panelWidth, panelHeight);
        int width = getScaledSize(WIDTH, scale);
        int height = getScaledSize(HEIGHT, scale);
        g.drawImage(powerUpImage, x, y, width, height, null);

        if (debug) {
            g.setColor(Color.RED);
            Rectangle hitBox = getHitBoxPowerUps(panelWidth, panelHeight);
            g.drawRect(hitBox.x, hitBox.y, hitBox.width, hitBox.height);
        }
    }

    public void paint(Graphics g, boolean debug) {
        paint(g, debug, REFERENCE_PANEL_WIDTH, REFERENCE_PANEL_HEIGHT);
    }

    public Rectangle getHitBoxPowerUps(int panelWidth, int panelHeight) {
        double scale = getPanelScale(panelWidth, panelHeight);
        int width = getScaledSize(WIDTH, scale);
        int height = getScaledSize(HEIGHT, scale);
        int hitboxWidth = (int) (width * HITBOX_SCALE);
        int hitboxHeight = (int) (height * HITBOX_SCALE);
        int hitboxX = x + (width - hitboxWidth) / 2;
        int hitboxY = y + (height - hitboxHeight) / 2;

        return new Rectangle(hitboxX, hitboxY, hitboxWidth, hitboxHeight);
    }

    public Rectangle getHitBoxPowerUps() {
        return getHitBoxPowerUps(REFERENCE_PANEL_WIDTH, REFERENCE_PANEL_HEIGHT);
    }

    private double getPanelScale(int panelWidth, int panelHeight) {
        double scaleX = panelWidth / (double) REFERENCE_PANEL_WIDTH;
        double scaleY = panelHeight / (double) REFERENCE_PANEL_HEIGHT;
        return Math.max(0.1, Math.min(scaleX, scaleY));
    }

    private int getScaledSize(int baseSize, double scale) {
        return Math.max(1, (int) Math.round(baseSize * scale));
    }

    public String getType() {
        return type;
    }
}
