import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class MusicPlayer {
    private static Clip clip;
    private static boolean isMuted = false;
    private static String currentTrack = null;

    /**
     * Plays background music from the specified file path
     * @param filePath Path to the audio file
     */
    public static void playBackgroundMusic(String filePath) {
        // If same track is already playing, do nothing
        if (filePath.equals(currentTrack) && clip != null && clip.isRunning()) {
            return;
        }

        stopMusic(); // Stop any currently playing music

        try {
            File soundFile = new File(filePath);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            
            // Set up continuous looping
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            
            // Start playing if not muted
            if (!isMuted) {
                clip.start();
            }
            
            currentTrack = filePath;
            
        } catch (UnsupportedAudioFileException e) {
            System.err.println("Unsupported audio format: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error reading audio file: " + e.getMessage());
        } catch (LineUnavailableException e) {
            System.err.println("Audio line unavailable: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }
    }

    /**
     * Stops the currently playing music and releases resources
     */
    public static void stopMusic() {
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
        }
        currentTrack = null;
    }

    /**
     * Toggles sound on/off
     */
    public static void toggleSound() {
        isMuted = !isMuted;
        
        if (clip != null) {
            if (isMuted) {
                clip.stop();
            } else {
                clip.start();
            }
        }
    }

    /**
     * Checks if music is currently muted
     * @return true if muted, false otherwise
     */
    public static boolean isMuted() {
        return isMuted;
    }

    /**
     * Checks if music is currently playing
     * @return true if music is playing, false otherwise
     */
    public static boolean isPlaying() {
        return clip != null && clip.isRunning();
    }

    /**
     * Gets the currently playing track path
     * @return Path to current track or null if none is playing
     */
    public static String getCurrentTrack() {
        return currentTrack;
    }

    /**
     * Sets the volume (0.0 to 1.0)
     * @param volume Desired volume level
     */
    public static void setVolume(float volume) {
        if (clip != null) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        }
    }
}