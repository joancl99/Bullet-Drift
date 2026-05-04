package Files;

import javax.swing.*;
import java.awt.*;

public class Enemy {
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
    g.drawImage(enemyImage, x, y, 90, 90, null);

    // Dibuja el hitbox en rojo para depuración

    }

    public Rectangle getHitBoxEnemy() {
    int hitboxWidth = 50;
    int hitboxHeight = 65;

    // Ajusta la posición del hitbox dentro de la imagen
    int hitboxX = x + (90 - hitboxWidth) / 2;  // centrado horizontalmente
    int hitboxY = y + (90 - hitboxHeight) / 2; // centrado verticalmente

    return new Rectangle(hitboxX, hitboxY, hitboxWidth, hitboxHeight);
}

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}