package uk.ac.soton.comp1206.scene;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * Responsible for showing the UI needed for when an user clicks on the "How to play button" in the menu
 */
public class InstructionsScene extends BaseScene {

    /**
     * Logger to keep track of new changes
     */
    private static final Logger logger = LogManager.getLogger(InstructionsScene.class);

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public InstructionsScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Instructions Scene");
    }

    /**
     * Initialise the "How to Play" instructions scene
     */
    @Override
    public void initialise() {
        logger.info("Initialising How To Play scene");

        //Handle the event of pressing the escape button
        this.scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                KeyCode keyCode = keyEvent.getCode();
                if(keyCode==KeyCode.ESCAPE){
                    gameWindow.startMenu();
                }
            }
        });
    }

    /**
     * Build the Instructions window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        //Set the main pane inside the root and its layout
        var instructionPane = new BorderPane();
        instructionPane.setMaxWidth(gameWindow.getWidth());
        instructionPane.setMaxHeight(gameWindow.getHeight());
        instructionPane.getStyleClass().add("menu-background");
        root.getChildren().add(instructionPane);

        var container = new VBox();
        container.setAlignment(Pos.CENTER);

        //Setting the instructions heading
        var instructionsTitle = new Text("Instructions");
        instructionsTitle.getStyleClass().add("heading");

        //Setting the instructions text
        var instructions = new Text("TetrECS is a fast-paced gravity-free block placement game, where you must survive " +
                "by clearing rows through careful placement of the upcoming blocks before the time runs out." +
                "Lose all 3 lives and you're destroyed!");
        instructions.setWrappingWidth(gameWindow.getWidth()-2);
        instructions.setTextAlignment(TextAlignment.CENTER);
        instructions.getStyleClass().add("instructions");

        //Making sure the image appears good
        Image image = new Image(this.getClass().getResource("/images/Instructions.png").toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(false);
        imageView.setFitWidth(gameWindow.getWidth()/1.3);
        imageView.setFitHeight(gameWindow.getHeight() / 2.0);


        //Creating the heading above the game pieces
        var gamePiecesTitle = new Text("Game Pieces");
        gamePiecesTitle.getStyleClass().add("heading");

        //Customize the grid pane, where all game pieces will appear
        var gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setAlignment(Pos.CENTER);

        GameBoard gameBoard;
        GamePiece gamePiece;

        //Set counter to hold the different unique values for the game pieces
        int count = 0;

        //
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 3; j++) {

                //Create the game piece
                gamePiece = GamePiece.createPiece(count);

                //Create a piece board out of the game piece
                gameBoard = new PieceBoard(3, 3, gameWindow.getWidth() / 10.0,
                        gameWindow.getHeight() / 10.0);

                //Visually display the block
                gameBoard.displayBlock(gamePiece);
                gridPane.add(gameBoard, i, j);

                count++;
            }
        }

        //Organize the layout
        container.getChildren().addAll(instructionsTitle, instructions, imageView, gamePiecesTitle, gridPane);
        instructionPane.setCenter(container);
    }
}
