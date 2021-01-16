/**
 * @author Alexander Cieslewicz
 * @author Eric Dong
 * 
 * Abstract Player class for game.
 */
package clueGame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;

public abstract class Player {
	private String name;
	private Color color;
	private Room currentRoom;
	protected int row, column;
	private ArrayList<Card> hand;
	private ArrayList<Card> seenCards;
	protected ArrayList<Card> unseenCards;
	protected boolean isHuman;
	private boolean movedSuggestion;

	public Player(String name, String color, int row, int column) {
		this(name, convertStrColor(color), row, column);
	}

	public Player(String name, Color color, int row, int column) {
		this.name = name;
		this.color = color;
		this.row = row;
		this.column = column;
		hand = new ArrayList<>();
		seenCards = new ArrayList<>();
		unseenCards = new ArrayList<>();
	}

	public BoardCell selectTarget(Set<BoardCell> targets) {
		return null;
	}

	public Solution createSuggestion() {
		return null;
	}

	/**
	 * Converts a string color to a awt Color
	 * 
	 * @param color String color
	 * @return awt Color equivalent
	 */
	private static Color convertStrColor(String color) {
		if (color.equals("red")) {
			return new Color(230, 72, 72);
		}
		else if (color.equals("cyan")) {
			return Color.cyan;
		}
		else if (color.equals("orange")) {
			return Color.orange;
		}
		else if (color.equals("yellow")) {
			return Color.yellow;
		}
		else if (color.equals("black")) {
			return new Color(113, 106, 106);
		}
		else if (color.equals("pink")) {
			return Color.pink;
		}
		else {
			return Color.magenta;
		}
	}

	/**
	 * Add a card to hand and update the seen cards
	 * 
	 * @param card
	 */
	public void updateHand(Card card) {
		hand.add(card);
		updateSeen(card);
	}

	/**
	 * Add a card to seen and adjust unseen cards
	 * 
	 * @param card
	 */
	public void updateSeen(Card card) {
		seenCards.add(card);
		updateUnseen(card);
	}

	/**
	 * Remove card from unseen cards
	 * 
	 * @param card
	 */
	public void updateUnseen(Card card) {
		if (unseenCards.contains(card)) {
			unseenCards.remove(unseenCards.indexOf(card));
		}
	}

	/**
	 * Draw player marker based on position
	 * 
	 * @param g          Board graphics object
	 * @param cellWidth
	 * @param cellHeight
	 */
	public void draw(Graphics g, int cellWidth, int cellHeight, int offset) {

		// Handle importing from jar or files
		Image image = null;
		if (ClueGame.JAR) {
			try {
				InputStream is = null;
				if (color.equals(new Color(230, 72, 72))) {
					is = getClass().getResourceAsStream("red.png");
				}
				else if (color.equals(Color.cyan)) {
					is = getClass().getResourceAsStream("cyan.png");
				}
				else if (color.equals(Color.orange)) {
					is = getClass().getResourceAsStream("orange.png");
				}
				else if (color.equals(Color.yellow)) {
					is = getClass().getResourceAsStream("yellow.png");
				}
				else if (color.equals(new Color(113, 106, 106))) {
					is = getClass().getResourceAsStream("black.png");
				}
				else if (color.equals(Color.pink)) {
					is = getClass().getResourceAsStream("pink.png");
				}
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
				if (color.equals(new Color(230, 72, 72))) {
					image = ImageIO.read(new File("resources/sprites/red.png"));
				}
				else if (color.equals(Color.cyan)) {
					image = ImageIO.read(new File("resources/sprites/cyan.png"));
				}
				else if (color.equals(Color.orange)) {
					image = ImageIO.read(new File("resources/sprites/orange.png"));
				}
				else if (color.equals(Color.yellow)) {
					image = ImageIO.read(new File("resources/sprites/yellow.png"));
				}
				else if (color.equals(new Color(113, 106, 106))) {
					image = ImageIO.read(new File("resources/sprites/black.png"));
				}
				else if (color.equals(Color.pink)) {
					image = ImageIO.read(new File("resources/sprites/pink.png"));
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (image != null) {
			g.drawImage(image, column * cellWidth + offset, row * cellHeight, cellWidth, cellHeight, null);
		}
		else {
			g.setColor(getColor());
			g.fillOval(column * cellWidth + offset, row * cellHeight, cellWidth, cellHeight);
		}
	}

	/**
	 * Determines whether a suggestion is valid or not based on the cards in all of
	 * the players hands other than the accuser
	 * 
	 * @param personSug person suggestion card
	 * @param roomSug   room suggestion card
	 * @param weaponSug weapon suggestion card
	 * @return The first card that disproves the suggestion
	 */
	public Card disproveSuggestion(Card personSug, Card roomSug, Card weaponSug) {
		ArrayList<Card> matches = new ArrayList<>();
		if (hand.contains(personSug)) {
			matches.add(personSug);
		}
		if (hand.contains(roomSug)) {
			matches.add(roomSug);
		}
		if (hand.contains(weaponSug)) {
			matches.add(weaponSug);
		}

		if (matches.size() == 1) {
			return matches.get(0);
		}
		else if (matches.size() > 1) {
			Random rand = new Random();
			return matches.get(rand.nextInt(matches.size()));
		}
		else {
			return null;
		}

	}

	/*
	 * Getters and Setters
	 */
	public Solution getAccusation() {
		return null;
	}

	public void setAccusation(Solution accusation) {
		return;
	}

	public void setUnseenCards(ArrayList<Card> deck) {
		unseenCards = new ArrayList<>(deck);
	}

	public String getName() {
		return name;
	}

	public Color getColor() {
		return color;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public Room getCurrentRoom() {
		return currentRoom;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public ArrayList<Card> getHand() {
		return hand;
	}

	public void updatePosition(BoardCell target) {
		row = target.getRow();
		column = target.getCol();
	}

	public void setRoom(Room updatedRoom) {
		currentRoom = updatedRoom;
	}

	public ArrayList<Card> getSeenCards() {
		return seenCards;
	}

	public boolean isHuman() {
		return isHuman;
	}

	/**
	 * Check if the player was moved for a suggestion
	 * 
	 * @return bool
	 */
	public boolean isMovedSuggestion() {
		return movedSuggestion;
	}

	public void setMovedSuggestion(boolean movedSuggestion) {
		this.movedSuggestion = movedSuggestion;
	}

	@Override
	public String toString() {
		return name;
	}

}
