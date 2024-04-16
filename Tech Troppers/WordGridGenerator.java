import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;

public class WordGridGenerator {
    private static final int GRID_SIZE = 25;
    private static final int MIN_WORD_LENGTH = 5;
    private static final String DICTIONARY_FILE_PATH = "D:/New Download/dictionary.txt";
    private static final int MIN_WORDS_REQUIRED = 20;

    private static char[][] grid;
    public static List<String> dictionary;
    static List<String> foundWords = new ArrayList<>();
    static List<String> currentW = new ArrayList<>();
    static int score = 0; // Score variable
    static int searchCount = 0; // Search count variable

    public static void main(String[] args) {
        loadDictionary();
        generateGrid();
        showInstructions(); // Show instructions before launching GUI
        SwingUtilities.invokeLater(WordGridGenerator::launchGUI);
    }

    private static void loadDictionary() {
        dictionary = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new File(DICTIONARY_FILE_PATH));
            while (scanner.hasNextLine()) {
                String word = scanner.nextLine().trim().toLowerCase();
                if (word.length() >= MIN_WORD_LENGTH) {
                    dictionary.add(word);
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("Dictionary file not found.");
            System.exit(1);
        }
    }

    private static void generateGrid() {
        grid = new char[GRID_SIZE][GRID_SIZE];
        Random random = new Random();
        int wordsPlaced = 0;
        boolean horizontal = true;
        while (wordsPlaced < MIN_WORDS_REQUIRED) {
            String word = dictionary.get(random.nextInt(dictionary.size()));
            int row = random.nextInt(GRID_SIZE);
            int col = random.nextInt(GRID_SIZE);
            if (horizontal) {
                if (placeWordHorizontally(word, row, col)) {
                    wordsPlaced++;
                    horizontal = false;
                    currentW.add(word);
                }
            } else {
                if (placeWordVertically(word, row, col)) {
                    wordsPlaced++;
                    horizontal = true;
                    currentW.add(word);
                }
            }
        }
        fillRemaining();
    }

    private static boolean placeWordHorizontally(String word, int row, int col) {
        if (row < 0 || col < 0 || row >= GRID_SIZE || col >= GRID_SIZE) {
            return false;
        }
        int length = word.length();
        if (col + length > GRID_SIZE) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            grid[row][col + i] = word.charAt(i);
        }
        return true;
    }

    private static boolean placeWordVertically(String word, int row, int col) {
        if (row < 0 || col < 0 || row >= GRID_SIZE || col >= GRID_SIZE) {
            return false;
        }
        int length = word.length();
        if (row + length > GRID_SIZE) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            grid[row + i][col] = word.charAt(i);
        }
        return true;
    }

    private static void fillRemaining() {
        Random random = new Random();
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (grid[i][j] == '\0') {
                    grid[i][j] = (char) (random.nextInt(26) + 'a');
                }
            }
        }
    }

    private static void launchGUI() {
        WordGridGUI gui = new WordGridGUI(grid);
        gui.setVisible(true);
    }

    private static void showInstructions() {
        String message = "Welcome to Word Grid Search!\n\n" +
                         "Find 10 words you in the grid.\n" +
                         "Words must be at least 5 letters long or more than that.\n" +
                         "You will earn 10 points for each word found,\n" +
                         "but lose 10 points if a word is not found.\n" +
                         "The words are pokemon names.\n" +
                         "Good luck!";
        JOptionPane.showMessageDialog(null, message, "Instructions", JOptionPane.INFORMATION_MESSAGE);
    }

    // Public method to access foundWords
    public static List<String> getFoundWords() {
        return foundWords;
    }

    // Public method to access the score
    public static int getScore() {
        return score;
    }

    // Public method to update the score
    public static void updateScore(boolean wordFound) {
        if (wordFound) {
            score += 10; // Increment score if word found
        } else {
            score -= 10; // Decrement score if word not found
        }
    }

    // Public method to update search count
    public static void updateSearchCount() {
        searchCount++;
    }

    // Public method to check if search count has exceeded the limit
    public static boolean isSearchLimitExceeded() {
        return searchCount >= 10;
    }
}

class WordGridGUI extends JFrame {
    private static final int CELL_SIZE = 30;
    private char[][] grid;
    private JTextField searchField;
    private JLabel scoreLabel; // Label to display score

    public WordGridGUI(char[][] grid) {
        this.grid = grid;
        setTitle("Word Grid");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel gridPanel = new JPanel(new GridLayout(grid.length, grid[0].length));
        createGrid(gridPanel);
        add(gridPanel, BorderLayout.CENTER);

        JPanel searchPanel = new JPanel();
        searchField = new JTextField(15);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchWord = searchField.getText().trim().toLowerCase();
                if (!searchWord.isEmpty()) {
                    if (!WordGridGenerator.isSearchLimitExceeded()) {
                        WordGridGenerator.updateSearchCount(); // Update search count
                        searchWord(searchWord);
                    } else {
                        endGame(); // End game if search limit exceeded
                    }
                }
            }
        });
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        add(searchPanel, BorderLayout.SOUTH);

        scoreLabel = new JLabel("Score: 0"); // Initialize score label
        add(scoreLabel, BorderLayout.NORTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void createGrid(JPanel gridPanel) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                JLabel label = new JLabel(String.valueOf(grid[i][j]), SwingConstants.CENTER);
                label.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
                label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                label.setOpaque(true); // Set to true to make background color visible
                gridPanel.add(label);
            }
        }
    }

    private void searchWord(String word) {
        boolean wordFound = false;

        for (String wored : WordGridGenerator.currentW) {
            System.out.println(wored);
        }
        System.out.println("------------------------------");
        if (WordGridGenerator.getFoundWords().contains(word)) {
            wordFound = true;
        } else {
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[i].length; j++) {
                    if (searchWordInGrid(word, i, j)) {
                        wordFound = true;
                        break;
                    }
                }
                if (wordFound) {
                    break;
                }
            }
        }

        if (wordFound) {
            WordGridGenerator.updateScore(true); // Update score if word found
        } else {
            WordGridGenerator.updateScore(false); // Update score if word not found
        }

        scoreLabel.setText("Score: " + WordGridGenerator.getScore()); // Update score label

        if (!wordFound) {
            JOptionPane.showMessageDialog(this, "Word not found.");
        }
    }

    private boolean searchWordInGrid(String word, int startRow, int startCol) {
        boolean foundIndict = false;
        for (String wored : WordGridGenerator.dictionary) {
            if (wored.equals(word)) foundIndict = true;
        }
        if (!foundIndict) return false;
        if (searchWordHorizontally(word, startRow, startCol)) {
            return true;
        }
        if (searchWordVertically(word, startRow, startCol)) {
            return true;
        }
        if (searchWordDiagonallyDownLeft(word, startRow, startCol)) {
            return true;
        }
        if (searchWordDiagonallyDownRight(word, startRow, startCol)) {
            return true;
        }
        if (searchWordDiagonallyUpRight(word, startRow, startCol)) {
            return true;
        }
        if (searchWordDiagonallyUpLeft(word, startRow, startCol)) {
            return true;
        }
        return false;
    }

    private boolean searchWordHorizontally(String word, int startRow, int startCol) {
        int wordLength = word.length();
        if (startCol + wordLength > grid[startRow].length) {
            return false;
        }
        for (int i = 0; i < wordLength; i++) {
            if (grid[startRow][startCol + i] != word.charAt(i)) {
                return false;
            }
        }
        for (int i = 0; i < wordLength; i++) {
            JLabel cellLabel = (JLabel) ((JPanel) getContentPane().getComponent(0)).getComponent(startRow * grid[0].length + startCol + i);
            cellLabel.setBackground(Color.GREEN);
        }
        return true;
    }

    private boolean searchWordVertically(String word, int startRow, int startCol) {
        int wordLength = word.length();
        if (startRow + wordLength > grid.length) {
            return false;
        }
        for (int i = 0; i < wordLength; i++) {
            if (grid[startRow + i][startCol] != word.charAt(i)) {
                return false;
            }
        }
        for (int i = 0; i < wordLength; i++) {
            JLabel cellLabel = (JLabel) ((JPanel) getContentPane().getComponent(0)).getComponent((startRow + i) * grid[0].length + startCol);
            cellLabel.setBackground(Color.GREEN);
        }
        return true;
    }

    private boolean searchWordDiagonallyDownRight(String word, int startRow, int startCol) {
        int wordLength = word.length();
        if (startRow + wordLength > grid.length || startCol + wordLength > grid[startRow].length) {
            return false;
        }
        for (int i = 0; i < wordLength; i++) {
            if (grid[startRow + i][startCol + i] != word.charAt(i)) {
                return false;
            }
        }
        for (int i = 0; i < wordLength; i++) {
            JLabel cellLabel = (JLabel) ((JPanel) getContentPane().getComponent(0)).getComponent((startRow + i) * grid[0].length + startCol + i);
            cellLabel.setBackground(Color.GREEN);
        }
        return true;
    }

    private boolean searchWordDiagonallyDownLeft(String word, int startRow, int startCol) {
        int wordLength = word.length();
        if (startRow + wordLength > grid.length || startCol - wordLength < 0) {
            return false;
        }
        for (int i = 0; i < wordLength; i++) {
            if (grid[startRow + i][startCol - i] != word.charAt(i)) {
                return false;
            }
        }
        for (int i = 0; i < wordLength; i++) {
            JLabel cellLabel = (JLabel) ((JPanel) getContentPane().getComponent(0)).getComponent((startRow + i) * grid[0].length + startCol - i);
            cellLabel.setBackground(Color.GREEN);
        }
        return true;
    }

    private boolean searchWordDiagonallyUpRight(String word, int startRow, int startCol) {
        int wordLength = word.length();
        if (startRow - wordLength < 0 || startCol + wordLength > grid[startRow].length) {
            return false;
        }
        for (int i = 0; i < wordLength; i++) {
            if (grid[startRow - i][startCol + i] != word.charAt(i)) {
                return false;
            }
        }
        for (int i = 0; i < wordLength; i++) {
            JLabel cellLabel = (JLabel) ((JPanel) getContentPane().getComponent(0)).getComponent((startRow - i) * grid[0].length + startCol + i);
            cellLabel.setBackground(Color.GREEN);
        }
        return true;
    }

    private boolean searchWordDiagonallyUpLeft(String word, int startRow, int startCol) {
        int wordLength = word.length();
        if (startRow - wordLength < 0 || startCol - wordLength < 0) {
            return false;
        }
        for (int i = 0; i < wordLength; i++) {
            if (grid[startRow - i][startCol - i] != word.charAt(i)) {
                return false;
            }
        }
        for (int i = 0; i < wordLength; i++) {
            JLabel cellLabel = (JLabel) ((JPanel) getContentPane().getComponent(0)).getComponent((startRow - i) * grid[0].length + startCol - i);
            cellLabel.setBackground(Color.GREEN);
        }
        return true;
    }

    private void endGame() {
        JOptionPane.showMessageDialog(this, "Search limit exceeded.\nYour total score is: " + WordGridGenerator.getScore(), "Game Over", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }
}
