/**
 * @author Alexander Cieslewicz
 * @author Eric Dong
 * 
 * This program check that the setup and layout is correct for our Clue board
 */

package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.BoardCell;
import clueGame.DoorDirection;
import clueGame.Room;

class FileInitTests {
	// Board dimensions that should be checked
	public static final int NUM_ROOMS = 9;
	public static final int NUM_DOORS = 14;
	public static final int NUM_ROWS = 26;
	public static final int NUM_COLUMNS = 26;

	// Board object
	private static Board board;

	@BeforeAll
	public static void setUp() {
		// Board is singleton, get the only instance
		board = Board.getInstance();
		// set the game config files
		board.setConfigFiles("data/ClueSetup.csv", "data/ClueSetup.txt");
		// Initialize will load BOTH config files
		board.initialize();
	}

	@Test
	public void testRoomLabels() {
		// Check that the correct number of rooms are loaded and that you can get some
		// of the rooms that should be there
		assertEquals(11, board.getNumRooms());
		assertEquals("Shields", board.getRoom('S').getName());
		assertEquals("Guns", board.getRoom('G').getName());
		assertEquals("Medical", board.getRoom('M').getName());
		assertEquals("Oxygen", board.getRoom('O').getName());
		assertEquals("Left Engine", board.getRoom('L').getName());
		assertEquals("Right Engine", board.getRoom('R').getName());
	}

	@Test
	public void testBoardDimensions() {
		// Ensure we have the proper number of rows and columns
		assertEquals(NUM_ROWS, board.getNumRows());
		assertEquals(NUM_COLUMNS, board.getNumColumns());
	}

	// Test that only doors are doors and four different direction doors
	@Test
	public void fourDoorDirections() {
		// Test left door for Shield room
		BoardCell cell = board.getCell(4, 7);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.LEFT, cell.getDoorDirection());

		// Test right door for Guns
		cell = board.getCell(4, 18);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.RIGHT, cell.getDoorDirection());

		// Test up door for Navigation
		cell = board.getCell(6, 12);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.UP, cell.getDoorDirection());

		// Test down door for Medical
		cell = board.getCell(9, 3);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.DOWN, cell.getDoorDirection());

		// Check that Shields is not a door
		cell = board.getCell(0, 0);
		assertFalse(cell.isDoorway());

		// Check that Communications is not a door
		cell = board.getCell(10, 17);
		assertFalse(cell.isDoorway());

		// Check that Unused area is not a door
		cell = board.getCell(10, 10);
		assertFalse(cell.isDoorway());

		// Check that walkway is not a door
		cell = board.getCell(18, 18);
		assertFalse(cell.isDoorway());
	}

	// Test that we have the correct number of doors
	@Test
	public void testNumberOfDoorways() {
		int numDoors = 0;
		for (int row = 0; row < board.getNumRows(); row++)
			for (int col = 0; col < board.getNumColumns(); col++) {
				BoardCell cell = board.getCell(row, col);
				if (cell.isDoorway())
					numDoors++;
			}
		Assert.assertEquals(NUM_DOORS, numDoors);
	}

	// Test that the room cells have to correct center and label
	@Test
	public void testRooms() {
		// Check a room not on the edge of the board
		BoardCell cell = board.getCell(14, 17);
		Room room = board.getRoom(cell);
		assertTrue(room != null);
		assertEquals(room.getName(), "Comms");
		assertTrue(cell.isLabel());
		assertTrue(room.getLabelCell() == cell);

		// Test of room center
		cell = board.getCell(11, 18);
		room = board.getRoom(cell);
		assertTrue(room != null);
		assertEquals(room.getName(), "Comms");
		assertTrue(cell.isRoomCenter());
		assertTrue(room.getCenterCell() == cell);

		// Check a room that is on the edge and a weird shape, with offset center and
		// label
		cell = board.getCell(18, 12);
		room = board.getRoom(cell);
		assertTrue(room != null);
		assertEquals(room.getName(), "Energy");
		assertTrue(cell.isLabel());
		assertTrue(room.getLabelCell() == cell);

		// Test of room center
		cell = board.getCell(20, 13);
		room = board.getRoom(cell);
		assertTrue(room != null);
		assertEquals(room.getName(), "Energy");
		assertTrue(cell.isRoomCenter());
		assertTrue(room.getCenterCell() == cell);

		// Check a room that in a corner
		cell = board.getCell(2, 3);
		room = board.getRoom(cell);
		assertTrue(room != null);
		assertEquals(room.getName(), "Shields");
		assertTrue(cell.isLabel());
		assertTrue(room.getLabelCell() == cell);

		// Test of room center
		cell = board.getCell(4, 3);
		room = board.getRoom(cell);
		assertTrue(room != null);
		assertEquals(room.getName(), "Shields");
		assertTrue(cell.isRoomCenter());
		assertTrue(room.getCenterCell() == cell);
	}

	// Check that the initials of secret passage, room, walkways, doors, and unused
	// area
	@Test
	public void testInitial() {
		// Test test the secret passage from Medical to Communications
		BoardCell cell = board.getCell(17, 1);
		Room room = board.getRoom(cell);
		assertTrue(room != null);
		assertEquals(room.getName(), "Medical");
		assertTrue(cell.getSecretPassage() == 'C');
		assertTrue(cell.getInitial() == 'M');

		// Test the initial for a room
		cell = board.getCell(0, 11);
		room = board.getRoom(cell);
		assertTrue(room != null);
		assertEquals(room.getName(), "Navigation");
		assertTrue(cell.getInitial() == 'N');

		// Test the initial for a walkway
		cell = board.getCell(9, 0);
		room = board.getRoom(cell);
		assertTrue(room != null);
		assertEquals(room.getName(), "Walkway");
		assertTrue(cell.getInitial() == 'W');
		assertFalse(cell.isRoomCenter());
		assertFalse(cell.isLabel());

		// Test the initial for a door
		cell = board.getCell(23, 18);
		room = board.getRoom(cell);
		assertTrue(room != null);
		assertEquals(room.getName(), "Walkway");
		assertTrue(cell.getInitial() == 'W');
		assertFalse(cell.isRoomCenter());
		assertFalse(cell.isLabel());

		// Test the unused area
		cell = board.getCell(0, 7);
		room = board.getRoom(cell);
		assertTrue(room != null);
		assertEquals(room.getName(), "Unused");
		assertTrue(cell.getInitial() == 'X');
		assertFalse(cell.isRoomCenter());
		assertFalse(cell.isLabel());
	}
}
