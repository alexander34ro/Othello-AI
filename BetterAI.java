import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class BetterAI implements IOthelloAI {

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

     public int utility(GameState s) {
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

    public int maxValue(GameState s, int bestValueforMAX, int bestValueforMIN) {

        if (s.isFinished()) return utility(s);

        int value;
        int bestValue = Integer.MIN_VALUE;
        for (Position move : prioritizeMoves(s)) {
            value = minValue(result(s, move), bestValueforMAX, bestValueforMIN);
            if (value > bestValue) bestValue = value;
            if (value > bestValueforMAX) bestValueforMAX = value;
            if (bestValueforMAX > bestValueforMIN) {
                //System.out.println("best " + bestValueforMAX + " > " + bestValueforMIN);
                break;
            }
        }
        if (s.legalMoves().isEmpty()) {
            bestValue = minValue(noMove(s), bestValueforMAX, bestValueforMIN);
        }
        return bestValue;
    }

    public int minValue(GameState s, int bestValueforMAX, int bestValueforMIN) {

        if (s.isFinished()) return utility(s);

        int value;
        int bestValue = Integer.MAX_VALUE;
        for (Position move : prioritizeMoves(s)) {
            value = maxValue(result(s, move), bestValueforMAX, bestValueforMIN);
            if (value < bestValue) bestValue = value;
            if (value < bestValueforMIN) bestValueforMIN = value;
            if (bestValueforMAX > bestValueforMIN) {
                //System.out.println("best " + bestValueforMAX + " > " + bestValueforMIN);
                break;
            }
        }
        if (s.legalMoves().isEmpty()) {
            bestValue = maxValue(noMove(s), bestValueforMAX, bestValueforMIN);
        }
        return bestValue;
    }

	public Position decideMove(GameState s) {

        System.out.println("BetterAI moves:");

        int value;
        int bestValueforMAX = Integer.MIN_VALUE;
        int bestValueforMIN = Integer.MAX_VALUE;
        int bestValue = Integer.MIN_VALUE;
        Position bestMove = null;
        for (Position move : prioritizeMoves(s)) {
            value = minValue(result(s, move), bestValueforMAX, bestValueforMIN);
            System.out.println("value " + value + " Position " + move.row + " " + move.col);
            if (value > bestValue) {
                bestValue = value;
                bestMove = move;
            }
        }

        System.out.println(bestValue);

        return bestMove;
    }

    public double distance(int boardSize, Position p) {
        int half = boardSize/2;
        int distanceX = Math.abs(half - p.row);
        int distanceY = Math.abs(half - p.col);
        return Math.sqrt((distanceX * distanceX) + (distanceY * distanceY));
    }

    public double heuristic(int boardSize, Position p) {
        double cost = distance(boardSize, p);
        if (isCorner(boardSize, p)) cost = cost - boardSize;
        return cost;
    }

    public boolean isCorner(int boardSize, Position p) {
        int x = p.row;
        int y = p.col;
        return ((x == 0 || x == boardSize - 1) && (y == 0 || y == boardSize - 1)); 
    }

    public boolean isNearlyCorner(int boardSize, Position p) {
        return false;
    }

    public ArrayList<Position> prioritizeMoves(GameState s) {
        
        Comparator<Position> c = new Comparator<Position>() {
            
            public int compare(Position p1, Position p2) {
                double c1 = heuristic(s.getBoard().length, p1);
                double c2 = heuristic(s.getBoard().length, p2);
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
        System.out.println(str);
        return moves;
    }
}