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
	public int dealerLocation = 0;
	public ArrayList<PokerPlayer> players = new ArrayList<PokerPlayer>();
	private int pot = 0;
	private TwitterStream twitter;
	
	//Phases
		//1. Five cards dealt to each player
		//2. Blinds on the table
		//3. Bettting Round #1 - starting at player left of big blind
		//4. Discarding
		//5. Betting Round #2 - left of dealer
		//6. Showdown

	public RoundOfPoker(DeckOfCards deck, ArrayList<PokerPlayer> players, TwitterStream stream) {
		this.players = players;
		deck.reset();
		for(int i=0; i<players.size(); i++){
			players.get(i).round_active = true;
			players.get(i).resetHand(deck);
		}
		twitter = stream;
	}
	//Alternative constructor for AI only games
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
		int lastToBet = playerToAct;	//Last person to bet
		
		while(!roundFinished){
			PokerPlayer acting = players.get(playerToAct);
			//Skip player if not in round
			if(!acting.round_active){
				playerToAct = (playerToAct+1)%players.size();
				if(playerToAct == lastToBet) roundFinished = true;
				continue;
			}
			
			int toCall = betAmount - acting.getChipsInPot();	//Amount needed for player to call
			//Get player action, add chips to pot and increase bet amounts if necessary
			int temp = acting.action(betAmount, minimumBet, blind);
			pot += temp;
			if((temp - toCall) > 0){
				betAmount += temp - toCall;
				
				//If a bet/raise - increase minimumBet
				if((temp - toCall) >= minimumBet){
					minimumBet = temp - toCall;
					lastToBet = playerToAct;
				}
			}
			
			String str = getAction(acting, temp, toCall);
			//Output for testing
			System.out.println(str + toCall + "  " + temp + "  " + minimumBet + "  " + betAmount + "  " + players.get(lastToBet).player_name);
			if(twitter!=null){
				twitter.addToTweet(str);
			}
			
			//Check if round is over (in case all players folded before big blind)
			if(roundOver()) roundFinished = true;
			
			playerToAct = (playerToAct+1)%players.size();
			//Next player was the last to bet (end of betting round)
			if(playerToAct == lastToBet) roundFinished = true;
		}
		
		//Clear the amount of chips each player has put in pot
		clearPersonalBetValues();
	}
	
	//Returns a string describing a player's action using the amount of chips they added to the pot
	private String getAction(PokerPlayer player, int addedChips, int requiredChips){
		if(!player.round_active){
			return player.player_name + " folded. ";
		}
		if(addedChips>requiredChips){
			if(requiredChips == 0) return player.player_name + " bet " + addedChips + ". ";
			return player.player_name + " raised by " + (addedChips-requiredChips) + ". ";
		} else {
			if(addedChips == 0) return player.player_name + " checked. ";
			return player.player_name + " called. ";
		}
	}
	
	//Complete a round of discarding hands
	private void discardRound(){
		for(int i=0; i<players.size(); i++){
			if(!players.get(i).round_active) continue;
			
			int discarded = players.get(i).discard();
			if(twitter!=null){
				String str = players.get(i).player_name + " discarded " + discarded;
				if(discarded==1){
					str += " card. ";
				} else {
					str += " cards. ";
				}
				twitter.addToTweet(str);
			}
		}
	}
	
	//Reset pot values
	private void clearPersonalBetValues(){
		for(int i=0; i<players.size(); i++){
			players.get(i).resetChipsInPot();
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
	
	//Return last player in play
	private PokerPlayer lastInPlay(){
		PokerPlayer winner = players.get(0);
		for(int i=1; i<players.size(); i++){
			if(players.get(i).round_active){
				winner = players.get(i);
			}
		}
		return winner;
	}
	
	//Return player with best hand
	private PokerPlayer getWinner(){
		PokerPlayer winner = null;
		int winVal = 0;
		for(int i=0; i<players.size(); i++){
			PokerPlayer p = players.get(i);
			int handVal = p.hand.getGameValue();
			if(p.round_active && handVal>winVal){
				winVal = handVal;
				winner = p;
			}
		}
		return winner;
	}
	
	public PokerPlayer startRound() {
		
		playerChipsUpdate();
		System.out.println("Round 1 betting begin");
		if(twitter!=null){
			twitter.addToTweet("Round 1 of betting: ");
		}
		
		//PHASE 1 - five cards dealt to each player
		//automatically dealt cards when the poker player instantiated
		//reply at user his/her cards
		
		//PHASE 2 - setting up small and big blinds
		pot += players.get(dealerLocation+1%players.size()).bet(SMALL_BLIND);
		pot += players.get(dealerLocation+2%players.size()).bet(BIG_BLIND);
		
		//PHASE 3 - Betting Round #1
		betRound(BIG_BLIND, dealerLocation+3%players.size(), BIG_BLIND);
		if(roundOver()){
			return lastInPlay();
		}
		twitter.addToTweet("(Current chips in pot = " + pot + ")");
		twitter.completeMessage();
		
		//PHASE 4 - Discarding
		discardRound();
		System.out.println("Round 2 betting begin");
		
		//PHASE 5 - Betting Round #2
		if(twitter!=null){
			twitter.addToTweet("Round 2 of betting: ");
		}
		
		betRound(0, dealerLocation+3%players.size(), BIG_BLIND);
		if(roundOver()){
			return lastInPlay();
		}
		
		//PHASE 6 - Showdown
		return getWinner();
	}
	
	public int getPot(){
		return pot;
	}
	
	public void playerChipsUpdate(){
		for (int i = 0; i < players.size(); i++) {
			twitter.addToTweet("(" + players.get(i).player_name + "'s current chips = " + players.get(i).getChips() +")");
		}
		twitter.completeMessage();
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
		PokerPlayer p = round.startRound();
		System.out.println(p.player_name + " - " + p.hand);
		
		//Test a number of rounds
		for(int i=0; i<300; i++){
			deck.reset();
			players = new ArrayList<PokerPlayer>();
			players.add(new AutomatedPokerPlayer("Gill", deck));
			players.add(new AutomatedPokerPlayer("Henry", deck));
			players.add(new AutomatedPokerPlayer("John", deck));
			players.add(new AutomatedPokerPlayer("Alice", deck));
			players.add(new AutomatedPokerPlayer("Sarah", deck));
			
			round = new RoundOfPoker(deck, players);
			p = round.startRound();
			System.out.println(p.player_name + " - " + p.hand);
		}
	}
	
}
