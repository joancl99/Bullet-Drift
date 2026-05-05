package Files;

import javax.swing.*;
import java.awt.*;

public class Enemy {
    public enum Type {
        NORMAL,
        FAST
    }

    private static final int NORMAL_IMAGE_SIZE = 90;
    private static final int NORMAL_HITBOX_WIDTH = 44;
    private static final int NORMAL_HITBOX_HEIGHT = 58;
    private static final int FAST_IMAGE_SIZE = 70;
    private static final int FAST_HITBOX_WIDTH = 34;
    private static final int FAST_HITBOX_HEIGHT = 45;
    private static final int FAST_OUTLINE_WIDTH = 3;

    private int x, y, speed;
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
        this.enemyImage = new ImageIcon("Images/enemigo.png").getImage();
    }

    public void moveDownEnemy(int getHeight) {
        y += speed;
    }

    public void paint(Graphics g) {
        int imageSize = getImageSize();
        g.drawImage(enemyImage, x, y, imageSize, imageSize, null);

        if (type == Type.FAST) {
            Graphics2D g2d = (Graphics2D) g;
            Stroke previousStroke = g2d.getStroke();
            g2d.setStroke(new BasicStroke(FAST_OUTLINE_WIDTH));
            g2d.setColor(new Color(255, 140, 40));
            g2d.drawOval(x, y, imageSize, imageSize);
            g2d.setStroke(previousStroke);
        }
    }

    public Rectangle getHitBoxEnemy() {
        int imageSize = getImageSize();
        int hitboxWidth = type == Type.FAST ? FAST_HITBOX_WIDTH : NORMAL_HITBOX_WIDTH;
        int hitboxHeight = type == Type.FAST ? FAST_HITBOX_HEIGHT : NORMAL_HITBOX_HEIGHT;
        int hitboxX = x + (imageSize - hitboxWidth) / 2;
        int hitboxY = y + (imageSize - hitboxHeight) / 2;

        return new Rectangle(hitboxX, hitboxY, hitboxWidth, hitboxHeight);
    }

    private int getImageSize() {
        return type == Type.FAST ? FAST_IMAGE_SIZE : NORMAL_IMAGE_SIZE;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
