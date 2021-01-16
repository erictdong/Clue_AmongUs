/**
 * @author Alexander Cieslewicz
 * @author Eric Dong
 * 
 * Create the Panel for controlling the game
 */
package clueGame;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class GameControlPanel extends JPanel {

	JTextField rollNumber;
	JTextField player;
	JTextField guess;
	JTextField guessResult;
	CardDisplayPanel cardDisplayPanel;
	Board board;

	public GameControlPanel() {
		setLayout(new GridLayout(2, 0));
		JPanel movementControlPanel = createMovementPanel();
		add(movementControlPanel);

		JPanel guessPanel = createGuessPanel();
		add(guessPanel);
	}

	public GameControlPanel(Board board, CardDisplayPanel cardDisplayPanel) {
		this();
		this.board = board;
		this.cardDisplayPanel = cardDisplayPanel;
	}

	/**
	 * Create a panel that allows for game control
	 * 
	 * @return JPanel
	 */
	private JPanel createMovementPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 4));

		JPanel turnPanel = createTurnPanel();
		panel.add(turnPanel);

		JPanel rollPanel = createRollPanel();
		panel.add(rollPanel);

		JButton makeAccusation = new JButton("Make Accusation");
		makeAccusation.addActionListener(new AccListener());
		panel.add(makeAccusation);
		JButton nextPlayer = new JButton("NEXT!");

		nextPlayer.addActionListener(new NextListener());
		panel.add(nextPlayer);

		return panel;
	}

	/**
	 * Action lister for next button, calls board methods to update game state
	 */
	private class NextListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!board.isPlayerFinished()) {
				JOptionPane.showMessageDialog(board, "Move your player before ending your turn.");
				return;
			}

			// Update Board States
			board.updateCurrentPlayer();
			board.rollDice();
			cardDisplayPanel.updateCardDisplay();
			board.updateBoardState();

			// Update Panel Information
			setTurn(board.getCurrentPlayer(), board.getDiceRoll());
			updateSuggestions();
			repaint();
		}

	}

	/**
	 * Action lister for accusation button, calls make accusation when pressed
	 */
	private class AccListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			board.makeAccusation();
		}

	}

	/**
	 * Update current suggestion and result display in game control panel
	 */
	public void updateSuggestions() {
		Player currentPlayer = board.getCurrentPlayer();
		Solution currentSuggestion = board.getCurrentSuggestion();

		if (currentSuggestion != null) {
			// Handle displaying suggestion information if suggestion exists
			guess.setText(currentSuggestion.toString());
			guess.setBackground(board.getCurrentPlayer().getColor());

			// Show disprove result
			if (board.getCurrentDisprove() != null) {
				if (board.getCurrentPlayer().isHuman()) {
					guessResult.setText("Disproving Card: " + board.getCurrentDisprove().toString());
				}
				else {
					guessResult.setText("Suggestion Disproved.");
				}

			}
			else {
				guessResult.setText("No new clue");
			}
		}
		else {
			// Reset display for the next player
			guess.setText("");
			guess.setBackground(getBackground());
			guessResult.setText("");

		}

		if (currentPlayer.isHuman()) {
			board.setPlayerFinished(false);

		}
		cardDisplayPanel.updateCardDisplay();
		repaint();
	}

	// Create the lower half of the panel displaying guess information
	private JPanel createGuessPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 2));
		JPanel guessPanel = createGuessDisplayPanel();
		panel.add(guessPanel);

		JPanel guessResultPanel = createGuessResultPanel();
		panel.add(guessResultPanel);
		return panel;
	}

	// Create left half of guessPanel displaying the guess
	private JPanel createGuessDisplayPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 0));
		panel.setBorder(new TitledBorder(new EtchedBorder(), "Guess"));

		guess = new JTextField();
		guess.setEditable(false);
		panel.add(guess);

		return panel;
	}

	// Create right half of guess panel displaying guess results
	private JPanel createGuessResultPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(1, 0));
		panel.setBorder(new TitledBorder(new EtchedBorder(), "Guess Result"));

		guessResult = new JTextField();
		guessResult.setEditable(false);
		panel.add(guessResult);

		return panel;
	}

	// Create the turn panel in the movement panel displaying who's turn it is
	private JPanel createTurnPanel() {
		JPanel panel = new JPanel();

		JLabel label = new JLabel("Whose turn?");
		panel.add(label);

		player = new JTextField("", 10);
		player.setEditable(false);
		panel.add(player);

		return panel;
	}

	// Create the dice roll panel displaying current dice roll in the movement panel
	private JPanel createRollPanel() {
		JPanel panel = new JPanel();
		JLabel label = new JLabel("Roll:");
		panel.add(label);

		rollNumber = new JTextField("", 10);
		rollNumber.setEditable(false);
		panel.add(rollNumber);

		return panel;
	}

	// Update the display to show the current player turn
	public void setTurn(Player player, int roll) {
		this.rollNumber.setText(Integer.toString(roll));
		this.player.setText(player.getName());
		this.player.setBackground(player.getColor());
	}

	/**
	 * updates the current display to correctly reflect the current player and die
	 * roll
	 */
	public void updateDisplay() {
		setTurn(board.getCurrentPlayer(), board.getDiceRoll());
		repaint();
	}

	/*
	 * Getters & Setters
	 */

	public void setGuess(String guess) {
		this.guess.setText(guess);
	}

	public void setGuessResult(String guessResult) {
		this.guessResult.setText(guessResult);
	}

	public static void main(String[] args) {
		GameControlPanel panel = new GameControlPanel();
		// create the panel 2x0
		JFrame frame = new JFrame();
		// create the frame
		frame.setContentPane(panel);// put the panel in the frame
		frame.setSize(750, 180); // size the frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // allow it to close
		frame.setVisible(true); // make it visible

		// test filling in the data
		panel.setTurn(new ComputerPlayer("Orange", Color.orange, 0, 0), 5);
		panel.setGuess("I have no guess!");
		panel.setGuessResult("So you have nothing?");
	}
}
