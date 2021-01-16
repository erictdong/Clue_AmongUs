/**
 * @author Alexander Cieslewicz
 * @author Eric Dong
 * 
 * Create the game of AmongUs Clue
 */
package clueGame;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class ClueGame extends JFrame {
	public static final boolean JAR = false;
	Board gameBoard = Board.getInstance();
	GameControlPanel gameControlPanel;
	CardDisplayPanel cardDisplayPanel;

	/**
	 * Create a frame display the game control, cards and board for clue
	 * 
	 * @throws HeadlessException
	 */
	public ClueGame() throws HeadlessException {
		super();
		if (ClueGame.JAR) {
			gameBoard.setConfigFiles("ClueSetup.csv", "ClueSetup.txt");
		}
		else {
			gameBoard.setConfigFiles("./data/ClueSetup.csv", "./data/ClueSetup.txt");
		}

		gameBoard.initialize();
		gameBoard.deal();

		setLayout(new BorderLayout());
		cardDisplayPanel = new CardDisplayPanel(gameBoard.getCurrentPlayer());
		add(cardDisplayPanel, BorderLayout.EAST);
		gameControlPanel = new GameControlPanel(gameBoard, cardDisplayPanel);
		add(gameControlPanel, BorderLayout.SOUTH);
		add(gameBoard, BorderLayout.CENTER);

		gameBoard.setGameControl(gameControlPanel);

		setTitle("Among Us Clue");
		setSize(1000, 1000);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public Player getHumanPlayer() {
		return gameBoard.getPlayers().get(0);
	}

	public void playMusic() {
		try {
			AudioInputStream audioInputStream = null;
			if (ClueGame.JAR) {
				InputStream is = getClass().getResourceAsStream("among_us_theme.wav");
				if (is == null) {
					throw new FileNotFoundException("Could not find music");
				}
				audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));

			}
			else {
				audioInputStream = AudioSystem
						.getAudioInputStream(new File("resources/music/among_us_theme.wav").getAbsoluteFile());
			}

			Clip clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(-10.0f); // Reduce volume by 10 decibels.

			clip.start();
		}
		catch (Exception e) {
			System.out.println("Music is not working.");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// Create the game instance
		ClueGame amongUs = new ClueGame();
		amongUs.setVisible(true);

		// Show the default splash
		Player humanPlayer = amongUs.getHumanPlayer();
		JOptionPane.showMessageDialog(amongUs, "There is an imposter among us. \n You are " + humanPlayer.getName()
				+ ". Can you find the \n imposter before the other Computer players?");

		// Update the game state for the first turn
		amongUs.gameBoard.updateBoardState();
		amongUs.cardDisplayPanel.updateCardDisplay();
		amongUs.gameControlPanel.updateDisplay();

		amongUs.playMusic();
	}
}
