package towerwarspp.main;

import towerwarspp.main.game.Game;

/**
 * Enumeration WinType providing option for the way to win a {@link Game}
 *
 * @author Niklas Mueller
 * @version 07-03-2017
 */
public enum WinType {
    BASE_DESTROYED, NO_POSSIBLE_MOVES, SURRENDER, ILLEGAL_MOVE;

    /**
     * Overriden method toString return a string containing description of this {@link WinType}
     *
     * @return string representing a {@link WinType}
     */
    @Override
    public String toString() {
        switch (this) {
            case BASE_DESTROYED: return "Enemy base destroyed";
            case NO_POSSIBLE_MOVES: return "Enemy without legal moves";
            case SURRENDER: return "Enemy surrendered";
            case ILLEGAL_MOVE: return  "Enemy made illegal move";
            default: return "Unknown win type";
        }
    }
}
