package uk.ac.soton.comp1206.scene;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.Lighting;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.event.*;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    /**
     * Logger to keep track of new changes
     */
    private static final Logger logger = LogManager.getLogger(ChallengeScene.class);

    /**
     * The current instance of the game
     */
    protected Game game;

    /**
     * Converter needed for binding
     */
    private final StringConverter<Number> converter = new NumberStringConverter();

    /**
     * Initialising both Boards for the current and next pieces
     */
    private final GameBoard miniBoard = new PieceBoard(3, 3, 150, 150);
    private final GameBoard tinyBoard = new PieceBoard(3, 3, 100, 100);

    /**
     * The current game board
     */
    private GameBoard gameBoard;

    /**
     * Timeline and rectangle for the animation time bar
     */
    private Rectangle rectangle;
    private Timeline timeline;

    /**
     * The bottom pane
     */
    private StackPane bottomPane;

    /**
     * All dynamic texts for counting a specific value
     */
    private Text scoreMainCount;
    private Text livesCount;
    private Text multiplier;
    private Text levelCount;
    private Text highestScore;

    /**
     * The properties that go for binding
     */
    private IntegerProperty livesProperty;
    private IntegerProperty highestScoreProperty = new SimpleIntegerProperty();

    /**
     * Button for power up
     */
    private Button powerUp;


    /**
     * Create a new Single Player challenge scene
     *
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());
        setupGame();
        gameBoard = new GameBoard(game.getGrid(), gameWindow.getWidth() / 2.0, gameWindow.getWidth() / 2.0);
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        //Set the main pane inside the root and its layout
        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("challenge-background");
        root.getChildren().add(challengePane);

        //Adding some panes and boxes for this scene
        BorderPane mainPane = new BorderPane();
        var topPart = new BorderPane();
        var topLeftPart = new HBox();
        topLeftPart.setSpacing(6.0);
        var topRightPart = new VBox();

        challengePane.getChildren().add(mainPane);
        mainPane.setCenter(gameBoard);

        //Handling UI for score and the score dynamic counter
        var topLeftLeftPart = new VBox();
        var scoreMainTitle = new Text("Score");
        scoreMainTitle.getStyleClass().add("heading");
        scoreMainCount = new Text();
        scoreMainCount.getStyleClass().add("score");
        topLeftLeftPart.getChildren().addAll(scoreMainTitle, scoreMainCount);

        //Button that activates if multiplier becomes three
        var topLeftRightPart = new VBox();
        powerUp = new Button("Power Up +500 points");
        powerUp.setDisable(true);
        topLeftRightPart.getChildren().add(powerUp);

        topLeftPart.getChildren().addAll(topLeftLeftPart, topLeftRightPart);
        topLeftPart.setAlignment(Pos.CENTER);
        topPart.setLeft(topLeftPart);

        //Handling UI for lives and the lives dynamic counter
        var livesTitle = new Text("Lives");
        livesTitle.getStyleClass().add("heading");
        livesCount = new Text();
        livesCount.getStyleClass().add("lives");
        topRightPart.getChildren().add(livesTitle);
        topRightPart.getChildren().add(livesCount);
        topRightPart.setAlignment(Pos.CENTER);
        topPart.setRight(topRightPart);

        //Initialisation
        livesProperty = new SimpleIntegerProperty();

        //handling UI for the challenge mode title
        var topCenterPart = new VBox();
        var title = new Text("Challenge Mode");
        topCenterPart.getChildren().add(title);
        title.getStyleClass().add("title");
        topCenterPart.setAlignment(Pos.CENTER);
        topPart.setCenter(topCenterPart);

        BorderPane.setMargin(topPart, new Insets(10, 10, 10, 10));
        mainPane.setTop(topPart);

        //Designing right part
        var rightPart = new BorderPane();
        var sidebar = new VBox();
        rightPart.setCenter(sidebar);

        //Handling UI for multiplier and the multiplier dynamic counter
        var multiplierTitle = new Text("Multiplier");
        multiplierTitle.getStyleClass().add("heading");
        multiplier = new Text();
        multiplier.getStyleClass().add("level");

        //handling UI for the highest score and the highest score dynamic number
        var scoreTitle = new Text("High Score");
        scoreTitle.getStyleClass().add("heading");
        sidebar.setAlignment(Pos.CENTER);
        highestScore = new Text(String.valueOf(this.getHighestScore()));
        highestScore.getStyleClass().add("hiscore");

        //Handling UI for level and the level dynamic counter
        var levelTitle = new Text("Level");
        levelTitle.getStyleClass().add("heading");
        levelCount = new Text();
        levelCount.getStyleClass().add("level");

        //Handling UI for the incoming heading
        var incomingTitle = new Text("Incoming");
        incomingTitle.getStyleClass().add("heading");

        //Handling UI for both piece boards
        var emptyText = new Text();
        this.miniBoard.displayBlock(this.game.getCurrentPiece());
        this.tinyBoard.displayBlock(this.game.getFollowingPiece());

        sidebar.getChildren().addAll(multiplierTitle, multiplier, scoreTitle, highestScore, levelTitle, levelCount,
                incomingTitle, miniBoard, emptyText, tinyBoard);

        BorderPane.setMargin(sidebar, new Insets(10, 10, 10, 10));
        mainPane.setRight(rightPart);

        //Handling UI for the animated timer
        bottomPane = new StackPane();
        rectangle = new Rectangle(gameWindow.getWidth(), 20);
        rectangle.setEffect(new Lighting());
        rectangle.setFill(Color.GREEN);
        bottomPane.setAlignment(Pos.TOP_LEFT);
        timeline = new Timeline();
        KeyValue beginValue = new KeyValue(rectangle.widthProperty(), 30);
        KeyFrame keyFrame1 = new KeyFrame(Duration.millis(12000L), beginValue);
        timeline.getKeyFrames().addAll(keyFrame1);
        timeline.play();
        bottomPane.getChildren().add(rectangle);
        mainPane.setBottom(bottomPane);

        //Handle block on gameBoard grid being clicked
        gameBoard.setOnBlockClick(this::blockClicked);

    }

    /**
     * Handle how to proceed when the timer loops again
     *
     * @param time           time
     * @param currentPiece   current piece
     * @param followingPiece following piece
     */
    public void handleAnimation(long time, GamePiece currentPiece, GamePiece followingPiece) {
        timeline = new Timeline();

        //Reflect the change of the current and next block on the UI
        tinyBoard.displayBlock(followingPiece);
        miniBoard.displayBlock(currentPiece);

        //Load the whole rectangle when reset and then wait until it totally disappears within the specified time period
        KeyValue beginValue = new KeyValue(rectangle.widthProperty(), bottomPane.getWidth());
        KeyValue endValue = new KeyValue(rectangle.widthProperty(), 0);
        KeyFrame beginFrame = new KeyFrame(new Duration(0), beginValue);
        KeyFrame endFrame = new KeyFrame(new Duration(time), endValue);

        timeline.getKeyFrames().addAll(beginFrame, endFrame);

        //Start the action specified above
        timeline.play();
    }

    /**
     * Handle when a block is clicked
     *
     * @param gameBlock the Game Block that was clocked
     */
    private void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
    }

    /**
     * Setup the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");

        //Start new game
        game = new Game(5, 5);
    }

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");

        //Start the game
        game.start();

        //Playing background music
        this.multimedia.playBackgroundMusic("music/game.wav");

        //Handle all bindings needed for the dynamic components
        Bindings.bindBidirectional(scoreMainCount.textProperty(), this.game.scoreProperty(), converter);
        Bindings.bindBidirectional(livesCount.textProperty(), this.game.livesProperty(), converter);
        Bindings.bindBidirectional(multiplier.textProperty(), this.game.multiplierProperty(), converter);
        Bindings.bindBidirectional(levelCount.textProperty(), this.game.levelProperty(), converter);
        Bindings.bindBidirectional(highestScore.textProperty(), this.highestScoreProperty, converter);
        livesProperty.bindBidirectional(game.livesProperty());


        //If the multiplier becomes three, the button for a power up becomes active
        this.multiplier.textProperty().addListener((observableValue, s, t1) -> {
            if (game.multiplierProperty().get() >= 3) {
                powerUp.setDisable(false);
            }
        });

        //Handle the event of clicking power up button
        this.powerUp.setOnMouseClicked(event -> {

            //Play audio effect
            multimedia.playAudioFile("sounds/lifegain.wav");
            multimedia.playAudioFile("sounds/lifegain.wav");

            //Additional 500 points received
            game.setScore(500);
            powerUp.setDisable(true);
        });

        /*
        Listen if the current score goes above the highest one.
        Once it reaches it, the highest score becomes the current one.
         */
        this.game.scoreProperty().addListener((observableValue, number, t1) -> {

            //Play audio effect
            multimedia.playAudioFile("sounds/clear.wav");

            if (game.getScore() > highestScoreProperty.get()) {
                Bindings.bindBidirectional(highestScore.textProperty(), game.scoreProperty(), converter);
            }
        });

        //Handle event when no lives are left
        this.livesProperty.addListener((observableValue, number, t1) -> {

            //Play audio effect
            multimedia.playAudioFile("sounds/lifelose.wav");

            if (livesProperty.get() < 0) {
                Platform.runLater(() -> {
                    try {
                        game.setScheduledExecutorService(null);
                        game.setScheduledFuture(null);
                    } catch (Exception ignored) {

                    }
                    this.multimedia.stopSounds("music");
                    gameWindow.startScores(game);
                });
            }
        });

        //Handle game loops
        game.setGameLoopListener(this::handleAnimation);

        //Handle going back to menu when Escape button is pressed
        this.scene.setOnKeyPressed(keyEvent -> {
            KeyCode keyCode = keyEvent.getCode();
            if (keyCode == KeyCode.ESCAPE) {
                game.setGameLoopListener(null);
                this.multimedia.stopSounds("music");
                try {
                    game.setScheduledExecutorService(null);
                    game.setScheduledFuture(null);
                } catch (Exception ignored) {

                }
                gameWindow.startMenu();
            }
        });

        //Handle tinyBoard being left clicked
        tinyBoard.setLeftClickedListener(() -> {
            game.swapCurrentPiece();
            miniBoard.displayBlock(game.getCurrentPiece());
            tinyBoard.displayBlock(game.getFollowingPiece());
        });

        //Handle miniBoard being left clicked
        miniBoard.setLeftClickedListener(() -> {
            game.rotateCurrentPiece();

            //Play audio effect
            multimedia.playAudioFile("sounds/rotate.wav");

            miniBoard.displayBlock(game.getCurrentPiece());
        });

        //Handle gameBoard being right clicked
        gameBoard.setRightClickedListener(() -> {
            game.rotateCurrentPiece();

            //Play audio effect
            multimedia.playAudioFile("sounds/rotate.wav");

            miniBoard.displayBlock(game.getCurrentPiece());
        });

        //Handle next piece being generated
        this.game.setNextPieceListener(new NextPieceListener() {

            @Override
            public void nextPiece(GamePiece gamePiece) {

                //Play audio effect
                multimedia.playAudioFile("sounds/place.wav");

                tinyBoard.displayBlock(gamePiece);
            }

            @Override
            public void currentPiece(GamePiece gamePiece) {
                miniBoard.displayBlock(gamePiece);
            }
        });
    }

    /**
     * Returns the highest score from the scores.txt
     *
     * @return the highest score
     */
    public Integer getHighestScore() {

        //All results
        ArrayList<Integer> results = new ArrayList<>();

        try {
            //Initialise all needed readers and streams
            FileInputStream fileInputStream = new FileInputStream("scores.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            //Add all scores into the array
            String currentLine = bufferedReader.readLine();
            String[] inputSeparation;
            while (currentLine != null) {
                inputSeparation = currentLine.split(": ");
                results.add(Integer.parseInt(inputSeparation[1]));
                currentLine = bufferedReader.readLine();
            }

            //Close all readers and streams
            bufferedReader.close();
            fileInputStream.close();
            inputStreamReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Sort the collection and return the highest score
        if (results.size() != 0) {
            Collections.sort(results);
            Integer integer = results.get(results.size() - 1);
            this.highestScoreProperty = new SimpleIntegerProperty(integer);
            return integer;
        } else {
            return 0;
        }
    }
}
