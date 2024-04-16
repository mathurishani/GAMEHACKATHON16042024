package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SnakeGame extends JFrame implements KeyListener {
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;
    private static final int UNIT_SIZE = 69;
    private static final int GAME_UNITS = (WIDTH * HEIGHT) / UNIT_SIZE;
    private static final int DELAY = 200;
    private final List<Point> snake;
    private Point fruit;
    private char direction;
    private boolean running;
    private int score;
    private final int[][] maze;
    private final String username;
    private JLabel maxScoreLabel;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/snake_game_db1";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    public SnakeGame(String username) {
        this.username = username;
        setTitle("Snake Game");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        addKeyListener(this);

        snake = new ArrayList<>();
        direction = 'R'; // Start moving right initially
        running = true;
        score = 0;

        // Define the maze (1 for walls, 0 for empty space)
        maze = new int[][]{
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 0, 1, 1, 1, 1, 1, 1, 0, 1},
                {1, 0, 1, 0, 0, 0, 0, 1, 0, 1},
                {1, 0, 1, 0, 1, 1, 0, 1, 0, 1},
                {1, 0, 0, 0, 1, 0, 0, 0, 0, 1},
                {1, 0, 1, 0, 0, 0, 1, 0, 0, 1},
                {1, 0, 1, 1, 1, 1, 1, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1,0, 1, 1, 1, 0, 1, 1, 0, 1},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
        };

        // Create max score label
        maxScoreLabel = new JLabel("Max Score: ");
        maxScoreLabel.setForeground(Color.WHITE);
        maxScoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        maxScoreLabel.setBounds(10, 60, 200, 30);
        add(maxScoreLabel);

        startGame();

        setVisible(true);
    }

    private void startGame() {
        snake.clear();
        snake.add(new Point(UNIT_SIZE, UNIT_SIZE)); // Initial snake position
        placeFruit();

        Timer timer = new Timer(DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (running) {
                    move();
                    checkCollision();
                    checkFruit();
                    repaint();
                }
            }
        });
        timer.start();
    }

    private void move() {
        Point head = snake.get(0);
        Point newHead = new Point(head);

        switch (direction) {
            case 'U':
                newHead.y -= UNIT_SIZE;
                break;
            case 'D':
                newHead.y += UNIT_SIZE;
                break;
            case 'L':
                newHead.x -= UNIT_SIZE;
                break;
            case 'R':
                newHead.x += UNIT_SIZE;
                break;
        }

        snake.add(0, newHead);
        snake.remove(snake.size() - 1);
    }

    private void checkCollision() {
        Point head = snake.get(0);

        if (head.x < 0 || head.x >= WIDTH || head.y < 0 || head.y >= HEIGHT) {
            gameOver();
        }

        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                gameOver();
                break;
            }
        }

        if (maze[head.y / UNIT_SIZE][head.x / UNIT_SIZE] == 1) {
            gameOver();
        }
    }

    private void checkFruit() {
        Point head = snake.get(0);

        if (head.equals(fruit)) {
            score++;
            snake.add(new Point(snake.get(snake.size() - 1))); // Grow the snake
            placeFruit();
        }
    }

    private void placeFruit() {
        Random random = new Random();
        int x, y;

        do {
            x = random.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE;
            y = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
        } while (x >= WIDTH || y >= HEIGHT || x / UNIT_SIZE >= maze[0].length || y / UNIT_SIZE >= maze.length || maze[y / UNIT_SIZE][x / UNIT_SIZE] == 1);

        fruit = new Point(x, y);
    }

    private void gameOver() {
        running = false;

        int maxScore=getMaxScore();
        // Show final score with username
        int option = JOptionPane.showConfirmDialog(this, "\n Game Over!"+"\nUsername: " + username + "\nFinal Score: " + score + "\nDo you want to restart the game?", "Game Over", JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            // Restart the game
            new SnakeGame(username);
        } else {
            // Close the interface
            dispose(); // Close the JFrame
            System.exit(0); // Exit the application
        }
        storeScore(username, score);

        // Update max score label
        updateMaxScore();

        startGame();
    }

    @Override
    public void paint(Graphics g) {
        // Draw background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // Draw maze
        g.setColor(Color.WHITE);
        for (int y = 0; y < maze.length; y++) {
            for (int x = 0; x < maze[y].length; x++) {
                if (maze[y][x] == 1) {
                    g.fillRect(x * UNIT_SIZE, y * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                }
            }
        }

        // Draw snake
        g.setColor(Color.GREEN);
        for (Point point : snake) {
            g.fillOval(point.x, point.y, UNIT_SIZE, UNIT_SIZE);
        }

        // Draw fruit
        g.setColor(Color.RED);
        g.fillOval(fruit.x, fruit.y, UNIT_SIZE, UNIT_SIZE);

        // Draw score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 10, 30);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                if (direction != 'D') direction = 'U';
                break;
            case KeyEvent.VK_DOWN:
                if (direction != 'U') direction = 'D';
                break;
            case KeyEvent.VK_LEFT:
                if (direction != 'R') direction = 'L';
                break;
            case KeyEvent.VK_RIGHT:
                if (direction != 'L') direction = 'R';
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    // Method to store score in the database
    private void storeScore(String username, int score) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO scores (username, score) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setInt(2, score);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to retrieve max score from the database
    private int getMaxScore() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT MAX(score) FROM scores";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    // Method to update max score label
    private void updateMaxScore() {
        int maxScore = getMaxScore();
        maxScoreLabel.setText("Max Score: " + maxScore);
    }

    public static void main(String[] args) {
        String username = JOptionPane.showInputDialog("Enter your username:");
        if (username == null || username.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Username cannot be empty. Exiting...");
            System.exit(0);
        }
        new SnakeGame(username);
    }
}
