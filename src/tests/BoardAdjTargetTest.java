/**
 * @author Alexander Cieslewicz
 * @author Eric Dong
 * 
 * Tests for verifying adjList and target list accuracy
 */

package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.BoardCell;

class BoardAdjTargetTest {
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

	// Ensure that player does not move around within room
	// These cells are LIGHT ORANGE on the planning spreadsheet
	@Test
	public void testAdjacenciesRooms() {
		// First, test Medical has 2 doors and a secret passage
		Set<BoardCell> testList = board.getAdjList(14, 2);
		assertEquals(3, testList.size());
		assertTrue(testList.contains(board.getCell(9, 3)));
		assertTrue(testList.contains(board.getCell(14, 5)));
		assertTrue(testList.contains(board.getCell(11, 18)));

		// Test Navigation, has 2 doors
		testList = board.getAdjList(2, 12);
		assertEquals(2, testList.size());
		assertTrue(testList.contains(board.getCell(6, 12)));
		assertTrue(testList.contains(board.getCell(6, 13)));

		// Test Oxygen, has 2 doors
		testList = board.getAdjList(13, 23);
		assertEquals(2, testList.size());
		assertTrue(testList.contains(board.getCell(13, 20)));
		assertTrue(testList.contains(board.getCell(14, 20)));

		// Finally Test Energy, has 2 doors
		testList = board.getAdjList(20, 13);
		assertEquals(2, testList.size());
		assertTrue(testList.contains(board.getCell(20, 8)));
		assertTrue(testList.contains(board.getCell(20, 17)));
	}

	// Ensure door locations include their rooms and also additional walkways
	// These cells are LIGHT ORANGE on the planning spreadsheet
	@Test
	public void testAdjacencyDoor() {
		Set<BoardCell> testList = board.getAdjList(6, 13);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.getCell(6, 12)));
		assertTrue(testList.contains(board.getCell(6, 14)));
		assertTrue(testList.contains(board.getCell(7, 13)));
		assertTrue(testList.contains(board.getCell(2, 12)));

		testList = board.getAdjList(20, 8);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.getCell(20, 13)));
		assertTrue(testList.contains(board.getCell(21, 8)));
		assertTrue(testList.contains(board.getCell(19, 8)));
		assertTrue(testList.contains(board.getCell(20, 7)));

		testList = board.getAdjList(13, 20);
		assertEquals(3, testList.size());
		assertTrue(testList.contains(board.getCell(13, 23)));
		assertTrue(testList.contains(board.getCell(12, 20)));
		assertTrue(testList.contains(board.getCell(14, 20)));
	}

	// Test a variety of walkway scenarios
	// These tests are Dark Orange on the planning spreadsheet
	@Test
	public void testAdjacencyWalkways() {
		// Test a walkway that is in the open 4 potentials
		Set<BoardCell> testList = board.getAdjList(12, 6);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.getCell(12, 5)));
		assertTrue(testList.contains(board.getCell(12, 7)));
		assertTrue(testList.contains(board.getCell(11, 6)));
		assertTrue(testList.contains(board.getCell(13, 6)));

		// Sandwiched between two rooms
		testList = board.getAdjList(10, 15);
		assertEquals(2, testList.size());
		assertTrue(testList.contains(board.getCell(9, 15)));
		assertTrue(testList.contains(board.getCell(11, 15)));

		// Test adjacent to edge of board
		testList = board.getAdjList(7, 25);
		assertEquals(2, testList.size());
		assertTrue(testList.contains(board.getCell(7, 24)));
		assertTrue(testList.contains(board.getCell(8, 25)));

		// Test next unused space and room
		testList = board.getAdjList(24, 18);
		assertEquals(2, testList.size());
		assertTrue(testList.contains(board.getCell(23, 18)));
		assertTrue(testList.contains(board.getCell(24, 17)));

	}

	// Tests out of room center, 1, 3 and 4
	// These are Cyan on the planning spreadsheet
	@Test
	public void testTargetsInOxygen() {
		// test a roll of 1
		board.calcTargets(board.getCell(13, 23), 1);
		Set<BoardCell> targets = board.getTargets();
		assertEquals(2, targets.size());
		assertTrue(targets.contains(board.getCell(13, 20)));
		assertTrue(targets.contains(board.getCell(14, 20)));

		// test a roll of 3
		board.calcTargets(board.getCell(13, 23), 3);
		targets = board.getTargets();
		assertEquals(4, targets.size());
		assertTrue(targets.contains(board.getCell(11, 20)));
		assertTrue(targets.contains(board.getCell(12, 20)));
		assertTrue(targets.contains(board.getCell(15, 20)));
		assertTrue(targets.contains(board.getCell(16, 20)));

		// test a roll of 4
		board.calcTargets(board.getCell(13, 23), 4);
		targets = board.getTargets();
		assertEquals(5, targets.size());
		assertTrue(targets.contains(board.getCell(10, 20)));
		assertTrue(targets.contains(board.getCell(11, 20)));
		assertTrue(targets.contains(board.getCell(16, 20)));
		assertTrue(targets.contains(board.getCell(17, 20)));
		assertTrue(targets.contains(board.getCell(16, 19)));
	}

	// Tests out of door, 1, 3 and 4
	// These are Cyan on the planning spreadsheet
	@Test
	public void testTargetsAtDoor() {
		// test a roll of 1, at door
		board.calcTargets(board.getCell(7, 22), 1);
		Set<BoardCell> targets = board.getTargets();
		assertEquals(4, targets.size());
		assertTrue(targets.contains(board.getCell(8, 22)));
		assertTrue(targets.contains(board.getCell(7, 21)));
		assertTrue(targets.contains(board.getCell(7, 23)));
		assertTrue(targets.contains(board.getCell(8, 22)));

		// test a roll of 3
		board.calcTargets(board.getCell(7, 22), 3);
		targets = board.getTargets();
		assertEquals(8, targets.size());
		assertTrue(targets.contains(board.getCell(3, 22)));
		assertTrue(targets.contains(board.getCell(7, 25)));
		assertTrue(targets.contains(board.getCell(7, 19)));
		assertTrue(targets.contains(board.getCell(7, 21)));
		assertTrue(targets.contains(board.getCell(7, 23)));
		assertTrue(targets.contains(board.getCell(8, 20)));
		assertTrue(targets.contains(board.getCell(8, 22)));
		assertTrue(targets.contains(board.getCell(8, 24)));

		// test a roll of 4
		board.calcTargets(board.getCell(7, 22), 4);
		targets = board.getTargets();
		assertEquals(8, targets.size());
		assertTrue(targets.contains(board.getCell(3, 22)));
		assertTrue(targets.contains(board.getCell(7, 18)));
		assertTrue(targets.contains(board.getCell(7, 20)));
		assertTrue(targets.contains(board.getCell(7, 24)));
		assertTrue(targets.contains(board.getCell(8, 21)));
		assertTrue(targets.contains(board.getCell(8, 23)));
		assertTrue(targets.contains(board.getCell(8, 25)));
		assertTrue(targets.contains(board.getCell(9, 20)));
	}

	@Test
	public void testTargetsInWalkway1() {
		// test a roll of 1
		board.calcTargets(board.getCell(9, 2), 1);
		Set<BoardCell> targets = board.getTargets();
		assertEquals(2, targets.size());
		assertTrue(targets.contains(board.getCell(9, 1)));
		assertTrue(targets.contains(board.getCell(9, 3)));

		// test a roll of 3
		board.calcTargets(board.getCell(9, 2), 3);
		targets = board.getTargets();
		assertEquals(2, targets.size());
		assertTrue(targets.contains(board.getCell(14, 2)));
		assertTrue(targets.contains(board.getCell(9, 5)));

		// test a roll of 4
		board.calcTargets(board.getCell(9, 2), 4);
		targets = board.getTargets();
		assertEquals(3, targets.size());
		assertTrue(targets.contains(board.getCell(14, 2)));
		assertTrue(targets.contains(board.getCell(9, 6)));
		assertTrue(targets.contains(board.getCell(10, 5)));
	}

	@Test
	public void testTargetsInWalkway2() {
		// test a roll of 1
		board.calcTargets(board.getCell(22, 7), 1);
		Set<BoardCell> targets = board.getTargets();
		assertEquals(3, targets.size());
		assertTrue(targets.contains(board.getCell(21, 7)));
		assertTrue(targets.contains(board.getCell(23, 7)));
		assertTrue(targets.contains(board.getCell(22, 8)));

		// test a roll of 3
		board.calcTargets(board.getCell(22, 7), 3);
		targets = board.getTargets();
		assertEquals(9, targets.size());
		assertTrue(targets.contains(board.getCell(23, 3)));
		assertTrue(targets.contains(board.getCell(20, 8)));
		assertTrue(targets.contains(board.getCell(23, 7)));
		assertTrue(targets.contains(board.getCell(19, 7)));
		assertTrue(targets.contains(board.getCell(20, 6)));
		assertTrue(targets.contains(board.getCell(21, 7)));

		// test a roll of 4
		board.calcTargets(board.getCell(22, 7), 4);
		targets = board.getTargets();
		assertEquals(11, targets.size());
		assertTrue(targets.contains(board.getCell(23, 3)));
		assertTrue(targets.contains(board.getCell(20, 13)));
		assertTrue(targets.contains(board.getCell(18, 7)));
		assertTrue(targets.contains(board.getCell(21, 8)));
		assertTrue(targets.contains(board.getCell(24, 9)));
		assertTrue(targets.contains(board.getCell(24, 7)));
	}

	@Test
	// test to make sure occupied locations do not cause problems
	// The occupied spots are marked with Purple and start with Cyan
	public void testTargetsOccupied() {
		// test a roll of 4 blocked to the right
		board.getCell(9, 4).setOccupied(true);
		board.calcTargets(board.getCell(9, 2), 4);
		board.getCell(9, 4).setOccupied(false);
		Set<BoardCell> targets = board.getTargets();
		assertEquals(1, targets.size());
		assertTrue(targets.contains(board.getCell(14, 2)));

		// check getting into room if occupied
		board.getCell(7, 23).setOccupied(true);
		board.getCell(3, 22).setOccupied(true);
		board.calcTargets(board.getCell(7, 22), 1);
		board.getCell(3, 22).setOccupied(false);
		board.getCell(7, 23).setOccupied(false);
		targets = board.getTargets();
		assertEquals(3, targets.size());
		assertTrue(targets.contains(board.getCell(3, 22)));
		assertTrue(targets.contains(board.getCell(7, 21)));
		assertTrue(targets.contains(board.getCell(8, 22)));

		// check leaving a room with a blocked doorway
		board.getCell(23, 7).setOccupied(true);
		board.calcTargets(board.getCell(23, 3), 3);
		board.getCell(23, 7).setOccupied(false);
		targets = board.getTargets();
		assertEquals(0, targets.size());
	}

}
