package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * The Game Loop listener is used to handle the event when the timer should be restarted.
 * It handles the following change:
 * 1) The next piece is removed.
 * 2) The piece after next piece becomes the next piece.
 * 3) A new random piece appears.
 * It also handles the Ui animation of the timer and the start action.
 */
public interface GameLoopListener {

    /**
     * Handle the loop needed for the timer to keep going
     * @param time the time
     * @param currentPiece the current piece
     * @param followingPiece the following piece
     */
    public void loop(long time, GamePiece currentPiece, GamePiece followingPiece);
}
