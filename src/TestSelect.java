public class TestSelect {
    public static void main(String[] args) {
        AudioSynth.setSoundEnabled(true);
        AudioSynth.playSelect();
        // Wait a short time to let sound play
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
    }
}
