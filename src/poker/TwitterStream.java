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

/**
 * Represents a Twitter conversation between the application and a user. Contains attributes
 * for the {@link Twitter} class, the {@link #mostRecent most recent} {@link Status} sent
 * in the conversation, the {@link User} in this conversation and two {@link String strings}
 * containing the {@link #toSend message to send} in the next tweet, and a {@link #tweetID tweet ID}
 * to prevent duplicate statuses.
 * @author Dara Callinan
 * @author Jazheel Luna
 * @author Eoghan O'Donnell
 * @author Crischelle Pana
 */
public class TwitterStream {
	
	private static final int CHAR_LIMIT = 133;
	private static final int NUM_TWEETS_TO_CHECK = 20;
	private static final int BASE_TWEET_DELAY = 15;		//Necessary to avoid rate limiting
	private static final int RANDOM_TWEET_DELAY = 5;
	private static final double TIME_OUT_SECONDS = 600;	//Time out user if takes too long to reply
	
	private Twitter twitter;
	protected User user;
	private Status mostRecent;
	private String toSend;
	private String streamID;
	private String tweetID;
	
	/**
	 * Class constructor. Initialises the {@link #twitter twitter}, {@link #mostRecent most recent status}, and
	 * {@link #user user} attributes. Sets the {@link #toSend} string to an empty string, and gets a starting
	 * {@link #tweetID tweet ID}.
	 * @param twitter   The {@link Twitter} object to be used.
	 * @param status   The initial {@link Status}.
	 * @param user   The {@link User} in the conversation.
	 * @param streamID   The two character ID of the conversation.
	 */
	TwitterStream(Twitter twitter, Status status, User user, int streamID){
		this.twitter = twitter;
		this.user = user;
		mostRecent = status;
		toSend = "";
		this.streamID = String.format("%02X", streamID);
		if(streamID>255) this.streamID = this.streamID.substring(1, this.streamID.length());
		tweetID = "00";
	}
	
	/**
	 * Adds a {@link String} to the current output {@link #toSend message} that has not yet been sent. If adding the
	 * string would make the tweet too long, {@link #sendTweet} is called, and the current {@link #toSend message} is
	 * emptied. The string is then added to the message. If the initial string exceeds the {@link #CHAR_LIMIT character limit}
	 * then the string will be broken up into multiple tweets. 
	 * @param string   The {@link String} to be added to the current output message.
	 * @see #sendTweet(String)
	 */
	public void addToTweet(String string){
		if(string.length()>CHAR_LIMIT) string = string.substring(0, CHAR_LIMIT);
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
	
	/**
	 * Sends whatever the {@link #toSend current message} is, as long as it is not empty.
	 */
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
		int id = Integer.parseInt(tweetID, 16);
		id++;
		if(id>255) id = 0;
		tweetID = String.format("%02X", id);
	}
	
	/**
	 * Sends a tweet containing an {@link #tweetID identifier} followed by a {@link String}.
	 * @param str   The string to be sent.
	 * @throws TwitterException   If API request is denied.
	 * @throws InterruptedException   If thread is interrupted.
	 */
	private synchronized void sendTweet(String str) throws TwitterException, InterruptedException {
    	StatusUpdate statusUpdate = new StatusUpdate("[" + streamID + tweetID + "] " + str);
    	updateTweetID();
    	//statusUpdate.inReplyToStatusId(mostRecent.getId());	//Replies potentially causing api restriction?
    	mostRecent = tweetStatus(twitter, statusUpdate);
	}
	
	/**
	 * Gets the most recent {@link Status statuses} from the {@link #user}, and returns one if it
	 * is in reply to the {@link #mostRecent most recent status} in the twitter stream.
	 * @return The user's {@link Status status} replying to the {@link #mostRecent most recent status} if one exists.
	 * @throws TwitterException   If the API request is denied.
	 * @throws InterruptedException   If the thread is interrupted.
	 */
	private Status getRecentReply() throws TwitterException, InterruptedException{
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
	
	/**
	 * Continuously checks for a reply to the {@link #mostRecent latest tweet}, by the {@link #user} and
	 * returns a {@link String} of the text of the reply status if it finds one.
	 * @return The text of a reply by the {@link #user} to the {@link #mostRecent latest tweet}.
	 * @throws TwitterException   If the API request is denied.
	 * @throws InterruptedException   If the thread is interrupted.
	 */
	public String parseResponse() throws TwitterException, InterruptedException{
		Status response = null;
		double counter = 0;
		while(response == null){
			response = getRecentReply();
			counter += BASE_TWEET_DELAY + (RANDOM_TWEET_DELAY/2);
			if(counter>TIME_OUT_SECONDS) return "leave";
		}
		return response.getText();
	}
	
	/**
	 * Tweets a {@link StatusUpdate status update}. Returns the newly made {@link Status status}.
	 * @param tw   The {@link Twitter} object used by this stream.
	 * @param s   The {@link StatusUpdate} object to be updated.
	 * @return The new {@link Status status}.
	 * @throws TwitterException   If the API request is denied.
	 * @throws InterruptedException   If the thread is interrupted.
	 */
	private static synchronized Status tweetStatus(Twitter tw, StatusUpdate s) throws TwitterException, InterruptedException{
		System.out.println("Tweeting");
		Status newStatus = tw.updateStatus(s);
		Random rand = new Random();
		int delay = BASE_TWEET_DELAY + rand.nextInt(RANDOM_TWEET_DELAY);
		Thread.sleep(delay*1000);
		return newStatus;
	}
	
	/**
	 * Gets a certain number of newest tweets from a user's timeline.
	 * @param tw   The {@link Twitter} object used by this stream.
	 * @param user   The {@link User user} whose timeline will be retrieved.
	 * @param tweetsToCheck   The number of {@link Status statuses} to get.
	 * @return A {@link ResponseList list} of {@link Status statuses} made by the user.
	 * @throws TwitterException   If the API request is denied.
	 * @throws InterruptedException   If the thread is interrupted.
	 */
	private static synchronized ResponseList<Status> getTimeline(Twitter tw, String user, int tweetsToCheck) throws TwitterException, InterruptedException{
		System.out.println("Getting timeline");
		Paging paging = new Paging(1, tweetsToCheck);
		Random rand = new Random();
		int delay = BASE_TWEET_DELAY + rand.nextInt(RANDOM_TWEET_DELAY);
		Thread.sleep(delay*1000);
		return tw.getUserTimeline(user, paging);
	}
	
	public static void main(String[] args) {
		TwitterStream s = new TwitterStream(null, null, null, 40);
		for(int i=0; i<260; i++){
			s.updateTweetID();
			System.out.println(s.streamID+s.tweetID);
		}
	}
}
