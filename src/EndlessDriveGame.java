import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

/**
 * Kelas EndlessDriveGame merupakan titik masuk (entry point) utama untuk aplikasi game.
 * Kelas ini mewarisi JFrame bawaan Java dan mengatur jendela aplikasi agar berjalan
 * dalam mode layar penuh eksklusif (exclusive fullscreen) setiap saat tanpa bingkai jendela (undecorated).
 * 
 * Pengaturan suara awal dimuat melalui SaveManager saat startup.
 */
public class EndlessDriveGame extends JFrame {

    private GamePanel gamePanel;
    private SaveManager saveManager;

    /**
     * Metode utama untuk meluncurkan game.
     * Menggunakan SwingUtilities.invokeLater untuk memastikan pembuatan komponen GUI aman secara thread.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EndlessDriveGame().setVisible(true));
    }

    /**
     * Konstruktor EndlessDriveGame.
     * Mengatur konfigurasi jendela, fullscreen, serta menginisialisasi panel game.
     */
    public EndlessDriveGame() {
        setTitle("Endless Drive");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Menginisialisasi SaveManager untuk memuat preferensi suara
        saveManager = new SaveManager();
        AudioSynth.setSoundEnabled(saveManager.isSoundEnabled());

        // Jalankan game dalam mode fullscreen tanpa dekorasi bingkai jendela
        setUndecorated(true);

        gamePanel = new GamePanel(this);
        add(gamePanel);

        // Meminta kontrol fullscreen eksklusif ke perangkat grafis default
        GraphicsDevice gd = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();
        gd.setFullScreenWindow(this);

        // Meminta fokus keyboard secara asinkron ke panel game
        SwingUtilities.invokeLater(() -> gamePanel.requestFocusInWindow());
    }

    /**
     * Mendapatkan objek SaveManager yang dimiliki frame.
     * @return instansi SaveManager
     */
    public SaveManager getSaveManager() {
        return saveManager;
    }

    /**
     * Mempertahankan kompatibilitas jika ada kode eksternal yang memanggil getPrefs.
     * @return instansi Preferences bawaan dari SaveManager
     */
    public Preferences getPrefs() {
        return saveManager.getPrefs();
    }
}