package chess;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MinMax {

    boolean color;
    int maxDepth;
    Random rand;

    public MinMax(boolean color, int maxDepth) {
        this.color = color;
        this.maxDepth = maxDepth;
        rand = new Random();
    }

	/*
	 * Alpha-beta pruning methods. These functions examine all possible moves for Player 1 (alpha) and responses by Player 2 (beta).
	 * The objective is to minimize the beta score while maximizing alpha. When we come to a beta that can't possibly exceed alpha,
	 * we need not explore the remaining moves any further.
	 */
    private float maxValue(Board b, ArrayList<Move> state, float alpha, float beta, int depth) {
        // Per depth constraints, we cannot explore any deeper.
		if(depth > maxDepth)
            return powerIndex(b, state, color);

		// Obtain all possible moves for this color based on board configuration.
        ArrayList<Move> moves = b.getMovesAfter(color, state);
		// No possible move.
        if(moves.size() == 0)
            return Float.NEGATIVE_INFINITY;

		// Explore the board one set of moves at a time.
		// We pass back and forth between min/max to determine alpha and beta values.
        for(int i = 0; i < moves.size(); i++) {
            state.add(moves.get(i));
            float tmp = minValue(b, state, alpha, beta, depth + 1);
            state.remove(state.lastIndexOf(moves.get(i)));
            if(tmp > alpha) {
                alpha = tmp;
            }

			// Pruning.
			// When beta can no longer exceed alpha, there is no need to continue examining this portion of the moves List.
            if(beta <= alpha)
                break;
        }

        return alpha;
    }

	// Veritably symmetrical functionality to maxValue.
    private float minValue(Board b, ArrayList<Move> state, float alpha, float beta, int depth) {
        if(depth > maxDepth)
            return powerIndex(b, state, !color);

        ArrayList<Move> moves = b.getMovesAfter(!color, state);
        if(moves.size() == 0) 
            return Float.POSITIVE_INFINITY;

        for(int i = 0; i < moves.size(); i++) {
            state.add(moves.get(i));
            float tmp = maxValue(b, state, alpha, beta, depth + 1);
            state.remove(state.lastIndexOf(moves.get(i)));
            if(tmp < beta) {
                beta = tmp;
            }
			
            if(beta <= alpha)
                break;
        }

        return beta;
    }

    public Move decision(final Board b) {
        // Determine 'best' possible by concurrently evaluating all moves available.

		// Get all moves for this player.
        final ArrayList<Move> moves = b.getMoves(color);
		// Cannot make a decision if no moves exist.
        if(moves.size() == 0)
            return null;

		// Container for values obtained from min/max with pruning.
		// This will be concurrently accessed, we must qualify the data type as such (Future).
        Vector<Future<Float>> costs = new Vector<Future<Float>>(moves.size());
        costs.setSize(moves.size());

		// Create a number of threads up to the amount contained in the list of possible moves.
		// Depending on hardware limitations, this drastically improves decision making time.
        ExecutorService exec = Executors.newFixedThreadPool(moves.size());
        try {
            for (int i = 0; i < moves.size(); i++) {
                final Move move = moves.get(i);
				// The values to be calculated in parallel.
                Future<Float> result = exec.submit(new Callable<Float>() {

                    @Override
                    public Float call() {
                        ArrayList<Move> state = new ArrayList<Move>();
                        state.add(move);

						// Begin numerical analysis of move options with a call to min.
						// Viable moves will be whittled down based on min/max evaluation.
                        float tmp = minValue(b, state, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 1);
                        return tmp;
                    }
                });
                costs.set(i, result);
            }
        } finally {
            exec.shutdown();
        }

        // Search costs Vector for best option.
		// Positon of best
        int maxPos = -1;
		// Actual best
        float max = Float.NEGATIVE_INFINITY;
        for(int i = 0; i < moves.size(); i++) {
            float cost;
            try {
                cost = costs.get(i).get();
            } catch (Exception e) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e1) {
                }
                continue;
            }
			// Randomization to help prevent stalemates, identical moves being chosen every game.
            if(cost >= max) {
                if(Math.abs(cost-max) < 0.1) 
                    if(rand.nextBoolean())
                        continue;

                max = cost;
                maxPos = i;
            }
        }

        return moves.get(maxPos);
    }

    public Move SingleThreadDecision(Board b) {
        // Same idea as the concurrently implemented decision().
		// This function considers each move and resultant board state serially.

        ArrayList<Move> moves = b.getMoves(color);
        ArrayList<Move> state = new ArrayList<Move>();
        float[] costs = new float[moves.size()];
        for(int i = 0; i < moves.size(); i++) {
            state.add(moves.get(i));
            float tmp = minValue(b, state, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 1);
            costs[i] = tmp;
            state.remove(state.lastIndexOf(moves.get(i)));
        }

        int maxPos = -1;
        float max = Float.NEGATIVE_INFINITY;
        for(int i = 0; i < moves.size(); i++) {
            if(costs[i] >= max) {
                if(Math.abs(costs[i]-max) < 0.1)
                    if(rand.nextBoolean())
                        continue;

                max = costs[i];
                maxPos = i;
            }
        }

        if(maxPos == -1)
            return null;
        else
            return moves.get(maxPos);
    }

	// This function attempts to evaluate standing in the game based on values assigned to pieces and their positioning.
	// (From the perspective of parameter currentColor)
    private float powerIndex(Board b, ArrayList<Move> moves, boolean currentColor) {
        Tile[][] tiles = b.getTilesAfter(moves);

        if(b.getMoves(currentColor).size() == 0) {
            if(b.isCheckAfter(currentColor, moves))
                return (currentColor == this.color) ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
            else
                return Float.NEGATIVE_INFINITY;
        }

        int whiteScore = 0;
        int blackScore = 0;
		int[] scores = {whiteScore, blackScore};

		getScores(scores, tiles);
		whiteScore = scores[0];
		blackScore = scores[1];

        if(color == Piece.WHITE)
            return whiteScore - blackScore;
        else
            return blackScore - whiteScore;
    }

	public void getScores(int[] scores, Tile[][] tiles){
		// Tally 'scores' for both players.
		// 1st element of array represents white, 2nd holds black.
    	for(int i = 0; i < 8; i++) {
        	for(int j = 0; j < 8; j++) {
            	if(tiles[i][j].isOccupied()){ 
                	if(tiles[i][j].getPiece().getColor() == Piece.WHITE)
                    	scores[0] += tiles[i][j].getPiece().getValue();
                	else
                    	scores[1] += tiles[i][j].getPiece().getValue();
				}
        	}
		}
	}

} // MinMax
