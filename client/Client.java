import java.util.*;
import java.io.*;
import java.util.concurrent.atomic.AtomicLong;
/**
 * @author Ikonija Bogojevic
 * @author Sandra Weber
 * @author Miguel Perez
 *
 * This is the main Client class, which spawns ClientThreads which connect to the server. This class
 * includes methods for obtaining the user's commands and validating them.
 */
public class Client {
	private static String hostName;

	// create a variable to initialize new threads with
	private static Thread thrd = null;

	// the threads are kept track of with a linked list
	private static LinkedList<Thread> list = new LinkedList<Thread>();

	// AtomicLong is a class that is synchronized, and can be used across
	// multiple threads. Here it is used for benchmarking, to store the sum
	// of the command completion times for all threads
	private static AtomicLong totalTime = new AtomicLong(0);

	// this AtomicLong is used to keep track of the current # of running threads
	private static AtomicLong runningThreads = new AtomicLong(0);
	private static boolean printOutput = true;

	public static void main(String[] args) {
		String menuSelection = "0";
		int numProcesses = 1;
		// if no hostname is provided, quit
		if (args.length == 0) {
			System.out.println("User did not enter a host name. Client program exiting.");
			System.exit(1);
		}

		// until the user selects 8, the Exit option, keep looping and
		// offering the menu again after running the queries to the server
		else while (menuSelection != "exit") {
			// display the menu and get the user's choice
			menuSelection = mainMenu();

			// if 8, exit program
			if (menuSelection == "exit") {
				System.out.println("Quitting.");
				System.exit(0);
			}

			// create threads. since numProcesses is initialized to 1 and gets reset
			// at the end of this loop, if the user has not selected to benchmark 
			// a command, this loop  will only create one process
			totalTime.set(0);
			runningThreads.set(numProcesses);
			for (int i = 0; i < numProcesses; i++) {
				// make a new thread, tell it the hostname to connect to
				// and the command to run. It is also passed the totalTime object,
				// so it can record how much time its command took to complete
				thrd = new Thread(new ClientThread(args[0], menuSelection, totalTime, printOutput, runningThreads));
				thrd.start(); // start the thread
				list.add(thrd); // add the thread to the end of the linked list

			}

			// wait for all of the threads to complete before going to the top
			// of the loop again. This ensures that all threads complete before the
			// menu is shown again
			for (int i = 0; i < numProcesses; i++) {
				try {
					// wait for the thread to finish
					list.get(i).join();
				} catch (InterruptedException e) {
					// if the join interrupts the thread, print an error
					e.printStackTrace();
				}
			}
			// while runningThreads is not 0, there are still clients waiting for the server
			// to send a response, so keep looping (waiting) until they are finished
			while (runningThreads.get() != 0) {}

			System.out.println("Average response time: " + (totalTime.get() / numProcesses) + " ms\n");
			numProcesses = 1;
			printOutput = true;
		}

	}
	//----------------------------------------------------------------------------
	/**
	* Function to prompt the user for a command to run
	* @return command number 1-8
	*/
	public static String mainMenu() {
		String menuSelection = "";

		System.out.print("Prelude> ");
		Scanner sc = new Scanner(System.in);
		if (sc.hasNext()) menuSelection = sc.next();
		return menuSelection;
	}

}
