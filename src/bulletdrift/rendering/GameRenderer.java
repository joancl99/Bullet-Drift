package bulletdrift.rendering;

import bulletdrift.entities.Enemy;
import bulletdrift.entities.KeyObjective;
import bulletdrift.entities.Player;
import bulletdrift.entities.Portal;
import bulletdrift.entities.PowerUp;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

public class GameRenderer {
    public void drawScene(
        Graphics g,
        int panelWidth,
        int panelHeight,
        Image backgroundImage,
        ImageObserver imageObserver,
        Player player,
        ArrayList<Enemy> enemies,
        ArrayList<PowerUp> powerUps,
        KeyObjective keyObjective,
        Portal portal
    ) {
        g.drawImage(backgroundImage, 0, 0, panelWidth, panelHeight, imageObserver);
        player.render(g);

        for (Enemy enemy : enemies) {
            enemy.paint(g, panelWidth, panelHeight);
        }

        for (PowerUp powerUp : powerUps) {
            powerUp.paint(g, false, panelWidth, panelHeight);
        }

        if (keyObjective != null) {
            keyObjective.paint(g, panelWidth, panelHeight);
        }

        if (portal != null) {
            portal.paint(g, panelWidth, panelHeight);
        }
    }
}
