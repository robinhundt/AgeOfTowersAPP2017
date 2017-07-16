package towerwarspp.board;

import towerwarspp.preset.Move;

/**
 * This class represents the information on a move in respect to its score made according to the simple strategy
 * and the move's possible consequence. The score is stored as an integer value.
 * The possible conseq‪uence is represented by one of the {@link MoveResult} values.
 * The scoerd move is also saved in this object and can be returned on request.
 *
 * @author Anastasiia Kysliak
 * @version 15-07-17
 */
public class MoveScore implements Comparable<MoveScore> {

    /**
     * The move whose score and possible consequence are saved in this {@link MoveScore} object.
     */
    private Move move;
    /**
     * The score for the move made according to the simple strategy.
     */
    private int score;
    /**
     * The possible consequence of the move.
     */
    private MoveResult result;

    /**
     * Creates a new instance of {@link MoveScore} with the specified parameters.
     *
     * @param move   the move whose score and consequence are to be stored in this {@link MoveScore} object.
     * @param score  the score for the move made according to the simple strategy.
     * @param result the possible consequence of the move.
     */
    public MoveScore(Move move, int score, MoveResult result) {
        this.move = move;
        this.score = score;
        this.result = result;
    }

    /**
     * Returns the move whose score and consequence are stored in this {@link MoveScore} object.
     *
     * @return the move whose score and consequence are stored in this object.
     */
    public Move getMove() {
        return move;
    }

    /**
     * Returns the score stored in this object.
     *
     * @return the score stored in this object.
     */
    public int getScore() {
        return score;
    }

    /**
     * Returns the {@link MoveResult} stored in this object.
     *
     * @return the {@link MoveResult} stored in this object.
     */
    public MoveResult getResult() {
        return result;
    }

    /**
     * Compares this object with the specified object for order. Returns a negative integer, zero,
     * or a positive integer as this object is less than, equal to, or greater than the specified object.
     * The smaller the order of the {@link MoveScore} object, the better the corresponding move is.
     *
     * @param other the {@link MoveScore} object to be compared.
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
     */
    public int compareTo(MoveScore other) {
        if (other == null) {
            throw new java.lang.NullPointerException();
        }
        int comp = this.result.compareTo(other.getResult());
        if (comp == 0) {
            return other.getScore() - this.score;
        }
        return comp;
    }
}
