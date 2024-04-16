
package chess;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayDeque;


import chess.pieces.Piece;
import gui.PlayerBox;

public class Player {
	private final HashMap<Piece, Square> playerPieces = new HashMap<Piece, Square>();
	private final HashMap<Piece, ArrayDeque<Square>> attackingPieces = new HashMap<Piece, ArrayDeque<Square>>();
	private final HashMap<Piece, ArrayDeque<Square>> coveringPieces = new HashMap<Piece, ArrayDeque<Square>>();
	private final HashSet<Square> collectiveMoves = new HashSet<Square>();
	
	
	private final  PlayerBox playerBox = new PlayerBox();
	private boolean lightPieces = false;
	private boolean playerTurn = false;
	private boolean kingChecked = false;
	private Player opponent;
	private Square selection;
	private int pointTotal;
	
// =================================== GETTER METHODS ===================================
	
	public HashMap<Piece, Square> getPlayerPieces() {return this.playerPieces;}
	public HashMap<Piece, ArrayDeque<Square>> getAttackingPieces() {return this.attackingPieces;}
	public HashMap<Piece, ArrayDeque<Square>> getCoveredLines() {return this.coveringPieces;}
	public HashSet<Square> getCollectiveMoves() {return this.collectiveMoves;}
	
	public PlayerBox getPlayerBox() {return this.playerBox;}
	public boolean isLightPieces() {return this.lightPieces;}
	public boolean isKingChecked() {return this.kingChecked;}
	public boolean isPlayerTurn() {return this.playerTurn;}
	public Player getOpponent() {return this.opponent;}
	public Square getSelection() {return this.selection;}
	public int getPointTotal() {return this.pointTotal;}
	
// =================================== SETTER METHODS ===================================
	
/*
 * Sets up the player upon game creation.
 */
	
	public void setupPlayer(boolean isLightPieces) {
		this.pointTotal = 0;
		this.kingChecked = false;
		this.lightPieces = isLightPieces;
		this.playerTurn = isLightPieces;
		this.playerBox.resetPlayerBox(isLightPieces);
	}
	
	public void setChecked(boolean isChecked) {
		this.kingChecked = isChecked;
	}
	
	public void setPlayerTurn(boolean turn) {
		this.playerTurn = turn;
	}
	
	public void setOpponent(Player opponent) {
		this.opponent = opponent;
	}
	
// =================================== OTHER METHODS ===================================
	
/*
 * Calculates the points when a player captures a piece. Updates the points label in the Move Panel.
 */
	
	public void calculatePoints(int points) {
		this.pointTotal += points;
		int diff = this.pointTotal - this.getOpponent().getPointTotal();
		this.getOpponent().getPlayerBox().setPointLabel(this.getOpponent().getPointTotal() - this.getPointTotal());
		this.getPlayerBox().setPointLabel(diff);
	}
	
/*
 * If a piece is unselected, then it will de-highlight the selection.
 */
	
	private void resetSquareBG(Square sq) {
		sq.getBtn().setBackground(sq.getCurrentBGColour());
		if (sq.getPiece() != null) {sq.getPiece().deHighlightMoves();}
	}
	
/*
 * Will reset the valid moves for each piece, and find the new possible moves for the pieces.
 */
	
	public void refreshMoves() {
		for (Piece piece : this.playerPieces.keySet()) {
			piece.clearMoves();
			piece.findMoves(this.playerPieces.get(piece));
		}
		
		this.collectiveMoves.clear();
		for (Piece piece : this.playerPieces.keySet()) {
			this.collectiveMoves.addAll(piece.getValidSquares());
		}
	}

// =================================== PLAYER SELECTION METHOD ===================================
	
/*
 * When a player clicks on a tile, then it will set the selection for that player.
 */
	
	public void setSelection(Square square) {
		// If player has no selection
		if (this.selection == null) {
			this.selection = square;
			
		// If player selects same square
		} else if (this.selection == square) {
			this.resetSquareBG(this.selection);
			this.selection = null;
			return;
		
		// Otherwise, set new selection
		} else {
			
			// If selection is highlighted, move piece
			if (this.selection.getPiece() != null && this.selection.getPiece().getValidSquares().contains(square) && Main.getCurrentPlayer().isLightPieces() == this.selection.getPiece().getOwner().isLightPieces()) {
					new Move(this.selection, square);
				return;
			}
						
			this.resetSquareBG(this.selection);
			this.selection = square;
		}

		this.selection.getBtn().setBackground(Color.yellow);
		
		// If new selection is a piece
		if (this.selection.getPiece() != null && Main.getCurrentPlayer().isLightPieces() == this.selection.getPiece().getOwner().isLightPieces()) {
			this.selection.getPiece().highlightMoves();				
		} 
	}
	
}
