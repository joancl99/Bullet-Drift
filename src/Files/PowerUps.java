package Files;

import java.awt.*;
import javax.swing.ImageIcon;

public class PowerUps {
    private int x, y;
    private int width = 55, height = 60; // tamaño del power up
    private Image powerUpImage;
    private String type; // vida, escudo, disparoRapido

    public PowerUps(int x, int y, String type) {
        this.x = x;
        this.y = y;
        this.type = type;

        // Cargar imagen según el tipo
        switch (type) {
            case "vida":
                this.powerUpImage = new ImageIcon("Images/1.png").getImage();
                break;
            case "escudo":
                this.powerUpImage = new ImageIcon("Images/2.png").getImage();
                break;
            case "disparoRapido":
                this.powerUpImage = new ImageIcon("Images/3.png").getImage();
                break;
            default:
                this.powerUpImage = new ImageIcon("Images/4.png").getImage(); // fallback
        }
    }

    public void paint(Graphics g, boolean debug) {
        // Dibuja la imagen del power-up
        g.drawImage(powerUpImage, x, y, width, height, null);

        // Dibujar hitbox en rojo si debug = true
        if (debug) {
            g.setColor(Color.RED);
            Rectangle hitBox = getHitBoxPowerUps();
            g.drawRect(hitBox.x, hitBox.y, hitBox.width, hitBox.height);
        }
    }

    public Rectangle getHitBoxPowerUps() {
        return new Rectangle(x, y, width, height);
    }

    public String getType() {
        return type;
    }
}
