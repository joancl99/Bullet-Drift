package Files;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GameManager extends JPanel {
    private static final int DESIGN_WIDTH = 1920;
    private static final int DESIGN_HEIGHT = 1080;
    private static final int MIN_PANEL_WIDTH = 960;
    private static final int MIN_PANEL_HEIGHT = 540;
    private static final double INITIAL_SCREEN_USAGE = 0.90;
    private static final int GAME_TIMER_DELAY_MS = 20;
    private static final int POINTS_PER_WAVE = 100;
    private static final int MAX_POWER_UPS = 3;
    private static final int POWER_UP_SPAWN_CHANCE = 500;
    private static final int POWER_UP_SPAWN_MARGIN = 50;
    private static final int SCORE_PER_ENEMY = 10;
    private static final String POWER_UP_LIFE = "vida";
    private static final String POWER_UP_SHIELD = "escudo";
    private static final String POWER_UP_RAPID_FIRE = "disparoRapido";
    private static final long SHIELD_DURATION_MS = 5000;
    private static final long RAPID_FIRE_DURATION_MS = 5000;
    private static final long DAMAGE_INVULNERABILITY_MS = 1000;
    private static final int POWER_UP_TOP_MARGIN = 220;
    private static final long POWER_UP_FEEDBACK_DURATION_MS = 1200;
    private static final long WAVE_FEEDBACK_DURATION_MS = 1800;
    private static final int ENEMY_COLLISION_DAMAGE = 20;
    private static final int LIFE_POWER_UP_HEAL_AMOUNT = 20;
    private static final int HUD_X = 40;
    private static final int HUD_SCORE_Y = 150;
    private static final int HUD_LIVES_Y = 200;
    private static final int HEALTH_BAR_Y = 220;
    private static final int HEALTH_BAR_WIDTH = 260;
    private static final int HEALTH_BAR_HEIGHT = 26;
    private static final int HUD_POWER_UP_Y = 285;
    private static final int HUD_LINE_HEIGHT = 40;
    private static final int WAVE_HUD_Y = 70;
    private static final int WAVE_FEEDBACK_Y_OFFSET = 120;
    private static final int PAUSE_BUTTON_WIDTH = 300;
    private static final int PAUSE_BUTTON_HEIGHT = 60;

    private Player player;
    private ArrayList<Enemy> enemies;
    private ArrayList<PowerUps> powerUps;
    private EnemySpawner enemySpawner;
    private int score;
    private Random rand;
    private boolean gameOver;
    private boolean debugHitboxes;
    private boolean paused;
    private boolean firing;
    private String powerUpFeedbackText;
    private Color powerUpFeedbackColor;
    private long powerUpFeedbackEndTime;
    private int lastAnnouncedWave;
    private String waveFeedbackText;
    private long waveFeedbackEndTime;
    private Image backgroundImage;

    public GameManager() {
        Dimension initialPanelSize = getInitialPanelSize();
        this.setPreferredSize(initialPanelSize);
        this.setMinimumSize(new Dimension(MIN_PANEL_WIDTH, MIN_PANEL_HEIGHT));
        this.setFocusable(true);

        int panelWidth = initialPanelSize.width;
        int panelHeight = initialPanelSize.height;

        player = new Player(panelWidth, panelHeight);
        enemies = new ArrayList<>();
        powerUps = new ArrayList<>();
        rand = new Random();
        enemySpawner = new EnemySpawner(rand);
        score = 0;
        gameOver = false;
        debugHitboxes = false;
        paused = false;
        firing = false;
        powerUpFeedbackText = "";
        powerUpFeedbackColor = Color.WHITE;
        powerUpFeedbackEndTime = 0;
        lastAnnouncedWave = 0;
        waveFeedbackText = "";
        waveFeedbackEndTime = 0;
        backgroundImage = new ImageIcon("Images/fondo1.png").getImage();

        powerUps.add(new PowerUps(180, 330, POWER_UP_RAPID_FIRE));

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                player.setPanelSize(getWidth(), getHeight());
            }
        });

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

        Timer gameTimer = new Timer(GAME_TIMER_DELAY_MS, e -> {
            if (!gameOver && !paused) {
                if (firing) {
                    player.shoot();
                }
                generateEnemies();
                generatePowerUps();
                moveEnemies();
                player.updateProjectiles(getWidth(), getHeight());
                checkCollisions();
                repaint();
            }
        });
        gameTimer.start();
    }

    private Dimension getInitialPanelSize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int availableWidth = (int) (screenSize.width * INITIAL_SCREEN_USAGE);
        int availableHeight = (int) (screenSize.height * INITIAL_SCREEN_USAGE);
        double targetAspectRatio = DESIGN_WIDTH / (double) DESIGN_HEIGHT;

        int width = Math.min(DESIGN_WIDTH, availableWidth);
        int height = (int) (width / targetAspectRatio);
        if (height > availableHeight) {
            height = Math.min(DESIGN_HEIGHT, availableHeight);
            width = (int) (height * targetAspectRatio);
        }

        width = Math.max(MIN_PANEL_WIDTH, width);
        height = Math.max(MIN_PANEL_HEIGHT, height);
        return new Dimension(width, height);
    }

    private void generateEnemies() {
        enemySpawner.generateEnemy(enemies, getWidth(), getWave());
    }

    private int getWave() {
        return score / POINTS_PER_WAVE;
    }

    private void generatePowerUps() {
        if (getWidth() <= POWER_UP_SPAWN_MARGIN || getHeight() <= POWER_UP_TOP_MARGIN + POWER_UP_SPAWN_MARGIN) return;

        if (powerUps.size() < MAX_POWER_UPS && rand.nextInt(POWER_UP_SPAWN_CHANCE) == 0) {
            int x = rand.nextInt(getWidth() - POWER_UP_SPAWN_MARGIN);
            int availableHeight = Math.max(1, getHeight() - POWER_UP_TOP_MARGIN - POWER_UP_SPAWN_MARGIN);
            int y = POWER_UP_TOP_MARGIN + rand.nextInt(availableHeight);

            String[] tipos = {POWER_UP_LIFE, POWER_UP_SHIELD, POWER_UP_RAPID_FIRE};
            String tipo = tipos[rand.nextInt(tipos.length)];

            powerUps.add(new PowerUps(x, y, tipo));
        }
    }

    private void moveEnemies() {
        for (Enemy enemy : enemies) {
            enemy.moveDownEnemy(getHeight());
        }
        enemies.removeIf(e -> e.getY() > getHeight());
    }

    private void checkCollisions() {
        Rectangle playerHitbox = player.getHitBox();

        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            if (playerHitbox.intersects(enemy.getHitBoxEnemy(getWidth(), getHeight()))) {
                if (player.isInvulnerable()) {
                    continue;
                }

                enemyIterator.remove();

                if (player.hasShield()) {
                    player.deactivateShield();
                    continue;
                }

                player.takeDamage(ENEMY_COLLISION_DAMAGE);
                if (player.isDead()) {
                    handlePlayerLifeLost();
                    return;
                }

                showPowerUpFeedback("-" + ENEMY_COLLISION_DAMAGE + " HP", new Color(255, 90, 90));
                player.activateInvulnerability(DAMAGE_INVULNERABILITY_MS);

                continue;
            }

            for (Projectile projectile : new ArrayList<>(player.projectiles)) {
                if (projectile.getHitBox().intersects(enemy.getHitBoxEnemy(getWidth(), getHeight()))) {
                    player.projectiles.remove(projectile);
                    if (enemy.takeHit()) {
                        enemyIterator.remove();
                        score += SCORE_PER_ENEMY;
                        updateWaveFeedback();
                    }
                    break;
                }
            }
        }

        Iterator<PowerUps> powerUpIterator = powerUps.iterator();
        while (powerUpIterator.hasNext()) {
            PowerUps powerUp = powerUpIterator.next();
            if (playerHitbox.intersects(powerUp.getHitBoxPowerUps(getWidth(), getHeight()))) {
                aplicarPowerUp(powerUp);
                powerUpIterator.remove();
            }
        }
    }

    private void handlePlayerLifeLost() {
        int waveStartScore = getWave() * POINTS_PER_WAVE;
        player.loseLife(1);

        if (player.getLives() <= 0) {
            gameOver = true;
            repaint();
            return;
        }

        score = waveStartScore;
        enemies.clear();
        powerUps.clear();
        player.resetAfterLifeLost(DAMAGE_INVULNERABILITY_MS);
        lastAnnouncedWave = getWave();
        waveFeedbackText = "";
        waveFeedbackEndTime = 0;
        firing = false;
        showPowerUpFeedback("VIDA PERDIDA", new Color(255, 90, 90));
        repaint();
    }

    private void aplicarPowerUp(PowerUps powerUp) {
        switch (powerUp.getType()) {
            case POWER_UP_LIFE:
                if (player.hasFullHealth()) {
                    player.addLife(1);
                    showPowerUpFeedback("+1 VIDA", new Color(80, 255, 120));
                } else {
                    player.heal(LIFE_POWER_UP_HEAL_AMOUNT);
                    showPowerUpFeedback("+" + LIFE_POWER_UP_HEAL_AMOUNT + " HP", new Color(80, 255, 120));
                }
                break;
            case POWER_UP_SHIELD:
                player.activateShield(SHIELD_DURATION_MS);
                showPowerUpFeedback("ESCUDO", new Color(80, 220, 255));
                break;
            case POWER_UP_RAPID_FIRE:
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
            enemy.paint(g, getWidth(), getHeight());
        }

        for (PowerUps powerUp : powerUps) {
            powerUp.paint(g, false, getWidth(), getHeight());
        }


        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString("Puntos: " + score, HUD_X, HUD_SCORE_Y);
        g.drawString("Vidas: " + player.getLives(), HUD_X, HUD_LIVES_Y);
        drawPlayerHealthBar(g);
        drawWaveHud(g);

        g.setFont(new Font("Arial", Font.BOLD, 28));
        int powerUpTextY = HUD_POWER_UP_Y;
        if (player.hasShield()) {
            g.drawString("Escudo: " + player.getShieldSecondsLeft() + "s", HUD_X, powerUpTextY);
            powerUpTextY += HUD_LINE_HEIGHT;
        }
        if (player.isRapidFire()) {
            g.drawString("Disparo rapido: " + player.getRapidFireSecondsLeft() + "s", HUD_X, powerUpTextY);
        }

        if (debugHitboxes) {
            g.drawString("Hitboxes: ON", HUD_X, getHeight() - HUD_LINE_HEIGHT);
            drawHitboxes(g);
        }

        drawPowerUpFeedback(g);
        drawWaveFeedback(g);

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

    private void drawWaveHud(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(new Font("Arial", Font.BOLD, 46));
        String waveText = "Oleada " + getWave();
        FontMetrics metrics = g2d.getFontMetrics();
        int textX = (getWidth() - metrics.stringWidth(waveText)) / 2;

        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawString(waveText, textX + 3, WAVE_HUD_Y + 3);
        g2d.setColor(Color.WHITE);
        g2d.drawString(waveText, textX, WAVE_HUD_Y);
    }

    private void drawPlayerHealthBar(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int health = player.getHealth();
        int maxHealth = player.getMaxHealth();
        int filledWidth = (int) (HEALTH_BAR_WIDTH * (health / (double) maxHealth));

        g2d.setColor(new Color(0, 0, 0, 170));
        g2d.fillRect(HUD_X, HEALTH_BAR_Y, HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT);
        g2d.setColor(new Color(220, 45, 45));
        g2d.fillRect(HUD_X, HEALTH_BAR_Y, filledWidth, HEALTH_BAR_HEIGHT);
        g2d.setColor(Color.WHITE);
        g2d.drawRect(HUD_X, HEALTH_BAR_Y, HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT);

        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        String healthText = health + " / " + maxHealth + " HP";
        FontMetrics metrics = g2d.getFontMetrics();
        int textX = HUD_X + (HEALTH_BAR_WIDTH - metrics.stringWidth(healthText)) / 2;
        int textY = HEALTH_BAR_Y + ((HEALTH_BAR_HEIGHT - metrics.getHeight()) / 2) + metrics.getAscent();
        g2d.drawString(healthText, textX, textY);
    }

    private void updateWaveFeedback() {
        int currentWave = getWave();
        if (currentWave <= lastAnnouncedWave) return;

        lastAnnouncedWave = currentWave;
        waveFeedbackText = "OLEADA " + currentWave;
        waveFeedbackEndTime = System.currentTimeMillis() + WAVE_FEEDBACK_DURATION_MS;
    }

    private void drawWaveFeedback(Graphics g) {
        if (waveFeedbackText.isEmpty() || System.currentTimeMillis() > waveFeedbackEndTime) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(new Font("Arial", Font.BOLD, 74));
        FontMetrics metrics = g2d.getFontMetrics();
        int textX = (getWidth() - metrics.stringWidth(waveFeedbackText)) / 2;
        int textY = getHeight() / 2 - WAVE_FEEDBACK_Y_OFFSET;

        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.drawString(waveFeedbackText, textX + 4, textY + 4);
        g2d.setColor(new Color(255, 210, 80));
        g2d.drawString(waveFeedbackText, textX, textY);
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
            Rectangle enemyHitbox = enemy.getHitBoxEnemy(getWidth(), getHeight());
            g2d.drawRect(enemyHitbox.x, enemyHitbox.y, enemyHitbox.width, enemyHitbox.height);
        }

        g2d.setColor(Color.YELLOW);
        for (Projectile projectile : player.projectiles) {
            Rectangle projectileHitbox = projectile.getHitBox();
            g2d.drawRect(projectileHitbox.x, projectileHitbox.y, projectileHitbox.width, projectileHitbox.height);
        }

        g2d.setColor(Color.CYAN);
        for (PowerUps powerUp : powerUps) {
            Rectangle powerUpHitbox = powerUp.getHitBoxPowerUps(getWidth(), getHeight());
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
        return new Rectangle(getWidth() / 2 - PAUSE_BUTTON_WIDTH / 2, getHeight() / 2 - 10, PAUSE_BUTTON_WIDTH, PAUSE_BUTTON_HEIGHT);
    }

    private Rectangle getExitButtonBounds() {
        return new Rectangle(getWidth() / 2 - PAUSE_BUTTON_WIDTH / 2, getHeight() / 2 + 70, PAUSE_BUTTON_WIDTH, PAUSE_BUTTON_HEIGHT);
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
        powerUps.clear();
        player.resetState();
        gameOver = false;
        paused = false;
        firing = false;
        powerUpFeedbackText = "";
        powerUpFeedbackEndTime = 0;
        lastAnnouncedWave = 0;
        waveFeedbackText = "";
        waveFeedbackEndTime = 0;
        repaint();
    }
}
