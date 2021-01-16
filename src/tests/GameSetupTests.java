/**
 * @author Alexander Cieslewicz
 * @author Eric Dong
 * 
 * Tests for the game setup that tests players and the deck
 */
package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

class GameSetupTests {

	private static Board board;

	@BeforeAll
	public static void setUp() {
		// Board is singleton, get the only instance
		board = Board.getInstance();
		// set the file names to use my config files
		board.setConfigFiles("data/ClueSetup.csv", "data/ClueSetup.txt");
		// Initialize will load config files
		board.initialize();
		board.deal();
	}

	@Test
	// check the human player properties
	public void LoadHumanPlayer() {
		ArrayList<Player> players = board.getPlayers();
		Player currentPlayer = players.get(0);
		assertEquals(currentPlayer.getName(), "Red");
		assertEquals(currentPlayer.getColor(), new Color(230, 72, 72));
		assertTrue(currentPlayer instanceof HumanPlayer);
		assertEquals(currentPlayer.getRow(), 1);
		assertEquals(currentPlayer.getColumn(), 7);
	}

	@Test
	// Check a middle and last computer player properties
	public void LoadComputerPlayers() {
		ArrayList<Player> players = board.getPlayers();
		Player currentPlayer = players.get(2);
		assertEquals(currentPlayer.getName(), "Orange");
		assertEquals(currentPlayer.getColor(), Color.orange);
		assertTrue(currentPlayer instanceof ComputerPlayer);
		assertEquals(currentPlayer.getRow(), 20);
		assertEquals(currentPlayer.getColumn(), 0);

		currentPlayer = players.get(5);
		assertEquals(currentPlayer.getName(), "Pink");
		assertEquals(currentPlayer.getColor(), Color.pink);
		assertTrue(currentPlayer instanceof ComputerPlayer);
		assertEquals(currentPlayer.getRow(), 8);
		assertEquals(currentPlayer.getColumn(), 25);
	}

	@Test
	public void testTotalPlayers() {
		ArrayList<Player> players = board.getPlayers();
		assertEquals(players.size(), 6);
	}

	@Test
	// Test if correct players, rooms, and weapons are in the deck
	public void testDeck() {
		ArrayList<Card> deck = board.getDeck();
		Card currentCard = new Card("Shields", CardType.ROOM);
		assertTrue(deck.contains(currentCard));
		currentCard = new Card("Medical", CardType.ROOM);
		assertTrue(deck.contains(currentCard));
		currentCard = new Card("Right Engine", CardType.ROOM);
		assertTrue(deck.contains(currentCard));

		currentCard = new Card("Red", CardType.PERSON);
		assertTrue(deck.contains(currentCard));
		currentCard = new Card("Pink", CardType.PERSON);
		assertTrue(deck.contains(currentCard));

		currentCard = new Card("Gun", CardType.WEAPON);
		assertTrue(deck.contains(currentCard));
		currentCard = new Card("Meltdown", CardType.WEAPON);
		assertTrue(deck.contains(currentCard));
	}

	@Test
	public void testDeckSize() {
		ArrayList<Card> deck = board.getDeck();
		assertEquals(deck.size(), 21);
	}

	// Base Solution takes first room, player, and weapon in deck as solution and is
	// not shuffled
	@Test
	public void testSolution() {
		Solution answer = board.getTheAnswer();
		Card playerSolutionCard = new Card("Red", CardType.PERSON);
		Card roomSolutionCard = new Card("Shields", CardType.ROOM);
		Card weaponSolutionCard = new Card("Gun", CardType.WEAPON);
		assertTrue(answer.getPerson().equals(playerSolutionCard));
		assertTrue(answer.getRoom().equals(roomSolutionCard));
		assertTrue(answer.getWeapon().equals(weaponSolutionCard));
	}

	@Test
	public void testPlayerHandCount() {
		ArrayList<Player> players = board.getPlayers();
		for (Player player : players) {
			assertEquals(player.getHand().size(), 3);
		}
	}

	@Test
	// Test if players have the right hand
	public void testPlayerHandValid() {
		ArrayList<Player> players = board.getPlayers();
		Player currentPlayer = players.get(0);
		ArrayList<Card> currentHand = currentPlayer.getHand();
		Card currentCard = new Card("Navigation", CardType.ROOM);
		assertTrue(currentHand.contains(currentCard));
		currentCard = new Card("Energy", CardType.ROOM);
		assertTrue(currentHand.contains(currentCard));
		currentCard = new Card("Pink", CardType.PERSON);
		assertTrue(currentHand.contains(currentCard));

		currentPlayer = players.get(5);
		currentHand = currentPlayer.getHand();
		currentCard = new Card("Left Engine", CardType.ROOM);
		assertTrue(currentHand.contains(currentCard));
		currentCard = new Card("Black", CardType.PERSON);
		assertTrue(currentHand.contains(currentCard));
		currentCard = new Card("Meltdown", CardType.WEAPON);
		assertTrue(currentHand.contains(currentCard));

	}
}
