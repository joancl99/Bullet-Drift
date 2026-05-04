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

    private Player player;
    private ArrayList<Enemy> enemies;
    private ArrayList<PowerUps> powerUps; // 👈 ahora es una lista
    private int score;
    private Random rand;
    private boolean gameOver;
    private boolean debugHitboxes;
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
        backgroundImage = new ImageIcon("Images/fondo1.png").getImage();

        // Añadimos un powerup inicial en el centro (ejemplo)
        powerUps.add(new PowerUps(180,330, "disparoRapido"));

        // KeyListener delegado al player
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                player.getKeyAdapter().keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_F1) {
                    debugHitboxes = !debugHitboxes;
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER && gameOver) {
                    resetGame();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                player.getKeyAdapter().keyReleased(e);
            }
        });

        // **MouseListener para disparar**
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    player.shoot();
                }
            }
        });

        // Timer principal
        Timer gameTimer = new Timer(20, e -> {
            if (!gameOver) {
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
        if (powerUps.size() < 3 && rand.nextInt(500) == 0) { // probabilidad baja
            int x = rand.nextInt(getWidth() - 50);
            int y = rand.nextInt(getHeight() - 50);

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
                enemyIterator.remove();

                if (player.hasShield()) {
                    player.deactivateShield();
                    continue;
                }

                player.loseLife(1);
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
                break;
            case "escudo":
                player.activateShield(SHIELD_DURATION_MS);
                break;
            case "disparoRapido":
                player.activateRapidFire(RAPID_FIRE_DURATION_MS);
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

        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("¡Has perdido!", getWidth() / 2 - 150, getHeight() / 2 - 50);
            g.drawString("Presiona ENTER para reiniciar", getWidth() / 2 - 200, getHeight() / 2);
        }
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

    public void resetGame() {
        score = 0;
        enemies.clear();
        powerUps.clear(); // 👈 limpiar también los powerups
        player.resetState();
        gameOver = false;
        repaint();
    }
}
