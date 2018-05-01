package chess;

import java.util.ArrayList;

public class Board {
    public static final int a=0, b=1, c=2, d=3, e=4, f=5, g=6, h=7;

    private Tile[][] tiles;

    public Board(Tile[][] tiles) {
        this.tiles = tiles;
    }

    public Board() {
        // Initialize board
        boolean co = Piece.WHITE;
        tiles = new Tile[8][8];
		// Bottom row
        tiles[a][0] = new Tile(new Rook(co));
        tiles[b][0] = new Tile(new Knight(co));
        tiles[c][0] = new Tile(new Bishop(co));
        tiles[d][0] = new Tile(new Queen(co));
        tiles[e][0] = new Tile(new King(co));
        tiles[f][0] = new Tile(new Bishop(co));
        tiles[g][0] = new Tile(new Knight(co));
        tiles[h][0] = new Tile(new Rook(co));

		// Row of pawns
        for(int i = 0; i < 8; i++) {
            tiles[i][1] = new Tile(new Pawn(co));
        }

		// Empty field
        for(int i = 2; i < 7; i++) {
            for(int j = 0; j < 8; j++) {
                tiles[j][i] = new Tile();
            }
        }

		// Top row
        co = Piece.BLACK;
        tiles[a][7] = new Tile(new Rook(co));
        tiles[b][7] = new Tile(new Knight(co));
        tiles[c][7] = new Tile(new Bishop(co));
        tiles[d][7] = new Tile(new Queen(co));
        tiles[e][7] = new Tile(new King(co));
        tiles[f][7] = new Tile(new Bishop(co));
        tiles[g][7] = new Tile(new Knight(co));
        tiles[h][7] = new Tile(new Rook(co));

		// Row of (black) pawns
        for(int i = 0; i < 8; i++) {
            tiles[i][6] = new Tile(new Pawn(co));
        }
    }

	// Ascii representation of playing board.
	// a-h along x-axis, 1-8 along y-axis.
    public String toString() {
        String str = "";
        for(int i = 7; i >= 0; i--) {
            str += (i+1) + "  ";
            for(int j = 0; j < 8; j++) {
                str += tiles[j][i] + " ";
            }
            str += "\n";
        }

        str += "\n   a b c d e f g h";

        return str;
    }

    public ArrayList<Move> getMoves(boolean color) {
        return getMoves(color, true);
    }

	public void getKingCoords(boolean color, int[] xy, Tile[][] t){	
		for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++) {
                if(t[i][j].isOccupied() &&
                        t[i][j].getPiece().getColor() == color &&
                        t[i][j].getPiece().toString().equalsIgnoreCase("K")) {
                    xy[0] = i;
					xy[1] = j;
                }
            }
		}
	}

	// Determine whether this color is in check.
    public boolean isCheck(boolean color) {
        // Find the king first.	Set his coordinates to (x, y).
		int x = -1, y = -1;
		int[] coords = {x, y};
		getKingCoords(color, coords, tiles);
		x = coords[0];
		y = coords[1];

		// Obtain all possible moves of other color.
        ArrayList<Move> opponentMoves = getMoves(!color, false);

		// Iterate through other color's options.
		// If any of them end on this king's coords, this king is in check.
        for(int j = 0; j < opponentMoves.size(); j++) {
            if(opponentMoves.get(j).getX2() == x && opponentMoves.get(j).getY2() == y)
                return true;
        }

        return false;
    }

	// Similar to isCheck, but looks a move ahead.
    public boolean isCheckAfter(boolean color, ArrayList<Move> moves) {
        Tile[][] newTiles = getTilesAfter(moves);

        int x = -1, y = -1;
		int[] coords = {x, y};
		getKingCoords(color, coords, newTiles);
		x = coords[0];
		y = coords[1];

        ArrayList<Move> opponentMoves = getMovesAfter(!color, moves, false);

        for(int j = 0; j < opponentMoves.size(); j++) {
            if(opponentMoves.get(j).getX2() == x && opponentMoves.get(j).getY2() == y)
                return true;
        }

        return false;
    }

    public ArrayList<Move> getMoves(boolean color, boolean checkCheck) {
        ArrayList<Move> moves = new ArrayList<Move>();

		// Find all of this color's pieces, add their moves to container.
        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++) {
                if(tiles[i][j].isOccupied() &&
                        tiles[i][j].getPiece().getColor() == color) {
                    moves.addAll(tiles[i][j].getPiece().getMoves(this, i, j));
                }
            }

		// Determine if move is valid.
		// Specifically, that we're not moving into check.
        if(checkCheck) {
  			// Find king first.
            int x = -1, y = -1;
			int[] coords = {x, y};
			getKingCoords(color, coords, tiles);
			x = coords[0];
			y = coords[1];

            ArrayList<Move> removeThese = new ArrayList<Move>();
            for(int i = 0; i < moves.size(); i++) {
				// See if a move results in moving into check
                ArrayList<Move> checkThis = new ArrayList<Move>(moves.subList(i, i+1));
                ArrayList<Move> opponentMoves = getMovesAfter(!color, checkThis, false);

                int xUpdated = x, yUpdated = y;
                if(checkThis.get(0).getX1() == x && checkThis.get(0).getY1() == y) {
                    xUpdated = checkThis.get(0).getX2();
                    yUpdated = checkThis.get(0).getY2();
                }

				// Look ahead for opponent check opportunities
                for(int j = 0; j < opponentMoves.size(); j++) {
                    if(opponentMoves.get(j).getX2() == xUpdated && opponentMoves.get(j).getY2() == yUpdated)
                        removeThese.add(checkThis.get(0));
                }
            }

			// Discard these 'invalid' moves
            moves.removeAll(removeThese);
        }

        return moves;
    }

    public ArrayList<Move> getMovesAfter(boolean color, ArrayList<Move> moves) {
        return getMovesAfter(color, moves, true);
    }

    public ArrayList<Move> getMovesAfter(boolean color, ArrayList<Move> moves, boolean checkCheck) {

        Tile[][] temp = new Tile[8][8];
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                temp[x][y] = new Tile(this.tiles[x][y]);
            }
        }

        Board b = new Board(temp);

        for(int i = 0; i < moves.size(); i++)
            b.makeMove(moves.get(i));

        ArrayList<Move> futureMoves = b.getMoves(color, checkCheck);

        return futureMoves;
    }

    public Tile[][] getTilesAfter(ArrayList<Move> moves) {

        Tile[][] temp = new Tile[8][8];
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                temp[x][y] = new Tile(this.tiles[x][y]);
            }
        }

        Board b = new Board(temp);

        for(int i = 0; i < moves.size(); i++)
            b.makeMove(moves.get(i));

        Tile[][] temp2 = new Tile[8][8];
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                temp2[x][y] = new Tile(b.getTile(x, y));
            }
        }

        return temp2;
    }

	// Returns -1 if black wins, 1 if white wins. 0 if game continues.
    public int makeMove(Move m) {
        Tile oldTile = tiles[m.getX1()][m.getY1()];

        tiles[m.getX2()][m.getY2()] = tiles[m.getX1()][m.getY1()];
        tiles[m.getX1()][m.getY1()] = new Tile();

        // Pawn promotion. Assumes player would choose another queen.
        if(oldTile.getPiece().toString().equals("P") && m.getY2() == 8-1)
            tiles[m.getX2()][m.getY2()] = new Tile(new Queen(Piece.WHITE));

        if(oldTile.getPiece().toString().equals("p") && m.getY2() == 1-1)
            tiles[m.getX2()][m.getY2()] = new Tile(new Queen(Piece.BLACK));

        return 0;
    }

    public Tile getTile(int x, int y) {
        return tiles[x][y];
    }
} // Board
