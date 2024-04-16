
package chess.pieces;

import java.util.ArrayDeque;

import chess.Main;
import chess.Move;
import chess.Player;
import chess.Square;
import gui.Board;

public final class King extends Piece {
	private boolean leftCastlingMove = false;
	private Square leftCastlingRook, rightCastlingRook;
	private boolean rightCastlingMove = false;
	
// =================================== CONSTRUCTOR ===================================	
	
	public King(Player owner) {
		super(owner, 1, "K");
	}
	
// =================================== GETTER METHODS ===================================
	
	public boolean leftCastlePossible() {return this.leftCastlingMove;}
	public Square getLeftCastlingRook() {return this.leftCastlingRook;}
	public boolean rightCastlePossible() {return this.rightCastlingMove;}
	public Square getRightCastlingRook() {return this.rightCastlingRook;}
	
// =================================== SETTER METHODS ===================================
	
	public void notCastlingMove() {
		this.leftCastlingMove = false;
		this.rightCastlingMove = false;
	}
	
	private void setLeftCastlingMove(ArrayDeque<Square> arr, Square sq, Square rookPiece) {
		this.leftCastlingMove = true;
		arr.add(sq);
		this.leftCastlingRook = rookPiece;
	}
	
	private void setRightCastlingMove(ArrayDeque<Square> arr, Square sq, Square rookPiece) {
		this.rightCastlingMove = true;
		arr.add(sq);
		this.rightCastlingRook = rookPiece;
	}
	
// =================================== CASTLING METHODS ===================================
	
/*
 * The following two methods are identical, other than checking different squares along the Y-axis.
 * Checks whether it's the first move for the king and first move for the rook.
 * Then checks whether there's a piece covering the line for castling. If there is, then castling is not possible.
 * Additionally, if the king is in check, then castling is also not possible.
 * 
 * @param
 * boolean reversed - Board direction
 * int x - the King's square "X" coordinate on the board
 * int y - the King's square "Y" coordinate on the board
 */
	
	private void checkLeftCastle(boolean reversed, int x, int y) {
		if (!this.isFirstMove() || Board.getSquare(x, y-1).getPiece() != null || Board.getSquare(x, y-2).getPiece() != null || this.getOwner().isKingChecked()) return;
		for (Piece p : this.getOwner().getOpponent().getPlayerPieces().keySet()) {
			if (p.getCoveringSquares().contains(Board.getSquare(x, y-1)) || p.getCoveringSquares().contains(Board.getSquare(x, y-2))) {
				return;}
			if (reversed && p.getCoveringSquares().contains(Board.getSquare(x, y-3))) return;	
		}
		
		if (reversed) {
			if (Board.getSquare(x, y-3).getPiece() == null && Board.getSquare(x, y-4).getPiece() instanceof Rook && Board.getSquare(x, y-4).getPiece().isFirstMove()) {setLeftCastlingMove(this.westMoves, Board.getSquare(x, y-2), Board.getSquare(x, y-4));}	
		} else {
			if (Board.getSquare(x, y-3).getPiece() instanceof Rook && Board.getSquare(x, y-3).getPiece().isFirstMove()) {setLeftCastlingMove(this.westMoves, Board.getSquare(x, y-2), Board.getSquare(x, y-3));}
		}
	}
	
	private void checkRightCastle(boolean reversed, int x, int y) {
		if (!this.isFirstMove() || Board.getSquare(x, y+1).getPiece() != null || Board.getSquare(x, y+2).getPiece() != null || this.getOwner().isKingChecked()) return;
		
		for (Piece p : this.getOwner().getOpponent().getPlayerPieces().keySet()) {
			if (p.getCoveringSquares().contains(Board.getSquare(x, y+1)) || p.getCoveringSquares().contains(Board.getSquare(x, y+2))) return;
			if (!reversed && p.getCoveringSquares().contains(Board.getSquare(x, y+3))) return;	
		}
		
		if (reversed) {
			if (Board.getSquare(x, y+3).getPiece() instanceof Rook && Board.getSquare(x, y+3).getPiece().isFirstMove()) {setRightCastlingMove(this.eastMoves, Board.getSquare(x, y+2), Board.getSquare(x, y+3));}
		} else {
			if (Board.getSquare(x, y+3).getPiece() == null && Board.getSquare(x, y+4).getPiece() instanceof Rook && Board.getSquare(x, y+4).getPiece().isFirstMove()) {setRightCastlingMove(this.eastMoves, Board.getSquare(x, y+2), Board.getSquare(x, y+4));}
		}
	}
	
/*
 * Handles castling moves. Sets the King to the appropriate square and the rook to the left or right of it.
 * 
 * @param
 * Move move - The current King move
 * Square rookSq - The left or right rook, depending on the direction
 * int y - The current square's y coordinate +/- 1
 * boolean reversed - The board's direction
 */
	
public void handleCastlingMove(Move move, Square rookSq, int y, boolean reversed) {
	Board.getSquare(move.getCurrent().getX(), y).setPiece(rookSq.getPiece());
	this.getOwner().getPlayerPieces().replace(rookSq.getPiece(), Board.getSquare(move.getCurrent().getX(), y));
	
	rookSq.setPiece(null);
}
	
	// =================================== FIND MOVES METHOD ===================================
	
	public void findMoves(Square current) {
		int x = current.getX();
		int y = current.getY();
		
		// Check for moves up
		if (x-1 >= 0) {
			this.northMoves.add(Board.getSquare(x-1, y));
			if (y+1 <= 7) this.northEastMoves.add(Board.getSquare(x-1, y+1));
			if (y-1 >= 0) this.northWestMoves.add(Board.getSquare(x-1, y-1));
		}
		
		// Check for moves down
		if (x+1 <= 7) {
			this.southMoves.add(Board.getSquare(x+1, y));
			if (y+1 <= 7) this.southEastMoves.add(Board.getSquare(x+1, y+1));
			if (y-1 >= 0) this.southWestMoves.add(Board.getSquare(x+1, y-1));
		}
		
		// Check for moves left and right
		if (y+1 <= 7) this.eastMoves.add(Board.getSquare(x, y+1));
		if (y-1 >= 0) this.westMoves.add(Board.getSquare(x, y-1));
		
		// Check for possible castles
		this.checkLeftCastle(Main.getBoardReversed(), x, y);
		this.checkRightCastle(Main.getBoardReversed(), x, y);
		
		// Add all to the possible Moves
		this.getPossibleMoves().add(this.southWestMoves);
		this.getPossibleMoves().add(this.southEastMoves);
		this.getPossibleMoves().add(this.northEastMoves);
		this.getPossibleMoves().add(this.northWestMoves);
		this.getPossibleMoves().add(this.eastMoves);
		this.getPossibleMoves().add(this.westMoves);
		this.getPossibleMoves().add(this.northMoves);
		this.getPossibleMoves().add(this.southMoves);
		
		// Validate moves
		validateMoves(this.getPossibleMoves());
	}
}
