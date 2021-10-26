package uk.ac.soton.comp1206.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.Multimedia;

import java.util.Arrays;

/**
 * The Grid is a model which holds the state of a game board. It is made up of a set of Integer values arranged in a 2D
 * arrow, with rows and columns.
 * <p>
 * Each value inside the Grid is an IntegerProperty can be bound to enable modification and display of the contents of
 * the grid.
 * <p>
 * The Grid contains functions related to modifying the model, for example, placing a piece inside the grid.
 * <p>
 * The Grid should be linked to a GameBoard for it's display.
 */
public class Grid {

    /**
     * Logger to keep track of new changes
     */
    private static final Logger logger = LogManager.getLogger(Grid.class);

    /**
     * The number of columns in this grid
     */
    private final int cols;

    /**
     * The number of rows in this grid
     */
    private final int rows;

    /**
     * The grid is a 2D arrow with rows and columns of SimpleIntegerProperties.
     */
    private final SimpleIntegerProperty[][] grid;

    /**
     * Multimedia for sound effects
     */
    private Multimedia multimedia;

    /**
     * Create a new Grid with the specified number of columns and rows and initialise them
     *
     * @param cols number of columns
     * @param rows number of rows
     */
    public Grid(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
        this.multimedia = new Multimedia();

        //Create the grid itself
        grid = new SimpleIntegerProperty[cols][rows];

        //Add a SimpleIntegerProperty to every block in the grid
        for (var y = 0; y < rows; y++) {
            for (var x = 0; x < cols; x++) {
                grid[x][y] = new SimpleIntegerProperty(0);
            }
        }
    }

    /**
     * Get the Integer property contained inside the grid at a given row and column index. Can be used for binding.
     *
     * @param x column
     * @param y row
     * @return the IntegerProperty at the given x and y in this grid
     */
    public IntegerProperty getGridProperty(int x, int y) {
        return grid[x][y];
    }

    /**
     * Update the value at the given x and y index within the grid
     *
     * @param x     column
     * @param y     row
     * @param value the new value
     */
    public void set(int x, int y, int value) {
        grid[x][y].set(value);
    }

    /**
     * Get the value represented at the given x and y index within the grid
     *
     * @param x column
     * @param y row
     * @return the value
     */
    public int get(int x, int y) {
        try {
            //Get the value held in the property at the x and y index provided
            return grid[x][y].get();
        } catch (ArrayIndexOutOfBoundsException e) {
            //No such index
            return -1;
        }
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
     * Check whether a there's an available space for a piece
     *
     * @param gamePiece gamePiece
     * @param x         x
     * @param y         y
     * @return whether a piece can be played
     */
    public boolean canPlayPiece(GamePiece gamePiece, int x, int y) {

        //Initializing values
        boolean canPlayPiece = false;
        int[][] gamePieceGrid = gamePiece.getBlocks();
        int lengthOfPiece = lengthOfPiece(gamePieceGrid);
        int row = 0;
        int col = 0;
        int count = 0;

        //Check if each place needed from the bigger grid is free
        if (this.grid[x][y].get() == 0) {
            for (int i = x - 1; i <= x + 1; i++) {
                for (int j = y - 1; j <= y + 1; j++) {
                    //Checking edge cases
                    if ((gamePieceGrid[col][row] != 0 && i < 0) || (gamePieceGrid[col][row] != 0 && j < 0)
                            || (gamePieceGrid[col][row] != 0 && i >= this.grid.length)
                            || (gamePieceGrid[col][row] != 0 && j >= this.grid.length)) {
                        logger.info("Out of bound");
                    } else {
                        if (gamePieceGrid[col][row] != 0 && this.get(i, j) == 0) {
                            count++;
                        }
                    }
                    col++;
                }
                row++;
                col = 0;
            }
        }

        //Check whether all blocks from the piece can successfully be put in the game grid
        if (count == lengthOfPiece) {
            canPlayPiece = true;
            logger.info("The piece can be put.");
        } else {

            //Play audio effect
            multimedia.playAudioFile("sounds/fail.wav");

            logger.info("The piece can't be put.");
        }

        return canPlayPiece;
    }

    /**
     * Puts a piece on the grid
     *
     * @param x         x
     * @param y         y
     * @param gamePiece gamePiece
     */
    public void playPiece(GamePiece gamePiece, int x, int y) {

        //Initializing values
        int[][] gamePieceGrid = gamePiece.getBlocks();
        int row = 0;
        int col = 0;

        //Updates values
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {

                /*
                If the current block of the piece is a coloured one
                and the needed block from the game board is empty,
                then it changes it to the new value of the game block
                 */
                if (gamePieceGrid[col][row] != 0 && this.get(i, j) == 0) {
                    this.set(i, j, gamePieceGrid[col][row]);
                }

                col++;
            }
            col = 0;
            row++;
        }

        //Log event
        logger.info("The grid is updated with the new values successfully.");
    }

    /**
     * Checks how many blocks a figure covers
     *
     * @param gamePieceGrid gamePieceGrid
     * @return number of colored blocks
     */
    private int lengthOfPiece(int[][] gamePieceGrid) {
        int count = 0;

        //Counts the coloured block of a piece
        for (int[] blocks : gamePieceGrid) {
            for (int block : blocks) {
                if (block != 0) {
                    count++;
                }
            }
        }

        return count;
    }
}
