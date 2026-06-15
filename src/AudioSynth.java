// Library bawaan dari Java
import javax.sound.sampled.*;
import java.util.Random;

/**
 * [Class Buatan Sendiri]
 * Mesin synthesizer untuk menghasilkan suara retro 8-bit secara prosedural secara langsung.
 * Menghasilkan suara secara matematis untuk menghilangkan kebutuhan file eksternal.
 */
public class AudioSynth {
    private static boolean soundEnabled = true;

    public static void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
    }

    public static boolean isSoundEnabled() {
        return soundEnabled;
    }

    public static void playCoin() {
        if (!soundEnabled) return;
        new Thread(() -> {
            try {
                // Quick alternating frequencies for classical arcade "ding"
                byte[] buffer = new byte[3500];
                for (int i = 0; i < buffer.length; i++) {
                    double freq = (i < 1200) ? 950.0 : 1300.0;
                    double decay = 1.0 - ((double) i / buffer.length);
                    // Using sine wave
                    buffer[i] = (byte) (Math.sin(2 * Math.PI * freq * i / 8000.0) * 127 * decay);
                }
                playBuffer(buffer);
            } catch (Exception ignored) {}
        }).start();
    }

    public static void playCrash() {
        if (!soundEnabled) return;
        new Thread(() -> {
            try {
                // Sweep frequency from low to high with white noise for heavy explosion sound
                byte[] buffer = new byte[7500];
                Random rand = new Random();
                for (int i = 0; i < buffer.length; i++) {
                    double progress = (double) i / buffer.length;
                    double decay = 1.0 - progress;
                    double freqSweep = 200.0 * Math.exp(-4.0 * progress);
                    double rawWave = Math.sin(2 * Math.PI * freqSweep * i / 8000.0);
                    double whiteNoise = rand.nextFloat() * 2.0 - 1.0;
                    // Mix noise with sweep for crunchiness
                    buffer[i] = (byte) (((rawWave * 0.3) + (whiteNoise * 0.7)) * 127 * decay);
                }
                playBuffer(buffer);
            } catch (Exception ignored) {}
        }).start();
    }

    public static void playSelect() {
        if (!soundEnabled) return;
        new Thread(() -> {
            try {
                // Soft laser blip for UI selection
                byte[] buffer = new byte[1200];
                for (int i = 0; i < buffer.length; i++) {
                    double progress = (double) i / buffer.length;
                    double freq = 600.0 - (progress * 400.0); // Sweep down
                    double decay = 1.0 - progress;
                    buffer[i] = (byte) (Math.sin(2 * Math.PI * freq * i / 8000.0) * 127 * decay);
                }
                playBuffer(buffer);
            } catch (Exception ignored) {}
        }).start();
    }

    public static void playBuffer(byte[] buffer) throws Exception {
        // Standard 8000Hz, 8-bit, Mono, Signed, 1 byte/frame configuration
        AudioFormat format = new AudioFormat(8000f, 8, 1, true, false);
        SourceDataLine line = AudioSystem.getSourceDataLine(format);
        line.open(format);
        line.start();
        line.write(buffer, 0, buffer.length);
        line.drain();
        line.close();
    }
}
