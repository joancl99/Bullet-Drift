package bulletdrift.systems;

import bulletdrift.entities.Player;
import bulletdrift.entities.PowerUps;

import java.awt.Color;

public class PowerUpSystem {
    private static final long SHIELD_DURATION_MS = 5000;
    private static final long RAPID_FIRE_DURATION_MS = 5000;
    private static final int LIFE_POWER_UP_HEAL_AMOUNT = 20;

    public PowerUpFeedback apply(PowerUps powerUp, Player player) {
        switch (powerUp.getType()) {
            case PowerUps.TYPE_LIFE:
                if (player.hasFullHealth()) {
                    player.addLife(1);
                    return new PowerUpFeedback("+1 VIDA", new Color(80, 255, 120));
                }

                player.heal(LIFE_POWER_UP_HEAL_AMOUNT);
                return new PowerUpFeedback("+" + LIFE_POWER_UP_HEAL_AMOUNT + " HP", new Color(80, 255, 120));
            case PowerUps.TYPE_SHIELD:
                player.activateShield(SHIELD_DURATION_MS);
                return new PowerUpFeedback("ESCUDO", new Color(80, 220, 255));
            case PowerUps.TYPE_RAPID_FIRE:
                player.activateRapidFire(RAPID_FIRE_DURATION_MS);
                return new PowerUpFeedback("DISPARO RAPIDO", new Color(255, 220, 80));
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
