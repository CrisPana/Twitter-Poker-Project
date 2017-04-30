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

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;


/**
 * A poker bot which searches for Twitter statuses using the Twitter API, and creates playable 
 * {@link GameOfPoker} instances.
 * 
 * @author Dara Callinan
 * @author Jazheel Luna
 * @author Eoghan O'Donnell
 * @author Crischelle Pana
 */
public class JDECPokerBot {
	
	static private final int BASE_SCAN_DELAY = 30;		//Search for game every minute
	static private final int MAX_GAMES = 2;				//Maximum number of simultaneous games
	
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
	
	/**
     * Searches for statuses with the 'join game' string.
	 * 
	 * @return <code>int</code> - the amount of games found
	 * @throws TwitterException
	 * @throws InterruptedException
	 */
    public int searchForGame() throws TwitterException, InterruptedException {
		games = null;
		//Until a game is found
    	//while(games == null || games.size()<1){
		Query query = new Query("#PlayPokerWithJDEC");
    	QueryResult result = twitter.search(query);
    	games = result.getTweets();
    	//Remove tweets
    	removeUnplayableTweets();
    	//if(games.size()<1) Thread.sleep(BASE_SCAN_DELAY*1000);
    	//}
    	return games.size();
    }
    
    /**
     * Removes old statuses, duplicate statuses and statuses by active players from {@link #games}
     * 
     * @see {@link #searchForGame()}
     */
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
    
    /**
     * Create a new game of poker with a specified status.
     * 
     * @param threadName	The {@link Thread} name for the game.
     * @param status - the {@link Status} status used to create the game.
     * @return Newly created {@link GameOfPoker}
     * @throws TwitterException
     */
    private GameOfPoker newGame(String threadName, Status status) throws TwitterException {
    	
    	//** Use twitter stream for twitter input/output or local stream for console input/output
    	//TwitterStream stream = new TwitterStream(twitter, game, game.getUser());
    	LocalStream stream = new LocalStream(twitter, status, status.getUser());
        
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
			System.out.println("Creating game...");
			playing.add(stream.user.getScreenName());
			return pokerGame;
		} else {
			System.out.println("Could not create game with status");
			return null;
		}
    }
    
    /**
     * Creates and runs up to {@link #MAX_GAMES} {@link GameOfPoker} objects but will not
     * replace any currently running games in {@link #activeGames}.
     * @throws TwitterException
     */
    public void createGames() throws TwitterException{
    	//Create and run games
		for(int i=0; i<MAX_GAMES; i++){
			if(activeGames[i]==null && games.size()>0){
				String threadName = "Thread " + (i+1);
				Status game = games.get(0);
				activeGames[i] = newGame(threadName, game);
				games.remove(0);
				activeGames[i].start();
			}
		}
    }
    
    /**
     * Sets any terminated {@link GameOfPoker} threads in {@link #activeGames} to null and removes
     * the player's username from {@link #playing}, so that new games can be started.
     */
    public void clearCompletedGames(){
    	//Set finished games to null
		for(int i=0; i<MAX_GAMES; i++){
			if(activeGames[i]!=null && activeGames[i].getThread().getState()==Thread.State.TERMINATED){
				playing.remove(activeGames[i].human.player_name);
				activeGames[i] = null;
			}
		}
    }
	
   //if something goes wrong, we might see a TwitterException
    public static void main(String... args) throws TwitterException, InterruptedException, FileNotFoundException, IOException{

    	JDECPokerBot bot = new JDECPokerBot();
    	
    	boolean singleGame = false;
    	if(singleGame){
    		System.out.println("Searching for new game");
    		bot.searchForGame();
	    	bot.newGame("Thread 1", bot.games.get(0));
	    	bot.searchBegin = new Date();
	    	return;
    	}
    	
    	while(!singleGame){
    		bot.searchForGame();
    		bot.clearCompletedGames();
    		bot.createGames();
    		Thread.sleep(BASE_SCAN_DELAY*1000);
    	}
    }

}