import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class BetterAI implements IOthelloAI {

    //// Utility stuff here
    //
    public class Move {
        public double utility;
        public Position position;

        public Move(double utility, Position position) {
            this.utility = utility;
            this.position = position;
        }
    }

     public GameState result(GameState s, Position position) {
         GameState newState = new GameState(s.getBoard(), s.getPlayerInTurn());
         newState.insertToken(position);
         return newState;
     }

     public GameState noMove(GameState s) {
        GameState newState = new GameState(s.getBoard(), s.getPlayerInTurn());
        newState.changePlayer();
        return newState;
     }

     public double utility(GameState s) {
         int[] tokens = s.countTokens();
         // Player 1 won, we lost
         if (tokens[0] > tokens[1]) {
             return -1;
         // Player 2 won, that's us
         } else if (tokens[0] < tokens[1]) {
             return 1;
         // Draw
         } else {
             return 0;
         }
     }

     //// Evaluation function starts here
     //
     public double hUtility(GameState s) {
        // number of tokens for each player
        int[] tokens = s.countTokens();

        // number of available moves for each player
        GameState nextS = noMove(s);
        int movesP1;
        int movesP2;
        if (s.getPlayerInTurn() == 1) {
            movesP1 = s.legalMoves().size();
            movesP2 = nextS.legalMoves().size();
        } else {
            movesP1 = nextS.legalMoves().size();
            movesP2 = s.legalMoves().size();
        }

        // proportions
        double totalSpace = s.getBoard().length * s.getBoard().length;
        double tokensProportion = 0.1;
        double movesProportion = 0.9;

        double hPlayer1 = tokens[0] * tokensProportion + movesP1 * movesProportion;
        double hPlayer2 = tokens[1] * tokensProportion + movesP2 * movesProportion;

        return (hPlayer2 - hPlayer1) / totalSpace;
    }

    //// MINIMAX with alpha-beta pruning starts here
    //
    public Move maxValue(GameState s, Position position, double bestValueforMAX, double bestValueforMIN, int depth) {

        if (s.isFinished()) return new Move(utility(s), position);
        if (s.legalMoves().isEmpty()) return minValue(noMove(s), position, bestValueforMAX, bestValueforMIN, depth + 1);
        if (depth > 7) return new Move(hUtility(s), position);

        Position prePosition;
        Move newMove;
        Move bestMove = new Move(Integer.MIN_VALUE, position);
        for (Position move : prioritizeMoves(s)) {
            if (position == null) prePosition = move; else prePosition = position;
            newMove = minValue(result(s, move), prePosition, bestValueforMAX, bestValueforMIN, depth + 1);
            if (position == null) System.out.println("" + prePosition + " utility: " + newMove.utility);
            if (newMove.utility > bestMove.utility) bestMove = newMove;
            if (newMove.utility > bestValueforMAX) bestValueforMAX = newMove.utility;
            if (bestValueforMAX > bestValueforMIN) break;
        }
        return bestMove;
    }

    public Move minValue(GameState s, Position position, double bestValueforMAX, double bestValueforMIN, int depth) {

        if (s.isFinished()) return new Move(utility(s), position);
        if (s.legalMoves().isEmpty()) return maxValue(noMove(s), position, bestValueforMAX, bestValueforMIN, depth + 1);
        if (depth > 7) return new Move(hUtility(s), position);

        Position prePosition;
        Move newMove;
        Move bestMove = new Move(Integer.MAX_VALUE, position);
        for (Position move : prioritizeMoves(s)) {
            if (position == null) prePosition = move; else prePosition = position;
            newMove = maxValue(result(s, move), prePosition, bestValueforMAX, bestValueforMIN, depth + 1);
            if (newMove.utility < bestMove.utility) bestMove.utility = newMove.utility;
            if (newMove.utility < bestValueforMIN) bestValueforMIN = newMove.utility;
            if (bestValueforMAX > bestValueforMIN) break;
        }
        return bestMove;
    }

	public Position decideMove(GameState s) {
        System.out.println("BetterAI thinks...");
        Move bestMove = maxValue(s, null, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
        System.out.println("BetterAI moves: " + bestMove.position + " Utility:  " + bestMove.utility);
        return bestMove.position;
    }

    //// Moves ordering heuristic starts here
    //
    public double distance(int boardSize, Position p) {
        int half = boardSize/2;
        int distanceX = Math.abs(half - p.row);
        int distanceY = Math.abs(half - p.col);
        return Math.sqrt((distanceX * distanceX) + (distanceY * distanceY));
    }

    public double orderingHeuristic(int boardSize, Position p) {
        double cost = distance(boardSize, p);
        if (isCorner(boardSize, p)) cost = cost - boardSize;
        return cost;
    }

    public boolean isCorner(int boardSize, Position p) {
        int x = p.row;
        int y = p.col;
        return ((x == 0 || x == boardSize - 1) && (y == 0 || y == boardSize - 1)); 
    }

    // killer moves heuristic, best moves are tried first
    public ArrayList<Position> prioritizeMoves(GameState s) {
        
        Comparator<Position> c = new Comparator<Position>() {
            
            public int compare(Position p1, Position p2) {
                double c1 = orderingHeuristic(s.getBoard().length, p1);
                double c2 = orderingHeuristic(s.getBoard().length, p2);
                if(c1 > c2) return 1;
                else if (c1 < c2) return -1;
                c1 = hUtility(result(s, p1));
                c2 = hUtility(result(s, p2));
                if(c1 > c2) return 1;
                else if (c1 < c2) return -1;
                return 0;
            }
        };

        ArrayList<Position> moves = s.legalMoves();
        Collections.sort(moves, c);
        String str = "moves ";
        for (Position position : moves) {
            str = str + position + " ";
        }
        // System.out.println(str);
        return moves;
    }
}