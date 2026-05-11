package bulletdrift.systems;

import bulletdrift.entities.Player;
import bulletdrift.entities.PowerUp;

import java.awt.Color;
import java.util.Random;

public class PowerUpSystem {
    private static final long SHIELD_DURATION_MS = 5000;
    private static final long RAPID_FIRE_DURATION_MS = 5000;
    private static final long INVULNERABILITY_DURATION_MS = 4000;
    private static final long SPEED_BOOST_DURATION_MS = 5000;
    private static final long BOMB_SHOT_DURATION_MS = 6000;
    private static final long FIRE_SHOT_DURATION_MS = 5000;
    private static final long MAGNET_DURATION_MS = 8000;
    private static final int LIFE_POWER_UP_HEAL_AMOUNT = 20;
    private static final String[] MYSTERY_TYPES = {
        PowerUp.TYPE_HEALING,
        PowerUp.TYPE_SHIELD,
        PowerUp.TYPE_RAPID_FIRE,
        PowerUp.TYPE_INVULNERABILITY,
        PowerUp.TYPE_SPEED,
        PowerUp.TYPE_BOMB_SHOT,
        PowerUp.TYPE_FIRE_SHOT,
        PowerUp.TYPE_MAGNET
    };

    private Random rand = new Random();

    public PowerUpFeedback apply(PowerUp powerUp, Player player) {
        switch (powerUp.getType()) {
            case PowerUp.TYPE_LIFE:
                player.addLife(1);
                return new PowerUpFeedback("+1 VIDA", new Color(80, 255, 120));
            case PowerUp.TYPE_HEALING:
                player.heal(LIFE_POWER_UP_HEAL_AMOUNT);
                return new PowerUpFeedback("+" + LIFE_POWER_UP_HEAL_AMOUNT + " HP", new Color(80, 255, 120));
            case PowerUp.TYPE_SHIELD:
                player.activateShield(SHIELD_DURATION_MS);
                return new PowerUpFeedback("ESCUDO", new Color(80, 220, 255));
            case PowerUp.TYPE_RAPID_FIRE:
                player.activateRapidFire(RAPID_FIRE_DURATION_MS);
                return new PowerUpFeedback("DISPARO RAPIDO", new Color(255, 220, 80));
            case PowerUp.TYPE_INVULNERABILITY:
                player.activateInvulnerability(INVULNERABILITY_DURATION_MS);
                return new PowerUpFeedback("INVULNERABLE", new Color(220, 120, 255));
            case PowerUp.TYPE_SPEED:
                player.activateSpeedBoost(SPEED_BOOST_DURATION_MS);
                return new PowerUpFeedback("SUPER VELOCIDAD", new Color(80, 220, 255));
            case PowerUp.TYPE_BOMB:
                return new PowerUpFeedback("BOMBA", new Color(255, 120, 60));
            case PowerUp.TYPE_BOMB_SHOT:
                player.activateBombShot(BOMB_SHOT_DURATION_MS);
                return new PowerUpFeedback("BALAS BOMBA", new Color(255, 120, 60));
            case PowerUp.TYPE_FIRE_SHOT:
                player.activateFireShot(FIRE_SHOT_DURATION_MS);
                return new PowerUpFeedback("LANZALLAMAS", new Color(255, 80, 40));
            case PowerUp.TYPE_COIN:
                return new PowerUpFeedback("+1 MONEDA", new Color(255, 220, 80));
            case PowerUp.TYPE_KEY:
                return new PowerUpFeedback("LLAVE", new Color(180, 220, 255));
            case PowerUp.TYPE_MEGA_MUSH:
                player.activateSpeedBoost(SPEED_BOOST_DURATION_MS);
                player.activateInvulnerability(INVULNERABILITY_DURATION_MS);
                player.activateRapidFire(RAPID_FIRE_DURATION_MS);
                return new PowerUpFeedback("MEGA MUSH", new Color(255, 80, 180));
            case PowerUp.TYPE_MYSTERY_BOX:
                return apply(new PowerUp(0, 0, MYSTERY_TYPES[rand.nextInt(MYSTERY_TYPES.length)]), player);
            case PowerUp.TYPE_MAGNET:
                player.activateMagnet(MAGNET_DURATION_MS);
                return new PowerUpFeedback("IMAN", new Color(120, 200, 255));
            default:
                System.out.println("PowerUp desconocido");
                return null;
        }
    }

    public static class PowerUpFeedback {
        private String text;
        private Color color;

        public PowerUpFeedback(String text, Color color) {
            this.text = text;
            this.color = color;
        }

        public String getText() {
            return text;
        }

        public Color getColor() {
            return color;
        }
    }
}
