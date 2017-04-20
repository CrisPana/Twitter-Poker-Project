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
import java.util.Collections;
import java.util.Random;

public class RoundOfPoker {
	
	//player left of dealer gets small blind
	//plyer left of small blind guy gets big blind
	//big blind usully minimum bet 
	//small blind half of that
	//In most games of poker, cards are dealt clockwise, or to the dealer's left. 
	//In Texas Hold 'Em, a variation of poker, the dealer deals to the left but skips two players, 
	//the small blind and the big blind, and deals first to the third person on the left
	static public final int SMALL_BLIND = 5;
	static public final int BIG_BLIND = 10;
	public boolean betRoundTwoFinished = false;
	public ArrayList<PokerPlayer> players = new ArrayList<PokerPlayer>();
	public int pot = 0;
	
	//Phases
		//1. Blinds on the table
		//2. Five cards dealt to each player
		//3. Bettting Round #1 - starting at player left of big blind
		//4. Discarding
		//5. Betting Round #2 - left of dealer
		//6. Showdown

	public RoundOfPoker(DeckOfCards deck) {
		int numPlayers = 1 + (int)(Math.random() * 4); ; 
		for(int i = 0; i < numPlayers; i++)
			players.add(new AutomatedPokerPlayer("John", deck));
	}
	
	public void addPlayer(HumanPokerPlayer player){
		players.add(player);
		Collections.shuffle(players);	//shuffle players around the 'table'
	}
	
	public boolean startGame() {
		boolean roundFinished = false;
		
		while (!roundFinished) {
			System.out.println("- Starting poker game with JDEC -");
			
			//PHASE 1 - setting up small and big blinds
			for(int i = 0; i < players.size(); i++){
				if (i == 0)
					pot += players.get(i).enterGame(SMALL_BLIND);
				else if (i == 1)
					pot += players.get(i).enterGame(BIG_BLIND);
			}
			
			//PHASE 2 - five cards dealt to each player
			//automatically dealt cards when the poker player instantiated 
			
			//PHASE 3 - Betting Round #1
			int playerStartBet = -1;
			int betAmount = BIG_BLIND;
			int minimumBet = BIG_BLIND;
			
			//we start betting from the person left of the person with the big blind
			if (players.size() == 2) playerStartBet = 0;
			else playerStartBet = 2;
			
			int temp = 0;
			for (int i = playerStartBet; i < players.size(); i++) {
				int toCall = betAmount - players.get(i).chipsInPot; //Amount needed for player to call
				temp = players.get(i).action(betAmount, minimumBet, BIG_BLIND);	//Return chips added to pot
				pot += temp;
				betAmount = temp - toCall; //Amount added beyond a call; total amount of chips required for play
				if ((temp - toCall) > minimumBet)
					minimumBet = temp;	//Increase minimum bet/raise
			}
			
			
			//PHASE 4 - Discarding
			
			for(int i = 0; i < players.size(); i++){
				System.out.println("Do you want to discard cards?");
				
//				if ()
			}
			
			System.out.println();
		}

		return roundFinished;
	}
}
