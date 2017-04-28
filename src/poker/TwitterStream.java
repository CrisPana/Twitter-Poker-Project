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

import java.util.ArrayList;
import java.util.Random;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

public class TwitterStream {
	
	static private final int CHAR_LIMIT = 134;
	static private final int NUM_TWEETS_TO_CHECK = 20;
	static private final int BASE_TWEET_DELAY = 10;		//Necessary to avoid rate limiting
	static private final int RANDOM_TWEET_DELAY = 5;
	
	private Twitter twitter;
	public User user;
	private Status mostRecent;
	private String toSend;
	private String tweetID;
	
	TwitterStream(Twitter twit, Status status, User twitterUser){
		twitter = twit;
		user = twitterUser;
		mostRecent = status;
		toSend = "";
		tweetID = "00";
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
				toSend = "";
			} catch (TwitterException | InterruptedException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	//TweetIdentifier to avoid duplicates
	private void updateTweetID(){
		int id = Integer.parseInt(tweetID);
		id++;
		tweetID = String.format("%02d", id);
	}
	
	//Send a tweet given a string
	private synchronized void sendTweet(String str) throws TwitterException, InterruptedException {
    	StatusUpdate statusUpdate = new StatusUpdate("[" + tweetID + "] " + str);
    	updateTweetID();
    	//statusUpdate.inReplyToStatusId(mostRecent.getId());	//Replies potentially causing api restriction?
    	mostRecent = tweetStatus(twitter, statusUpdate);
	}
	
	//Get the most recent status from user that is a reply to latest poker tweet (if it exists)
	//Limited to 20 tweets
	public Status getRecentReply() throws TwitterException, InterruptedException{
		//List of statuses
		ArrayList<Status> statuses = new ArrayList<Status>();
		//Get all statuses for user
		String u = user.getScreenName();
		statuses.addAll(getTimeline(twitter, u, NUM_TWEETS_TO_CHECK));
		//Check if any status is a reply to latest tweet
		for(int i = 0; i<statuses.size(); i++){
			if(statuses.get(i).getInReplyToStatusId()==mostRecent.getId()){
				mostRecent = statuses.get(i);
				return statuses.get(i);
			}
		}
		return null;
	}
	
	//Return a string of most recent response to latest tweet
	public String parseResponse() throws TwitterException, InterruptedException{
		Status response = null;
		while(response == null){
			response = getRecentReply();
		}
		return response.getText();
	}
	
	//Tweet a status
	private static synchronized Status tweetStatus(Twitter tw, StatusUpdate s) throws TwitterException, InterruptedException{
		System.out.println("tweeting");
		Status newStatus = tw.updateStatus(s);
		Random rand = new Random();
		int delay = BASE_TWEET_DELAY + rand.nextInt(RANDOM_TWEET_DELAY);
		Thread.sleep(delay*1000);
		return newStatus;
	}
	
	//Get a user's timeline
	private static synchronized ResponseList<Status> getTimeline(Twitter tw, String user, int tweetsToCheck) throws TwitterException, InterruptedException{
		System.out.println("Getting timeline");
		Paging paging = new Paging(1, tweetsToCheck);
		Random rand = new Random();
		int delay = BASE_TWEET_DELAY + rand.nextInt(RANDOM_TWEET_DELAY);
		Thread.sleep(delay*1000);
		return tw.getUserTimeline(user, paging);
	}
}
