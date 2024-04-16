package chess;

import java.awt.BorderLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import gui.Board;
import gui.GameOver;
import gui.Menu;
import gui.MovePanel;

public class Main {
	private final static JPanel gui = new JPanel(new BorderLayout());
	private final static Board board = new Board(gui);
	private final static GameOver gameOver = new GameOver();
	private final static MovePanel movePanel = new MovePanel(gui);
	private final static Player p1 = new Player();
	private final static Player p2 = new Player();
	private static Player currentPlayer;
	private static boolean boardReversed;
	
// =================================== CONSTRUCTOR ===================================
	
	Main() {
		// Main Container Settings
		gui.setBorder(new EmptyBorder(10,20,20,20));
		
		p1.setOpponent(p2);
		p2.setOpponent(p1);
		
		p1.getPlayerBox().addToGUI(gui, BorderLayout.PAGE_END);
		p2.getPlayerBox().addToGUI(gui, BorderLayout.PAGE_START);
		
		// Setup game
		setupNewGame(board);
	}
	
// =================================== GETTER METHODS ===================================	
	
	public static boolean getBoardReversed() {return boardReversed;}
	public static Player getCurrentPlayer() {return currentPlayer;}
	public static MovePanel getMovePanel() {return movePanel;}
	public static Board getBoard() {return board;}
	public static GameOver getGameOverPopUp() {return gameOver;}
	public static Player getP1() {return p1;}
	public static Player getP2() {return p2;}
	
	
// =================================== SETTER METHODS ===================================
	
	private static void setCurrentPlayer(Player player) {
		currentPlayer = player;
	}
	
	public static void setBoardReversed(boolean isReversed) {
		boardReversed = isReversed;
	}
	
// =================================== OTHER METHODS ===================================
	
/*
 * GAME SETUP
 * Resets turn cout, randomizes which player starts as light pieces, and sets the current player.
 *  
 */
	
	public static void setupNewGame(Board board) {
		movePanel.resetPanel();
		
		if (Math.random() >= 0.5) {
			setCurrentPlayer(p1);
			setBoardReversed(true);
		} else {
			setCurrentPlayer(p2);
			setBoardReversed(false);
		}
		
		currentPlayer.setupPlayer(true);
		currentPlayer.getOpponent().setupPlayer(false);
		
		board.resetBoard(p1, p2, getBoardReversed());
	}
	
/*
 * LOAD SETUP
 * If we load a game from the file chooser, we're going to set up a new board but won't set random colours.
 * We'll base ourselves from the PGN file.
 *  
 */
	
	public static void setupLoadedGame(Board board, Player lightPlayer) {
		movePanel.resetPanel();	
		
		setCurrentPlayer(lightPlayer);
		currentPlayer.setupPlayer(true);
		currentPlayer.getOpponent().setupPlayer(false);
		
		board.resetBoard(p1, p2, getBoardReversed());
	}
	
	
/*
 * SWAP TURNS
 * This must happen AFTER updateMovePanel() as the listMoves checks the algebraic notation to see if there's repetition more than three times.
 *  
 */
	
	public static void swapTurns() {
		currentPlayer.getOpponent().setPlayerTurn(true);
		currentPlayer.setPlayerTurn(false);
		setCurrentPlayer(currentPlayer.getOpponent());
		currentPlayer.refreshMoves();
	}
	


	
// =================================== MAIN METHOD ===================================	
	
	public static void main(String[] args) {
		new Main();
		// Create window and add window menu
		final JFrame main = new JFrame("CST8284 Chess by Gabriel Montplaisir -- 041125807");
		new Menu(main);
		
		// Set window default settings
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main.setSize(1200,800);
		
		// Add GUI container and make frame visible		
		main.add(gui);
		main.setVisible(true);
	}

}