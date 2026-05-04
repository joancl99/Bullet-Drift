package Files;

import java.awt.*;
import javax.swing.ImageIcon;

public class Projectile {
    private double x, y;      // posición real
    private double dx, dy;    // velocidad en px por tick
    private int baseSize = 25;
    private int width, height;
    private Image bulletImage;

    private String direction; // 👈 nueva propiedad para rotación

    public Projectile(double x, double y, double dx, double dy, int panelWidth, int panelHeight, String direction) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.direction = direction;
        this.bulletImage = new ImageIcon("Images/bullet.png").getImage();

        // Escalamos el tamaño de la bala según la resolución
        double scaleX = panelWidth / 800.0;
        double scaleY = panelHeight / 600.0;
        width = (int)(baseSize * scaleX);
        height = (int)(baseSize * scaleY);
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
        int hitboxWidth = Math.max(8, (int) (width * 0.45));
        int hitboxHeight = Math.max(8, (int) (height * 0.45));
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
