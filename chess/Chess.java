package chess;

public class Chess {

    public static void main(String[] args) {
		// Begin 'clock' to track game length
        long startTime = System.currentTimeMillis();
        int iter = 3;
        float player1Score = 0;
        int draw = 0;
        for(int i = 0; i < iter; i++) {
            Board board = new Board();
            Player player1 = new MinMaxPlayer(Piece.WHITE,2);
            Player player2 = new MinMaxPlayer(Piece.BLACK,1);

            int winner = play(player1, player2, board);

            if(winner == 1)
                player1Score++;
            if(winner == 0) {
                player1Score += 0.5f;
                draw++;
            }
        }

        long elapsed = System.currentTimeMillis() - startTime;
        System.out.println(player1Score);
        System.out.println("Total time on single thread decisions: " + elapsed);

    }

    public static int play(Player player1, Player player2, Board b) {
        Move move;
        int result;
        int turn = 0;
        while(true) {
            if(turn++ > 200)
                return 0;

            move = player1.getNextMove(b);
            if(move == null && b.isCheck(player1.getColor())) // check and can't move
                return -1;
            if(move == null) // no check but can't move
                return 0;

			// Uncomment print statements of b to see play transpire
            result = b.makeMove(move);
            // System.out.println(b);
            if(result == -1) return (player1.getColor() == Piece.WHITE) ? -1 : 1; // black wins
            if(result == 1) return (player1.getColor() == Piece.WHITE) ? 1 : -1; // white wins


            move = player2.getNextMove(b);
            if(move == null && b.isCheck(player2.getColor())) // check and can't move
                return 1;
            if(move == null) // no check but can't move
                return 0;

            result = b.makeMove(move);
            // System.out.println(b);
            if(result == -1) return (player1.getColor() == Piece.WHITE) ? 1 : -1; // black wins
            if(result == 1) return (player1.getColor() == Piece.WHITE) ? -1 : 1; // white wins

        }
    }

}
