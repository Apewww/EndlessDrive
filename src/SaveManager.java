import java.util.prefs.Preferences;

/**
 * Kelas SaveManager bertanggung jawab untuk mengelola penyimpanan data game secara lokal.
 * Data yang disimpan meliputi skor tertinggi, total koin pemain, skin aktif, status unlock skin,
 * serta preferensi suara (sound enabled).
 * 
 * Menggunakan Java Preferences API agar data tetap tersimpan meskipun aplikasi ditutup.
 */
public class SaveManager {
    // Kunci preferensi untuk menyimpan data secara lokal
    private static final String PREF_COINS          = "endless_drive_coins";
    private static final String PREF_HIGH_SCORE     = "endless_drive_high_score";
    private static final String PREF_UNLOCKED_SKINS = "endless_drive_unlocked_skins";
    private static final String PREF_ACTIVE_SKIN    = "endless_drive_active_skin";
    private static final String PREF_SOUND          = "endless_drive_sound";

    private Preferences prefs;

    /**
     * Konstruktor SaveManager.
     * Menggunakan kelas EndlessDriveGame sebagai basis node package agar data tersimpan
     * pada namespace yang sama dengan versi sebelumnya (mencegah hilangnya save data).
     */
    public SaveManager() {
        prefs = Preferences.userNodeForPackage(EndlessDriveGame.class);
    }

    /**
     * Memuat skor tertinggi yang pernah dicapai pemain.
     * @return Skor tertinggi (default: 0 jika belum ada)
     */
    public int getHighScore() {
        return prefs.getInt(PREF_HIGH_SCORE, 0);
    }

    /**
     * Menyimpan skor tertinggi yang baru.
     * @param score Skor tertinggi yang akan disimpan
     */
    public void saveHighScore(int score) {
        prefs.putInt(PREF_HIGH_SCORE, score);
    }

    /**
     * Memuat total koin yang dimiliki pemain saat ini.
     * @return Total koin (default: 0 jika belum ada koin)
     */
    public int getCoins() {
        return prefs.getInt(PREF_COINS, 0);
    }

    /**
     * Menyimpan total koin baru yang dimiliki pemain.
     * @param coins Jumlah total koin yang akan disimpan
     */
    public void saveCoins(int coins) {
        prefs.putInt(PREF_COINS, coins);
    }

    /**
     * Memuat indeks skin aktif yang sedang digunakan pemain.
     * @return Indeks skin aktif (default: 0)
     */
    public int getActiveSkin() {
        return prefs.getInt(PREF_ACTIVE_SKIN, 0);
    }

    /**
     * Menyimpan indeks skin aktif yang dipilih pemain.
     * @param idx Indeks skin aktif
     */
    public void saveActiveSkin(int idx) {
        prefs.putInt(PREF_ACTIVE_SKIN, idx);
    }

    /**
     * Memeriksa apakah fitur suara diaktifkan atau dinonaktifkan.
     * @return True jika suara diaktifkan, false jika dinonaktifkan
     */
    public boolean isSoundEnabled() {
        return prefs.getBoolean(PREF_SOUND, true);
    }

    /**
     * Menyimpan preferensi status suara game.
     * @param enabled Status suara (aktif/nonaktif)
     */
    public void saveSoundEnabled(boolean enabled) {
        prefs.putBoolean(PREF_SOUND, enabled);
    }

    /**
     * Memeriksa apakah indeks skin tertentu sudah dibeli atau terbuka.
     * @param idx Indeks skin yang akan diperiksa
     * @return True jika skin sudah terbuka (skin indeks 0 selalu terbuka)
     */
    public boolean isSkinUnlocked(int idx) {
        if (idx == 0) return true; // Skin bawaan (indeks 0) gratis dan selalu terbuka
        String unlockedList = prefs.get(PREF_UNLOCKED_SKINS, "0");
        String[] indices = unlockedList.split(",");
        for (String s : indices) {
            if (s.trim().equals(String.valueOf(idx))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Membuka kunci skin berdasarkan indeks yang dibeli dan menyimpannya ke preferensi lokal.
     * @param idx Indeks skin yang akan dibuka
     */
    public void unlockSkin(int idx) {
        String unlockedList = prefs.get(PREF_UNLOCKED_SKINS, "0");
        if (!unlockedList.contains(String.valueOf(idx))) {
            unlockedList += "," + idx;
            prefs.put(PREF_UNLOCKED_SKINS, unlockedList);
        }
    }

    /**
     * Mengakses objek Preferences secara langsung jika dibutuhkan oleh kelas eksternal.
     * @return Objek Preferences
     */
    public Preferences getPrefs() {
        return prefs;
    }
}
