import javax.sound.sampled.*;
import java.util.Random;

/**
 * Kelas AudioSynth bertindak sebagai mesin pensintesis suara (audio synthesizer) prosedural.
 * Berfungsi menghasilkan efek suara arcade 8-bit klasik secara langsung (real-time) melalui
 * perhitungan matematis (misalnya gelombang sinus, decay amplitude, dan white noise).
 * 
 * Keuntungannya adalah game dapat memutar efek suara dinamis tanpa memerlukan file audio eksternal (.wav/.mp3),
 * sehingga ukuran installer game tetap sangat kecil.
 */
public class AudioSynth {
    // Flag status apakah fitur suara diaktifkan atau tidak
    private static boolean soundEnabled = true;

    /**
     * Mengubah status aktif/nonaktif suara.
     * @param enabled Status suara baru
     */
    public static void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
    }

    /**
     * Memeriksa apakah suara sedang aktif.
     * @return True jika aktif, false jika dinonaktifkan
     */
    public static boolean isSoundEnabled() {
        return soundEnabled;
    }

    /**
     * Sintesis suara "Coin Pick-up" (Suara koin diambil).
     * Menghasilkan dua frekuensi nada tinggi secara berurutan cepat (ding klasik arcade).
     */
    public static void playCoin() {
        if (!soundEnabled) return;
        new Thread(() -> {
            try {
                // Buffer audio berukuran 3500 byte untuk durasi pendek
                byte[] buffer = new byte[3500];
                for (int i = 0; i < buffer.length; i++) {
                    // Berpindah nada dari 950Hz ke 1300Hz di tengah-tengah pemutaran
                    double freq = (i < 1200) ? 950.0 : 1300.0;
                    
                    // Efek amplitudo memudar perlahan (decay)
                    double decay = 1.0 - ((double) i / buffer.length);
                    
                    // Sintesis gelombang sinus
                    buffer[i] = (byte) (Math.sin(2 * Math.PI * freq * i / 8000.0) * 127 * decay);
                }
                playBuffer(buffer);
            } catch (Exception ignored) {}
        }).start();
    }

    /**
     * Sintesis suara "Crash Explosion" (Suara tabrakan).
     * Menggunakan frekuensi rendah yang menurun cepat (sweep down) dicampur dengan derau putih (white noise)
     * untuk menciptakan efek ledakan yang hancur berkeping-keping.
     */
    public static void playCrash() {
        if (!soundEnabled) return;
        new Thread(() -> {
            try {
                // Buffer berukuran lebih besar (7500 byte) untuk ledakan yang lebih panjang
                byte[] buffer = new byte[7500];
                Random rand = new Random();
                for (int i = 0; i < buffer.length; i++) {
                    double progress = (double) i / buffer.length;
                    double decay = 1.0 - progress;
                    
                    // Menurunkan frekuensi dasar secara eksponensial (sweep down dari ~200Hz ke 0Hz)
                    double freqSweep = 200.0 * Math.exp(-4.0 * progress);
                    double rawWave = Math.sin(2 * Math.PI * freqSweep * i / 8000.0);
                    
                    // Menggenerasikan white noise acak (-1.0 hingga 1.0)
                    double whiteNoise = rand.nextFloat() * 2.0 - 1.0;
                    
                    // Mencampur gelombang sinus murni (30%) dengan white noise (70%) untuk suara serak/ledakan
                    buffer[i] = (byte) (((rawWave * 0.3) + (whiteNoise * 0.7)) * 127 * decay);
                }
                playBuffer(buffer);
            } catch (Exception ignored) {}
        }).start();
    }

    /**
     * Sintesis suara "Select / Click UI" (Suara navigasi menu).
     * Menghasilkan nada sapuan turun lembut (laser blip) yang singkat.
     */
    public static void playSelect() {
        if (!soundEnabled) return;
        new Thread(() -> {
            try {
                byte[] buffer = new byte[1200];
                for (int i = 0; i < buffer.length; i++) {
                    double progress = (double) i / buffer.length;
                    
                    // Sapuan nada menurun dari 600Hz ke 200Hz
                    double freq = 600.0 - (progress * 400.0);
                    double decay = 1.0 - progress;
                    
                    buffer[i] = (byte) (Math.sin(2 * Math.PI * freq * i / 8000.0) * 127 * decay);
                }
                playBuffer(buffer);
            } catch (Exception ignored) {}
        }).start();
    }

    /**
     * Memutar buffer byte gelombang suara menggunakan java.sound.sampled.
     * Menggunakan format audio standar: 8000Hz sample rate, 8-bit, Mono, Signed, Little-Endian.
     * @param buffer Array byte berisi gelombang suara sintesis
     */
    public static void playBuffer(byte[] buffer) throws Exception {
        AudioFormat format = new AudioFormat(8000f, 8, 1, true, false);
        SourceDataLine line = AudioSystem.getSourceDataLine(format);
        line.open(format);
        line.start();
        line.write(buffer, 0, buffer.length);
        line.drain();
        line.close();
    }
}
