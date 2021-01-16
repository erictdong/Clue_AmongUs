/**
 * @author Alexander Cieslewicz
 * @author Eric Dong
 * 
 * Computer Player class for game
 */

package tests;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.Card;
import clueGame.CardType;
import clueGame.ComputerPlayer;
import clueGame.HumanPlayer;
import clueGame.Player;
import clueGame.Solution;

class GameSolutionTest {
	private static Board board;
	private static Card playerSolution;
	private static Card roomSolution;
	private static Card weaponSolution;
	private static Card playerBadSolution;
	private static Card roomBadSolution;
	private static Card weaponBadSolution;

	@BeforeAll
	public static void setUp() {
		// Board is singleton, get the only instance
		board = Board.getInstance();
		// set the file names to use my config files
		board.setConfigFiles("data/ClueSetup.csv", "data/ClueSetup.txt");
		// Initialize will load config files
		board.initialize();

		// Setup Test Cards
		playerSolution = new Card("Pink", CardType.PERSON);
		roomSolution = new Card("Energy", CardType.ROOM);
		weaponSolution = new Card("Spike", CardType.WEAPON);
		playerBadSolution = new Card("Red", CardType.PERSON);
		roomBadSolution = new Card("Shields", CardType.ROOM);
		weaponBadSolution = new Card("Gun", CardType.WEAPON);
	}

	@Test
	/**
	 * Check that checkAccusation if functioning correctly by verifying it returns
	 * the correct boolean
	 */
	void testCheckAccusation() {
		// Create and add a test Solution
		Solution testSolution = new Solution(playerSolution, roomSolution, weaponSolution);
		board.setTheAnswer(testSolution);

		// Check that returns true with correct answer
		assertTrue(board.checkAccusation(playerSolution, roomSolution, weaponSolution));

		// Test wrong person
		assertFalse(board.checkAccusation(playerBadSolution, roomSolution, weaponSolution));

		// Test wrong room
		assertFalse(board.checkAccusation(playerSolution, roomBadSolution, weaponSolution));

		// Test wrong weapon
		assertFalse(board.checkAccusation(playerSolution, roomSolution, weaponBadSolution));

		// Test all wrong
		assertFalse(board.checkAccusation(playerBadSolution, roomBadSolution, weaponBadSolution));
	}

	@Test
	/**
	 * Test that disprove suggestion is functioning properly by checking against
	 * known cards
	 */
	void testDisproveSuggestion() {
		// Create player and give player hand
		Player testPlayer = new HumanPlayer("Test1", Color.red, 0, 0);
		testPlayer.setUnseenCards(board.getDeck());

		testPlayer.updateHand(playerSolution);
		testPlayer.updateHand(roomSolution);
		testPlayer.updateHand(weaponSolution);

		// Test player with one matching suggestion of either weapon, player or room
		assertEquals(testPlayer.disproveSuggestion(playerSolution, roomBadSolution, weaponBadSolution), playerSolution);
		assertEquals(testPlayer.disproveSuggestion(playerBadSolution, roomSolution, weaponBadSolution), roomSolution);
		assertEquals(testPlayer.disproveSuggestion(playerBadSolution, roomBadSolution, weaponSolution), weaponSolution);

		// Check that if no cards matching returns null
		assertNull(testPlayer.disproveSuggestion(playerBadSolution, roomBadSolution, weaponBadSolution));

		// Simulate the suggestion 50 times and check that each card gets chosen at
		// least once
		int playerCount = 0, roomCount = 0, weaponCount = 0;
		for (int i = 0; i < 50; i++) {
			Card returnedCard = testPlayer.disproveSuggestion(playerSolution, roomSolution, weaponSolution);
			if (returnedCard.equals(playerSolution)) {
				playerCount++;
			}
			else if (returnedCard.equals(roomSolution)) {
				roomCount++;
			}
			else {
				weaponCount++;
			}
		}
		assertTrue(playerCount > 0);
		assertTrue(roomCount > 0);
		assertTrue(weaponCount > 0);
	}

	@Test
	/**
	 * Test that handle a suggestion returns the correct card or null
	 */
	void testHandleSuggestion() {
		// Create Test Players
		ArrayList<Player> testPlayers = new ArrayList<>();
		testPlayers.add(new HumanPlayer("Test1", Color.red, 0, 0));
		testPlayers.add(new ComputerPlayer("Test2", Color.red, 0, 0));
		testPlayers.add(new ComputerPlayer("Test3", Color.red, 0, 0));

		// Set unseen cards
		testPlayers.get(0).setUnseenCards(board.getDeck());
		testPlayers.get(1).setUnseenCards(board.getDeck());
		testPlayers.get(2).setUnseenCards(board.getDeck());

		// Set Test Player Hands. Since testDisprove suggestion verifies multicard hands
		// only need to have one card per person
		testPlayers.get(0).updateHand(playerSolution);
		testPlayers.get(1).updateHand(roomSolution);
		testPlayers.get(2).updateHand(weaponSolution);

		// Add test Players to board
		board.setPlayers(testPlayers);

		// Check that if no player can disprove return null
		assertNull(board.handleSuggestion(testPlayers.get(0), playerBadSolution, roomBadSolution, weaponBadSolution));

		// Check that with player 0 as accusing null is returned
		assertNull(board.handleSuggestion(testPlayers.get(0), playerSolution, roomBadSolution, weaponBadSolution));

		// Check that player returns correct answer if in hand
		assertTrue(board.handleSuggestion(testPlayers.get(1), playerSolution, roomBadSolution, weaponBadSolution)
				.equals(playerSolution));

		// Check that disproving card is the first in player order
		assertTrue(board.handleSuggestion(testPlayers.get(0), playerBadSolution, roomSolution, weaponSolution)
				.equals(roomSolution));
		assertTrue(board.handleSuggestion(testPlayers.get(0), playerSolution, roomBadSolution, weaponSolution)
				.equals(weaponSolution));

		assertTrue(board.handleSuggestion(testPlayers.get(1), playerSolution, roomSolution, weaponSolution)
				.equals(weaponSolution));
		assertTrue(board.handleSuggestion(testPlayers.get(1), playerSolution, roomBadSolution, weaponBadSolution)
				.equals(playerSolution));

		assertTrue(board.handleSuggestion(testPlayers.get(2), playerSolution, roomSolution, weaponSolution)
				.equals(playerSolution));
		assertTrue(board.handleSuggestion(testPlayers.get(2), playerBadSolution, roomSolution, weaponSolution)
				.equals(roomSolution));

	}
}
