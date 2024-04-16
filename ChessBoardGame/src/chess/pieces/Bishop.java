
package chess.pieces;

import chess.Player;
import chess.Square;
import gui.Board;

public final class Bishop extends Piece {
	
// =================================== CONSTRUCTOR ===================================
	public Bishop(Player owner) {
		super(owner, 3, "B");
	}
	
// =================================== FIND MOVES METHOD ===================================
	
	public void findMoves(Square current) {
		int x = current.getX();
		int y = current.getY();
		Square sq;
		
		// Creates an array which will add moves diagonally to the appropriate moves array.
		
		for (int i = 1; i <= 7; i++) {
			if (x+i <=7 && y+i <= 7) {
				sq = Board.getSquare(x+i, y+i);
				this.southEastMoves.add(sq);
			}
			
			if (x+i <=7 && y-i >= 0) {
				sq = Board.getSquare(x+i, y-i);
				this.southWestMoves.add(sq);
			}
			if (x-i >=0 && y+i <= 7) {
				sq = Board.getSquare(x-i, y+i);
				this.northEastMoves.add(sq);
			}
			if (x-i >=0 && y-i >= 0) {
				sq = Board.getSquare(x-i, y-i);
				this.northWestMoves.add(sq);
			}
		}
		
		this.getPossibleMoves().add(this.southEastMoves);
		this.getPossibleMoves().add(this.southWestMoves);
		this.getPossibleMoves().add(this.northEastMoves);
		this.getPossibleMoves().add(this.northWestMoves);
		
		validateMoves(this.getPossibleMoves());
	}
}
