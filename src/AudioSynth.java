import javax.sound.sampled.*;
import java.io.File;
import java.util.Random;

/**
 * Synthesizer engine to generate 8‑bit retro sounds procedurally on‑the‑fly.
 * For the crash sound we now use an external audio file (assets/crash.mpeg).
 */
public class AudioSynth {
    private static Clip crashClip;
    private static Clip coinClip;
    private static Clip selectClip;
    private static boolean soundEnabled = true;

    public static void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
    }

    public static boolean isSoundEnabled() {
        return soundEnabled;
    }

    /** Play the coin collection sound using an external audio file. */
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
                byte[] buffer = new byte[3500];
                for (int i = 0; i < buffer.length; i++) {
                    double freq = (i < 1200) ? 950.0 : 1300.0;
                    double decay = 1.0 - ((double) i / buffer.length);
                    buffer[i] = (byte) (Math.sin(2 * Math.PI * freq * i / 8000.0) * 127 * decay);
                }
                playBuffer(buffer);
            } catch (Exception ignored) {}
        }).start();

    }

    /** Play the crash sound using an external audio file. */
    public static void playCrash() {
        if (!soundEnabled) return; // play full audio file duration
        try {
            java.io.File audioFile = new java.io.File("assets/crash.wav");
            if (!audioFile.exists()) audioFile = new java.io.File("assets/crash.mpeg");
            if (audioFile.exists()) {
                AudioInputStream in = AudioSystem.getAudioInputStream(audioFile);
                AudioFormat baseFormat = in.getFormat();
                AudioFormat decodedFormat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        baseFormat.getSampleRate(), 16, baseFormat.getChannels(),
                        baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
                AudioInputStream din = AudioSystem.getAudioInputStream(decodedFormat, in);
                if (crashClip != null && crashClip.isOpen()) crashClip.close();
                crashClip = AudioSystem.getClip();
                crashClip.open(din);
                // Ensure playback starts from the beginning and plays the full file duration
                crashClip.setFramePosition(0);
                crashClip.start();
                return;
            }
        } catch (Exception e) {
            System.err.println("[AudioSynth] Failed to play external crash sound, falling back to generated noise: " + e.getMessage());
        }
        // Fallback: generate simple white‑noise burst for crash sound
        byte[] buffer = new byte[2000];
        java.util.Random rand = new java.util.Random();
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = (byte) (rand.nextInt(256) - 128);
        }
        try {
            playBuffer(buffer);
        } catch (Exception ignored) {}

    }



        /** Play UI selection sound (external audio or procedural fallback). */
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
                    double freq = 600.0 - (progress * 400.0);
                    double decay = 1.0 - progress;
                    buffer[i] = (byte) (Math.sin(2 * Math.PI * freq * i / 8000.0) * 127 * decay);
                }
                playBuffer(buffer);
            } catch (Exception ignored) {}
        }).start();
    }

    /** Helper to play a raw PCM buffer. */
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
