package bulletdrift.entities;

import javax.swing.*;
import java.awt.*;

public class Enemy {
    public enum Type {
        NORMAL,
        FAST,
        TANK,
        KEY_HUNTER
    }

    private static final int NORMAL_IMAGE_SIZE = 90;
    private static final int NORMAL_HITBOX_WIDTH = 44;
    private static final int NORMAL_HITBOX_HEIGHT = 58;
    private static final int FAST_IMAGE_SIZE = 70;
    private static final int FAST_HITBOX_WIDTH = 34;
    private static final int FAST_HITBOX_HEIGHT = 45;
    private static final int FAST_OUTLINE_WIDTH = 3;
    private static final int TANK_IMAGE_SIZE = 115;
    private static final int TANK_HITBOX_WIDTH = 62;
    private static final int TANK_HITBOX_HEIGHT = 78;
    private static final int TANK_OUTLINE_WIDTH = 4;
    private static final int KEY_HUNTER_IMAGE_SIZE = 78;
    private static final int KEY_HUNTER_HITBOX_WIDTH = 38;
    private static final int KEY_HUNTER_HITBOX_HEIGHT = 50;
    private static final int KEY_HUNTER_OUTLINE_WIDTH = 4;
    private static final int NORMAL_HEALTH = 1;
    private static final int FAST_HEALTH = 1;
    private static final int TANK_HEALTH = 3;
    private static final int KEY_HUNTER_HEALTH = 1;
    private static final int REFERENCE_PANEL_WIDTH = 1920;
    private static final int REFERENCE_PANEL_HEIGHT = 1080;

    private int x, y, speed;
    private int health;
    private Image enemyImage;
    private Type type;

    public Enemy(int x, int y, int speed) {
        this(x, y, speed, Type.NORMAL);
    }

    public Enemy(int x, int y, int speed, Type type) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.type = type;
        this.health = getInitialHealth(type);
        this.enemyImage = new ImageIcon("src/Files/BugEnemy.png").getImage();
    }

    public void moveDownEnemy(int getHeight) {
        y += speed;
    }

    public void moveToward(int targetX, int targetY, int panelWidth, int panelHeight) {
        int centerX = getCenterX(panelWidth, panelHeight);
        int centerY = getCenterY(panelWidth, panelHeight);
        double dx = targetX - centerX;
        double dy = targetY - centerY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance < 1) return;

        x += (int) Math.round(dx / distance * speed);
        y += (int) Math.round(dy / distance * speed);
    }

    public void paint(Graphics g, int panelWidth, int panelHeight) {
        double scale = getPanelScale(panelWidth, panelHeight);
        int imageSize = getScaledSize(getImageSize(), scale);
        g.drawImage(enemyImage, x, y, imageSize, imageSize, null);

        if (type == Type.FAST) {
            Graphics2D g2d = (Graphics2D) g;
            Stroke previousStroke = g2d.getStroke();
            g2d.setStroke(new BasicStroke(getScaledSize(FAST_OUTLINE_WIDTH, scale)));
            g2d.setColor(new Color(255, 140, 40));
            g2d.drawOval(x, y, imageSize, imageSize);
            g2d.setStroke(previousStroke);
        } else if (type == Type.TANK) {
            Graphics2D g2d = (Graphics2D) g;
            Stroke previousStroke = g2d.getStroke();
            g2d.setStroke(new BasicStroke(getScaledSize(TANK_OUTLINE_WIDTH, scale)));
            g2d.setColor(new Color(170, 90, 255));
            g2d.drawRect(x, y, imageSize, imageSize);
            g2d.setStroke(previousStroke);
        } else if (type == Type.KEY_HUNTER) {
            Graphics2D g2d = (Graphics2D) g;
            Stroke previousStroke = g2d.getStroke();
            g2d.setStroke(new BasicStroke(getScaledSize(KEY_HUNTER_OUTLINE_WIDTH, scale)));
            g2d.setColor(new Color(255, 60, 60));
            g2d.drawOval(x, y, imageSize, imageSize);
            g2d.setStroke(previousStroke);
        }
    }

    public Rectangle getHitBoxEnemy(int panelWidth, int panelHeight) {
        double scale = getPanelScale(panelWidth, panelHeight);
        int imageSize = getScaledSize(getImageSize(), scale);
        int hitboxWidth = getScaledSize(getHitboxWidth(), scale);
        int hitboxHeight = getScaledSize(getHitboxHeight(), scale);
        int hitboxX = x + (imageSize - hitboxWidth) / 2;
        int hitboxY = y + (imageSize - hitboxHeight) / 2;

        return new Rectangle(hitboxX, hitboxY, hitboxWidth, hitboxHeight);
    }

    public Rectangle getHitBoxEnemy() {
        return getHitBoxEnemy(REFERENCE_PANEL_WIDTH, REFERENCE_PANEL_HEIGHT);
    }

    private double getPanelScale(int panelWidth, int panelHeight) {
        double scaleX = panelWidth / (double) REFERENCE_PANEL_WIDTH;
        double scaleY = panelHeight / (double) REFERENCE_PANEL_HEIGHT;
        return Math.max(0.1, Math.min(scaleX, scaleY));
    }

    private int getScaledSize(int baseSize, double scale) {
        return Math.max(1, (int) Math.round(baseSize * scale));
    }

    private int getImageSize() {
        switch (type) {
            case FAST:
                return FAST_IMAGE_SIZE;
            case TANK:
                return TANK_IMAGE_SIZE;
            case KEY_HUNTER:
                return KEY_HUNTER_IMAGE_SIZE;
            default:
                return NORMAL_IMAGE_SIZE;
        }
    }

    private int getHitboxWidth() {
        switch (type) {
            case FAST:
                return FAST_HITBOX_WIDTH;
            case TANK:
                return TANK_HITBOX_WIDTH;
            case KEY_HUNTER:
                return KEY_HUNTER_HITBOX_WIDTH;
            default:
                return NORMAL_HITBOX_WIDTH;
        }
    }

    private int getHitboxHeight() {
        switch (type) {
            case FAST:
                return FAST_HITBOX_HEIGHT;
            case TANK:
                return TANK_HITBOX_HEIGHT;
            case KEY_HUNTER:
                return KEY_HUNTER_HITBOX_HEIGHT;
            default:
                return NORMAL_HITBOX_HEIGHT;
        }
    }

    private int getInitialHealth(Type type) {
        switch (type) {
            case FAST:
                return FAST_HEALTH;
            case TANK:
                return TANK_HEALTH;
            case KEY_HUNTER:
                return KEY_HUNTER_HEALTH;
            default:
                return NORMAL_HEALTH;
        }
    }

    public boolean takeHit() {
        health = Math.max(0, health - 1);
        return health <= 0;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Type getType() {
        return type;
    }

    private int getCenterX(int panelWidth, int panelHeight) {
        double scale = getPanelScale(panelWidth, panelHeight);
        return x + getScaledSize(getImageSize(), scale) / 2;
    }

    private int getCenterY(int panelWidth, int panelHeight) {
        double scale = getPanelScale(panelWidth, panelHeight);
        return y + getScaledSize(getImageSize(), scale) / 2;
    }
}
