package chess;

public class MinMaxPlayerSerial extends Player {
    MinMax mm;

    public MinMaxPlayerSerial(boolean color, int maxDepth) {
        super(color);
        mm = new MinMax(color, maxDepth);
    }

    public Move getNextMove(Board b) {
        Move move = mm.SingleThreadDecision(b);
        return move;
    }

}

