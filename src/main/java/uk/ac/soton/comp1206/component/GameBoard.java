package uk.ac.soton.comp1206.component;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.BlockClickedListener;
import uk.ac.soton.comp1206.event.LeftClickedListener;
import uk.ac.soton.comp1206.event.RightClickedListener;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;

/**
 * A GameBoard is a visual component to represent the visual GameBoard.
 * It extends a GridPane to hold a grid of GameBlocks.
 * <p>
 * The GameBoard can hold an internal grid of it's own, for example, for displaying an upcoming block. It also be
 * linked to an external grid, for the main game board.
 * <p>
 * The GameBoard is only a visual representation and should not contain game logic or model logic in it, which should
 * take place in the Grid.
 */
public class GameBoard extends GridPane {

    /**
     * Logger to keep track of new changes
     */
    private static final Logger logger = LogManager.getLogger(GameBoard.class);

    /**
     * Listeners for left and right clicking on the board
     */
    private RightClickedListener rightClickedListener;
    private LeftClickedListener leftClickedListener;

    /**
     * Number of columns in the board
     */
    private final int cols;

    /**
     * Number of rows in the board
     */
    private final int rows;

    /**
     * The visual width of the board - has to be specified due to being a Canvas
     */
    private final double width;

    /**
     * The visual height of the board - has to be specified due to being a Canvas
     */
    private final double height;

    /**
     * The grid this GameBoard represents
     */
    final Grid grid;

    /**
     * The blocks inside the grid
     */
    GameBlock[][] blocks;

    /**
     * The listener to call when a specific block is clicked
     */
    private BlockClickedListener blockClickedListener;


    /**
     * Create a new GameBoard, based off a given grid, with a visual width and height.
     *
     * @param grid   linked grid
     * @param width  the visual width
     * @param height the visual height
     */
    public GameBoard(Grid grid, double width, double height) {
        this.cols = grid.getCols();
        this.rows = grid.getRows();
        this.width = width;
        this.height = height;
        this.grid = grid;

        //Build the GameBoard
        build();
    }

    /**
     * Create a new GameBoard with it's own internal grid, specifying the number of columns and rows, along with the
     * visual width and height.
     *
     * @param cols   number of columns for internal grid
     * @param rows   number of rows for internal grid
     * @param width  the visual width
     * @param height the visual height
     */
    public GameBoard(int cols, int rows, double width, double height) {
        this.cols = cols;
        this.rows = rows;
        this.width = width;
        this.height = height;
        this.grid = new Grid(cols, rows);

        //Build the GameBoard
        build();
    }


    /**
     * Get a specific block from the GameBoard, specified by it's row and column
     *
     * @param x column
     * @param y row
     * @return game block at the given column and row
     */
    public GameBlock getBlock(int x, int y) {
        return blocks[x][y];
    }

    /**
     * Build the GameBoard by creating a block at every x and y column and row
     */
    protected void build() {
        logger.info("Building grid: {} x {}", cols, rows);

        setMaxWidth(width);
        setMaxHeight(height);

        setGridLinesVisible(true);

        blocks = new GameBlock[cols][rows];

        //Creations of blocks
        for (var y = 0; y < rows; y++) {
            for (var x = 0; x < cols; x++) {
                createBlock(x, y);
            }
        }
    }

    /**
     * Parent method that's implemented by the child classes
     * Visualises the current piece on a smaller grid
     *
     * @param gamePiece the game piece
     */
    public void displayBlock(GamePiece gamePiece) {
    }

    /**
     * Create a block at the given x and y position in the GameBoard
     *
     * @param x column
     * @param y row
     */
    protected GameBlock createBlock(int x, int y) {
        var blockWidth = width / cols;
        var blockHeight = height / rows;

        //Create a new GameBlock UI component
        GameBlock block = new GameBlock(this, x, y, blockWidth, blockHeight);

        //Design the game block
        block.getStyleClass().add("gameBlock");

        //Add to the GridPane
        add(block, x, y);

        //Add to our block directory
        blocks[x][y] = block;

        //Link the GameBlock component to the corresponding value in the Grid
        block.bind(grid.getGridProperty(x, y));

        //Add a mouse click handler to the block to trigger GameBoard blockClicked method
        block.setOnMouseClicked((e) -> blockClicked(e, block));

        return block;
    }

    /**
     * Set the listener to handle an event when a block is clicked
     *
     * @param listener listener to add
     */
    public void setOnBlockClick(BlockClickedListener listener) {
        this.blockClickedListener = listener;
    }

    /**
     * Triggered when a block is clicked. Call the attached listener.
     *
     * @param event mouse event
     * @param block block clicked on
     */
    private void blockClicked(MouseEvent event, GameBlock block) {
        logger.info("Block clicked: {}", block);
        if (event.getButton().equals(MouseButton.SECONDARY) && rightClickedListener != null) {
            rightClickedListener.rightClick();
        } else if (event.getButton().equals(MouseButton.PRIMARY) && leftClickedListener != null) {
            leftClickedListener.leftClick();
        } else if (blockClickedListener != null) {
            blockClickedListener.blockClicked(block);
        }
    }

    /**
     * Set the listener to handle an event when a grid is right clicked
     *
     * @param rightClickedListener listens for a right click
     */
    public void setRightClickedListener(RightClickedListener rightClickedListener) {
        this.rightClickedListener = rightClickedListener;
    }

    /**
     * Set the listener to handle an event when a grid is left clicked
     *
     * @param leftClickedListener listens for a left click
     */
    public void setLeftClickedListener(LeftClickedListener leftClickedListener) {
        this.leftClickedListener = leftClickedListener;
    }

    /**
     * Returns the middle block of a piece, which has a 3x3 grid
     *
     * @return the middle block of a piece
     */
    public GameBlock getMiddleBlock() {
        return blocks[1][1];
    }

    /**
     * Returns how wide is the grid
     *
     * @return width of the grid
     */
    public double getWidthOfGrid() {
        return width;
    }
}
