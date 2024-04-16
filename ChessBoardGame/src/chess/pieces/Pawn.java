package chess.pieces;

import java.io.IOException;
import java.util.ArrayDeque;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import chess.Main;
import chess.Move;
import chess.Player;
import chess.Square;
import gui.Board;

public final class Pawn extends Piece {
	private boolean moveTopDown;
	private Square leftDiag, rightDiag;
	private boolean secondMove = false;	
	private Square enPassantPiece;
	
	// Promotion popup	
	Piece promotionPiece;
	Object[] promotionOptions = new Object[4];
	JButton qBtn = new JButton();
	JButton rBtn = new JButton();
	JButton nBtn = new JButton();
	JButton bBtn = new JButton();
	
// =================================== CONSTRUCTOR ===================================
	
	public Pawn(Player owner, boolean topDown) {
		super(owner, 1, "");
		this.moveTopDown = topDown;
		
		// Set images for promotion popup.
		try {
			if (this.getOwner().isLightPieces() ) {
				this.qBtn.setIcon(new ImageIcon(ImageIO.read(this.getClass().getResource("/light_q.png"))));
				this.rBtn.setIcon(new ImageIcon(ImageIO.read(this.getClass().getResource("/light_r.png"))));
				this.nBtn.setIcon(new ImageIcon(ImageIO.read(this.getClass().getResource("/light_n.png"))));
				this.bBtn.setIcon(new ImageIcon(ImageIO.read(this.getClass().getResource("/light_b.png"))));
			} else {
				this.qBtn.setIcon(new ImageIcon(ImageIO.read(this.getClass().getResource("/dark_q.png"))));
				this.rBtn.setIcon(new ImageIcon(ImageIO.read(this.getClass().getResource("/dark_r.png"))));
				this.nBtn.setIcon(new ImageIcon(ImageIO.read(this.getClass().getResource("/dark_n.png"))));
				this.bBtn.setIcon(new ImageIcon(ImageIO.read(this.getClass().getResource("/dark_b.png"))));
			}
		} catch (IOException e) {
			System.out.println(e);
		}
		
		// Set the button options and relevant pieces to each button.
		this.promotionOptions[0] = qBtn;
		this.promotionOptions[1] = rBtn;
		this.promotionOptions[2] = nBtn;
		this.promotionOptions[3] = bBtn;
		
		this.qBtn.addActionListener(e -> {
			this.promotionPiece = new Queen(this.getOwner());
			JOptionPane.getRootFrame().dispose();		
		});
		this.rBtn.addActionListener(e -> {
			this.promotionPiece = new Rook(this.getOwner());
			JOptionPane.getRootFrame().dispose();
			});
		this.nBtn.addActionListener(e -> {
			this.promotionPiece = new Knight(this.getOwner());
			JOptionPane.getRootFrame().dispose();
			});
		this.bBtn.addActionListener(e -> {
			this.promotionPiece = new Bishop(this.getOwner());
			JOptionPane.getRootFrame().dispose();
			});
	}
	
// =================================== GETTER METHODS ===================================
	
	public boolean isSecondMove() {return this.secondMove;}
	public boolean getTopDown() {return this.moveTopDown;}
	public Square getLeftDiag() {return this.leftDiag;}
	public Square getRightDiag() {return this.rightDiag;}
	public Piece getPromotionPiece() {return this.promotionPiece;}
	public Object[] getPromotionOptions() {return this.promotionOptions;}
	public Square getEnPassant() {return this.enPassantPiece;}

// =================================== SETTER METHODS ===================================
	
	public void setEnPassant(Square sq) {
		this.enPassantPiece = sq;
	}
	
	public void setSecondMove(boolean isSecond) {
		this.secondMove = isSecond;
	}
	
	public void setPromotionPiece(Piece piece) {
		this.promotionPiece = piece;
	}
	
// =================================== OVERRIDE METHODS ===================================
	
	@Override
	public void handleFirstMove() {
		if (this.isSecondMove()) this.setSecondMove(false);
		if (this.isFirstMove()) {
			this.notFirstMove();
			this.setSecondMove(true);
		}
	}
		
	@Override
	public void handleCapture(Move move) {
		if (move.getCurrentPiece() != null && this.isOpponentPiece(move.getCurrent())) {
			move.getCurrentPiece().setCaptured(move.getCurrentPiece(), true);
			move.setSpecial((Main.getBoardReversed()) ? move.getRank().charAt(move.getLast().getY())+"x" : move.getRank().charAt(7-move.getLast().getY())+"x");
		}
	}
	
// =================================== OTHER METHODS ===================================
	
	// Reverse direction of piece movement
	
	private int moveDirection(int num) {
		if (moveTopDown) {return num * 1;}
		return num * -1;
	}
	
	// Used to check En Passant moves.
	private void addEnPassantMove(ArrayDeque<Square> array, Square item, int x, int y) {
		if (item.getPiece() != null && item.getPiece() instanceof Pawn && this.isOpponentPiece(item) && Main.getMovePanel().getLastMove().getCurrentPiece() == item.getPiece() && ((Pawn) item.getPiece()).isSecondMove()) {
			array.add(Board.getSquare(x, y));
			this.setEnPassant(item);
		}
	}

// =================================== MAIN MOVE FINDER METHOD ===================================
	public void findMoves(Square current) {
		ArrayDeque<Square> tempMoves = new ArrayDeque<Square>();
		int x = current.getX();
		int y = current.getY();
		int possibleNegY = y-1;
		int possiblePosY = y+1;
		int possibleX = x+moveDirection(1);
		
		// Default possible moves
		if (possibleX >= 0 && possibleX <= 7) {
			if (Board.getSquare(possibleX, y).getPiece() == null) {tempMoves.add(Board.getSquare(possibleX, y));}
			if (this.isFirstMove() && Board.getSquare(possibleX, y).getPiece() == null && Board.getSquare(x+moveDirection(2), y).getPiece() == null) {tempMoves.add(Board.getSquare(x+moveDirection(2), y));}

			
			// Check diagonals			
			if (possibleNegY >= 0) {
				this.leftDiag = Board.getSquare(possibleX, possibleNegY);
				this.getCoveringSquares().add(this.leftDiag);
				if (this.leftDiag.getPiece() != null) {tempMoves.add(this.leftDiag);}
			}
			if (possiblePosY <= 7) {
				this.rightDiag = Board.getSquare(possibleX, possiblePosY);
				this.getCoveringSquares().add(this.rightDiag);
				if (this.rightDiag.getPiece() != null) {tempMoves.add(this.rightDiag);}
			}
		}
		
		// Check en passant
		if ((!this.getTopDown() && x == 3) || (this.getTopDown() && x == 4)) {
			if (possibleNegY >= 0 && possibleNegY <= 7) addEnPassantMove(tempMoves, Board.getSquare(x, possibleNegY), possibleX, possibleNegY);
			if (possiblePosY >= 0 && possiblePosY <= 7) addEnPassantMove(tempMoves, Board.getSquare(x, possiblePosY), possibleX, possiblePosY);
		} 
		
		this.getPossibleMoves().add(tempMoves);
		
		validateMoves(this.getPossibleMoves());
	}
	
// =================================== HANDLER METHODS ===================================

/*
 * HANDLE IF PAWN PERFORMS EN PASSANT.
 * If the pawn moves diagonally during an en passant and the square is empty, then assume a successful en passant.
 * Remove the piece beside the pawn (if it's a pawn).
 * If the player does not take advantage of en passant, then set it to null.
 * 
 */
	
	public boolean handleEnPassantMove(Move move) {
		if (this.getEnPassant() == null) return false;
		
		if (move.getCurrent().getY() != move.getLast().getY() && Board.getSquare(move.getCurrent().getX(), move.getCurrent().getY()).getPiece() == null) {
			this.getEnPassant().getPiece().setCaptured(this.getEnPassant().getPiece(), true);
			if (Main.getBoardReversed()) {
				move.setRankAndFile(move.getRank().charAt(move.getLast().getY())+"x", null);
			} else {
				move.setRankAndFile(move.getRank().charAt(7-move.getLast().getY())+"x", null);
			}
			
			this.getEnPassant().setPiece(null);
			
			this.setEnPassant(null);
			return true;
		}
		
		this.setEnPassant(null);
		return false;
	}
	
/*
 * HANDLE IS PAWN PROMOTES.
 * Opens a popup with four buttons (see Pawn class) for pawn promotion.
 * Once selection is made, checks if piece captured, and sets piece to promoted option.
 * 
 */
	
	public boolean handlePawnPromotion(Move move) {
		if ((!this.getTopDown() && move.getCurrent().getX() != 0) || (this.getTopDown() && move.getCurrent().getX() != 7)) return false;
			
		if (!move.getPromotionPieceSet()) {
			JOptionPane.showOptionDialog(null, null, null, JOptionPane.PLAIN_MESSAGE, JOptionPane.PLAIN_MESSAGE, null, this.getPromotionOptions(), null);
			if (this.getPromotionPiece() == null) return handlePawnPromotion(move);
		}
		
		this.handleCapture(move);
		
		move.setRankAndFile(move.getSpecial(), "="+this.getPromotionPiece().getName());
		this.getOwner().calculatePoints(this.getPromotionPiece().getPointValue());
		this.getOwner().getPlayerPieces().remove(this);
		this.getOwner().getPlayerPieces().put(this.getPromotionPiece(), move.getLast());
		return true;
	}	
}