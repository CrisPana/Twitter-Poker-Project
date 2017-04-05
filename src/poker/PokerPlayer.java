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
	
	public String player_name;
	public int chips = 10;
	public boolean round_active = false;
	public boolean game_active = false;
	private HandOfCards myHand;
	
	PokerPlayer (String name, DeckOfCards deck){
		player_name = name;
		myHand = new HandOfCards(deck);
	}
	
	public int discard(){
		/*To be implemented*/
		return 0;
	}
	
	public void raise(int ch){
		chips -= ch;
	}
	
	public void fold(){
		round_active = false;
	}
	
	public void enterGame(){
		game_active = true;
		round_active = true;
	}

	public void leaveGame(){
		game_active = true;
		round_active = true;
	}
	
	//return what kind of hand player has
	private String checkHand(){
		return myHand.getHandType();
	}
}
