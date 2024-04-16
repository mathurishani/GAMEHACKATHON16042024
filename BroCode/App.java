import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        int boardWidth = 420;
        int boardHeight = 640;

        JFrame frame = new JFrame("Maut ka khel");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Maut game = new Maut();
        frame.add(game);
        frame.pack();
        game.requestFocus();
        frame.setVisible(true);

        // Leaderboard
        while (!game.gameOver) {
            Thread.sleep(100); // Wait until the game is over
        }

        // Insert player's score into the leaderboard
        game.insert("Player", game.score);

        // Display the leaderboard
        game.displayLeaderboard();
    }
}
