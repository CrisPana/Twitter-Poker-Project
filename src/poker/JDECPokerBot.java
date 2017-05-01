/*
 * Software Engineering III - Twitter Poker Project
 * Team Name : JDEC
 * Team Members:
 * 		Dara Callinan 		14500717
 * 		Jazheel Luna		14486752
 * 		Eoghan O'Donnell	14464082
 * 		Crischelle Pana 	14366596
 * 
 * � 2017 
 * */

package poker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

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
	
	private static final int BASE_SCAN_DELAY = 30;		//Search for game every minute
	private static final int MAX_GAMES = 3;				//Maximum number of simultaneous games
	private static final int MAX_ID = 256;
	
	Date searchBegin;
	Twitter twitter;
	List<Status> games;
	List<String> playing;
	GameOfPoker[] activeGames;

	/**
	 * Class constructor. Initialises the {@link #playing} list, {@link #activeGames} array and {@link #searchBegin} date.
	 * Reads Twitter API keys from keys file, and connects to the Twitter API.
	 * @throws TwitterException   If API request is denied.
	 * @throws FileNotFoundException   If keys file cannot be found.
	 * @throws IOException   If I/O operations fail or are interrupted.
	 */
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
	 * Update the oldest status date for searching.
	 */
	public void updateSearchDate(){
		searchBegin = new Date();
	}

	/**
	 * Searches for statuses with the 'join game' string.
	 * 
	 * @return <code>int</code> - the amount of games found
	 * @throws TwitterException   If Twitter request is denied
	 * @throws InterruptedException   If a {@link GameOfPoker} thread is interrupted
	 */
	public int searchForGame() throws TwitterException, InterruptedException {
		games = null;
		Query query = new Query("#PlayPokerWithJDEC");
		QueryResult result = twitter.search(query);
		games = result.getTweets();
		//Remove tweets
		removeUnplayableTweets();
		return games.size();
	}
	
	/**
	 * Removes old statuses, duplicate statuses and statuses by active players from {@link #games}
	 * 
	 * @see #searchForGame()
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
	 * Gets an ID for a game from the properties file. This is used to avoid duplicate tweets.
	 * @return An {@code int} ID number.
	 * @throws IOException   If there is a failed or interrupted I/O operation.
	 */
	private int getGameID() throws IOException{
		Properties prop = new Properties();
		int id;
	    try {
			prop.load(new FileInputStream("Game.properties"));
			id = Integer.parseInt(prop.getProperty("nextGameID"));
		} catch (FileNotFoundException e) {
			prop.setProperty("nextGameID", "0");
		    prop.store(new FileOutputStream("Game.properties"), null);
			id = 0;
		}
		int newID = (id + 1) % MAX_ID;
		String nextID = String.valueOf(newID);
		prop.setProperty("nextGameID", nextID);
		prop.store(new FileOutputStream("Game.properties"), null);
		return id;
	}
	
	/**
	 * Create a new game of poker with a specified status.
	 * 
	 * @param threadName	The {@link Thread} name for the game.
	 * @param status	The {@link Status} used to create the game.
	 * @return Newly created {@link GameOfPoker}
	 * @throws TwitterException   If Twitter request is denied
	 */
	private GameOfPoker newGame(String threadName, Status status) throws TwitterException {
		//Get a unique game ID
		int gameID;
		try {
			gameID = getGameID();
		} catch (IOException e1) {
			return null;
		}
		
		//** Use twitter stream for twitter input/output or local stream for console input/output
		//TwitterStream stream = new TwitterStream(twitter, status, status.getUser(), gameID);
		LocalStream stream = new LocalStream(twitter, status, status.getUser());
	    
		GameOfPoker pokerGame = null;
		try {
			pokerGame = new GameOfPoker(threadName, stream, 5);
		} catch (FileNotFoundException e) {
			System.out.println("botnames.txt is missing or cannot be read");
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
	 * @throws TwitterException   If Twitter request is denied
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
				playing.remove(activeGames[i].getHumanPlayer().getName());
				activeGames[i] = null;
			}
		}
	}
	
    /**
     * Main method for executing the poker bot.
     * @param args   Arguments.
     * @throws TwitterException   If API request is denied.
     * @throws FileNotFoundException   If a file is not found.
     * @throws IOException   If I/O operations fail or are interrupted.
     * @throws InterruptedException   If a thread is interrupted.
     */
	public static void main(String... args) throws FileNotFoundException, TwitterException, IOException, InterruptedException {
	
		JDECPokerBot bot = new JDECPokerBot();
		
		boolean singleGame = true;
		if(singleGame){
			System.out.println("Searching for new game");
			bot.searchForGame();
	    	bot.newGame("Thread 1", bot.games.get(0)).start();
	    	bot.updateSearchDate();
	    	return;
		}
		
		while(!singleGame){
			bot.searchForGame();
			bot.clearCompletedGames();
			bot.createGames();
			bot.updateSearchDate();
			Thread.sleep(BASE_SCAN_DELAY*1000);
		}
	}
}