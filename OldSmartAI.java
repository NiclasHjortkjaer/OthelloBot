
/**
 * A simple OthelloAI-implementation. The method to decide the next move just
 * returns the first legal move that it finds. 
 * @author Mai Ajspur
 * @version 9.2.2018
 */
public class OldSmartAI implements IOthelloAI{

	/**
	 * Returns first legal move
	 */
	public Position decideMove(GameState state){
        var m = MAXVALUE(state, Integer.MIN_VALUE, Integer.MAX_VALUE, -1, state.getPlayerInTurn());
        System.out.println("--- OLD AI decided on the move: ---");
        System.out.println(m.toString() + "\n\n");
        return m.move;
	}

    // function MAX-VALUE(game,state) returns (utility,move)
    // if game.IS-TERMINAL(state) then
    // return game.UTILITY(state,MAX), null
    // v ← -∞
    // for each a in game.ACTIONS(state) do
    // v2,a2 ← MIN-VALUE(game,game.RESULT(state,a))
    // if v2 > v then
    // v,move ← v2,a
    // return v,move
    
    public static UtilMove MAXVALUE(GameState state, int alpha, int beta, int counter, int player) {
        if (state.isFinished() || counter > 3) {
            return new UtilMove(Utility(state, player), null);
        }
        int v = Integer.MIN_VALUE;
        var moves = state.legalMoves();
        Position move = null;

        for (var m : moves) {
            var newState = new GameState(state.getBoard(), state.getPlayerInTurn());
            if (!newState.insertToken(m)) break;
            
            //if it's the AI's turn, call MAXVALUE, else MINVALUE
            UtilMove utilMove = state.getPlayerInTurn() == player ? MAXVALUE(newState, alpha, beta, counter + 1, player) : MINVALUE(newState, alpha, beta, counter + 1, player);

            if (utilMove.util > v || move == null){
                v = utilMove.util;
                move = m;
                alpha = Math.max(alpha, v);
            }
            if (v >= beta) {
                var mo = new UtilMove(v, move);
                if (mo != null) System.out.println(mo);
                return mo;
            }
        }

        var m = new UtilMove(v, move);
        if (m != null) System.out.println(m);
        return m;
    }

    // function MIN-VALUE(game,state) returns (utility,move)
    // if game.IS-TERMINAL(state) then
    // return game.UTILITY(state,MAX), null
    // v ← +∞
    // for each a in game.ACTIONS(state) do
    // v2,a2 ← MAX-VALUE(game,game.RESULT(state,a))
    // if v2 < v then
    // v,move ← v2,a
    // return v,move

    public static UtilMove MINVALUE(GameState state, int alpha, int beta, int counter, int player) {
        if (state.isFinished() || counter > 3) {
            return new UtilMove(Utility(state, player), null);
        }
        int v = Integer.MAX_VALUE;
        var moves = state.legalMoves();
        Position move = null;

        for (var m : moves) {
            var newState = new GameState(state.getBoard(), state.getPlayerInTurn());
            if (!newState.insertToken(m)) break;
            
            UtilMove utilMove = state.getPlayerInTurn() == player ? MAXVALUE(newState, alpha, beta, counter + 1, player) : MINVALUE(newState, alpha, beta, counter + 1, player);
            if (utilMove.util < v || move == null){
                v = utilMove.util;
                move = m;
                beta = Math.min(beta, v);
            }
            if (v <= alpha) {
                var mo = new UtilMove(v, move);
                if (mo != null) System.out.println(mo);
                return mo;
            }
        }
        var m = new UtilMove(v, move);
        if (m != null) System.out.println(m);
        return m;
    }
	
    //Evaluater function
    public static int Utility(GameState state, int player) {
        var tokens = state.countTokens();
        var score = (player == 1 ? 1 : -1) * (tokens[0] - tokens[1]);
        if (state.isFinished())
            return score > 0 ? Integer.MAX_VALUE : (score < 0 ? Integer.MIN_VALUE : 0);

        return score;
    }
    
    static class UtilMove {
        int util;
        Position move;

        public UtilMove(int util, Position move) {
            this.util = util;
            this.move = move;
        }

        @Override
        public String toString() {
            return "Utility: " + util + ", Move: " + (move != null ? move.toString() : "null");
        }
    }
}

