
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

public class JDECPokerBot {

	//access the twitter API using your twitter4j.properties file
	Twitter twitter;
	List<Status> games;
	
	public JDECPokerBot() throws TwitterException, FileNotFoundException, IOException {	

		//getting keys from file
		String[] keys = new String[4];
		int i = 0;
		File file = new File("keys.txt");
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while((line = br.readLine()) != null){
				keys[i] = line;
				i++;
			}	
		}
		
		//configuration and authentication
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true);
		cb.setOAuthConsumerKey(keys[0]);
		cb.setOAuthConsumerSecret(keys[1]);
		cb.setOAuthAccessToken(keys[2]);
		cb.setOAuthAccessTokenSecret(keys[3]);
		
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();
	}
	
    //search for a game
    public int searchForGame() throws TwitterException {
    	
    	Query query = new Query("#PlayPokerWithJDEC");
    	QueryResult result = twitter.search(query);
    	games = result.getTweets();
    	
    	return games.size();
    }
    
    public String getHumanPlayer() {
    	Status tweetResult = games.get(0);
    	return tweetResult.getUser().getScreenName();
    }
    
    public int replyToTweet() throws TwitterException, InterruptedException {
    	//testing for now
    	Status tweetResult = games.get(0);
    	StatusUpdate statusUpdate = new StatusUpdate("@" + tweetResult.getUser().getScreenName() + " Starting poker game with JDEC");
    	
    	statusUpdate.inReplyToStatusId(tweetResult.getId());
    	twitter.updateStatus(statusUpdate); 

    	//Thread.sleep(5*60*1000);
    	return 0;
    }
	
   //if something goes wrong, we might see a TwitterException
    public static void main(String... args) throws TwitterException, InterruptedException, FileNotFoundException, IOException{

//        //a list of searches to look for in twitter
//        List<String> searches = new ArrayList<>();
//        searches.add("\"love live\"");
//        searches.add("\"lovelive\"");
//
//        //a list of replies 
//        List<String> replies = new ArrayList<>();
//        replies.add(" Nico is best girl!");
//
//        //keeps tweeting
//        while(true){
//
//            //this creates a new search and it chooses randomly in the list of searches
//            Query query = new Query(searches.get((int)(searches.size()*Math.random())));
//
//            //get the results from that search
//            QueryResult result = twitter.search(query);
//
//            //get the first tweet from those results
//            Status tweetResult = result.getTweets().get(0);
//
//            //reply to that tweet, choose from random replies
//            StatusUpdate statusUpdate = new StatusUpdate(".@" + tweetResult.getUser().getScreenName() + replies.get((int)(replies.size()*Math.random())));
//            statusUpdate.inReplyToStatusId(tweetResult.getId());
//            Status status = twitter.updateStatus(statusUpdate); 
//
//            System.out.println("Sleeping.");
//
//            //this sends tweets every 5 minutes
//            Thread.sleep(5*60*1000);
//        }

    	//testing purposes - temp
    	JDECPokerBot newGame = new JDECPokerBot();
    	
    	if (newGame.searchForGame() > 0){
    		newGame.replyToTweet();
    		System.out.println("Found game.");
    	} else {
    		System.out.println("No games found.");
    	}
    	
    }

}
