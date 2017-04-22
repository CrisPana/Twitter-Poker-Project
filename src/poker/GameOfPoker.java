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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import twitter4j.TwitterException;

public class GameOfPoker {

	private DeckOfCards deck;
	public JDECPokerBot pokerGame;
	public RoundOfPoker round;
	public ArrayList<PokerPlayer> players = new ArrayList<PokerPlayer>();
	public HumanPokerPlayer human;

	public GameOfPoker() throws TwitterException, FileNotFoundException, IOException{
		pokerGame = new JDECPokerBot();
		
		int noOfGame = 0;
		while (noOfGame <= 0){
			noOfGame = pokerGame.searchForGame();
		}

		human = new HumanPokerPlayer(pokerGame.getHumanPlayer(), deck);
		players.add(human);
		
		int numPlayers = 1 + (int)(Math.random() * 4); ; 
		for(int i = 0; i < numPlayers; i++)
			players.add(new AutomatedPokerPlayer("John", deck));
		
		Collections.shuffle(players);	//shuffle players around the 'table'
		round = new RoundOfPoker(deck, players);
	}
	
	public static void main(String[] args) throws TwitterException, InterruptedException, FileNotFoundException, IOException {
		
		//testing - temp
		GameOfPoker game = new GameOfPoker();
		game.pokerGame.replyToTweet();
	}
}
