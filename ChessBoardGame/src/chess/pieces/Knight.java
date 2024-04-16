package chess.pieces;

import java.util.ArrayDeque;

import chess.Player;
import chess.Square;
import gui.Board;

public final class Knight extends Piece {
	
// =================================== CONSTRUCTOR ===================================
	public Knight(Player owner) {
		super(owner, 3, "N");
	}
	
// =================================== FIND MOVES METHOD ===================================
	
	public void findMoves(Square current) {
		ArrayDeque<Square> tempMoves = new ArrayDeque<Square>();
		int x = current.getX();
		int y = current.getY();
		
		// No need to worry about pieces in front as the knight can jump.
		
		if (x-2 >= 0) {
			if (y-1 >= 0) tempMoves.add(Board.getSquare(x-2, y-1));
			if (y+1 <= 7) tempMoves.add(Board.getSquare(x-2, y+1));
		}
		
		if (x-1 >= 0) {
			if (y-2 >= 0) tempMoves.add(Board.getSquare(x-1, y-2));
			if (y+2 <= 7) tempMoves.add(Board.getSquare(x-1, y+2));
		}
		
		if (x+1 <= 7) {
			if (y-2 >= 0) tempMoves.add(Board.getSquare(x+1, y-2));
			if (y+2 <= 7) tempMoves.add(Board.getSquare(x+1, y+2));
		}
		
		if (x+2 <= 7) {
			if (y-1 >= 0) tempMoves.add(Board.getSquare(x+2, y-1));
			if (y+1 <= 7) tempMoves.add(Board.getSquare(x+2, y+1));
		}

		this.getPossibleMoves().add(tempMoves);
		
		validateMoves(this.getPossibleMoves());
	}
}