/**
 * @author Alexander Cieslewicz
 * @author Eric Dong
 * 
 * Tests for test board of clue
 */

package tests;

import java.util.Set;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import experiment.TestBoard;
import experiment.TestBoardCell;

public class BoardTestExp {
	TestBoard board;

	@BeforeEach
	public void setup() {
		board = new TestBoard();
	}

	/*
	 * Tests that corner cell adjacency are correct
	 */
	@Test
	public void testAdjacencyCorners() {
		TestBoardCell cell = board.getCell(0, 0);
		Set<TestBoardCell> testList = cell.getAdjList();
		Assert.assertTrue(testList.contains(board.getCell(1, 0)));
		Assert.assertTrue(testList.contains(board.getCell(0, 1)));
		Assert.assertEquals(2, testList.size());

		cell = board.getCell(3, 3);
		testList = cell.getAdjList();
		Assert.assertTrue(testList.contains(board.getCell(2, 3)));
		Assert.assertTrue(testList.contains(board.getCell(3, 2)));
		Assert.assertEquals(2, testList.size());

		cell = board.getCell(0, 3);
		testList = cell.getAdjList();
		Assert.assertTrue(testList.contains(board.getCell(1, 3)));
		Assert.assertTrue(testList.contains(board.getCell(0, 2)));
		Assert.assertEquals(2, testList.size());

		cell = board.getCell(3, 0);
		testList = cell.getAdjList();
		Assert.assertTrue(testList.contains(board.getCell(3, 1)));
		Assert.assertTrue(testList.contains(board.getCell(2, 0)));
		Assert.assertEquals(2, testList.size());
	}

	/*
	 * Tests that center cell adjacency are correct
	 */
	@Test
	public void testAdjacencyCenters() {
		TestBoardCell cell = board.getCell(2, 2);
		Set<TestBoardCell> testList = cell.getAdjList();
		Assert.assertTrue(testList.contains(board.getCell(2, 1)));
		Assert.assertTrue(testList.contains(board.getCell(1, 2)));
		Assert.assertTrue(testList.contains(board.getCell(2, 3)));
		Assert.assertTrue(testList.contains(board.getCell(3, 2)));
		Assert.assertEquals(4, testList.size());

		cell = board.getCell(1, 1);
		testList = cell.getAdjList();
		Assert.assertTrue(testList.contains(board.getCell(0, 1)));
		Assert.assertTrue(testList.contains(board.getCell(1, 0)));
		Assert.assertTrue(testList.contains(board.getCell(1, 2)));
		Assert.assertTrue(testList.contains(board.getCell(2, 1)));
		Assert.assertEquals(4, testList.size());
	}

	/*
	 * Tests that you can get to the correct targets on an empty board
	 */
	@Test
	public void testTargetsNormal() {
		// Top left corner with 1 step
		TestBoardCell cell = board.getCell(0, 0);
		board.calcTargets(cell, 1);
		Set<TestBoardCell> targets = board.getTargets();
		Assert.assertEquals(2, targets.size());
		Assert.assertTrue(targets.contains(board.getCell(1, 0)));
		Assert.assertTrue(targets.contains(board.getCell(0, 1)));

		// Center cell with 1 step
		cell = board.getCell(2, 2);
		board.calcTargets(cell, 1);
		targets = board.getTargets();
		Assert.assertEquals(4, targets.size());
		Assert.assertTrue(targets.contains(board.getCell(2, 1)));
		Assert.assertTrue(targets.contains(board.getCell(1, 2)));
		Assert.assertTrue(targets.contains(board.getCell(2, 3)));
		Assert.assertTrue(targets.contains(board.getCell(3, 2)));

		// Bottom right corner with 3 steps
		cell = board.getCell(3, 3);
		board.calcTargets(cell, 3);
		targets = board.getTargets();
		Assert.assertEquals(6, targets.size());
		Assert.assertTrue(targets.contains(board.getCell(3, 2)));
		Assert.assertTrue(targets.contains(board.getCell(2, 3)));
		Assert.assertTrue(targets.contains(board.getCell(3, 0)));
		Assert.assertTrue(targets.contains(board.getCell(0, 3)));
		Assert.assertTrue(targets.contains(board.getCell(1, 2)));
		Assert.assertTrue(targets.contains(board.getCell(2, 1)));

		// Center cell with 3 steps
		cell = board.getCell(1, 1);
		board.calcTargets(cell, 3);
		targets = board.getTargets();
		Assert.assertEquals(8, targets.size());
		Assert.assertTrue(targets.contains(board.getCell(1, 0)));
		Assert.assertTrue(targets.contains(board.getCell(0, 1)));
		Assert.assertTrue(targets.contains(board.getCell(3, 0)));
		Assert.assertTrue(targets.contains(board.getCell(0, 3)));
		Assert.assertTrue(targets.contains(board.getCell(2, 1)));
		Assert.assertTrue(targets.contains(board.getCell(1, 2)));
		Assert.assertTrue(targets.contains(board.getCell(2, 3)));
		Assert.assertTrue(targets.contains(board.getCell(3, 2)));
	}

	/*
	 * Tests that you can get to the correct targets with occupied pieces
	 */
	@Test
	public void testTargetsWithOccupied() {
		// Top left corner with unrelated occupied spaces
		TestBoardCell cell = board.getCell(0, 0);
		board.calcTargets(cell, 1);
		board.getCell(3, 3).setOccupied(true);
		Set<TestBoardCell> targets = board.getTargets();
		Assert.assertEquals(2, targets.size());
		Assert.assertTrue(targets.contains(board.getCell(1, 0)));
		Assert.assertTrue(targets.contains(board.getCell(0, 1)));
		board.getCell(3, 3).setOccupied(false);

		// Center with occupied target cell
		cell = board.getCell(2, 2);
		board.getCell(2, 1).setOccupied(true);
		board.calcTargets(cell, 1);
		targets = board.getTargets();
		Assert.assertEquals(3, targets.size());
		Assert.assertTrue(targets.contains(board.getCell(1, 2)));
		Assert.assertTrue(targets.contains(board.getCell(2, 3)));
		Assert.assertTrue(targets.contains(board.getCell(3, 2)));
		board.getCell(2, 1).setOccupied(true);

		// Bottom right corner with 3 steps 1 related occupied and one unrelated
		// occupied
		cell = board.getCell(3, 3);
		board.getCell(2, 1).setOccupied(true);
		board.getCell(3, 3).setOccupied(true);
		board.calcTargets(cell, 3);
		targets = board.getTargets();
		Assert.assertEquals(5, targets.size());
		Assert.assertTrue(targets.contains(board.getCell(3, 2)));
		Assert.assertTrue(targets.contains(board.getCell(2, 3)));
		Assert.assertTrue(targets.contains(board.getCell(3, 0)));
		Assert.assertTrue(targets.contains(board.getCell(0, 3)));
		Assert.assertTrue(targets.contains(board.getCell(1, 2)));
		board.getCell(2, 1).setOccupied(true);
		board.getCell(3, 3).setOccupied(true);

		// Center cell with 3 steps and multiple occupied cells
		cell = board.getCell(1, 1);
		board.getCell(1, 0).setOccupied(true);
		board.getCell(0, 1).setOccupied(true);
		board.getCell(3, 0).setOccupied(true);
		board.getCell(0, 3).setOccupied(true);
		board.getCell(0, 2).setOccupied(true);
		board.calcTargets(cell, 3);
		targets = board.getTargets();
		Assert.assertEquals(2, targets.size());
		Assert.assertTrue(targets.contains(board.getCell(2, 3)));
		Assert.assertTrue(targets.contains(board.getCell(3, 2)));
		board.getCell(1, 0).setOccupied(true);
		board.getCell(0, 1).setOccupied(true);
		board.getCell(3, 0).setOccupied(true);
		board.getCell(0, 3).setOccupied(true);
		board.getCell(0, 2).setOccupied(true);
	}

	/*
	 * Tests that you can get to the correct targets with room cells
	 */
	@Test
	public void testTargetsRoom() {
		// Top left corner with 1 step and 1 room
		TestBoardCell cell = board.getCell(0, 0);
		board.getCell(1, 0).setRoom(true);
		board.calcTargets(cell, 1);
		Set<TestBoardCell> targets = board.getTargets();
		Assert.assertEquals(2, targets.size());
		Assert.assertTrue(targets.contains(board.getCell(1, 0)));
		Assert.assertTrue(targets.contains(board.getCell(0, 1)));
		board.getCell(1, 0).setRoom(false);

		// Center cell with 1 step and non reachable room cell
		cell = board.getCell(2, 2);
		board.getCell(1, 0).setRoom(true);
		board.calcTargets(cell, 1);
		targets = board.getTargets();
		Assert.assertEquals(4, targets.size());
		Assert.assertTrue(targets.contains(board.getCell(2, 1)));
		Assert.assertTrue(targets.contains(board.getCell(1, 2)));
		Assert.assertTrue(targets.contains(board.getCell(2, 3)));
		Assert.assertTrue(targets.contains(board.getCell(3, 2)));
		board.getCell(1, 0).setRoom(false);

		// Bottom right corner with 3 steps and 1 reachable rooms
		cell = board.getCell(3, 3);
		board.getCell(2, 2).setRoom(true);
		board.getCell(0, 2).setRoom(true); //unreachable
		board.calcTargets(cell, 3);
		targets = board.getTargets();
		Assert.assertEquals(5, targets.size());
		Assert.assertTrue(targets.contains(board.getCell(3, 0)));
		Assert.assertTrue(targets.contains(board.getCell(0, 3)));
		Assert.assertTrue(targets.contains(board.getCell(1, 2)));
		Assert.assertTrue(targets.contains(board.getCell(2, 1)));
		Assert.assertTrue(targets.contains(board.getCell(2, 2)));
		board.getCell(2, 2).setRoom(false);
		board.getCell(0, 2).setRoom(false);


		// Center cell with 3 steps 3 reachable rooms and 1 unreachable room
		cell = board.getCell(1, 1);
		board.getCell(1, 0).setRoom(true);
		board.getCell(3, 3).setRoom(true); // Unreachable
		board.getCell(2, 2).setRoom(true);
		board.getCell(0, 0).setRoom(true);
		board.calcTargets(cell, 3);
		targets = board.getTargets();
		Assert.assertEquals(9, targets.size());
		Assert.assertTrue(targets.contains(board.getCell(1, 0)));
		Assert.assertTrue(targets.contains(board.getCell(3, 0)));
		Assert.assertTrue(targets.contains(board.getCell(0, 3)));
		Assert.assertTrue(targets.contains(board.getCell(1, 2)));
		Assert.assertTrue(targets.contains(board.getCell(2, 3)));
		Assert.assertTrue(targets.contains(board.getCell(3, 2)));
		Assert.assertTrue(targets.contains(board.getCell(2, 2)));
		Assert.assertTrue(targets.contains(board.getCell(0, 0)));
		Assert.assertTrue(targets.contains(board.getCell(0, 1)));
		
		board.getCell(1, 0).setRoom(false);
		board.getCell(3, 3).setRoom(false);
		board.getCell(2, 2).setRoom(false);
		board.getCell(0, 0).setRoom(false);
	}

	/*
	 * Tests that you can get to the correct targets on an mixed board
	 */
	@Test
	public void testTargetsMixed() {
		// Top left corner with 1 step 1 occupied and 1 reachable room
		TestBoardCell cell = board.getCell(0, 0);

		board.getCell(3, 3).setRoom(true);
		board.getCell(1, 0).setOccupied(true);
		board.calcTargets(cell, 1);
		Set<TestBoardCell> targets = board.getTargets();
		Assert.assertEquals(1, targets.size());
		Assert.assertTrue(targets.contains(board.getCell(0, 1)));
		board.getCell(3, 3).setRoom(false);
		board.getCell(1, 0).setOccupied(false);

		// Center cell with 1 step 1 room and 1 occupied
		cell = board.getCell(2, 2);
		board.getCell(1, 2).setRoom(true);
		board.getCell(2, 1).setOccupied(true);
		board.calcTargets(cell, 1);
		targets = board.getTargets();
		Assert.assertEquals(3, targets.size());
		Assert.assertTrue(targets.contains(board.getCell(1, 2)));
		Assert.assertTrue(targets.contains(board.getCell(2, 3)));
		Assert.assertTrue(targets.contains(board.getCell(3, 2)));
		board.getCell(1, 2).setRoom(false);
		board.getCell(2, 1).setOccupied(false);
		
		// Bottom right corner with 3 steps 2 occupied and a room
		cell = board.getCell(3, 3);
		board.getCell(1, 3).setRoom(true);
		board.getCell(3, 2).setOccupied(true);
		board.getCell(0, 3).setOccupied(true);
		board.calcTargets(cell, 3);
		targets = board.getTargets();
		Assert.assertEquals(3, targets.size());
		Assert.assertTrue(targets.contains(board.getCell(1, 2)));
		Assert.assertTrue(targets.contains(board.getCell(2, 1)));
		Assert.assertTrue(targets.contains(board.getCell(1, 3)));
		board.getCell(1, 3).setRoom(false);
		board.getCell(3, 2).setOccupied(false);
		board.getCell(0, 3).setOccupied(false);

		// Center cell with 3 steps 3 occupied 2 rooms
		cell = board.getCell(1, 1);
		board.getCell(1, 0).setOccupied(true);
		board.getCell(2, 3).setOccupied(true);
		board.getCell(3, 1).setOccupied(true);
		board.getCell(3, 3).setRoom(true); // Unreachable
		board.getCell(2, 2).setRoom(true);
		board.getCell(0, 0).setRoom(true);
		board.calcTargets(cell, 3);
		targets = board.getTargets();
		Assert.assertEquals(6, targets.size());
		Assert.assertTrue(targets.contains(board.getCell(0, 1)));
		Assert.assertTrue(targets.contains(board.getCell(3, 0)));
		Assert.assertTrue(targets.contains(board.getCell(0, 3)));
		Assert.assertTrue(targets.contains(board.getCell(1, 2)));
		Assert.assertTrue(targets.contains(board.getCell(2, 2)));
		Assert.assertTrue(targets.contains(board.getCell(0, 0)));
		board.getCell(1, 0).setOccupied(false);
		board.getCell(2, 3).setOccupied(false);
		board.getCell(3, 1).setOccupied(false);
		board.getCell(3, 3).setRoom(false); // Unreachable
		board.getCell(2, 2).setRoom(false);
		board.getCell(0, 0).setRoom(false);
	}
}
