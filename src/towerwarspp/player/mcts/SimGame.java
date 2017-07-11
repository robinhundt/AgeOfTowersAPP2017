//package towerwarspp.player.mcts;
//
//import towerwarspp.board.Board;
//import towerwarspp.preset.Move;
//import towerwarspp.preset.PlayerColor;
//import towerwarspp.preset.Status;
//
//import java.util.ArrayDeque;
//import java.util.Random;
//import java.util.Vector;
//
//import static towerwarspp.preset.PlayerColor.*;
//import static towerwarspp.preset.Status.*;
//
///**
// * Created by robin on 07.07.17.
// */
//class SimGame {
//    private Board board;
//    PlayerColor turn;
//    PlayerColor originalPlayer;
//
//    static Random random = new Random();
//
//    SimGame(Board board, OldNode oldNode) {
//
//    }
//
//    public static double simulate(Board board, ArrayDeque<Move> moveHistory, PlayerColor self) {
//        PlayerColor turn = self;
//
//        board = playHistory(board, moveHistory, self);
//        if(board.getStatus() != OK)
//            return checkWin(board.getStatus(), self);
//        // turn is the same after calling playHostory, because every hostory move is answered with a random move
//        while (board.getStatus() == OK) {
//            board.update(rndMove(board.allPossibleMoves(turn)), turn);
//            if(board.getStatus() == OK)
//                turn = turn == RED ? BLUE : RED;
//        }
//        return checkWin(board.getStatus(), self);
//    }
//
//    private static Board playHistory(Board board, ArrayDeque<Move> moveHistory, PlayerColor self) {
//        PlayerColor enemy = self == RED ? BLUE : RED;
//
//        for(Move move : moveHistory) {
//            System.out.println("playing " + move);
//            board.update(move, self);
//            board.update(rndMove(board.allPossibleMoves(enemy)), enemy);
//            if(board.getStatus() != OK)
//                return board;
//        }
//        return board;
//    }
//
//    static Move rndMove(Vector<Move> moves) {
//        return moves.get(random.nextInt(moves.size()));
//    }
//
////    static double checkWin(Status status, PlayerColor self) {
////
////        switch (status) {
////            case BLUE_WIN: return self == BLUE ? 1 : 0;
////            case RED_WIN: return self == RED ? 1 : 0;
////            default: throw new IllegalStateException("SimGame.checkWin. Board status can not be illegal.");
////        }
////    }
//
//
//
//    /*------------------------------------------------------------------------------*/
//
//
//
//
//
//    public double simulate() {
////        Vector<Move> moves;
//        Move move = null;
//
//        while (board.getStatus() == OK) {
//            Vector<Move> moves = board.allPossibleMoves(turn);
//            move = moves.get(random.nextInt(moves.size()));
////            System.out.println("making move " + move);
//            board.update(move, turn);
//            if(board.getStatus() == OK)
//                turn = turn == RED ? BLUE : RED;
//        }
//
//        if(board.getStatus() == ILLEGAL) {
//            System.out.println(move);
//            System.out.println("ILLEGAL Boardstatus");
//        }
//
//        // returns true if the originalPlayer made the winning move, false otherwise
//        if(originalPlayer == turn)
//            return 1;
//        else
//            return 0;
//    }
//
//    public double advSimulate() {
//        while (board.getStatus() == OK) {
//            board.update(makeMove(turn), turn);
//            if(board.getStatus() == OK)
//                turn = turn == RED ? BLUE : RED;
//        }
//
//        if(originalPlayer == turn)
//            return 1;
//        else
//            return 0;
//    }
//
//
//
//
//
//
//
//
//
//    private Move makeMove(PlayerColor turn) {
//        Vector<Move> moves = board.allPossibleMoves(turn);
//        int maxScore = Board.LOSE;
//        // create new Vector of moves that will always hold the Move objects that have the highest score
//        Vector<Move> maxMoves = new Vector<>();
//
//        for(Move move : moves) {
//            // iterate over all possible moves and calculate their scores
//            int score = board.altScore(move, turn);
////            System.out.println("score " + score + "Move " + move);
//            if(score == maxScore) {
//                maxMoves.add(move);
//            } else if(score > maxScore) {
//                /*
//                * If the score of the last evaluated move is higher than the current maximum score, clear the maxMoves
//                * vector containing the prior best moves. Then add the new best move to the vector and update
//                * the maxScore variable to new Score
//                * */
//                maxMoves.clear();
//                maxMoves.add(move);
//                maxScore = score;
//                // if the score indicates that this move is a winning move, break out of the loop and return it
//                if(score == Board.WIN) {
//                    break;
//                }
//            }
//
//        }
//        return maxMoves.get(random.nextInt(maxMoves.size()));
//    }
//}
