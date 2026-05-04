package Files;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class Player extends JPanel {
    private final double SPEED = 10;
    private final int PROJECTILESPEED = 30;
    private final int PLAYERWIDTH = 25;
    private final int PLAYERHEIGHT = 35;
    private final int INITIAL_LIVES = 3;
    private final int NORMAL_SHOOT_COOLDOWN_MS = 550;
    private final int RAPID_SHOOT_COOLDOWN_MS = 80;

    private int x, y;
    private boolean facingLeft, facingRight, facingUp, facingDown;
    private Image playerImage;
    private LinkedHashSet<Integer> pressedKeys = new LinkedHashSet<>();
    private Timer movementTimer;
    private int width, height; 
    private int panelWidth, panelHeight; 

    ArrayList<Projectile> projectiles;

    private boolean canShoot;
    private Timer shootCooldownTimer;

    // 👇 NUEVOS CAMPOS
    private int lives;
    private boolean rapidFire;
    private boolean shieldActive;
    private boolean invulnerable;
    private long rapidFireEndTime;
    private long shieldEndTime;
    private long invulnerabilityEndTime;

    public Player(int panelWidth, int panelHeight) {
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;

        updateScale();

        this.x = (panelWidth - width) / 2;
        this.y = (panelHeight - height) / 2;

        this.projectiles = new ArrayList<>();
        this.playerImage = new ImageIcon("Images/loco1.png").getImage();
        setFocusable(true);

        movementTimer = new Timer(16, e -> updateMovement());
        movementTimer.start();

        canShoot = true;
        shootCooldownTimer = new Timer(NORMAL_SHOOT_COOLDOWN_MS, e -> canShoot = true);

        // 👇 inicializamos powerups
        lives = INITIAL_LIVES;
        rapidFire = false;
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
        double scaleX = panelWidth / 800.0;
        double scaleY = panelHeight / 600.0;

        width = (int) (PLAYERWIDTH * scaleX);
        height = (int) (PLAYERHEIGHT * scaleY);
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

        // Dibujar escudo si está activo
        if (shieldActive) {
            g2d.setColor(new Color(0, 200, 255, 100));
            g2d.fillOval(x - 10, y - 10, width + 20, height + 20);
        }

        // Dibujar proyectiles
        for (Projectile p : projectiles) {
            p.paint(g);
        }
    }

    private void updateMovement() {
        double dx = 0, dy = 0;

        if (pressedKeys.contains(KeyEvent.VK_W) || pressedKeys.contains(KeyEvent.VK_UP)) dy -= 1;
        if (pressedKeys.contains(KeyEvent.VK_S) || pressedKeys.contains(KeyEvent.VK_DOWN)) dy += 1;
        if (pressedKeys.contains(KeyEvent.VK_A) || pressedKeys.contains(KeyEvent.VK_LEFT)) dx -= 1;
        if (pressedKeys.contains(KeyEvent.VK_D) || pressedKeys.contains(KeyEvent.VK_RIGHT)) dx += 1;

        if (dx != 0 || dy != 0) {
            double length = Math.sqrt(dx * dx + dy * dy);
            dx = dx / length * SPEED;
            dy = dy / length * SPEED;
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

        // 👇 actualizar escudo
        if (shieldActive && System.currentTimeMillis() > shieldEndTime) {
            shieldActive = false;
        }

        if (rapidFire && System.currentTimeMillis() > rapidFireEndTime) {
            rapidFire = false;
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
        return new Rectangle(x, y, width, height); 
    }

    public void shoot() {
        if (!canShoot) return;

        int currentPanelWidth = getParent() != null ? getParent().getWidth() : 800;
        int currentPanelHeight = getParent() != null ? getParent().getHeight() : 600;

        int speed = PROJECTILESPEED;

        if (facingUp) 
            projectiles.add(new Projectile(x + width / 2 - 5, y, 0, -speed, currentPanelWidth, currentPanelHeight, "up"));
        else if (facingDown) 
            projectiles.add(new Projectile(x + width / 2 - 5, y + height, 0, speed, currentPanelWidth, currentPanelHeight, "down"));
        else if (facingLeft) 
            projectiles.add(new Projectile(x, y + height / 2 - 5, -speed, 0, currentPanelWidth, currentPanelHeight, "left"));
        else if (facingRight) 
            projectiles.add(new Projectile(x + width, y + height / 2 - 5, speed, 0, currentPanelWidth, currentPanelHeight, "right"));

        canShoot = false;

        // 👇 si rapidFire activo, cooldown más corto
        shootCooldownTimer.setDelay(rapidFire ? RAPID_SHOOT_COOLDOWN_MS : NORMAL_SHOOT_COOLDOWN_MS);
        shootCooldownTimer.restart();
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

    public void setPanelSize(int panelWidth, int panelHeight) {
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
        updateScale();
    }

    // 👇 MÉTODOS NUEVOS

    public void addLife(int amount) {
        lives += amount;
    }

    public int getLives() {
        return lives;
    }

    public void loseLife(int amount) {
        lives = Math.max(0, lives - amount);
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
        rapidFireEndTime = System.currentTimeMillis() + durationMs;
    }

    public boolean isRapidFire() {
        return rapidFire;
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
    }

    public void resetState() {
        lives = INITIAL_LIVES;
        rapidFire = false;
        shieldActive = false;
        invulnerable = false;
        rapidFireEndTime = 0;
        shieldEndTime = 0;
        invulnerabilityEndTime = 0;
        canShoot = true;
        projectiles.clear();
        pressedKeys.clear();
        x = (panelWidth - width) / 2;
        y = (panelHeight - height) / 2;
    }
}
