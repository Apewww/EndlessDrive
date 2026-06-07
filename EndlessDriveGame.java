import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

/**
 * Endless Drive - A Premium Retro-Synthwave Arcade Racing Game in Pure Java.
 * Features: Procedural Sound synthesis, Shop System, Skin Switcher, Visual Particles,
 * High Score System, and fully responsive retro GUI.
 * Runs in exclusive fullscreen at all times.
 */
public class EndlessDriveGame extends JFrame {

    // Preferences keys for saving data locally
    static final String PREF_COINS          = "endless_drive_coins";
    static final String PREF_HIGH_SCORE     = "endless_drive_high_score";
    static final String PREF_UNLOCKED_SKINS = "endless_drive_unlocked_skins";
    static final String PREF_ACTIVE_SKIN    = "endless_drive_active_skin";
    static final String PREF_SOUND          = "endless_drive_sound";

    private GamePanel      gamePanel;
    private Preferences    prefs;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EndlessDriveGame().setVisible(true));
    }

    public EndlessDriveGame() {
        setTitle("Endless Drive: Synth Retro-Wave");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        prefs = Preferences.userNodeForPackage(EndlessDriveGame.class);

        // Restore sound preference
        AudioSynth.setSoundEnabled(prefs.getBoolean(PREF_SOUND, true));

        // Always run in exclusive fullscreen — no decorations needed
        setUndecorated(true);

        gamePanel = new GamePanel(this);
        add(gamePanel);

        // Claim exclusive fullscreen
        GraphicsDevice gd = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();
        gd.setFullScreenWindow(this);

        SwingUtilities.invokeLater(() -> gamePanel.requestFocusInWindow());
    }

    public Preferences getPrefs() {
        return prefs;
    }
}