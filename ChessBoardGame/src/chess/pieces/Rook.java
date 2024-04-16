
package chess.pieces;

import chess.Player;
import chess.Square;
import gui.Board;

public final class Rook extends Piece {
	
	public Rook(Player owner) {
		super(owner, 5, "R");
	}
	
	public void findMoves(Square current) {
		int x = current.getX();
		int y = current.getY();
		
		// Add moves horizontally and vertically. Sets the index to the value of X and Y coordinates as required.
		
		for (int i = x+1; i <= 7; i++) {
			this.southMoves.add(Board.getSquare(i, y));
		}
		for (int i = x-1; i >= 0; i--) {
			this.northMoves.add(Board.getSquare(i, y));
		}
		for (int i = y+1; i <= 7; i++) {
			this.eastMoves.add(Board.getSquare(x, i));
		}
		for (int i = y-1; i >= 0; i--) {
			this.westMoves.add(Board.getSquare(x, i));
		}
		
		
		this.getPossibleMoves().add(this.eastMoves);
		this.getPossibleMoves().add(this.westMoves);
		this.getPossibleMoves().add(this.northMoves);
		this.getPossibleMoves().add(this.southMoves);
		
		validateMoves(this.getPossibleMoves());
	}
}
