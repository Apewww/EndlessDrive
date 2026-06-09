import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

/**
 * Represents physical cars (player and obstacles).
 */
public class Vehicle {
    public double x, y;
    public int width = 50;
    public int height = 90;
    public double speed;
    public Skin skin;
    public BufferedImage img;
    public double driftAngle = 0.0;

    public Vehicle(double x, double y, Skin skin, double speed) {
        this.x = x;
        this.y = y;
        this.skin = skin;
        this.speed = speed;
    }

    // New constructor accepting an image for rendering
    public Vehicle(double x, double y, Skin skin, double speed, BufferedImage img) {
        this.x = x;
        this.y = y;
        this.skin = skin;
        this.speed = speed;
        this.img = img;
    }

    public Rectangle2D.Double getBounds() {
        // Shrink hitbox slightly for better, player-forgiving gameplay feeling
        return new Rectangle2D.Double(x + 4, y + 4, width - 8, height - 8);
    }

    public void draw(Graphics2D g) {
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    AffineTransform old = g.getTransform();
    g.translate(x + width / 2.0, y + height / 2.0);
    g.rotate(Math.toRadians(driftAngle));
    if (img != null) {
        g.drawImage(img, -width / 2, -height / 2, width, height, null);
    } else {
        // Simple placeholder rectangle
        g.setColor(Color.DARK_GRAY);
        g.fillRoundRect(-width / 2, -height / 2, width, height, 10, 10);
    }
    g.setTransform(old);
}
}
