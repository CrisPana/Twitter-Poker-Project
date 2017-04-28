package poker;

import java.util.Scanner;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.User;

public class LocalStream extends TwitterStream {
	
	static private final int CHAR_LIMIT = 134;
	
	private String toSend;

	LocalStream(Twitter twit, Status status, User twitterUser) {
		super(twit, status, twitterUser);
		// TODO Auto-generated constructor stub
	}

	private synchronized void sendTweet(String str) {
    	System.out.println(str);
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
	
	//Force current string to send
	public void completeMessage(){
		sendTweet(toSend);
		toSend = "";
	}
	
	
	
	@Override
	public String parseResponse(){
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		String input = scanner.nextLine();
		return input;
	}
	
	public static void main(String[] args) {
		LocalStream l = new LocalStream(null, null, null);
		l.parseResponse();
	}
}
