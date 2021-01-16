/**
 * @author Alexander Cieslewicz
 * @author Eric Dong
 * 
 * Game solution that stores room, person and weapon cards.
 */
package clueGame;

public class Solution {
	public Card person;
	public Card room;
	public Card weapon;

	public Solution(Card person, Card room, Card weapon) {
		super();
		this.person = person;
		this.room = room;
		this.weapon = weapon;
	}

	public Solution() {
		super();
	}

	public Card getPerson() {
		return person;
	}

	public Card getRoom() {
		return room;
	}

	public Card getWeapon() {
		return weapon;
	}

	@Override
	public String toString() {
		return person.toString() + ", " + room.toString() + ", " + weapon.toString();
	}

	@Override
	/**
	 * Compares two solution objects to determine if they are equal by verifying
	 * that each card matches
	 */
	public boolean equals(Object target) {
		if (target == this) {
			return true;
		}

		if (target instanceof Solution) {
			Solution targetSol = (Solution) target;
			return targetSol.getPerson().equals(this.getPerson()) && targetSol.getWeapon().equals(this.getWeapon())
					&& targetSol.getRoom().equals(this.getRoom());
		}
		else {
			return false;
		}
	}

}
