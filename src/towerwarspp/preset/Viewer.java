package towerwarspp.preset;


public interface Viewer {
    int getSize();
    int getTurn();
    Status getStatus();
    int getStones();
    Move[] getPossibleMoves(Position position);
}
