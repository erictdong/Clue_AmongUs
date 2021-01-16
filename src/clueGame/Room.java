/**
 * @author Alexander Cieslewicz
 * @author Eric Dong
 * 
 * A room object that keeps track or relevant details including label, center, passage and adjacent doors
 */
package clueGame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.HashSet;
import java.util.Set;

public class Room {
	private String name;
	private boolean isRoom;
	private BoardCell centerCell, labelCell, secretPassageCell;
	private Set<BoardCell> doors;
	private int currentOffset;

	public Room(String name) {
		super();
		this.name = name;
		doors = new HashSet<BoardCell>();
		isRoom = true;
		currentOffset = 0;
	}

	/**
	 * Draw a Room label on the board
	 * 
	 * @param g          Board graphics
	 * @param cellWidth  Offset to determine correct location
	 * @param cellHeight Offset to determine correct location
	 */
	public void draw(Graphics g, int cellWidth, int cellHeight) {
		if (isRoom) {
			g.setFont(new Font("TimesRoman", Font.PLAIN, 25));
			g.setColor(Color.BLACK);
			g.drawString(name, labelCell.getCol() * cellWidth, (labelCell.getRow() + 1) * cellHeight);
		}
	}

	/**
	 * Returns the current offset and increments. This offset is used to draw
	 * players when they are on the same room cell
	 * 
	 * @return currentOffset
	 */
	public int getCurrentOffset() {
		return currentOffset++;
	}

	public void resetCurrentOffset() {
		currentOffset = 0;
	}

	/*
	 * Getters and Setters
	 */

	/**
	 * Add a door to the set of adjacent doors
	 * 
	 * @param door BoardCell that represents a door
	 */
	public void addDoor(BoardCell door) {
		doors.add(door);
	}

	public String getName() {
		return name;
	}

	public BoardCell getLabelCell() {
		return labelCell;
	}

	public BoardCell getCenterCell() {
		return centerCell;
	}

	public void setCenterCell(BoardCell centerCell) {
		this.centerCell = centerCell;
	}

	public void setLabelCell(BoardCell labelCell) {
		this.labelCell = labelCell;
	}

	public boolean isRoom() {
		return isRoom;
	}

	public void setRoom(boolean isRoom) {
		this.isRoom = isRoom;
	}

	public Set<BoardCell> getDoors() {
		return doors;
	}

	public BoardCell getSecretPassageCell() {
		return secretPassageCell;
	}

	public void setSecretPassageCell(BoardCell secretPassageCell) {
		this.secretPassageCell = secretPassageCell;
	}

	public boolean hasSecretPassage() {
		if (secretPassageCell != null) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return name;
	}

}
