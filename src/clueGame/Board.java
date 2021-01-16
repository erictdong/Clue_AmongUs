/**
 * @author Alexander Cieslewicz
 * @author Eric Dong
 * 
 * Board game board, contains cells configuration methods to load data from setup files
 */

package clueGame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class Board extends JPanel {
	private static final String FILE_DELIMITER = ", ";
	private static final char DOOR_DOWN = 'v';
	private static final char DOOR_RIGHT = '>';
	private static final char DOOR_LEFT = '<';
	private static final char DOOR_UP = '^';
	private static final char ROOM_LABEL = '#';
	public static final char ROOM_CENTER = '*';
	private static final String COMMENT_PREFIX = "//";
	/*
	 * Variables for Board instance
	 */
	private int numRows, numColumns;
	private String layoutConfigFile, setupConfigFile;
	private Map<Character, Room> roomMap;
	private BoardCell[][] grid;
	private Set<BoardCell> targets;
	private Set<BoardCell> visited;

	private Solution theAnswer;
	private ArrayList<Player> players;
	private ArrayList<Card> deck;
	private Map<String, Card> weaponCards;
	private Map<String, Card> playerCards;
	private Map<String, Card> roomCards;

	private int currentPlayer;
	private int diceRoll;
	public boolean isPlayerFinished;

	private GameControlPanel gameControlPanel;

	/*
	 * Variables for game control
	 */

	Solution currentSuggestion;
	Card currentDisprove;

	/*
	 * variable and methods used for singleton pattern
	 */
	private static Board theInstance = new Board();

	// constructor is private to ensure only one can be created
	private Board() {
		super();
		targets = new HashSet<>();
		currentPlayer = 0;
		diceRoll = rollDice();
		addMouseListener(new boardMouseListener());
	}

	// add the mouse listener class to use in board
	private class boardMouseListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		/*
		 * Handle board actions if human player's turn and human clicks on board
		 */
		public void mousePressed(MouseEvent e) {
			if (!isPlayerFinished) {
				// If no possible targets set player turn to finished
				if (targets.size() == 0) {
					isPlayerFinished = true;
					return;
				}
				// Iterate through cells to find which one contains the click
				for (int row = 0; row < numRows; row++) {
					for (int col = 0; col < numColumns; col++) {
						BoardCell currentCell = getCell(row, col);
						if (currentCell.containsClick(e.getY(), e.getX(), getHeight() / numRows,
								getWidth() / numColumns)) {
							// If cell is in a room get the center cell
							if (currentCell.isRoom()) {
								currentCell = getRoom(currentCell).getCenterCell();
							}
							// UPdate the cells to reflect player move
							if (targets.contains(currentCell)) {
								getCell(getCurrentPlayer().getRow(), getCurrentPlayer().getColumn()).setOccupied(false);
								if (currentCell.isRoom()) {
									Room currentRoom = getRoom(currentCell);
									getCurrentPlayer().updatePosition(currentRoom.getCenterCell());
								}
								else {
									getCurrentPlayer().updatePosition(currentCell);
								}
								currentCell.setOccupied(true);

								getCurrentPlayer()
										.setRoom(getRoom(getCurrentPlayer().getRow(), getCurrentPlayer().getColumn()));
								targets = new HashSet<>();
								repaint();

								// Generate suggestion box if moved to room
								if (currentCell.isRoom()) {
									JDialog suggestionBox = new SuggestionBox();
								}

								// Move other players if accussed in suggestion
								if (currentSuggestion != null) {
									for (Player player : players) {
										if (player.getName().equals(currentSuggestion.getPerson().getCardName())) {
											getCell(player.getRow(), player.getColumn()).setOccupied(false);
											player.setMovedSuggestion(true);
											player.updatePosition(currentCell);
											player.setRoom(getRoom(player.getRow(), player.getColumn()));
										}
									}
								}
								repaint();
								isPlayerFinished = true;
								return;
							}
						}
					}
				}
				JOptionPane.showMessageDialog(theInstance, "Invalid target.");
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
	}

	@Override
	/**
	 * Paint the board and players on the board
	 * 
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		int cellWidth = getWidth() / numColumns;
		int cellHeight = getHeight() / numRows;

		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numColumns; col++) {
				if ((targets.contains(getCell(row, col))) && (players.get(currentPlayer).isHuman())) {
					getCell(row, col).draw(g, cellWidth, cellHeight, row, col, Color.BLUE);
				}
				else if (targets.contains(getRoom(getCell(row, col)).getCenterCell())
						&& (players.get(currentPlayer).isHuman())) {
					getCell(row, col).draw(g, cellWidth, cellHeight, row, col, Color.BLUE);
				}
				else {
					getCell(row, col).draw(g, cellWidth, cellHeight, row, col, null);
				}
			}
		}

		// Handle importing image from jar or files
		Image image = null;
		if (ClueGame.JAR) {
			try {
				InputStream is = getClass().getResourceAsStream("cafeteria.png");
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
				image = ImageIO.read(new File("resources/sprites/cafeteria.png"));
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (image != null) {
			g.drawImage(image, 8 * cellWidth, 8 * cellHeight, cellWidth * 7, cellHeight * 7, null);
		}

		for (Room room : roomMap.values()) {
			room.draw(g, cellWidth, cellHeight);
		}

		for (Player player : players) {
			if (player.getCurrentRoom() != null && player.getCurrentRoom().isRoom()) {
				player.draw(g, cellWidth, cellHeight, player.getCurrentRoom().getCurrentOffset() * 10);
			}
			else {
				player.draw(g, cellWidth, cellHeight, 0);
			}
		}

		for (Room room : roomMap.values()) {
			room.resetCurrentOffset();
		}

	}

	@SuppressWarnings("serial")
	/**
	 * Create an accusation box so that a player can make an accusation and submit
	 * it
	 *
	 */
	public class AccusationBox extends JDialog {
		JComboBox personCombo;
		JComboBox weaponCombo;
		JComboBox roomCombo;
		JButton submit, cancel;

		public AccusationBox() {
			setModal(true);
			setTitle("Make Accusation");
			setLayout(new GridLayout(4, 2));
			add(new JLabel("Room"));
			roomCombo = new JComboBox(roomCards.values().toArray());
			add(roomCombo);
			add(new JLabel("Person"));
			personCombo = new JComboBox(playerCards.values().toArray());
			add(personCombo);
			add(new JLabel("Weapon"));
			weaponCombo = new JComboBox(weaponCards.values().toArray());
			add(weaponCombo);

			setupButtons();
			pack();
		}

		/**
		 * Create the next and cancel buttons for the accusation box
		 */
		private void setupButtons() {
			cancel = new JButton("Cancel");
			add(cancel);
			submit = new JButton("Submit");
			add(submit);

			class ButtonListener implements ActionListener {
				@Override
				/**
				 * If next is pressed check if the accusation is valid and display win/lose and
				 * exit
				 */
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() == submit) {
						Solution accusation = new Solution();
						accusation.room = roomCards.get(roomCombo.getSelectedItem().toString());
						accusation.weapon = weaponCards.get(weaponCombo.getSelectedItem().toString());
						accusation.person = playerCards.get(personCombo.getSelectedItem().toString());
						if (accusation.equals(theAnswer)) {
							JOptionPane.showMessageDialog(theInstance,
									"You have won the game. The Solution was " + theAnswer.toString());
						}
						else {
							JOptionPane.showMessageDialog(theInstance,
									"You have lost the game. The Solution was " + theAnswer.toString());
						}
						setVisible(false);
						System.exit(0);
					}
					else {
						setVisible(false);
					}
				}
			}

			ButtonListener listener = new ButtonListener();
			cancel.addActionListener(listener);
			submit.addActionListener(listener);
		}
	}

	/**
	 * Create Accusation popup and handle input
	 */
	public void makeAccusation() {
		if (!isPlayerFinished) {
			AccusationBox accusationBox = new AccusationBox();
			accusationBox.setVisible(true);
		}
		else {
			JOptionPane.showMessageDialog(theInstance, "It's not your turn fool!");
		}

	}

	/**
	 * Allow player to create a suggestion
	 *
	 */
	@SuppressWarnings("serial")
	public class SuggestionBox extends JDialog {
		JComboBox personCombo;
		JComboBox weaponCombo;
		JButton submit, cancel;

		public SuggestionBox() {
			setModal(true);
			setTitle("Make Suggestion");
			setLayout(new GridLayout(4, 2));
			add(new JLabel("Current Room"));
			JTextField roomName = new JTextField(getCurrentPlayer().getCurrentRoom().toString());
			roomName.setEditable(false);
			add(roomName);
			add(new JLabel("Person"));
			personCombo = new JComboBox(playerCards.values().toArray());
			add(personCombo);
			add(new JLabel("Weapon"));
			weaponCombo = new JComboBox(weaponCards.values().toArray());
			add(weaponCombo);

			setupButtons();
			pack();
			setVisible(true);
		}

		private void setupButtons() {
			cancel = new JButton("Cancel");
			add(cancel);
			submit = new JButton("Submit");
			add(submit);

			class ButtonListener implements ActionListener {
				@Override
				/**
				 * When submit is pressed get the current suggestions and validate
				 */
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() == submit) {
						currentSuggestion = new Solution();
						currentSuggestion.room = roomCards.get(getCurrentPlayer().getCurrentRoom().getName());
						currentSuggestion.weapon = weaponCards.get(weaponCombo.getSelectedItem().toString());
						currentSuggestion.person = playerCards.get(personCombo.getSelectedItem().toString());
						currentDisprove = handleSuggestion(getCurrentPlayer(), currentSuggestion.getPerson(),
								currentSuggestion.getRoom(), currentSuggestion.getWeapon());
						if (currentDisprove != null) {
							getCurrentPlayer().updateSeen(currentDisprove);
						}
						gameControlPanel.updateSuggestions();
					}
					setVisible(false);
				}
			}

			ButtonListener listener = new ButtonListener();
			cancel.addActionListener(listener);
			submit.addActionListener(listener);
		}
	}

	// Updates current player and draws the targets if Human Player
	public void updateBoardState() {
		// Reset the suggestions for new player
		currentSuggestion = null;
		currentDisprove = null;

		// Calculate new targets and update player moved due to suggestion state
		calcTargets(getCell(players.get(currentPlayer).row, players.get(currentPlayer).column), diceRoll);
		Player movePlayer = getCurrentPlayer();
		if (movePlayer.isMovedSuggestion()) {
			movePlayer.setMovedSuggestion(false);
			targets.add(getCell(movePlayer.getRow(), movePlayer.getColumn()));
		}

		if (movePlayer.isHuman() && targets.size() == 0) {

		}

		// Hande suggestions and accusations for computer players
		if (!movePlayer.isHuman()) {
			// Complete accusation if possible
			if (movePlayer.getAccusation() != null) {
				if (movePlayer.getAccusation().equals(theAnswer)) {
					JOptionPane.showMessageDialog(theInstance,
							movePlayer.getName() + " has won the game. The Solution was " + theAnswer.toString());
				}
				else {
					JOptionPane.showMessageDialog(theInstance,
							movePlayer.getName() + " has lost the game. The Solution was " + theAnswer.toString());
				}
				System.exit(0);
			}

			// Move the computer player and update board cells
			getCell(movePlayer.getRow(), movePlayer.getColumn()).setOccupied(false);
			if (targets.size() == 0) {
				return;
			}
			BoardCell target = movePlayer.selectTarget(getTargets());
			Room previousRoom = movePlayer.getCurrentRoom();
			movePlayer.updatePosition(target);
			target.setOccupied(true);
			movePlayer.setRoom(getRoom(movePlayer.getRow(), movePlayer.getColumn()));

			// Handle computer suggestions if computer makes it into room
			if (movePlayer.getCurrentRoom().isRoom()) {
				currentSuggestion = movePlayer.createSuggestion();
				for (Player player : players) {
					if (player.getName().equals(currentSuggestion.getPerson().getCardName())) {
						getCell(player.getRow(), player.getColumn()).setOccupied(false);
						player.updatePosition(target);
						player.setRoom(getRoom(movePlayer.getRow(), movePlayer.getColumn()));
						player.setMovedSuggestion(true);
						if (player.isHuman()) {
							System.out.println("Why");
						}
					}
				}
				currentDisprove = handleSuggestion(movePlayer, currentSuggestion.getPerson(),
						currentSuggestion.getRoom(), currentSuggestion.getWeapon());
				if (currentDisprove != null) {
					movePlayer.updateSeen(currentDisprove);
				}
				else {
					if (!movePlayer.getHand().contains(currentSuggestion.getRoom())) {
						movePlayer.setAccusation(currentSuggestion);
					}
				}

			}

		}

		this.repaint();
	}

	// this method returns the only Board
	public static Board getInstance() {
		return theInstance;
	}

	// roll the dice
	public int rollDice() {
		Random rnd = new Random();
		diceRoll = rnd.nextInt(6) + 1;
		return diceRoll;
	}

	/*
	 * initialize the board (since we are using singleton pattern)
	 */
	public void initialize() {
		try {
			loadConfigFiles();
			// calcTargets(getCell(players.get(0).row, players.get(0).column), 5);
		}
		catch (FileNotFoundException | BadConfigFormatException e) {
			System.out.println(e.getMessage());
		}

	}

	/*
	 * Load the configuration of the board and cell from the files
	 */
	public void loadConfigFiles() throws FileNotFoundException, BadConfigFormatException {
		loadSetupConfig();
		loadLayoutConfig();
	}

	/*
	 * Loads the game config setup and builds the game board legend
	 */
	public void loadSetupConfig() throws FileNotFoundException, BadConfigFormatException {
		roomMap = new HashMap<>();
		players = new ArrayList<>();
		deck = new ArrayList<>();
		weaponCards = new HashMap<>();
		playerCards = new HashMap<>();
		roomCards = new HashMap<>();
		Scanner setupConfigIn;
		if (ClueGame.JAR) {
			InputStream is = getClass().getResourceAsStream(setupConfigFile);
			if (is == null) {
				throw new FileNotFoundException("Could not find Setup");
			}
			setupConfigIn = new Scanner(is);
		}
		else {
			FileReader reader = new FileReader(setupConfigFile);
			setupConfigIn = new Scanner(reader);
		}
		// InputStream is = this.getClass().getResourceAsStream(setupConfigFile);
		while (setupConfigIn.hasNextLine()) {
			String line = setupConfigIn.nextLine();
			if (!line.startsWith(COMMENT_PREFIX)) {
				String gameObjectLabel[] = line.split(FILE_DELIMITER);

				String name = gameObjectLabel[1];
				if (gameObjectLabel[0].equals("Room")) {
					char roomInitial = gameObjectLabel[2].charAt(0);
					roomMap.put(roomInitial, new Room(name));
					roomMap.get(roomInitial).setRoom(true);
					Card card = new Card(name, CardType.ROOM);
					deck.add(card);
					roomCards.put(name, card);
				}
				else if (gameObjectLabel[0].equals("Space")) {
					char roomInitial = gameObjectLabel[2].charAt(0);
					roomMap.put(roomInitial, new Room(name));
					roomMap.get(roomInitial).setRoom(false);
				}
				else if (gameObjectLabel[0].equals("Player")) {
					Player temp;
					String color = gameObjectLabel[2];
					int row = Integer.parseInt(gameObjectLabel[4]);
					int column = Integer.parseInt(gameObjectLabel[5]);
					if (gameObjectLabel[3].equals("Human")) {
						temp = new HumanPlayer(name, color, row, column);
						players.add(temp);
					}
					if (gameObjectLabel[3].equals("Computer")) {
						temp = new ComputerPlayer(name, color, row, column);
						players.add(temp);

					}
					Card card = new Card(name, CardType.PERSON);
					deck.add(card);
					playerCards.put(name, card);
				}
				else if (gameObjectLabel[0].equals("Weapon")) {
					Card card = new Card(name, CardType.WEAPON);
					deck.add(card);
					weaponCards.put(name, card);
				}
				else {
					throw new BadConfigFormatException();
				}
			}
		}
	}

	/*
	 * Check for valid adjacent cells and add to cells adjList
	 */
	private void setupAdjList(BoardCell cell, int row, int col) {
		if (cell.isDoorway()) {
			setupAdjListDoorway(cell, row, col);
		}
		else if (cell.isWalkway()) {
			setupAdjListWalkWay(cell, row, col);
		}
		else if (cell.isRoomCenter()) {
			setupAdjListRoomCenter(cell);
		}
		else {
			setupAdjListDefault(cell, row, col);
		}
	}

	/*
	 * Default catch case for cells that are not in handled directly
	 */
	private void setupAdjListDefault(BoardCell cell, int row, int col) {
		if ((row - 1) >= 0) {
			cell.addAdj(getCell(row - 1, col));
		}
		if ((row + 1) < numRows) {
			cell.addAdj(getCell(row + 1, col));
		}
		if ((col - 1) >= 0) {
			cell.addAdj(getCell(row, col - 1));
		}
		if ((col + 1) < numColumns) {
			cell.addAdj(getCell(row, col + 1));
		}
	}

	/**
	 * Setup adjList for room Centers
	 * 
	 * @param The cell belonging to the center of the room
	 * 
	 *            Adds the following adjacencies to the roomCenter cell: Doors that
	 *            grant access to the room, Room centers that are connected via
	 *            secret passage
	 */
	private void setupAdjListRoomCenter(BoardCell roomCenterCell) {
		// Get entrances from Room object and add to adjList
		for (BoardCell door : getRoom(roomCenterCell).getDoors()) {
			roomCenterCell.addAdj(door);
		}

		// Get the room center that is reachable via secret passage
		if (getRoom(roomCenterCell).hasSecretPassage()) {
			BoardCell secretPassage = getRoom(roomCenterCell).getSecretPassageCell();
			char connectedRoomInitial = secretPassage.getSecretPassage();
			roomCenterCell.addAdj(getRoom(connectedRoomInitial).getCenterCell());
		}
	}

	/**
	 * Setup adjList for door Cells
	 * 
	 * @param doorCell The cell belonging to the doorway
	 * @param row      doorCell's row in the board
	 * @param col      doorCell's column in the board
	 * 
	 *                 Adds the following cells to the Doors adjList: adjacent
	 *                 walkways, and connected room center cells
	 */
	private void setupAdjListDoorway(BoardCell doorCell, int row, int col) {
		// A door is a special walkway, get adjacent walkways and add to list
		setupAdjListWalkWay(doorCell, row, col);

		// Handle room centers
		switch (doorCell.getDoorDirection()) {
		case UP:
			doorCell.addAdj(getRoom(getCell(row - 1, col)).getCenterCell());
			break;
		case DOWN:
			doorCell.addAdj(getRoom(getCell(row + 1, col)).getCenterCell());
			break;
		case LEFT:
			doorCell.addAdj(getRoom(getCell(row, col - 1)).getCenterCell());
			break;
		case RIGHT:
			doorCell.addAdj(getRoom(getCell(row, col + 1)).getCenterCell());
			break;
		default:
			break;
		}
	}

	/**
	 * Setup adjList for door Cells
	 * 
	 * walkwayCell The cell belonging to the doorway
	 * 
	 * @param row           walkwayCell's row in the board
	 * @param walkwayCell's column in the board
	 * 
	 *                      Adds the following cells to the walkway: adjacent
	 *                      walkways
	 */
	private void setupAdjListWalkWay(BoardCell walkwayCell, int row, int col) {
		if ((row - 1) >= 0 && getCell(row - 1, col).isWalkway()) {
			walkwayCell.addAdj(getCell(row - 1, col));
		}
		if ((row + 1) < numRows && getCell(row + 1, col).isWalkway()) {
			walkwayCell.addAdj(getCell(row + 1, col));
		}
		if ((col - 1) >= 0 && getCell(row, col - 1).isWalkway()) {
			walkwayCell.addAdj(getCell(row, col - 1));
		}
		if ((col + 1) < numColumns && getCell(row, col + 1).isWalkway()) {
			walkwayCell.addAdj(getCell(row, col + 1));
		}
	}

	/**
	 * Determine reachable cells based on start cell, pathlength, and cell types
	 * 
	 * @param startCell
	 * @param pathLength Max travel distance
	 */
	public void calcTargets(BoardCell startCell, int pathLength) {
		targets = new HashSet<>();
		visited = new HashSet<>();
		visited.add(startCell);
		findAllTargets(startCell, pathLength);
	}

	/**
	 * Determine whether cell is a target or intermediate cell
	 * 
	 * @param thisCell
	 * @param numSteps
	 * 
	 *                 Checks reachable cells. If cell isRoom or unoccupied add to
	 *                 list else check others
	 */
	private void findAllTargets(BoardCell thisCell, int numSteps) {
		for (BoardCell adjCell : thisCell.getAdjList()) {
			if (!visited.contains(adjCell)) {
				visited.add(adjCell);
				if ((numSteps == 1 && !adjCell.isOccupied()) || adjCell.isRoom()) {
					targets.add(adjCell);
				}
				else if (!adjCell.isRoom() && !adjCell.isOccupied()) {
					findAllTargets(adjCell, numSteps - 1);
				}
				visited.remove(adjCell);
			}
		}

	}

	/**
	 * Checks if an array of config cells has a room that is not in the legend
	 * 
	 * @row The row of config cell strings to check
	 */
	private Boolean areRoomsInLegend(String[] rowOfCells) {
		for (String cell : rowOfCells) {
			if (!roomMap.containsKey(cell.charAt(0))) {
				return false;
			}
		}
		return true;
	}

	/*
	 * Method that set appropriate cell properties based on the second char
	 */
	private void setCellParameters(int row, int col, char settingChar) throws BadConfigFormatException {
		BoardCell currentCell = getCell(row, col);
		switch (settingChar) {
		case ROOM_CENTER:
			currentCell.setRoomCenter(true);
			getRoom(currentCell).setCenterCell(currentCell);
			break;
		case ROOM_LABEL:
			currentCell.setRoomLabel(true);
			getRoom(currentCell).setLabelCell(currentCell);
			break;
		case DOOR_UP:
			getRoom(getCell(row - 1, col)).addDoor(currentCell);
			currentCell.setDoorDirection(DoorDirection.UP);
			break;
		case DOOR_LEFT:
			getRoom(getCell(row, col - 1)).addDoor(currentCell);
			currentCell.setDoorDirection(DoorDirection.LEFT);
			break;
		case DOOR_RIGHT:
			getRoom(getCell(row, col + 1)).addDoor(currentCell);
			currentCell.setDoorDirection(DoorDirection.RIGHT);
			break;
		case DOOR_DOWN:
			getRoom(getCell(row + 1, col)).addDoor(currentCell);
			currentCell.setDoorDirection(DoorDirection.DOWN);
			break;
		default:
			if (!roomMap.containsKey(settingChar)) {
				throw new BadConfigFormatException("Secret Room Character does not exist");
			}
			getRoom(currentCell).setSecretPassageCell(currentCell);
			currentCell.setSecretPassage(settingChar);
			break;
		}
	}

	/*
	 * Instantiate a board grid and set the cell information based on what is in the
	 * layoutConfig
	 */
	private void initializeGrid(ArrayList<String[]> loadedCells) throws BadConfigFormatException {
		grid = new BoardCell[numRows][numColumns];
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numColumns; col++) {
				char cellInitial = loadedCells.get(row)[col].charAt(0);
				grid[row][col] = new BoardCell(row, col, cellInitial);
				BoardCell currentCell = getCell(row, col);
				if (!getRoom(currentCell).isRoom()) {
					currentCell.setRoom(false);
				}
				else {
					currentCell.setRoom(true);
				}
				if (cellInitial == 'X') {
					currentCell.setUnused(true);
				}
			}
		}

		setExtraCellProperties(loadedCells);

		// Create adjList for each cell
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numColumns; col++) {
				setupAdjList(getCell(row, col), row, col);
			}
		}
	}

	/**
	 * Parses imported cells array and sets properties based on second string
	 * character
	 * 
	 * @param loadedCells
	 * @throws BadConfigFormatException
	 */
	private void setExtraCellProperties(ArrayList<String[]> loadedCells) throws BadConfigFormatException {
		for (int row = 0; row < numRows; row++) {
			for (int col = 0; col < numColumns; col++) {
				if (loadedCells.get(row)[col].length() == 2) {
					char settingChar = loadedCells.get(row)[col].charAt(1);
					setCellParameters(row, col, settingChar);
				}
			}
		}
	}

	/*
	 * Checks if number of cols is valid and that all imported rooms exist
	 */
	private void isLayoutFileValid(ArrayList<String[]> cells) throws BadConfigFormatException {
		for (String[] row : cells) {
			if (row.length != numColumns || !areRoomsInLegend(row)) {
				throw new BadConfigFormatException();
			}
		}
	}

	/*
	 * Loads the data from a layout config file and instantiate the board grid array
	 */
	public void loadLayoutConfig() throws FileNotFoundException, BadConfigFormatException {

		Scanner layoutConfigIn;
		if (ClueGame.JAR) {
			InputStream is = getClass().getResourceAsStream(layoutConfigFile);
			if (is == null) {
				throw new FileNotFoundException("Could not find layout file");
			}
			layoutConfigIn = new Scanner(is);
		}
		else {
			FileReader reader = new FileReader(layoutConfigFile);
			layoutConfigIn = new Scanner(reader);
		}

		ArrayList<String[]> cells = new ArrayList<>();
		while (layoutConfigIn.hasNextLine()) {
			String line = layoutConfigIn.nextLine();
			cells.add(line.split(","));
		}
		numRows = cells.size();
		numColumns = cells.get(0).length;
		isLayoutFileValid(cells);
		initializeGrid(cells);
	}

	// Create Solution and deal cards to players
	public void deal() {
		ArrayList<Card> dealDeck = new ArrayList<>(deck);
		createSolution(dealDeck);

		for (Player player : players) {
			player.setUnseenCards(deck);
		}

		while (dealDeck.size() > 0) {
			for (Player player : players) {
				Card dealCard = dealDeck.get(0);
				dealCard.setCardHolder(player);
				player.updateHand(dealCard);
				dealDeck.remove(0);
			}
		}

	}

	// Create solution from deck of cards and remove cards from deck
	private void createSolution(ArrayList<Card> dealDeck) {
		Card playerSolutionCard = null;
		Card roomSolutionCard = null;
		Card weaponSolutionCard = null;

		for (int i = 0; i < dealDeck.size(); i++) {
			Card card = dealDeck.get(i);
			if (playerSolutionCard == null && card.getCardType().equals(CardType.PERSON)) {
				playerSolutionCard = card;
				dealDeck.remove(i);
			}
			if (roomSolutionCard == null && card.getCardType().equals(CardType.ROOM)) {
				roomSolutionCard = card;
				dealDeck.remove(i);
			}
			if (weaponSolutionCard == null && card.getCardType().equals(CardType.WEAPON)) {
				weaponSolutionCard = card;
				dealDeck.remove(i);
			}
		}
		theAnswer = new Solution(playerSolutionCard, roomSolutionCard, weaponSolutionCard);
	}

	/**
	 * 
	 * @return true if accusation is correct else false
	 */
	public boolean checkAccusation(Card personSolution, Card roomSolution, Card weaponSolution) {
		if (!theAnswer.getPerson().equals(personSolution) || !theAnswer.getRoom().equals(roomSolution)
				|| !theAnswer.getWeapon().equals(weaponSolution)) {
			return false;
		}
		return true;
	}

	/**
	 * @param players list of players to check suggestions with
	 * @return first card that disputes a suggestion
	 */
	public Card handleSuggestion(Player accuser, Card personSug, Card roomSug, Card weaponSug) {
		int startOffset = players.indexOf(accuser) + 1;
		Card disprovingCard;
		for (int i = 0; i < players.size() - 1; i++) {
			disprovingCard = players.get((i + startOffset) % players.size()).disproveSuggestion(personSug, roomSug,
					weaponSug);
			if (disprovingCard != null) {
				return disprovingCard;
			}
		}
		return null;
	}

	/*
	 * Getters and Setters
	 */
	public void setConfigFiles(String layoutConfigFile, String setupConfigFile) {
		this.layoutConfigFile = layoutConfigFile;
		this.setupConfigFile = setupConfigFile;
	}

	public BoardCell getCell(int row, int col) {
		return grid[row][col];
	}

	public int getNumRows() {
		return numRows;
	}

	public int getNumColumns() {
		return numColumns;
	}

	public Room getRoom(BoardCell cell) {
		return roomMap.get(cell.getInitial());
	}

	public Room getRoom(char roomSymbol) {
		return roomMap.get(roomSymbol);
	}

	public Room getRoom(int row, int col) {
		return getRoom(getCell(row, col));
	}

	public int getNumRooms() {
		return roomMap.size();
	}

	public Set<BoardCell> getAdjList(int row, int col) {
		return getCell(row, col).getAdjList();
	}

	public Set<BoardCell> getTargets() {
		return targets;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public ArrayList<Card> getDeck() {
		return deck;
	}

	public Solution getTheAnswer() {
		return theAnswer;
	}

	public int getDiceRoll() {
		return diceRoll;
	}

	public Player getCurrentPlayer() {
		return players.get(currentPlayer);
	}

	public void updateCurrentPlayer() {
		currentPlayer = (currentPlayer + 1) % players.size();
	}

	public boolean isPlayerFinished() {
		return isPlayerFinished;
	}

	public void setPlayerFinished(boolean isPlayerFinished) {
		this.isPlayerFinished = isPlayerFinished;
	}

	public Solution getCurrentSuggestion() {
		return currentSuggestion;
	}

	public Card getCurrentDisprove() {
		return currentDisprove;
	}

	public void setGameControl(GameControlPanel gameControl) {
		this.gameControlPanel = gameControl;
	}

	/*
	 * Testing Getters and Setters
	 */

	public void setTheAnswer(Solution answer) {
		theAnswer = answer;
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}

	public void setDeck(ArrayList<Card> deck) {
		this.deck = deck;
	}

}
