import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        showVersionSelectionMenu();
    }

    private static void showVersionSelectionMenu() {
        String[] options = { "Graphical Interface", "Console Version" };

        int choice = JOptionPane.showOptionDialog(
                null,
                "Choose the game version:",
                "Checkers Game",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        switch (choice) {
            case 0 -> {
                MusicPlayer.playBackgroundMusic("Media/audio/city-bgm-336601.wav");
                launchGraphicalVersion();
            }
            case 1 -> {
                MusicPlayer.stopMusic();
                launchConsoleVersion();
            }
            default -> System.exit(0);
        }
    }

    private static void launchGraphicalVersion() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Standard Checkers Game"); // Updated title
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(700, 700); // Adjusted window size
            frame.setLocationRelativeTo(null);
            MainMenu.showMainMenu(frame);
            frame.setVisible(true);
        });
    }

    private static void launchConsoleVersion() {
        System.out.println(
                GameConstants.BOLD + GameConstants.GOLD + "=== Standard Checkers Game ===" + GameConstants.RESET);
        System.out.println("Board size: " + GameConstants.BOARD_SIZE + "x" + GameConstants.BOARD_SIZE);
        CheckersGame game = new CheckersGame();
        game.start();
    }
}