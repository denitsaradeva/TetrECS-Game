package uk.ac.soton.comp1206.scene;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.App;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    /**
     * Logger to keep track of new changes
     */
    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Create a new menu scene
     *
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {

        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        //Set the main pane inside the root and its layout
        var menuPane = new BorderPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

        //Set the layout for the central vbox
        var menuBox = new VBox();
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setSpacing(35.0);

        //Set the layout specifics for the image
        Image image = new Image(getClass().getResource("/images/TetrECS.png").toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(false);
        imageView.setFitWidth(610);
        imageView.setFitHeight(120);


        //Adding all option components into a grid pane
        var listPane = new GridPane();

        var firstOption = new Text("Single Player");
        firstOption.getStyleClass().add("menuItem");
        listPane.add(firstOption, 0, 0);

        var secondOption = new Text("Multi Player");
        secondOption.getStyleClass().add("menuItem");
        listPane.add(secondOption, 0, 1);

        var thirdOption = new Text("How To Play");
        thirdOption.getStyleClass().add("menuItem");
        listPane.add(thirdOption, 0, 2);

        var fourthOption = new Text("Exit");
        fourthOption.getStyleClass().add("menuItem");
        listPane.add(fourthOption, 0, 3);

        //Adding the functionality to the 4 options
        firstOption.setOnMouseClicked(this::startGame);
        secondOption.setOnMouseClicked(this::startGame);
        thirdOption.setOnMouseClicked(this::startHowToPlay);
        fourthOption.setOnMouseClicked(e -> App.getInstance().shutdown());

        listPane.setAlignment(Pos.CENTER);

        menuBox.getChildren().addAll(imageView, listPane);
        menuPane.setTop(new VBox());
        menuPane.setCenter(menuBox);
    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {

        //Play the audio music
        this.multimedia.playBackgroundMusic("music/menu.mp3");

        //Handle the event of pressing the escape button --> shut down the game
        this.scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                KeyCode keyCode = keyEvent.getCode();
                if (keyCode == KeyCode.ESCAPE) {
                    App.getInstance().shutdown();
                }
            }
        });
    }

    /**
     * Handle when the Start Game button is pressed
     *
     * @param event event
     */
    private void startGame(MouseEvent event) {
        this.multimedia.stopSounds("music");
        gameWindow.startChallenge();
    }

    /**
     * Handle when the How to Play button is pressed
     *
     * @param event event
     */
    private void startHowToPlay(MouseEvent event) {
        gameWindow.startHowToPlay();
    }

}
