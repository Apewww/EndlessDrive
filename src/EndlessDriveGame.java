// Library bawaan dari Java
import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

/**
 * [Class Buatan Sendiri (Turunan/Override dari JFrame bawaan Java)]
 * Endless Drive - Game Balapan Arcade Retro-Synthwave Premium dengan Java Murni.
 * Fitur: Sintesis suara prosedural, Sistem Toko, Pengubah Skin, Partikel Visual,
 * Sistem Skor Tinggi, dan GUI retro yang sepenuhnya responsif.
 * Berjalan dalam mode layar penuh eksklusif setiap saat.
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
        setTitle("Endless Drive");
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