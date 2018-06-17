import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/* 
 * Class containing methods for taking the command string (a string with a digit 1-6)
 * received from the server and converting that into its respective Linux shell command, 
 * then executing it and returning the results.
 */
public class CommandExecutor {

	/**
	 * Runs the shell command after first using parseCommand() to determine which
	 * command to run.
	 * 
	 * @param commandString		A string containing a single digit, 1-6;
	 * @return			A string containing the results of the shell command.
	 */
	static String run(String commandString) {
		String result = "";
		String line;
		try {
			// start the shell command running as a child processes
			Process child = Runtime.getRuntime().exec(commandString);

			// open a BufferedReader to read the output of the child process
			BufferedReader output = new BufferedReader(new InputStreamReader(child.getInputStream()));
			// while the child process is still outputting, add the output to the result string
			while ((line = output.readLine()) != null) {
				result = result.concat(line);
				result = result.concat("\n");
			}

			result = result.concat("\n");
			// add "END_MESSAGE" to the result string. When the client sees END_MESSAGE it
			// will know that the server is done sending
			result = result.concat("END_MESSAGE");
			output.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

}
