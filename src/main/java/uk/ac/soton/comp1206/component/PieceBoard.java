package uk.ac.soton.comp1206.component;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * The logic behind the next piece UI grid and the following after next piece UI grid is implemented here.
 */
public class PieceBoard extends GameBoard {

    /**
     * Creates a board that will hold a specific piece
     *
     * @param rows the rows
     * @param cols the columns
     * @param width the width
     * @param height the height
     */
    public PieceBoard(int rows, int cols, double width, double height) {
        super(rows, cols, width, height);
    }

    /**
     * Handles how the board will display a given piece
     *
     * @param gamePiece the game piece
     */
    public void displayBlock(GamePiece gamePiece) {
        //Holds all 9 blocks of a given piece
        int[][] gameBlocks = gamePiece.getBlocks();

        /*
        Replaces all previously coloured blocks with empty ones, so that it clears the old values.
        Then, it paints the new game piece parameter, showing it on the UI piece board.
         */
        for (int i = 0; i < gameBlocks.length; i++) {
            for (int j = 0; j < gameBlocks[i].length; j++) {
                if (this.grid.get(j, i) != 0) {
                    this.grid.set(j, i, 0);
                }
                if (gameBlocks[i][j] != 0) {
                    this.grid.set(j, i, gamePiece.getValue());
                }
            }
        }
    }
}
