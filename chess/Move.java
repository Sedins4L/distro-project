/*
 * Spencer Gabhart, Sam Bland
 */

package chess;

public class Move {
	// Members to represent board positioning
    private int x1, y1, x2, y2;

	// Constructor with destination parameters
    public Move(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

	// Getters and setters
    public int getX1() {
        return x1;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getY1() {
        return y1;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

	// Standard chess grid representation; A1 thru H8
    public String toString(){ 
        return (char)('A'+x1) + "" + (y1+1) + " " + (char)('A'+x2) + "" + (y2+1);
    }

	// Used to check for duplicates when generating list of possible moves
    public boolean equals(Object o){
        Move op = (Move) o;

        if(op.getX1() == x1 && op.getY1() == y1 && op.getX2() == x2 && op.getY2() == y2){
            return true;
        }
        else
            return false;
    }

} // Move
