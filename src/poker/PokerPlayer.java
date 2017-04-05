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

public class PokerPlayer {
	
	static public final String player_name = "";
	public int chips = 0;
	private HandOfCards myHand;
	
	
	PokerPlayer (DeckOfCards deck){
		myHand = new HandOfCards(deck);
	}
	
	public int diacard(){
		return 0;
	}
	
	public void check(){
		//neither raising nor folding
	}
	
	public void raise(int ch){
		//subtract ch from chips
	}
	
	public void fold(){
		//return hand to deck
	}
	
	private void checkHand(){
		//return what kind of hand player has
	}

	public void leaveGame(){
		//player leaves table/game
	}
}
