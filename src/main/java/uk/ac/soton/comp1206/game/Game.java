package uk.ac.soton.comp1206.game;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.event.NextPieceListener;

import java.util.Random;
import java.util.concurrent.*;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

    /**
     * Logger to keep track of new changes
     */
    private static final Logger logger = LogManager.getLogger(Game.class);

    /**
     * Properties for the different changing values
     */
    private IntegerProperty score;
    private IntegerProperty level;
    private IntegerProperty lives;
    private IntegerProperty multiplier;

    /**
     * The fields used for the timer's implementation
     */
    private ScheduledExecutorService scheduledExecutorService;
    private ScheduledFuture scheduledFuture;

    /**
     * Listeners for the next piece and when to keep a game loop going
     */
    private NextPieceListener nextPieceListener;
    private GameLoopListener gameLoopListener;

    /**
     * Points that are left until a user levels up
     */
    private int leftPointsToLevelUp;

    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;

    /**
     * The grid model linked to the game
     */
    protected final Grid grid;

    /**
     * Keeping track of the current piece
     */
    protected GamePiece currentPiece;
    protected GamePiece followingPiece;


    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     *
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        //Assigning how many rows and cols the game will consist of
        this.cols = cols;
        this.rows = rows;

        //Initialise all properties that are going to be used for observing the change of specific values
        this.score = new SimpleIntegerProperty(0);
        this.level = new SimpleIntegerProperty(0);
        this.lives = new SimpleIntegerProperty(3);
        this.multiplier = new SimpleIntegerProperty(1);

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols, rows);

        //Generating a current piece
        this.currentPiece = spawnPiece();

        //Generating the next piece
        this.followingPiece = spawnPiece();

        //Assign start values to the following properties:
        this.leftPointsToLevelUp = 1000;
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");

        try {
            //Initialize the timer
            Platform.runLater(() -> scheduledExecutorService = Executors.newSingleThreadScheduledExecutor());
        } catch (Exception ignore) {

        }

        //Initialise the game
        initialiseGame();

        //Start the timer running
        Platform.runLater(this::startLoop);
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");
    }

    /**
     * Start the timer to be running actively
     */
    public void startLoop() {
        //The resulting loop from the timer's scheduled event to count the time
        try {
            Platform.runLater(() -> scheduledFuture = scheduledExecutorService.schedule(Game.this::loopWhenTimeOut,
                    getTimerDelay(),
                    TimeUnit.MILLISECONDS));
        } catch (Exception ignore) {

        }

        //Make the timer to make the needed changes and to start again
        if (gameLoopListener != null) {
            gameLoopListener.loop(getTimerDelay(), this.currentPiece, this.followingPiece);
        }
    }

    /**
     * Handles the event when the time is out, how to proceed and to start the timer again
     */
    public void loopWhenTimeOut() {
        //Logging change
        logger.info("Loop starting again.");

        if (scheduledFuture != null) {

            //Taking one life off because of time out
            this.lives.setValue(this.lives.getValue() - 1);

            //Making the change for the current and the next pieces
            this.nextPiece();

            //Setting the value of the multiplier to its initial one
            this.multiplier.setValue(1);

            //Reflecting changes on the UI
            gameLoopListener.loop(getTimerDelay(), this.currentPiece, this.followingPiece);

            //Starting the timer again
            try {
                if (scheduledFuture != null) {
                    Platform.runLater(() ->
                            scheduledFuture = scheduledExecutorService.schedule(Game.this::loopWhenTimeOut,
                                    getTimerDelay(), TimeUnit.MILLISECONDS));
                }
            } catch (Exception ignore) {

            }
        }
    }

    /**
     * Handles the event when a piece is put onto the game board:
     * 1) Cancels the timer
     * 2) Starts the timer again
     * 3) Reflects changes on the UI
     */
    public void loopWhenPutPiece() {
        //Cancels the timer
        try {
            Platform.runLater(() -> scheduledFuture.cancel(false));
        } catch (Exception ignore) {

        }

        //Reflects changes on the UI
        if (gameLoopListener != null) {
            gameLoopListener.loop(getTimerDelay(), this.currentPiece, this.followingPiece);
        }

        //Starts the timer again
        try {
            Platform.runLater(() -> scheduledFuture = scheduledExecutorService.schedule(Game.this::loopWhenTimeOut, getTimerDelay(), TimeUnit.MILLISECONDS));
        } catch (Exception ignore) {

        }
    }

    /**
     * Handle what should happen when a particular block is clicked
     *
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();

        //Log the event
        logger.info(x + " " + y);

        //Checks if the piece can be put
        if (this.grid.canPlayPiece(this.currentPiece, x, y)) {

            //Plays the piece
            this.grid.playPiece(this.currentPiece, x, y);

            //Changes the information for the current and next pieces
            nextPiece();

            //Looks for any lines being made
            afterPiece();

            //Displays the current and next pieces on their piece boards
            nextPieceListener.currentPiece(this.currentPiece);
            nextPieceListener.nextPiece(this.followingPiece);

            //Reflect and synchronise changes with the timer
            Platform.runLater(this::loopWhenPutPiece);
        }
    }

    /**
     * Keep track of the score and level
     *
     * @param numberOfLines  number of lines made
     * @param numberOfBlocks number of blocks
     */
    public void score(int numberOfLines, int numberOfBlocks) {

        //Calculate the score when a line is made
        if (numberOfLines != 0) {
            int scoreCount = numberOfBlocks * numberOfLines * 10 * this.getMultiplier();

            //Update the score field
            this.setScore(scoreCount);
        }

        //Checks whether the user levels up
        if (this.score.get() >= this.leftPointsToLevelUp) {

            //Updates the points needed for the next level up
            this.leftPointsToLevelUp += 1000;

            //Updates the level field
            this.level.set(this.level.get() + 1);
        }
    }

    /**
     * Check whether a vertical/horizontal line is made
     * If there is such line, remove it
     */
    public void afterPiece() {

        //Setting initial values
        int numberOfLines = 0;
        int numberOfBlocks = 0;
        int horizontalRemoved = 0;
        int verticalRemoved = 0;

        //The amount of coloured blocks on a specific row
        int horizontalCount;

        //The amount of coloured blocks on a specific column
        int verticalCount;

        //These arrays hold the indices of rows or columns, which are completed
        int[] indicesHorizontal = new int[5];
        int[] indicesVertical = new int[5];

        //Checking whether there's a completed row / column
        for (int i = 0; i < this.grid.getRows(); i++) {

            //Resetting the values for the horizontal and vertical counts
            horizontalCount = 0;
            verticalCount = 0;

            //Counting the horizontal and vertical coloured blocks
            for (int j = 0; j < this.grid.getCols(); j++) {

                //If a block is coloured, the horizontal count is increased
                if (this.grid.get(j, i) != 0) {
                    horizontalCount++;
                }

                //If a block is coloured, the vertical count is increased
                if (this.grid.get(i, j) != 0) {
                    verticalCount++;
                }
            }

            /*
            If 5 of the blocks in a row are coloured:
                - I mark that row as completed (setting the index to 1)
                - I increase th variable, holding the number of completed lines
                - I increase the completed rows with 1
            If 5 of the blocks in a column are coloured:
                - I mark that column as completed (setting the index to 1)
                - I increase the variable, holding the number of completed lines, with 1
                - I increase the completed columns with 1
             */
            if (horizontalCount == this.getCols()) {
                indicesHorizontal[i] = 1;
                numberOfLines++;
                horizontalRemoved++;
                numberOfBlocks += this.getCols();
            }
            if (verticalCount == this.getCols()) {
                indicesVertical[i] = 1;
                numberOfLines++;
                numberOfBlocks += this.getRows();
                verticalRemoved++;
            }
        }

        //Removes all coloured blocks from the completed lines and sets their value to 0
        for (int k = 0; k < indicesHorizontal.length; k++) {

            //Checks all rows
            if (indicesHorizontal[k] == 1) {
                for (int l = 0; l < this.getRows(); l++) {
                    this.grid.set(l, k, 0);
                }
                logger.info("A horizontal line is deleted.");
            }

            //Checks all columns
            if (indicesVertical[k] == 1) {
                for (int l = 0; l < this.getRows(); l++) {
                    this.grid.set(k, l, 0);
                }
                logger.info("A vertical line is deleted.");
            }
        }

        //If there are not any completed lines during this round, the multiplier resets to 0
        if (numberOfLines == 0) {
            this.multiplier.set(1);
        }

        //If there are some completed lines, the multiplier increases by 1
        if (numberOfLines > 0) {
            int multiplierValue = this.multiplier.get();
            this.multiplier.set(multiplierValue + 1);
        }

        //Calculating how many blocks were actually removed
        numberOfBlocks -= horizontalRemoved * verticalRemoved;

        //Update the current score
        score(numberOfLines, numberOfBlocks);
    }

    /**
     * Generate a new game piece with specified value
     *
     * @return newly created game piece
     */
    public GamePiece spawnPiece() {

        //Generating a random piece
        Random random = new Random();
        int bound = 15;
        GamePiece gamePiece = GamePiece.createPiece(random.nextInt(bound));

        //Log event
        logger.info(gamePiece.getName());

        return gamePiece;
    }

    /**
     * Get a new piece and set it as a current one
     */
    public void nextPiece() {

        //The current piece becomes the piece after it
        if (this.followingPiece != null) {
            this.currentPiece = this.followingPiece;
        }

        //The piece after becomes a new one, generated randomly
        this.followingPiece = spawnPiece();
    }

    /**
     * Rotating the current piece
     */
    public void rotateCurrentPiece() {

        //Rotating the current piece
        this.currentPiece.rotate();

        //Log event
        logger.info("the piece is rotated");
    }

    /**
     * Handles the event of swapping the current and next pieces
     */
    public void swapCurrentPiece() {

        //Swapping the pieces
        GamePiece temp = this.currentPiece;
        this.currentPiece = this.followingPiece;
        this.followingPiece = temp;

        //Log the event
        logger.info("Pieces are swapped.");
    }

    /**
     * Calculates the delay for the timer
     *
     * @return the delay
     */
    public long getTimerDelay() {

        //The calculation
        long delay = 12000 - (500L * this.level.get());

        //If it reaches or falls below the minimum, it should keep the minimal value
        if (delay <= 2500) {
            delay = 2500;
        }

        return delay;
    }

    /**
     * Generate a new game piece with specified value and rotation
     *
     * @param value    value
     * @param rotation rotation
     * @return newly created game piece
     */
    public GamePiece spawnPiece(int value, int rotation) {
        return GamePiece.createPiece(value, rotation);
    }

    /**
     * Get the grid model inside this game representing the game state of the board
     *
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Get the number of columns in this game
     *
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     *
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Sets the listener to listen for a game loop
     *
     * @param gameLoopListener the listener for a game loop
     */
    public void setGameLoopListener(GameLoopListener gameLoopListener) {
        this.gameLoopListener = gameLoopListener;
    }

    /**
     * Get the current score value
     *
     * @return the current score
     */
    public int getScore() {
        return score.get();
    }

    /**
     * Get the current score
     *
     * @return the score as an integer property
     */
    public IntegerProperty scoreProperty() {
        return score;
    }

    /**
     * Get the current level
     *
     * @return the level as an integer property
     */
    public IntegerProperty levelProperty() {
        return level;
    }

    /**
     * Get the current lives
     *
     * @return the lives as an integer property
     */
    public IntegerProperty livesProperty() {
        return lives;
    }

    /**
     * Get the current multiplier int value
     *
     * @return the multiplier as an integer value
     */
    public int getMultiplier() {
        return multiplier.get();
    }

    /**
     * Get the current multiplier
     *
     * @return the multiplier as an integer property
     */
    public IntegerProperty multiplierProperty() {
        return multiplier;
    }

    /**
     * Changes the current score with a given number
     */
    public void setScore(int score) {
        int current = this.score.get();
        this.score.set(current + score);
    }

    /**
     * Get the current piece
     *
     * @return the current game piece
     */
    public GamePiece getCurrentPiece() {
        return currentPiece;
    }

    /**
     * Get the next piece
     *
     * @return the next game piece
     */
    public GamePiece getFollowingPiece() {
        return followingPiece;
    }

    /**
     * Sets a value for the next piece listener to listen for next piece
     */
    public void setNextPieceListener(NextPieceListener nextPieceListener) {
        this.nextPieceListener = nextPieceListener;
    }

    /**
     * Sets a value for the timer
     */
    public void setScheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
    }

    /**
     * Sets a value for the loop
     */
    public void setScheduledFuture(ScheduledFuture<String> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }
}
