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

    public EndlessDriveGame() {
        setTitle("Endless Drive: Synth Retro-Wave");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Load Preferences
        prefs = Preferences.userNodeForPackage(EndlessDriveGame.class);
        
        // Initialize sound state
        boolean soundEnabled = prefs.getBoolean(PREF_SOUND, true);
        AudioSynth.setSoundEnabled(soundEnabled);

        // Setup Main Panel
        gamePanel = new GamePanel(this);
        add(gamePanel);

        // Determine Fullscreen State
        graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        boolean isFullscreen = prefs.getBoolean(PREF_FULLSCREEN, false);
        
        if (isFullscreen) {
            setUndecorated(true);
            graphicsDevice.setFullScreenWindow(this);
        } else {
            setUndecorated(false);
            setSize(900, 700);
            setLocationRelativeTo(null);
        }
    }

    /**
     * Toggles between windowed mode and fullscreen mode dynamically.
     */
    public void toggleFullscreen() {
        boolean isCurrentlyFullscreen = graphicsDevice.getFullScreenWindow() != null;

        if (isCurrentlyFullscreen) {
            graphicsDevice.setFullScreenWindow(null);
            setExtendedState(JFrame.NORMAL);
            dispose(); // Terminate window temporarily to modify decoration state
            setUndecorated(false);
            setSize(900, 700);
            setLocationRelativeTo(null);
            prefs.putBoolean(PREF_FULLSCREEN, false);
        } else {
            dispose(); // Terminate window temporarily to modify decoration state
            setUndecorated(true);
            try {
                graphicsDevice.setFullScreenWindow(this);
                prefs.putBoolean(PREF_FULLSCREEN, true);
            } catch (Exception e) {
                // Fallback to maximized screen if exclusive fullscreen fails
                setExtendedState(JFrame.MAXIMIZED_BOTH);
                prefs.putBoolean(PREF_FULLSCREEN, true);
            }
        }
        setVisible(true);
        gamePanel.requestFocusInWindow();
    }

    public Preferences getPrefs() {
        return prefs;
    }
}