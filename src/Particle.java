import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Kelas Particle merepresentasikan elemen partikel kecil individual yang digunakan
 * untuk mensimulasikan efek visual dinamis dalam game, seperti asap roda (drift)
 * dan percikan ledakan saat tabrakan terjadi.
 * 
 * Partikel memiliki posisi, kecepatan gerak (vektor arah), warna, ukuran,
 * serta masa hidup (life cycle) yang akan berkurang di setiap frame.
 */
public class Particle {
    // Koordinat posisi X dan Y partikel
    public double x, y;
    
    // Vektor kecepatan X (horizontal) dan Y (vertical)
    public double vx, vy;
    
    // Warna partikel
    public Color color;
    
    // Ukuran partikel (lebar & tinggi persegi dalam piksel)
    public int size;
    
    // Masa hidup partikel yang tersisa saat ini (frame)
    public int life;
    
    // Durasi maksimal masa hidup partikel saat pertama kali dibuat (frame)
    public int maxLife;

    /**
     * Konstruktor Particle.
     * @param x Koordinat awal X
     * @param y Koordinat awal Y
     * @param vx Kecepatan horizontal awal
     * @param vy Kecepatan vertikal awal
     * @param color Warna partikel
     * @param size Ukuran persegi partikel
     * @param maxLife Masa hidup partikel dalam frame
     */
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

    /**
     * Memperbarui posisi partikel berdasarkan vektor kecepatannya,
     * serta mengurangi sisa masa hidup partikel.
     * @return True jika partikel masih hidup (life > 0), false jika partikel harus dihancurkan
     */
    public boolean update() {
        x += vx;
        y += vy;
        life--;
        return life > 0;
    }

    /**
     * Menggambar partikel ke layar dengan opasitas (alpha) yang memudar
     * seiring berkurangnya masa hidup partikel.
     * @param g Objek Graphics2D
     */
    public void draw(Graphics2D g) {
        // Hitung nilai alpha (transparansi) secara linier berdasarkan sisa masa hidup
        float alpha = (float) life / maxLife;
        if (alpha < 0.0f) alpha = 0.0f;
        if (alpha > 1.0f) alpha = 1.0f;

        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (alpha * 255)));
        g.fillRect((int) x, (int) y, size, size);
    }
}
