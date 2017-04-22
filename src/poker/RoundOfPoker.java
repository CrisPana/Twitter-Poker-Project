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
	private PokerUpdate twitter;
	
	//Phases
		//1. Five cards dealt to each player
		//2. Blinds on the table
		//3. Bettting Round #1 - starting at player left of big blind
		//4. Discarding
		//5. Betting Round #2 - left of dealer
		//6. Showdown

	public RoundOfPoker(DeckOfCards deck, ArrayList<PokerPlayer> players, PokerUpdate update) {
		this.players = players;
		for(int i=0; i<players.size(); i++){
			players.get(i).round_active = true;
		}
		twitter = update;
	}
	//Alternative constructor
	public RoundOfPoker(DeckOfCards deck, ArrayList<PokerPlayer> players) {
		this.players = players;
		for(int i=0; i<players.size(); i++){
			players.get(i).round_active = true;
		}
	}
	
	//Complete a round of betting starting with an acting player and minimum bet (usually big blind or zero)
	private void betRound(int minimumBet, int playerToAct, int blind){
		boolean roundFinished = false;	//Round of betting complete
		int betAmount = minimumBet;		//Amount required to play (total call amount)
		int lastToBet = playerToAct;	//Last person to bet/First acting player if no bets/raises
		
		while(!roundFinished){
			PokerPlayer acting = players.get(playerToAct);
			//Skip player if not in round
			if(!acting.round_active){
				playerToAct = (playerToAct+1)%players.size();
				continue;
			}
			
			int toCall = betAmount - acting.chipsInPot;	//Amount needed for player to call
			//Get player action, add chips to pot and increase bet amounts if necessary
			int temp = acting.action(betAmount, minimumBet, blind);
			pot += temp;
			betAmount += temp - toCall;
			if ((temp - toCall) >= minimumBet){
				minimumBet = temp - toCall;
				lastToBet = playerToAct;
			}
			
			playerToAct = (playerToAct+1)%players.size();
			//Next player was the last to bet (end of betting round)
			if(playerToAct == lastToBet) roundFinished = true;
		}
	}
	
	private String getAction(){
		return "";
	}
	
	//Complete a round of discarding hands
	private void discardRound(){
		for(int i=0; i<players.size(); i++){
			if(!players.get(i).round_active) continue;
			
			players.get(i).discard();
			
		}
	}
	
	//Check if round is over (one winner)
	private boolean roundOver(){
		int activePlayers = 0;
		for(int i=0; i<players.size(); i++){
			if(players.get(i).round_active){
				activePlayers++;
			}
		}
		if(activePlayers == 1){
			return true;
		} else {
			return false;
		}
	}
	
	//Return winner
	private PokerPlayer getWinner(){
		PokerPlayer winner = players.get(0);
		for(int i=0; i<players.size(); i++){
			if(players.get(i).round_active){
				winner = players.get(i);
			}
		}
		return winner;
	}
	
	public PokerPlayer startRound() {
		//change syso to a tweet to human user
		System.out.println("Begin");
		
		//PHASE 1 - five cards dealt to each player
		//automatically dealt cards when the poker player instantiated
		//reply at user his/her cards
		
		//PHASE 2 - setting up small and big blinds
		pot += players.get(dealerLocation+1%players.size()).bet(SMALL_BLIND);
		pot += players.get(dealerLocation+2%players.size()).bet(BIG_BLIND);
		
		//PHASE 3 - Betting Round #1
		betRound(BIG_BLIND, dealerLocation+3%players.size(), BIG_BLIND);
		if(roundOver()){
			return getWinner();
		}
		
		//PHASE 4 - Discarding
		discardRound();
		System.out.println("Round 2");
		//PHASE 5 - Betting Round #2
		betRound(0, dealerLocation+3%players.size(), BIG_BLIND);
		if(roundOver()){
			return getWinner();
		}
		
		//PHASE 6 - Showdown
		//to do
		return players.get(0);
	}
	
	
	public static void main(String args[]){
		DeckOfCards deck = new DeckOfCards();
		ArrayList<PokerPlayer> players = new ArrayList<PokerPlayer>();
		players.add(new AutomatedPokerPlayer("Gill", deck));
		players.add(new AutomatedPokerPlayer("Henry", deck));
		players.add(new AutomatedPokerPlayer("John", deck));
		players.add(new AutomatedPokerPlayer("Alice", deck));
		players.add(new AutomatedPokerPlayer("Sarah", deck));
		
		RoundOfPoker round = new RoundOfPoker(deck, players);
		round.startRound();
	}
	
}
