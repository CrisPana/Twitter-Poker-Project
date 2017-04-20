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
	
	public boolean startRound() {
		boolean roundFinished = false;
		dealerLocation++;
		
		//dealerLocation+1%players.size();
		while (!roundFinished) {
			
			//change to status update
			System.out.println("- Starting poker game with JDEC -");
			
			//PHASE 1 - setting up small and big blinds
			pot += players.get(dealerLocation+1%players.size()).enterGame(SMALL_BLIND);
			pot += players.get(dealerLocation+2%players.size()).enterGame(BIG_BLIND);
			
			//PHASE 2 - five cards dealt to each player
			//automatically dealt cards when the poker player instantiated 
			//reply at the user his/her cards
			
			//PHASE 3 - Betting Round #1
			int playerStartBet = -1;
			int betAmount = BIG_BLIND;
			
			//we start betting from the person left of the person with the big blind
			if (players.size() == 2) playerStartBet = dealerLocation+1%players.size();
			else playerStartBet = dealerLocation+3%players.size();
			
			int temp = 0;
			for (int i = playerStartBet; i < players.size(); i++) {
				
				//we get the action of the player which returns an integer
				//each different integer returned will correspond to an action eg fold, raise, etc
				temp = players.get(i).action();
				
				//if player folds, we remove them from arraylist or players
				//for this im setting 3 as the integer corresponding to action fold
				if (temp == 3)	players.get(i).leaveRound();
				if (temp > betAmount)
					betAmount = temp;
			}
			
			
			//PHASE 4 - Discarding
			for(int i = 0; i < players.size(); i++){
				//tweet this at user
				//System.out.println("Discard phase. Please tweet 'no' or the indices of cards you want to discard");
				
				//Twitter get status and parse and put into discard function
				players.get(i).discard();
				//if 

				//tweet player their new hand
			}
			
			//PHASE 5 - Betting Round #2
			
			//PHASE 6 - Showdown
			
			System.out.println();
		}

		return roundFinished;
	}
}
