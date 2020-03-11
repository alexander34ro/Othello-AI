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

    public int maxValue(GameState s) {

        if (s.isFinished()) return utility(s);

        int value;
        int bestValue = Integer.MIN_VALUE;
        for (Position move : s.legalMoves()) {
            value = minValue(result(s, move));
            if (value > bestValue) bestValue = value;
        }
        if (s.legalMoves().isEmpty()) {
            bestValue = minValue(noMove(s));
        }
        return bestValue;
    }

    public int minValue(GameState s) {

        if (s.isFinished()) return utility(s);

        int value;
        int bestValue = Integer.MAX_VALUE;
        for (Position move : s.legalMoves()) {
            value = maxValue(result(s, move));
            if (value < bestValue) bestValue = value;
        }
        if (s.legalMoves().isEmpty()) {
            bestValue = maxValue(noMove(s));
        }
        return bestValue;
    }

	public Position decideMove(GameState s) {

        System.out.println("BetterAI moves:");

        int value;
        int bestValue = Integer.MIN_VALUE;
        Position bestMove = null;
        for (Position move : s.legalMoves()) {
            value = minValue(result(s, move));
            System.out.println("value " + value + " Position " + move.row + " " + move.col);
            if (value > bestValue) {
                bestValue = value;
                bestMove = move;
            }
        }

        System.out.println(bestValue);

        return bestMove;

         // explore potential future states using MINIMAX
//        Queue<GameState> states_to_evaluate = new Queue(s); // queue with the initial state
//        while (!states_to_evaluate.isEmpty()) {
//            GameState currentGameState = states_to_evaluate.remove();
//            ArrayList<Position> moves = currentGameState.legalMoves();
//            // find the move with best utility
//            for (Position position : moves) {
//                GameState newGameState =
//                        new GameState(currentGameState.board, currentGameState.currentPlayer).insertToken(position);
//
//            }
//            if (currentGameState.getPlayerInTurn() == 1) {
//
//            } else {
//
//            }
//        }
    }
}