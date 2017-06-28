package towerwarspp.preset;


public interface Viewer {
    int getSize();
    int getTurn();
    Status getStatus();
    Move[] getPossibleMoves(Position position);
    boolean isTower(int row, int col);
    boolean isStone(int row, int col);
    boolean isBlocked(int row, int col);
    PlayerColor getPlayerColor(int row, int col);
}
