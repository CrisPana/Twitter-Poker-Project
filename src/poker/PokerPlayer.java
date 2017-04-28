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

/*
 * PokerPlayer is an abstract class that defines a basic poker player
 * with the basic poker player methods and attributes
 * */
abstract class PokerPlayer {
	
	public String player_name;
	private int chips = 100;
	protected int chipsInPot = 0;
	protected boolean isBot;
	public boolean round_active = false;
	public boolean game_active = false;
	protected HandOfCards hand;

	PokerPlayer (String name, DeckOfCards deck){
		player_name = name;
		hand = new HandOfCards(deck);
	}
	
	protected int bet(int ch){
		chips -= ch;
		chipsInPot += ch;
		return ch;
	}
	
	public int enterGame(int buyIn){
		int startingBet = bet(buyIn);
		round_active = true;
		
		return startingBet;
	}

	public int getChips(){
		return chips;
	}
	
	public void addChips(int ch){
		chips += ch;
	}
	
	public int getChipsInPot(){
		return chipsInPot;
	}
	
	public void resetChipsInPot(){
		chipsInPot = 0;
	}
	
	public void resetHand(DeckOfCards deck){
		hand = new HandOfCards(deck);
	}
	
	public String toString(){
		return hand.toString();
	}
	
	//return what kind of hand player has
	private String checkHand(){
		return hand.getHandType();
	}

	abstract int action(int betAmount, int minimumBet, int blind);
	abstract int discard();
}
