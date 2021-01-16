/**
 * @author Alexander Cieslewicz
 * @author Eric Dong
 * 
 * A cell in the board gird of Board. Contains info about what the cell represents
 */
package clueGame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

public class BoardCell {
	private static final int DOOR_SCALE = 7;
	private static final Color DEFAULT_CELL_COLOR = Color.BLACK;
	private static final Color WALKWAY_CELL_COLOR = new Color(207, 185, 151);
	private static final Color ROOM_CELL_COLOR = new Color(204, 255, 255);
	private int row, col;
	private char initial, secretPassage;

	private boolean roomLabel, roomCenter, isOccupied, isRoom, isUnused;
	private DoorDirection doorDirection;
	private Set<BoardCell> adjList;

	public BoardCell(int row, int col, char initial) {
		super();
		this.row = row;
		this.col = col;
		this.initial = initial;
		secretPassage = ' ';
		roomLabel = false;
		roomCenter = false;
		isOccupied = false;
		isUnused = false;
		doorDirection = DoorDirection.NONE;
		adjList = new HashSet<>();
	}

	/*
	 * Add cell to adjacency List
	 */
	public void addAdj(BoardCell cell) {
		adjList.add(cell);
	}

	/**
	 * Draw a cell on a graphics object
	 * 
	 * @param g          Graphics object
	 * @param cellWidth
	 * @param cellHeight
	 * @param row
	 * @param col
	 * 
	 */
	public void draw(Graphics g, int cellWidth, int cellHeight, int row, int col, Color cellColor) {
		int cellPositionVertical = cellHeight * row;
		int cellPositionHorizontal = cellWidth * col;

		// Draw default cell
		if (cellColor == null) {
			g.setColor(getCellColor());
		}
		else {
			g.setColor(cellColor);
		}
		g.fillRect(cellPositionHorizontal, cellPositionVertical, cellWidth, cellHeight);

		// Draw border if it is a walkway
		if (isWalkway()) {
			g.setColor(DEFAULT_CELL_COLOR);
			g.drawRect(cellPositionHorizontal, cellPositionVertical, cellWidth, cellHeight);
		}

		if (isDoorway()) {
			drawDoorway(g, cellWidth, cellHeight, cellPositionVertical, cellPositionHorizontal);
		}

		if (secretPassage != ' ') {
			drawSecretPassage(g, cellWidth, cellHeight, cellPositionVertical, cellPositionHorizontal);
		}
	}

	/**
	 * Helper function to draw a door on a cell
	 * 
	 * @param g
	 * @param cellWidth
	 * @param cellHeight
	 * @param cellPositionVertical
	 * @param cellPositionHorizontal
	 */
	private void drawDoorway(Graphics g, int cellWidth, int cellHeight, int cellPositionVertical,
			int cellPositionHorizontal) {
		int wOffset = 0;
		int hOffset = 0;
		g.setColor(Color.BLACK);
		switch (doorDirection) {
		case LEFT:
			g.fillRect(cellPositionHorizontal, cellPositionVertical, cellWidth / DOOR_SCALE, cellHeight);
			break;
		case RIGHT:
			wOffset = (cellWidth - cellWidth / DOOR_SCALE);
			g.fillRect(cellPositionHorizontal + wOffset, cellPositionVertical, cellWidth / DOOR_SCALE, cellHeight);
			break;
		case UP:
			g.fillRect(cellPositionHorizontal, cellPositionVertical, cellWidth, cellHeight / DOOR_SCALE);
			break;
		case DOWN:
			hOffset = (cellHeight - cellHeight / DOOR_SCALE);
			g.fillRect(cellPositionHorizontal, cellPositionVertical + hOffset, cellWidth, cellHeight / DOOR_SCALE);
			break;
		}
	}

	/**
	 * Helper function to draw a grate on a secret passage
	 * 
	 * @param g
	 * @param cellWidth
	 * @param cellHeight
	 * @param cellPositionVertical
	 * @param cellPositionHorizontal
	 */
	private void drawSecretPassage(Graphics g, int cellWidth, int cellHeight, int cellPositionVertical,
			int cellPositionHorizontal) {
		Image image = null;

		// Handle loading vent from jar or file
		if (ClueGame.JAR) {
			try {
				InputStream is = getClass().getResourceAsStream("vent.png");
				if (is == null) {
					throw new FileNotFoundException("Could not find layout file");
				}
				image = ImageIO.read(is);
			}
			catch (IOException e) {
				e.printStackTrace();
			}

		}
		else {
			try {
				image = ImageIO.read(new File("resources/sprites/vent.png"));
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (image != null) {
			g.drawImage(image, cellPositionHorizontal, cellPositionVertical, cellWidth, cellHeight, null);
		}
		else {
			g.setColor(Color.GRAY);
			g.fillRect(cellPositionHorizontal, cellPositionVertical, cellWidth / DOOR_SCALE, cellHeight);
			g.fillRect(cellPositionHorizontal, cellPositionVertical, cellWidth, cellHeight / DOOR_SCALE);
			int wOffset = (cellWidth - cellWidth / DOOR_SCALE);
			g.fillRect(cellPositionHorizontal + wOffset, cellPositionVertical, cellWidth / DOOR_SCALE, cellHeight);
			int hOffset = (cellHeight - cellHeight / DOOR_SCALE);
			g.fillRect(cellPositionHorizontal, cellPositionVertical + hOffset, cellWidth, cellHeight / DOOR_SCALE);
			g.fillRect(cellPositionHorizontal, cellPositionVertical + cellHeight / 2, cellWidth,
					cellHeight / DOOR_SCALE);
			g.fillRect(cellPositionHorizontal, cellPositionVertical + cellHeight * 1 / 4, cellWidth,
					cellHeight / DOOR_SCALE);
			g.fillRect(cellPositionHorizontal, cellPositionVertical + cellHeight * 3 / 4, cellWidth,
					cellHeight / DOOR_SCALE);
		}
	}

	public boolean containsClick(int mouseX, int mouseY, int cellWidth, int cellHeight) {
		Rectangle rect = new Rectangle(row * cellWidth, col * cellHeight, cellWidth, cellHeight);
		if (rect.contains(new Point(mouseX, mouseY))) {
			return true;
		}
		else
			return false;
	}

	/**
	 * Returns a color based on cell type
	 */
	public Color getCellColor() {
		if (isRoom()) {
			return ROOM_CELL_COLOR;
		}
		else if (isWalkway()) {
			return WALKWAY_CELL_COLOR;
		}
		else {
			return DEFAULT_CELL_COLOR;
		}
	}

	/*
	 * Getters and Setters
	 */
	public Set<BoardCell> getAdjList() {
		return adjList;
	}

	public boolean isLabel() {
		return roomLabel;
	}

	public boolean isRoomCenter() {
		return roomCenter;
	}

	public char getSecretPassage() {
		return secretPassage;
	}

	public DoorDirection getDoorDirection() {
		return doorDirection;
	}

	public boolean isDoorway() {
		if (doorDirection != DoorDirection.NONE) {
			return true;
		}
		return false;
	}

	public char getInitial() {
		return initial;
	}

	public void setRoomLabel(boolean roomLabel) {
		this.roomLabel = roomLabel;
	}

	public void setSecretPassage(char secretPassage) {
		this.secretPassage = secretPassage;
	}

	public void setRoomCenter(boolean roomCenter) {
		this.roomCenter = roomCenter;
	}

	public void setDoorDirection(DoorDirection doorDirection) {
		this.doorDirection = doorDirection;
	}

	public boolean isOccupied() {
		return isOccupied;
	}

	public void setOccupied(boolean isOccupied) {
		this.isOccupied = isOccupied;
	}

	public boolean isRoom() {
		return isRoom;
	}

	public void setRoom(boolean isRoom) {
		this.isRoom = isRoom;
	}

	public boolean isUnused() {
		return isUnused;
	}

	public void setUnused(boolean isUnused) {
		this.isUnused = isUnused;
	}

	public boolean isWalkway() {
		return !isRoom() && !isUnused();
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}
}
