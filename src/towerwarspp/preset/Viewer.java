package towerwarspp.preset;

import java.util.List;

public interface Viewer {
    int getSize();
    int getTurn();
    Status getStatus();
    int getStones();
    Move[] getPossibleMoves(Position position);
}
