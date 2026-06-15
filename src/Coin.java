// Library bawaan dari Java
import java.awt.*;
import java.awt.geom.*;

/**
 * [Class Buatan Sendiri]
 * Item kolektif peningkat skor yang muncul di jalur jalan raya (coin/shard).
 */
public class Coin {
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
