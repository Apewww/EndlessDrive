import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.awt.image.BufferedImage;

/**
 * Kelas GamePanel bertindak sebagai pengendali utama (Controller/Mediator) game.
 * Mengimplementasikan loop utama permainan menggunakan Swing Timer, mendeteksi input keyboard/mouse,
 * serta menghubungkan berbagai modul (AssetManager, SaveManager, EntityManager, GameRenderer).
 */
public class GamePanel extends JPanel implements ActionListener, KeyListener, MouseListener, MouseMotionListener {
    
    // Status Layar Game (Game State)
    public enum GameState { MENU, GAME, SETTINGS, SHOP, GAME_OVER }
    GameState currentState = GameState.MENU;

    // Referensi ke Frame Utama
    private EndlessDriveGame frame;
    private Timer gameTimer;
    
    // Modul pendukung hasil modularisasi
    AssetManager assetManager;
    SaveManager saveManager;
    EntityManager entityManager;
    private GameRenderer gameRenderer;

    // Parameter Kecepatan Jalan & Fisika
    double roadOffset = 0;
    double roadSpeed = 10;

    // Control flags (Input keyboard)
    boolean keyLeft = false, keyRight = false;

    // Konfigurasi Kendaraan Pemain
    Vehicle player;
    int selectedSkinIdx = 0;
    ArrayList<Skin> skins = new ArrayList<>();

    // Sistem Skor & Koin
    int distanceScore = 0;
    int coinsCollectedThisRun = 0;
    int highScore = 0;
    int playerCoinsTotal = 0;

    // Efek Visual
    int screenShakeAmount = 0;
    Random random = new Random();

    // Hitbox Tombol GUI Menu Utama
    final Rectangle btnPlay = new Rectangle();
    final Rectangle btnShop = new Rectangle();
    final Rectangle btnSettings = new Rectangle();
    final Rectangle btnExit = new Rectangle();
    Point mousePos = new Point(0, 0);

    // Hitbox Tombol GUI Toko (Shop)
    int previewSkinIdx = 0;
    final Rectangle btnShopPrev = new Rectangle();
    final Rectangle btnShopNext = new Rectangle();
    final Rectangle btnShopAction = new Rectangle();
    final Rectangle btnShopExit = new Rectangle();

    // Hitbox Tombol GUI Setting
    final Rectangle btnToggleSound  = new Rectangle();
    final Rectangle btnSettingsExit = new Rectangle();

    // Hitbox Tombol GUI Game Over
    final Rectangle btnGoAgain = new Rectangle();
    final Rectangle btnGoMenu = new Rectangle();

    /**
     * Konstruktor GamePanel.
     * @param frame Frame utama aplikasi
     */
    public GamePanel(EndlessDriveGame frame) {
        this.frame = frame;
        setBackground(new Color(11, 0, 26));
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

        // Katalog Skin Mobil
        skins.add(new Skin("Apex Crimson", 0, new Color(220, 20, 60), new Color(40, 40, 45), new Color(255, 50, 50), "Mobil sport standar berkinerja tinggi. Siap melaju."));
        skins.add(new Skin("Neon Pulse", 50, new Color(0, 240, 255), new Color(255, 0, 127), new Color(0, 255, 255), "Desain teraliri listrik dengan panel karbon neon."));
        skins.add(new Skin("Desert Nomad", 150, new Color(255, 140, 0), new Color(90, 75, 50), new Color(255, 165, 0), "Mobil gurun berlapis baja tangguh dengan besi pelindung depan."));
        skins.add(new Skin("Royal Phoenix", 300, new Color(255, 215, 0), new Color(75, 0, 130), new Color(255, 223, 0), "Hypercar mewah berbalut lapisan emas mikro berkilau."));

        // Inisialisasi Modul Manajer hasil modularisasi
        assetManager = new AssetManager();
        saveManager = new SaveManager();
        entityManager = new EntityManager(this);
        gameRenderer = new GameRenderer(this);

        // Muat data permainan dari penyimpanan lokal
        loadGameData();

        // Siapkan kendaraan pemain dengan skin aktifnya
        BufferedImage imgForPlayer = getPlayerImageForSkin(selectedSkinIdx);
        player = new Vehicle(425, 500, skins.get(selectedSkinIdx), 0, imgForPlayer);

        // Timer utama game loop berjalan di kisaran 60 FPS (~16ms per frame)
        gameTimer = new Timer(16, this);
        gameTimer.start();
    }

    /** 
     * Mengembalikan gambar yang sesuai untuk setiap indeks skin mobil pemain.
     */
    BufferedImage getPlayerImageForSkin(int idx) {
        if (idx == 3) return assetManager.getPlayerImg();
        if (idx == 2) return assetManager.getDesertNomadImg();
        if (idx == 1) return assetManager.getNeonPulseImg();
        return assetManager.getShopCarImg();
    }

    /**
     * Perhitungan posisi dinamis tombol GUI agar responsif di berbagai resolusi layar.
     */
    private void recalculateUIBounds() {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        int centerY = h / 2;

        // Tombol Menu Utama
        btnPlay.setBounds(w / 2 - 100, centerY - 50 + 50, 200, 45);
        btnShop.setBounds(w / 2 - 100, centerY - -10 + 50, 200, 45);
        btnSettings.setBounds(w / 2 - 100, centerY - -70 + 50, 200, 45);
        btnExit.setBounds(w / 2 - 100, centerY - -130 + 50, 200, 45);

        // Tombol Toko (Shop)
        btnShopPrev.setBounds(w / 2 - 250, centerY + 30, 60, 45);
        btnShopNext.setBounds(w / 2 + 190, centerY + 30, 60, 45);
        btnShopAction.setBounds(w / 2 - 100, centerY + 130, 200, 50);
        btnShopExit.setBounds(50, 50, 100, 40);

        // Tombol Pengaturan
        btnToggleSound.setBounds(w / 2 - 150, centerY - 60, 300, 45);
        btnSettingsExit.setBounds(50, 50, 100, 40);

        // Tombol Game Over
        btnGoAgain.setBounds(w / 2 - 220, centerY + 130, 200, 45);
        btnGoMenu.setBounds(w / 2 + 20, centerY + 130, 200, 45);
    }

    /**
     * Memuat data lokal pemain (highscore, koin, skin) menggunakan SaveManager.
     */
    private void loadGameData() {
        highScore = saveManager.getHighScore();
        playerCoinsTotal = saveManager.getCoins();
        selectedSkinIdx = saveManager.getActiveSkin();
        
        // Validasi agar skin terpilih sudah di-unlock
        if (selectedSkinIdx != 0 && !isSkinUnlocked(selectedSkinIdx)) {
            selectedSkinIdx = 0;
        }
        if (selectedSkinIdx < 0 || selectedSkinIdx >= skins.size()) {
            selectedSkinIdx = 0;
        }
        previewSkinIdx = selectedSkinIdx;
    }

    /**
     * Menyimpan data lokal pemain saat game over atau membeli skin.
     */
    void saveGameData() {
        saveManager.saveCoins(playerCoinsTotal);
        saveManager.saveHighScore(highScore);
        saveManager.saveActiveSkin(selectedSkinIdx);
    }

    boolean isSkinUnlocked(int idx) {
        return saveManager.isSkinUnlocked(idx);
    }

    void unlockSkin(int idx) {
        saveManager.unlockSkin(idx);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (currentState == GameState.GAME) {
            updateGameplay();
        } else {
            // Menggulirkan garis neon di menu utama secara perlahan
            roadOffset += 1.5;
        }
        
        // Mengurangi efek getar layar perlahan
        if (screenShakeAmount > 0) {
            screenShakeAmount--;
        }
        repaint();
    }

    /**
     * Memperbarui logika inti kecepatan jalan raya, skor jarak, dan memanggil update entitas.
     */
    private void updateGameplay() {
        // Skor jarak bertambah sebanding dengan kecepatan kendaraan
        distanceScore += (int) (roadSpeed * 0.1);

        // Gerakkan aspal ke belakang
        roadOffset += roadSpeed;

        // Delegasikan update seluruh entitas (pemain, koin, rintangan, partikel) ke EntityManager
        entityManager.update();
    }

    /**
     * Inisialisasi ulang variabel ketika ronde permainan baru dimulai.
     */
    void initGame() {
        entityManager.clearAll();
        distanceScore = 0;
        coinsCollectedThisRun = 0;
        roadSpeed = 5;

        // Posisikan mobil pemain di tengah jalur jalan raya
        int trackWidth = 500;
        int leftEdge = ((getWidth() > 0 ? getWidth() : 900) - trackWidth) / 2;
        player.x = leftEdge + trackWidth / 2 - player.width / 2;
        player.y = (getHeight() > 0 ? getHeight() : 700) - 200;
        player.driftAngle = 0;
        player.skin = skins.get(selectedSkinIdx);
        player.img = getPlayerImageForSkin(selectedSkinIdx);
        keyLeft = keyRight = false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        recalculateUIBounds();
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Terapkan efek getaran kamera (screen shake) jika terjadi tabrakan
        if (screenShakeAmount > 0) {
            int maxOffset = Math.min(25, screenShakeAmount);
            int dx = random.nextInt(maxOffset) - (maxOffset / 2);
            int dy = random.nextInt(maxOffset) - (maxOffset / 2);
            g2.translate(dx, dy);
        }

        // Delegasikan seluruh tugas rendering layar ke GameRenderer
        gameRenderer.draw(g2);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (currentState == GameState.GAME) {
            if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) keyLeft = true;
            if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) keyRight = true;
            if (keyCode == KeyEvent.VK_ESCAPE) {
                currentState = GameState.MENU;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (currentState == GameState.GAME) {
            if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) keyLeft = false;
            if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) keyRight = false;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();

        if (currentState == GameState.MENU) {
            if (btnPlay.contains(p)) {
                AudioSynth.playSelect();
                initGame();
                currentState = GameState.GAME;
            } else if (btnShop.contains(p)) {
                AudioSynth.playSelect();
                previewSkinIdx = selectedSkinIdx;
                currentState = GameState.SHOP;
            } else if (btnSettings.contains(p)) {
                AudioSynth.playSelect();
                currentState = GameState.SETTINGS;
            } else if (btnExit.contains(p)) {
                AudioSynth.playSelect();
                System.exit(0);
            }
        } else if (currentState == GameState.SETTINGS) {
            if (btnSettingsExit.contains(p)) {
                AudioSynth.playSelect();
                currentState = GameState.MENU;
            } else if (btnToggleSound.contains(p)) {
                AudioSynth.playSelect();
                boolean curSound = AudioSynth.isSoundEnabled();
                AudioSynth.setSoundEnabled(!curSound);
                saveManager.saveSoundEnabled(!curSound);
            }
        } else if (currentState == GameState.SHOP) {
            if (btnShopExit.contains(p)) {
                AudioSynth.playSelect();
                currentState = GameState.MENU;
            } else if (btnShopPrev.contains(p)) {
                AudioSynth.playSelect();
                previewSkinIdx = (previewSkinIdx - 1 + skins.size()) % skins.size();
            } else if (btnShopNext.contains(p)) {
                AudioSynth.playSelect();
                previewSkinIdx = (previewSkinIdx + 1) % skins.size();
            } else if (btnShopAction.contains(p)) {
                Skin previewedSkin = skins.get(previewSkinIdx);
                boolean isUnlocked = isSkinUnlocked(previewSkinIdx);

                if (isUnlocked) {
                    AudioSynth.playSelect();
                    selectedSkinIdx = previewSkinIdx;
                    player.skin = previewedSkin;
                    player.img = getPlayerImageForSkin(previewSkinIdx);
                    saveGameData();
                } else {
                    // Coba membeli skin
                    if (playerCoinsTotal >= previewedSkin.cost) {
                        AudioSynth.playCoin();
                        playerCoinsTotal -= previewedSkin.cost;
                        unlockSkin(previewSkinIdx);
                        selectedSkinIdx = previewSkinIdx;
                        player.skin = previewedSkin;
                        player.img = getPlayerImageForSkin(previewSkinIdx);
                        saveGameData();
                    } else {
                        // Suara bip rendah ganda jika koin tidak cukup
                        new Thread(() -> {
                            try {
                                byte[] buffer = new byte[1600];
                                for (int i = 0; i < buffer.length; i++) {
                                    double freq = i < 800 ? 150 : 100;
                                    buffer[i] = (byte) (Math.sin(2 * Math.PI * freq * i / 8000.0) * 127);
                                }
                                AudioSynth.playBuffer(buffer);
                            } catch (Exception ignored) {}
                        }).start();
                    }
                }
            }
        } else if (currentState == GameState.GAME_OVER) {
            if (btnGoAgain.contains(p)) {
                AudioSynth.playSelect();
                initGame();
                currentState = GameState.GAME;
            } else if (btnGoMenu.contains(p)) {
                AudioSynth.playSelect();
                currentState = GameState.MENU;
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        // Melacak kursor mouse untuk deteksi tombol hover
        mousePos = e.getPoint();
    }

    public EndlessDriveGame getFrame() {
        return frame;
    }
}