import javax.sound.sampled.*;
import java.io.File;
import java.util.Random;

/**
 * Synthesizer engine to generate 8-bit retro sounds procedurally on-the-fly.
 * Generates sounds mathematically to eliminate any external file requirement.
 */
public class AudioSynth {
    private static boolean soundEnabled = true;
    private static Clip coinClip;
    private static Clip selectClip;

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

    public static void playCoin() {
        if (!soundEnabled) return;
        try {
            java.io.File audioFile = new java.io.File("assets/coin.wav");
            if (!audioFile.exists()) audioFile = new java.io.File("assets/coin.mp3");
            if (audioFile.exists()) {
                AudioInputStream in = AudioSystem.getAudioInputStream(audioFile);
                AudioFormat baseFormat = in.getFormat();
                AudioFormat decodedFormat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
                        baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
                AudioInputStream din = AudioSystem.getAudioInputStream(decodedFormat, in);
                if (coinClip != null && coinClip.isOpen()) coinClip.close();
                coinClip = AudioSystem.getClip();
                coinClip.open(din);
                coinClip.setFramePosition(0);
                coinClip.start();
                return;
            }
        } catch (Exception e) {
            System.err.println("[AudioSynth] Failed to play external coin sound: " + e.getMessage());
        }
        // Fallback: generate simple procedural coin sound (same as original)
        new Thread(() -> {
            try {
                // Quick alternating frequencies for classical arcade "ding"
                byte[] buffer = new byte[3500];
                for (int i = 0; i < buffer.length; i++) {
                    // Berpindah nada dari 950Hz ke 1300Hz di tengah-tengah pemutaran
                    double freq = (i < 1200) ? 950.0 : 1300.0;
                    
                    // Efek amplitudo memudar perlahan (decay)
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
        // Try loading touch sound from assets (wav first, then mp3)
        java.io.File audioFile = new java.io.File("assets/touch.wav");
        if (!audioFile.exists()) {
            audioFile = new java.io.File("assets/touch.mp3");
        }
        if (audioFile.exists()) {
            try {
                AudioInputStream in = AudioSystem.getAudioInputStream(audioFile);
                AudioFormat baseFormat = in.getFormat();
                AudioFormat decodedFormat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
                        baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
                AudioInputStream din = AudioSystem.getAudioInputStream(decodedFormat, in);
                if (selectClip != null && selectClip.isOpen()) selectClip.close();
                selectClip = AudioSystem.getClip();
                selectClip.open(din);
                selectClip.setFramePosition(0);
                selectClip.start();
                return;
            } catch (Exception e) {
                System.err.println("[AudioSynth] Failed to play external select sound: " + e.getMessage());
            }
        }
        // Fallback: generate procedural select sound (same as original implementation)
        new Thread(() -> {
            try {
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
        AudioFormat format = new AudioFormat(8000f, 8, 1, true, false);
        SourceDataLine line = AudioSystem.getSourceDataLine(format);
        line.open(format);
        line.start();
        line.write(buffer, 0, buffer.length);
        line.drain();
        line.close();
    }
}
