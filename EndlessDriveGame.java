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
    private static final String PREF_COINS = "endless_drive_coins";
    private static final String PREF_HIGH_SCORE = "endless_drive_high_score";
    private static final String PREF_UNLOCKED_SKINS = "endless_drive_unlocked_skins";
    private static final String PREF_ACTIVE_SKIN = "endless_drive_active_skin";
    private static final String PREF_SOUND = "endless_drive_sound";
    private static final String PREF_FULLSCREEN = "endless_drive_fullscreen";

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
        dispose(); // Terminate window temporarily to modify decoration state

        if (isCurrentlyFullscreen) {
            setUndecorated(false);
            graphicsDevice.setFullScreenWindow(null);
            setSize(900, 700);
            setLocationRelativeTo(null);
            prefs.putBoolean(PREF_FULLSCREEN, false);
        } else {
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

    /**
     * Synthesizer engine to generate 8-bit retro sounds procedurally on-the-fly.
     * Generates sounds mathematically to eliminate any external file requirement.
     */
    public static class AudioSynth {
        private static boolean soundEnabled = true;

        public static void setSoundEnabled(boolean enabled) {
            soundEnabled = enabled;
        }

        public static boolean isSoundEnabled() {
            return soundEnabled;
        }

        public static void playCoin() {
            if (!soundEnabled) return;
            new Thread(() -> {
                try {
                    // Quick alternating frequencies for classical arcade "ding"
                    byte[] buffer = new byte[3500];
                    for (int i = 0; i < buffer.length; i++) {
                        double freq = (i < 1200) ? 950.0 : 1300.0;
                        double decay = 1.0 - ((double) i / buffer.length);
                        // Using sine wave
                        buffer[i] = (byte) (Math.sin(2 * Math.PI * freq * i / 8000.0) * 127 * decay);
                    }
                    playBuffer(buffer);
                } catch (Exception ignored) {}
            }).start();
        }

        public static void playCrash() {
            if (!soundEnabled) return;
            new Thread(() -> {
                try {
                    // Sweep frequency from low to high with white noise for heavy explosion sound
                    byte[] buffer = new byte[7500];
                    Random rand = new Random();
                    for (int i = 0; i < buffer.length; i++) {
                        double progress = (double) i / buffer.length;
                        double decay = 1.0 - progress;
                        double freqSweep = 200.0 * Math.exp(-4.0 * progress);
                        double rawWave = Math.sin(2 * Math.PI * freqSweep * i / 8000.0);
                        double whiteNoise = rand.nextFloat() * 2.0 - 1.0;
                        // Mix noise with sweep for crunchiness
                        buffer[i] = (byte) (((rawWave * 0.3) + (whiteNoise * 0.7)) * 127 * decay);
                    }
                    playBuffer(buffer);
                } catch (Exception ignored) {}
            }).start();
        }

        public static void playSelect() {
            if (!soundEnabled) return;
            new Thread(() -> {
                try {
                    // Soft laser blip for UI selection
                    byte[] buffer = new byte[1200];
                    for (int i = 0; i < buffer.length; i++) {
                        double progress = (double) i / buffer.length;
                        double freq = 600.0 - (progress * 400.0); // Sweep down
                        double decay = 1.0 - progress;
                        buffer[i] = (byte) (Math.sin(2 * Math.PI * freq * i / 8000.0) * 127 * decay);
                    }
                    playBuffer(buffer);
                } catch (Exception ignored) {}
            }).start();
        }

        private static void playBuffer(byte[] buffer) throws Exception {
            // Standard 8000Hz, 8-bit, Mono, Signed, 1 byte/frame configuration
            AudioFormat format = new AudioFormat(8000f, 8, 1, true, false);
            SourceDataLine line = AudioSystem.getSourceDataLine(format);
            line.open(format);
            line.start();
            line.write(buffer, 0, buffer.length);
            line.drain();
            line.close();
        }
    }

    /**
     * Skin model structure containing color styling, names, and purchase data.
     */
    public static class Skin {
        public String name;
        public int cost;
        public Color primaryColor;
        public Color secondaryColor;
        public Color glowColor;
        public String desc;

        public Skin(String name, int cost, Color pCol, Color sCol, Color gCol, String desc) {
            this.name = name;
            this.cost = cost;
            this.primaryColor = pCol;
            this.secondaryColor = sCol;
            this.glowColor = gCol;
            this.desc = desc;
        }
    }

    /**
     * Represents physical cars (player and obstacles).
     */
    public static class Vehicle {
        public double x, y;
        public int width = 50;
        public int height = 90;
        public double speed;
        public Skin skin;
        public double driftAngle = 0.0;

        public Vehicle(double x, double y, Skin skin, double speed) {
            this.x = x;
            this.y = y;
            this.skin = skin;
            this.speed = speed;
        }

        public Rectangle2D.Double getBounds() {
            // Shrink hitbox slightly for better, player-forgiving gameplay feeling
            return new Rectangle2D.Double(x + 4, y + 4, width - 8, height - 8);
        }

        public void draw(Graphics2D g) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Save context for smooth drifting rotation
            AffineTransform old = g.getTransform();
            g.translate(x + width / 2.0, y + height / 2.0);
            g.rotate(Math.toRadians(driftAngle));

            // Draw Neon Glow shadow
            g.setColor(new Color(skin.glowColor.getRed(), skin.glowColor.getGreen(), skin.glowColor.getBlue(), 60));
            g.fill(new RoundRectangle2D.Double(-width / 2.0 - 4, -height / 2.0 - 4, width + 8, height + 8, 12, 12));

            // Draw Car Wheels
            g.setColor(new Color(25, 25, 30));
            g.fillRect(-width / 2 - 4, -height / 2 + 10, 6, 16); // Front Left
            g.fillRect(width / 2 - 2, -height / 2 + 10, 6, 16);  // Front Right
            g.fillRect(-width / 2 - 4, height / 2 - 26, 6, 16);  // Rear Left
            g.fillRect(width / 2 - 2, height / 2 - 26, 6, 16);   // Rear Right

            // Main Car Body (Procedural Retro Polygon Design)
            g.setColor(skin.primaryColor);
            g.fillRoundRect(-width / 2, -height / 2, width, height, 10, 10);

            // Roof Accent & Stripe details
            g.setColor(skin.secondaryColor);
            g.fillRect(-width / 6, -height / 2 + 4, width / 3, height / 2);
            g.fillRect(-width / 2 + 6, height / 4, width - 12, 6);

            // Cockpit Windshield (Polygonal Dark Blue/Cyan Glass)
            g.setColor(new Color(15, 45, 60));
            g.fillRoundRect(-width / 3, -height / 5, 2 * width / 3, height / 4, 4, 4);
            g.setColor(new Color(0, 240, 255));
            g.setStroke(new BasicStroke(1));
            g.drawRoundRect(-width / 3, -height / 5, 2 * width / 3, height / 4, 4, 4);

            // Headlights glowing (Active Forward yellow/cyan lights)
            g.setColor(new Color(255, 255, 200, 200));
            g.fillArc(-width / 2 + 6, -height / 2 - 10, 12, 15, 45, 90);
            g.fillArc(width / 2 - 18, -height / 2 - 10, 12, 15, 45, 90);

            // Break / Taillights glowing
            g.setColor(new Color(255, 30, 30));
            g.fillRect(-width / 2 + 8, height / 2 - 4, 8, 4);
            g.fillRect(width / 2 - 16, height / 2 - 4, 8, 4);

            g.setTransform(old);
        }
    }

    /**
     * Score booster collectibles appearing on lanes.
     */
    public static class Coin {
        public double x, y;
        public int size = 26;
        private double rotation = 0.0;

        public Coin(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public Rectangle2D.Double getBounds() {
            return new Rectangle2D.Double(x, y, size, size);
        }

        public void update() {
            rotation += 4.5;
        }

        public void draw(Graphics2D g) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Neon rotating golden medallion glow
            g.setColor(new Color(255, 215, 0, 80));
            g.fillOval((int) x - 3, (int) y - 3, size + 6, size + 6);

            AffineTransform old = g.getTransform();
            g.translate(x + size/2.0, y + size/2.0);
            g.rotate(Math.toRadians(rotation));

            g.setColor(new Color(255, 200, 10));
            g.fillOval(-size/2, -size/2, size, size);

            g.setColor(new Color(255, 255, 120));
            g.setStroke(new BasicStroke(2));
            g.drawOval(-size/2, -size/2, size, size);

            // Draw dollar '$' logo inside the coin
            g.setFont(new Font("Segoe UI", Font.BOLD, 14));
            FontMetrics fm = g.getFontMetrics();
            g.drawString("$", -fm.stringWidth("$") / 2, fm.getAscent() - fm.getHeight()/2 - 1);

            g.setTransform(old);
        }
    }

    /**
     * Neon explosion debris and drift smoke simulation particles.
     */
    public static class Particle {
        public double x, y, vx, vy;
        public Color color;
        public int size;
        public int life;
        public int maxLife;

        public Particle(double x, double y, double vx, double vy, Color color, int size, int maxLife) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.color = color;
            this.size = size;
            this.maxLife = maxLife;
            this.life = maxLife;
        }

        public boolean update() {
            x += vx;
            y += vy;
            life--;
            return life > 0;
        }

        public void draw(Graphics2D g) {
            float alpha = (float) life / maxLife;
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (alpha * 255)));
            g.fillRect((int) x, (int) y, size, size);
        }
    }

    /**
     * Primary Graphics rendering engine handling gameplay physics, drawing, inputs,
     * and dynamic screens.
     */
    public static class GamePanel extends JPanel implements ActionListener, KeyListener, MouseListener, MouseMotionListener {
        
        // Screens State Control
        public enum GameState { MENU, GAME, SETTINGS, SHOP, GAME_OVER }
        private GameState currentState = GameState.MENU;

        private EndlessDriveGame frame;
        private Timer gameTimer;
        
        // Road Speed & Tracking Parameters
        private double roadOffset = 0;
        private double roadSpeed = 5;
        private double targetRoadSpeed = 10;
        private final double maxRoadSpeed = 22;
        private final double acceleration = 0.05;

        // Player configuration
        private Vehicle player;
        private int selectedSkinIdx = 0;
        private ArrayList<Skin> skins = new ArrayList<>();

        // Dynamic lists for entities
        private ArrayList<Vehicle> traffic = new ArrayList<>();
        private ArrayList<Coin> coins = new ArrayList<>();
        private ArrayList<Particle> particles = new ArrayList<>();

        // Score system & user stats
        private int currentCoins = 0;
        private int distanceScore = 0;
        private int coinsCollectedThisRun = 0;
        private int highScore = 0;
        private int playerCoinsTotal = 0;

        // Visual effects variables
        private int screenShakeAmount = 0;
        private Random random = new Random();

        // Control flags
        private boolean keyLeft = false, keyRight = false, keyUp = false, keyDown = false;

        // Interactive GUI elements (Dynamic bounds updated in recalculateUIBounds)
        private final Rectangle btnPlay = new Rectangle();
        private final Rectangle btnShop = new Rectangle();
        private final Rectangle btnSettings = new Rectangle();
        private Point mousePos = new Point(0, 0);

        // Shop Pagination & Navigation parameters
        private int previewSkinIdx = 0;
        private final Rectangle btnShopPrev = new Rectangle();
        private final Rectangle btnShopNext = new Rectangle();
        private final Rectangle btnShopAction = new Rectangle();
        private final Rectangle btnShopExit = new Rectangle();

        // Settings Buttons
        private final Rectangle btnToggleSound = new Rectangle();
        private final Rectangle btnToggleWindow = new Rectangle();
        private final Rectangle btnSettingsExit = new Rectangle();

        // Game Over Buttons
        private final Rectangle btnGoAgain = new Rectangle();
        private final Rectangle btnGoMenu = new Rectangle();

        public GamePanel(EndlessDriveGame frame) {
            this.frame = frame;
            setBackground(new Color(11, 0, 26)); // Deep Synthwave Void Purple
            setFocusable(true);
            addKeyListener(this);
            addMouseListener(this);
            addMouseMotionListener(this);

            // Pre-create skins catalog
            skins.add(new Skin("Apex Crimson", 0, new Color(220, 20, 60), new Color(40, 40, 45), new Color(255, 50, 50), "Standard high-performance sports vehicle. Ready to roll."));
            skins.add(new Skin("Neon Pulse", 50, new Color(0, 240, 255), new Color(255, 0, 127), new Color(0, 255, 255), "Electrified design loaded with neon carbon plating."));
            skins.add(new Skin("Desert Nomad", 150, new Color(255, 140, 0), new Color(90, 75, 50), new Color(255, 165, 0), "Armored rugged desert build with reinforced iron grill."));
            skins.add(new Skin("Royal Phoenix", 300, new Color(255, 215, 0), new Color(75, 0, 130), new Color(255, 223, 0), "Majestic luxury hypercar covered in glistening micro-gold plating."));

            loadGameData();

            // Set default active skin
            player = new Vehicle(425, 500, skins.get(selectedSkinIdx), 0);

            // Setup Main Game timer looping at ~60 FPS (16ms per frame)
            gameTimer = new Timer(16, this);
            gameTimer.start();
        }

        /**
         * Dynamic alignment calculation for UI hitboxes in any window size/fullscreen aspect ratio.
         */
        private void recalculateUIBounds() {
            int w = getWidth();
            int h = getHeight();
            if (w <= 0 || h <= 0) return;

            int centerY = h / 2;

            // Menu Buttons (centered horizontally)
            btnPlay.setBounds(w / 2 - 100, centerY - 30, 200, 45);
            btnShop.setBounds(w / 2 - 100, centerY + 30, 200, 45);
            btnSettings.setBounds(w / 2 - 100, centerY + 90, 200, 45);

            // Shop Buttons
            btnShopPrev.setBounds(w / 2 - 250, centerY + 30, 60, 45);
            btnShopNext.setBounds(w / 2 + 190, centerY + 30, 60, 45);
            btnShopAction.setBounds(w / 2 - 100, centerY + 130, 200, 50);
            btnShopExit.setBounds(50, 50, 100, 40);

            // Settings Buttons
            btnToggleSound.setBounds(w / 2 - 150, centerY - 90, 300, 45);
            btnToggleWindow.setBounds(w / 2 - 150, centerY - 20, 300, 45);
            btnSettingsExit.setBounds(50, 50, 100, 40);

            // Game Over Buttons
            btnGoAgain.setBounds(w / 2 - 220, centerY + 130, 200, 45);
            btnGoMenu.setBounds(w / 2 + 20, centerY + 130, 200, 45);
        }

        /**
         * Loads high score, coins count, and skin states from storage preferences.
         */
        private void loadGameData() {
            Preferences p = frame.getPrefs();
            highScore = p.getInt(PREF_HIGH_SCORE, 0);
            playerCoinsTotal = p.getInt(PREF_COINS, 0);
            selectedSkinIdx = p.getInt(PREF_ACTIVE_SKIN, 0);
            if (selectedSkinIdx < 0 || selectedSkinIdx >= skins.size()) {
                selectedSkinIdx = 0;
            }
            previewSkinIdx = selectedSkinIdx;
        }

        /**
         * Saves state metrics to prevent loss of currency or progress.
         */
        private void saveGameData() {
            Preferences p = frame.getPrefs();
            p.putInt(PREF_COINS, playerCoinsTotal);
            p.putInt(PREF_HIGH_SCORE, highScore);
            p.putInt(PREF_ACTIVE_SKIN, selectedSkinIdx);
        }

        private boolean isSkinUnlocked(int idx) {
            if (idx == 0) return true; // Default skin is free
            String unlockedList = frame.getPrefs().get(PREF_UNLOCKED_SKINS, "0");
            String[] indices = unlockedList.split(",");
            for (String s : indices) {
                if (s.trim().equals(String.valueOf(idx))) return true;
            }
            return false;
        }

        private void unlockSkin(int idx) {
            Preferences p = frame.getPrefs();
            String unlockedList = p.get(PREF_UNLOCKED_SKINS, "0");
            if (!unlockedList.contains(String.valueOf(idx))) {
                unlockedList += "," + idx;
                p.put(PREF_UNLOCKED_SKINS, unlockedList);
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentState == GameState.GAME) {
                updateGameplay();
            } else {
                // Background visual update for other menus (making neon grid slide)
                roadOffset += 1.5;
            }
            repaint();
        }

        /**
         * Physics, movements, dynamic difficulty adjustments, spawn logic, and collisions
         */
        private void updateGameplay() {
            // Keep screen shake decaying
            if (screenShakeAmount > 0) screenShakeAmount--;

            // Handle smooth driving acceleration & deceleration
            if (keyUp) {
                targetRoadSpeed = maxRoadSpeed;
            } else if (keyDown) {
                targetRoadSpeed = 3.0;
            } else {
                targetRoadSpeed = 10.0;
            }

            // Interpolate road speed smoothly
            if (roadSpeed < targetRoadSpeed) {
                roadSpeed += acceleration;
            } else if (roadSpeed > targetRoadSpeed) {
                roadSpeed -= acceleration * 2.0;
            }

            // Distance score accumulates proportionally to road speed
            distanceScore += (int) (roadSpeed * 0.1);

            // Move the background lines
            roadOffset += roadSpeed;

            // Player Horizontal steering with drift aesthetics
            double steerSpeed = 7.5;
            if (keyLeft) {
                player.x -= steerSpeed;
                player.driftAngle = Math.max(-12, player.driftAngle - 1.5);
                spawnDriftSmoke();
            } else if (keyRight) {
                player.x += steerSpeed;
                player.driftAngle = Math.min(12, player.driftAngle + 1.5);
                spawnDriftSmoke();
            } else {
                // Pull drift angle back to zero when centering
                if (player.driftAngle > 0) player.driftAngle = Math.max(0, player.driftAngle - 1.0);
                if (player.driftAngle < 0) player.driftAngle = Math.min(0, player.driftAngle + 1.0);
            }

            // Boundary Lock (Centered on current screen size)
            int trackWidth = 500;
            int leftEdge = (getWidth() - trackWidth) / 2;
            int rightEdge = leftEdge + trackWidth;
            int laneMinX = leftEdge;
            int laneMaxX = rightEdge - player.width;
            if (player.x < laneMinX) {
                player.x = laneMinX;
                player.driftAngle = 0;
            }
            if (player.x > laneMaxX) {
                player.x = laneMaxX;
                player.driftAngle = 0;
            }

            // Spawn Obstacles (Traffic cars coming down) centered in lanes
            if (random.nextInt(100) < 3.5 && traffic.size() < 4) {
                int lane = random.nextInt(3);
                int spawnX = leftEdge + 20 + lane * 155;
                // Double check to make sure it doesn't instantly crash into existing traffic on spawn
                boolean spaceClear = true;
                for (Vehicle ob : traffic) {
                    if (Math.abs(ob.y) < 200 && Math.abs(ob.x - spawnX) < 40) {
                        spaceClear = false;
                        break;
                    }
                }
                if (spaceClear) {
                    // Pick a random style for incoming car
                    Skin trafficSkin = skins.get(random.nextInt(skins.size()));
                    // Coming downwards relative to player speed
                    double obSpeed = 2 + random.nextInt(5);
                    traffic.add(new Vehicle(spawnX, -100, trafficSkin, obSpeed));
                }
            }

            // Spawn Coins nicely in lanes
            if (random.nextInt(100) < 2.5 && coins.size() < 3) {
                int lane = random.nextInt(3);
                int spawnX = leftEdge + 35 + lane * 155;
                boolean spaceClear = true;
                for (Coin c : coins) {
                    if (Math.abs(c.y) < 150) {
                        spaceClear = false;
                        break;
                    }
                }
                if (spaceClear) {
                    coins.add(new Coin(spawnX, -50));
                }
            }

            // Update Traffic and handle bounding boxes
            for (int i = traffic.size() - 1; i >= 0; i--) {
                Vehicle ob = traffic.get(i);
                // Obstacle speed combined with relative motion of player's perspective
                ob.y += ob.speed + (roadSpeed - 5) * 0.5;

                // Collided with player?
                if (ob.getBounds().intersects(player.getBounds())) {
                    triggerExplosion(player.x + player.width/2.0, player.y + player.height/2.0);
                    AudioSynth.playCrash();
                    screenShakeAmount = 25;
                    
                    // Finalize Score stats
                    playerCoinsTotal += coinsCollectedThisRun;
                    if (distanceScore > highScore) {
                        highScore = distanceScore;
                    }
                    saveGameData();

                    currentState = GameState.GAME_OVER;
                    return;
                }

                // Delete out-of-bounds obstacles
                if (ob.y > getHeight()) {
                    traffic.remove(i);
                }
            }

            // Update Coins
            for (int i = coins.size() - 1; i >= 0; i--) {
                Coin c = coins.get(i);
                c.y += roadSpeed;
                c.update();

                // Check Coin Pickup
                if (c.getBounds().intersects(player.getBounds())) {
                    coinsCollectedThisRun++;
                    AudioSynth.playCoin();
                    triggerCollectSpark(c.x + c.size / 2.0, c.y + c.size / 2.0);
                    coins.remove(i);
                    continue;
                }

                if (c.y > getHeight()) {
                    coins.remove(i);
                }
            }

            // Update Active Particles
            for (int i = particles.size() - 1; i >= 0; i--) {
                Particle p = particles.get(i);
                if (!p.update()) {
                    particles.remove(i);
                }
            }
        }

        private void spawnDriftSmoke() {
            if (random.nextInt(10) > 3) return;
            // Generate neon burning trail behind rear tires
            particles.add(new Particle(
                    player.x + 8 + random.nextInt(player.width - 16),
                    player.y + player.height - 5,
                    (random.nextDouble() - 0.5) * 2,
                    2 + random.nextDouble() * 3,
                    new Color(255, 0, 127, 180),
                    4,
                    25
            ));
        }

        private void triggerExplosion(double x, double y) {
            for (int i = 0; i < 40; i++) {
                double angle = random.nextDouble() * 2 * Math.PI;
                double speed = 2 + random.nextDouble() * 10;
                particles.add(new Particle(
                        x, y,
                        Math.cos(angle) * speed,
                        Math.sin(angle) * speed,
                        random.nextBoolean() ? player.skin.primaryColor : Color.YELLOW,
                        6 + random.nextInt(6),
                        50 + random.nextInt(30)
                ));
            }
        }

        private void triggerCollectSpark(double x, double y) {
            for (int i = 0; i < 15; i++) {
                double angle = random.nextDouble() * 2 * Math.PI;
                double speed = 1 + random.nextDouble() * 5;
                particles.add(new Particle(
                        x, y,
                        Math.cos(angle) * speed,
                        Math.sin(angle) * speed,
                        new Color(255, 215, 0),
                        4,
                        20 + random.nextInt(15)
                ));
            }
        }

        private void initGame() {
            traffic.clear();
            coins.clear();
            particles.clear();
            distanceScore = 0;
            coinsCollectedThisRun = 0;
            roadSpeed = 5;

            // Center initial player coordinates based on container width
            int trackWidth = 500;
            int leftEdge = (getWidth() > 0 ? getWidth() : 900 - trackWidth) / 2;
            player.x = leftEdge + trackWidth / 2 - player.width / 2;
            player.y = (getHeight() > 0 ? getHeight() : 700) - 200;
            player.driftAngle = 0;
            player.skin = skins.get(selectedSkinIdx);
            keyLeft = keyRight = keyUp = keyDown = false;
        }

        @Override
        protected void paintComponent(Graphics g) {
            // Re-align and adjust UI buttons bounds dynamically before rendering
            recalculateUIBounds();

            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            // Apply screen shake effect translation if active
            if (screenShakeAmount > 0) {
                int dx = random.nextInt(screenShakeAmount) - (screenShakeAmount / 2);
                int dy = random.nextInt(screenShakeAmount) - (screenShakeAmount / 2);
                g2.translate(dx, dy);
            }

            // Draw Background Gradients & Scrolling Neon Track
            drawRoadSkyline(g2);

            switch (currentState) {
                case MENU:
                    drawMenuScreen(g2);
                    break;
                case GAME:
                    drawGameplayScreen(g2);
                    break;
                case SETTINGS:
                    drawSettingsScreen(g2);
                    break;
                case SHOP:
                    drawShopScreen(g2);
                    break;
                case GAME_OVER:
                    drawGameOverScreen(g2);
                    break;
            }
        }

        /**
         * Renders the retro-neon synthwave background and scrolling asphalt lanes.
         */
        private void drawRoadSkyline(Graphics2D g) {
            int width = getWidth();
            int height = getHeight();

            // Background solid fill
            g.setColor(new Color(10, 2, 22));
            g.fillRect(0, 0, width, height);

            // Draw horizontal perspective lines (Starfield perspective)
            g.setColor(new Color(60, 10, 80));
            g.setStroke(new BasicStroke(1));
            int horizon = height / 3;
            for (int i = horizon; i < height; i += 40) {
                g.drawLine(0, i, width, i);
            }

            // Draw vertical perspective grid line beams on left and right shoulder panels
            int totalLines = 14;
            g.setStroke(new BasicStroke(2));
            for (int i = 0; i <= totalLines; i++) {
                int xStart = (width / totalLines) * i;
                g.drawLine(xStart, horizon, (int) (xStart * 1.5 - width * 0.25), height);
            }

            // Draw Road Track Boundaries
            int trackWidth = 500;
            int leftEdge = (width - trackWidth) / 2;
            int rightEdge = leftEdge + trackWidth;

            // Dark Asphalt Road Surface
            g.setColor(new Color(25, 10, 40));
            g.fillRect(leftEdge, 0, trackWidth, height);

            // Highlight margins (Neon cyber stripes)
            g.setColor(new Color(0, 240, 255)); // Cyber Cyan
            g.setStroke(new BasicStroke(4));
            g.drawLine(leftEdge, 0, leftEdge, height);
            g.drawLine(rightEdge, 0, rightEdge, height);

            // Lane Dividers (Dashed Scrolling Lines)
            g.setColor(new Color(255, 0, 127)); // Hot Pink Neon
            g.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{30.0f, 30.0f}, (float) roadOffset));
            int laneWidth = trackWidth / 3;
            g.drawLine(leftEdge + laneWidth, 0, leftEdge + laneWidth, height);
            g.drawLine(leftEdge + laneWidth * 2, 0, leftEdge + laneWidth * 2, height);
        }

        private void drawGameOverScreen(Graphics2D g) {
            int width = getWidth();
            int height = getHeight();
            int centerY = height / 2;

            // Translucent dark violet backdrop overlay
            g.setColor(new Color(11, 0, 26, 220));
            g.fillRect(0, 0, width, height);

            // Large Retro Font Glow Header
            g.setFont(new Font("Impact", Font.ITALIC, 70));
            g.setColor(new Color(255, 0, 127));
            String title = "CRASH DETECTED";
            int titleWidth = g.getFontMetrics().stringWidth(title);
            g.drawString(title, width/2 - titleWidth/2, centerY - 150);

            g.setFont(new Font("Impact", Font.ITALIC, 69));
            g.setColor(new Color(0, 240, 255));
            g.drawString(title, width/2 - titleWidth/2 - 2, centerY - 152);

            // Show Stats
            g.setFont(new Font("Segoe UI", Font.BOLD, 22));
            g.setColor(Color.WHITE);
            String distStr = "Distance Traveled: " + distanceScore + "m";
            String coinsStr = "Coins Gathered: " + coinsCollectedThisRun;
            String bestStr = "Personal High Score: " + highScore + "m";

            g.drawString(distStr, width/2 - g.getFontMetrics().stringWidth(distStr)/2, centerY - 50);
            g.drawString(coinsStr, width/2 - g.getFontMetrics().stringWidth(coinsStr)/2, centerY);
            
            g.setColor(new Color(255, 215, 0));
            g.drawString(bestStr, width/2 - g.getFontMetrics().stringWidth(bestStr)/2, centerY + 50);

            // Restart Button
            drawCustomButton(g, btnGoAgain, "PLAY AGAIN", new Color(0, 240, 255), true);
            // Main Menu Button
            drawCustomButton(g, btnGoMenu, "MAIN MENU", new Color(255, 0, 127), false);
        }

        private void drawMenuScreen(Graphics2D g) {
            int width = getWidth();
            int height = getHeight();
            int centerY = height / 2;

            // Overlay panel
            g.setColor(new Color(11, 0, 26, 160));
            g.fillRect(0, 0, width, height);

            // Title "ENDLESS DRIVE" Retro synthwave typography
            g.setFont(new Font("Impact", Font.ITALIC, 80));
            String title = "ENDLESS DRIVE";
            int titleWidth = g.getFontMetrics().stringWidth(title);
            
            // Neon pink / magenta back drop shadow
            g.setColor(new Color(255, 0, 127));
            g.drawString(title, width/2 - titleWidth/2 + 3, centerY - 147);

            // Main cyan text
            g.setColor(new Color(0, 240, 255));
            g.drawString(title, width/2 - titleWidth/2, centerY - 150);

            // Subtitle
            g.setFont(new Font("Segoe UI", Font.ITALIC, 18));
            g.setColor(Color.WHITE);
            String subtitle = "Steer smoothly. Dodge oncoming racers. Collect neon shards.";
            g.drawString(subtitle, width/2 - g.getFontMetrics().stringWidth(subtitle)/2, centerY - 90);

            // Display Top Info
            g.setFont(new Font("Segoe UI", Font.BOLD, 15));
            g.setColor(new Color(255, 215, 0));
            String hscore = "HIGH SCORE: " + highScore + "m";
            g.drawString(hscore, width - g.getFontMetrics().stringWidth(hscore) - 40, 40);

            String coinBal = "COINS BALANCE: $" + playerCoinsTotal;
            g.drawString(coinBal, 40, 40);

            // Draw Action Buttons
            drawCustomButton(g, btnPlay, "DRIVE NOW", new Color(0, 240, 255), true);
            drawCustomButton(g, btnShop, "NEON SHOP", new Color(255, 0, 127), false);
            drawCustomButton(g, btnSettings, "SETTINGS", Color.GRAY, false);
        }

        private void drawShopScreen(Graphics2D g) {
            int width = getWidth();
            int height = getHeight();
            int centerY = height / 2;

            g.setColor(new Color(11, 0, 26, 230));
            g.fillRect(0, 0, width, height);

            // Header Section
            g.setFont(new Font("Impact", Font.ITALIC, 50));
            g.setColor(new Color(255, 0, 127));
            String title = "COSMETIC SHOP";
            g.drawString(title, width / 2 - g.getFontMetrics().stringWidth(title) / 2, centerY - 200);

            // Exit Button
            drawCustomButton(g, btnShopExit, "BACK", Color.WHITE, false);

            // Coin counter
            g.setFont(new Font("Segoe UI", Font.BOLD, 18));
            g.setColor(new Color(255, 215, 0));
            String coinsStr = "COINS BALANCE: $" + playerCoinsTotal;
            g.drawString(coinsStr, width - g.getFontMetrics().stringWidth(coinsStr) - 50, 75);

            // Prev and Next Buttons for pagination
            drawCustomButton(g, btnShopPrev, "<", new Color(0, 240, 255), false);
            drawCustomButton(g, btnShopNext, ">", new Color(0, 240, 255), false);

            // Previewing skin rendering area
            Skin currentPreview = skins.get(previewSkinIdx);

            // Showcase Showcase Pedestal circle
            g.setColor(new Color(currentPreview.glowColor.getRed(), currentPreview.glowColor.getGreen(), currentPreview.glowColor.getBlue(), 40));
            g.fillOval(width / 2 - 100, centerY - 150, 200, 200);
            g.setColor(currentPreview.glowColor);
            g.setStroke(new BasicStroke(3));
            g.drawOval(width / 2 - 100, centerY - 150, 200, 200);

            // Render Preview Vehicle in center of pedestal
            Vehicle mockCar = new Vehicle(width / 2 - 25, centerY - 95, currentPreview, 0);
            mockCar.draw(g);

            // Display Selected Skin Properties
            g.setFont(new Font("Segoe UI", Font.BOLD, 26));
            g.setColor(Color.WHITE);
            int nameWidth = g.getFontMetrics().stringWidth(currentPreview.name);
            g.drawString(currentPreview.name, width / 2 - nameWidth / 2, centerY + 85);

            g.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            g.setColor(new Color(180, 180, 200));
            int descWidth = g.getFontMetrics().stringWidth(currentPreview.desc);
            g.drawString(currentPreview.desc, width / 2 - descWidth / 2, centerY + 110);

            // Render Action purchase/unlock buttons
            boolean unlocked = isSkinUnlocked(previewSkinIdx);
            boolean equipped = (previewSkinIdx == selectedSkinIdx);

            String actionText = "";
            Color buttonColor = Color.GREEN;

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

            drawCustomButton(g, btnShopAction, actionText, buttonColor, true);
        }

        private void drawSettingsScreen(Graphics2D g) {
            int width = getWidth();
            int height = getHeight();
            int centerY = height / 2;

            g.setColor(new Color(11, 0, 26, 230));
            g.fillRect(0, 0, width, height);

            // Header Section
            g.setFont(new Font("Impact", Font.ITALIC, 50));
            g.setColor(new Color(255, 0, 127));
            String header = "SETTINGS CONFIG";
            g.drawString(header, width / 2 - g.getFontMetrics().stringWidth(header) / 2, centerY - 180);

            // Exit Button
            drawCustomButton(g, btnSettingsExit, "BACK", Color.WHITE, false);

            // Sound Button Rendering
            String soundText = "BACKSOUND/SOUNDS: " + (AudioSynth.isSoundEnabled() ? "ON" : "OFF");
            drawCustomButton(g, btnToggleSound, soundText, new Color(0, 240, 255), false);

            // Fullscreen Button
            String windowText = "FULLSCREEN TOGGLE";
            drawCustomButton(g, btnToggleWindow, windowText, new Color(255, 0, 127), false);

            // Control Instructions Cheat Sheet
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

        private void drawGameplayScreen(Graphics2D g) {
            int width = getWidth();
            int height = getHeight();

            // Draw HUD Info boxes on upper left/right screens
            // Soft Glass Backdrop panel top
            g.setColor(new Color(0, 0, 0, 100));
            g.fillRect(20, 15, width - 40, 55);
            g.setColor(new Color(0, 240, 255, 50));
            g.setStroke(new BasicStroke(1));
            g.drawRect(20, 15, width - 40, 55);

            // Distance Travelled text
            g.setFont(new Font("Impact", Font.PLAIN, 24));
            g.setColor(Color.WHITE);
            g.drawString("DISTANCE: " + distanceScore + "m", 40, 48);

            // Speed gauge string
            g.setFont(new Font("Impact", Font.PLAIN, 20));
            g.setColor(new Color(255, 0, 127));
            g.drawString("SPEED: " + (int)(roadSpeed * 10) + " KM/H", width / 2 - 90, 48);

            // Coin counter
            g.setFont(new Font("Impact", Font.PLAIN, 24));
            g.setColor(new Color(255, 215, 0));
            g.drawString("SHARDS: " + coinsCollectedThisRun, width - 180, 48);

            // Render active entities
            for (Coin c : coins) {
                c.draw(g);
            }

            for (Vehicle ob : traffic) {
                ob.draw(g);
            }

            // Draw Particles
            for (Particle p : particles) {
                p.draw(g);
            }

            // Draw Player
            player.draw(g);
        }

        /**
         * Re-usable custom neon-looking hover responsive button renderer.
         */
        private void drawCustomButton(Graphics2D g, Rectangle r, String text, Color baseColor, boolean pulseGlow) {
            boolean isHovered = r.contains(mousePos);
            
            // Adjust transparency dynamically according to hover
            Color boxColor = isHovered ? baseColor.brighter() : baseColor;
            
            // Draw neon border shadow glow
            if (isHovered || pulseGlow) {
                g.setColor(new Color(boxColor.getRed(), boxColor.getGreen(), boxColor.getBlue(), 60));
                g.fill(new RoundRectangle2D.Double(r.x - 3, r.y - 3, r.width + 6, r.height + 6, 8, 8));
            }

            // Main Background Fill
            g.setColor(new Color(20, 5, 35, 220));
            g.fill(new RoundRectangle2D.Double(r.x, r.y, r.width, r.height, 8, 8));

            // Solid Stroke boundary
            g.setColor(boxColor);
            g.setStroke(new BasicStroke(isHovered ? 3 : 2));
            g.draw(new RoundRectangle2D.Double(r.x, r.y, r.width, r.height, 8, 8));

            // Text configuration
            g.setFont(new Font("Segoe UI", Font.BOLD, 15));
            FontMetrics fm = g.getFontMetrics();
            int tx = r.x + (r.width - fm.stringWidth(text)) / 2;
            int ty = r.y + (r.height - fm.getHeight()) / 2 + fm.getAscent();
            
            g.setColor(isHovered ? Color.WHITE : boxColor);
            g.drawString(text, tx, ty);
        }

        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();

            if (currentState == GameState.GAME) {
                if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) keyLeft = true;
                if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) keyRight = true;
                if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W) keyUp = true;
                if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S) keyDown = true;
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
                if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W) keyUp = false;
                if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S) keyDown = false;
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
                }
            } else if (currentState == GameState.SETTINGS) {
                if (btnSettingsExit.contains(p)) {
                    AudioSynth.playSelect();
                    currentState = GameState.MENU;
                } else if (btnToggleSound.contains(p)) {
                    AudioSynth.playSelect();
                    boolean curSound = AudioSynth.isSoundEnabled();
                    AudioSynth.setSoundEnabled(!curSound);
                    frame.getPrefs().putBoolean(PREF_SOUND, !curSound);
                } else if (btnToggleWindow.contains(p)) {
                    AudioSynth.playSelect();
                    frame.toggleFullscreen();
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
                        saveGameData();
                    } else {
                        // Attempt to buy the skin
                        if (playerCoinsTotal >= previewedSkin.cost) {
                            AudioSynth.playCoin();
                            playerCoinsTotal -= previewedSkin.cost;
                            unlockSkin(previewSkinIdx);
                            selectedSkinIdx = previewSkinIdx;
                            player.skin = previewedSkin;
                            saveGameData();
                        } else {
                            // Synthesize error low-pitched double beep sound
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
            // Keep track of cursor position for dynamic rendering glow states
            mousePos = e.getPoint();
        }
    }
}