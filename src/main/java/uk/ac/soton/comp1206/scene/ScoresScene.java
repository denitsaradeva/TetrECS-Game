package uk.ac.soton.comp1206.scene;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.ScoresList;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * The Scores Scene shows the top ten local scores and who made them.
 */
public class ScoresScene extends BaseScene {

    /**
     * Logger to keep track of new changes
     */
    private static final Logger logger = LogManager.getLogger(ScoresScene.class);

    /**
     * Holds the same values as the observable list but is used for the binding, since it's a property
     */
    private ListProperty<Pair<String, Integer>> localScores;

    /**
     * Initially holds the values of the static list, but then it dynamically updates
     */
    private ObservableList<Pair<String, Integer>> observableList;

    /**
     * The initial list of scores, that comes from extracting the scores from the external file
     */
    ArrayList<Pair<String, Integer>> staticScores;

    /**
     * The current instance of the game
     */
    private Game game;

    /**
     * The UI for the scores
     */
    private ScoresList scoresList;

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public ScoresScene(GameWindow gameWindow, Game game) {
        super(gameWindow);
        logger.info("Creating Scores Scene");

        //Initialising some fields
        this.staticScores = new ArrayList<>();
        this.game = game;
        writeScores();
        loadScores("scores");
        this.observableList = FXCollections.observableArrayList(staticScores);
        this.localScores = new SimpleListProperty<>(observableList);
        this.scoresList = new ScoresList(gameWindow.getWidth() / 4.0);
        Bindings.bindBidirectional(this.scoresList.simpleListPropertyProperty(), this.localScores);
        this.scoresList.reveal();
    }

    /**
     * Writes the current score to the external scores.txt file
     */
    private void writeScores() {
        try {
            //Initialise all needed tools
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

            //Write the current score to the file
            String output = "Deni: " + this.game.getScore();
            bufferedWriter.write(output);
            bufferedWriter.write("\n");

            //Close and flush all objects
            fileOutputStream.flush();
            outputStreamWriter.flush();
            bufferedWriter.flush();
            bufferedReader.close();
            inputStreamReader.close();
            fileInputStream.close();
            fileOutputStream.close();
            outputStreamWriter.close();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fill the array with information from an external file
     *
     * @param s path
     */
    private void loadScores(String s) {
        try {
            //Initialise all needed tools
            FileInputStream fileInputStream = new FileInputStream(s + ".txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String currentLine = bufferedReader.readLine();

            //Adding all scores, loaded from the file, to the array list
            String[] inputSeparation;
            while (currentLine != null) {
                inputSeparation = currentLine.split(": ");
                this.staticScores.add(new Pair<>(inputSeparation[0], Integer.parseInt(inputSeparation[1])));

                currentLine = bufferedReader.readLine();
            }

            //Close all streams and readers
            bufferedReader.close();
            fileInputStream.close();
            inputStreamReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.staticScores.sort(new Comparator<Pair<String, Integer>>() {
            @Override
            public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
    }

    /**
     * Initialise the scores scene
     */
    @Override
    public void initialise() {
        logger.info("Initialising scores scene");

        //Playing background music
        this.multimedia.playBackgroundMusic("music/end.wav");

        //Handle the event of pressing the escape button
        this.scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                KeyCode keyCode = keyEvent.getCode();
                if (keyCode == KeyCode.ESCAPE) {
                    multimedia.stopSounds("music");
                    gameWindow.startMenu();
                }
            }
        });
    }

    /**
     * Build the Scores window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        //Set the main pane inside the root and its layout
        var scoresPane = new StackPane();
        scoresPane.setMaxWidth(gameWindow.getWidth());
        scoresPane.setMaxHeight(gameWindow.getHeight());
        scoresPane.getStyleClass().add("challenge-background");
        scoresPane.setAlignment(Pos.CENTER);
        root.getChildren().add(scoresPane);

        var content = new VBox();
        content.setAlignment(Pos.CENTER);

        //Making sure the image appears good
        Image image = new Image(getClass().getResource("/images/TetrECS.png").toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(false);
        imageView.setFitWidth(610);
        imageView.setFitHeight(120);

        //The game over title
        var text = new Text("Game Over");
        text.getStyleClass().add("bigtitle");

        content.getChildren().addAll(imageView, text, this.scoresList);
        scoresPane.getChildren().add(content);
    }
}
