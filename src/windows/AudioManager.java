package windows;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

public class AudioManager {
    // This object controls the audio play, stop, and close operations
    private static Clip bgmClip;

    public static void playSound(String soundPath) {
        // If a sound is already playing, stop and close it first to prevent overlapping
        if (bgmClip != null && bgmClip.isRunning()) {
            bgmClip.stop();
            bgmClip.close();
        }
        try {
            // Get the audio file from the windows.resources package
            URL url = AudioManager.class.getResource(soundPath);
            if (url == null) {
                System.out.println("Error: Cannot find audio file at: " + soundPath);
                return;
            }
            
            // Read the audio file into the system stream
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            // Create a new clip object for playing
            bgmClip = AudioSystem.getClip();
            // Open the audio stream and start playing
            bgmClip.open(audioIn);
            bgmClip.start(); 
        } catch (Exception e) {
            System.out.println("Error: Failed to play audio!");
            e.printStackTrace();
        }
    }
}