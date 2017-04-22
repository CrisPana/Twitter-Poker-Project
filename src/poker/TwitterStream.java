/*
 * Software Engineering III - Twitter Poker Project
 * Team Name : JDEC
 * Team Members:
 * 		Dara Callinan 		14500717
 * 		Jazheel Luna		14486752
 * 		Eoghan O'Donnell	14464082
 * 		Crischelle Pana 	14366596
 * 
 * © 2017 
 * */

package poker;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class TwitterStream {
	
	static public final int CHAR_LIMIT = 140;
	
	Twitter twitter;
	Status mostRecent;
	String toSend;
	
	TwitterStream(Twitter twit, Status status){
		twitter = twit;
		mostRecent = status;
		toSend = "";
	}
	
	//Add string to current message, send current message if tweet would become too long
	public void addToTweet(String string){
		if(string.length()>140) string = string.substring(0, CHAR_LIMIT);
		String temp = toSend + string;
		if(temp.length() > CHAR_LIMIT){
			try {
				sendTweet(toSend);
			} catch (TwitterException | InterruptedException e) {
				System.out.println(e.getMessage());
			}
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
		if(toSend.length() > 0){
			try {
				sendTweet(toSend);
			} catch (TwitterException | InterruptedException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	//Send tweet
	public synchronized void sendTweet(String str) throws TwitterException, InterruptedException {
    	StatusUpdate statusUpdate = new StatusUpdate(str);
    	statusUpdate.inReplyToStatusId(mostRecent.getId());
    	mostRecent = twitter.updateStatus(statusUpdate); 
    	Thread.sleep(20*1000);
	}
	
	public void parseResponse(){
		//TO DO
	}
}
