package bulletdrift.entities;

import java.awt.*;
import javax.swing.ImageIcon;

public class Projectile {
    private static final int BASE_SIZE = 25;
    private static final int DEFAULT_PANEL_WIDTH = 800;
    private static final int DEFAULT_PANEL_HEIGHT = 600;
    private static final int MIN_HITBOX_SIZE = 8;
    private static final double HITBOX_SCALE = 0.45;

    private double x, y;
    private double dx, dy;
    private int width, height;
    private Image bulletImage;
    private String direction;

    public Projectile(double x, double y, double dx, double dy, int panelWidth, int panelHeight, String direction) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.direction = direction;
        this.bulletImage = new ImageIcon("Images/bullet.png").getImage();

        double scaleX = panelWidth / (double) DEFAULT_PANEL_WIDTH;
        double scaleY = panelHeight / (double) DEFAULT_PANEL_HEIGHT;
        width = (int)(BASE_SIZE * scaleX);
        height = (int)(BASE_SIZE * scaleY);
    }

    public void move() {
        x += dx;
        y += dy;
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        int centerX = (int)x + width / 2;
        int centerY = (int)y + height / 2;

        double angle = 0;
        switch (direction) {
            case "up":    angle = 90; break;
            case "down":  angle = -90;  break;
            case "left":  angle = 0; break;
            case "right": angle = 180;   break;
        }

        g2d.rotate(Math.toRadians(angle), centerX, centerY);
        g2d.drawImage(bulletImage, (int)x, (int)y, width, height, null);
        g2d.rotate(-Math.toRadians(angle), centerX, centerY);
    }

    public Rectangle getHitBox() {
        int hitboxWidth = Math.max(MIN_HITBOX_SIZE, (int) (width * HITBOX_SCALE));
        int hitboxHeight = Math.max(MIN_HITBOX_SIZE, (int) (height * HITBOX_SCALE));
        int hitboxX = (int) x + (width - hitboxWidth) / 2;
        int hitboxY = (int) y + (height - hitboxHeight) / 2;

        return new Rectangle(hitboxX, hitboxY, hitboxWidth, hitboxHeight);
    }

    public int getX() {
        return (int)x;
    }

    public int getY() {
        return (int)y;
    }
}
