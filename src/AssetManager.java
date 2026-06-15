import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Kelas AssetManager bertanggung jawab untuk memuat dan menyediakan semua aset gambar
 * yang dibutuhkan dalam game. Hal ini memisahkan logika pemuatan file gambar dari logic gameplay.
 * 
 * Jika berkas gambar gagal dimuat (misalnya file hilang), sistem akan menggunakan
 * placeholder transparan berukuran 1x1 piksel agar program tidak crash saat merender.
 */
public class AssetManager {
    private BufferedImage playerImg;
    private BufferedImage shopCarImg;
    private BufferedImage neonPulseImg;
    private BufferedImage desertNomadImg;
    private BufferedImage obstacleImg;
    private BufferedImage bgImg;
    private BufferedImage menuBgImg;
    private BufferedImage roadImg;
    private BufferedImage placeholderImg;

    /**
     * Konstruktor AssetManager.
     * Menginisialisasi placeholder transparan dan langsung memicu pemuatan aset.
     */
    public AssetManager() {
        placeholderImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        loadAllAssets();
    }

    /**
     * Memuat seluruh aset gambar dari folder 'assets/'.
     */
    private void loadAllAssets() {
        playerImg = loadImage("assets/RoyalPhoenix.png");
        shopCarImg = loadImage("assets/Cars.png");
        neonPulseImg = loadImage("assets/NeonPulse.png");
        desertNomadImg = loadImage("assets/DesertNomad.png");
        obstacleImg = loadImage("assets/Cars2.png");
        bgImg = loadImage("assets/Bg.png");
        menuBgImg = loadImage("assets/Home.png");
        roadImg = loadImage("assets/jln.png");
    }

    /**
     * Metode internal untuk memuat berkas gambar tunggal dengan penanganan IOException.
     * @param path Lokasi berkas gambar
     * @return BufferedImage gambar yang dimuat, atau placeholder jika gagal
     */
    private BufferedImage loadImage(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                return ImageIO.read(file);
            } else {
                System.err.println("Peringatan: Berkas gambar tidak ditemukan di '" + path + "'. Menggunakan placeholder.");
            }
        } catch (IOException e) {
            System.err.println("Gagal memuat gambar: " + path);
            e.printStackTrace();
        }
        return placeholderImg;
    }

    public BufferedImage getPlayerImg() {
        return playerImg != null ? playerImg : placeholderImg;
    }

    public BufferedImage getShopCarImg() {
        return shopCarImg != null ? shopCarImg : placeholderImg;
    }

    public BufferedImage getNeonPulseImg() {
        return neonPulseImg != null ? neonPulseImg : placeholderImg;
    }

    public BufferedImage getDesertNomadImg() {
        return desertNomadImg != null ? desertNomadImg : placeholderImg;
    }

    public BufferedImage getObstacleImg() {
        return obstacleImg != null ? obstacleImg : placeholderImg;
    }

    public BufferedImage getBgImg() {
        return bgImg != null ? bgImg : placeholderImg;
    }

    public BufferedImage getMenuBgImg() {
        return menuBgImg != null ? menuBgImg : placeholderImg;
    }

    public BufferedImage getRoadImg() {
        return roadImg != null ? roadImg : placeholderImg;
    }

    public BufferedImage getPlaceholderImg() {
        return placeholderImg;
    }
}
