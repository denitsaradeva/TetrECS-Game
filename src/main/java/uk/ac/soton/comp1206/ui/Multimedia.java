package uk.ac.soton.comp1206.ui;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.Game;

public class Multimedia {

    /**
     * Logger to keep track of new changes
     */
    private static final Logger logger = LogManager.getLogger(Game.class);

    /**
     * The media players needed
     */
    static private MediaPlayer audioPlayer;
    static private MediaPlayer musicPlayer;

    /**
     * Playing audio sounds for the game
     */
    public void playAudioFile(String file) {

        //Get the path
        String audio = Multimedia.class.getResource("/" + file).toExternalForm();

        //Log the event
        logger.info("Playing sound: " + audio);

        //Play the audio
        try {
            Media play = new Media(audio);
            audioPlayer = new MediaPlayer(play);
            audioPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stop the current sounds
     *
     * @param mediaPlayer the media player
     */
    public void stopSounds(String mediaPlayer) {

        //Stop either the music or the audio player
        if (mediaPlayer.equals("music")) {
            musicPlayer.stop();
        }
        if (mediaPlayer.equals("audio")) {
            audioPlayer.stop();
        }
    }

    /**
     * Playing music as a background to the game
     */
    public void playBackgroundMusic(String file) {

        //Get the path
        String audio = Multimedia.class.getResource("/" + file).toExternalForm();

        //Log the event
        logger.info("Playing audio: " + audio);

        //Play the music
        try {
            Media play = new Media(audio);
            musicPlayer = new MediaPlayer(play);
            musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            musicPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
