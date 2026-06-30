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
        // HIGH SCORE - kanan atas
        g.setFont(new Font("Impact", Font.BOLD, 28));
        String hscore = "HIGH SCORE: " + gp.highScore + "m";
        int hscoreWidth = g.getFontMetrics().stringWidth(hscore);
        int hscoreX = width - hscoreWidth - 40;
        int hscoreY = 50;
        // Shadow untuk kontras
        g.setColor(new Color(0, 0, 0, 180));
        g.drawString(hscore, hscoreX + 2, hscoreY + 2);
        // Warna utama: neon cyan dengan glow
        g.setColor(new Color(0, 240, 255));
        g.drawString(hscore, hscoreX, hscoreY);

        // COINS BALANCE - kiri atas
        g.setFont(new Font("Impact", Font.BOLD, 28));
        String coinBal = "COINS: $" + gp.playerCoinsTotal;
        int coinX = 40;
        int coinY = 50;
        // Shadow untuk kontras
        g.setColor(new Color(0, 0, 0, 180));
        g.drawString(coinBal, coinX + 2, coinY + 2);
        // Warna utama: emas/ kuning neon
        g.setColor(new Color(255, 215, 0));
        g.drawString(coinBal, coinX, coinY);

        // Gambar tombol Menu Utama
        drawCustomButton(g, gp.btnPlay,     "DRIVE NOW",  new Color(0, 240, 255), true);
        drawCustomButton(g, gp.btnShop,     "NEON SHOP",  new Color(255, 0, 127), false);
        drawCustomButton(g, gp.btnSettings, "SETTINGS",   Color.GRAY,             false);
        drawCustomButton(g, gp.btnExit,     "EXIT GAME",  new Color(180, 30, 30), false);

        // Credits di bagian bawah
        drawCredits(g, width, height);
    }

    /**
     * Menggambar informasi credit di bagian bawah layar menu.
     */
    private void drawCredits(Graphics2D g, int width, int height) {
        g.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // Developer credit
        String devText = "Developed by: EndlessDrive Team";
        int devWidth = g.getFontMetrics().stringWidth(devText);
        int devX = width / 2 - devWidth / 2;
        int devY = height - 70;
        g.setColor(new Color(0, 0, 0, 160));
        g.drawString(devText, devX + 1, devY + 1);
        g.setColor(new Color(180, 180, 220));
        g.drawString(devText, devX, devY);

        // Tech stack
        String techText = "Built with Java Swing | Procedural Audio | Synthwave Aesthetic";
        int techWidth = g.getFontMetrics().stringWidth(techText);
        int techX = width / 2 - techWidth / 2;
        int techY = height - 50;
        g.setColor(new Color(0, 0, 0, 160));
        g.drawString(techText, techX + 1, techY + 1);
        g.setColor(new Color(140, 140, 180));
        g.drawString(techText, techX, techY);

        // Version
        String verText = "Version 1.0.0";
        int verWidth = g.getFontMetrics().stringWidth(verText);
        int verX = width / 2 - verWidth / 2;
        int verY = height - 30;
        g.setColor(new Color(0, 0, 0, 160));
        g.drawString(verText, verX + 1, verY + 1);
        g.setColor(new Color(100, 100, 140));
        g.drawString(verText, verX, verY);
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
        g.setFont(new Font("Impact", Font.BOLD, 28));
        String coinsStr = "COINS: $" + gp.playerCoinsTotal;
        int coinsWidth = g.getFontMetrics().stringWidth(coinsStr);
        int coinsX = width - coinsWidth - 50;
        int coinsY = 80;
        // Shadow untuk kontras
        g.setColor(new Color(0, 0, 0, 180));
        g.drawString(coinsStr, coinsX + 2, coinsY + 2);
        // Warna utama: emas/ kuning neon
        g.setColor(new Color(255, 215, 0));
        g.drawString(coinsStr, coinsX, coinsY);

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
        g.setFont(new Font("Impact", Font.BOLD, 28));
        String shardsStr = "SHARDS: " + gp.coinsCollectedThisRun;
        int shardsWidth = g.getFontMetrics().stringWidth(shardsStr);
        int shardsX = width - shardsWidth - 40;
        int shardsY = 55;
        // Shadow untuk kontras
        g.setColor(new Color(0, 0, 0, 180));
        g.drawString(shardsStr, shardsX + 2, shardsY + 2);
        // Warna utama: emas/ kuning neon
        g.setColor(new Color(255, 215, 0));
        g.drawString(shardsStr, shardsX, shardsY);

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
        // Distance
        g.setFont(new Font("Impact", Font.BOLD, 28));
        String distStr = "Distance Traveled: " + gp.distanceScore + "m";
        int distWidth = g.getFontMetrics().stringWidth(distStr);
        int distX = width/2 - distWidth/2;
        int distY = centerY - 50;
        g.setColor(new Color(0, 0, 0, 180));
        g.drawString(distStr, distX + 2, distY + 2);
        g.setColor(Color.WHITE);
        g.drawString(distStr, distX, distY);

        // Coins Gathered
        g.setFont(new Font("Impact", Font.BOLD, 28));
        String coinsStr = "Coins Gathered: " + gp.coinsCollectedThisRun;
        int coinsWidth = g.getFontMetrics().stringWidth(coinsStr);
        int coinsX = width/2 - coinsWidth/2;
        int coinsY = centerY + 10;
        g.setColor(new Color(0, 0, 0, 180));
        g.drawString(coinsStr, coinsX + 2, coinsY + 2);
        g.setColor(new Color(255, 215, 0));
        g.drawString(coinsStr, coinsX, coinsY);
        
        // Personal High Score - lebih besar dan menonjol
        g.setFont(new Font("Impact", Font.BOLD, 32));
        String bestStr = "PERSONAL HIGH SCORE: " + gp.highScore + "m";
        int bestWidth = g.getFontMetrics().stringWidth(bestStr);
        int bestX = width/2 - bestWidth/2;
        int bestY = centerY + 70;
        g.setColor(new Color(0, 0, 0, 180));
        g.drawString(bestStr, bestX + 2, bestY + 2);
        g.setColor(new Color(255, 215, 0));
        g.drawString(bestStr, bestX, bestY);

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
