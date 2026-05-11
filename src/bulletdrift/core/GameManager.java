package bulletdrift.core;

import bulletdrift.entities.Enemy;
import bulletdrift.entities.Player;
import bulletdrift.entities.PowerUp;
import bulletdrift.rendering.GameRenderer;
import bulletdrift.rendering.HudRenderer;
import bulletdrift.spawning.EnemySpawner;
import bulletdrift.spawning.PowerUpSpawner;
import bulletdrift.systems.CollisionManager;
import bulletdrift.systems.GameUpdateSystem;
import bulletdrift.systems.PowerUpSystem;
import bulletdrift.systems.MovementSystem;

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
    private ArrayList<PowerUp> powerUps;
    private GameSession session;
    private GameUpdateSystem gameUpdateSystem;
    private GameRenderer gameRenderer;
    private HudRenderer hudRenderer;
    private boolean debugHitboxes;
    private boolean firing;
    private Image backgroundImage;
    private Image bossBackgroundImage;

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
        gameUpdateSystem = new GameUpdateSystem(
            new EnemySpawner(rand),
            new PowerUpSpawner(rand),
            new MovementSystem(),
            new CollisionManager(new PowerUpSystem())
        );
        gameRenderer = new GameRenderer();
        hudRenderer = new HudRenderer();
        debugHitboxes = false;
        firing = false;
        backgroundImage = new ImageIcon("src/Files/Wallpaper.png").getImage();
        bossBackgroundImage = new ImageIcon("src/Files/WallpaperBoss.png").getImage();

        powerUps.add(new PowerUp(180, 330, PowerUp.TYPE_RAPID_FIRE));

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
                    } else if (keyCode == KeyEvent.VK_R) {
                        resetGame();
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
                GameUpdateSystem.UpdateResult updateResult = gameUpdateSystem.update(
                    player,
                    enemies,
                    powerUps,
                    session,
                    getWidth(),
                    getHeight(),
                    DAMAGE_INVULNERABILITY_MS
                );
                if (updateResult.isKeyDestroyed()) {
                    session.setGameOver(true);
                    session.showPowerUpFeedback("LLAVE DESTRUIDA", new Color(255, 80, 80));
                } else if (updateResult.isPlayerLifeLost()) {
                    handlePlayerLifeLost();
                }
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
        gameRenderer.drawScene(
            g,
            getWidth(),
            getHeight(),
            session.isBossActive() ? bossBackgroundImage : backgroundImage,
            this,
            player,
            enemies,
            powerUps,
            session.getKeyObjective(),
            session.getPortal(),
            session.getBoss()
        );

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
        } else if (hudRenderer.getRestartButtonBounds(getWidth(), getHeight()).contains(point)) {
            resetGame();
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
