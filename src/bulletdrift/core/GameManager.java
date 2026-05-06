package bulletdrift.core;

import bulletdrift.entities.Enemy;
import bulletdrift.entities.Player;
import bulletdrift.entities.PowerUps;
import bulletdrift.rendering.HudRenderer;
import bulletdrift.spawning.EnemySpawner;
import bulletdrift.spawning.PowerUpSpawner;
import bulletdrift.systems.CollisionManager;
import bulletdrift.systems.PowerUpSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class GameManager extends JPanel {
    private static final int DESIGN_WIDTH = 1920;
    private static final int DESIGN_HEIGHT = 1080;
    private static final int MIN_PANEL_WIDTH = 960;
    private static final int MIN_PANEL_HEIGHT = 540;
    private static final double INITIAL_SCREEN_USAGE = 0.90;
    private static final int GAME_TIMER_DELAY_MS = 20;
    private static final long DAMAGE_INVULNERABILITY_MS = 1000;
    private Player player;
    private ArrayList<Enemy> enemies;
    private ArrayList<PowerUps> powerUps;
    private GameSession session;
    private EnemySpawner enemySpawner;
    private PowerUpSpawner powerUpSpawner;
    private CollisionManager collisionManager;
    private HudRenderer hudRenderer;
    private boolean debugHitboxes;
    private boolean firing;
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
        session = new GameSession();
        Random rand = new Random();
        enemySpawner = new EnemySpawner(rand);
        powerUpSpawner = new PowerUpSpawner(rand);
        collisionManager = new CollisionManager(new PowerUpSystem());
        hudRenderer = new HudRenderer();
        debugHitboxes = false;
        firing = false;
        backgroundImage = new ImageIcon("Images/fondo1.png").getImage();

        powerUps.add(new PowerUps(180, 330, PowerUps.TYPE_RAPID_FIRE));

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

                if (keyCode == KeyEvent.VK_ENTER && session.isGameOver()) {
                    resetGame();
                    return;
                }

                if (keyCode == KeyEvent.VK_ESCAPE && !session.isGameOver()) {
                    togglePause();
                    return;
                }

                if (session.isPaused()) {
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
                if (session.isPaused()) return;
                player.getKeyAdapter().keyReleased(e);
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (session.isPaused()) {
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
            if (!session.isGameOver() && !session.isPaused()) {
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
        enemySpawner.generateEnemy(enemies, getWidth(), session.getWave());
    }

    private void generatePowerUps() {
        powerUpSpawner.generatePowerUp(powerUps, getWidth(), getHeight());
    }

    private void moveEnemies() {
        for (Enemy enemy : enemies) {
            enemy.moveDownEnemy(getHeight());
        }
        enemies.removeIf(e -> e.getY() > getHeight());
    }

    private void checkCollisions() {
        CollisionManager.CollisionResult result = collisionManager.checkCollisions(
            player,
            enemies,
            powerUps,
            getWidth(),
            getHeight(),
            DAMAGE_INVULNERABILITY_MS
        );

        if (result.isPlayerLifeLost()) {
            handlePlayerLifeLost();
            return;
        }

        if (result.getScoreToAdd() > 0) {
            session.addScore(result.getScoreToAdd());
        }

        if (result.hasFeedback()) {
            session.showPowerUpFeedback(result.getFeedbackText(), result.getFeedbackColor());
        }
    }

    private void handlePlayerLifeLost() {
        int waveStartScore = session.getWaveStartScore();
        player.loseLife(1);

        if (player.getLives() <= 0) {
            session.setGameOver(true);
            repaint();
            return;
        }

        session.setScore(waveStartScore);
        enemies.clear();
        powerUps.clear();
        player.resetAfterLifeLost(DAMAGE_INVULNERABILITY_MS);
        session.clearWaveFeedback();
        firing = false;
        session.showPowerUpFeedback("VIDA PERDIDA", new Color(255, 90, 90));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        player.render(g);


        for (Enemy enemy : enemies) {
            enemy.paint(g, getWidth(), getHeight());
        }

        for (PowerUps powerUp : powerUps) {
            powerUp.paint(g, false, getWidth(), getHeight());
        }


        hudRenderer.draw(
            g,
            getWidth(),
            getHeight(),
            player,
            enemies,
            powerUps,
            session.getScore(),
            session.getWave(),
            debugHitboxes,
            session.isPaused(),
            session.isGameOver(),
            session.getPowerUpFeedbackText(),
            session.getPowerUpFeedbackColor(),
            session.getPowerUpFeedbackEndTime(),
            session.getWaveFeedbackText(),
            session.getWaveFeedbackEndTime()
        );
    }

    private void togglePause() {
        setPaused(!session.isPaused());
    }

    private void setPaused(boolean paused) {
        session.setPaused(paused);
        firing = false;
        if (paused) {
            player.clearMovementInput();
        }
        repaint();
    }

    private void handlePauseClick(Point point) {
        if (hudRenderer.getResumeButtonBounds(getWidth(), getHeight()).contains(point)) {
            setPaused(false);
        } else if (hudRenderer.getExitButtonBounds(getWidth(), getHeight()).contains(point)) {
            System.exit(0);
        }
    }

    public void resetGame() {
        enemies.clear();
        powerUps.clear();
        player.resetState();
        session.reset();
        firing = false;
        repaint();
    }
}
