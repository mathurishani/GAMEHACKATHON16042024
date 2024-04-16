import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.JButton;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

public class SnakeGame extends JPanel implements ActionListener {
    private final int WIDTH = 1000;
    private final int HEIGHT = 1000;
    private final int GRID_SIZE = 25;
    private final int GRID_WIDTH = WIDTH / GRID_SIZE;
    private final int GRID_HEIGHT = HEIGHT / GRID_SIZE;

    private int score = 0;
    private Timer timer;
    private boolean gameOver = false;
    private boolean aiMode = false;

    private int[][] snake = new int[GRID_WIDTH * GRID_HEIGHT][2];
    private int snakeLength = 1;
    private char direction = 'R';

    private int[] apple = new int[2];
    private ArrayList<int[]> obstacles = new ArrayList<>();

    private JButton aiButton;
    private JButton retryButton; // Added retry button

    public SnakeGame() {
        setFocusable(true);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        if (!aiMode && direction != 'D')
                            direction = 'U';
                        break;
                    case KeyEvent.VK_DOWN:
                        if (!aiMode && direction != 'U')
                            direction = 'D';
                        break;
                    case KeyEvent.VK_LEFT:
                        if (!aiMode && direction != 'R')
                            direction = 'L';
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (!aiMode && direction != 'L')
                            direction = 'R';
                        break;
                    case KeyEvent.VK_SPACE:
                        toggleAI();
                        break;
                }
            }
        });

        // Button to toggle AI mode
        aiButton = new JButton("AI Mode: OFF");
        aiButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleAI();
            }
        });
        add(aiButton);

        // Retry button
        retryButton = new JButton("Retry");
        retryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetGame();
            }
        });
        retryButton.setVisible(false); // Initially invisible
        add(retryButton);

        initGame();
    }

    private void initGame() {
        // Initialize snake position
        snake[0][0] = GRID_WIDTH / 2;
        snake[0][1] = GRID_HEIGHT / 2;

        // Initialize apple position
        generateApple();

        // Initialize obstacles
        generateObstacles();

        // Initialize timer
        timer = new Timer(100, this);
        timer.start();
    }

    private void generateApple() {
        Random rand = new Random();
        apple[0] = rand.nextInt(GRID_WIDTH);
        apple[1] = rand.nextInt(GRID_HEIGHT);
    }

    private void generateObstacles() {
        Random rand = new Random();
        int numObstacles = 20; // Increased number of obstacles
        for (int i = 0; i < numObstacles; i++) {
            int x = rand.nextInt(GRID_WIDTH);
            int y = rand.nextInt(GRID_HEIGHT);
            obstacles.add(new int[]{x, y});
        }
    }

    private void move() {
        if (aiMode) {
            char optimalDirection = findOptimalDirection();
            if (optimalDirection != 0) {
                direction = optimalDirection;
            }
        }

        // Move the snake
        for (int i = snakeLength - 1; i > 0; i--) {
            snake[i][0] = snake[i - 1][0];
            snake[i][1] = snake[i - 1][1];
        }

        // Move the head of the snake
        switch (direction) {
            case 'U':
                snake[0][1]--;
                break;
            case 'D':
                snake[0][1]++;
                break;
            case 'L':
                snake[0][0]--;
                break;
            case 'R':
                snake[0][0]++;
                break;
        }

        // Check if snake eats the apple
        if (snake[0][0] == apple[0] && snake[0][1] == apple[1]) {
            score++;
            snakeLength++;
            generateApple();
        }

        // Check if snake hits the wall or itself
        if (snake[0][0] < 0 || snake[0][0] >= GRID_WIDTH ||
                snake[0][1] < 0 || snake[0][1] >= GRID_HEIGHT ||
                checkCollision()) {
            gameOver = true;
            timer.stop();
            retryButton.setVisible(true); // Show retry button
        }
    }

    private boolean checkCollision() {
        // Check if snake hits itself
        for (int i = 1; i < snakeLength; i++) {
            if (snake[0][0] == snake[i][0] && snake[0][1] == snake[i][1]) {
                return true;
            }
        }
        // Check if snake hits an obstacle
        for (int[] obstacle : obstacles) {
            if (snake[0][0] == obstacle[0] && snake[0][1] == obstacle[1]) {
                return true;
            }
        }
        return false;
    }

    private void toggleAI() {
        aiMode = !aiMode;
        if (aiMode) {
            aiButton.setText("AI Mode: ON");
        } else {
            aiButton.setText("AI Mode: OFF");
        }
    }

    private void resetGame() {
        score = 0;
        gameOver = false;
        snakeLength = 1;
        direction = 'R';
        retryButton.setVisible(false); // Hide retry button
        initGame();
    }

    private char findOptimalDirection() {
        // Possible directions
        char[] directions = {'U', 'D', 'L', 'R'};
        int[][] nextMoves = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}}; // Corresponding changes in x and y for each direction

        int[][] visited = new int[GRID_WIDTH][GRID_HEIGHT];
        for (int i = 0; i < GRID_WIDTH; i++) {
            for (int j = 0; j < GRID_HEIGHT; j++) {
                visited[i][j] = -1; // Initialize all cells as unvisited
            }
        }

        // Mark obstacles and snake body as visited
        for (int[] obstacle : obstacles) {
            visited[obstacle[0]][obstacle[1]] = 1;
        }
        for (int i = 1; i < snakeLength; i++) {
            visited[snake[i][0]][snake[i][1]] = 1;
        }

        int[] snakeHead = snake[0];
        int[] applePosition = apple;

        return backtrackToFindOptimalDirection(snakeHead, applePosition, directions, nextMoves, visited);
    }

    private char backtrackToFindOptimalDirection(int[] currentPosition, int[] targetPosition, char[] directions, int[][] nextMoves, int[][] visited) {
        if (currentPosition[0] == targetPosition[0] && currentPosition[1] == targetPosition[1]) {
            // Target position reached
            return 0; // No need to move
        }

        char optimalDirection = 0;
        int minDistance = Integer.MAX_VALUE;

        for (int i = 0; i < directions.length; i++) {
            int newX = currentPosition[0] + nextMoves[i][0];
            int newY = currentPosition[1] + nextMoves[i][1];

            // Check if new position is valid
            if (newX >= 0 && newX < GRID_WIDTH && newY >= 0 && newY < GRID_HEIGHT && visited[newX][newY] == -1) {
                int distance = Math.abs(newX - targetPosition[0]) + Math.abs(newY - targetPosition[1]);
                if (distance < minDistance) {
                    minDistance = distance;
                    optimalDirection = directions[i];
                }
            }
        }

        return optimalDirection;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw obstacles
        g.setColor(Color.GRAY);
        for (int[] obstacle : obstacles) {
            g.fillRect(obstacle[0] * GRID_SIZE, obstacle[1] * GRID_SIZE, GRID_SIZE, GRID_SIZE);
        }
        // Draw snake
        for (int i = 0; i < snakeLength; i++) {
            g.setColor(Color.BLACK);
            g.fillRect(snake[i][0] * GRID_SIZE, snake[i][1] * GRID_SIZE, GRID_SIZE, GRID_SIZE);
        }
        // Draw apple
        g.setColor(Color.RED);
        g.fillRect(apple[0] * GRID_SIZE, apple[1] * GRID_SIZE, GRID_SIZE, GRID_SIZE);

        // Draw score
        g.setColor(Color.BLACK);
        g.drawString("Score: " + score, 10, 20);

        // Draw game over message
        if (gameOver) {
            g.drawString("Game Over", WIDTH / 2 - 40, HEIGHT / 2);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(new SnakeGame());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
