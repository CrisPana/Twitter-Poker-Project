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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

import twitter4j.TwitterException;

/**
 * A game of poker which extends the {@link Thread} class. Supports one {@link HumanPokerPlayer} player and up to
 * four additional {@link AutomatedPokerPlayer} players. Uses a {@link TwitterStream} for input and output.
 * 
 * @author Dara Callinan
 * @author Jazheel Luna
 * @author Eoghan O'Donnell
 * @author Crischelle Pana
 */
public class GameOfPoker extends Thread {
	
	private static final int MAX_PLAYERS = 6;
	private static final int MIN_PLAYERS = 2;
	static public final int SMALL_BLIND = 5;		//Base small blind
	static public final int BIG_BLIND = 10;			//Base big blind
	private static final int BLIND_INCREASE = 10;	//Amount of rounds blinds will increase after (blinds increase every 10 rounds)

	private Thread t;
	private String threadName;
	private DeckOfCards deck;
	private TwitterStream twitter;
	private int numPlayers;
	private int dealerPosition = 0;
	public ArrayList<PokerPlayer> players = new ArrayList<PokerPlayer>();
	private HumanPokerPlayer human;

	/**
	 * Class constructor. Initialises the player list by generating several {@link AutomatedPokerPlayer} objects in addition to one
	 * {@link HumanPokerPlayer} object.
	 * @param threadName   The {@link Thread} name for this object.
	 * @param tw   The {@link TwitterStream} to be used for input and output.
	 * @param playerAmount   The amount of players in the game, limited to the range 2-5 incl.
	 * @throws TwitterException   If a Twitter API request is denied.
	 * @throws FileNotFoundException   If the AI names text file cannot be read.
	 */
	public GameOfPoker(String threadName, TwitterStream tw, int playerAmount) throws TwitterException, FileNotFoundException{
		this.threadName = threadName;
		deck = new DeckOfCards();
		twitter = tw;
		numPlayers = Math.max(playerAmount, MIN_PLAYERS);
		numPlayers = Math.min(numPlayers, MAX_PLAYERS);

		human = new HumanPokerPlayer(twitter, deck);
		players.add(human);
		
		//Add players
		for(int i = 1; i < numPlayers; i++) players.add(getRandomAIPlayer());
		
		Collections.shuffle(players);	//shuffle players around the 'table'
	}
	
	/**
	 * Get the {@link Thread} of this object.
	 * @return The {@link Thread} {@link #t}
	 */
	public Thread getThread(){
		return t;
	}
	
	public void run() {
		System.out.println("Running " +  threadName );
		startGame();
		System.out.println(threadName + " exiting.");
	}
	
	public void start () {
		System.out.println("Starting " +  threadName );
		if(t == null){
			t = new Thread (this, threadName);
			t.start();
		}
	}
	
	/**
	 * Creates a new {@link AutomatedPokerPlayer} with a random name read from a text file. Names that
	 * have already been used for {@link PokerPlayer} objects in the {@link #players} list are not used.
	 * @return A new {@link AutomatedPokerPlayer} with a random name from a text file.
	 * @throws FileNotFoundException   If the AI name-containing file cannot be read.
	 */
	private AutomatedPokerPlayer getRandomAIPlayer() throws FileNotFoundException{
		//List of AI names
		File file = new File("botnames.txt");
		String result = null;
		Random rand = new Random();
		Scanner sc = new Scanner(file);
		//Reservoir sampling to select name
		for(int i=1; sc.hasNext();){
			String line = sc.nextLine();
			//Skip duplicate names
			boolean duplicate = false;
			for(int j=0; j<players.size(); j++){
				if(line.equals(players.get(j).getName())) duplicate = true;
			}
			if(duplicate) continue;
			//Chance to update name
			if(rand.nextInt(i)==0) result = line;
			i++;
		}
		sc.close();
		return new AutomatedPokerPlayer(result, deck);
	}
	
	/**
	 * Returns the {@link HumanPokerPlayer} in this game.
	 * @return The {@link HumanPokerPlayer}
	 */
	public HumanPokerPlayer getHumanPlayer(){
		return human;
	}
	
	/**
	 * Removes all {@link PokerPlayer} objects in the {@link #players} list who are bankrupt.
	 */
	private void removeBankruptPlayers(){
		for(int i=0; i<players.size(); i++){
			if(players.get(i).getChips()<=0){
				players.remove(i);
				if(i<=dealerPosition) dealerPosition--;
				i--;
			}
		}
	}
	
	/**
	 * Returns a winning {@link PokerPlayer} if there is one {@link PokerPlayer} left in the {@link #players} list,
	 * otherwise returns null.
	 * @return The winning {@link PokerPlayer} or null if one does not exist.
	 */
	private PokerPlayer getWinner(){
		if(players.size()>1){
			return null;
		} else {
			return players.get(0);
		}
	}
	
	/**
	 * Completes an entire game of poker. Multiple {@link RoundOfPoker} objects are created and run to completion. The
	 * method terminates when a winner can be determined, the {@link #human} player has left, or the {@link #human}
	 * player is bankrupt.
	 * 
	 * @see RoundOfPoker
	 */
	private void startGame(){
		twitter.addToTweet("Starting " + numPlayers + " player game with " + human.getName() + ".\n");
		
		int roundCount = 0;
		int bigBlind = BIG_BLIND;
		int smallBlind = SMALL_BLIND;
		while(getWinner()==null && human.game_active==true && human.getChips()>0){
			RoundOfPoker round = new RoundOfPoker(deck, players, twitter, bigBlind, smallBlind, dealerPosition);
			PokerPlayer roundWinner = round.startRound();
			//if human player decides to leave mid-game
			if (roundWinner == null) break;
			int pot = round.getPot();
			roundWinner.addChips(round.getPot());
			twitter.addToTweet("\n" + roundWinner.getName() + " won " + pot + " chips! ");
			
			//display players' cards who have not folded
			for(int i = 0; i < players.size(); i++){
				if (players.get(i).round_active != false){
					twitter.addToTweet("\n" + players.get(i).getName() + "'s cards: " + players.get(i).getHand().toString());
				}
			}
			twitter.completeMessage();
			removeBankruptPlayers();
			roundCount++;
			bigBlind = BIG_BLIND * ((roundCount/BLIND_INCREASE) + 1);
			smallBlind = SMALL_BLIND * ((roundCount/BLIND_INCREASE) + 1);
			dealerPosition = (dealerPosition + 1) % players.size();
		}
		
		if (getWinner() != null){
			twitter.addToTweet(getWinner().getName() + " won the game! ");
		} else if(human.getChips() == 0){
			twitter.addToTweet(human.getName() + " is bankrupt! ");
		} else {
			twitter.addToTweet(human.getName() + " has left the game.");
		}
		twitter.completeMessage();
		
	}
}
