package Files;

import javax.swing.*;
import java.awt.*;

public class Enemy {
    private static final int IMAGE_SIZE = 90;

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
    // Dibuja la imagen del enemigo
    g.drawImage(enemyImage, x, y, IMAGE_SIZE, IMAGE_SIZE, null);

    // Dibuja el hitbox en rojo para depuración

    }

    public Rectangle getHitBoxEnemy() {
    int hitboxWidth = 44;
    int hitboxHeight = 58;

    // Ajusta la posición del hitbox dentro de la imagen
    int hitboxX = x + (IMAGE_SIZE - hitboxWidth) / 2;  // centrado horizontalmente
    int hitboxY = y + (IMAGE_SIZE - hitboxHeight) / 2; // centrado verticalmente

    return new Rectangle(hitboxX, hitboxY, hitboxWidth, hitboxHeight);
}

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
