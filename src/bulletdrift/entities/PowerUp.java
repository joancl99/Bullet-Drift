package bulletdrift.entities;

import java.awt.*;
import javax.swing.ImageIcon;

public class PowerUp {
    public static final String TYPE_LIFE = "vida";
    public static final String TYPE_HEALING = "curacion";
    public static final String TYPE_SHIELD = "escudo";
    public static final String TYPE_RAPID_FIRE = "disparoRapido";
    public static final String TYPE_INVULNERABILITY = "invulnerabilidad";
    public static final String TYPE_SPEED = "superVelocidad";
    public static final String TYPE_BOMB_SHOT = "bombShot";
    public static final String TYPE_FIRE_SHOT = "fireShoot";
    public static final String TYPE_KEY = "llave";
    public static final String TYPE_MEGA_MUSH = "megaMush";
    public static final String TYPE_MYSTERY_BOX = "mysteryBox";
    public static final String TYPE_MAGNET = "iman";
    public static final String TYPE_PADLOCK = "padlock";

    private static final int WIDTH = 55;
    private static final int HEIGHT = 60;
    private static final double HITBOX_SCALE = 0.70;
    private static final int REFERENCE_PANEL_WIDTH = 1920;
    private static final int REFERENCE_PANEL_HEIGHT = 1080;

    private int x, y;
    private Image powerUpImage;
    private String type;

    public PowerUp(int x, int y, String type) {
        this.x = x;
        this.y = y;
        this.type = type;

        switch (type) {
            case TYPE_LIFE:
                this.powerUpImage = new ImageIcon("src/files/power-ups/extra-life.png").getImage();
                break;
            case TYPE_HEALING:
                this.powerUpImage = new ImageIcon("src/files/power-ups/healing.png").getImage();
                break;
            case TYPE_SHIELD:
                this.powerUpImage = new ImageIcon("src/files/power-ups/shield.png").getImage();
                break;
            case TYPE_RAPID_FIRE:
                this.powerUpImage = new ImageIcon("src/files/power-ups/rapid-fire.png").getImage();
                break;
            case TYPE_INVULNERABILITY:
                this.powerUpImage = new ImageIcon("src/files/power-ups/invulnerability.png").getImage();
                break;
            case TYPE_SPEED:
                this.powerUpImage = new ImageIcon("src/files/power-ups/super-velocity.png").getImage();
                break;
            case TYPE_BOMB_SHOT:
                this.powerUpImage = new ImageIcon("src/files/power-ups/bomb-shoot.png").getImage();
                break;
            case TYPE_FIRE_SHOT:
                this.powerUpImage = new ImageIcon("src/files/power-ups/fire-shoot.png").getImage();
                break;
            case TYPE_KEY:
                this.powerUpImage = new ImageIcon("src/files/boss-access/key.png").getImage();
                break;
            case TYPE_MEGA_MUSH:
                this.powerUpImage = new ImageIcon("src/files/power-ups/mega-mush.png").getImage();
                break;
            case TYPE_MYSTERY_BOX:
                this.powerUpImage = new ImageIcon("src/files/power-ups/mystery-box.png").getImage();
                break;
            case TYPE_MAGNET:
                this.powerUpImage = new ImageIcon("src/files/power-ups/magnet.png").getImage();
                break;
            case TYPE_PADLOCK:
                this.powerUpImage = new ImageIcon("src/files/boss-access/padlock.png").getImage();
                break;
            default:
                this.powerUpImage = new ImageIcon("src/files/power-ups/invulnerability.png").getImage();
        }
    }

    public void paint(Graphics g, boolean debug, int panelWidth, int panelHeight) {
        double scale = getPanelScale(panelWidth, panelHeight);
        int width = getScaledSize(WIDTH, scale);
        int height = getScaledSize(HEIGHT, scale);
        g.drawImage(powerUpImage, x, y, width, height, null);

        if (debug) {
            g.setColor(Color.RED);
            Rectangle hitBox = getHitBox(panelWidth, panelHeight);
            g.drawRect(hitBox.x, hitBox.y, hitBox.width, hitBox.height);
        }
    }

    public void paint(Graphics g, boolean debug) {
        paint(g, debug, REFERENCE_PANEL_WIDTH, REFERENCE_PANEL_HEIGHT);
    }

    public Rectangle getHitBox(int panelWidth, int panelHeight) {
        double scale = getPanelScale(panelWidth, panelHeight);
        int width = getScaledSize(WIDTH, scale);
        int height = getScaledSize(HEIGHT, scale);
        int hitboxWidth = (int) (width * HITBOX_SCALE);
        int hitboxHeight = (int) (height * HITBOX_SCALE);
        int hitboxX = x + (width - hitboxWidth) / 2;
        int hitboxY = y + (height - hitboxHeight) / 2;

        return new Rectangle(hitboxX, hitboxY, hitboxWidth, hitboxHeight);
    }

    public Rectangle getHitBox() {
        return getHitBox(REFERENCE_PANEL_WIDTH, REFERENCE_PANEL_HEIGHT);
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

    public int getCenterX(int panelWidth, int panelHeight) {
        double scale = getPanelScale(panelWidth, panelHeight);
        return x + getScaledSize(WIDTH, scale) / 2;
    }

    public int getCenterY(int panelWidth, int panelHeight) {
        double scale = getPanelScale(panelWidth, panelHeight);
        return y + getScaledSize(HEIGHT, scale) / 2;
    }

    public void moveToward(int targetX, int targetY, int panelWidth, int panelHeight) {
        int centerX = getCenterX(panelWidth, panelHeight);
        int centerY = getCenterY(panelWidth, panelHeight);
        double dx = targetX - centerX;
        double dy = targetY - centerY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance < 1) return;

        double speed = Math.min(18, Math.max(6, distance / 12));
        x += (int) Math.round(dx / distance * speed);
        y += (int) Math.round(dy / distance * speed);
    }
}
