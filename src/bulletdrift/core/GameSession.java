package bulletdrift.core;

import java.awt.Color;

public class GameSession {
    private static final int POINTS_PER_WAVE = 100;
    private static final long POWER_UP_FEEDBACK_DURATION_MS = 1200;
    private static final long WAVE_FEEDBACK_DURATION_MS = 1800;

    private int score;
    private int coins;
    private boolean gameOver;
    private boolean paused;
    private String powerUpFeedbackText;
    private Color powerUpFeedbackColor;
    private long powerUpFeedbackEndTime;
    private int lastAnnouncedWave;
    private String waveFeedbackText;
    private long waveFeedbackEndTime;

    public GameSession() {
        reset();
    }

    public void reset() {
        score = 0;
        coins = 0;
        gameOver = false;
        paused = false;
        powerUpFeedbackText = "";
        powerUpFeedbackColor = Color.WHITE;
        powerUpFeedbackEndTime = 0;
        lastAnnouncedWave = 0;
        waveFeedbackText = "";
        waveFeedbackEndTime = 0;
    }

    public void addScore(int amount) {
        score += amount;
        updateWaveFeedback();
    }

    public int getScore() {
        return score;
    }

    public void addCoins(int amount) {
        coins += amount;
    }

    public int getCoins() {
        return coins;
    }

    public int getWave() {
        return score / POINTS_PER_WAVE;
    }

    public int getWaveStartScore() {
        return getWave() * POINTS_PER_WAVE;
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
