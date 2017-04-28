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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

import twitter4j.TwitterException;

public class GameOfPoker {
	
	private static final int MAX_PLAYERS = 5;
	private static final int MIN_PLAYERS = 2;

	private DeckOfCards deck;
	private TwitterStream twitter;
	private int numPlayers;
	public RoundOfPoker round;
	public ArrayList<PokerPlayer> players = new ArrayList<PokerPlayer>();
	public HumanPokerPlayer human;

	public GameOfPoker(TwitterStream tw, int playerAmount) throws TwitterException, FileNotFoundException, IOException{
		deck = new DeckOfCards();
		twitter = tw;
		numPlayers = Math.max(playerAmount, MIN_PLAYERS);
		numPlayers = Math.min(numPlayers, MAX_PLAYERS);

		human = new HumanPokerPlayer(twitter, deck);
		players.add(human);
		
		//Add players
		for(int i = 1; i < numPlayers; i++) players.add(getRandomAIPlayer());
		
		Collections.shuffle(players);	//shuffle players around the 'table'
		round = new RoundOfPoker(deck, players);
	}
	
	//Generate a random player
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
				if(line.equals(players.get(j).player_name)) duplicate = true;
			}
			if(duplicate) continue;
			//Chance to update name
			if(rand.nextInt(i)==0) result = line;
			i++;
		}
		sc.close();
		return new AutomatedPokerPlayer(result, deck);
	}
	
	//Remove bankrupt players from the player list
	private void removeBankruptPlayers(){
		for(int i=0; i<players.size(); i++){
			if(players.get(i).getChips()<0){
				players.remove(i);
			}
		}
	}
	
	private PokerPlayer getWinner(){
		if(players.size()>1){
			return null;
		} else {
			return players.get(0);
		}
	}
	
	//gets the index from array list of players which the human is
	private int humanIndex(){
		int index = -1;
		for (int i = 0; i < players.size(); i++){
			if (players.get(i).isBot == false)
				index = i;
		}
		return index;
	}
	
	public void startGame(){
		twitter.addToTweet("Starting " + numPlayers + " player game with " + human.player_name + ".\n");
		
		int human_index = humanIndex();
		while(getWinner()==null && players.get(human_index).game_active==true){
			RoundOfPoker round = new RoundOfPoker(deck, players, twitter);
			PokerPlayer roundWinner = round.startRound();
			//if human player decides to leave mid-game
			if (roundWinner == null) break;
			int pot = round.getPot();
			roundWinner.addChips(round.getPot());
			twitter.addToTweet("\n" + roundWinner.player_name + " won " + pot + " chips! ");
			
			//display players' cards who have not folded
			for(int i = 0; i < players.size(); i++){
				if (players.get(i).round_active != false){
					twitter.addToTweet("\n" + players.get(i).player_name + "'s cards: " + players.get(i).hand.toString());
				}
			}
			twitter.completeMessage();
			removeBankruptPlayers();
		}
		
		if (getWinner() != null){
			twitter.addToTweet(getWinner() + " won the game! ");
		} else {
			twitter.addToTweet(players.get(human_index).player_name + " has left the game.");
		}
		twitter.completeMessage();
		
	}
	
	public static void main(String[] args) throws TwitterException, InterruptedException, FileNotFoundException, IOException {
		
		//testing - temp
		GameOfPoker game = new GameOfPoker(null, 5);
		for(int i=0; i<game.players.size(); i++){
			System.out.println(game.players.get(i).player_name);
		}
	}
}
