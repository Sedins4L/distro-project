package chess;

public class Chess {

    public static void main(String[] args) {
		// Begin 'clock' to track game length
        long startTime = System.currentTimeMillis();
		// Play three games
        int iter = 3;
        float player1Score = 0;
        
        for(int i = 0; i < iter; i++) {
            Board board = new Board();
			// Give white a slight advantage with higher (lower?) depth
			// of search.
            Player player1 = new MinMaxPlayerSerial(Piece.WHITE,2);
            Player player2 = new MinMaxPlayerSerial(Piece.BLACK,1);

            int winner = play(player1, player2, board);

            if(winner == 1)
                player1Score++;
            if(winner == 0) {
                player1Score += 0.5f;
            }
        }

        long elapsed = System.currentTimeMillis() - startTime;
        System.out.println("Player 1 won " + player1Score + " game(s).");
        System.out.println("Total time: " + elapsed + "ms (single thread)");

		// Now let's play three games with multithreaded decision making.
		startTime = System.currentTimeMillis();
		player1Score = 0;

		for(int i = 0; i < iter; i++){	
            Board board = new Board();
			
            Player player1 = new MinMaxPlayer(Piece.WHITE,2);
            Player player2 = new MinMaxPlayer(Piece.BLACK,1);

            int winner = play(player1, player2, board);

            if(winner == 1)
                player1Score++;
            if(winner == 0) {
                player1Score += 0.5f;
            }
		}

		elapsed = System.currentTimeMillis() - startTime;
        System.out.println("Player 1 won " + player1Score + " game(s).");
        System.out.println("Total time: " + elapsed + "ms (concurrent)");
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
            System.out.println(b);
            if(result == -1) return (player1.getColor() == Piece.WHITE) ? -1 : 1; // black wins
            if(result == 1) return (player1.getColor() == Piece.WHITE) ? 1 : -1; // white wins


            move = player2.getNextMove(b);
            if(move == null && b.isCheck(player2.getColor())) // check and can't move
                return 1;
            if(move == null) // no check but can't move
                return 0;

            result = b.makeMove(move);
            System.out.println(b);
            if(result == -1) return (player1.getColor() == Piece.WHITE) ? 1 : -1; // black wins
            if(result == 1) return (player1.getColor() == Piece.WHITE) ? -1 : 1; // white wins

        }
    }

}
