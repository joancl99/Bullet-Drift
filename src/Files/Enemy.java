package Files;

import javax.swing.*;
import java.awt.*;

public class Enemy {
    private static final int IMAGE_SIZE = 90;
    private static final int HITBOX_WIDTH = 44;
    private static final int HITBOX_HEIGHT = 58;

    private int x, y, speed;
    private Image enemyImage;

    public Enemy(int x, int y, int speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.enemyImage = new ImageIcon("Images/enemigo.png").getImage();
    }

    public void moveDownEnemy(int getHeight) {
        y += speed;
    }

    public void paint(Graphics g) {
        g.drawImage(enemyImage, x, y, IMAGE_SIZE, IMAGE_SIZE, null);
    }

    public Rectangle getHitBoxEnemy() {
        int hitboxX = x + (IMAGE_SIZE - HITBOX_WIDTH) / 2;
        int hitboxY = y + (IMAGE_SIZE - HITBOX_HEIGHT) / 2;

        return new Rectangle(hitboxX, hitboxY, HITBOX_WIDTH, HITBOX_HEIGHT);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
