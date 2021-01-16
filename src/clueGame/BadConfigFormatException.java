/**
 * @author Alexander Cieslewicz
 * @author Eric Dong
 * 
 * Custom error that is thrown if the config is not valid
 */
package clueGame;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

@SuppressWarnings("serial")
public class BadConfigFormatException extends Exception {
	public BadConfigFormatException() {
		super("Error Reading Game File Configuration: BadConfig");
		writeErrorToLogFile();
	}

	public BadConfigFormatException(String message) {
		super(message);
		writeErrorToLogFile();
	}

	private void writeErrorToLogFile() {
		try {
			FileWriter logFileWriter = new FileWriter("ClueSetup.log", true);
			PrintWriter logFile = new PrintWriter(logFileWriter);
			logFile.println(this.getMessage());
			logFile.close();
		}
		catch (IOException e) {
			System.out.println("Failed to open log file.");
		}
	}
}
