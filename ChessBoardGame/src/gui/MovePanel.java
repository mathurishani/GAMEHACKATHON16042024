
package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayDeque;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import chess.Main;
import chess.Move;

public class MovePanel {
	private final JPanel panel = new JPanel(new GridBagLayout());
	final GridBagConstraints cst = new GridBagConstraints();
	private final ArrayDeque<String> moves = new ArrayDeque<String>();
	private Move lastMove;
	private int turnCount;
	
// =================================== CONSTRUCTOR ===================================
	
	public MovePanel(JPanel gui) {
		this.panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Moves"));
		this.panel.setPreferredSize(new Dimension(375, gui.getHeight()));
		gui.add(this.panel, BorderLayout.LINE_END);
	}
	
// =================================== GETTER METHODS ===================================		
	
	public int getTurnCount() {return this.turnCount;}
	public JPanel getPanel() {return this.panel;}
	public Move getLastMove() {return this.lastMove;}
	public ArrayDeque<String> getMoves() {return this.moves;}
	
// =================================== SETTER METHODS ===================================
	
/*
 * SET TURN COUNT
 */
	
	public void setTurnCount(int count) {
		this.turnCount = count;
	}
	
/*
 * UPDATE LAST MOVE
 * Will also update the background colour of the square to show the last move.
 */
	
	public void setLastMove(Move move) {
		if (this.lastMove != null) this.revertTileColours(this.lastMove);
		
		this.lastMove = move;
		this.setTileColours(this.lastMove);
	}

// =================================== PANEL UPDATE METHODS ===================================

/*
 * Every time it's the light pieces' turn, we'll add a new row to the panel.
 */
	private void addRowToPanel() {
		this.cst.anchor = GridBagConstraints.PAGE_START;
		this.cst.gridx = 0;
		this.cst.gridy = this.getTurnCount();
		this.cst.ipadx = 50;
		this.cst.ipady = 10;
		if (this.getTurnCount() > 0) this.panel.add(new JLabel(this.getTurnCount()+"."), cst);
	}
	
/*
 * Every time a piece moves, We'll create a new label to add to the panel. Called by updateLastMove()
 */
	
	private void addMoveToPanel(String notation, boolean isLightPieces) {
		JLabel label = new JLabel(notation);
		this.cst.gridx = (isLightPieces) ? 1 : 2;
		this.cst.ipadx = 50;
		this.cst.gridy = this.getTurnCount();
		this.cst.ipady = 10;
		this.cst.anchor = GridBagConstraints.NORTH;
		this.panel.add(label, this.cst);
	}
	
/*
 * Every time a piece moves, we'll add a new row if necessary, add a new label with the algebraic notation, then add it to the panel.
 */
	public void updateLastMove(Move lastMove) {
		// Sets last move and highlights board tiles.
		this.setLastMove(lastMove);
		
		if (this.lastMove.getCurrentPiece().getOwner().isLightPieces()) {
			this.turnCount++;
			this.addRowToPanel();
		}
		
		this.moves.add(this.lastMove.getCoord());
		this.addMoveToPanel(this.lastMove.getCoord(), this.lastMove.getCurrentPiece().getOwner().isLightPieces());
		
		Main.swapTurns();
	}
	
/*
 * Resets the panel when a new game starts.
 */
	public void resetPanel() {
		this.setTurnCount(0);
		this.getPanel().removeAll();
		this.getPanel().updateUI();
		this.getMoves().clear();
	}
	

// =================================== LAST MOVE UPDATE METHODS ===================================
	
/*
 * Revert tile colour from a special colour to the checkered board tile
 */
	
	private void revertTileColours(Move move) {
		move.getLast().setCurrentBGColour(move.getLast().getOriginalBGColour());
		move.getLast().getBtn().setBackground(move.getLast().getOriginalBGColour());
		move.getCurrent().setCurrentBGColour(move.getCurrent().getOriginalBGColour());
		move.getCurrent().getBtn().setBackground(move.getCurrent().getOriginalBGColour());
	}
	
/*
 * Highlight tile colour for last move
 */
	
	private void setTileColours(Move move) {
		move.getLast().setCurrentBGColour(move.getHIGHLIGHT());
		move.getLast().getBtn().setBackground(move.getLast().getCurrentBGColour());
		move.getCurrent().setCurrentBGColour(move.getHIGHLIGHT());
		move.getCurrent().getBtn().setBackground(move.getCurrent().getCurrentBGColour());
	}
	
}
