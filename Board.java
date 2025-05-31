import java.util.ArrayList;
import java.util.List;

public class Board {
    private char[][] grid;
    private List<Piece> blackPieces;
    private List<Piece> whitePieces;
    private int movesSinceLastCapture;

    public List<Piece> getBlackPieces() {
        return blackPieces;
    }

    public List<Piece> getWhitePieces() {
        return whitePieces;
    }

    public int getMovesSinceLastCapture() {
        return movesSinceLastCapture;
    }

    public Board() {
        grid = new char[GameConstants.BOARD_SIZE][GameConstants.BOARD_SIZE];
        blackPieces = new ArrayList<>();
        whitePieces = new ArrayList<>();
        movesSinceLastCapture = 0;
        initializeBoard();
    }

    private void initializeBoard() {
        // Initialize empty board
        for (int i = 0; i < GameConstants.BOARD_SIZE; i++) {
            for (int j = 0; j < GameConstants.BOARD_SIZE; j++) {
                grid[i][j] = GameConstants.EMPTY;
            }
        }

        // Place black pieces (top) - 3 rows for standard checkers
        for (int i = 0; i < 3; i++) {
            for (int j = (i + 1) % 2; j < GameConstants.BOARD_SIZE; j += 2) {
                grid[i][j] = GameConstants.BLACK_PIECE;
                blackPieces.add(new Piece(GameConstants.BLACK_PIECE, i, j));
            }
        }

        // Place white pieces (bottom) - 3 rows for standard checkers
        for (int i = GameConstants.BOARD_SIZE - 3; i < GameConstants.BOARD_SIZE; i++) {
            for (int j = (i + 1) % 2; j < GameConstants.BOARD_SIZE; j += 2) {
                grid[i][j] = GameConstants.WHITE_PIECE;
                whitePieces.add(new Piece(GameConstants.WHITE_PIECE, i, j));
            }
        }
    }

    public void display() {
        System.out.println(GameConstants.GOLD + "  0 1 2 3 4 5 6 7" + GameConstants.RESET);
        for (int i = 0; i < GameConstants.BOARD_SIZE; i++) {
            System.out.print(GameConstants.GOLD + i + " " + GameConstants.RESET);
            for (int j = 0; j < GameConstants.BOARD_SIZE; j++) {
                if (grid[i][j] == GameConstants.BLACK_PIECE || grid[i][j] == GameConstants.BLACK_KING) {
                    System.out.print(GameConstants.BLACK + grid[i][j] + " " + GameConstants.RESET);
                } else if (grid[i][j] == GameConstants.WHITE_PIECE || grid[i][j] == GameConstants.WHITE_KING) {
                    System.out.print(GameConstants.WHITE + grid[i][j] + " " + GameConstants.RESET);
                } else {
                    System.out.print(grid[i][j] + " ");
                }
            }
            System.out.println();
        }
    }

    public boolean isJumpPossible(boolean isBlackTurn) {
        for (Piece piece : isBlackTurn ? blackPieces : whitePieces) {
            if (!getAllPossibleJumps(piece.getRow(), piece.getCol(), isBlackTurn).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public List<Move> getAllPossibleJumps(int row, int col, boolean isBlackTurn) {
        List<Move> jumps = new ArrayList<>();
        char piece = grid[row][col];
        int[][] directions = getValidDirections(piece);

        for (int[] dir : directions) {
            // Check single jumps
            int jumpRow = row + 2 * dir[0];
            int jumpCol = col + 2 * dir[1];
            int midRow = row + dir[0];
            int midCol = col + dir[1];

            if (isValidPosition(jumpRow, jumpCol) &&
                    grid[jumpRow][jumpCol] == GameConstants.EMPTY &&
                    isEnemy(grid[midRow][midCol], isBlackTurn)) {
                jumps.add(new Move(row, col, jumpRow, jumpCol));
            }

            // For kings, check longer jumps (flying kings)
            if (piece == GameConstants.BLACK_KING || piece == GameConstants.WHITE_KING) {
                for (int distance = 3; distance < GameConstants.BOARD_SIZE; distance++) {
                    jumpRow = row + distance * dir[0];
                    jumpCol = col + distance * dir[1];
                    if (!isValidPosition(jumpRow, jumpCol) ||
                            grid[jumpRow][jumpCol] != GameConstants.EMPTY) {
                        break;
                    }

                    // Check for exactly one enemy in the path
                    boolean foundEnemy = false;
                    for (int i = 1; i < distance; i++) {
                        int currentRow = row + i * dir[0];
                        int currentCol = col + i * dir[1];
                        if (grid[currentRow][currentCol] != GameConstants.EMPTY) {
                            if (isEnemy(grid[currentRow][currentCol], isBlackTurn) && !foundEnemy) {
                                foundEnemy = true;
                            } else {
                                foundEnemy = false;
                                break;
                            }
                        }
                    }

                    if (foundEnemy) {
                        jumps.add(new Move(row, col, jumpRow, jumpCol));
                    }
                }
            }
        }
        return jumps;
    }

    public int[][] getValidDirections(char pieceType) {
        if (pieceType == GameConstants.BLACK_PIECE) {
            return new int[][] { { 1, -1 }, { 1, 1 } };
        } else if (pieceType == GameConstants.WHITE_PIECE) {
            return new int[][] { { -1, -1 }, { -1, 1 } };
        } else { // Kings
            return new int[][] { { 1, -1 }, { 1, 1 }, { -1, -1 }, { -1, 1 } };
        }
    }

    public boolean isEnemy(char piece, boolean isBlackTurn) {
        if (isBlackTurn) {
            return piece == GameConstants.WHITE_PIECE || piece == GameConstants.WHITE_KING;
        } else {
            return piece == GameConstants.BLACK_PIECE || piece == GameConstants.BLACK_KING;
        }
    }

    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < GameConstants.BOARD_SIZE &&
                col >= 0 && col < GameConstants.BOARD_SIZE;
    }

    public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol, boolean isBlackTurn) {
        if (!isValidPosition(fromRow, fromCol) || !isValidPosition(toRow, toCol)) {
            return false;
        }

        char piece = grid[fromRow][fromCol];
        if ((isBlackTurn && (piece != GameConstants.BLACK_PIECE && piece != GameConstants.BLACK_KING)) ||
                (!isBlackTurn && (piece != GameConstants.WHITE_PIECE && piece != GameConstants.WHITE_KING))) {
            return false;
        }

        if (grid[toRow][toCol] != GameConstants.EMPTY) {
            return false;
        }

        int rowDiff = toRow - fromRow;
        int absRowDiff = Math.abs(rowDiff);
        int colDiff = toCol - fromCol;
        int absColDiff = Math.abs(colDiff);

        if (absRowDiff != absColDiff) {
            return false;
        }

        // Check piece movement direction for regular pieces
        if (piece == GameConstants.BLACK_PIECE && rowDiff <= 0) {
            return false;
        }
        if (piece == GameConstants.WHITE_PIECE && rowDiff >= 0) {
            return false;
        }

        // Check if jump is required
        boolean jumpAvailable = isJumpPossible(isBlackTurn);

        // For kings, check all squares along the path
        if (piece == GameConstants.BLACK_KING || piece == GameConstants.WHITE_KING) {
            if (absRowDiff > 1) {
                // Check for exactly one enemy in the path
                int enemyCount = 0;
                int rowStep = rowDiff > 0 ? 1 : -1;
                int colStep = colDiff > 0 ? 1 : -1;

                for (int i = 1; i < absRowDiff; i++) {
                    int currentRow = fromRow + i * rowStep;
                    int currentCol = fromCol + i * colStep;
                    char currentPiece = grid[currentRow][currentCol];

                    if (currentPiece != GameConstants.EMPTY) {
                        if (isEnemy(currentPiece, isBlackTurn)) {
                            enemyCount++;
                            if (enemyCount > 1)
                                return false;
                        } else {
                            return false; // Friendly piece in the way
                        }
                    }
                }

                if (jumpAvailable) {
                    return enemyCount == 1;
                }
                return enemyCount == 0;
            }
        }

        if (jumpAvailable) {
            if (absRowDiff != 2) {
                return false;
            }
            int midRow = (fromRow + toRow) / 2;
            int midCol = (fromCol + toCol) / 2;
            return isEnemy(grid[midRow][midCol], isBlackTurn);
        } else {
            return absRowDiff == 1;
        }
    }

    public void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        char piece = grid[fromRow][fromCol];
        grid[fromRow][fromCol] = GameConstants.EMPTY;
        grid[toRow][toCol] = piece;

        boolean isCapture = false;
        int rowDiff = toRow - fromRow;
        int absRowDiff = Math.abs(rowDiff);

        if (absRowDiff > 1) { // This is a jump or king slide capture
            if (piece == GameConstants.BLACK_KING || piece == GameConstants.WHITE_KING) {
                // For kings, remove all enemy pieces along the path
                int rowStep = rowDiff > 0 ? 1 : -1;
                int colStep = (toCol - fromCol) > 0 ? 1 : -1;

                for (int i = 1; i < absRowDiff; i++) {
                    int currentRow = fromRow + i * rowStep;
                    int currentCol = fromCol + i * colStep;
                    char currentPiece = grid[currentRow][currentCol];

                    if (currentPiece != GameConstants.EMPTY && isEnemy(currentPiece,
                            piece == GameConstants.BLACK_KING || piece == GameConstants.BLACK_PIECE)) {
                        grid[currentRow][currentCol] = GameConstants.EMPTY;
                        List<Piece> opponentPieces = (piece == GameConstants.BLACK_KING
                                || piece == GameConstants.BLACK_PIECE)
                                        ? whitePieces
                                        : blackPieces;
                        opponentPieces.removeIf(p -> p.getRow() == currentRow && p.getCol() == currentCol);
                        isCapture = true;
                    }
                }
            } else {
                // Regular piece jump
                int midRow = (fromRow + toRow) / 2;
                int midCol = (fromCol + toCol) / 2;
                grid[midRow][midCol] = GameConstants.EMPTY;
                List<Piece> opponentPieces = (piece == GameConstants.BLACK_PIECE || piece == GameConstants.BLACK_KING)
                        ? whitePieces
                        : blackPieces;
                opponentPieces.removeIf(p -> p.getRow() == midRow && p.getCol() == midCol);
                isCapture = true;
            }
        }

        if (isCapture) {
            movesSinceLastCapture = 0;
        } else {
            movesSinceLastCapture++;
        }

        // Update piece in the list
        List<Piece> pieces = (piece == GameConstants.BLACK_PIECE || piece == GameConstants.BLACK_KING)
                ? blackPieces
                : whitePieces;
        pieces.stream()
                .filter(p -> p.getRow() == fromRow && p.getCol() == fromCol)
                .findFirst()
                .ifPresent(p -> p.move(toRow, toCol));

        // Promote to king if reached the end
        if ((piece == GameConstants.BLACK_PIECE && toRow == GameConstants.BOARD_SIZE - 1) ||
                (piece == GameConstants.WHITE_PIECE && toRow == 0)) {
            grid[toRow][toCol] = (piece == GameConstants.BLACK_PIECE)
                    ? GameConstants.BLACK_KING
                    : GameConstants.WHITE_KING;
            pieces.stream()
                    .filter(p -> p.getRow() == toRow && p.getCol() == toCol)
                    .findFirst()
                    .ifPresent(Piece::promoteToKing);
        }
    }

    public boolean canContinueJumping(int row, int col, boolean isBlackTurn) {
        return getAllPossibleJumps(row, col, isBlackTurn).size() > 0;
    }

    public char[][] getGrid() {
        return grid;
    }
}