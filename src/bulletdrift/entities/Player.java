package bulletdrift.entities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class Player extends JPanel {
    private static final double BASE_SPEED = 10;
    private static final double SPEED_BOOST_MULTIPLIER = 1.7;
    private static final int PROJECTILE_SPEED = 30;
    private static final int PLAYER_BASE_WIDTH = 25;
    private static final int PLAYER_BASE_HEIGHT = 35;
    private static final int INITIAL_LIVES = 3;
    private static final int MAX_HEALTH = 60;
    private static final int MOVEMENT_TIMER_DELAY_MS = 16;
    private static final int NORMAL_SHOOT_COOLDOWN_MS = 550;
    private static final int RAPID_SHOOT_COOLDOWN_MS = 120;
    private static final int DEFAULT_PANEL_WIDTH = 800;
    private static final int DEFAULT_PANEL_HEIGHT = 600;
    private static final double HITBOX_WIDTH_SCALE = 0.58;
    private static final double HITBOX_HEIGHT_SCALE = 0.70;
    private static final int PROJECTILE_SPAWN_OFFSET = 5;

    private int x, y;
    private boolean facingLeft, facingRight, facingUp, facingDown;
    private Image playerImage;
    private LinkedHashSet<Integer> pressedKeys = new LinkedHashSet<>();
    private Timer movementTimer;
    private int width, height; 
    private int panelWidth, panelHeight; 

    ArrayList<Projectile> projectiles;

    private long lastShootTime;

    private int lives;
    private int health;
    private boolean rapidFire;
    private boolean speedBoost;
    private boolean bombShot;
    private boolean fireShot;
    private boolean magnetActive;
    private boolean shieldActive;
    private boolean invulnerable;
    private long rapidFireEndTime;
    private long speedBoostEndTime;
    private long bombShotEndTime;
    private long fireShotEndTime;
    private long magnetEndTime;
    private long shieldEndTime;
    private long invulnerabilityEndTime;

    public Player(int panelWidth, int panelHeight) {
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;

        updateScale();

        this.x = (panelWidth - width) / 2;
        this.y = (panelHeight - height) / 2;

        this.projectiles = new ArrayList<>();
        this.playerImage = new ImageIcon("Images/MainCharacter.png").getImage();
        setFocusable(true);

        movementTimer = new Timer(MOVEMENT_TIMER_DELAY_MS, e -> updateMovement());
        movementTimer.start();

        lastShootTime = 0;

        lives = INITIAL_LIVES;
        health = MAX_HEALTH;
        rapidFire = false;
        speedBoost = false;
        bombShot = false;
        fireShot = false;
        magnetActive = false;
        shieldActive = false;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    shoot();
                }
            }
        });
    }

    public void updateScale() {
        double scaleX = panelWidth / (double) DEFAULT_PANEL_WIDTH;
        double scaleY = panelHeight / (double) DEFAULT_PANEL_HEIGHT;

        width = (int) (PLAYER_BASE_WIDTH * scaleX);
        height = (int) (PLAYER_BASE_HEIGHT * scaleY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int centerX = x + width / 2;
        int centerY = y + height / 2;

        boolean shouldDrawPlayer = !invulnerable || (System.currentTimeMillis() / 100) % 2 == 0;
        if (shouldDrawPlayer) {
            if (facingLeft) {
                g2d.rotate(Math.toRadians(180), centerX, centerY);
                g2d.drawImage(playerImage, x, y, width, height, this);
                g2d.rotate(-Math.toRadians(180), centerX, centerY);
            } else if (facingRight) {
                g2d.drawImage(playerImage, x, y, width, height, this);
            } else if (facingUp) {
                g2d.rotate(Math.toRadians(270), centerX, centerY);
                g2d.drawImage(playerImage, x, y, width, height, this);
                g2d.rotate(-Math.toRadians(270), centerX, centerY);
            } else if (facingDown) {
                g2d.rotate(Math.toRadians(90), centerX, centerY);
                g2d.drawImage(playerImage, x, y, width, height, this);
                g2d.rotate(-Math.toRadians(90), centerX, centerY);
            } else {
                g2d.drawImage(playerImage, x, y, width, height, this);
            }
        }

        if (shieldActive) {
            g2d.setColor(new Color(0, 200, 255, 100));
            g2d.fillOval(x - 10, y - 10, width + 20, height + 20);
        }

        for (Projectile p : projectiles) {
            p.paint(g);
        }
    }

    public void render(Graphics g) {
        paintComponent(g);
    }

    private void updateMovement() {
        double dx = 0, dy = 0;

        if (pressedKeys.contains(KeyEvent.VK_W) || pressedKeys.contains(KeyEvent.VK_UP)) dy -= 1;
        if (pressedKeys.contains(KeyEvent.VK_S) || pressedKeys.contains(KeyEvent.VK_DOWN)) dy += 1;
        if (pressedKeys.contains(KeyEvent.VK_A) || pressedKeys.contains(KeyEvent.VK_LEFT)) dx -= 1;
        if (pressedKeys.contains(KeyEvent.VK_D) || pressedKeys.contains(KeyEvent.VK_RIGHT)) dx += 1;

        if (dx != 0 || dy != 0) {
            double length = Math.sqrt(dx * dx + dy * dy);
            double speed = speedBoost ? BASE_SPEED * SPEED_BOOST_MULTIPLIER : BASE_SPEED;
            dx = dx / length * speed;
            dy = dy / length * speed;
            x += dx;
            y += dy;
        }

        int currentPanelWidth = getParent() != null ? getParent().getWidth() : panelWidth;
        int currentPanelHeight = getParent() != null ? getParent().getHeight() : panelHeight;
        x = Math.max(0, Math.min(x, currentPanelWidth - width));
        y = Math.max(0, Math.min(y, currentPanelHeight - height));

        if (!pressedKeys.isEmpty()) {
            int lastKey = -1;
            for (int key : pressedKeys) lastKey = key;
            switch (lastKey) {
                case KeyEvent.VK_W: case KeyEvent.VK_UP: setDirection(false, false, true, false); break;
                case KeyEvent.VK_S: case KeyEvent.VK_DOWN: setDirection(false, false, false, true); break;
                case KeyEvent.VK_A: case KeyEvent.VK_LEFT: setDirection(true, false, false, false); break;
                case KeyEvent.VK_D: case KeyEvent.VK_RIGHT: setDirection(false, true, false, false); break;
            }
        }

        if (shieldActive && System.currentTimeMillis() > shieldEndTime) {
            shieldActive = false;
        }

        if (rapidFire && System.currentTimeMillis() > rapidFireEndTime) {
            rapidFire = false;
        }

        if (speedBoost && System.currentTimeMillis() > speedBoostEndTime) {
            speedBoost = false;
        }

        if (bombShot && System.currentTimeMillis() > bombShotEndTime) {
            bombShot = false;
        }

        if (fireShot && System.currentTimeMillis() > fireShotEndTime) {
            fireShot = false;
        }

        if (magnetActive && System.currentTimeMillis() > magnetEndTime) {
            magnetActive = false;
        }

        if (invulnerable && System.currentTimeMillis() > invulnerabilityEndTime) {
            invulnerable = false;
        }

        repaint();
    }

    private void setDirection(boolean left, boolean right, boolean up, boolean down) {
        facingLeft = left;
        facingRight = right;
        facingUp = up;
        facingDown = down;
    }

    public KeyAdapter getKeyAdapter() {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP ||
                    code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN ||
                    code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT ||
                    code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
                    pressedKeys.remove(code);
                    pressedKeys.add(code);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                pressedKeys.remove(e.getKeyCode());
            }
        };
    }

    public Rectangle getHitBox() {
        int hitboxWidth = (int) (width * HITBOX_WIDTH_SCALE);
        int hitboxHeight = (int) (height * HITBOX_HEIGHT_SCALE);
        int hitboxX = x + (width - hitboxWidth) / 2;
        int hitboxY = y + (height - hitboxHeight) / 2;

        return new Rectangle(hitboxX, hitboxY, hitboxWidth, hitboxHeight);
    }

    public void shoot() {
        long now = System.currentTimeMillis();
        int shootCooldownMs = rapidFire ? RAPID_SHOOT_COOLDOWN_MS : NORMAL_SHOOT_COOLDOWN_MS;
        if (now - lastShootTime < shootCooldownMs) return;

        int currentPanelWidth = getParent() != null ? getParent().getWidth() : DEFAULT_PANEL_WIDTH;
        int currentPanelHeight = getParent() != null ? getParent().getHeight() : DEFAULT_PANEL_HEIGHT;

        int speed = PROJECTILE_SPEED;
        boolean projectileCreated = true;
        Projectile.Type projectileType = getProjectileType();

        if (facingUp) 
            projectiles.add(new Projectile(x + width / 2 - PROJECTILE_SPAWN_OFFSET, y, 0, -speed, currentPanelWidth, currentPanelHeight, "up", projectileType));
        else if (facingDown) 
            projectiles.add(new Projectile(x + width / 2 - PROJECTILE_SPAWN_OFFSET, y + height, 0, speed, currentPanelWidth, currentPanelHeight, "down", projectileType));
        else if (facingLeft) 
            projectiles.add(new Projectile(x, y + height / 2 - PROJECTILE_SPAWN_OFFSET, -speed, 0, currentPanelWidth, currentPanelHeight, "left", projectileType));
        else if (facingRight) 
            projectiles.add(new Projectile(x + width, y + height / 2 - PROJECTILE_SPAWN_OFFSET, speed, 0, currentPanelWidth, currentPanelHeight, "right", projectileType));
        else
            projectileCreated = false;

        if (projectileCreated) {
            lastShootTime = now;
        }
    }

    private Projectile.Type getProjectileType() {
        if (bombShot) return Projectile.Type.BOMB;
        if (fireShot) return Projectile.Type.FIRE;
        return Projectile.Type.NORMAL;
    }

    public void updateProjectiles(int panelWidth, int panelHeight) {
        ArrayList<Projectile> toRemove = new ArrayList<>();
        for (Projectile projectile : projectiles) {
            projectile.move();
            if (projectile.getX() < 0 || projectile.getX() > panelWidth ||
                projectile.getY() < 0 || projectile.getY() > panelHeight) {
                toRemove.add(projectile);
            }
        }
        projectiles.removeAll(toRemove);
    }

    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }

    public void setPanelSize(int panelWidth, int panelHeight) {
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
        updateScale();
    }

    public void addLife(int amount) {
        lives += amount;
    }

    public int getLives() {
        return lives;
    }

    public void loseLife(int amount) {
        lives = Math.max(0, lives - amount);
    }

    public void takeDamage(int amount) {
        health = Math.max(0, health - amount);
    }

    public void heal(int amount) {
        health = Math.min(MAX_HEALTH, health + amount);
    }

    public boolean hasFullHealth() {
        return health >= MAX_HEALTH;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return MAX_HEALTH;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public void activateInvulnerability(long durationMs) {
        invulnerable = true;
        invulnerabilityEndTime = System.currentTimeMillis() + durationMs;
    }

    public boolean isInvulnerable() {
        return invulnerable;
    }

    public void clearMovementInput() {
        pressedKeys.clear();
    }

    public void setRapidFire(boolean active) {
        rapidFire = active;
    }

    public void activateRapidFire(long durationMs) {
        rapidFire = true;
        lastShootTime = 0;
        rapidFireEndTime = System.currentTimeMillis() + durationMs;
    }

    public boolean isRapidFire() {
        return rapidFire;
    }

    public void activateSpeedBoost(long durationMs) {
        speedBoost = true;
        speedBoostEndTime = System.currentTimeMillis() + durationMs;
    }

    public boolean isSpeedBoost() {
        return speedBoost;
    }

    public int getSpeedBoostSecondsLeft() {
        if (!speedBoost) return 0;
        return Math.max(0, (int) Math.ceil((speedBoostEndTime - System.currentTimeMillis()) / 1000.0));
    }

    public void activateBombShot(long durationMs) {
        bombShot = true;
        fireShot = false;
        bombShotEndTime = System.currentTimeMillis() + durationMs;
    }

    public void activateFireShot(long durationMs) {
        fireShot = true;
        bombShot = false;
        fireShotEndTime = System.currentTimeMillis() + durationMs;
    }

    public void activateMagnet(long durationMs) {
        magnetActive = true;
        magnetEndTime = System.currentTimeMillis() + durationMs;
    }

    public boolean isMagnetActive() {
        return magnetActive;
    }

    public int getCenterX() {
        return x + width / 2;
    }

    public int getCenterY() {
        return y + height / 2;
    }

    public int getRapidFireSecondsLeft() {
        if (!rapidFire) return 0;
        return Math.max(0, (int) Math.ceil((rapidFireEndTime - System.currentTimeMillis()) / 1000.0));
    }

    public void activateShield(long durationMs) {
        shieldActive = true;
        shieldEndTime = System.currentTimeMillis() + durationMs;
    }

    public boolean hasShield() {
        return shieldActive;
    }

    public int getShieldSecondsLeft() {
        if (!shieldActive) return 0;
        return Math.max(0, (int) Math.ceil((shieldEndTime - System.currentTimeMillis()) / 1000.0));
    }

    public void deactivateShield() {
        shieldActive = false;
        invulnerable = false;
    }

    public void resetState() {
        lives = INITIAL_LIVES;
        health = MAX_HEALTH;
        rapidFire = false;
        speedBoost = false;
        bombShot = false;
        fireShot = false;
        magnetActive = false;
        shieldActive = false;
        invulnerable = false;
        rapidFireEndTime = 0;
        speedBoostEndTime = 0;
        bombShotEndTime = 0;
        fireShotEndTime = 0;
        magnetEndTime = 0;
        shieldEndTime = 0;
        invulnerabilityEndTime = 0;
        lastShootTime = 0;
        projectiles.clear();
        pressedKeys.clear();
        x = (panelWidth - width) / 2;
        y = (panelHeight - height) / 2;
    }

    public void resetAfterLifeLost(long invulnerabilityDurationMs) {
        health = MAX_HEALTH;
        rapidFire = false;
        speedBoost = false;
        bombShot = false;
        fireShot = false;
        magnetActive = false;
        shieldActive = false;
        rapidFireEndTime = 0;
        speedBoostEndTime = 0;
        bombShotEndTime = 0;
        fireShotEndTime = 0;
        magnetEndTime = 0;
        shieldEndTime = 0;
        lastShootTime = 0;
        projectiles.clear();
        pressedKeys.clear();
        centerInPanel();
        activateInvulnerability(invulnerabilityDurationMs);
    }

    private void centerInPanel() {
        int currentPanelWidth = getParent() != null ? getParent().getWidth() : panelWidth;
        int currentPanelHeight = getParent() != null ? getParent().getHeight() : panelHeight;
        x = (currentPanelWidth - width) / 2;
        y = (currentPanelHeight - height) / 2;
    }
}
