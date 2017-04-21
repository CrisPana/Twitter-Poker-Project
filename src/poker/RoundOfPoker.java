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

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/*
 * RULES
 * - player left of dealer gets small blind
 * - plyer left of small blind guy gets big blind
 * - cards are dealt clockwise, or to the dealer's left.
 * - we are using blinds (i.e. big blinds and small blinds)
 * 
 * */

public class RoundOfPoker {

	static public final int SMALL_BLIND = 5;
	static public final int BIG_BLIND = 10;
	public boolean betRoundTwoFinished = false;
	public int dealerLocation = 0;
	public ArrayList<PokerPlayer> players = new ArrayList<PokerPlayer>();
	public int pot = 0;
	
	//Phases
		//1. Five cards dealt to each player
		//2. Blinds on the table
		//3. Bettting Round #1 - starting at player left of big blind
		//4. Discarding
		//5. Betting Round #2 - left of dealer
		//6. Showdown

	public RoundOfPoker(DeckOfCards deck, ArrayList<PokerPlayer> players) {
		this.players = players;
	}
	
	public boolean startRound() {
		boolean roundFinished = false;
		dealerLocation++;
			
		while (!roundFinished) {
			//change syso to a tweet to human user
			System.out.println("- Starting poker game with JDEC -");
			
			//PHASE 1 - five cards dealt to each player
			//automatically dealt cards when the poker player instantiated
			//reply at user his/her cards
			
			//PHASE 2 - setting up small and big blinds
			pot += players.get(dealerLocation+1%players.size()).enterGame(SMALL_BLIND);
			pot += players.get(dealerLocation+2%players.size()).enterGame(BIG_BLIND);
			
			//PHASE 3 - Betting Round #1
			int playerStartBet = -1;
			int betAmount = BIG_BLIND;
			int minimumBet = BIG_BLIND;
			
			//we start betting from the person left of the person with the big blind
			if (players.size() == 2) playerStartBet = dealerLocation+1%players.size();
			else playerStartBet = dealerLocation+3%players.size();
			
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
				//tweet this at user
				//System.out.println("Discard phase. Please tweet 'no' or the indices of cards you want to discard");
				
				//to do - Twitter get status and parse and put into discard function
				//players.get(i).discard();
				
				//to do - tweet player their new hand
			}
			
			//PHASE 5 - Betting Round #2
			
			//PHASE 6 - Showdown
			
		}

		return roundFinished;
	}
}
