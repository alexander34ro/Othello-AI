import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A better OthelloAI which implements Minimax search algorithm with Alpha-Beta pruning,
 * evaluation and cut off functions.
 * @author Alexandru Andrei Ardelean, Anna Liljeberg, Ugne Valionyte
 * @version 19.3.2020
 */
public class OthelloAI13 implements IOthelloAI {

    /**
     * Represents a move on the board to a given position and has a utility value.
     */
    public class Move {
        public double utility;
        public Position position;
        /**
         * Constructs a move consisting of it's target position and utility value.
         * @param utility The utility value of the position.
         * @param position Coordinates on the board stored in the Position object.
         */
        public Move(double utility, Position position) {
            this.utility = utility;
            this.position = position;
        }
    }
     /**
      * Makes a move by inserting a token on the board and the resulting new game state is returned.
      * @param s GameState before the new token is inserted.
      * @param position Position where the new tokens has to be inserted.
      * @return A new GameState.
      */
     public GameState result(GameState s, Position position) {
         GameState newState = new GameState(s.getBoard(), s.getPlayerInTurn());
         newState.insertToken(position);
         return newState;
     }
     /**
      * Changes the player when the current player has no more legal moves.
      * @param s Current GameState with the player who has no moves.
      * @return A new GameState with changed player.
      */
     public GameState noMove(GameState s) {
        GameState newState = new GameState(s.getBoard(), s.getPlayerInTurn());
        newState.changePlayer();
        return newState;
     }
     /**
      * Returns a utility value for terminal states based on the 
      * amount of tokens both players have.
      * @param s Current GameState.
      * @return -1.0 if player 2 lost, 0 if it's a draw, +1.0 if player 2 won
      */
     public double utility(GameState s) {
         int[] tokens = s.countTokens();
         if (tokens[0] > tokens[1]) {
             return -1;
         } else if (tokens[0] < tokens[1]) {
             return 1;
         } else {
             return 0;
         }
     }

     /**
      * Computes utility value for the moves using coin parity and mobility heuristics, 
      * given 0.1 and 0.9 weights respectively. Mobility heuristic gives higher values 
      * for positions which yield more legal moves. Coin parity gives higher values for 
      * positions which capture more tokens.
      * @param s Current GameState
      * @return An average weighted utility value 
      */
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

    /**
     * Minimax with Alpha-Beta pruning at Max point of view. 
     * @param s Current GameState
     * @param position Current Position
     * @param bestValueforMAX Alpha
     * @param bestValueforMIN Beta
     * @param depth Integer denoting the depth of the search tree
     * @return a Move
     */
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

    /**
     * Minimax with Alpha-Beta pruning at Min point of view.
     * @param s Current GameState
     * @param position Current Position
     * @param bestValueforMAX Alpha
     * @param bestValueforMIN Beta
     * @param depth Integer denoting the depth of the search tree
     * @return a Move
     */
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

    
    /**
     * Killer moves heuristic. Sorts the moves from having highest utility 
     * values to having lowest utility values.
     * @param s Current GameState
     * @return a sorted list of moves
     */
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
        return moves;
    }
    
    /**
     * Calculates path cost
     * @param p Position
     */
    public double orderingHeuristic(int boardSize, Position p) {
        double cost = distance(boardSize, p);
        if (isCorner(boardSize, p)) cost = cost - boardSize;
        return cost;
    }

    /**
     * Calculates the distance from the center of the board
     * @param p Position
     */
    public double distance(int boardSize, Position p) {
        int half = boardSize/2;
        int distanceX = Math.abs(half - p.row);
        int distanceY = Math.abs(half - p.col);
        return Math.sqrt((distanceX * distanceX) + (distanceY * distanceY));
    }

    /**
     * Checks whether a position is a corner on the game board
     * @param p Position
     * @return true if it's a corner, false otherwise
     */
    public boolean isCorner(int boardSize, Position p) {
        int x = p.row;
        int y = p.col;
        return ((x == 0 || x == boardSize - 1) && (y == 0 || y == boardSize - 1)); 
    }
}