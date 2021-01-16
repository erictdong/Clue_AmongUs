/**
 * @author Alexander Cieslewicz
 * @author Eric Dong
 * 
 * Human player class for game
 */
package clueGame;

import java.awt.Color;

public class HumanPlayer extends Player {

	public HumanPlayer(String name, Color color, int row, int column) {
		super(name, color, row, column);
		isHuman = true;
	}

	public HumanPlayer(String name, String color, int row, int column) {
		super(name, color, row, column);
		isHuman = true;
	}

}
