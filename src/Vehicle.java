import java.awt.*;
import java.awt.geom.*;

/**
 * Represents physical cars (player and obstacles).
 */
public class Vehicle {
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
