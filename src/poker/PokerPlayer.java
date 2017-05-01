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

/**
 * An abstract class that defines a basic poker player.
 * @author Dara Callinan
 * @author Jazheel Luna
 * @author Eoghan O'Donnell
 * @author Crischelle Pana
 */
abstract class PokerPlayer {
	
	private String player_name;
	private int chips = 100;
	private int chipsInPot = 0;
	protected boolean round_active = false;
	protected boolean game_active = false;
	private HandOfCards hand;

	/**
	 * Class constructor. Initialises the player's name and initialises their {@link #hand} by dealing
	 * a new {@link HandOfCards}.
	 * @param name   The {@code String} containing the player's name
	 * @param deck   The {@code DeckOfCards} being used to deal the player's hand.
	 */
	PokerPlayer (String name, DeckOfCards deck){
		player_name = name;
		hand = new HandOfCards(deck);
	}
	
	/**
	 * Reduces the player's {@link #chips} and increases their {@link #chipsInPot} by a specified amount,
	 * simulating a standard poker bet.
	 * @param ch   The amount of chips to bet.
	 * @return The amount of chips bet.
	 */
	protected int bet(int ch){
		chips -= ch;
		chipsInPot += ch;
		return ch;
	}
	
	/**
	 * Gets the player's name.
	 * @return The player's {@link #player_name name}.
	 */
	public String getName(){
		return player_name;
	}
	
	/**
	 * Gets the player's current {@link #chips}.
	 * @return The player's {@link #chips}.
	 */
	public int getChips(){
		return chips;
	}
	
	/**
	 * Awards the player some {@link #chips}.
	 * @param ch   The amount of chips to be awarded.
	 */
	public void addChips(int ch){
		chips += ch;
	}
	
	/**
	 * Gets the player's amount of {@link #chipsInPot chips already bet} in a round of betting.
	 * @return The amount of chips the player has added to the pot.
	 */
	public int getChipsInPot(){
		return chipsInPot;
	}
	
	/**
	 * Resets the amount of {@link #chipsInPot chips added to the pot}.
	 */
	public void resetChipsInPot(){
		chipsInPot = 0;
	}
	
	/**
	 * Deals the player a new hand.
	 * @param deck   The {@link DeckOfCards deck} being used to deal the hand.
	 */
	public void resetHand(DeckOfCards deck){
		hand = new HandOfCards(deck);
	}
	
	/**
	 * Gets the player's {@link HandOfCards} {@link #hand attribute}.
	 * @return The player's {@link #hand hand}.
	 */
	public HandOfCards getHand(){
		return hand;
	}
	
	public String toString(){
		return hand.toString();
	}

	/**
	 * Decides the player's action in a {@link RoundOfPoker round of poker} round of betting.
	 * @param betAmount   The total amount necessary to call.
	 * @param minimumBet   The minimum amount that can be bet or raised.
	 * @param blind   The big blind for the game.
	 * @return The amount of chips added to the pot.
	 */
	abstract int action(int betAmount, int minimumBet, int blind);
	
	/**
	 * Decides which cards the player should discard during the discard phase of a
	 * {@link RoundOfPoker round of poker}.
	 * @return The amount of cards discarded by the player.
	 */
	abstract int discard();
}
