package chess.pieces;

import java.awt.Color;
import java.awt.Image;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import chess.Main;
import chess.Move;
import chess.Player;
import chess.Square;

// Chess Pieces icons from https://commons.wikimedia.org/wiki/Category:PNG_chess_pieces/Standard_transparent

public abstract class Piece {
	private Player owner;
	protected final ArrayDeque<Square> validSquares = new ArrayDeque<Square>();
	private ArrayDeque<Square> coveringSquares = new ArrayDeque<Square>();
	private final ArrayDeque<ArrayDeque<Square>> possibleMoves = new ArrayDeque<ArrayDeque<Square>>();
	protected boolean firstMove = true;
	private boolean isCaptured = false;
	private int pointValue;
	private Image image;
	private String name;
	
// =================================== POSSIBLE MOVE LINES DEQUES ===================================
	
	protected final ArrayDeque<Square> northMoves = new ArrayDeque<Square>();
	protected final ArrayDeque<Square> northEastMoves = new ArrayDeque<Square>();
	protected final ArrayDeque<Square> eastMoves = new ArrayDeque<Square>();
	protected final ArrayDeque<Square> southEastMoves = new ArrayDeque<Square>();
	protected final ArrayDeque<Square> southMoves = new ArrayDeque<Square>();
	protected final ArrayDeque<Square> southWestMoves = new ArrayDeque<Square>();
	protected final ArrayDeque<Square> westMoves = new ArrayDeque<Square>();
	protected final ArrayDeque<Square> northWestMoves = new ArrayDeque<Square>();
	
// =================================== CONSTRUCTOR ===================================
	
	Piece(final Player owner, final int pointValue, final String name) {
		this.owner = owner;
		this.pointValue = pointValue;
		this.name = name;
		if (this.owner.isLightPieces()) {
			try {
				this.image = ImageIO.read(this.getClass().getResource("/light_"+this.name.toLowerCase()+".png"));
			} catch (IOException e) {
				System.out.println(e);
			}
		} else {
			try {
				this.image = ImageIO.read(this.getClass().getResource("/dark_"+this.name.toLowerCase()+".png"));
			} catch (IOException e) {
				System.out.println(e);
			}
		}
		
	}
	
// =================================== GETTER METHODS ===================================	
	
	public Player getOwner() {return this.owner;}
	public boolean isFirstMove() {return this.firstMove;}
	public boolean isCaptured() {return this.isCaptured;}
	public Image getImage() {return this.image;}
	public String getName() {return this.name;}
	public ArrayDeque<Square> getValidSquares() {return this.validSquares;}
	public ArrayDeque<Square> getCoveringSquares() {return this.coveringSquares;}
	public ArrayDeque<ArrayDeque<Square>> getPossibleMoves() {return this.possibleMoves;}
	public int getPointValue() {return this.pointValue;}
	
// =================================== SETTER METHODS ===================================
	
	public void setCaptured(Piece piece, boolean isCaptured) {
		this.isCaptured = isCaptured;
		Main.getCurrentPlayer().calculatePoints(this.pointValue);
		Main.getCurrentPlayer().getPlayerBox().getCapturedPiecesArea().add(new JLabel(new ImageIcon(piece.image.getScaledInstance(20, 20, Image.SCALE_DEFAULT))));
		this.getOwner().getPlayerPieces().remove(this);
		this.getOwner().getCoveredLines().remove(this);
	}
	
	public void notFirstMove() {
		this.firstMove = false;
	}
	
// =================================== ABSTRACT METHOD ===================================
	
	public abstract void findMoves(Square current);
	
// =================================== HANDLER METHODS ===================================
	
/*
 * Set to false after the piece's first move.
 * @Override in Pawn class.
 * 
 */
	public void handleFirstMove() {
		if (this.isFirstMove()) this.notFirstMove();
	}
	
/*
 * HANDLE PIECE CAPTURE.
 * If piece is captured, then set the algebraic notation and capture the piece.
 * Checks if piece is existent and belongs to opponent.
 * @Override in Pawn class
 */
	
	public void handleCapture(Move move) {
		if (move.getCurrentPiece() != null && this.isOpponentPiece(move.getCurrent())) {
			move.getCurrentPiece().setCaptured(move.getCurrentPiece(), true);
			move.setSpecial("x");
		}
	}
	
// =================================== MAIN VALIDATION METHOD ===================================

/*
 * Every turn, we validate the piece moves using the method below. Each connect to a separate method as described above.
 */
	
	public void validateMoves(ArrayDeque<ArrayDeque<Square>> array) {
		array.forEach(arr -> {
			this.lineContainsKing(arr);
			this.checkCoveredSquares(arr);
			this.checkValidSquares(arr);
			this.isLegalMove();
			this.filterKingMoves();
			this.filterCheckedMoves();
		});

	}
	
// =================================== OTHER METHOD ===================================
		
	public void clearMoves() {
		this.deHighlightMoves();
		this.coveringSquares.clear();
		this.possibleMoves.forEach(arr -> arr.clear());
		this.possibleMoves.clear();
		this.validSquares.clear();
	}
	
	public void highlightMoves() {				
		this.validSquares.forEach((sq) -> {
			sq.getBtn().setBackground(Color.CYAN);
		});
		
	}
	
	public void deHighlightMoves() {				
		this.validSquares.forEach((el) -> {
			el.getBtn().setBackground(el.getCurrentBGColour());
		});
	}
	
	public boolean isOpponentPiece(Square sq) {				
		if (this.getOwner().isLightPieces() != sq.getPiece().getOwner().isLightPieces()) return true;
		return false;
	}
	
	

// =================================== VALIDATION METHOD ===================================
	
/*
 * CHECK IF LINE CONTAINS OPPONENT KING
 * If it does, iterate through it to only get the line from the piece to the King. Store it for later to check if a player can move a piece without checking their own King.
 */
	
	private void lineContainsKing(ArrayDeque<Square> possible) {
	
		Optional<Square> opponentKingSquare = possible.stream().filter(sq -> sq.getPiece() instanceof King && sq.getPiece().getOwner().isLightPieces() != this.getOwner().isLightPieces()).findFirst();
		if (opponentKingSquare.isPresent()) {
			ArrayDeque<Square> temp = new ArrayDeque<Square>();
			for (Square sq : possible) {
				temp.add(sq);
				if (sq.equals(opponentKingSquare.get())) break;
			}
			temp.add(this.getOwner().getPlayerPieces().get(this));
			this.getOwner().getCoveredLines().put(this, temp);
		}
	}
	
/*
 *  CHECK FOR COVERED SQUARES
 *  Works for all pieces except pawns because they have weird covering rules.
 *  Evaluates the attacking line to check whether the King can move to this square.
 *  If there is a piece in the way (any piece which is NOT the opponent's King), then stop covering the line.
 *  
 *  @param the line's array (eg. northMoves, southMoves, etc.)
 *  
 */
		
	private void checkCoveredSquares(ArrayDeque<Square> possible) {
		if (this instanceof Pawn) return;
		
		boolean hitPiece = false;
		for (Square sq : possible) {
			if (!hitPiece) this.coveringSquares.add(sq);
			if (sq.getPiece() != null && !(this instanceof Knight) && (sq.getPiece().getOwner().isLightPieces() == this.getOwner().isLightPieces() || (sq.getPiece().getOwner().isLightPieces() != this.getOwner().isLightPieces() && !(sq.getPiece() instanceof King)))) hitPiece = true; 
		}
	}
	
/*
 *  CHECK FOR VALID SQUARES
 *  Evaluates the attacking line. If there is a piece in the way, then stop covering the line.
 *  These squares will be the valid moves for the piece.
 *  
 *  @param the line's array (eg. northMoves, southMoves, etc.)
 *  
 */
	
	private void checkValidSquares(ArrayDeque<Square> possible) {
		boolean hitPiece = false;
				
		for (Square sq : possible) {
			if (!hitPiece && (sq.getPiece() == null || sq.getPiece().getOwner().isLightPieces() != this.getOwner().isLightPieces())) this.validSquares.add(sq);
			if (sq.getPiece() != null && !(this instanceof Knight) && !(this instanceof Pawn)) hitPiece = true;
		}
	}
	
/*
 * CHECK IF PIECE WOULD TRIGGER A CHECK
 */
		
	private void isLegalMove() {
		if (this instanceof King || this.getOwner().getOpponent().getCoveredLines().isEmpty() || this.getOwner().getOpponent().getCoveredLines() == null) return;
		
		ArrayDeque<Square> temp = new ArrayDeque<Square>();
		Square currentSquare = this.getOwner().getPlayerPieces().get(this);
		int count = 0;
		
		// Find the line with the current square		
		for (ArrayDeque<Square> squares : this.getOwner().getOpponent().getCoveredLines().values()) {
			if (squares.contains(currentSquare) && currentSquare.getPiece().equals(this)) {
				temp.addAll(squares);
				break;
			}
		}
		
		if (temp.isEmpty() || !temp.contains(currentSquare)) return;
		
		for (Square sq : temp) {
//				if (sq.getPiece() != null && sq.getPiece().getOwner().isLightPieces() == this.getOwner().isLightPieces()) count++;
			if (sq.getPiece() != null) count++;
		}
		
		if (count <= 3) this.validSquares.retainAll(temp);
	}
	
	
	
/*
 *  FILTER KING MOVES
 *  If not a king, then exit.  
 *  
 *  Create a temporary array where we'll add squares covered by an opponent's piece.
 *  Remove those squares (if any) from the King's moves. 
 *    
 */
	
	private void filterKingMoves() {
		if (!(this instanceof King)) return;
		
		ArrayDeque<Square> temp = new ArrayDeque<Square>();
		this.validSquares.forEach(sq -> {
			for (Piece piece : this.getOwner().getOpponent().getPlayerPieces().keySet()) { 
				if (piece.getCoveringSquares().contains(sq)) temp.add(sq);
			}			
		});
		
		this.validSquares.removeAll(temp);
		
	}
	
/*
 *  FILTER CHECKED MOVES FROM PIECES
 *  If King, then exit.
 *  
 *  Create a temporary array where we'll compare an attacking piece's line squares with the pieces current valid moves.
 *  Remove the squares NOT in the intersection
 *    
 */
	
	private void filterCheckedMoves() {
		if (this instanceof King || !this.getOwner().isKingChecked()) return;
		
		HashSet<Square> temp = new HashSet<Square>();
		for (Piece p : this.getOwner().getAttackingPieces().keySet()) {
			this.getOwner().getAttackingPieces().get(p).forEach(sq -> {
				if (p.getValidSquares().contains(sq)) {
					temp.add(sq);
					if (this.getOwner().getAttackingPieces().size() < 2) temp.add(this.getOwner().getOpponent().getPlayerPieces().get(p));
				}
			});
			if (this.getOwner().getAttackingPieces().size() < 2) {
				this.validSquares.retainAll(temp);
			} else {
				this.validSquares.clear();
			}
		}
	}
}