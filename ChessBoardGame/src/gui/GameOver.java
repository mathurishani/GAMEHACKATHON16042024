package gui;

import javax.swing.JOptionPane;

import chess.Main;

public class GameOver extends JOptionPane {
	private static final long serialVersionUID = 1L;
	
/*
 * HANDLE GAME OVER
 * This must happen AFTER updateMovePanel() as the listMoves checks the algebraic notation to see if there's repetition more than three times.
 *  
 */

	public void handleGameOver() {
		final Object[] options = {"New Game", "Exit"};
		
		int choice = JOptionPane.showOptionDialog(null, this.getMessage(), "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, this.getIcon(), options, null);
		
		if (choice == JOptionPane.YES_OPTION) {
			Main.setupNewGame(Main.getBoard());
		} else if (choice == JOptionPane.NO_OPTION) {
			System.exit(0);
		}
		
	}
}