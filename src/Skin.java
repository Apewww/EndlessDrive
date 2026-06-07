import java.awt.Color;

/**
 * Skin model structure containing color styling, names, and purchase data.
 */
public class Skin {
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
