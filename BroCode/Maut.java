import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Maut extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    // Images
    Image backgroundImage;
    Image studentImage;
    Image studentImage2;
    Image topBlockImage;
    Image bottomBlockImage;

    // Student class
    int studentX = boardWidth / 8;
    int studentY = boardWidth / 2;
    int studentWidth = 60;
    int studentHeight = 130;

    class Student {
        int x = studentX;
        int y = studentY;
        int width = studentWidth;
        int height = studentHeight;
        Image img;

        Student(Image img) {
            this.img = img;
        }
    }

    // Block class
    int blockX = boardWidth;
    int blockY = 0;
    int blockWidth = 64;
    int blockHeight = 512;

    class Block {
        int x = blockX;
        int y = blockY;
        int width = blockWidth;
        int height = blockHeight;
        Image img;
        boolean passed = false;

        Block(Image img) {
            this.img = img;
        }
    }

    // Game
    Student student;
    int velocityX = -4;
    int velocityY = 0;
    int gravity = 1;

    ArrayList<Block> blocks;
    Random random = new Random();

    Timer gameLoop;
    Timer placeBlockTimer;
    boolean gameOver = false;
    double score = 0;

    String[] studentImagePaths = {"./jp2.png", "./mw.png", "./tb.png", "./sn2.png", "./tj.png"};
    String[] backgroundImagePaths = {"./bg1.png", "./bg2.png", "./bg3.png"};

    int currentStudentIndex = 0;
    int currentBgIndex = 0;
    boolean imageChanged = false;

    Node root;

    Maut() {

        //initializing:

        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        
        backgroundImage = new ImageIcon(getClass().getResource("./bg2.png")).getImage();
        studentImage = new ImageIcon(getClass().getResource("./jp1.png")).getImage();
        studentImage2 = new ImageIcon(getClass().getResource("./naruto.png")).getImage();
        topBlockImage = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomBlockImage = new ImageIcon(getClass().getResource("./champeign.png")).getImage();

        // Student
        student = new Student(studentImage);

        blocks = new ArrayList<Block>();

        // Place block timer
        placeBlockTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeBlocks();
            }
        });
        placeBlockTimer.start();

        // Game timer
        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();

        root = null; // Initialize the BST
    }

    void placeBlocks() {
        int randomBlockY = (int) (blockY - blockHeight / 4 - Math.random() * (blockHeight / 2));
        int openingSpace = boardHeight / 4;

        Block topBlock = new Block(topBlockImage);
        topBlock.y = randomBlockY;
        blocks.add(topBlock);

        Block bottomBlock = new Block(bottomBlockImage);
        bottomBlock.y = topBlock.y + blockHeight + openingSpace;
        blocks.add(bottomBlock);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Background
        g.drawImage(backgroundImage, 0, 0, this.boardWidth, this.boardHeight, null);

        // Student
        g.drawImage(studentImage, student.x, student.y, student.width, student.height, null);

        // Blocks
        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);
            g.drawImage(block.img, block.x, block.y, block.width, block.height, null);
        }

        // Score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 35);
        } else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void move() {
        // Student
        velocityY += gravity;
        student.y += velocityY;
        student.y = Math.max(student.y, 0);

        // Blocks
        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);
            block.x += velocityX;

            if (!block.passed && student.x > block.x + block.width) {
                score += 0.5;
                block.passed = true;
                if ((int) score >= 5 && (int) score % 5 == 0 && !imageChanged) {
                    currentStudentIndex = (currentStudentIndex + 1) % studentImagePaths.length;
                    studentImage = new ImageIcon(getClass().getResource(studentImagePaths[currentStudentIndex])).getImage();
                    velocityX--;
                    imageChanged = true;
                }

                if ((int) score % 15 == 0) {
                    currentBgIndex = (currentBgIndex + 1) % backgroundImagePaths.length;
                    backgroundImage = new ImageIcon(getClass().getResource(backgroundImagePaths[currentBgIndex])).getImage();
                }
            }

            if ((int) score % 5 != 0) {
                imageChanged = false;
            }

            if (collision(student, block)) {
                gameOver = true;
            }
        }

        if (student.y > boardHeight) {
            gameOver = true;
        }
    }

    boolean collision(Student a, Block b) {
        return a.x < b.x + b.width - 30 && 
               a.x + a.width - 10 > b.x && 
               a.y < b.y + b.height - 10 && 
               a.y + a.height - 80 > b.y;
    }

    void insert(String name, double score) {
        root = insertRec(root, name, score);
    }

    Node insertRec(Node root, String name, double score) {
        if (root == null) {
            root = new Node(name, score);
            return root;
        }

        if (score < root.score) {
            root.left = insertRec(root.left, name, score);
        } else if (score >= root.score) {
            root.right = insertRec(root.right, name, score);
        }

        return root;
    }

    void inorder(Node root) {
        if (root != null) {
            inorder(root.right);
            System.out.println(root.name + ": " + root.score);
            inorder(root.left);
        }
    }

    void displayLeaderboard() {
        System.out.println("Leaderboard:");
        inorder(root);
    }

    public void restartGame() {
        student.y = studentY;
        velocityY = 0;
        blocks.clear();
        gameOver = false;
        score = 0;
        gameLoop.start();
        placeBlockTimer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placeBlockTimer.stop();
            gameLoop.stop();

            String playerName = JOptionPane.showInputDialog("Enter your name:");
            insert(playerName, score);
            displayLeaderboard();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;

            if (gameOver) {
                restartGame();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Maut ka khel");
        Maut game = new Maut();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    class Node {
        String name;
        double score;
        Node left, right;

        public Node(String name, double score) {
            this.name = name;
            this.score = score;
            left = right = null;
        }
    }
}
