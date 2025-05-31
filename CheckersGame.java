import java.util.List;
import java.util.Scanner;

public class CheckersGame {
    public Board board;
    public Player blackPlayer;
    public Player whitePlayer;
    public boolean isBlackTurn;
    public Scanner scanner;
    public int lastToRow;
    public int lastToCol;

    public CheckersGame() {
        board = new Board();
        scanner = new Scanner(System.in);
        initializePlayers();
        isBlackTurn = true;
    }

    public void initializePlayers() {
        System.out.println(GameConstants.GOLD + "Welcome to Checkers!" + GameConstants.RESET);
        System.out.print("Enter Black player name: ");
        String blackName = scanner.nextLine();
        System.out.print("Enter White player name: ");
        String whiteName = scanner.nextLine();

        blackPlayer = new Player(blackName, true);
        whitePlayer = new Player(whiteName, false);
    }

    public void start() {
        while (!isGameOver()) {
            board.display();
            Player currentPlayer = isBlackTurn ? blackPlayer : whitePlayer;

            System.out.println(GameConstants.GOLD + "\n" + currentPlayer.getName() +
                    "'s turn (" + currentPlayer.getColorName() + ")" + GameConstants.RESET);

            boolean moveCompleted = false;
            while (!moveCompleted && !isGameOver()) {
                // Get piece selection
                int fromRow = -1, fromCol = -1;
                boolean validPieceSelected = false;

                while (!validPieceSelected) {
                    System.out.print("Select a piece (row col): ");
                    String input = scanner.nextLine().trim();

                    if (input.equalsIgnoreCase("quit")) {
                        System.out.println(GameConstants.GOLD + "Game ended." + GameConstants.RESET);
                        return;
                    }

                    try {
                        String[] parts = input.split("\\s+");
                        if (parts.length != 2) {
                            throw new IllegalArgumentException();
                        }

                        fromRow = Integer.parseInt(parts[0]);
                        fromCol = Integer.parseInt(parts[1]);

                        // Validate coordinates
                        if (!board.isValidPosition(fromRow, fromCol)) {
                            System.out.println("\u001B[31mInvalid coordinates! Values must be between 0 and " +
                                    (GameConstants.BOARD_SIZE - 1) + "\u001B[0m");
                            continue;
                        }

                        // Check if piece belongs to current player
                        char piece = board.getGrid()[fromRow][fromCol];
                        if ((isBlackTurn && (piece != GameConstants.BLACK_PIECE && piece != GameConstants.BLACK_KING))
                                ||
                                (!isBlackTurn
                                        && (piece != GameConstants.WHITE_PIECE && piece != GameConstants.WHITE_KING))) {
                            System.out.println("\u001B[31mThat's not your piece!\u001B[0m");
                            continue;
                        }

                        // Check if piece has valid moves
                        if (!hasValidMoves(new Piece(piece, fromRow, fromCol), isBlackTurn)) {
                            System.out.println(
                                    "\u001B[31mSelected piece has no valid moves! Choose another piece.\u001B[0m");
                            continue;
                        }

                        validPieceSelected = true;

                        // Show possible moves
                        System.out.println("Possible moves for this piece:");
                        showPossibleMoves(fromRow, fromCol, isBlackTurn);

                    } catch (Exception e) {
                        System.out.println("\u001B[31mInvalid input format! Use: row col (0-" +
                                (GameConstants.BOARD_SIZE - 1) + ")\u001B[0m");
                    }
                }

                // Get move input
                System.out.print("Enter your move (toRow toCol): ");
                String moveInput = scanner.nextLine().trim();

                if (moveInput.equalsIgnoreCase("quit")) {
                    System.out.println(GameConstants.GOLD + "Game ended." + GameConstants.RESET);
                    return;
                }

                try {
                    String[] parts = moveInput.split("\\s+");
                    if (parts.length != 2) {
                        throw new IllegalArgumentException();
                    }

                    int toRow = Integer.parseInt(parts[0]);
                    int toCol = Integer.parseInt(parts[1]);

                    // Validate move
                    if (board.isValidMove(fromRow, fromCol, toRow, toCol, isBlackTurn)) {
                        board.movePiece(fromRow, fromCol, toRow, toCol);
                        lastToRow = toRow;
                        lastToCol = toCol;

                        if (Math.abs(toRow - fromRow) == 2 && board.canContinueJumping(toRow, toCol, isBlackTurn)) {
                            System.out.println(GameConstants.GOLD + "You must continue jumping!" + GameConstants.RESET);
                            fromRow = toRow;
                            fromCol = toCol;
                            board.display();
                            System.out.println("Possible continuation jumps:");
                            showPossibleMoves(fromRow, fromCol, isBlackTurn);
                        } else {
                            moveCompleted = true;
                            isBlackTurn = !isBlackTurn;
                        }
                    } else {
                        System.out.println("\u001B[31mInvalid move! Try again.\u001B[0m");
                    }
                } catch (Exception e) {
                    System.out.println("\u001B[31mInvalid input format! Use: toRow toCol (0-" +
                            (GameConstants.BOARD_SIZE - 1) + ")\u001B[0m");
                }
            }
        }
        announceWinner();
    }

    private void showPossibleMoves(int row, int col, boolean isBlackTurn) {
        List<Move> jumps = board.getAllPossibleJumps(row, col, isBlackTurn);
        char piece = board.getGrid()[row][col];
        int[][] directions = board.getValidDirections(piece);

        if (!jumps.isEmpty()) {
            System.out.println("Jump moves:");
            for (Move move : jumps) {
                System.out.println("  -> " + move.getToRow() + " " + move.getToCol());
            }
        } else {
            System.out.println("Normal moves:");
            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];
                if (board.isValidPosition(newRow, newCol) &&
                        board.getGrid()[newRow][newCol] == GameConstants.EMPTY) {
                    System.out.println("  -> " + newRow + " " + newCol);
                }
            }
        }
    }

    public boolean isGameOver() {
        // Check if either player has no pieces
        if (board.getBlackPieces().isEmpty())
            return true;
        if (board.getWhitePieces().isEmpty())
            return true;

        // Check 40-move rule
        if (board.getMovesSinceLastCapture() >= 40)
            return true;

        // Check if current player can move
        return !canPlayerMove(isBlackTurn);
    }

    public boolean canPlayerMove(boolean isBlack) {
        List<Piece> pieces = isBlack ? board.getBlackPieces() : board.getWhitePieces();
        for (Piece piece : pieces) {
            if (hasValidMoves(piece, isBlack)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasValidMoves(Piece piece, boolean isBlack) {
        int row = piece.getRow();
        int col = piece.getCol();
        char pieceType = piece.getType();

        int[][] directions = board.getValidDirections(pieceType);
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (board.isValidPosition(newRow, newCol) &&
                    board.getGrid()[newRow][newCol] == GameConstants.EMPTY) {
                if (!board.isJumpPossible(isBlack)) {
                    return true;
                }
            }

            int jumpRow = row + 2 * dir[0];
            int jumpCol = col + 2 * dir[1];
            if (board.isValidPosition(jumpRow, jumpCol) &&
                    board.getGrid()[jumpRow][jumpCol] == GameConstants.EMPTY) {
                int midRow = row + dir[0];
                int midCol = col + dir[1];
                if (board.isEnemy(board.getGrid()[midRow][midCol], isBlack)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void announceWinner() {
        if (board.getBlackPieces().isEmpty()) {
            System.out.println(GameConstants.WHITE + whitePlayer.getName() + " (White) wins!" + GameConstants.RESET);
        } else if (board.getWhitePieces().isEmpty()) {
            System.out.println(GameConstants.BLACK + blackPlayer.getName() + " (Black) wins!" + GameConstants.RESET);
        } else if (board.getMovesSinceLastCapture() >= 40) {
            System.out.println(
                    GameConstants.GOLD + "Game ended in a draw (40 moves without capture)" + GameConstants.RESET);
        } else if (!canPlayerMove(true)) {
            System.out.println(
                    GameConstants.WHITE + whitePlayer.getName() + " (White) wins by stalemate!" + GameConstants.RESET);
        } else {
            System.out.println(
                    GameConstants.BLACK + blackPlayer.getName() + " (Black) wins by stalemate!" + GameConstants.RESET);
        }
    }
}