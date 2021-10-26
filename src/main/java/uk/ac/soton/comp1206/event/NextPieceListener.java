package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * The next piece listener handles the event when the transition happens between
 * the current piece and the next piece. The listeners listen for when to display them on the piece boards.
 */
public interface NextPieceListener {

    /**
     * Displays the piece on the next piece board -
     * the one that's holding the piece that should be put after
     * putting the current one
     * @param gamePiece the game piece
     */
    public void nextPiece(GamePiece gamePiece);

    /**
     * Displays the piece on the current piece board -
     * the one that's holding the piece that should be put now
     * @param gamePiece the game piece
     */
    public void currentPiece(GamePiece gamePiece);
}
