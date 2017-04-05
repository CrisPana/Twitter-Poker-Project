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
	public boolean active = false;
	private HandOfCards myHand;
	private DeckOfCards deck;
	
	PokerPlayer (String name, DeckOfCards deck){
		player_name = name;
		this.deck = deck;
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
		for(int i = 0; i < myHand.HAND_SIZE; i++) {
			//deck.returnCard(myHand.hand.get(i));
		}
	}
	
	//return what kind of hand player has
	private String checkHand(){
		String str = "";
		if(myHand.isRoyalFlush()){
			str = "Royal Flush";
		} else if(myHand.isStraightFlush()){
			str = "Straight Flush";
		} else if(myHand.isFourOfAKind()){
			str = "Four of a Kind";
		} else if(myHand.isFullHouse()){
			str = "Full House";
		} else if(myHand.isFlush()){
			str = "Flush";
		} else if(myHand.isStraight()){
			str = "Straight";
		} else if(myHand.isThreeOfAKind()){
			str = "Three of a Kind";
		} else if(myHand.isTwoPair()){
			str = "Two Pair";
		} else if(myHand.isOnePair()){
			str = "One Pair";
		} else {
			str = "High Hand";
		}
		
		return str;
	}
	
	public void enterGame(){
		active = true;
	}

	public void leaveGame(){
		active = false;
	}
}
