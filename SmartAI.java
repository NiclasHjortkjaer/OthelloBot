
/**
 * A simple OthelloAI-implementation. The method to decide the next move just
 * returns the first legal move that it finds. 
 * @author Mai Ajspur
 * @version 9.2.2018
 */
public class SmartAI implements IOthelloAI{

    private static int MAX_DEPTH = 7;

	/**
	 * Returns first legal move
	 */
	public Position decideMove(GameState state){
        var m = MAXVALUE(state, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, state.getPlayerInTurn());
        System.out.println("--- Smart AI decided on the move: ---");
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
        if (state.isFinished() || counter > MAX_DEPTH) {
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
        if (state.isFinished() || counter > MAX_DEPTH) {
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

        //Early game you want as few tokens as possible
        if ((tokens[0] + tokens[1]) < Math.pow(state.getBoard().length, 2) / 3) 
            score *= -1;
        
        var board = state.getBoard();
        var boardX = board.length - 1;
        var boardY = board[0].length - 1;

        var scores = new int[3];

        //corners +10 utility
        scores[board[0][0]] += 9;
        
        scores[board[boardX][0]] += 9;

        scores[board[0][boardY]] += 9;

        scores[board[boardX][boardY]] += 9;

        //squares beside corners -5 utility
        if (board[0][0] != player){
            scores[board[0][1]] -= 6;
            scores[board[1][1]] -= 6;
            scores[board[1][0]] -= 6;
        }
        
        if (board[boardX][0] != player) {
            scores[board[boardX][1]] -= 6;
            scores[board[boardX - 1][1]] -= 6;
            scores[board[boardX - 1][0]] -= 6;
        }

        if (board[0][boardY] != player) {
            scores[board[1][boardY]] -= 6;
            scores[board[1][boardY - 1]] -= 6;
            scores[board[0][boardY - 1]] -= 6;
        }

        if (board[boardX][boardY] != player) {
            scores[board[boardX - 1][boardY]] -= 6;
            scores[board[boardX - 1][boardY - 1]] -= 6;
            scores[board[boardX][boardY - 1]] -= 6;
        }

        //sides +3 utility
        for (int i = 2; i < boardX - 2; i++) {
            scores[board[i][0]] += 2;
            scores[board[0][i]] += 2;
            scores[board[boardX][i]] += 2;
            scores[board[i][boardY]] += 2;
        }

        if (board[0][0] != 0) {
            scores[board[0][1]] += 2;
            scores[board[1][0]] += 2;
        }

        if (board[boardX][0] != 0) {
            scores[board[boardX][1]] += 2;
            scores[board[boardX - 1][0]] += 2;
        }

        if (board[0][boardY] != 0) {
            scores[board[1][boardY]] += 2;
            scores[board[0][boardY - 1]] += 2;
        }

        if (board[boardX][boardY] != 0) {
            scores[board[boardX][boardY - 1]] += 2;
            scores[board[boardX - 1][boardY]] += 2;
        }

        if (player == 1) {
            score += scores[1];
            score -= scores[2];
        }
        else {
            score += scores[2];
            score -= scores[1];
        }
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

