package com.mycompany.diamondhuntergame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class DiamondHunterGame extends JFrame implements KeyListener {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int CELL_SIZE = 50;
    private static final int NUM_DIAMONDS_TO_WIN = 5;

    private Player player1;
    private Player player2;
    private List<Diamond> diamonds;
    private List<Obstacle> obstacles;
    private boolean player1Turn;
    private int[][] grid;

    private ScoresPanel scoresPanel;

    public DiamondHunterGame() {
        setTitle("Diamond Hunter Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Draw grid
                g2d.setColor(Color.LIGHT_GRAY);
                for (int i = 0; i < WIDTH / CELL_SIZE; i++) {
                    for (int j = 0; j < HEIGHT / CELL_SIZE; j++) {
                        g2d.fillRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    }
                }

                // Draw boundary
                g2d.setColor(Color.BLACK);
                g2d.drawRect(0, 0, WIDTH, HEIGHT);

                // Draw diamonds
                g2d.setColor(Color.RED);
                for (Diamond diamond : diamonds) {
                    int diamondX = diamond.getX() * CELL_SIZE + CELL_SIZE / 2;
                    int diamondY = diamond.getY() * CELL_SIZE + CELL_SIZE / 2;
                    int[] xPoints = {diamondX, diamondX - CELL_SIZE / 4, diamondX, diamondX + CELL_SIZE / 4};
                    int[] yPoints = {diamondY - CELL_SIZE / 4, diamondY, diamondY + CELL_SIZE / 4, diamondY};
                    g2d.fillPolygon(xPoints, yPoints, 4);
                }

                // Draw obstacles
                g2d.setColor(Color.BLACK);
                for (Obstacle obstacle : obstacles) {
                    g2d.fillRect(obstacle.getX() * CELL_SIZE, obstacle.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }

                // Draw players
                g2d.setColor(player1.getColor());
                g2d.fillRect(player1.getX() * CELL_SIZE, player1.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                g2d.setColor(player2.getColor());
                g2d.fillRect(player2.getX() * CELL_SIZE, player2.getY() * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        };
        gamePanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        gamePanel.setFocusable(true);
        gamePanel.addKeyListener(this);

        scoresPanel = new ScoresPanel();
        scoresPanel.setPreferredSize(new Dimension(WIDTH, 100)); // Increased height to accommodate the reset button

        add(gamePanel, BorderLayout.CENTER);
        add(scoresPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);

        player1 = new Player(0, 0, Color.BLUE);
        player2 = new Player(WIDTH / CELL_SIZE - 1, HEIGHT / CELL_SIZE - 1, Color.YELLOW);
        diamonds = new ArrayList<>();
        obstacles = new ArrayList<>();
        grid = new int[WIDTH / CELL_SIZE][HEIGHT / CELL_SIZE];
        initializeGrid();
        player1Turn = true;
        updateScores();
    }

    private void initializeGrid() {
        diamonds.clear();
        obstacles.clear();
        for (int i = 0; i < WIDTH / CELL_SIZE; i++) {
            for (int j = 0; j < HEIGHT / CELL_SIZE; j++) {
                if (Math.random() < 0.15) { // 15% chance of diamond
                    diamonds.add(new Diamond(i, j));
                    grid[i][j] = 2; // Diamond
                } else if (Math.random() < 0.1) { // 10% chance of obstacle
                    obstacles.add(new Obstacle(i, j));
                    grid[i][j] = 1; // Obstacle
                } else {
                    grid[i][j] = 0; // Empty cell
                }
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        Player currentPlayer = player1Turn ? player1 : player2;
        int newX = currentPlayer.getX();
        int newY = currentPlayer.getY();
        switch (keyCode) {
            case KeyEvent.VK_UP:
                newY--;
                break;
            case KeyEvent.VK_DOWN:
                newY++;
                break;
            case KeyEvent.VK_LEFT:
                newX--;
                break;
            case KeyEvent.VK_RIGHT:
                newX++;
                break;
            case KeyEvent.VK_SPACE: // Trigger pathfinding and move player
                movePlayerToNearestDiamond(currentPlayer);
                break;
        }
        // Check if the new position is within the bounds and not colliding with obstacles
        if (isValidMove(newX, newY) && !isObstacle(newX, newY)) {
            currentPlayer.setX(newX);
            currentPlayer.setY(newY);
            // Check for collision with diamonds
            for (Iterator<Diamond> iterator = diamonds.iterator(); iterator.hasNext();) {
                Diamond diamond = iterator.next();
                if (newX == diamond.getX() && newY == diamond.getY()) {
                    diamonds.remove(diamond);
                    currentPlayer.incrementScore();
                    updateScores();
                    if (currentPlayer.getScore() >= NUM_DIAMONDS_TO_WIN) {
                        JOptionPane.showMessageDialog(this, "Player " + (player1Turn ? "1" : "2") + " wins!");
                        resetGame();
                    }
                    break;
                }
            }
            player1Turn = !player1Turn;
            repaint();
        }
    }

    private void movePlayerToNearestDiamond(Player player) {
        Pathfinding pathfinding = new Pathfinding(grid, player);
        List<int[]> path = pathfinding.findPathToNearestDiamond();
        movePlayerAlongPath(player, path);
    }

    private void movePlayerAlongPath(Player player, List<int[]> path) {
        if (!path.isEmpty()) {
            int[] nextPos = path.get(0);
            int newX = nextPos[0];
            int newY = nextPos[1];
            if (!isObstacle(newX, newY)) {
                player.setX(newX);
                player.setY(newY);
                path.remove(0);
            }
        }
    }

    private boolean isValidMove(int x, int y) {
        return x >= 0 && x < WIDTH / CELL_SIZE && y >= 0 && y < HEIGHT / CELL_SIZE;
    }

    private boolean isObstacle(int x, int y) {
        for (Obstacle obstacle : obstacles) {
            if (x == obstacle.getX() && y == obstacle.getY()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DiamondHunterGame().setVisible(true);
        });
    }

    void resetGame() {
        player1.setX(0);
        player1.setY(0);
        player1.resetScore();
        player2.setX(WIDTH / CELL_SIZE - 1);
        player2.setY(HEIGHT / CELL_SIZE - 1);
        player2.resetScore();
        initializeGrid();
        player1Turn = true;
        updateScores();
        repaint();
    }

    private void updateScores() {
        scoresPanel.updateScores(player1.getScore(), player2.getScore());
    }

    private static class Pathfinding {
        private final int[][] grid;
        private final Player player;
        private final int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        public Pathfinding(int[][] grid, Player player) {
            this.grid = grid;
            this.player = player;
        }

        public List<int[]> findPathToNearestDiamond() {
            int playerX = player.getX();
            int playerY = player.getY();
            Queue<int[]> queue = new LinkedList<>();
            boolean[][] visited = new boolean[grid.length][grid[0].length];

            queue.offer(new int[]{playerX, playerY});
            visited[playerX][playerY] = true;

            while (!queue.isEmpty()) {
                int[] current = queue.poll();
                int x = current[0];
                int y = current[1];

                if (grid[x][y] == 2) { // Found diamond
                    return constructPath(new int[]{playerX, playerY}, current);
                }

                for (int[] direction : directions) {
                    int newX = x + direction[0];
                    int newY = y + direction[1];
                    if (isValid(newX, newY) && grid[newX][newY] != 1 && !visited[newX][newY]) {
                        queue.offer(new int[]{newX, newY});
                        visited[newX][newY] = true;
                    }
                }
            }

            // No path found
            return new ArrayList<>();
        }

        private boolean isValid(int x, int y) {
            return x >= 0 && x < grid.length && y >= 0 && y < grid[0].length;
        }

        private List<int[]> constructPath(int[] start, int[] end) {
            List<int[]> path = new ArrayList<>();
            int[] current = end;

            while (!Arrays.equals(current, start)) {
                path.add(0, current);
                int x = current[0];
                int y = current[1];

                for (int[] direction : directions) {
                    int newX = x + direction[0];
                    int newY = y + direction[1];
                    if (isValid(newX, newY) && grid[newX][newY] != 1 && Arrays.equals(new int[]{newX, newY}, start)) {
                        current = new int[]{newX, newY};
                        break;
                    }
                }
            }

            return path;
        }
    }
}

class Player {
    private int x, y;
    private Color color;
    private int score;

    public Player(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.score = 0;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Color getColor() {
        return color;
    }

    public int getScore() {
        return score;
    }

    public void incrementScore() {
        score++;
    }

    public void resetScore() {
        score = 0;
    }
}

class Diamond {
    private int x, y;

    public Diamond(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

class Obstacle {
    private int x, y;

    public Obstacle(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

class ScoresPanel extends JPanel {

    private JLabel player1ScoreLabel;
    private JLabel player2ScoreLabel;

    public ScoresPanel() {
        setLayout(new GridBagLayout()); // Set layout to GridBagLayout
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        setPreferredSize(new Dimension(800, 100)); // Increased height to accommodate the reset button

        player1ScoreLabel = new JLabel("Player 1 Score: 0");
        player2ScoreLabel = new JLabel("Player 2 Score: 0");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(player1ScoreLabel, gbc);

        gbc.gridy++;
        add(player2ScoreLabel, gbc);
    }

    public void updateScores(int player1Score, int player2Score) {
        player1ScoreLabel.setText("Player 1 Score: " + player1Score);
        player2ScoreLabel.setText("Player 2 Score: " + player2Score);
    }
}
