import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

/**
 * Kelas EntityManager mengelola seluruh objek dinamis (entitas) dalam game,
 * seperti kendaraan rintangan (traffic), koin (shards), dan partikel efek.
 * 
 * Tanggung jawab utamanya meliputi:
 * - Melakukan update pergerakan entitas berdasarkan kecepatan jalan raya.
 * - Membangkitkan rintangan dan koin baru (spawning) secara acak pada lajur yang tepat.
 * - Melakukan deteksi tabrakan (collision detection) antara pemain dengan rintangan atau koin.
 * - Memicu efek visual berupa ledakan partikel atau percikan koin.
 */
public class EntityManager {
    private GamePanel gp;
    
    // Daftar entitas dinamis
    private ArrayList<Vehicle> traffic;
    private ArrayList<Coin> coins;
    private ArrayList<Particle> particles;
    
    private Random random;

    /**
     * Konstruktor EntityManager.
     * @param gp Referensi ke GamePanel utama untuk mengakses parameter state game
     */
    public EntityManager(GamePanel gp) {
        this.gp = gp;
        this.traffic = new ArrayList<>();
        this.coins = new ArrayList<>();
        this.particles = new ArrayList<>();
        this.random = new Random();
    }

    /**
     * Membersihkan semua entitas aktif dari layar (misalnya saat game di-restart).
     */
    public void clearAll() {
        traffic.clear();
        coins.clear();
        particles.clear();
    }

    /**
     * Memperbarui fisika, pergerakan, pemunculan, dan tabrakan semua entitas.
     */
    public void update() {
        // 1. Pergerakan dasar pemain berdasarkan steering dan drift
        double steerSpeed = 2.5;

        if (gp.keyLeft) {
            gp.player.x -= steerSpeed;
            gp.player.driftAngle = Math.max(-12, gp.player.driftAngle - 1.5);
            spawnDriftSmoke();
        } else if (gp.keyRight) {
            gp.player.x += steerSpeed;
            gp.player.driftAngle = Math.min(12, gp.player.driftAngle + 1.5);
            spawnDriftSmoke();
        } else {
            // Mengembalikan sudut mobil ke 0 saat lurus
            if (gp.player.driftAngle > 0) gp.player.driftAngle = Math.max(0, gp.player.driftAngle - 1.0);
            if (gp.player.driftAngle < 0) gp.player.driftAngle = Math.min(0, gp.player.driftAngle + 1.0);
        }


        // Batasi posisi pemain di jalur jalan raya (kiri/kanan)
        int trackWidth = 500;
        int leftEdge = (gp.getWidth() - trackWidth) / 2;
        int rightEdge = leftEdge + trackWidth;
        int laneMinX = leftEdge;
        int laneMaxX = rightEdge - gp.player.width;
        if (gp.player.x < laneMinX) {
            gp.player.x = laneMinX;
            gp.player.driftAngle = 0;
        }
        if (gp.player.x > laneMaxX) {
            gp.player.x = laneMaxX;
            gp.player.driftAngle = 0;
        }

        // 2. Spawn kendaraan rintangan (traffic) secara acak
        if (random.nextInt(100) < 3.5 && traffic.size() < 4) {
            int lane = random.nextInt(3);
            int spawnX = leftEdge + 20 + lane * 155;
            
            // Cek jarak aman agar tidak menumpuk saat spawn
            boolean spaceClear = true;
            for (Vehicle ob : traffic) {
                if (Math.abs(ob.y) < 200 && Math.abs(ob.x - spawnX) < 40) {
                    spaceClear = false;
                    break;
                }
            }
            
            if (spaceClear) {
                Skin trafficSkin = gp.skins.get(random.nextInt(gp.skins.size()));
                double obSpeed = 2 + random.nextInt(5);
                traffic.add(new Vehicle(spawnX, -100, trafficSkin, obSpeed, gp.assetManager.getObstacleImg()));
            }
        }

        // 3. Spawn Koin di lajur secara acak
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

        // 4. Perbarui lalu lintas rintangan
        for (int i = traffic.size() - 1; i >= 0; i--) {
            Vehicle ob = traffic.get(i);
            // Gerakan ke bawah relatif terhadap kecepatan jalan raya pemain
            ob.y += ob.speed + (gp.roadSpeed - 5) * 0.5;

            // Deteksi tabrakan dengan mobil pemain
            if (ob.getBounds().intersects(gp.player.getBounds())) {
                triggerExplosion(gp.player.x + gp.player.width / 2.0, gp.player.y + gp.player.height / 2.0);
                AudioSynth.playCrash();
                gp.screenShakeAmount = 15; // getaran kamera singkat

                // Update data skor dan koin
                gp.playerCoinsTotal += gp.coinsCollectedThisRun;
                if (gp.distanceScore > gp.highScore) {
                    gp.highScore = gp.distanceScore;
                }
                gp.saveGameData();

                // Ubah state ke GAME_OVER
                gp.currentState = GamePanel.GameState.GAME_OVER;
                return;
            }

            // Hapus rintangan jika keluar layar bawah
            if (ob.y > gp.getHeight()) {
                traffic.remove(i);
            }
        }

        // 5. Perbarui pergerakan koin
        for (int i = coins.size() - 1; i >= 0; i--) {
            Coin c = coins.get(i);
            c.y += gp.roadSpeed;
            c.update();

            // Deteksi pengambilan koin oleh pemain
            if (c.getBounds().intersects(gp.player.getBounds())) {
                gp.coinsCollectedThisRun++;
                AudioSynth.playCoin();
                triggerCollectSpark(c.x + c.size / 2.0, c.y + c.size / 2.0);
                coins.remove(i);
                continue;
            }

            // Hapus koin jika keluar layar bawah
            if (c.y > gp.getHeight()) {
                coins.remove(i);
            }
        }

        // 6. Perbarui efek partikel
        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle p = particles.get(i);
            if (!p.update()) {
                particles.remove(i);
            }
        }
    }

    /**
     * Memunculkan asap neon dari roda belakang pemain saat melakukan belokan/drift.
     */
    private void spawnDriftSmoke() {
        if (random.nextInt(10) > 3) return;
        particles.add(new Particle(
                gp.player.x + 8 + random.nextInt(gp.player.width - 16),
                gp.player.y + gp.player.height - 5,
                (random.nextDouble() - 0.5) * 2,
                2 + random.nextDouble() * 3,
                new Color(255, 0, 127, 180),
                4,
                25
        ));
    }

    /**
     * Memicu efek partikel ledakan saat mobil pemain mengalami tabrakan.
     */
    public void triggerExplosion(double x, double y) {
        for (int i = 0; i < 40; i++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            double speed = 2 + random.nextDouble() * 10;
            particles.add(new Particle(
                    x, y,
                    Math.cos(angle) * speed,
                    Math.sin(angle) * speed,
                    random.nextBoolean() ? gp.player.skin.primaryColor : Color.YELLOW,
                    6 + random.nextInt(6),
                    50 + random.nextInt(30)
            ));
        }
    }

    /**
     * Memicu efek percikan emas saat pemain berhasil mengoleksi koin.
     */
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

    /**
     * Merender semua entitas dinamis (koin, traffic, partikel) ke layar.
     */
    public void drawEntities(Graphics2D g) {
        for (Coin c : coins) {
            c.draw(g);
        }

        for (Vehicle ob : traffic) {
            ob.draw(g);
        }

        for (Particle p : particles) {
            p.draw(g);
        }
    }

    // public ArrayList<Vehicle> getTraffic() {
    //     return traffic;
    // }

    // public ArrayList<Coin> getCoins() {
    //     return coins;
    // }

    // public ArrayList<Particle> getParticles() {
    //     return particles;
    // }
}
