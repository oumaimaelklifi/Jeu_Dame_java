import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



public class CheckersGUI extends JFrame {
    private Board board;
    private JPanel boardPanel;
    private JPanel infoPanel;
    private JLabel statusLabel;
    private JLabel blackScoreLabel;
    private JLabel whiteScoreLabel;
    private JLabel timerLabel;
    private JButton resetButton;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private boolean isBlackTurn = true;
    private boolean mustContinueJumping = false;
    private Timer gameTimer;
    private int blackTime = 0;
    private int whiteTime = 0;
    private ImageIcon soundOnIcon;
    private ImageIcon soundOffIcon;
    private JButton soundButton;

    public CheckersGUI() {
        board = new Board();
        initializeGUI();
        startTimer();
        updateBoard();
    }

    private void initializeGUI() {
        setTitle("Standard Checkers Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(700, 500);
        setLocationRelativeTo(null);

        // Initialize board panel
        boardPanel = new JPanel(new GridLayout(GameConstants.BOARD_SIZE, GameConstants.BOARD_SIZE));
        boardPanel.setPreferredSize(new Dimension(480, 480));

        // Info panel setup
        infoPanel = new JPanel(new BorderLayout());
        infoPanel.setPreferredSize(new Dimension(200, 480));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.setBackground(new Color(30, 30, 40));

        // Score panel
        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.Y_AXIS));
        scorePanel.setBackground(new Color(30, 30, 40));
        scorePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        blackScoreLabel = new JLabel("Black: 12 pieces", JLabel.CENTER);
        blackScoreLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        blackScoreLabel.setForeground(Color.WHITE);
        blackScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        whiteScoreLabel = new JLabel("White: 12 pieces", JLabel.CENTER);
        whiteScoreLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        whiteScoreLabel.setForeground(Color.WHITE);
        whiteScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        timerLabel = new JLabel("Time - Black: 0s", JLabel.CENTER);
        timerLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel whiteTimerLabel = new JLabel("White: 0s", JLabel.CENTER);
        whiteTimerLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        whiteTimerLabel.setForeground(Color.WHITE);
        whiteTimerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Sound button setup
        soundOnIcon = new ImageIcon("Media/images/sound_on.png");
        soundOffIcon = new ImageIcon("Media/images/sound_off.png");

        soundButton = new JButton(MusicPlayer.isMuted() ? soundOffIcon : soundOnIcon);
        soundButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        soundButton.setBorderPainted(false);
        soundButton.setContentAreaFilled(false);
        soundButton.setFocusPainted(false);
        soundButton.setOpaque(false);
        soundButton.setPreferredSize(new Dimension(60, 60));
        soundButton.addActionListener(e -> {
            MusicPlayer.toggleSound();
            soundButton.setIcon(MusicPlayer.isMuted() ? soundOffIcon : soundOnIcon);
        });

        JLabel soundLabel = new JLabel("Sound");
        soundLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        soundLabel.setForeground(Color.WHITE);
        soundLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        resetButton = new JButton("Reset Game");
        resetButton.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        resetButton.setBackground(new Color(70, 70, 90));
        resetButton.setForeground(Color.WHITE);
        resetButton.setFocusPainted(false);
        resetButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetButton.addActionListener(e -> resetGame());

        scorePanel.add(blackScoreLabel);
        scorePanel.add(whiteScoreLabel);
        scorePanel.add(Box.createVerticalStrut(10));
        scorePanel.add(timerLabel);
        scorePanel.add(whiteTimerLabel);
        scorePanel.add(Box.createVerticalStrut(20));
        scorePanel.add(soundLabel);
        scorePanel.add(soundButton);
        scorePanel.add(Box.createVerticalStrut(10));
        scorePanel.add(resetButton);

        // Status label
        statusLabel = new JLabel("Black's turn", JLabel.CENTER);
        statusLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(70, 70, 90));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        infoPanel.add(scorePanel, BorderLayout.CENTER);
        infoPanel.add(statusLabel, BorderLayout.SOUTH);

        add(boardPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.EAST);
    }

    private void startTimer() {
        if (gameTimer != null) {
            gameTimer.cancel();
        }
        gameTimer = new Timer();
        gameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    if (isBlackTurn) {
                        blackTime++;
                    } else {
                        whiteTime++;
                    }
                    updateTimerLabel();
                });
            }
        }, 1000, 1000);
    }

  public void updateTimerLabel() {
    timerLabel.setText("Time - Black: " + blackTime + "s");
    
    Component component = infoPanel.getComponent(0); // récupère le 1er composant
    if (component instanceof JPanel) {
        JPanel panel = (JPanel) component;
        Component whiteLabelComponent = panel.getComponent(4); // 5e composant dans ce panel
        if (whiteLabelComponent instanceof JLabel) {
            JLabel whiteLabel = (JLabel) whiteLabelComponent;
            whiteLabel.setText("White: " + whiteTime + "s");
        }
    }
}


    private void resetGame() {
        if (gameTimer != null) {
            gameTimer.cancel();
            gameTimer.purge();
        }
        board = new Board();
        isBlackTurn = true;
        mustContinueJumping = false;
        selectedRow = -1;
        selectedCol = -1;
        blackTime = 0;
        whiteTime = 0;
        startTimer();
        updateStatus();
        updateBoard();
    }

    private void updateBoard() {
        boardPanel.removeAll();

        // Update scores with actual piece counts
        blackScoreLabel.setText("Black: " + board.getBlackPieces().size() + " pieces");
        whiteScoreLabel.setText("White: " + board.getWhitePieces().size() + " pieces");

        for (int row = 0; row < GameConstants.BOARD_SIZE; row++) {
            for (int col = 0; col < GameConstants.BOARD_SIZE; col++) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(60, 60));

                // Set board colors
                Color baseColor = (row + col) % 2 == 0
                        ? new Color(210, 180, 140)
                        : new Color(139, 69, 19);
                button.setBackground(baseColor);

                // Highlight possible moves if piece is selected
                if (selectedRow != -1 && selectedCol != -1) {
                    if (board.isValidMove(selectedRow, selectedCol, row, col, isBlackTurn)) {
                        Color highlightColor = Math.abs(row - selectedRow) >= 2
                                ? new Color(50, 205, 50) // Green for captures
                                : new Color(144, 238, 144); // Light green for regular moves
                        button.setBackground(highlightColor);
                    }
                }

                char piece = board.getGrid()[row][col];
                if (piece == GameConstants.BLACK_PIECE || piece == GameConstants.BLACK_KING) {
                    button.setIcon(createPieceIcon(Color.BLACK, piece == GameConstants.BLACK_KING));
                } else if (piece == GameConstants.WHITE_PIECE || piece == GameConstants.WHITE_KING) {
                    button.setIcon(createPieceIcon(Color.WHITE, piece == GameConstants.WHITE_KING));
                }

                // Highlight selected piece with gold border
                if (row == selectedRow && col == selectedCol) {
                    button.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 3));
                } else {
                    button.setBorder(BorderFactory.createEmptyBorder());
                }

                final int currentRow = row;
                final int currentCol = col;
                button.addActionListener(e -> handleSquareClick(currentRow, currentCol));

                boardPanel.add(button);
            }
        }

        boardPanel.revalidate();
        boardPanel.repaint();
    }

    private ImageIcon createPieceIcon(Color color, boolean isKing) {
        int size = 50;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);
        g2d.fillOval(5, 5, size - 10, size - 10);

        if (isKing) {
            g2d.setColor(Color.YELLOW);
            g2d.fillPolygon(new int[] { size / 2 - 8, size / 2, size / 2 + 8 },
                    new int[] { size / 2 - 4, size / 2 + 8, size / 2 - 4 }, 3);
        }

        g2d.dispose();
        return new ImageIcon(image);
    }

    private void handleSquareClick(int row, int col) {
        if (mustContinueJumping && (row != selectedRow || col != selectedCol)) {
            if (board.isValidMove(selectedRow, selectedCol, row, col, isBlackTurn) &&
                    Math.abs(row - selectedRow) >= 2) {
                executeMove(row, col);
            }
            return;
        }

        if (selectedRow == -1) {
            char piece = board.getGrid()[row][col];
            boolean isBlackPiece = (piece == GameConstants.BLACK_PIECE || piece == GameConstants.BLACK_KING);
            boolean isWhitePiece = (piece == GameConstants.WHITE_PIECE || piece == GameConstants.WHITE_KING);

            if ((isBlackTurn && isBlackPiece) || (!isBlackTurn && isWhitePiece)) {
                selectedRow = row;
                selectedCol = col;
                updateBoard();
            }
        } else {
            if (board.isValidMove(selectedRow, selectedCol, row, col, isBlackTurn)) {
                executeMove(row, col);
            } else {
                selectedRow = -1;
                selectedCol = -1;
                updateBoard();
            }
        }
    }

    private void executeMove(int toRow, int toCol) {
        board.movePiece(selectedRow, selectedCol, toRow, toCol);

        if (Math.abs(toRow - selectedRow) >= 2 && board.canContinueJumping(toRow, toCol, isBlackTurn)) {
            mustContinueJumping = true;
            selectedRow = toRow;
            selectedCol = toCol;
            statusLabel.setText((isBlackTurn ? "Black" : "White") + " must continue jumping!");
        } else {
            mustContinueJumping = false;
            isBlackTurn = !isBlackTurn;
            selectedRow = -1;
            selectedCol = -1;
            updateStatus();
        }
        updateBoard();
        checkGameOver();
    }

    private void checkGameOver() {
        boolean blackCanMove = canPlayerMove(true);
        boolean whiteCanMove = canPlayerMove(false);

        if (board.getBlackPieces().isEmpty()) {
            endGame(createGameOverMessage("White Wins!", "All black pieces have been captured!", Color.WHITE));
        } else if (board.getWhitePieces().isEmpty()) {
            endGame(createGameOverMessage("Black Wins!", "All white pieces have been captured!", Color.BLACK));
        } else if (board.getMovesSinceLastCapture() >= 40) {
            endGame(createGameOverMessage("Game Draw!", "40 moves without capture", Color.GRAY));
        } else if (!blackCanMove) {
            endGame(createGameOverMessage("White Wins!", "Black has no valid moves left!", Color.WHITE));
        } else if (!whiteCanMove) {
            endGame(createGameOverMessage("Black Wins!", "White has no valid moves left!", Color.BLACK));
        }
    }

    private JPanel createGameOverMessage(String title, String message, Color color) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(30, 30, 40));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
        titleLabel.setForeground(color);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Message
        JLabel messageLabel = new JLabel(message, JLabel.CENTER);
        messageLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
        messageLabel.setForeground(Color.WHITE);

        // Button
        JButton button = new JButton("Play Again");
        button.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        button.setBackground(new Color(70, 70, 90));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.addActionListener(e -> resetGame());

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(messageLabel, BorderLayout.CENTER);
        panel.add(button, BorderLayout.SOUTH);

        return panel;
    }

    private boolean canPlayerMove(boolean isBlack) {
        List<Piece> pieces = isBlack ? board.getBlackPieces() : board.getWhitePieces();
        for (Piece piece : pieces) {
            if (hasValidMoves(piece, isBlack)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasValidMoves(Piece piece, boolean isBlack) {
        int row = piece.getRow();
        int col = piece.getCol();
        char pieceType = piece.getType();

        int[][] directions = board.getValidDirections(pieceType);
        for (int[] dir : directions) {
            // Check normal moves
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (board.isValidPosition(newRow, newCol) &&
                    board.getGrid()[newRow][newCol] == GameConstants.EMPTY) {
                if (!board.isJumpPossible(isBlack)) {
                    return true;
                }
            }

            // Check jumps
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

    private void endGame(JPanel messagePanel) {
        if (gameTimer != null) {
            gameTimer.cancel();
        }

        JOptionPane.showMessageDialog(
                this,
                messagePanel,
                "Game Over",
                JOptionPane.PLAIN_MESSAGE,
                null);

        resetGame();
    }

    private void updateStatus() {
        statusLabel.setText(isBlackTurn ? "Black's turn" : "White's turn");
        statusLabel.setBackground(isBlackTurn ? new Color(70, 70, 90) : new Color(100, 100, 120));
    }
}