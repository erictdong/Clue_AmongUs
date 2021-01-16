/**
 * @author Alexander Cieslewicz
 * @author Eric Dong
 * 
 * Computer Player class for game
 */

package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.BoardCell;
import clueGame.Card;
import clueGame.CardType;
import clueGame.ComputerPlayer;
import clueGame.Solution;

class ComputerAITest {

	private static Board board;

	@BeforeAll
	public static void setUp() {
		// Board is singleton, get the only instance
		board = Board.getInstance();
		// set the file names to use my config files
		board.setConfigFiles("data/ClueSetup.csv", "data/ClueSetup.txt");
		// Initialize will load config files
		board.initialize();
	}

	@Test
	/**
	 * Check that the correct possible solutions are returned by the player
	 * suggestion
	 */
	void testCreateSuggestion() {
		// Build a small test deck don't need room currently
		ArrayList<Card> smallDeck = new ArrayList<>();
		smallDeck.add(new Card("TestPlayer1", CardType.PERSON));
		smallDeck.add(new Card("TestPlayer2", CardType.PERSON));
		smallDeck.add(new Card("TestPlayer3", CardType.PERSON));

		smallDeck.add(new Card("TestWeapon1", CardType.WEAPON));
		smallDeck.add(new Card("TestWeapon2", CardType.WEAPON));
		smallDeck.add(new Card("TestWeapon3", CardType.WEAPON));

		Card shields = new Card("Shields", CardType.ROOM);
		smallDeck.add(shields);
		Card guns = new Card("Guns", CardType.ROOM);
		smallDeck.add(guns);

		// Generate Computer player cards
		Card npcPersonCard = new Card("TestPlayer4", CardType.PERSON);
		Card npcWeaponCard = new Card("TestWeapon4", CardType.WEAPON);
		smallDeck.add(npcPersonCard);
		smallDeck.add(npcWeaponCard);

		// Create test player in Shields
		ComputerPlayer testComputerPlayer = new ComputerPlayer("TestPlayer1", Color.red, 4, 3);
		testComputerPlayer.setUnseenCards(smallDeck);
		testComputerPlayer.updateHand(npcPersonCard);
		testComputerPlayer.updateHand(npcWeaponCard);

		// Test that room matches current player location
		testComputerPlayer.setRoom(board.getRoom(testComputerPlayer.getRow(), testComputerPlayer.getColumn()));
		assertTrue(testComputerPlayer.createSuggestion().getRoom().equals(shields));

		// Change room and verify that test passes still
		testComputerPlayer.setRow(3);
		testComputerPlayer.setColumn(22);
		testComputerPlayer.setRoom(board.getRoom(testComputerPlayer.getRow(), testComputerPlayer.getColumn()));
		assertTrue(testComputerPlayer.createSuggestion().getRoom().equals(guns));

		// Check that player selects person randomly from unseen
		int countSeenPlayers[] = new int[4];
		int countSeenWeapons[] = new int[4];
		for (int i = 0; i < 100; i++) {
			Solution newSolution = testComputerPlayer.createSuggestion();
			if (newSolution.getPerson().equals(smallDeck.get(0))) {
				countSeenPlayers[0]++;
			}
			else if (newSolution.getPerson().equals(smallDeck.get(1))) {
				countSeenPlayers[1]++;
			}
			else if (newSolution.getPerson().equals(smallDeck.get(2))) {
				countSeenPlayers[2]++;
			}
			else {
				countSeenPlayers[3]++;
			}

			if (newSolution.getWeapon().equals(smallDeck.get(3))) {
				countSeenWeapons[0]++;
			}
			else if (newSolution.getWeapon().equals(smallDeck.get(4))) {
				countSeenWeapons[1]++;
			}
			else if (newSolution.getWeapon().equals(smallDeck.get(5))) {
				countSeenWeapons[2]++;
			}
			else {
				countSeenWeapons[3]++;
			}
		}

		// Assert that each card was chosen at least once
		assertTrue(countSeenPlayers[0] > 0);
		assertTrue(countSeenPlayers[1] > 0);
		assertTrue(countSeenPlayers[2] > 0);
		assertTrue(countSeenWeapons[0] > 0);
		assertTrue(countSeenWeapons[1] > 0);
		assertTrue(countSeenWeapons[2] > 0);

		// Assert that only unseen cards have been chosen
		assertTrue(countSeenPlayers[3] == 0);
		assertTrue(countSeenWeapons[3] == 0);

		// Add cards to seen
		testComputerPlayer.updateSeen(smallDeck.get(0));
		testComputerPlayer.updateSeen(smallDeck.get(1));
		testComputerPlayer.updateSeen(smallDeck.get(3));
		testComputerPlayer.updateSeen(smallDeck.get(4));

		// Assert that returned solution matches only unseen cards
		assertTrue(testComputerPlayer.createSuggestion().getPerson().equals(smallDeck.get(2)));
		assertTrue(testComputerPlayer.createSuggestion().getWeapon().equals(smallDeck.get(5)));

	}

	@Test
	/**
	 * Test that the AI for a computer player correctly checks a target
	 */
	public void testSelectTargets() {
		// Setup Test Computer Player starting near left engine on board
		ComputerPlayer testComputerPlayer = new ComputerPlayer("testComputerPlayer", Color.black, 22, 7);
		board.calcTargets(board.getCell(22, 7), 2);
		Set<BoardCell> targets = board.getTargets();

		// Test to make sure that player goes into non visited room
		assertEquals(testComputerPlayer.selectTarget(targets), board.getCell(23, 3));

		// Test that player randomly selects room if two rooms are unvisited and
		// reachable
		int leftEngineCount = 0;
		int energyCount = 0;
		int notRoomCount = 0;

		board.calcTargets(board.getCell(22, 7), 4);
		targets = board.getTargets();

		for (int i = 0; i < 100; i++) {
			BoardCell playerTarget = testComputerPlayer.selectTarget(targets);
			if (playerTarget.equals(board.getCell(23, 3))) {
				leftEngineCount++;
			}
			else if (playerTarget.equals(board.getCell(20, 13))) {
				energyCount++;
			}
			else {
				notRoomCount++;
			}
			testComputerPlayer.clearVistedRooms();
		}

		// Verify that player only went into the two unvisted rooms
		assertTrue(leftEngineCount > 0);
		assertTrue(energyCount > 0);
		assertTrue(notRoomCount == 0);

		// add energy to visited rooms
		testComputerPlayer.addVisitedRoom(board.getCell(20, 13));

		// Verify that player went each cell randomly
		leftEngineCount = 0;
		notRoomCount = 0;

		board.calcTargets(board.getCell(22, 7), 2);
		targets = board.getTargets();

		for (int i = 0; i < 100; i++) {
			BoardCell playerTarget = testComputerPlayer.selectTarget(targets);
			if (playerTarget.equals(board.getCell(23, 3))) {
				leftEngineCount++;
			}
			else if (playerTarget.equals(board.getCell(20, 13))) {
				energyCount++;
			}
			else {
				notRoomCount++;
			}
		}

		// Verify that player went both left engine and walkway cells
		assertTrue(leftEngineCount > 0);
		assertTrue(notRoomCount > 0);

		// Add energy room to visited
		testComputerPlayer.addVisitedRoom(board.getCell(23, 3));

		// Verify that player went to left engine energy and walkways equally
		leftEngineCount = 0;
		energyCount = 0;
		notRoomCount = 0;

		board.calcTargets(board.getCell(22, 7), 4);
		targets = board.getTargets();

		for (int i = 0; i < 100; i++) {
			BoardCell playerTarget = testComputerPlayer.selectTarget(targets);
			if (playerTarget.equals(board.getCell(23, 3))) {
				leftEngineCount++;
			}
			else if (playerTarget.equals(board.getCell(20, 13))) {
				energyCount++;
			}
			else {
				notRoomCount++;
			}
		}
		assertTrue(leftEngineCount > 0);
		assertTrue(energyCount > 0);
		assertTrue(notRoomCount > 0);
	}

}
