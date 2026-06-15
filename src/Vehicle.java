import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

/**
 * Kelas Vehicle merepresentasikan mobil fisik di dalam permainan.
 * Ini digunakan baik untuk kendaraan pemain maupun mobil rintangan (traffic) lainnya.
 * 
 * Kelas ini menangani koordinat posisi, ukuran, kecepatan, jenis skin yang digunakan,
 * rotasi drift, serta penggambaran visual kendaraan.
 */
public class Vehicle {
    // Koordinat X dan Y posisi mobil di layar
    public double x, y;
    
    // Dimensi default mobil
    public int width = 50;
    public int height = 90;
    
    // Kecepatan mobil
    public double speed;
    
    // Konfigurasi kosmetik skin mobil
    public Skin skin;
    
    // Gambar aset visual mobil
    public BufferedImage img;
    
    // Sudut rotasi (drift) mobil dalam derajat
    public double driftAngle = 0.0;

    /**
     * Konstruktor Vehicle dasar.
     * @param x Koordinat awal X
     * @param y Koordinat awal Y
     * @param skin Objek konfigurasi skin mobil
     * @param speed Kecepatan gerak
     */
    public Vehicle(double x, double y, Skin skin, double speed) {
        this.x = x;
        this.y = y;
        this.skin = skin;
        this.speed = speed;
    }

    /**
     * Konstruktor Vehicle lengkap dengan aset gambar.
     * @param x Koordinat awal X
     * @param y Koordinat awal Y
     * @param skin Objek konfigurasi skin mobil
     * @param speed Kecepatan gerak
     * @param img Aset gambar kendaraan
     */
    public Vehicle(double x, double y, Skin skin, double speed, BufferedImage img) {
        this.x = x;
        this.y = y;
        this.skin = skin;
        this.speed = speed;
        this.img = img;
    }

    /**
     * Mengembalikan batas tabrakan (hitbox) mobil.
     * Hitbox diperkecil sedikit di setiap sisi (4 piksel) agar permainan terasa
     * lebih toleran dan adah terhadap pemain (forgiving gameplay feeling).
     * @return Bounding box mobil berbentuk Rectangle2D
     */
    public Rectangle2D.Double getBounds() {
        return new Rectangle2D.Double(x + 4, y + 4, width - 8, height - 8);
    }

    /**
     * Menggambar mobil ke layar grafis dengan efek rotasi drift jika ada.
     * @param g Objek Graphics2D
     */
    public void draw(Graphics2D g) {
        // Aktifkan anti-aliasing agar rendering gambar/garis mulus
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Simpan transformasi grafis sebelum diputar
        AffineTransform old = g.getTransform();
        
        // Pindahkan sumbu koordinat ke tengah mobil untuk rotasi lokal
        g.translate(x + width / 2.0, y + height / 2.0);
        g.rotate(Math.toRadians(driftAngle));
        
        // Gambar mobil
        if (img != null) {
            g.drawImage(img, -width / 2, -height / 2, width, height, null);
        } else {
            // Gambar kotak rounded abu-abu sebagai pengganti jika aset gambar null
            g.setColor(Color.DARK_GRAY);
            g.fillRoundRect(-width / 2, -height / 2, width, height, 10, 10);
        }
        
        // Kembalikan transformasi grafis ke keadaan semula
        g.setTransform(old);
    }
}
