/**
 * @author Alexander Cieslewicz
 * @author Eric Dong
 * 
 * Test board that contains TestBoardCells
 */

package experiment;

import java.util.HashSet;
import java.util.Set;

public class TestBoard {
	final static int COLS = 4;
	final static int ROWS = 4;
	private TestBoardCell[][] grid;
	private Set<TestBoardCell> targets;
	private Set<TestBoardCell> visited;

	public TestBoard() {
		grid = new TestBoardCell[COLS][ROWS];
		// Fill board with cells
		for (int i = 0; i < COLS; i++) {
			for (int j = 0; j < ROWS; j++) {
				grid[i][j] = new TestBoardCell(i, j);
			}
		}

		// Create adjList for each cell
		for (int i = 0; i < COLS; i++) {
			for (int j = 0; j < ROWS; j++) {
				setupAdjList(grid[i][j], i, j);
			}
		}
	}

	/*
	 * Check for valid adjacent cells and add to cells adjList
	 */
	private void setupAdjList(TestBoardCell cell, int row, int col) {
		if ((row - 1) >= 0) {
			cell.addAdj(getCell(row - 1, col));
		}
		if ((row + 1) < ROWS) {
			cell.addAdj(getCell(row + 1, col));
		}
		if ((col - 1) >= 0) {
			cell.addAdj(getCell(row, col - 1));
		}
		if ((col + 1) < COLS) {
			cell.addAdj(getCell(row, col + 1));
		}
	}

	/*
	 * Determine reachable cells based on startCell and pathLength
	 */
	public void calcTargets(TestBoardCell startCell, int pathLength) {
		targets = new HashSet<TestBoardCell>();
		visited = new HashSet<TestBoardCell>();
		visited.add(startCell);
		findAllTargets(startCell, pathLength);
	}

	private void findAllTargets(TestBoardCell thisCell, int numSteps) {
		for (TestBoardCell adjCell : thisCell.getAdjList()) {
			if (!visited.contains(adjCell) && !adjCell.getOccupied()) {
				visited.add(adjCell);
				if (numSteps == 1 || adjCell.isRoom()) {
					targets.add(adjCell);
				}
				else if(!adjCell.isRoom()){
					findAllTargets(adjCell, numSteps - 1);
				}
				visited.remove(adjCell);
			}
		}
		
	}

	public Set<TestBoardCell> getTargets() {
		return targets;
	}

	public TestBoardCell getCell(int row, int col) {
		return grid[row][col];
	}
}
