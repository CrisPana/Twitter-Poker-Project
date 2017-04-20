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

abstract class PokerPlayer {
	
	public String player_name;
	public int chips = 100;
	public boolean round_active = false;
	public boolean game_active = false;
	private HandOfCards myHand;
	
	PokerPlayer (String name, DeckOfCards deck){
		player_name = name;
		myHand = new HandOfCards(deck);
	}
	
	private int raise(int ch){
		chips -= ch;
		return ch;
	}
	
	private void fold(){
		round_active = false;
	}
	
	public int enterGame(int buyIn){
		int startingBet = raise(buyIn);
		game_active = true;
		round_active = true;
		
		return startingBet;
	}

	public void leaveGame(){
		game_active = false;
		round_active = false;
	}
	
	//return what kind of hand player has
	private String checkHand(){
		return myHand.getHandType();
	}

	abstract int action();
	abstract int discard();
}
