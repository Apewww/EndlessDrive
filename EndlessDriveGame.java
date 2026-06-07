import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.prefs.Preferences;
import javax.sound.sampled.*;

/**
 * Endless Drive - A Premium Retro-Synthwave Arcade Racing Game in Pure Java.
 * Features: Procedural Sound synthesis, Shop System, Skin Switcher, Visual Particles,
 * High Score System, Fullscreen toggling, and fully responsive retro GUI.
 */
public class EndlessDriveGame extends JFrame {
    
    // Preferences keys for saving data locally
    static final String PREF_COINS = "endless_drive_coins";
    static final String PREF_HIGH_SCORE = "endless_drive_high_score";
    static final String PREF_UNLOCKED_SKINS = "endless_drive_unlocked_skins";
    static final String PREF_ACTIVE_SKIN = "endless_drive_active_skin";
    static final String PREF_SOUND = "endless_drive_sound";
    static final String PREF_FULLSCREEN = "endless_drive_fullscreen";

    private GamePanel gamePanel;
    private GraphicsDevice graphicsDevice;
    private Preferences prefs;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EndlessDriveGame game = new EndlessDriveGame();
            game.setVisible(true);
        });
    }

    // Fixed windowed size - single source of truth
    private static final int WINDOW_WIDTH  = 900;
    private static final int WINDOW_HEIGHT = 700;

    public EndlessDriveGame() {
        setTitle("Endless Drive: Synth Retro-Wave");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Load Preferences
        prefs = Preferences.userNodeForPackage(EndlessDriveGame.class);

        // Initialize sound state
        boolean soundEnabled = prefs.getBoolean(PREF_SOUND, true);
        AudioSynth.setSoundEnabled(soundEnabled);

        // Determine Fullscreen State before adding the panel
        graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        boolean isFullscreen = prefs.getBoolean(PREF_FULLSCREEN, false);

        if (isFullscreen) {
            setUndecorated(true);
        } else {
            setUndecorated(false);
            setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        }

        // Setup Main Panel
        gamePanel = new GamePanel(this);
        add(gamePanel);

        if (isFullscreen) {
            // Must be called AFTER add(gamePanel) and pack equivalent
            graphicsDevice.setFullScreenWindow(this);
        } else {
            setLocationRelativeTo(null);
        }
    }

    /**
     * Toggles between windowed mode and fullscreen mode dynamically.
     */
    public void toggleFullscreen() {
        boolean isCurrentlyFullscreen = graphicsDevice.getFullScreenWindow() != null;

        if (isCurrentlyFullscreen) {
            // 1) Release exclusive fullscreen before any other operation
            graphicsDevice.setFullScreenWindow(null);
            // 2) Dispose so we can change the undecorated property
            dispose();
            setUndecorated(false);
            // 3) Show first - the OS creates the native peer with decorations
            setVisible(true);
            // 4) Force size and center AFTER visible so Windows respects the values
            setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            setLocationRelativeTo(null);
            prefs.putBoolean(PREF_FULLSCREEN, false);
        } else {
            // Dispose to allow toggling the undecorated property
            dispose();
            setUndecorated(true);
            setVisible(true);
            try {
                graphicsDevice.setFullScreenWindow(this);
                prefs.putBoolean(PREF_FULLSCREEN, true);
            } catch (Exception e) {
                // Fallback to maximized if exclusive fullscreen is unavailable
                setExtendedState(JFrame.MAXIMIZED_BOTH);
                prefs.putBoolean(PREF_FULLSCREEN, true);
            }
        }

        // Schedule focus on EDT after the window finishes painting
        SwingUtilities.invokeLater(() -> gamePanel.requestFocusInWindow());
    }

    public Preferences getPrefs() {
        return prefs;
    }
}