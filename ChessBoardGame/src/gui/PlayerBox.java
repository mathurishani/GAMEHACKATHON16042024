
package gui;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class PlayerBox {
	// UI Components for Player	
	private final JPanel panel = new JPanel(new BorderLayout(0,0));
	private final JLabel playerTitle = new JLabel("BLACK");
	private final JLabel pointDiff = new JLabel();
	private final JPanel capturedPiecesArea = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
	
// =================================== GETTER METHODS ===================================	
	
	public JLabel getPlayerTitle() {return this.playerTitle;}
	public JPanel getCapturedPiecesArea() {return this.capturedPiecesArea;}
	
// =================================== OTHER METHODS ===================================
	
/*
 * Sets the point difference label depending on who has more points.
 */
	
	public void setPointLabel(int diff) {
		if (diff > 0) {
			pointDiff.setText("+"+diff);
		} else {
			pointDiff.setText(null);
		}
	}
	
/*
 * A pseudo-constructor method to add the panel to the GUI. Not added as a constructor as it requires a lot of components from the Main class.
 * Instead, we call this method from the main class once the players are created (the Player class creates the PlayerBox instead).
 */
	
	public void addToGUI(JPanel gui, String position) {
		this.panel.setPreferredSize(new Dimension(gui.getWidth(), 40));
		this.panel.add(this.playerTitle, BorderLayout.PAGE_START);
		
		this.capturedPiecesArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		this.panel.add(capturedPiecesArea, BorderLayout.PAGE_END);
		gui.add(this.panel, position);
	}
	
/*
 * When a new game is created, we reset the player box to remove the point difference, the pieces and set the player title.
 */
	
	public void resetPlayerBox(boolean isLightPieces) {
		this.capturedPiecesArea.removeAll();
		this.capturedPiecesArea.add(pointDiff);
		this.pointDiff.setText(null);
		if (isLightPieces) {
			this.playerTitle.setText("WHITE");
		} else {
			this.playerTitle.setText("BLACK");
		}
	}
}
