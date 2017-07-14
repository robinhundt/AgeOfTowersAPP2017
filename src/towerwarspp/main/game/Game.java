package towerwarspp.main.game;

import towerwarspp.board.Board;
import towerwarspp.io.View;
import towerwarspp.main.Debug;
import towerwarspp.main.WinType;
import towerwarspp.preset.*;
import towerwarspp.player.*;

import java.rmi.RemoteException;

import static towerwarspp.preset.Status.*;
import static towerwarspp.preset.PlayerColor.BLUE;
import static towerwarspp.preset.PlayerColor.RED;

/**
 * Class {@link Game} doing management of players and output.
 *
 * @author Niklas Mueller
 * @version 1.5 July 03rd 2017
 */
public class Game {
    /**
     * {@link Player} with {@link PlayerColor} RED
     */
    private Player redPlayer;
    /**
     * {@link Player} with {@link PlayerColor} BLUE
     */
    private Player bluePlayer;
    /**
     * {@link Board} to play with
     */
    private Board board;
    /**
     * {@link View} object to visualize the {@link Board} and {@link Game}
     */
    private View view;
    /**
     * boolean debug activating debug-mode if true
     */
    private boolean debug;
    /**
     * boolean hasView showing if {@link View} object is given
     */
    private boolean hasView;
    /**
     * integer delayTime, time to wait after every turn
     */
    private int delayTime;
    /**
     * {@link Debug} object that is used to print out debug information from all parts of the program in main game loop
     */
    private Debug debugMsg;

    /**
     * Save Object, which saves all moves and exports them to a savefile
     */
    private Save saveGame;

    /**
     * the size of the board
     */
    private int boardSize;

    /**
     *Constructor setting {@link Player}s, {@link Board}, {@link View} and integer variables
     *
     * @param redPlayer {@link Player} with {@link PlayerColor} RED
     * @param bluePlayer {@link Player} with {@link PlayerColor} BLUE
     * @param boardSize integer size of {@link Board}
     * @param view {@link View} object providing possibility for visualization
     * @param debug integer debug activating debug mode if true
     * @param delayTime time to wait after every turn (in millisecond)
     */
    public Game(Player redPlayer, Player bluePlayer, int boardSize, View view, boolean debug, int delayTime) {
        debugMsg = Debug.getInstance();
        /*if one of the players is null*/
        if (redPlayer == null || bluePlayer == null) {
            throw new IllegalArgumentException("Player cannot be null!");
        }
        /*set players , debug-mode, delay time and viewer*/
        this.redPlayer = redPlayer;
        this.bluePlayer = bluePlayer;
        this.debug = debug;
        this.delayTime = delayTime;
        this.view = view;
        this.boardSize = boardSize;
        saveGame = new Save(boardSize);
        

        if(debug)
            System.out.println(debugMsg);

        /*create new board and include viewer object in view*/
        board = new Board(boardSize);
        if(view != null) {
            view.setViewer(board.viewer());
            hasView = true;
        }

        if(debug)
            System.out.println(debugMsg);

        /*try to initialized players*/
        try {
            this.redPlayer.init(board.getSize(), RED);
            this.bluePlayer.init(board.getSize(), BLUE);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        if(debug)
            System.out.println(debugMsg);
    }

    /**
     * Constructor for loading games from an .aot-Save-File
     * @param redPlayer {@link Player} with {@link PlayerColor} RED
     * @param bluePlayer {@link Player} with {@link PlayerColor} BLUE
     * @param view {@link View} object providing possibility for visualization
     * @param debug integer debug activating debug mode if true
     * @param delayTime time to wait after every turn (in millisecond)
     * @param saveGame Save-Object from Save-File
     */
    public Game (Player redPlayer, Player bluePlayer, View view, boolean debug, int delayTime, Save saveGame) {
        debugMsg = Debug.getInstance();
        /*if one of the players is null*/
        if (redPlayer == null || bluePlayer == null) {
            throw new IllegalArgumentException("Player cannot be null!");
        }
        /*set players , debug-mode, delay time and viewer*/
        this.redPlayer = redPlayer;
        this.bluePlayer = bluePlayer;
        this.debug = debug;
        this.delayTime = delayTime;
        this.view = view;
        this.saveGame = saveGame;
        this.boardSize = saveGame.getSize();
        

        if(debug)
            System.out.println(debugMsg);

        /*create new board and include viewer object in view*/
        board = new Board(boardSize);
        if(view != null) {
            view.setViewer(board.viewer());
            hasView = true;
        }

        if(debug)
            System.out.println(debugMsg);

        /*try to initialized players*/
        try {
            this.redPlayer.init(board.getSize(), RED);
            this.bluePlayer.init(board.getSize(), BLUE);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        if(debug)
            System.out.println(debugMsg);
        replay(saveGame);
        ((BasePlayer)redPlayer).setBoard(board.clone());
        ((BasePlayer)bluePlayer).setBoard(board.clone());
    }


    /**
     * Method play to start the {@link Game}, means requesting {@link Move}s from {@link Player}s, updating the {@link Board}
     * and visualizing the whole game
     *
     * @param timeOut integer timeOut, maximum number of {@link Move}s, after which the {@link Game} will be stopped
     * @return {@link Result} containing information about the winner of this {@link Game}
     * @throws RemoteException if an error occurs with the connection in a network game
     * @throws Exception if an error occurs in the {@link Board} or {@link View} object
     */
    public Result play(int timeOut, PlayerColor turn) throws Exception {
        if(debug)
            System.out.println(debugMsg);
        /*set redPlayer as first player, red as first color, and counter of move*/
        Player currentPlayer = turn == RED ? redPlayer : bluePlayer;
        PlayerColor currentColor = turn;
        Move currentMove;
        int moveCounter = 0;
        

        /*if a view object exists, so visualization is wanted, do this*/
        if(hasView) {
            view.visualize();
        }

        /*as long as a valid move has been made and none of the players did not win, ask for next moves*/
        while (board.getStatus() == OK && ((timeOut != 0 && moveCounter < timeOut) || timeOut == 0)) {
            if (debug)
                System.out.println(debugMsg);
            /*increment move count*/
            moveCounter++;
            /*output turn*/
            if(hasView)
                view.display(currentColor + "'s turn\n" + "Round No.: " + (moveCounter == 1 ? "1" : moveCounter/2));

            /*get a move from current player*/
            currentMove = currentPlayer.request();

            /*make move on board*/
            board.makeMove(currentMove);
            

            /*if debug mode is enabled output information*/
            if (debug && currentMove != null && hasView) {
                view.display(currentColor + "'move :" + currentMove);
                view.display("Status: " + board.getStatus());
            }
            if(currentMove == null && view.getSave() == true) {
                saveGame.export(view.getSaveGameName());
            }
            saveGame.add(currentMove);
            /*check if boardstatus of player is equal to own boardstatus*/
            currentPlayer.confirm(board.getStatus());
            /*switch current player and playercolor*/
            currentPlayer = currentPlayer == redPlayer ? bluePlayer : redPlayer;
            currentColor = currentColor == RED ? BLUE : RED;

            /*inform player about last move of opponent player*/
            currentPlayer.update(currentMove, board.getStatus());

            /*if delay time is set, wait*/
            try {
                Thread.sleep(delayTime);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            /*if view object has been added, visualized game*/
            if(hasView) {
                view.visualize();
            }
        }

        /*if game ended with timeout, create result object with winner == null, indicating this case*/
        if (moveCounter == timeOut) {
            return new Result(moveCounter, WinType.TIMEOUT);
        }
        /*create result object with playercolor, number of moves and type of win*/
        if (board.getStatus() == RED_WIN) {
            return new Result(RED, winnerMoves(moveCounter), board.getWinType());
        }
        else if (board.getStatus() == BLUE_WIN) {
            return new Result(BLUE, winnerMoves(moveCounter), board.getWinType());
        }
        else {
            return new Result(currentColor, winnerMoves(moveCounter), board.getWinType());
        }

    }

    /**
     * Method winnerMoves to calculate number of moves of the winner
     *
     * @param combinedMoveCounter total number of move from both player
     * @return integer number of moves winner needed to win
     */
    private int winnerMoves(int combinedMoveCounter) {
        return (int) (Math.ceil((double) combinedMoveCounter/2.0));
    }

    /**
     * This Method uses the {@link Save} to replay a game
     * @param save the Saveobject to be replayed
     */
    private void replay(Save save) {
        for(int i = 0; i < save.getHistorySize(); i++) {
            Status stat = board.makeMove(save.getNextMove());
        }
    }

    /**
     * returns the the playercolor of the player, who has to play now
     */
    public PlayerColor turn () {
        return board.getTurn();
    }
}
