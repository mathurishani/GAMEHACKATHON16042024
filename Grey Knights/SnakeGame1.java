package src;



import javax.swing.*;

import java.awt.*;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;

import java.awt.event.KeyEvent;

import java.awt.event.KeyListener;

import java.util.ArrayList;

import java.util.List;

import java.util.Random;



public class SnakeGame1 extends JPanel implements KeyListener {

    private final int gridSize = 20;

    private final int canvasWidth = 800;

    private final int canvasHeight = 600;

    private final int gameSpeed = 100;



    private List<Point> snake;

    private Point food;

    private String direction = "RIGHT";

    private int score = 0;

    private int highScore = 0;

    private int shortestDist = 10;

    private int distance = 0;

    private int keypressedcnt = 0;

    private int totalKeysStrok = 0;



    private JLabel scoreLabel;

    private JLabel highScoreLabel;

    private JLabel strokeLabel;

    private JLabel bonusLabel;



    public SnakeGame1() {

        setPreferredSize(new Dimension(canvasWidth, canvasHeight));

        setBackground(Color.BLACK);

        setFocusable(true);

        addKeyListener(this);



        initializeGame();

        startGameLoop();

        createScoreLabel();

        createHighScoreLabel();

        createStrokeLabel();

        createBonusLabel();

    }



    private void createScoreLabel() {

        scoreLabel = new JLabel("Score: 0");

        scoreLabel.setForeground(Color.WHITE);

        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        scoreLabel.setBounds(10, 10, 200, 30);

        add(scoreLabel);

    }



    private void createHighScoreLabel() {

        highScoreLabel = new JLabel("High Score: 0");

        highScoreLabel.setForeground(Color.WHITE);

        highScoreLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        highScoreLabel.setBounds(canvasWidth - 200, 10, 200, 30);

        add(highScoreLabel);

    }



    private void createStrokeLabel() {

        strokeLabel = new JLabel("Stroke: 0");

        strokeLabel.setForeground(Color.WHITE);

        strokeLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        strokeLabel.setBounds(10, 40, 200, 30);

        add(strokeLabel);

    }



    private void createBonusLabel() {

        bonusLabel = new JLabel();

        bonusLabel.setForeground(Color.YELLOW);

        bonusLabel.setFont(new Font("Arial", Font.BOLD, 24));

        bonusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        bonusLabel.setBounds(0, 0, canvasWidth, 50);

        bonusLabel.setVisible(false);

        add(bonusLabel);

    }



    private void initializeGame() {

        snake = new ArrayList<>();

        snake.add(new Point(10, 10));

        generateFood();

    }



    private void generateFood() {

        Random random = new Random();

        int x = random.nextInt(canvasWidth / gridSize);

        int y = random.nextInt(canvasHeight / gridSize);

        food = new Point(x, y);

    }



    private void startGameLoop() {

        Timer timer = new Timer(gameSpeed, e -> {

            if (!checkCollision()) {

                moveSnake();

                repaint();

            } else {

                endGame();

            }

        });

        timer.start();

    }



    private void moveSnake() {

        Point head = snake.get(0);

        Point newHead = new Point(head);



        switch (direction) {

            case "RIGHT":

                newHead.x++;

                distance = distance + 1;

                break;

            case "LEFT":

                newHead.x--;

                distance = distance + 1;

                break;

            case "UP":

                newHead.y--;

                distance = distance + 1;

                break;

            case "DOWN":

                newHead.y++;

                distance = distance + 1;

                break;

        }



        snake.add(0, newHead);



        if (newHead.equals(food)) {

            boolean isMovingInSameDirection = false;



            if (snake.size() > 1) {

                Point neck = snake.get(1);

                if ((direction.equals("UP") && head.y > neck.y) || 

                    (direction.equals("DOWN") && head.y < neck.y)) {

                    isMovingInSameDirection = true;

                }

            }



            if (isMovingInSameDirection && keypressedcnt <= 1) {

                score += 1;

                showBonusMessage();

            } else if (!isMovingInSameDirection && keypressedcnt <= 2) {

                score += 1;

                showBonusMessage();

            }



            score++;

            totalKeysStrok += keypressedcnt;

            keypressedcnt = 0;



            if (score > highScore) {

                highScore = score;

                updateHighScoreLabel();

            }



            generateFood();



            shortestDist = Math.abs(newHead.x - food.x) + Math.abs(newHead.y - food.y);

        } else {

            snake.remove(snake.size() - 1);

        }



        distance = Math.abs(newHead.x - food.x) + Math.abs(newHead.y - food.y);

        updateScoreLabel();

        updateStrokeLabel();

    }





    private void updateScoreLabel() {

        scoreLabel.setText("Score: " + score);

    }



    private void updateHighScoreLabel() {

        highScoreLabel.setText("High Score: " + highScore);

    }



    private void updateStrokeLabel() {

        strokeLabel.setText("Stroke: " + keypressedcnt);

    }



    private boolean checkCollision() {

        Point head = snake.get(0);

        if (head.x < 0 || head.x >= canvasWidth / gridSize || head.y < 0 || head.y >= canvasHeight / gridSize) {

            return true;

        }

        for (int i = 1; i < snake.size(); i++) {

            if (head.equals(snake.get(i))) {

                return true;

            }

        }

        return false;

    }



    private void endGame() {

        JOptionPane.showMessageDialog(this, "Game Over!\nScore: " + score, "Game Over", JOptionPane.INFORMATION_MESSAGE);

        score = 0;

        keypressedcnt = 0;

        initializeGame();

    }



    @Override

    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        drawSnake(g);

        drawFood(g);

    }



    private void drawSnake(Graphics g) {

        g.setColor(Color.GREEN);

        for (Point p : snake) {

            g.fillRect(p.x * gridSize, p.y * gridSize, gridSize, gridSize);

        }

    }



    private void drawFood(Graphics g) {

        g.setColor(Color.RED);

        g.fillRect(food.x * gridSize, food.y * gridSize, gridSize, gridSize);

    }



    @Override

    public void keyPressed(KeyEvent e) {

        keypressedcnt++;



        switch (e.getKeyCode()) {

            case KeyEvent.VK_LEFT:

                if (!direction.equals("RIGHT")) direction = "LEFT";

                break;

            case KeyEvent.VK_RIGHT:

                if (!direction.equals("LEFT")) direction = "RIGHT";

                break;

            case KeyEvent.VK_UP:

                if (!direction.equals("DOWN")) direction = "UP";

                break;

            case KeyEvent.VK_DOWN:

                if (!direction.equals("UP")) direction = "DOWN";

                break;

        }

    }



    @Override

    public void keyTyped(KeyEvent e) {}



    @Override

    public void keyReleased(KeyEvent e) {}



    private void showBonusMessage() {

        bonusLabel.setText("+1 Bonus Received!");

        bonusLabel.setVisible(true);

        Timer bonusTimer = new Timer(1000, new ActionListener() {

            @Override

            public void actionPerformed(ActionEvent e) {

                bonusLabel.setVisible(false);

            }

        });

        bonusTimer.setRepeats(false);

        bonusTimer.start();

    }



    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Snake Game");

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.setResizable(false);

            frame.add(new SnakeGame1());

            frame.pack();

            frame.setLocationRelativeTo(null);

            frame.setVisible(true);

        });

    }

}