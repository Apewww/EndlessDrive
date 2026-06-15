// Library bawaan dari Java
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * [Class Buatan Sendiri]
 * Partikel simulasi untuk efek ledakan neon dan asap drift.
 */
public class Particle {
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
