import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Kelas GameRenderer menangani seluruh urusan rendering grafis game.
 * Memisahkan kode penggambaran UI (User Interface) dari logika utama game.
 * 
 * Tanggung jawab utamanya meliputi:
 * - Menggambar latar belakang synthwave dan jalan raya neon.
 * - Menggambar layar Menu Utama, Toko Skin, Pengaturan, Game Over, dan HUD.
 * - Menggambar komponen tombol kustom yang responsif terhadap hover mouse.
 */
public class GameRenderer {
    private GamePanel gp;

    /**
     * Konstruktor GameRenderer.
     * @param gp Referensi ke GamePanel utama untuk mengakses data state game
     */
    public GameRenderer(GamePanel gp) {
        this.gp = gp;
    }

    /**
     * Metode utama untuk merender semua tampilan berdasarkan state game saat ini.
     * @param g Objek Graphics dari JPanel
     */
    public void draw(Graphics2D g) {
        // Selalu gambar jalan raya dan latar belakang neon terlebih dahulu
        drawRoadSkyline(g);

        // Render layar yang sesuai berdasarkan GameState
        switch (gp.currentState) {
            case MENU:
                drawMenuScreen(g);
                break;
            case GAME:
                drawGameplayScreen(g);
                break;
            case SETTINGS:
                drawSettingsScreen(g);
                break;
            case SHOP:
                drawShopScreen(g);
                break;
            case GAME_OVER:
                drawGameOverScreen(g);
                break;
        }
    }

    /**
     * Menggambar latar belakang retro-neon dan jalur jalan raya di bagian tengah.
     */
    private void drawRoadSkyline(Graphics2D g) {
        int width = gp.getWidth();
        int height = gp.getHeight();

        // Gambar latar belakang utama
        if (gp.assetManager.getBgImg() != null) {
            g.drawImage(gp.assetManager.getBgImg(), 0, 0, width, height, null);
        }

        // Gambar jalan raya berukuran lebar 500 piksel di tengah layar
        int trackWidth = 500;
        int leftEdge = (width - trackWidth) / 2;
        if (gp.assetManager.getRoadImg() != null) {
            g.drawImage(gp.assetManager.getRoadImg(), leftEdge, 0, trackWidth, height, null);
        }
    }

    /**
     * Menggambar tampilan Menu Utama.
     */
    private void drawMenuScreen(Graphics2D g) {
        int width = gp.getWidth();
        int height = gp.getHeight();

        // Gambar gambar menu jika ada
        if (gp.assetManager.getMenuBgImg() != null) {
            g.drawImage(gp.assetManager.getMenuBgImg(), 0, 0, width, height, null);
        } else {
            // Fallback jika gambar menu kosong
            g.setColor(new Color(11, 0, 26, 160));
            g.fillRect(0, 0, width, height);
        }

        // Tampilkan info Skor Tertinggi & Koin di bagian atas
        g.setFont(new Font("Segoe UI", Font.BOLD, 15));
        g.setColor(new Color(255, 215, 0));
        String hscore = "HIGH SCORE: " + gp.highScore + "m";
        g.drawString(hscore, width - g.getFontMetrics().stringWidth(hscore) - 40, 40);

        String coinBal = "COINS BALANCE: $" + gp.playerCoinsTotal;
        g.drawString(coinBal, 40, 40);

        // Gambar tombol Menu Utama
        drawCustomButton(g, gp.btnPlay,     "DRIVE NOW",  new Color(0, 240, 255), true);
        drawCustomButton(g, gp.btnShop,     "NEON SHOP",  new Color(255, 0, 127), false);
        drawCustomButton(g, gp.btnSettings, "SETTINGS",   Color.GRAY,             false);
        drawCustomButton(g, gp.btnExit,     "EXIT GAME",  new Color(180, 30, 30), false);
    }

    /**
     * Menggambar tampilan Toko Skin (Shop).
     */
    private void drawShopScreen(Graphics2D g) {
        int width = gp.getWidth();
        int height = gp.getHeight();
        int centerY = height / 2;

        g.setColor(new Color(11, 0, 26, 230));
        g.fillRect(0, 0, width, height);

        // Header Toko
        g.setFont(new Font("Impact", Font.ITALIC, 50));
        g.setColor(new Color(255, 0, 127));
        String title = "COSMETIC SHOP";
        g.drawString(title, width / 2 - g.getFontMetrics().stringWidth(title) / 2, centerY - 200);

        // Tombol Kembali
        drawCustomButton(g, gp.btnShopExit, "BACK", Color.WHITE, false);

        // Saldo Koin saat ini
        g.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g.setColor(new Color(255, 215, 0));
        String coinsStr = "COINS BALANCE: $" + gp.playerCoinsTotal;
        g.drawString(coinsStr, width - g.getFontMetrics().stringWidth(coinsStr) - 50, 75);

        // Tombol Navigasi Kiri / Kanan
        drawCustomButton(g, gp.btnShopPrev, "<", new Color(0, 240, 255), false);
        drawCustomButton(g, gp.btnShopNext, ">", new Color(0, 240, 255), false);

        // Ambil data skin yang sedang dilihat
        Skin currentPreview = gp.skins.get(gp.previewSkinIdx);

        // Gambar lingkaran dekorasi di belakang kendaraan preview
        g.setColor(new Color(currentPreview.glowColor.getRed(), currentPreview.glowColor.getGreen(), currentPreview.glowColor.getBlue(), 40));
        g.fillOval(width / 2 - 100, centerY - 150, 200, 200);
        g.setColor(currentPreview.glowColor);
        g.setStroke(new BasicStroke(3));
        g.drawOval(width / 2 - 100, centerY - 150, 200, 200);

        // Gambar preview kendaraan
        Vehicle mockCar = new Vehicle(width / 2 - 25, centerY - 95, currentPreview, 0,
            (gp.previewSkinIdx == 3 ? gp.assetManager.getPlayerImg() : 
            (gp.previewSkinIdx == 2 ? gp.assetManager.getDesertNomadImg() : 
            (gp.previewSkinIdx == 1 ? gp.assetManager.getNeonPulseImg() : gp.assetManager.getShopCarImg()))));
        mockCar.draw(g);

        // Info Nama dan Deskripsi Skin
        g.setFont(new Font("Segoe UI", Font.BOLD, 26));
        g.setColor(Color.WHITE);
        int nameWidth = g.getFontMetrics().stringWidth(currentPreview.name);
        g.drawString(currentPreview.name, width / 2 - nameWidth / 2, centerY + 85);

        g.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g.setColor(new Color(180, 180, 200));
        int descWidth = g.getFontMetrics().stringWidth(currentPreview.desc);
        g.drawString(currentPreview.desc, width / 2 - descWidth / 2, centerY + 110);

        // Deteksi status unlock skin
        boolean unlocked = gp.isSkinUnlocked(gp.previewSkinIdx);
        boolean equipped = (gp.previewSkinIdx == gp.selectedSkinIdx);

        String actionText;
        Color buttonColor;

        if (equipped) {
            actionText = "EQUIPPED";
            buttonColor = new Color(0, 240, 255);
        } else if (unlocked) {
            actionText = "EQUIP SKIN";
            buttonColor = Color.GREEN;
        } else {
            actionText = "BUY: $" + currentPreview.cost;
            buttonColor = new Color(255, 0, 127);
        }

        drawCustomButton(g, gp.btnShopAction, actionText, buttonColor, true);
    }

    /**
     * Menggambar layar Pengaturan (Settings).
     */
    private void drawSettingsScreen(Graphics2D g) {
        int width = gp.getWidth();
        int height = gp.getHeight();
        int centerY = height / 2;

        g.setColor(new Color(11, 0, 26, 230));
        g.fillRect(0, 0, width, height);

        // Header
        g.setFont(new Font("Impact", Font.ITALIC, 50));
        g.setColor(new Color(255, 0, 127));
        String header = "SETTINGS CONFIG";
        g.drawString(header, width / 2 - g.getFontMetrics().stringWidth(header) / 2, centerY - 180);

        // Tombol Kembali
        drawCustomButton(g, gp.btnSettingsExit, "BACK", Color.WHITE, false);

        // Tombol Toggle Sound
        String soundText = "BACKSOUND/SOUNDS: " + (AudioSynth.isSoundEnabled() ? "ON" : "OFF");
        drawCustomButton(g, gp.btnToggleSound, soundText, new Color(0, 240, 255), false);

        // Daftar Instruksi Kontrol Keyboard
        g.setColor(new Color(180, 180, 220));
        g.setFont(new Font("Segoe UI", Font.BOLD, 18));
        String controlsHeader = "KEYBOARD CONTROLS:";
        g.drawString(controlsHeader, width / 2 - 150, centerY + 70);
        
        g.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        g.drawString("- LEFT ARROW / A  : Turn Left", width / 2 - 150, centerY + 105);
        g.drawString("- RIGHT ARROW / D : Turn Right", width / 2 - 150, centerY + 135);
        g.drawString("- UP ARROW / W    : Accelerate Speed (Gain Multiplier)", width / 2 - 150, centerY + 165);
        g.drawString("- DOWN ARROW / S  : Apply Brakes", width / 2 - 150, centerY + 195);
    }

    /**
     * Menggambar antarmuka HUD selama permainan aktif.
     */
    private void drawGameplayScreen(Graphics2D g) {
        int width = gp.getWidth();

        // Desain Panel HUD Kaca Semi Transparan di bagian atas
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(20, 15, width - 40, 55);
        g.setColor(new Color(0, 240, 255, 50));
        g.setStroke(new BasicStroke(1));
        g.drawRect(20, 15, width - 40, 55);

        // Jarak perjalanan
        g.setFont(new Font("Impact", Font.PLAIN, 24));
        g.setColor(Color.WHITE);
        g.drawString("DISTANCE: " + gp.distanceScore + "m", 40, 48);

        // Indikator Kecepatan kendaraan
        g.setFont(new Font("Impact", Font.PLAIN, 20));
        g.setColor(new Color(255, 0, 127));
        g.drawString("SPEED: " + (int)(gp.roadSpeed * 10) + " KM/H", width / 2 - 90, 48);

        // Jumlah koin yang terkumpul
        g.setFont(new Font("Impact", Font.PLAIN, 24));
        g.setColor(new Color(255, 215, 0));
        g.drawString("SHARDS: " + gp.coinsCollectedThisRun, width - 180, 48);

        // Menggambar entitas (Koin, Musuh, Efek Partikel) lewat EntityManager
        gp.entityManager.drawEntities(g);

        // Gambar Kendaraan Pemain
        gp.player.draw(g);
    }

    /**
     * Menggambar tampilan Game Over saat menabrak rintangan.
     */
    private void drawGameOverScreen(Graphics2D g) {
        int width = gp.getWidth();
        int height = gp.getHeight();
        int centerY = height / 2;

        // Overlay warna gelap violet transparan
        g.setColor(new Color(11, 0, 26, 220));
        g.fillRect(0, 0, width, height);

        // Header Ledakan Glow "CRASH DETECTED"
        g.setFont(new Font("Impact", Font.ITALIC, 70));
        g.setColor(new Color(255, 0, 127));
        String title = "CRASH DETECTED";
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, width/2 - titleWidth/2, centerY - 150);

        g.setFont(new Font("Impact", Font.ITALIC, 69));
        g.setColor(new Color(0, 240, 255));
        g.drawString(title, width/2 - titleWidth/2 - 2, centerY - 152);

        // Statistik Akhir Sesi
        g.setFont(new Font("Segoe UI", Font.BOLD, 22));
        g.setColor(Color.WHITE);
        String distStr = "Distance Traveled: " + gp.distanceScore + "m";
        String coinsStr = "Coins Gathered: " + gp.coinsCollectedThisRun;
        String bestStr = "Personal High Score: " + gp.highScore + "m";

        g.drawString(distStr, width/2 - g.getFontMetrics().stringWidth(distStr)/2, centerY - 50);
        g.drawString(coinsStr, width/2 - g.getFontMetrics().stringWidth(coinsStr)/2, centerY);
        
        g.setColor(new Color(255, 215, 0));
        g.drawString(bestStr, width/2 - g.getFontMetrics().stringWidth(bestStr)/2, centerY + 50);

        // Tombol Ulang / Kembali ke Menu
        drawCustomButton(g, gp.btnGoAgain, "PLAY AGAIN", new Color(0, 240, 255), true);
        drawCustomButton(g, gp.btnGoMenu, "MAIN MENU", new Color(255, 0, 127), false);
    }

    /**
     * Utilitas rendering tombol kustom neon dengan efek hover interaktif.
     */
    private void drawCustomButton(Graphics2D g, Rectangle r, String text, Color baseColor, boolean pulseGlow) {
        boolean isHovered = r.contains(gp.mousePos);
        
        // Buat warna tombol lebih terang ketika disentuh kursor
        Color boxColor = isHovered ? baseColor.brighter() : baseColor;
        
        // Gambar bayangan berpendar neon di belakang tombol
        if (isHovered || pulseGlow) {
            g.setColor(new Color(boxColor.getRed(), boxColor.getGreen(), boxColor.getBlue(), 60));
            g.fill(new RoundRectangle2D.Double(r.x - 3, r.y - 3, r.width + 6, r.height + 6, 8, 8));
        }

        // Gambar latar solid tombol
        g.setColor(new Color(20, 5, 35, 220));
        g.fill(new RoundRectangle2D.Double(r.x, r.y, r.width, r.height, 8, 8));

        // Gambar garis luar neon
        g.setColor(boxColor);
        g.setStroke(new BasicStroke(isHovered ? 3 : 2));
        g.draw(new RoundRectangle2D.Double(r.x, r.y, r.width, r.height, 8, 8));

        // Gambar Teks di tengah tombol
        g.setFont(new Font("Segoe UI", Font.BOLD, 15));
        FontMetrics fm = g.getFontMetrics();
        int tx = r.x + (r.width - fm.stringWidth(text)) / 2;
        int ty = r.y + (r.height - fm.getHeight()) / 2 + fm.getAscent();
        
        g.setColor(isHovered ? Color.WHITE : boxColor);
        g.drawString(text, tx, ty);
    }
}
