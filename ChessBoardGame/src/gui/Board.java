
package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import chess.Player;
import chess.Square;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;

public class Board {
	private final static Square[][] boardArray = new Square[8][8];
	
// =================================== CONSTRUCTOR ===================================
	
	public Board(final JPanel panel) {
		final JPanel boardGrid = new JPanel(new GridLayout(8, 8));
		
		// Sets up an 8x8 grid of Squares.
		for (int i = 0; i < boardArray.length; i++) {
			for (int j = 0; j < boardArray[i].length; j++) {
				boardArray[i][j] = new Square(i, j);
				boardGrid.add(boardArray[i][j].getBtn());
			}
		}
		
		boardGrid.setBorder(new EmptyBorder(15, 0, 5, 0));
		panel.add(boardGrid, BorderLayout.LINE_START);
	}
	
// =================================== GETTER METHODS ===================================
	
	public static Square getSquare(int x, int y) {return boardArray[x][y];}
	
	
// =================================== OTHER METHODS ===================================
	
/*
 * RESETS THE BOARD
 * Removes all pieces from the board and resets player pieces
 */
	
	public void resetBoard(final Player p1, final Player p2, boolean isBoardReversed) {
		
		// 
		for (int i = 0; i < boardArray.length; i++) {
			for (int j = 0; j < boardArray[i].length; j++) {
				boardArray[i][j].setPiece(null);
				boardArray[i][j].setCurrentBGColour(boardArray[i][j].getOriginalBGColour());
				boardArray[i][j].getBtn().setBackground(boardArray[i][j].getOriginalBGColour());
			}
		}
		
		// Set up pieces for each player.
		this.setupBoard(p2, isBoardReversed, 1, 0);
		this.setupBoard(p1, isBoardReversed, 6, 7);
	}
		
/*
 * SETUP THE BOARD for the players
 * Remove all pieces for the player, and sets a fresh set of pieces.
 */
	
	private void setupBoard(final Player player, boolean isBoardReversed, int pawnX, int pieceX) {
		
		// Resets all Arrays
		player.getPlayerPieces().clear();
		player.getAttackingPieces().clear();
		player.getCoveredLines().clear();
		player.getCollectiveMoves().clear();
		
		// Add pawns
		for (int i = 0; i < boardArray.length; i++) {
			if (pawnX == 1) boardArray[pawnX][i].setPiece(new Pawn(player, true));
			else {
				boardArray[pawnX][i].setPiece(new Pawn(player, false));
			}
		}
		
		// Add pieces
		boardArray[pieceX][0].setPiece(new Rook(player));
		boardArray[pieceX][1].setPiece(new Knight(player));
		boardArray[pieceX][2].setPiece(new Bishop(player));
		boardArray[pieceX][5].setPiece(new Bishop(player));
		boardArray[pieceX][6].setPiece(new Knight(player));
		boardArray[pieceX][7].setPiece(new Rook(player));
		
		// Flip King and Queen position if board is reversed.
		// If board is reversed, then dark pieces are on the bottom.
		if (isBoardReversed) {
			boardArray[pieceX][4].setPiece(new King(player));	
			boardArray[pieceX][3].setPiece(new Queen(player));
		} else {
			boardArray[pieceX][3].setPiece(new King(player));
			boardArray[pieceX][4].setPiece(new Queen(player));
		}
		
		// Add pieces to Player Pieces.
		for (int i = 0; i < boardArray.length; i++) {
			player.getPlayerPieces().put(boardArray[pawnX][i].getPiece(), boardArray[pawnX][i]); // Add pawns
			player.getPlayerPieces().put(boardArray[pieceX][i].getPiece(), boardArray[pieceX][i]); // Add pieces
		}
		
		// Refresh the player's moves.
		player.refreshMoves();

	}
}
