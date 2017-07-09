package towerwarspp.player.ai;

import towerwarspp.board.Board;
import towerwarspp.preset.Move;
import towerwarspp.preset.PlayerColor;

import java.util.Random;
import java.util.Vector;

import static towerwarspp.preset.PlayerColor.*;
import static towerwarspp.preset.Status.*;

/**
 * Created by robin on 07.07.17.
 */
public class SimRndGame {
    private Board board;
    PlayerColor turn;
    PlayerColor originalPlayer;

    Random random;

    public SimRndGame(Board board, Move move, PlayerColor turn) {
        this.board = board;
        this.turn = turn;
        this.originalPlayer = turn;

        this.board.update(move, turn);
        this.turn = this.turn == RED ? BLUE : RED;

        random = new Random();
    }

    public boolean simulate() {
//        Vector<Move> moves;
        Move move = null;

        while (board.getStatus() == OK) {
//            moves = board.allPossibleMoves(turn);
            move = makeMove(turn);
            board.update(move, turn);
            if(board.getStatus() == OK)
                turn = turn == RED ? BLUE : RED;
        }

        if(board.getStatus() == ILLEGAL) {
            System.out.println(move);
            System.out.println("ILLEGAL Boardstatus");
        }

        // returns true if the originalPlayer made the winning move, false otherwise
        return originalPlayer == turn;
    }

    private Move makeMove(PlayerColor turn) {
        Vector<Move> moves = board.allPossibleMoves(turn);
        int maxScore = Board.LOSE;
        // create new Vector of moves that will always hold the Move objects that have the highest score
        Vector<Move> maxMoves = new Vector<>();

        for(Move move : moves) {
            // iterate over all possible moves and calculate their scores
            int score = board.verySimpleScoreMove(move, turn);
//            System.out.println("score " + score + "Move " + move);
            if(score == maxScore) {
                maxMoves.add(move);
            } else if(score > maxScore) {
                /*
                * If the score of the last evaluated move is higher than the current maximum score, clear the maxMoves
                * vector containing the prior best moves. Then add the new best move to the vector and update
                * the maxScore variable to new Score
                * */
                maxMoves.clear();
                maxMoves.add(move);
                maxScore = score;
                // if the score indicates that this move is a winning move, break out of the loop and return it
                if(score == Board.WIN) {
                    break;
                }
            }

        }
        return maxMoves.get(random.nextInt(maxMoves.size()));
    }
}
