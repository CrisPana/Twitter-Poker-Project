
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
import java.util.Date;
import java.util.List;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

public class JDECPokerBot {

	static private final int BASE_TWEET_DELAY = 15;		//Necessary to avoid rate limiting
	static private final int BASE_SCAN_DELAY = 30;		//Search for game every minute
	
	//access the twitter API using your twitter4j.properties file
	Date searchBegin;
	Twitter twitter;
	List<Status> games;
	
	public JDECPokerBot() throws TwitterException, FileNotFoundException, IOException {
		searchBegin = new Date();

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
    public int searchForGame() throws TwitterException, InterruptedException {
		games = null;
		//Until a game is found
    	while(games == null || games.size()<1){
			Query query = new Query("#PlayPokerWithJDEC");
	    	QueryResult result = twitter.search(query);
	    	games = result.getTweets();
	    	//Remove old tweets
	    	for(int i=0; i<games.size(); i++){
	    		Date created = games.get(i).getCreatedAt();
	    		System.out.println(games.size() + " - " + i);
	    		System.out.println(created);
	    		System.out.println(searchBegin);
	    		if(created.before(searchBegin)){
	    			games.remove(i);
	    			i = i-1;
	    		}
	    	}
	    	if(games.size()<1) Thread.sleep(BASE_SCAN_DELAY*1000);
    	}
    	return games.size();
    }
    
    public String getHumanPlayer() {
    	Status tweetResult = games.get(0);
    	return tweetResult.getUser().getScreenName();
    }
    
    public int playGame(Status game) throws TwitterException, InterruptedException {
    	//StatusUpdate statusUpdate = new StatusUpdate("Starting game with " + game.getUser().getScreenName());
    	//statusUpdate.inReplyToStatusId(tweetResult.getId());
    	//Status s = twitter.updateStatus(statusUpdate);
    	
    	TwitterStream stream = new TwitterStream(twitter, game, game.getUser());
    	
    	Thread.sleep(BASE_TWEET_DELAY*1000);
    	
    	GameOfPoker pokerGame = null;
		try {
			pokerGame = new GameOfPoker(stream, 5);
		} catch (FileNotFoundException e) {
			System.out.println("botnames.txt is missing or cannot be read");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(pokerGame!=null){
			System.out.println("Starting game...");
			pokerGame.startGame();
		} else {
			System.out.println("Could not start game with status");
		}
    	

    	return 0;
    }
    
    public Status getReply(Status s) throws TwitterException{
    	Status r = twitter.showStatus(s.getInReplyToStatusId());
    	return r;
    }
	
   //if something goes wrong, we might see a TwitterException
    public static void main(String... args) throws TwitterException, InterruptedException, FileNotFoundException, IOException{

    	JDECPokerBot bot = new JDECPokerBot();
    	while(true){
    		System.out.println("Searching for new game");
    		bot.searchForGame();
	    	bot.playGame(bot.games.get(0));
	    	bot.searchBegin = new Date();
    	}
    }

}
