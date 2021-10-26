package uk.ac.soton.comp1206.scene;

import javafx.scene.Scene;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ui.Multimedia;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * A Base Scene used in the game. Handles common functionality between all scenes.
 */
public abstract class BaseScene {

    /**
     * Logger to keep track of new changes
     */
    private static final Logger logger = LogManager.getLogger(BaseScene.class);

    /**
     * The current game window
     */
    protected final GameWindow gameWindow;

    /**
     * The root pane
     */
    protected GamePane root;

    /**
     * Having the multimedia, in order to produce sounds / sound effects
     */
    protected Multimedia multimedia;

    /**
     * The current scene
     */
    protected Scene scene;

    /**
     * Keep the file with scores
     */
    protected File file;

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public BaseScene(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        this.multimedia = new Multimedia();
        this.file = new File("scores.txt");

        //Checks if the scores file already exists
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Initialise this scene. Called after creation
     */
    public abstract void initialise();

    /**
     * Build the layout of the scene
     */
    public abstract void build();

    /**
     * Create a new JavaFX scene using the root contained within this scene
     *
     * @return JavaFX scene
     */
    public Scene setScene() {
        var previous = gameWindow.getScene();
        Scene scene = new Scene(root, previous.getWidth(), previous.getHeight(), Color.BLACK);
        scene.getStylesheets().add(getClass().getResource("/style/game.css").toExternalForm());
        this.scene = scene;
        return scene;
    }

    /**
     * Get the JavaFX scene contained inside
     *
     * @return JavaFX scene
     */
    public Scene getScene() {
        return this.scene;
    }

}
