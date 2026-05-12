package bulletdrift.core;

import bulletdrift.entities.Boss;
import bulletdrift.entities.KeyObjective;
import bulletdrift.entities.Portal;

import java.awt.Color;

public class GameSession {
    private static final int POINTS_PER_WAVE = 100;
    private static final int KEY_SPAWN_SCORE = 600;
    private static final int PORTAL_SCORE = 1000;
    private static final int KEY_BASE_SIZE = 64;
    private static final int KEY_BOTTOM_MARGIN = 120;
    private static final int REFERENCE_PANEL_WIDTH = 1920;
    private static final int REFERENCE_PANEL_HEIGHT = 1080;
    private static final long POWER_UP_FEEDBACK_DURATION_MS = 1200;
    private static final long WAVE_FEEDBACK_DURATION_MS = 1800;

    private int score;
    private boolean gameOver;
    private boolean paused;
    private String powerUpFeedbackText;
    private Color powerUpFeedbackColor;
    private long powerUpFeedbackEndTime;
    private int lastAnnouncedWave;
    private String waveFeedbackText;
    private long waveFeedbackEndTime;
    private KeyObjective keyObjective;
    private Portal portal;
    private Boss boss;
    private boolean keyCollected;
    private boolean portalUsed;
    private boolean bossDefeated;
    private boolean victory;
    private boolean waveChanged;

    public GameSession() {
        reset();
    }

    public void reset() {
        score = 0;
        gameOver = false;
        paused = false;
        powerUpFeedbackText = "";
        powerUpFeedbackColor = Color.WHITE;
        powerUpFeedbackEndTime = 0;
        lastAnnouncedWave = 0;
        waveFeedbackText = "";
        waveFeedbackEndTime = 0;
        keyObjective = null;
        portal = null;
        boss = null;
        keyCollected = false;
        portalUsed = false;
        bossDefeated = false;
        victory = false;
        waveChanged = false;
    }

    public void addScore(int amount) {
        int previousWave = getWave();
        score += amount;
        updateWaveFeedback();
        waveChanged = getWave() > previousWave;
    }

    public int getScore() {
        return score;
    }

    public int getWave() {
        return score / POINTS_PER_WAVE;
    }

    public int getWaveStartScore() {
        return getWave() * POINTS_PER_WAVE;
    }

    public boolean consumeWaveChanged() {
        boolean changed = waveChanged;
        waveChanged = false;
        return changed;
    }

    public void updateFinalProgression(int panelWidth, int panelHeight) {
        if (score >= KEY_SPAWN_SCORE && keyObjective == null && !keyCollected) {
            keyObjective = createKeyObjective(panelWidth, panelHeight);
            showPowerUpFeedback("DEFIENDE LA LLAVE", new Color(180, 220, 255));
        }

        if (score >= PORTAL_SCORE && portal == null) {
            portal = new Portal(panelWidth, panelHeight);
            showPowerUpFeedback("PORTAL ABIERTO", new Color(180, 120, 255));
        }
    }

    private KeyObjective createKeyObjective(int panelWidth, int panelHeight) {
        double scale = getPanelScale(panelWidth, panelHeight);
        int keySize = getScaledSize(KEY_BASE_SIZE, scale);
        int bottomMargin = getScaledSize(KEY_BOTTOM_MARGIN, scale);
        int x = panelWidth / 2 - keySize / 2;
        int y = Math.max(0, panelHeight - keySize - bottomMargin);
        return new KeyObjective(x, y);
    }

    private double getPanelScale(int panelWidth, int panelHeight) {
        double scaleX = panelWidth / (double) REFERENCE_PANEL_WIDTH;
        double scaleY = panelHeight / (double) REFERENCE_PANEL_HEIGHT;
        return Math.max(0.1, Math.min(scaleX, scaleY));
    }

    private int getScaledSize(int baseSize, double scale) {
        return Math.max(1, (int) Math.round(baseSize * scale));
    }

    public boolean isPortalActive() {
        return portal != null && !portalUsed;
    }

    public boolean shouldSpawnEnemies() {
        return portal == null && boss == null;
    }

    public boolean hasDefendableKey() {
        return keyObjective != null && !keyCollected && portal == null;
    }

    public boolean hasCollectableKey() {
        return keyObjective != null && !keyCollected && isPortalActive();
    }

    public KeyObjective getKeyObjective() {
        return keyObjective;
    }

    public Portal getPortal() {
        return portalUsed ? null : portal;
    }

    public Boss getBoss() {
        return bossDefeated ? null : boss;
    }

    public boolean isBossActive() {
        return boss != null && !bossDefeated;
    }

    public boolean hasKeyCollected() {
        return keyCollected;
    }

    public void collectKey() {
        keyCollected = true;
        keyObjective = null;
    }

    public void usePortal(int panelWidth, int panelHeight) {
        portalUsed = true;
        boss = new Boss(panelWidth, panelHeight);
        showPowerUpFeedback("BOSS FINAL", new Color(225, 205, 155));
    }

    public void defeatBoss() {
        bossDefeated = true;
        victory = true;
        gameOver = true;
        showPowerUpFeedback("VICTORIA", new Color(225, 205, 155));
    }

    public boolean isVictory() {
        return victory;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public void showPowerUpFeedback(String text, Color color) {
        powerUpFeedbackText = text;
        powerUpFeedbackColor = color;
        powerUpFeedbackEndTime = System.currentTimeMillis() + POWER_UP_FEEDBACK_DURATION_MS;
    }

    public void clearWaveFeedback() {
        lastAnnouncedWave = getWave();
        waveFeedbackText = "";
        waveFeedbackEndTime = 0;
    }

    private void updateWaveFeedback() {
        int currentWave = getWave();
        if (currentWave <= lastAnnouncedWave) return;

        lastAnnouncedWave = currentWave;
        waveFeedbackText = "OLEADA " + currentWave;
        waveFeedbackEndTime = System.currentTimeMillis() + WAVE_FEEDBACK_DURATION_MS;
    }

    public String getPowerUpFeedbackText() {
        return powerUpFeedbackText;
    }

    public Color getPowerUpFeedbackColor() {
        return powerUpFeedbackColor;
    }

    public long getPowerUpFeedbackEndTime() {
        return powerUpFeedbackEndTime;
    }

    public String getWaveFeedbackText() {
        return waveFeedbackText;
    }

    public long getWaveFeedbackEndTime() {
        return waveFeedbackEndTime;
    }
}
