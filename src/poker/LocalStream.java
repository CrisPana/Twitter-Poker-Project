package poker;

import java.util.Scanner;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.User;

/**
 * A subclass of {@link TwitterStream} but replaces the twitter input and output with console
 * input and output by overriding the {@link #parseResponse()} and {@link #addToTweet(String)}
 * methods. This class can be used for testing the program as there is no use of the Twitter API
 * and as such, no need to wait for the API rate limits.
 * @author Dara Callinan
 * @author Jazheel Luna
 * @author Eoghan O'Donnell
 * @author Crischelle Pana
 */
public class LocalStream extends TwitterStream {
	
	static private final int CHAR_LIMIT = 134;
	static private final int BASE_TWEET_DELAY = 1;
	
	private String toSend;

	/**
	 * Class constructor. Calls the {@link TwitterStream} {@link TwitterStream#TwitterStream constructor}
	 * and initialises {@link #toSend}.
	 * @param twit   The {@link Twitter} object to be used.
	 * @param status   The initial {@link Status}.
	 * @param twitterUser   The {@link User} in the conversation.
	 */
	LocalStream(Twitter twit, Status status, User twitterUser) {
		super(twit, status, twitterUser, 0);
		toSend = "";
	}

	/**
	 * Prints a tweet to the console, starting with an identifying string to distinguish
	 * separate tweets.
	 * @param str   The tweet to be printed.
	 */
	private static synchronized void sendTweet(String str) {
    	System.out.println("TWEET " + str);
    	try {
			Thread.sleep(BASE_TWEET_DELAY*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void addToTweet(String string){
		if(string.length()>140) string = string.substring(0, CHAR_LIMIT);
		String temp = toSend + string;
		if(temp.length() > CHAR_LIMIT){
			sendTweet(toSend);
			//Remaining string too long
			if(string.length()>140){
				toSend = string.substring(0, CHAR_LIMIT);
				addToTweet(string.substring(CHAR_LIMIT));
			}
			toSend = string;
		} else {
			toSend = temp;
		}
	}
	
	@Override
	public void completeMessage(){
		sendTweet(toSend);
		toSend = "";
	}
	
	/**
	 * Reads input from the console.
	 * @return The {@link String} from the console.
	 */
	private static synchronized String readInput(){
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		String input = scanner.nextLine();
		return input;
	}
	
	@Override
	public String parseResponse(){
		String input = readInput();
		try {
			Thread.sleep(BASE_TWEET_DELAY*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return input;
	}
}
