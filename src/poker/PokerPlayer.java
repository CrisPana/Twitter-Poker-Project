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

import java.util.Random;

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
	
	/*
	 * method that gets the cards we want to discard
	 * and we call the discard function in the HandOfCards class to discard them
	 * */
	public int discard(){

		int cardDisProb[] = {-1, -1, -1, -1, -1};
		
		//store indices of the cards that we want to dicard
		//accordigng to discard probability
		int j = 0;
		for (int i = 0; i < myHand.HAND_SIZE; i++){
			Random rand = new Random();
			int randProb = rand.nextInt(99) + 1;
//			System.out.println("randProb = " + randProb + " \t card "+i+" discard prob = " + myHand.getDiscardProbability(i));
			if (myHand.getDiscardProbability(i) >= randProb){
				cardDisProb[j] = i;
				j++;
			}
		}
		
		//return unwanted cards to deck
		//and only up to three
		int sizeToDiscard = j;
		if (j > 3)
			sizeToDiscard = 3;

		for (int i = 0; i < sizeToDiscard; i++){
			if (cardDisProb[i] != -1){
				myHand.discard(cardDisProb[i], sizeToDiscard);
			}
		}

		return j;
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
