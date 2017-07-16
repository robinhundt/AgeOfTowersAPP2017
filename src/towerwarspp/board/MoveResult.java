package towerwarspp.board;

/**
 * This enumeration represents possible consequences of a move for the player who is going to make this move.
 * The enum values have the following meanings:
 * WILL_WIN means an immediate win for the current player as a result of the move in question.
 * CAN_LOSE means that the opponent will have at least one winning move after the move in question will have been executed.
 * UKNOWN means that neither of the previous two options is relevant.
 *
 * @author Anastasiia Kysliak
 * @version 15-07-17
 */
public enum MoveResult {
    WILL_WIN,
    UNKNOWN,
    CAN_LOSE
}
