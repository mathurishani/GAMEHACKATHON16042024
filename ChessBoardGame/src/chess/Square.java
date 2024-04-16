package chess;

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import chess.pieces.Piece;

public class Square {
	private final Color LIGHT_SQUARE = new Color(250, 250, 240);
	private final Color DARK_SQUARE = new Color(115, 140, 80);
	
	private final int x, y;
	private Piece piece;
	private final JButton btn = new JButton();
	private final Color originalBGColour;
	private Color currentBGColour;

	
// =================================== CONSTRUCTOR ===================================
	
/*
 * Sets the coordinates to the square, as well as the background.
 * Adds a button which will set the selection when a player clicks on it.
 */
	
	public Square(final int x, final int y) {
		this.x = x;
		this.y = y;
		if ((x % 2 == 0 && y % 2 == 0) || (x % 2 == 1 && y % 2 == 1)) {
			this.originalBGColour = LIGHT_SQUARE;
			this.currentBGColour = LIGHT_SQUARE;
		} else {
			this.originalBGColour = DARK_SQUARE;
			this.currentBGColour = DARK_SQUARE;
		}
		
		this.btn.setBackground(this.originalBGColour);
		
		this.btn.addActionListener(event -> {
			Main.getCurrentPlayer().setSelection(this);
		});
	}
	
// =================================== GETTER METHODS ===================================	

	public Piece getPiece() {return this.piece;}
	public JButton getBtn() {return this.btn;}
	public int getX() {return this.x;}
	public int getY() {return this.y;}
	public Color getOriginalBGColour() {return this.originalBGColour;}
	public Color getCurrentBGColour() {return this.currentBGColour;}
	
// =================================== SETTER METHODS ===================================
	
	public void setCurrentBGColour(Color colour) {
		this.currentBGColour = colour;
	}
	
	public void setPiece(Piece piece) {
		this.piece = piece;
		if (this.piece != null) {
			this.btn.setIcon(new ImageIcon(piece.getImage()));
		} else {
			this.btn.setIcon(null);
		}
	}
		
}