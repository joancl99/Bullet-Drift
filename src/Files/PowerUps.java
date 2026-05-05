package Files;

import java.awt.*;
import javax.swing.ImageIcon;

public class PowerUps {
    private static final int WIDTH = 55;
    private static final int HEIGHT = 60;
    private static final double HITBOX_SCALE = 0.70;

    private int x, y;
    private Image powerUpImage;
    private String type;

    public PowerUps(int x, int y, String type) {
        this.x = x;
        this.y = y;
        this.type = type;

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
                this.powerUpImage = new ImageIcon("Images/4.png").getImage();
        }
    }

    public void paint(Graphics g, boolean debug) {
        g.drawImage(powerUpImage, x, y, WIDTH, HEIGHT, null);

        if (debug) {
            g.setColor(Color.RED);
            Rectangle hitBox = getHitBoxPowerUps();
            g.drawRect(hitBox.x, hitBox.y, hitBox.width, hitBox.height);
        }
    }

    public Rectangle getHitBoxPowerUps() {
        int hitboxWidth = (int) (WIDTH * HITBOX_SCALE);
        int hitboxHeight = (int) (HEIGHT * HITBOX_SCALE);
        int hitboxX = x + (WIDTH - hitboxWidth) / 2;
        int hitboxY = y + (HEIGHT - hitboxHeight) / 2;

        return new Rectangle(hitboxX, hitboxY, hitboxWidth, hitboxHeight);
    }

    public String getType() {
        return type;
    }
}
