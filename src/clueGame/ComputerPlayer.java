/**
 * @author Alexander Cieslewicz
 * @author Eric Dong
 * 
 * Computer Player class for game
 */
package clueGame;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class ComputerPlayer extends Player {
	private Set<BoardCell> visitedRooms;
	private Solution accusation = null;

	public ComputerPlayer(String name, Color color, int row, int column) {
		super(name, color, row, column);
		visitedRooms = new HashSet<>();
	}

	public ComputerPlayer(String name, String color, int row, int column) {
		super(name, color, row, column);
		visitedRooms = new HashSet<>();
	}

	/**
	 * Generates a solution based on a random weapon and person card from unseen
	 * hand and the room the player is currently in
	 * 
	 * @return Solution object containing the generated suggestion cards.
	 */
	public Solution createSuggestion() {
		Card roomSugCard = new Card(super.getCurrentRoom().getName(), CardType.ROOM);
		Card weaponSugCard = null;
		Card personSugCard = null;
		Collections.shuffle(unseenCards);
		for (Card card : unseenCards) {
			if (card.getCardType() == CardType.PERSON && personSugCard == null) {
				personSugCard = card;
			}
			if (card.getCardType() == CardType.WEAPON && weaponSugCard == null) {
				weaponSugCard = card;
			}
		}
		return new Solution(personSugCard, roomSugCard, weaponSugCard);
	}

	/**
	 * Returns a random selection from a passed list of target board cells
	 * 
	 * @param targets
	 * @return If unvisited rooms select unvisited room randomly. If visited rooms
	 *         select randomly from rooms and walkways
	 */
	@Override
	public BoardCell selectTarget(Set<BoardCell> targets) {
		Random rand = new Random();
		ArrayList<BoardCell> reachableRoomCells = new ArrayList<>();

		// Get room cells that are not in visited
		for (BoardCell target : targets) {
			if (target.isRoom() && !visitedRooms.contains(target)) {
				reachableRoomCells.add(target);
				visitedRooms.add(target);
			}
		}

		// Select target appropriately
		if (reachableRoomCells.size() > 0) {
			return reachableRoomCells.get(rand.nextInt(reachableRoomCells.size()));
		}
		else {
			ArrayList<BoardCell> targetsList = new ArrayList<>();
			targetsList.addAll(targets);
			return targetsList.get(rand.nextInt(targetsList.size()));
		}
	}

	/*
	 * Getters & Setters
	 */
	public void addVisitedRoom(BoardCell cell) {
		visitedRooms.add(cell);
	}

	public Solution getAccusation() {
		return accusation;
	}

	public void setAccusation(Solution accusation) {
		this.accusation = accusation;
	}

	/*
	 * Junit test helpers
	 */
	public void clearVistedRooms() {
		visitedRooms.clear();
	}
}
