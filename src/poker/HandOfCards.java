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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class HandOfCards {

	static public final int HAND_SIZE = 5;
	private DeckOfCards deck;
	private ArrayList<PlayingCard> hand = new ArrayList<PlayingCard>();
	
	static private final int DECK_SIZE = 52;
	static private final int CARD_TYPES = 13;
	static public final int HIGH_HAND_DEFAULT = 0;				//defaults are multiples of one million for easy identification
	static public final int ONE_PAIR_DEFAULT = 1000000;
	static public final int TWO_PAIR_DEFAULT = 2000000;
	static public final int THREE_OF_A_KIND_DEFAULT = 3000000;
	static public final int STRAIGHT_DEFAULT = 4000000;
	static public final int FLUSH_DEFAULT = 5000000;
	static public final int FULL_HOUSE_DEFAULT = 6000000;
	static public final int FOUR_OF_A_KIND_DEFAULT = 7000000;
	static public final int STRAIGHT_FLUSH_DEFAULT = 8000000;
	static public final int ROYAL_FLUSH_DEFAULT = 9000000;
	static public final int FOURTEEN_FIRST = 14;				//multiplication constants are powers of 14 (as value of ace is 14)
	static public final int FOURTEEN_SECOND = 196;				//they could be lower but this is a very easy way to do and it won't affect calculation time
	static public final int FOURTEEN_THIRD = 2744;
	static public final int FOURTEEN_FOURTH = 38416;

	public HandOfCards(DeckOfCards deck){
		this.deck = deck;
		int i = 0;
		while (i != HAND_SIZE){
			hand.add(deck.dealNext());
			i++;
		}
		sort();
	}
	
	public DeckOfCards returnDeck() {
		return deck;
	}

	private void sort(){
		Collections.sort(hand, new Comparator<PlayingCard>() {
			@Override public int compare(PlayingCard card1, PlayingCard card2){
				return card2.getGameValue()-card1.getGameValue();
			}
		});
	}
	
	private boolean containsStraight(){
		int val = hand.get(0).getFaceValue();
		
		if(val == 1 && hand.get(1).getFaceValue() == 13)
			val = 14;
		else if(val == 1)
			val = 6;
		
		for(int i=1; i<HAND_SIZE; i++){
			if(hand.get(i).getFaceValue() != val-1 || hand.get(i).getGameValue() != val-1){
				return false;
			}
			val = hand.get(i).getFaceValue();
		}
		return true;
	}
	
	private boolean containsFlush(){
		char suit = hand.get(0).getSuit();
		for(int i = 1; i < HAND_SIZE; i++){
			if(hand.get(i).getSuit() != suit){
				return false;
			}
		}
		return true;
	}
	
	public boolean isRoyalFlush(){
		boolean hasAceKing = hand.get(0).getFaceValue()==1 && hand.get(1).getFaceValue()==13;
		return containsStraight() && containsFlush() && hasAceKing;
	}
	
	public boolean isStraightFlush(){
		boolean hasAceKing = hand.get(0).getFaceValue()==1 && hand.get(1).getFaceValue()==13;	
		return containsStraight() && containsFlush() && !hasAceKing;
	}
	
	public boolean isFourOfAKind(){
		if (hand.get(0).getFaceValue() == hand.get(3).getFaceValue() || 
			hand.get(1).getFaceValue() == hand.get(4).getFaceValue()){
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isFullHouse(){
		if ((hand.get(0).getFaceValue() == hand.get(2).getFaceValue() && 
			hand.get(3).getFaceValue() == hand.get(4).getFaceValue()) ||
			(hand.get(0).getFaceValue() == hand.get(1).getFaceValue() && 
			hand.get(2).getFaceValue() == hand.get(4).getFaceValue())){
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isFlush(){
		return containsFlush() && !containsStraight();
	}
	
	public boolean isStraight(){
		return containsStraight() && !containsFlush();
	}
	
	public boolean isThreeOfAKind(){
		if(isFourOfAKind() || isFullHouse()){
			return false;
		}
		//If hand has at least 3 matching cards, return true
		if(	(hand.get(0).getFaceValue() == hand.get(2).getFaceValue()) || 
			(hand.get(1).getFaceValue() == hand.get(3).getFaceValue()) ||
			(hand.get(2).getFaceValue() == hand.get(4).getFaceValue())){
			return true && !isFourOfAKind() && !isFullHouse();
		}
		//Otherwise, return false
		return false;
	}
	
	//Determines whether hand is a two pair (and not a more valuable hand)
	public boolean isTwoPair(){
		//If cards make a higher value hand containing matching cards, return false
		if(isFourOfAKind() || isFullHouse()){
			return false;
		} else if(isThreeOfAKind()){
			return false;
		}
		//Ensure hand contains at least 2 pairs - if so, return true
		if(	((hand.get(0).getFaceValue() == hand.get(1).getFaceValue()) && 
			((hand.get(2).getFaceValue() == hand.get(3).getFaceValue()) ||
			(hand.get(3).getFaceValue() == hand.get(4).getFaceValue()))) ||
			((hand.get(1).getFaceValue() == hand.get(2).getFaceValue()) && 
			((hand.get(3).getFaceValue() == hand.get(4).getFaceValue())))){
			
			return true;
		}
		//Otherwise, return false
		return false;
	}
	
	//Returns boolean determining if the hand has One pair (and is not a more valuable hand)
	public boolean isOnePair(){
		//If cards make a higher value hand containing matching cards, return false
		if(isFourOfAKind() || isFullHouse()){
			return false;
		} else if(isThreeOfAKind()){
			return false;
		} else if(isTwoPair()){
			return false;
		}
		//Ensure hand contains at least 2 matching cards - if so, return true
		if(	(hand.get(0).getFaceValue() == hand.get(1).getFaceValue()) || 
			(hand.get(1).getFaceValue() == hand.get(2).getFaceValue()) ||
			(hand.get(2).getFaceValue() == hand.get(3).getFaceValue()) ||
			(hand.get(3).getFaceValue() == hand.get(4).getFaceValue())){
			return true;
		}
		//Otherwise return false
		return false;
	}
	
	//Determines if the hand is only a high hand (by ensuring that it is not any
	//of the other hands.
	public boolean isHighHand(){
		//If all other hand methods return false, then the hand is a high hand
		if(	!isOnePair() && !isTwoPair() && !isThreeOfAKind() && !isFourOfAKind() && 
			!isFullHouse() && !containsStraight() && !containsFlush()){
			return true;
		} else {
			return false;
		}
	}
	
	public int getGameValue(){
		//returns royal flush default
		if(isRoyalFlush()) 
			return ROYAL_FLUSH_DEFAULT;
		
		//returns straight default + high card value
		else if(isStraightFlush()){
			if(hand.get(0).getGameValue()==14 && hand.get(1).getGameValue()==5)
				return STRAIGHT_FLUSH_DEFAULT + 5;
			else 
				return STRAIGHT_FLUSH_DEFAULT + hand.get(0).getGameValue();
		}
		
		//returns four of a kind default + value of four
		else if(isFourOfAKind()){
			if(hand.get(0).getGameValue()==hand.get(1).getGameValue()) 
				return FOUR_OF_A_KIND_DEFAULT + hand.get(0).getGameValue();
			else 
				return FOUR_OF_A_KIND_DEFAULT + hand.get(1).getGameValue();
		}
		
		//returns full house default + value of three
		else if(isFullHouse()){
			return FULL_HOUSE_DEFAULT + hand.get(2).getGameValue();
		}
		
		//returns flush default + first * 14^4 + second * 14^3 and so on
		else if(isFlush()){
			return FLUSH_DEFAULT + hand.get(0).getGameValue()*FOURTEEN_FOURTH + 
					hand.get(1).getGameValue()*FOURTEEN_THIRD + hand.get(2).getGameValue()*FOURTEEN_SECOND + 
					hand.get(3).getGameValue()*FOURTEEN_FIRST + hand.get(4).getGameValue(); 
		}
		
		//returns straight default + value of high card
		else if(isStraight()){
			if(hand.get(0).getGameValue()==14 && hand.get(2).getGameValue()==5) 
				return STRAIGHT_DEFAULT + hand.get(1).getGameValue()*FOURTEEN_FOURTH + 
						hand.get(2).getGameValue()*FOURTEEN_THIRD + hand.get(3).getGameValue()*FOURTEEN_SECOND + 
						hand.get(4).getGameValue()*FOURTEEN_FIRST + hand.get(0).getGameValue(); 
			else 
				return STRAIGHT_DEFAULT + hand.get(0).getGameValue()*FOURTEEN_FOURTH + 
						hand.get(1).getGameValue()*FOURTEEN_THIRD + hand.get(2).getGameValue()*FOURTEEN_SECOND + 
						hand.get(3).getGameValue()*FOURTEEN_FIRST + hand.get(4).getGameValue(); 
		}
		
		//returns three of a kind default + value of three
		else if(isThreeOfAKind()){
			return THREE_OF_A_KIND_DEFAULT + hand.get(2).getGameValue();
		}
		
		//returns two pair default + higher pair * 14^2 + lower paid * 14^1 + remaining card value
		else if(isTwoPair()){
			if(hand.get(0).getGameValue() == hand.get(1).getGameValue()){
				if(hand.get(2).getGameValue() == hand.get(3).getGameValue()) 
					return TWO_PAIR_DEFAULT + hand.get(0).getGameValue()*FOURTEEN_SECOND + hand.get(2).getFaceValue()*FOURTEEN_FIRST + hand.get(4).getGameValue();
				else 
					return TWO_PAIR_DEFAULT + hand.get(0).getGameValue()*FOURTEEN_SECOND + hand.get(3).getFaceValue()*FOURTEEN_FIRST + hand.get(2).getGameValue();
			}
			else 
				return TWO_PAIR_DEFAULT + hand.get(1).getGameValue()*FOURTEEN_SECOND + hand.get(3).getFaceValue()*FOURTEEN_FIRST + hand.get(0).getGameValue();
		}
		
		//returns pair + 14^3 + highest card * 14^2 and so on
		else if(isOnePair()){
			if(hand.get(0).getGameValue() == hand.get(1).getGameValue()) 
				return ONE_PAIR_DEFAULT + hand.get(0).getGameValue()*FOURTEEN_THIRD + hand.get(2).getGameValue()*FOURTEEN_SECOND + hand.get(3).getGameValue()*FOURTEEN_FIRST + hand.get(4).getGameValue();
			else if(hand.get(1).getGameValue() == hand.get(2).getGameValue()) 
				return ONE_PAIR_DEFAULT + hand.get(1).getGameValue()*FOURTEEN_THIRD + hand.get(0).getGameValue()*FOURTEEN_SECOND + hand.get(3).getGameValue()*FOURTEEN_FIRST + hand.get(4).getGameValue();
			else if(hand.get(2).getGameValue() == hand.get(3).getGameValue()) 
				return ONE_PAIR_DEFAULT + hand.get(2).getGameValue()*FOURTEEN_THIRD + hand.get(0).getGameValue()*FOURTEEN_SECOND + hand.get(1).getGameValue()*FOURTEEN_FIRST + hand.get(4).getGameValue();
			else 
				return ONE_PAIR_DEFAULT + hand.get(3).getGameValue()*FOURTEEN_THIRD + hand.get(0).getGameValue()*FOURTEEN_SECOND + hand.get(1).getGameValue()*FOURTEEN_FIRST + hand.get(2).getGameValue();
		}
		
		//otherwise returns high hand default + first * 14^4 and so on
		else 
			return HIGH_HAND_DEFAULT + hand.get(0).getGameValue()*FOURTEEN_FOURTH + 
					hand.get(1).getGameValue()*FOURTEEN_THIRD + hand.get(2).getGameValue()*FOURTEEN_SECOND + 
					hand.get(3).getGameValue()*FOURTEEN_FIRST + hand.get(4).getGameValue(); 
	}
	
	//for testing purposes
	public String toString(){
		String output = "";
		for(int i=0;i<5;i++){
			output+= hand.get(i).toString() + "\t";
		}
		return output;
	}
	
	//for testing purposes
	public String getHandType(){
		if(isRoyalFlush()) return "Royal Flush\t";
		else if(isStraightFlush()) return "Straight Flush\t";
		else if(isFourOfAKind()) return "Four of a Kind\t";
		else if(isFullHouse()) return "Full House\t";
		else if(isFlush()) return "Flush\t\t";
		else if(isStraight()) return "Straight\t";
		else if(isThreeOfAKind()) return "Three of a Kind\t";
		else if(isTwoPair()) return "Two Pair\t";
		else if(isOnePair()) return "One Pair\t";
		else return "High Hand\t";
	}
	
	public static void main(String[] args) {
		DeckOfCards deck = new DeckOfCards();
		
		for (int i = 0; i < 10; i++){
			HandOfCards myHand = new HandOfCards(deck);
			System.out.println(myHand.toString());
			System.out.println(myHand.getHandType());
		}
	}

}
