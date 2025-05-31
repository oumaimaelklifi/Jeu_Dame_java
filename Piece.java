public class Piece {
    private char type;
    private int row;
    private int col;
    private boolean isKing;

    public Piece(char type, int row, int col) {
        this.type = type;
        this.row = row;
        this.col = col;
        this.isKing = false;
    }

    public char getType() {
        return type;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isKing() {
        return isKing;
    }

    public void promoteToKing() {
        this.isKing = true;
        if (type == GameConstants.BLACK_PIECE) {
            type = GameConstants.BLACK_KING;
        } else {
            type = GameConstants.WHITE_KING;
        }
    }

    public void move(int newRow, int newCol) {
        this.row = newRow;
        this.col = newCol;
    }
}