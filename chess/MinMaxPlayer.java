package chess;

public class MinMaxPlayer extends Player {
    MinMax mm;

    public MinMaxPlayer(boolean color, int maxDepth) {
        super(color);
        mm = new MinMax(color, maxDepth);
    }

    public Move getNextMove(Board b) {
        Move move = mm.decision(b);
        return move;
    }

}
