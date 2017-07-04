package towerwarspp.main;

import towerwarspp.preset.Player;

/**
 * Created by niklas on 04.07.17.
 */
public class Tournament {

    Player firstPlayer, secondPlayer;
    OutputType outputType;
    boolean debug;
    int delayTime, boardSize, rounds;
    TResult tResult;

    public Tournament(Player firstPlayer, Player secondPlayer, OutputType outputType, boolean debug,
                      int delayTime, int boardSize, int rounds) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
        this.outputType = outputType;
        this.debug = debug;
        this.delayTime = delayTime;
        this.boardSize = boardSize;
        this.rounds = rounds;
    }

    public TResult play() {
        tResult = new TResult();
        for (int i=1; i<=rounds; i++) {
            System.out.println("Round No.: " + i);
            Game game = new Game(firstPlayer, secondPlayer, outputType, debug, delayTime, boardSize);

            try {
                Result result = game.play();
                tResult.addResult(result);
            }
            catch (Exception e) {
                e.printStackTrace();
            };
        }
        return tResult;
    }
}
