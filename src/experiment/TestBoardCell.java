/**
 * @author Alexander Cieslewicz
 * @author Eric Dong
 * 
 * Test board cell class contains position and state status
 */
package experiment;

import java.util.HashSet;
import java.util.Set;

public class TestBoardCell {
	private int row, col;
	private boolean isRoom, isOccupied;
	private Set<TestBoardCell> adjList;

	public TestBoardCell(int row, int col) {
		super();
		this.row = row;
		this.col = col;
		this.isRoom = false;
		this.isOccupied = false;
		adjList = new HashSet<TestBoardCell>();
	}

	public void addAdj(TestBoardCell cell) {
		adjList.add(cell);
	}

	/*
	 * Return a set of adjacent cell
	 */
	public Set<TestBoardCell> getAdjList() {
		return adjList;
	}

	public void setOccupied(boolean setOccupied) {
		isOccupied = setOccupied;
	}

	public boolean getOccupied() {
		return isOccupied;
	}

	public void setRoom(boolean setRoom) {
		isRoom = setRoom;
	}

	@Override
	public String toString() {
		return "TestBoardCell [row=" + row + ", col=" + col + "]";
	}

	public boolean isRoom() {
		return isRoom;
	}
}
