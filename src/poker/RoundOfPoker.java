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
 * RoundOfPoker is a class that defines a round of poker
 * It goes through the phases detailed below
 * and will be instantiated few times to create a whole game of poker
 * */

/**
 * Defines a round of poker. {@link #startRound} completes an entire round.
 * 
 * @author Dara Callinan
 * @author Jazheel Luna
 * @author Eoghan O'Donnell
 * @author Crischelle Pana
 */
public class RoundOfPoker {

	/*
	 * Phases
	 * 1. Five cards dealt to each player
	 * 2. Blinds on the table
	 * 3. Bettting Round #1 - starting at player left of big blind
	 * 4. Discarding
	 * 5. Betting Round #2 - left of dealer
	 * 6. Showdown
	 * */
	
	private int smallBlind;
	private int bigBlind;
	public int dealerLocation;
	public ArrayList<PokerPlayer> players = new ArrayList<PokerPlayer>();
	private int pot = 0;
	private TwitterStream twitter;
	
	/**
	 * Class constructor. Sets the {@link #bigBlind} and {@link #smallBlind} values and the {@link #players} list.
	 * Resets the {@link DeckOfCards} in use, before dealing new cards to each player in the {@link #players} list.
	 * Uses a {@link TwitterStream} for input and output.
	 * @param deck   The {@link DeckOfCards} being used in the game.
	 * @param players   A list of {@link PokerPlayer} objects representing the players in the game.
	 * @param stream   The {@link TwitterStream} to be used for input and output.
	 * @param bigBlind   The current big blind in the game.
	 * @param smallBlind   The current small blind in the game.
	 * @param dealerLocation   The index of the dealer in the {@link #players} list.
	 */
	public RoundOfPoker(DeckOfCards deck, ArrayList<PokerPlayer> players, TwitterStream stream, int bigBlind, int smallBlind, int dealerLocation) {
		this.bigBlind = bigBlind;
		this.smallBlind = smallBlind;
		this.dealerLocation = dealerLocation;
		this.players = players;
		deck.reset();
		for(int i=0; i<players.size(); i++){
			players.get(i).round_active = true;
			players.get(i).resetHand(deck);
		}
		twitter = stream;
	}
	
	/**
	 * Completes a round of betting, starting with an acting player and minimum bet. {@link PokerPlayer} objects in
	 * the {@link #players} list take an action. The round ends when all players have folded or matched the last
	 * bet/raise. Actions taken by the player are output to twitter via the {@link TwitterStream} {@link #twitter}.
	 * @param minimumBet   The minimum amount that can be bet, or raised.
	 * @param playerToAct   An {@code int} representing the index of the acting player in the {@link #players} list.
	 * @param blind   The big blind for the game.
	 * @return An {@code int} return status (0 for success).
	 * @see #getAction(PokerPlayer, int, int)
	 * @see PokerPlayer#action(int, int, int)
	 */
	private int betRound(int minimumBet, int playerToAct, int blind){
		boolean roundFinished = false;	//Round of betting complete
		int betAmount = minimumBet;		//Amount required to play (total call amount)
		int lastToBet = playerToAct;	//Last person to bet
		
		while(!roundFinished){
			PokerPlayer acting = players.get(playerToAct);
			//Skip player if not in round
			if(!acting.round_active || acting.getChips()==0){
				playerToAct = (playerToAct+1)%players.size();
				if(playerToAct == lastToBet) roundFinished = true;
				continue;
			}
			
			int toCall = betAmount - acting.getChipsInPot();	//Amount needed for player to call
			//Get player action, add chips to pot and increase bet amounts if necessary
			int temp = acting.action(betAmount, minimumBet, blind);
			if (temp == -1) return -1;
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
			//System.out.println(str + toCall + "  " + temp + "  " + minimumBet + "  " + betAmount + "  " + players.get(lastToBet).player_name);
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
		return 0;
	}
	
	/**
	 * Returns a string describing a player's action using the amount of chips they added to the pot.
	 * @param player   The {@link PokerPlayer} who acted.
	 * @param addedChips   The amount of chips added to the pot.
	 * @param requiredChips   The amount of chips the player needed to call.
	 * @return A {@link String} describing the action the player took.
	 */
	private String getAction(PokerPlayer player, int addedChips, int requiredChips){
		if(!player.round_active){
			return "\n" + player.getName() + " folded. ";
		}
		if(addedChips>requiredChips){
			if(requiredChips == 0) return player.getName() + " bet " + addedChips + ". ";
			return "\n" + player.getName() + " raised by " + (addedChips-requiredChips) + ". ";
		} else {
			if(addedChips == 0) return player.getName() + " checked. ";
			return "\n" + player.getName() + " called. ";
		}
	}
	
	//Complete a round of discarding hands
	/**
	 * Complete a round of discarding cards.
	 * @return A return status (0 for success).
	 */
	private int discardRound(){
		for(int i=0; i<players.size(); i++){
			if(!players.get(i).round_active) continue;
			
			int discarded = players.get(i).discard();
			if (discarded == -1) return -1;
			if(twitter!=null){
				String str = "\n" + players.get(i).getName() + " discarded " + discarded;
				if(discarded==1){
					str += " card. ";
				} else {
					str += " cards. ";
				}
				twitter.addToTweet(str);
			}
		}
		return 0;
	}
	
	/**
	 * Resets the amount of chips each {@link PokerPlayer} object has put in the pot.
	 */
	private void clearPersonalBetValues(){
		for(int i=0; i<players.size(); i++){
			players.get(i).resetChipsInPot();
		}
	}
	
	/**
	 * Checks if a round is over, returning true if there is one active player left.
	 * @return A {@code boolean}, true if the round is over and false otherwise.
	 */
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
	
	/**
	 * Gets the last {@link PokerPlayer} object that is still an active player. If
	 * the round is declared over by {@link #roundOver()}, this player will be the winner.
	 * @return The last {@link PokerPlayer} object in the {@link #players} list that is still active in the round.
	 * @see #roundOver()
	 */
	private PokerPlayer lastInPlay(){
		PokerPlayer winner = players.get(0);
		for(int i=1; i<players.size(); i++){
			if(players.get(i).round_active){
				winner = players.get(i);
			}
		}
		return winner;
	}
	
	/**
	 * Get the player with the best hand.
	 * @return The {@link PokerPlayer} who has the highest scoring {@link HandOfCards} object.
	 * @see HandOfCards#getGameValue()
	 */
	private PokerPlayer getWinner(){
		PokerPlayer winner = null;
		int winVal = 0;
		for(int i=0; i<players.size(); i++){
			PokerPlayer p = players.get(i);
			int handVal = p.getHand().getGameValue();
			if(p.round_active && handVal>winVal){
				winVal = handVal;
				winner = p;
			}
		}
		return winner;
	}
	
	/**
	 * Completes an entire round of poker. Begins with two players paying the {@link bigBlind} and {@link smallBlind}
	 * values, then completes a round of betting. If there is no single winner of the round, a discard round takes place,
	 * followed by a second round of betting. If there is still no single winner, each player's {@link PokerPlayer#hand}
	 * is compared to declare a winner, which is then returned by the method.
	 * @return The {@link PokerPlayer} who wins the round.
	 * @see #betRound(int, int, int)
	 * @see #discardRound()
	 */
	public PokerPlayer startRound() {
		
		playerChipsUpdate();
		if(twitter!=null){
			twitter.addToTweet("\nRound 1 of betting: ");
		}
		
		//PHASE 1 - five cards dealt to each player
		//automatically dealt cards when the poker player instantiated
		//reply at user his/her cards
		
		//PHASE 2 - setting up small and big blinds
		pot += players.get((dealerLocation+1) % players.size()).bet(smallBlind);
		twitter.addToTweet(players.get((dealerLocation+1) % players.size()).getName() + " paid the small blind. ");
		pot += players.get((dealerLocation+2) % players.size()).bet(bigBlind);
		twitter.addToTweet(players.get((dealerLocation+2) % players.size()).getName() + " paid the big blind. ");
		
		//PHASE 3 - Betting Round #1
		int toAct = (dealerLocation+3)%players.size();
		int status = betRound(bigBlind, toAct, bigBlind);
		if (status == -1)	return null;
		if(roundOver()){
			return lastInPlay();
		}
		twitter.addToTweet("(Current chips in pot = " + pot + ")");
		twitter.completeMessage();
		
		//PHASE 4 - Discarding
		status = discardRound();
		if (status == -1)	return null;
		
		//PHASE 5 - Betting Round #2
		if(twitter!=null){
			twitter.addToTweet("\nRound 2 of betting: ");
		}
		
		status = betRound(0, toAct, bigBlind);
		if (status == -1)	return null;
		if(roundOver()){
			return lastInPlay();
		}
		
		//PHASE 6 - Showdown
		return getWinner();
	}
	
	/**
	 * Gets the pot (the total amount of winnable chips) for this round.
	 * @return The {@link #pot} amount.
	 */
	public int getPot(){
		return pot;
	}
	
	/**
	 * Outputs a list of each player's current chips using the {@link TwitterStream}, {@link #twitter}.
	 */
	private void playerChipsUpdate(){
		twitter.addToTweet("Current chips:\n");
		for (int i = 0; i < players.size(); i++) {
			twitter.addToTweet("-" + players.get(i).getName() + " = " + players.get(i).getChips() +"\n");
		}
		twitter.completeMessage();
	}
}
