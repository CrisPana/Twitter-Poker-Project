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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

public class JDECPokerBot {

	static private final int BASE_TWEET_DELAY = 15;		//Necessary to avoid rate limiting
	static private final int BASE_SCAN_DELAY = 30;		//Search for game every minute
	static private final int MAX_GAMES = 3;				//Maximum number of simultaneous games
	
	//access the twitter API using your twitter4j.properties file
	Date searchBegin;
	Twitter twitter;
	List<Status> games;
	List<String> playing;
	GameOfPoker[] activeGames;
	
	public JDECPokerBot() throws TwitterException, FileNotFoundException, IOException {
		searchBegin = new Date();
		playing = new ArrayList<String>();
		activeGames = new GameOfPoker[MAX_GAMES];

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
	    	//Remove tweets
	    	removeUnplayableTweets();
	    	if(games.size()<1) Thread.sleep(BASE_SCAN_DELAY*1000);
    	}
    	return games.size();
    }
    
    //Removes old tweets, duplicate tweets and tweets by active players
    private void removeUnplayableTweets(){
    	for(int i=0; i<games.size(); i++){
    		Status game = games.get(i);
    		boolean removed = false;
    		//Remove tweets by same user
    		for(int j=0; j<i; j++){
    			if(games.get(j).getUser()==games.get(i).getUser()){
    				games.remove(i);
    				i = i-1;
    				removed = true;
    			}
    		}
    		if(removed) continue;
    		//Remove tweets by user who is currently in a game
    		for(int j=0; j<playing.size(); j++){
    			if(playing.get(j)!=null && playing.get(j).equals(game.getUser().getScreenName())){
    				games.remove(i);
    				i = i-1;
    				removed = true;
    			}
    		}
    		if(removed) continue;
    		Date created = games.get(i).getCreatedAt();
    		if(created.before(searchBegin)){
    			//games.remove(i);
    			//i = i-1;
    		}
    	}
    }
    
    public String getHumanPlayer() {
    	Status tweetResult = games.get(0);
    	return tweetResult.getUser().getScreenName();
    }
    
    public GameOfPoker playGame(String threadName, Status game) throws TwitterException, InterruptedException {
    	
    	//** Use twitter stream for twitter input/output or local stream for console input/output
    	//TwitterStream stream = new TwitterStream(twitter, game, game.getUser());
    	LocalStream stream = new LocalStream(twitter, game, game.getUser());
        
    	//Thread.sleep(BASE_TWEET_DELAY*1000);
    	GameOfPoker pokerGame = null;
		try {
			pokerGame = new GameOfPoker(threadName, stream, 5);
		} catch (FileNotFoundException e) {
			System.out.println("botnames.txt is missing or cannot be read");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(pokerGame!=null){
			System.out.println("Starting game...");
			playing.add(stream.user.getScreenName());
			return pokerGame;
		} else {
			System.out.println("Could not start game with status");
			return null;
		}
    }
    
    public Status getReply(Status s) throws TwitterException{
    	Status r = twitter.showStatus(s.getInReplyToStatusId());
    	return r;
    }
	
   //if something goes wrong, we might see a TwitterException
    public static void main(String... args) throws TwitterException, InterruptedException, FileNotFoundException, IOException{

    	JDECPokerBot bot = new JDECPokerBot();
    	
    	boolean singleGame = true;
    	while(singleGame){
    		System.out.println("Searching for new game");
    		bot.searchForGame();
	    	bot.playGame("Thread 1", bot.games.get(0));
	    	bot.searchBegin = new Date();
    	}
    	
    	
    	while(true){
    		System.out.println("Searching for new game");
    		bot.searchForGame();
    		for(int i=0; i<bot.games.size(); i++){
    			System.out.println(bot.games.get(i).getUser().getScreenName());
    		}
    		//Set finished games to null
    		for(int i=0; i<MAX_GAMES; i++){
    			if(bot.activeGames[i]!=null && bot.activeGames[i].getState()==Thread.State.TERMINATED){
    				bot.playing.remove(bot.activeGames[i].human.player_name);
    				bot.activeGames[i] = null;
    			}
    		}
    		//Create and run games
    		for(int i=0; i<MAX_GAMES; i++){
    			if(bot.activeGames[i]==null && bot.games.size()>0){
    				String threadName = "Thread " + (i+1);
    				Status game = bot.games.get(0);
    				//bot.playing[i] = game.getUser().getScreenName();
    				bot.activeGames[i] = bot.playGame(threadName, game);
    				bot.games.remove(0);
    				bot.activeGames[i].start();
    			}
    		}
    		
	    	Thread.sleep(20*1000);
    	}
    }

}
