import java.awt.Color;

/**
 * Kelas Skin adalah struktur data sederhana (model) yang mendefinisikan kosmetik mobil.
 * 
 * Objek Skin berisi informasi nama kosmetik, biaya koin untuk membeli (cost),
 * palet warna visual (warna primer, warna sekunder, warna glow neon), serta
 * deskripsi singkat dari skin tersebut untuk ditampilkan di Toko Kosmetik.
 */
public class Skin {
    // Nama dari skin kosmetik kendaraan
    public String name;
    
    // Harga skin dalam koin untuk melakukan pembelian di toko
    public int cost;
    
    // Warna dasar/primer utama kendaraan
    public Color primaryColor;
    
    // Warna sekunder/pendukung kendaraan
    public Color secondaryColor;
    
    // Warna pendaran cahaya neon (glow) yang memancar dari bawah mobil
    public Color glowColor;
    
    // Penjelasan deskripsi singkat mengenai skin kendaraan
    public String desc;

    /**
     * Konstruktor Skin.
     * @param name Nama skin
     * @param cost Harga koin
     * @param pCol Warna primer
     * @param sCol Warna sekunder
     * @param gCol Warna glow neon
     * @param desc Deskripsi skin
     */
    public Skin(String name, int cost, Color pCol, Color sCol, Color gCol, String desc) {
        this.name = name;
        this.cost = cost;
        this.primaryColor = pCol;
        this.secondaryColor = sCol;
        this.glowColor = gCol;
        this.desc = desc;
    }
}
