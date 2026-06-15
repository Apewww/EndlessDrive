import java.awt.*;
import java.awt.geom.*;

/**
 * Kelas Coin merepresentasikan item shards/koin yang muncul di jalan raya
 * untuk dikoleksi oleh pemain guna meningkatkan skor dan menambah saldo uang di toko.
 * 
 * Kelas ini mengatur posisi koin, efek animasi berputar (rotasi),
 * deteksi hitbox koin, serta rendering visual koin dengan pendaran cahaya neon.
 */
public class Coin {
    // Koordinat X dan Y posisi koin di layar
    public double x, y;
    
    // Diameter ukuran koin (piksel)
    public int size = 26;
    
    // Sudut rotasi animasi koin saat berputar
    private double rotation = 0.0;

    /**
     * Konstruktor Coin.
     * @param x Koordinat awal X
     * @param y Koordinat awal Y
     */
    public Coin(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Mengembalikan batas tabrakan (hitbox) koin.
     * @return Bounding box koin berbentuk Rectangle2D
     */
    public Rectangle2D.Double getBounds() {
        return new Rectangle2D.Double(x, y, size, size);
    }

    /**
     * Memperbarui sudut rotasi koin untuk efek animasi berputar konstan.
     */
    public void update() {
        rotation += 4.5;
    }

    /**
     * Menggambar koin bercahaya neon ke layar.
     * @param g Objek Graphics2D
     */
    public void draw(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Render pendaran (glow) neon emas transparan di belakang koin
        g.setColor(new Color(255, 215, 0, 80));
        g.fillOval((int) x - 3, (int) y - 3, size + 6, size + 6);

        // Simpan transformasi sebelum melakukan rotasi koin
        AffineTransform old = g.getTransform();
        g.translate(x + size / 2.0, y + size / 2.0);
        g.rotate(Math.toRadians(rotation));

        // Gambar lingkaran utama koin berwarna kuning tua
        g.setColor(new Color(255, 200, 10));
        g.fillOval(-size / 2, -size / 2, size, size);

        // Gambar garis luar lingkaran koin berwarna kuning cerah
        g.setColor(new Color(255, 255, 120));
        g.setStroke(new BasicStroke(2));
        g.drawOval(-size / 2, -size / 2, size, size);

        // Gambar simbol mata uang '$' di tengah koin
        g.setFont(new Font("Segoe UI", Font.BOLD, 14));
        FontMetrics fm = g.getFontMetrics();
        g.drawString("$", -fm.stringWidth("$") / 2, fm.getAscent() - fm.getHeight() / 2 - 1);

        // Kembalikan transformasi grafis
        g.setTransform(old);
    }
}
