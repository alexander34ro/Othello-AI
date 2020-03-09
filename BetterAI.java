import java.util.ArrayList;
import java.util.Queue;

public class BetterAI implements IOthelloAI {

    // add a structre for the MINIMAX tree
    // Tree tree;

    // public BetterAI(int size) {
    //     // build tree with utility functions
    //     tree = new Tree();
    //     GameState s = new GameState(size, 1); // generate initial state
    //     Queue<GameState> states_to_evaluate = new Queue(s); // queue with the initial state
    //     while (!states_to_evaluate.isEmpty()) {
    //         GameState currentGameState = states_to_evaluate.remove();
    //         ArrayList<Position> moves = currentGameState.legalMoves();
    //         for (Position position : moves) {
    //             GameState newGameState = currentGameState..insertToken(position);
    //         }
    //         if (currentGameState.getPlayerInTurn() == 1) {
                
    //         } else {

    //         }
    //     }
    // }



	public Position decideMove(GameState s) {
        
        ArrayList<Position> moves = s.legalMoves();
        for (Position position : moves) {
            GameState newGameState = currentGameState.copy().insertToken(position);
            
        }



        // if (s.isFinished()) {
        //     int tokensP1 = s.countTokens()[0];
        //     int tokensP2 = s.countTokens()[1];
        //     if (tokensP1 > tokensP2) {
        //         return -1;
        //     } 
        //     else if (tokensP1 < tokensP2) {
        //         return 1;
        //     }
        //     else {
        //         return 0;
        //     }
        // }





        return new Position(0, 0);
    }
}