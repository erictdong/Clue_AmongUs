/**
 * @author Alexander Cieslewicz
 * @author Eric Dong
 * 
 * Creates the Card Display GUI
 */
package clueGame;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

@SuppressWarnings("serial")
public class CardDisplayPanel extends JPanel {

	private JPanel peopleCardsInHand;
	private JPanel peopleCardsSeen;
	private JPanel roomCardsInHand;
	private JPanel roomCardsSeen;
	private JPanel weaponCardsInHand;
	private JPanel weaponCardsSeen;
	private Player displayPlayer;

	/**
	 * Setup card display section
	 * 
	 * @param displayPlayer
	 */
	public CardDisplayPanel(Player displayPlayer) {
		setLayout(new GridLayout(3, 0));
		setBorder(new TitledBorder(new EtchedBorder(), "Known Cards"));
		JPanel peopleCards = createCardGroupingPanel("People");
		peopleCardsInHand = createCardDisplayPanel("In Hand:");
		peopleCardsSeen = (createCardDisplayPanel("Seen:"));
		peopleCards.add(peopleCardsInHand);
		peopleCards.add(peopleCardsSeen);
		add(peopleCards);

		JPanel roomCards = createCardGroupingPanel("Rooms");
		roomCardsInHand = createCardDisplayPanel("In Hand:");
		roomCardsSeen = createCardDisplayPanel("Seen:");
		roomCards.add(roomCardsInHand);
		roomCards.add(roomCardsSeen);
		add(roomCards);

		JPanel weaponCards = createCardGroupingPanel("Weapons");
		weaponCardsInHand = createCardDisplayPanel("In Hand:");
		weaponCardsSeen = createCardDisplayPanel("Seen:");
		weaponCards.add(weaponCardsInHand);
		weaponCards.add(weaponCardsSeen);
		add(weaponCards);

		this.displayPlayer = displayPlayer;
	}

	// Create the Panel for each group of cards
	private JPanel createCardGroupingPanel(String cardType) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(2, 0));
		panel.setBorder(new TitledBorder(new EtchedBorder(), cardType));

		return panel;
	}

	// Create the in hand and seen panels for each card group
	private JPanel createCardDisplayPanel(String status) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1));
		panel.setBorder(new TitledBorder(status));
		panel.add(new JTextField("None", 10));

		return panel;
	}

	// updates people section of gui
	private void updatePeople(ArrayList<Card> inHandCards, ArrayList<Card> seenCards) {
		buildCardDisplayArray(inHandCards, peopleCardsInHand);
		buildCardDisplayArray(seenCards, peopleCardsSeen);
	}

	// updates room section of gui
	private void updateRooms(ArrayList<Card> inHandCards, ArrayList<Card> seenCards) {
		buildCardDisplayArray(inHandCards, roomCardsInHand);
		buildCardDisplayArray(seenCards, roomCardsSeen);
	}

	// update weapon section of gui
	private void updateWeapons(ArrayList<Card> inHandCards, ArrayList<Card> seenCards) {
		buildCardDisplayArray(inHandCards, weaponCardsInHand);
		buildCardDisplayArray(seenCards, weaponCardsSeen);
	}

	// Builds the Jtextfields for each card and colors them depending on player
	// ownership
	private void buildCardDisplayArray(ArrayList<Card> cards, JPanel panel) {

		panel.removeAll();
		if (cards.size() == 0) {
			JTextField newCard = new JTextField("None", 10);
			newCard.setEditable(false);
			panel.add(newCard);
		}
		else {
			for (Card card : new HashSet<Card>(cards)) {
				JTextField newCard = new JTextField(card.getCardName(), 10);
				newCard.setEditable(false);
				newCard.setBackground(card.getCardHolder().getColor());
				panel.add(newCard);
			}
		}

		panel.revalidate();
	}

	// Helper function that will separate cards into seen and and hand cards
	public void updateCardDisplay() {
		ArrayList<Card> playerCards = displayPlayer.getHand();
		ArrayList<Card>[] parsedHandCards = new ArrayList[3];
		parsedHandCards[0] = new ArrayList<Card>();
		parsedHandCards[1] = new ArrayList<Card>();
		parsedHandCards[2] = new ArrayList<Card>();
		for (Card card : playerCards) {
			if (card.getCardType().equals(CardType.PERSON)) {
				parsedHandCards[0].add(card);
			}
			if (card.getCardType().equals(CardType.ROOM)) {
				parsedHandCards[1].add(card);
			}
			if (card.getCardType().equals(CardType.WEAPON)) {
				parsedHandCards[2].add(card);
			}
		}

		ArrayList<Card> seenCards = displayPlayer.getSeenCards();
		ArrayList<Card>[] parsedSeenCards = new ArrayList[3];
		parsedSeenCards[0] = new ArrayList<Card>();
		parsedSeenCards[1] = new ArrayList<Card>();
		parsedSeenCards[2] = new ArrayList<Card>();

		for (Card card : seenCards) {
			if (card.getCardType().equals(CardType.PERSON) && !parsedHandCards[0].contains(card)) {
				parsedSeenCards[0].add(card);
			}
			if (card.getCardType().equals(CardType.ROOM) && !parsedHandCards[1].contains(card)) {
				parsedSeenCards[1].add(card);
			}
			if (card.getCardType().equals(CardType.WEAPON) && !parsedHandCards[2].contains(card)) {
				parsedSeenCards[2].add(card);
			}
		}

		updatePeople(parsedHandCards[0], parsedSeenCards[0]);
		updateRooms(parsedHandCards[1], parsedSeenCards[1]);
		updateWeapons(parsedHandCards[2], parsedSeenCards[2]);
	}

	public static void main(String[] args) {
		// Create test variables
		ArrayList<Card> deck = new ArrayList<>();
		Card card1 = new Card("Pink", CardType.PERSON);
		Card card2 = new Card("Energy", CardType.ROOM);
		Card card3 = new Card("Spike", CardType.WEAPON);
		Card card4 = new Card("Red", CardType.PERSON);
		Card card5 = new Card("Shields", CardType.ROOM);
		Card card6 = new Card("Gun", CardType.WEAPON);
		Card card7 = new Card("Fists", CardType.WEAPON);
		Card card8 = new Card("Knife", CardType.WEAPON);

		deck.add(card1);
		deck.add(card2);
		deck.add(card3);
		deck.add(card4);
		deck.add(card5);
		deck.add(card6);
		deck.add(card7);
		deck.add(card8);

		Player player1 = new HumanPlayer("Cyan", Color.cyan, 0, 0);
		Player player2 = new HumanPlayer("Pink", Color.pink, 0, 0);
		Player player3 = new HumanPlayer("LightGray", Color.lightGray, 0, 0);

		player1.setUnseenCards(deck);

		card1.setCardHolder(player1);
		card2.setCardHolder(player1);
		card3.setCardHolder(player1);
		card4.setCardHolder(player2);
		card5.setCardHolder(player2);
		card6.setCardHolder(player2);
		card7.setCardHolder(player2);
		card8.setCardHolder(player3);

		player1.updateHand(card1);
		player1.updateHand(card2);
		player1.updateHand(card3);
		player1.updateSeen(card4);
		player1.updateSeen(card5);
		player1.updateSeen(card6);
		player1.updateSeen(card7);
		player1.updateSeen(card8);

		CardDisplayPanel panel = new CardDisplayPanel(player1); // create the panel 2x0
		JFrame frame = new JFrame(); // create the frame
		frame.setContentPane(panel); // put the panel in the frame
		frame.setSize(180, 1000); // size the frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // allow it to close
		frame.setVisible(true); // make it visible

		panel.updateCardDisplay();
		panel.peopleCardsInHand.removeAll();
	}

}
