/**
 * @author Alexander Cieslewicz
 * @author Eric Dong
 * 
 * Cards for the game that contain the names and types of cards
 */
package clueGame;

public class Card {
	private String cardName;
	private CardType cardType;
	private Player cardHolder;

	public Card(String cardName, CardType cardType) {
		super();
		this.cardName = cardName;
		this.cardType = cardType;
	}

	@Override
	/**
	 * Compares cards based on name and type to determien equality
	 */
	public boolean equals(Object target) {
		if (target == this) {
			return true;
		}

		if (target instanceof Card) {
			Card targetCard = (Card) target;
			return targetCard.getCardName().equals(this.getCardName())
					&& targetCard.getCardType().equals(this.getCardType());
		}
		else {
			return false;
		}
	}

	/*
	 * Getters & Setters
	 */

	public String getCardName() {
		return cardName;
	}

	public CardType getCardType() {
		return cardType;
	}

	public Player getCardHolder() {
		return cardHolder;
	}

	public void setCardHolder(Player cardHolder) {
		this.cardHolder = cardHolder;
	}

	@Override
	public String toString() {
		return cardName;
	}

}
