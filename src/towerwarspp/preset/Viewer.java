package towerwarspp.preset;


public interface Viewer {
    int getSize();
    int getTurn();
    Status getStatus();
    Move[] getPossibleMoves(Position position);
    boolean isTower(int x, int y);
    boolean isStone(int x, int y);
    boolean isBlocked(int x, int y);
    int getHeight(int x, int y);
    PlayerColor getPlayerColor(int x, int y);
}
