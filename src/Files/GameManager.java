package Files;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GameManager extends JPanel {
    private static final long SHIELD_DURATION_MS = 5000;
    private static final long RAPID_FIRE_DURATION_MS = 5000;
    private static final long DAMAGE_INVULNERABILITY_MS = 1000;
    private static final int POWER_UP_TOP_MARGIN = 220;
    private static final long POWER_UP_FEEDBACK_DURATION_MS = 1200;

    private Player player;
    private ArrayList<Enemy> enemies;
    private ArrayList<PowerUps> powerUps; // 👈 ahora es una lista
    private int score;
    private Random rand;
    private boolean gameOver;
    private boolean debugHitboxes;
    private boolean paused;
    private boolean firing;
    private String powerUpFeedbackText;
    private Color powerUpFeedbackColor;
    private long powerUpFeedbackEndTime;
    private Image backgroundImage;

    public GameManager() {
        this.setPreferredSize(new Dimension(1920, 1080));
        this.setFocusable(true);

        int panelWidth = this.getPreferredSize().width;
        int panelHeight = this.getPreferredSize().height;

        // Crear player
        player = new Player(panelWidth, panelHeight);
        enemies = new ArrayList<>();
        powerUps = new ArrayList<>(); // 👈 inicializamos lista
        rand = new Random();
        score = 0;
        gameOver = false;
        debugHitboxes = false;
        paused = false;
        firing = false;
        powerUpFeedbackText = "";
        powerUpFeedbackColor = Color.WHITE;
        powerUpFeedbackEndTime = 0;
        backgroundImage = new ImageIcon("Images/fondo1.png").getImage();

        // Añadimos un powerup inicial en el centro (ejemplo)
        powerUps.add(new PowerUps(180,330, "disparoRapido"));

        // KeyListener delegado al player
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();

                if (keyCode == KeyEvent.VK_F1) {
                    debugHitboxes = !debugHitboxes;
                }

                if (keyCode == KeyEvent.VK_ENTER && gameOver) {
                    resetGame();
                    return;
                }

                if (keyCode == KeyEvent.VK_ESCAPE && !gameOver) {
                    togglePause();
                    return;
                }

                if (paused) {
                    if (keyCode == KeyEvent.VK_ENTER) {
                        setPaused(false);
                    } else if (keyCode == KeyEvent.VK_Q) {
                        System.exit(0);
                    }
                    return;
                }

                player.getKeyAdapter().keyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (paused) return;
                player.getKeyAdapter().keyReleased(e);
            }
        });

        // **MouseListener para disparar**
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (paused) {
                    handlePauseClick(e.getPoint());
                    return;
                }

                if (SwingUtilities.isLeftMouseButton(e)) {
                    firing = true;
                    player.shoot();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    firing = false;
                }
            }
        });

        // Timer principal
        Timer gameTimer = new Timer(20, e -> {
            if (!gameOver && !paused) {
                if (firing) {
                    player.shoot();
                }
                generateEnemies();
                generatePowerUps(); // 👈 nuevo generador de powerups
                moveEnemies();
                player.updateProjectiles(getWidth(), getHeight()); // límites dinámicos
                checkCollisions();
                repaint();
            }
        });
        gameTimer.start();
    }

    private void generateEnemies() {
        if (enemies.size() < 20) {
            int x = rand.nextInt(getWidth() - 50);
            int y = -50;
            int speed = rand.nextInt(7) + 2;
            enemies.add(new Enemy(x, y, speed));
        }
    }

    // 👇 Nuevo: generación aleatoria de powerups
    private void generatePowerUps() {
        if (getWidth() <= 50 || getHeight() <= POWER_UP_TOP_MARGIN + 50) return;

        if (powerUps.size() < 3 && rand.nextInt(500) == 0) { // probabilidad baja
            int x = rand.nextInt(getWidth() - 50);
            int availableHeight = Math.max(1, getHeight() - POWER_UP_TOP_MARGIN - 50);
            int y = POWER_UP_TOP_MARGIN + rand.nextInt(availableHeight);

            // elegir un tipo aleatorio
            String[] tipos = {"vida", "escudo", "disparoRapido"};
            String tipo = tipos[rand.nextInt(tipos.length)];

            powerUps.add(new PowerUps(x, y, tipo));
        }
    }

    private void moveEnemies() {
        for (Enemy enemy : enemies) {
            enemy.moveDownEnemy(getHeight()); // límite dinámico
        }
        enemies.removeIf(e -> e.getY() > getHeight());
    }

    private void checkCollisions() {
        Rectangle playerHitbox = player.getHitBox();

        // --- Colisiones con enemigos ---
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            if (playerHitbox.intersects(enemy.getHitBoxEnemy())) {
                if (player.isInvulnerable()) {
                    continue;
                }

                enemyIterator.remove();

                if (player.hasShield()) {
                    player.deactivateShield();
                    continue;
                }

                player.loseLife(1);
                player.activateInvulnerability(DAMAGE_INVULNERABILITY_MS);
                if (player.getLives() <= 0) {
                    gameOver = true;
                    repaint();
                    return;
                }

                continue;
            }

            for (Projectile projectile : new ArrayList<>(player.projectiles)) {
                if (projectile.getHitBox().intersects(enemy.getHitBoxEnemy())) {
                    enemyIterator.remove();
                    player.projectiles.remove(projectile);
                    score += 10;
                    break;
                }
            }
        }

        // --- Colisiones con powerups ---
        Iterator<PowerUps> powerUpIterator = powerUps.iterator();
        while (powerUpIterator.hasNext()) {
            PowerUps powerUp = powerUpIterator.next();
            if (playerHitbox.intersects(powerUp.getHitBoxPowerUps())) {
                aplicarPowerUp(powerUp);
                powerUpIterator.remove();
            }
        }
    }

    private void aplicarPowerUp(PowerUps powerUp) {
        switch (powerUp.getType()) {
            case "vida":
                player.addLife(1);
                showPowerUpFeedback("+1 VIDA", new Color(80, 255, 120));
                break;
            case "escudo":
                player.activateShield(SHIELD_DURATION_MS);
                showPowerUpFeedback("ESCUDO", new Color(80, 220, 255));
                break;
            case "disparoRapido":
                player.activateRapidFire(RAPID_FIRE_DURATION_MS);
                showPowerUpFeedback("DISPARO RAPIDO", new Color(255, 220, 80));
                break;
            default:
                System.out.println("PowerUp desconocido");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        player.paintComponent(g);


        for (Enemy enemy : enemies) {
            enemy.paint(g);
        }

          // pintar powerups
        for (PowerUps powerUp : powerUps) {
            powerUp.paint(g, false); // false = no debug
        }


        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString("Puntos: " + score, 40, 150);
        g.drawString("Vidas: " + player.getLives(), 40, 200);

        g.setFont(new Font("Arial", Font.BOLD, 28));
        int powerUpTextY = 250;
        if (player.hasShield()) {
            g.drawString("Escudo: " + player.getShieldSecondsLeft() + "s", 40, powerUpTextY);
            powerUpTextY += 40;
        }
        if (player.isRapidFire()) {
            g.drawString("Disparo rapido: " + player.getRapidFireSecondsLeft() + "s", 40, powerUpTextY);
        }

        if (debugHitboxes) {
            g.drawString("Hitboxes: ON", 40, getHeight() - 40);
            drawHitboxes(g);
        }

        drawPowerUpFeedback(g);

        if (paused) {
            drawPauseMenu(g);
        }

        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("¡Has perdido!", getWidth() / 2 - 150, getHeight() / 2 - 50);
            g.drawString("Presiona ENTER para reiniciar", getWidth() / 2 - 200, getHeight() / 2);
        }
    }

    private void showPowerUpFeedback(String text, Color color) {
        powerUpFeedbackText = text;
        powerUpFeedbackColor = color;
        powerUpFeedbackEndTime = System.currentTimeMillis() + POWER_UP_FEEDBACK_DURATION_MS;
    }

    private void drawPowerUpFeedback(Graphics g) {
        if (powerUpFeedbackText.isEmpty() || System.currentTimeMillis() > powerUpFeedbackEndTime) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(new Font("Arial", Font.BOLD, 42));
        FontMetrics metrics = g2d.getFontMetrics();
        int textX = (getWidth() - metrics.stringWidth(powerUpFeedbackText)) / 2;
        int textY = 120;

        g2d.setColor(new Color(0, 0, 0, 160));
        g2d.drawString(powerUpFeedbackText, textX + 3, textY + 3);
        g2d.setColor(powerUpFeedbackColor);
        g2d.drawString(powerUpFeedbackText, textX, textY);
    }

    private void drawHitboxes(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Stroke previousStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(2));

        g2d.setColor(Color.GREEN);
        Rectangle playerHitbox = player.getHitBox();
        g2d.drawRect(playerHitbox.x, playerHitbox.y, playerHitbox.width, playerHitbox.height);

        g2d.setColor(Color.RED);
        for (Enemy enemy : enemies) {
            Rectangle enemyHitbox = enemy.getHitBoxEnemy();
            g2d.drawRect(enemyHitbox.x, enemyHitbox.y, enemyHitbox.width, enemyHitbox.height);
        }

        g2d.setColor(Color.YELLOW);
        for (Projectile projectile : player.projectiles) {
            Rectangle projectileHitbox = projectile.getHitBox();
            g2d.drawRect(projectileHitbox.x, projectileHitbox.y, projectileHitbox.width, projectileHitbox.height);
        }

        g2d.setColor(Color.CYAN);
        for (PowerUps powerUp : powerUps) {
            Rectangle powerUpHitbox = powerUp.getHitBoxPowerUps();
            g2d.drawRect(powerUpHitbox.x, powerUpHitbox.y, powerUpHitbox.width, powerUpHitbox.height);
        }

        g2d.setStroke(previousStroke);
    }

    private void togglePause() {
        setPaused(!paused);
    }

    private void setPaused(boolean paused) {
        this.paused = paused;
        firing = false;
        if (paused) {
            player.clearMovementInput();
        }
        repaint();
    }

    private void handlePauseClick(Point point) {
        if (getResumeButtonBounds().contains(point)) {
            setPaused(false);
        } else if (getExitButtonBounds().contains(point)) {
            System.exit(0);
        }
    }

    private Rectangle getResumeButtonBounds() {
        return new Rectangle(getWidth() / 2 - 150, getHeight() / 2 - 10, 300, 60);
    }

    private Rectangle getExitButtonBounds() {
        return new Rectangle(getWidth() / 2 - 150, getHeight() / 2 + 70, 300, 60);
    }

    private void drawPauseMenu(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(new Color(0, 0, 0, 170));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 56));
        g2d.drawString("PAUSA", getWidth() / 2 - 95, getHeight() / 2 - 90);

        drawMenuButton(g2d, getResumeButtonBounds(), "Reanudar");
        drawMenuButton(g2d, getExitButtonBounds(), "Salir");

        g2d.setFont(new Font("Arial", Font.BOLD, 22));
        g2d.drawString("ESC/ENTER: reanudar   Q: salir", getWidth() / 2 - 170, getHeight() / 2 + 170);
    }

    private void drawMenuButton(Graphics2D g2d, Rectangle bounds, String text) {
        g2d.setColor(new Color(40, 40, 40, 220));
        g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        g2d.setColor(Color.WHITE);
        g2d.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        g2d.setFont(new Font("Arial", Font.BOLD, 30));

        FontMetrics metrics = g2d.getFontMetrics();
        int textX = bounds.x + (bounds.width - metrics.stringWidth(text)) / 2;
        int textY = bounds.y + ((bounds.height - metrics.getHeight()) / 2) + metrics.getAscent();
        g2d.drawString(text, textX, textY);
    }

    public void resetGame() {
        score = 0;
        enemies.clear();
        powerUps.clear(); // 👈 limpiar también los powerups
        player.resetState();
        gameOver = false;
        paused = false;
        firing = false;
        powerUpFeedbackText = "";
        powerUpFeedbackEndTime = 0;
        repaint();
    }
}
