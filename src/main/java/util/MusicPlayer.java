package util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * Utility class for managing background music in the Yu-Gi-Oh! game.
 * <p>
 * This class provides a simple static interface for playing, pausing, stopping,
 * and controlling background music using JavaFX's MediaPlayer. It ensures that
 * only one music track plays at a time and handles smooth transitions between
 * different music files.
 * </p>
 * <p>
 * The music player supports looping tracks indefinitely, volume control, and
 * pause/resume functionality. Music files are loaded from the classpath as
 * resources, making them easily bundled with the application.
 * </p>
 * <p>
 * <strong>Supported Audio Formats:</strong>
 * JavaFX MediaPlayer supports various audio formats including MP3, WAV, and AIFF.
 * Ensure your music files are in a compatible format.
 * </p>
 * <p>
 * <strong>Example Usage:</strong>
 * <pre>
 * // Play background music during a duel
 * MusicPlayer.playMusic("/music/duel_theme.mp3");
 * 
 * // Adjust volume to 50%
 * MusicPlayer.setVolume(0.5);
 * 
 * // Pause during a menu
 * MusicPlayer.pauseMusic();
 * 
 * // Resume after returning to gameplay
 * MusicPlayer.resumeMusic();
 * 
 * // Stop music completely
 * MusicPlayer.stopMusic();
 * </pre>
 * </p>
 *
 * @author Your Name
 * @version 1.0
 * @since 2025
 */
public class MusicPlayer {
    
    /** The currently active MediaPlayer instance, or null if no music is playing */
    private static MediaPlayer currentPlayer;
    
    /**
     * Plays a music file in an infinite loop.
     * <p>
     * If music is already playing, it will be stopped before the new track starts.
     * The music file is loaded from the classpath as a resource, so the path should
     * be relative to the resources directory (e.g., "/music/battle_theme.mp3").
     * </p>
     * <p>
     * The music is set to loop indefinitely and plays at 30% volume by default
     * to avoid overwhelming other game sounds. Use {@link #setVolume(double)} to
     * adjust the volume if needed.
     * </p>
     * <p>
     * If the music file cannot be loaded (wrong path, unsupported format, or missing
     * file), an error message is printed to the console and no music plays.
     * </p>
     *
     * @param musicFile the path to the music file relative to the classpath
     *                  (e.g., "/music/duel_theme.mp3")
     */
    public static void playMusic(String musicFile) {
        try {
            // Stop any currently playing music
            stopMusic();
            
            // Load the new music file
            Media media = new Media(
                MusicPlayer.class.getResource(musicFile).toExternalForm()
            );
            
            currentPlayer = new MediaPlayer(media);
            currentPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Infinite loop
            currentPlayer.setVolume(0.3); // Volume at 30%
            currentPlayer.play();
            
            System.out.println("Music started: " + musicFile);
            
        } catch (Exception e) {
            System.err.println("Error loading music: " + e.getMessage());
        }
    }
    
    /**
     * Stops the currently playing music and releases resources.
     * <p>
     * This method completely stops playback and disposes of the current MediaPlayer.
     * After calling this method, the music cannot be resumed; you must call
     * {@link #playMusic(String)} again to start music playback.
     * </p>
     * <p>
     * If no music is currently playing, this method does nothing.
     * </p>
     */
    public static void stopMusic() {
        if (currentPlayer != null) {
            currentPlayer.stop();
            currentPlayer = null;
        }
    }
    
    /**
     * Pauses the currently playing music.
     * <p>
     * The music can be resumed from the same position using {@link #resumeMusic()}.
     * This is useful for temporarily silencing music during menus, dialogs, or
     * cutscenes without losing the current playback position.
     * </p>
     * <p>
     * If no music is currently playing, this method does nothing.
     * </p>
     */
    public static void pauseMusic() {
        if (currentPlayer != null) {
            currentPlayer.pause();
        }
    }
    
    /**
     * Resumes playback of paused music.
     * <p>
     * This method continues playing from where the music was paused. If the music
     * was stopped (not just paused), this method will have no effect and you must
     * call {@link #playMusic(String)} to start music again.
     * </p>
     * <p>
     * If no music is currently loaded or if music is already playing, this method
     * does nothing.
     * </p>
     */
    public static void resumeMusic() {
        if (currentPlayer != null) {
            currentPlayer.play();
        }
    }
    
    /**
     * Changes the volume of the currently playing music.
     * <p>
     * The volume is specified as a value between 0.0 (silent) and 1.0 (maximum volume).
     * Values outside this range will be clamped by the MediaPlayer. A volume of 0.5
     * represents 50% volume.
     * </p>
     * <p>
     * The volume change takes effect immediately. If no music is currently playing,
     * this method does nothing (but the next music played will use the default volume
     * of 0.3, not the volume set here).
     * </p>
     * <p>
     * <strong>Example:</strong>
     * <pre>
     * MusicPlayer.setVolume(0.0);  // Mute
     * MusicPlayer.setVolume(0.5);  // 50% volume
     * MusicPlayer.setVolume(1.0);  // Maximum volume
     * </pre>
     * </p>
     *
     * @param volume the desired volume level (0.0 to 1.0)
     */
    public static void setVolume(double volume) {
        if (currentPlayer != null) {
            currentPlayer.setVolume(volume);
        }
    }
}