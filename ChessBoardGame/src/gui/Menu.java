
package gui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Iterator;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import chess.Main;
import chess.Move;

public class Menu {
	private final JMenuBar mainMenu = new JMenuBar();
	private final JFrame frame;
	private final JFileChooser fileChooser = new JFileChooser();
	private final JMenu fileMenu = new JMenu("File");
	private final JMenuItem newGame = new JMenuItem("New Game");
	private final JMenuItem open = new JMenuItem("Open");
	private final JMenuItem save = new JMenuItem("Save");
	private final JMenuItem exit = new JMenuItem("Exit");
	
// =================================== CONSTRUCTOR ===================================
	
	public Menu(JFrame frame) {
		this.frame = frame;
		// Layout Menu		
		this.fileMenu.add(newGame);
		this.fileMenu.addSeparator();
		this.fileMenu.add(open);
		this.fileMenu.add(save);
		this.fileMenu.addSeparator();
		this.fileMenu.add(exit);
		
		// File saver and opener defaults
		this.fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("*.txt", "txt"));
		
		// newGame Button clicked
		newGame.addActionListener(event -> {
			Main.setupNewGame(Main.getBoard());
		});
		
/*
 * When the Open or Save buttons are clicked, then open a fileChooser. If a file is selected for opening or saving, then we'll handle them appropriately.
 * See below for handler methods.
 * 
 */
		// Open button clicked
		open.addActionListener(event -> {
			int result = this.fileChooser.showOpenDialog(frame);
			if (result == JFileChooser.APPROVE_OPTION) {
				File file = this.fileChooser.getSelectedFile();
				this.handleOpen(file);
			}
		});
		
		// Save button clicked
		save.addActionListener(event -> {
			int result = this.fileChooser.showSaveDialog(frame);
			if (result == JFileChooser.APPROVE_OPTION) {
				File file = this.fileChooser.getSelectedFile();
				this.handleSave(file);
			}
		});
		
		// Exit button clicked
		exit.addActionListener(event -> {
			System.exit(0);
		});
		
		// Add File menu and set the menu bar.		
		mainMenu.add(fileMenu);	
		frame.setJMenuBar(mainMenu);
	}

// =================================== HANDLER FUNCTIONS ===================================
	
/*
 * Handle saving files.
 * Opens a FileWriter using a try-catch-with resources. This handles writer closes automatically.
 * Checks if the user entered the file format. We're only accepting .txt files to create pseudo-PGN files.
 * See the PGN format here: http://www.saremba.de/chessgml/standards/pgn/pgn-complete.htm
 * 
 * We only need limited data to open a file later: Who is the light pieces, and which moves were played.
 * We'll take advantage of the Move class to handle everything else just like a regular game.
 * 
 */
	
	private void handleSave(File file) {
		String fileName = file.getAbsolutePath();
		int turnCount = 0;
		
		if (!fileName.endsWith(".txt")) {
			fileName = fileName+".txt";
		}
		
		try (	FileWriter fw = new FileWriter(fileName, StandardCharsets.US_ASCII);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter pw = new PrintWriter(bw))
		{
			int i = 0;
			
			// Save which player is the light / dark pieces
			if (Main.getP1().isLightPieces()) {
				pw.println("[White p1]");
			} else {
				pw.println("[White p2]");
			}
			
			// Iterate through the moves, add the move # and print the algebraic notation
			for (Iterator<String> it = Main.getMovePanel().getMoves().iterator(); it.hasNext(); i++) {
				if (i % 2 == 0) {
					turnCount++;
					pw.print(turnCount+". ");
				}
				
				pw.print(it.next()+" ");
			}
			
		} catch (IOException e) {
			System.out.println("Error: "+e);
		}
	}
	
/*
 * Handle file openings. We'll read the .txt file line by line.
 */
	private void handleOpen(File file) {
		try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.US_ASCII))) {
			String line;
			ArrayDeque<String> moveSet = null;
			
			if (!file.getName().endsWith(".txt")) JOptionPane.showMessageDialog(this.frame, "Error loading game. Invalid type or contains invalid moves.", "Error loading game.", JOptionPane.ERROR_MESSAGE);;
			
			
			// Start a new game
			
			while ((line = br.readLine()) != null) {				
				// Check
				if (line.startsWith("[White p1")) {
					Main.setBoardReversed(true);
					Main.setupLoadedGame(Main.getBoard(), Main.getP1());
					continue;
				} else if (line.startsWith("[White p2")) {
					Main.setBoardReversed(false);
					Main.setupLoadedGame(Main.getBoard(), Main.getP2());
					continue;
				} else {
					moveSet = new ArrayDeque<String>(Arrays.asList(line.split("\s")));
				}
			}
			
			// Sets up turns
			// If there's an error with the file, then we'll pop up an error to tell the user that there was an error loading the game.
			try {
				moveSet.removeIf(el -> el.matches("^\\d+\\.$"));
				for (String el : moveSet) {
					new Move(el);
				}	
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this.frame, "Error loading game. Invalid type or contains invalid moves.", "Error loading game.", JOptionPane.ERROR_MESSAGE);
			}

			
		} catch (IOException e) {
			System.out.println("Error: "+e);
		}
		
	}
	
}
