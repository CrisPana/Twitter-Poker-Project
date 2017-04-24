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
	static public final int HIGH_HAND_DEFAULT = 0; // defaults are multiples of one million for easy identification
	static public final int ONE_PAIR_DEFAULT = 1000000;
	static public final int TWO_PAIR_DEFAULT = 2000000;
	static public final int THREE_OF_A_KIND_DEFAULT = 3000000;
	static public final int STRAIGHT_DEFAULT = 4000000;
	static public final int FLUSH_DEFAULT = 5000000;
	static public final int FULL_HOUSE_DEFAULT = 6000000;
	static public final int FOUR_OF_A_KIND_DEFAULT = 7000000;
	static public final int STRAIGHT_FLUSH_DEFAULT = 8000000;
	static public final int ROYAL_FLUSH_DEFAULT = 9000000;
	static public final int FOURTEEN_FIRST = 14; // multiplication constants are powers of 14 (as value of ace is 14)
	static public final int FOURTEEN_SECOND = 196; // they could be lower but this is a very easy way to do and it won't affect calculation time
	static public final int FOURTEEN_THIRD = 2744;
	static public final int FOURTEEN_FOURTH = 38416;

	public HandOfCards(DeckOfCards deck) {
		this.deck = deck;
		int i = 0;
		while (i != HAND_SIZE) {
			hand.add(deck.dealNext());
			i++;
		}
		sort();
	}

	public DeckOfCards returnDeck() {
		return deck;
	}

	private void sort() {
		Collections.sort(hand, new Comparator<PlayingCard>() {
			@Override
			public int compare(PlayingCard card1, PlayingCard card2) {
				return card2.getGameValue() - card1.getGameValue();
			}
		});
	}

	private boolean containsStraight() {
		int val = hand.get(0).getFaceValue();

		if (val == 1 && hand.get(1).getFaceValue() == 13)
			val = 14;
		else if (val == 1)
			val = 6;

		for (int i = 1; i < HAND_SIZE; i++) {
			if (hand.get(i).getFaceValue() != val - 1 || hand.get(i).getGameValue() != val - 1)
				return false;
			val = hand.get(i).getFaceValue();
		}
		return true;
	}

	private boolean containsFlush() {
		char suit = hand.get(0).getSuit();

		for (int i = 1; i < HAND_SIZE; i++) {
			if (hand.get(i).getSuit() != suit)
				return false;
		}

		return true;
	}

	public boolean isRoyalFlush() {
		boolean hasAceKing = hand.get(0).getFaceValue() == 1 && hand.get(1).getFaceValue() == 13;
		return containsStraight() && containsFlush() && hasAceKing;
	}

	public boolean isStraightFlush() {
		boolean hasAceKing = hand.get(0).getFaceValue() == 1 && hand.get(1).getFaceValue() == 13;
		return containsStraight() && containsFlush() && !hasAceKing;
	}

	public boolean isFourOfAKind() {
		if (hand.get(0).getFaceValue() == hand.get(3).getFaceValue()
				|| hand.get(1).getFaceValue() == hand.get(4).getFaceValue())
			return true;
		else
			return false;
	}

	public boolean isFullHouse() {
		if ((hand.get(0).getFaceValue() == hand.get(2).getFaceValue()
				&& hand.get(3).getFaceValue() == hand.get(4).getFaceValue())
				|| (hand.get(0).getFaceValue() == hand.get(1).getFaceValue()
						&& hand.get(2).getFaceValue() == hand.get(4).getFaceValue()))
			return true;
		else
			return false;
	}

	public boolean isFlush() {
		return containsFlush() && !containsStraight();
	}

	public boolean isStraight() {
		return containsStraight() && !containsFlush();
	}

	public boolean isThreeOfAKind() {
		if (isFourOfAKind() || isFullHouse())
			return false;

		// If hand has at least 3 matching cards, return true
		if ((hand.get(0).getFaceValue() == hand.get(2).getFaceValue())
				|| (hand.get(1).getFaceValue() == hand.get(3).getFaceValue())
				|| (hand.get(2).getFaceValue() == hand.get(4).getFaceValue())) {
			return true && !isFourOfAKind() && !isFullHouse();
		}

		// Otherwise, return false
		return false;
	}

	// Determines whether hand is a two pair (and not a more valuable hand)
	public boolean isTwoPair() {
		// If cards make a higher value hand containing matching cards, return
		// false
		if (isFourOfAKind() || isFullHouse() || isThreeOfAKind())
			return false;

		// Ensure hand contains at least 2 pairs - if so, return true
		if (((hand.get(0).getFaceValue() == hand.get(1).getFaceValue())
				&& ((hand.get(2).getFaceValue() == hand.get(3).getFaceValue())
						|| (hand.get(3).getFaceValue() == hand.get(4).getFaceValue())))
				|| ((hand.get(1).getFaceValue() == hand.get(2).getFaceValue())
						&& ((hand.get(3).getFaceValue() == hand.get(4).getFaceValue())))) {
			return true;
		}

		// Otherwise, return false
		return false;
	}

	// Returns boolean determining if the hand has One pair (and is not a more
	// valuable hand)
	public boolean isOnePair() {
		// If cards make a higher value hand containing matching cards, return
		// false
		if (isFourOfAKind() || isFullHouse() || isThreeOfAKind() || isTwoPair())
			return false;

		// Ensure hand contains at least 2 matching cards - if so, return true
		if ((hand.get(0).getFaceValue() == hand.get(1).getFaceValue())
				|| (hand.get(1).getFaceValue() == hand.get(2).getFaceValue())
				|| (hand.get(2).getFaceValue() == hand.get(3).getFaceValue())
				|| (hand.get(3).getFaceValue() == hand.get(4).getFaceValue())) {
			return true;
		}
		// Otherwise return false
		return false;
	}

	// Determines if the hand is only a high hand (by ensuring that it is not
	// any
	// of the other hands.
	public boolean isHighHand() {
		// If all other hand methods return false, then the hand is a high hand
		if (!isOnePair() && !isTwoPair() && !isThreeOfAKind() && !isFourOfAKind() && !isFullHouse()
				&& !containsStraight() && !containsFlush())
			return true;
		else
			return false;
	}

	// Determine whether the hand is a busted straight (1 card needed to make a
	// straight)
	public boolean isBustedStraight() {
		// Return false for any hand other than high hand, one pair or flush. -
		// All other hands either
		// contain a straight already, or would require more than 1 card to make
		// a straight
		if (isHighHand() || isOnePair() || isFlush()) {
			// Get lowest card
			int min = hand.get(HAND_SIZE - 1).getFaceValue();
			int prev = min;
			// Amount of 'straight' cards - 1 (including initial lowest card)
			int straightCards = 1;
			// For all other cards, if card is less than lowest card + 5,
			// increase straightCards
			// Check for cards of same value (do not include pairs as 2 cards)
			for (int i = HAND_SIZE - 2; i >= 0; i--) {
				if (hand.get(i).getFaceValue() != prev && hand.get(i).getFaceValue() < min + 5) {
					straightCards++;
					prev = hand.get(i).getFaceValue();
				}
			}
			// Check if ace can be low
			if (hand.get(0).getFaceValue() == 1 && min == 2)
				straightCards++;
			// Return true if 4 of 5 cards are straight cards
			if (straightCards == 4)
				return true;

			// Repeat, but with second lowest card.
			min = hand.get(HAND_SIZE - 2).getFaceValue();
			prev = min;
			straightCards = 1;
			// For all higher cards, if card is less than second lowest card +5,
			// increase straightCards
			// Check for cards of same value (do not include pairs as 2 cards)
			// *Use game value for high ace
			for (int i = HAND_SIZE - 3; i >= 0; i--) {
				if (hand.get(i).getFaceValue() != prev && hand.get(i).getGameValue() < min + 5) {
					straightCards++;
					prev = hand.get(i).getFaceValue();
				}
			}
			
			// *No need to check ace a second time.
			// Return true if 4 of 5 cards are straight cards, otherwise return
			// false.
			if (straightCards == 4)
				return true;
			else
				return false;
			
		} else {
			return false;
		}
	}

	// Determine whether the hand is a busted flush (1 card needed to make a
	// flush)
	public boolean isBustedFlush() {
		// Return false for any hand other than high hand, one pair or straight.
		// - All other hands either
		// contain a straight already, or would require more than 1 card to make
		// a straight
		if (isHighHand() || isOnePair() || isStraight()) {
			char suitOne = hand.get(0).getSuit();	// Get suit of first card
			char suitTwo = '\0';
			
			// Counters for cards of each suit
			int suitOneAmount = 1;
			int suitTwoAmount = 0;

			// For each card, if card suit is suit one or suit two, increase
			// appropriate counter
			// If card suit does not equal suit one, and suit two counter is
			// zero, assign this suit
			// to suit two, and increase counter. If a third suit is discovered,
			// return false.
			for (int i = 1; i < HAND_SIZE; i++) {
				char cardSuit = hand.get(0).getSuit();
				
				if (cardSuit == suitOne)	// Card suit is the same as suit one
					suitOneAmount += 1;
				else if (suitTwoAmount != 0 && cardSuit == suitTwo)	// Card suit is the same as suit two
					suitTwoAmount += 1;
				else if (suitTwoAmount == 0) {	// Card suit is not the same as suit one, and is the second / suit discovered
					suitTwo = cardSuit;
					suitTwoAmount += 1;
				}
				else return false;	// Card suit is neither suit one nor suit two - return false
			}
			
			// If there are exactly 4 cards of suit one or two, return true,
			// otherwise return false
			if (suitOneAmount == 4 || suitTwoAmount == 4)
				return true;
			else 
				return false;
			
		} else {
			return false;
		}
	}

	// Get card to discard in busted straight
	private int getBustedStraightCard() {
		
		if (!isBustedStraight()) return -1;	// If not a busted straight, cannot discard any card

		int max, ind;
		// Get the max card in the hand - assume to be highest card in straight
		// If straight is an ace low straight, take second highest (after ace)
		// and
		// start straight from there.
		if (hand.get(0).getFaceValue() == 1 && hand.get(HAND_SIZE - 1).getFaceValue() == 2) {
			max = hand.get(1).getFaceValue();
			ind = 2;
			// In ace low straight, ensure the highest card is within range of
			// ace
			// Otherwise discard that card.
			if (max > hand.get(0).getFaceValue() + 4) return 1;
		} 
		else {
			max = hand.get(0).getGameValue();
			ind = 1;
		}
		
		int prev = max;
		int discard = -1;
		// Check if each card is compatible with straight
		for (int i = ind; i < HAND_SIZE; i++) {
			// Continue if card is compatible
			if (hand.get(i).getFaceValue() != prev && hand.get(i).getFaceValue() > max - 5)
				prev = hand.get(i).getFaceValue();
			else {
				// If card does not make straight, and there is already a
				// discard value,
				// discard max card (incompatible with at least 2 cards).
				// Otherwise set
				// discard value to current card.
				if (discard != -1) return 0;
				else discard = i;
			}
		}
		return discard;
	}

	// Get card to discard in busted flush
	private int getBustedFlushCard() {
		// If not a busted straight, cannot discard any card
		if (!isBustedFlush()) return -1;
		
		char suit = hand.get(0).getSuit();
		int unmatched = 0;
		int discard = -1;
		for (int i = 1; i < HAND_SIZE; i++) {
			if (hand.get(i).getSuit() != suit) {
				discard = i;
				unmatched++;
			}
		}
		
		if (unmatched > 1) return 0;
		else return discard;

	}

	public int getGameValue() {
		// returns royal flush default
		if (isRoyalFlush()) return ROYAL_FLUSH_DEFAULT;

		// returns straight default + high card value
		else if (isStraightFlush()) {
			if (hand.get(0).getGameValue() == 14 && hand.get(1).getGameValue() == 5)
				return STRAIGHT_FLUSH_DEFAULT + 5;
			else
				return STRAIGHT_FLUSH_DEFAULT + hand.get(0).getGameValue();
		}

		// returns four of a kind default + value of four
		else if (isFourOfAKind()) {
			if (hand.get(0).getGameValue() == hand.get(1).getGameValue())
				return FOUR_OF_A_KIND_DEFAULT + hand.get(0).getGameValue();
			else
				return FOUR_OF_A_KIND_DEFAULT + hand.get(1).getGameValue();
		}

		// returns full house default + value of three
		else if (isFullHouse())
			return FULL_HOUSE_DEFAULT + hand.get(2).getGameValue();

		// returns flush default + first * 14^4 + second * 14^3 and so on
		else if (isFlush()) {
			return FLUSH_DEFAULT + hand.get(0).getGameValue() * FOURTEEN_FOURTH
					+ hand.get(1).getGameValue() * FOURTEEN_THIRD + hand.get(2).getGameValue() * FOURTEEN_SECOND
					+ hand.get(3).getGameValue() * FOURTEEN_FIRST + hand.get(4).getGameValue();
		}

		// returns straight default + value of high card
		else if (isStraight()) {
			if (hand.get(0).getGameValue() == 14 && hand.get(2).getGameValue() == 5)
				return STRAIGHT_DEFAULT + hand.get(1).getGameValue() * FOURTEEN_FOURTH
						+ hand.get(2).getGameValue() * FOURTEEN_THIRD + hand.get(3).getGameValue() * FOURTEEN_SECOND
						+ hand.get(4).getGameValue() * FOURTEEN_FIRST + hand.get(0).getGameValue();
			else
				return STRAIGHT_DEFAULT + hand.get(0).getGameValue() * FOURTEEN_FOURTH
						+ hand.get(1).getGameValue() * FOURTEEN_THIRD + hand.get(2).getGameValue() * FOURTEEN_SECOND
						+ hand.get(3).getGameValue() * FOURTEEN_FIRST + hand.get(4).getGameValue();
		}

		// returns three of a kind default + value of three
		else if (isThreeOfAKind())
			return THREE_OF_A_KIND_DEFAULT + hand.get(2).getGameValue();

		// returns two pair default + higher pair * 14^2 + lower paid * 14^1 +
		// remaining card value
		else if (isTwoPair()) {
			if (hand.get(0).getGameValue() == hand.get(1).getGameValue()) {
				if (hand.get(2).getGameValue() == hand.get(3).getGameValue())
					return TWO_PAIR_DEFAULT + hand.get(0).getGameValue() * FOURTEEN_SECOND
							+ hand.get(2).getFaceValue() * FOURTEEN_FIRST + hand.get(4).getGameValue();
				else
					return TWO_PAIR_DEFAULT + hand.get(0).getGameValue() * FOURTEEN_SECOND
							+ hand.get(3).getFaceValue() * FOURTEEN_FIRST + hand.get(2).getGameValue();
			} else
				return TWO_PAIR_DEFAULT + hand.get(1).getGameValue() * FOURTEEN_SECOND
						+ hand.get(3).getFaceValue() * FOURTEEN_FIRST + hand.get(0).getGameValue();
		}

		// returns pair + 14^3 + highest card * 14^2 and so on
		else if (isOnePair()) {
			if (hand.get(0).getGameValue() == hand.get(1).getGameValue())
				return ONE_PAIR_DEFAULT + hand.get(0).getGameValue() * FOURTEEN_THIRD
						+ hand.get(2).getGameValue() * FOURTEEN_SECOND + hand.get(3).getGameValue() * FOURTEEN_FIRST
						+ hand.get(4).getGameValue();
			else if (hand.get(1).getGameValue() == hand.get(2).getGameValue())
				return ONE_PAIR_DEFAULT + hand.get(1).getGameValue() * FOURTEEN_THIRD
						+ hand.get(0).getGameValue() * FOURTEEN_SECOND + hand.get(3).getGameValue() * FOURTEEN_FIRST
						+ hand.get(4).getGameValue();
			else if (hand.get(2).getGameValue() == hand.get(3).getGameValue())
				return ONE_PAIR_DEFAULT + hand.get(2).getGameValue() * FOURTEEN_THIRD
						+ hand.get(0).getGameValue() * FOURTEEN_SECOND + hand.get(1).getGameValue() * FOURTEEN_FIRST
						+ hand.get(4).getGameValue();
			else
				return ONE_PAIR_DEFAULT + hand.get(3).getGameValue() * FOURTEEN_THIRD
						+ hand.get(0).getGameValue() * FOURTEEN_SECOND + hand.get(1).getGameValue() * FOURTEEN_FIRST
						+ hand.get(2).getGameValue();
		}

		// otherwise returns high hand default + first * 14^4 and so on
		else
			return HIGH_HAND_DEFAULT + hand.get(0).getGameValue() * FOURTEEN_FOURTH
					+ hand.get(1).getGameValue() * FOURTEEN_THIRD + hand.get(2).getGameValue() * FOURTEEN_SECOND
					+ hand.get(3).getGameValue() * FOURTEEN_FIRST + hand.get(4).getGameValue();
	}

	// Return the discard probability for a card in a high hand
	private int getDiscardProbHighHand(int cardPos) {
		boolean potentialFlush = isBustedFlush();
		boolean potentialStraight = isBustedStraight();
		int cardGameVal = hand.get(cardPos).getGameValue();
		
		// Normal High Hand
		if (!potentialFlush && !potentialStraight) {
			// Discard most cards, but keep valuable cards (like ace, king)
			return (int) (100 - (Math.pow(cardGameVal, 3) / 28));
			// Busted flush
		} else if (potentialFlush) {
			if (cardPos == getBustedFlushCard()) {
				return 100;
			} else {
				return 14 - cardGameVal;
			}
			// Busted straight
		} else {
			if (cardPos == getBustedStraightCard())
				return 100;
			else
				return 40 - cardGameVal;
		}
	}

	// Return the discard probability for a card in a one pair hand
	private int getDiscardProbOnePair(int cardPos) {
		boolean potentialFlush = isBustedFlush();
		boolean potentialStraight = isBustedStraight();
		int cardGameVal = hand.get(cardPos).getGameValue();
		// Normal hand
		if (!potentialFlush && !potentialStraight) {
			// Prioritize discarding any card that is not in the pair
			if (cardPos + 1 < HAND_SIZE && hand.get(cardPos + 1).getGameValue() == cardGameVal) {
				return 0;
			} else if (cardPos - 1 >= 0 && hand.get(cardPos - 1).getGameValue() == cardGameVal) {
				return 0;
			} else {
				// Attempt to make a set/pairs; Can afford to keep higher cards.
				// Weighted discard (Ace is 0%, 10 is 77%, 7 is 100%)
				return (int) (100 - (Math.pow(cardGameVal, 4) / 300)) + 10;
			}
			// Busted flush
		} else if (potentialFlush) {
			if (cardPos == getBustedFlushCard())
				return 45;
			else
				return 20 - cardGameVal;
			// Busted straight
		} else {
			if (cardPos == getBustedStraightCard()) {
				// Better odds if straight card is on end
				if (cardPos == 0 || cardPos == HAND_SIZE - 1)
					return 40;
				else
					return 20;
			} else {
				return 20 - cardGameVal;
			}
		}
	}

	// Return the discard probability for a card in a two pair hand
	private int getDiscardProbTwoPair(int cardPos) {
		int cardGameVal = hand.get(cardPos).getGameValue();
		// Prioritize discarding any card that is not in the pair
		if (cardPos + 1 < HAND_SIZE && hand.get(cardPos + 1).getGameValue() == cardGameVal)
			return 0;
		else if (cardPos - 1 >= 0 && hand.get(cardPos - 1).getGameValue() == cardGameVal)
			return 0;
		else {
			// Attempting to make a full house, still worth keeping high kickers
			// Ace: 13%, King: 45%, Queen: 70%, Jack: 87%, 10: 100%
			return (int) (100 - (Math.pow(cardGameVal, 5) / 5000)) + 20;
		}
	}

	// Return the discard probability for a card in a three of a kind hand
	private int getDiscardProbThreeOfAKind(int cardPos) {
		int cardGameVal = hand.get(cardPos).getGameValue();
		// Keep all cards in the set
		if (cardPos + 2 < HAND_SIZE && hand.get(cardPos + 2).getGameValue() == cardGameVal)
			return 0;
		else if (cardPos - 2 >= 0 && hand.get(cardPos - 2).getGameValue() == cardGameVal)
			return 0;
		else if (cardPos - 1 >= 0 && cardPos + 1 < HAND_SIZE
				&& hand.get(cardPos - 1).getGameValue() == hand.get(cardPos + 1).getGameValue()) {
			return 0;
		} else {
			// Attempting to make a full house, still worth keeping high kickers
			// Ace: 13%, King: 45%, Queen: 70%, Jack: 87%, 10: 100%
			return (int) (100 - (Math.pow(cardGameVal, 5) / 5000)) + 20;
		}
	}

	// Return the discard probability for a card in a straight (0 unless flush
	// can be made)
	private int getDiscardProbStraight(int cardPos) {
		if (isBustedFlush() && cardPos == getBustedFlushCard()) {
			// System.out.println("H");
			// Return approximate probability of making the flush (about 20%)
			double p = ((double) (CARD_TYPES - 4) / (DECK_SIZE - 5)) * 100;
			return (int) p;
		} 
		else return 0;
	}

	// Return the discard probability for a card in a flush
	private int getDiscardProbFlush(int cardPos) {
		if (isBustedStraight() && cardPos == getBustedStraightCard()) {
			// Return approximate probability of making a straight flush (very
			// low)
			double p = ((double) 1 / (DECK_SIZE - 5)) * 100;
			return (int) p;
		} 
		else return 0;
	}

	// Return the discard probability for a card in a four of a kind hand
	private int getDiscardProbFourOfAKind(int cardPos) {
		int cardGameVal = hand.get(cardPos).getGameValue();
		// Keep all cards in the four of a kind
		if (cardPos + 1 < HAND_SIZE && hand.get(cardPos + 1).getGameValue() == cardGameVal)
			return 0;
		else if (cardPos - 1 >= 0 && hand.get(cardPos - 1).getGameValue() == cardGameVal)
			return 0;
		else {
			// Discard most cards to bluff, keep higher cards from other players
			// Ace: 13%, King: 45%, Queen: 70%, Jack: 87%, 10: 80%
			return (int) (100 - (Math.pow(cardGameVal, 5) / 5000)) + 20;
		}
	}

	// Returns integer representing whether the card should be discarded
	public int getDiscardProbability(int cardPosition) {
		// Return 0 if cardPosition outside range/card is null
		if (cardPosition < 0 || cardPosition > HAND_SIZE) return 0;
		
		int discard = 0;
		
		if (isHighHand()) 			discard = getDiscardProbHighHand(cardPosition);
		else if (isOnePair()) 		discard = getDiscardProbOnePair(cardPosition);
		else if (isTwoPair()) 		discard = getDiscardProbTwoPair(cardPosition);
		else if (isThreeOfAKind())	discard = getDiscardProbThreeOfAKind(cardPosition);
		else if (isStraight())		discard = getDiscardProbStraight(cardPosition);
		else if (isFlush()) 		discard = getDiscardProbFlush(cardPosition);
		else if (isFullHouse()) 	return 0;
		else if (isFourOfAKind()) 	discard = getDiscardProbFourOfAKind(cardPosition);
		else if (isStraightFlush()) return 0;
		else return 0;
		
		if (discard > 100) 		
			discard = 100;
		else if (discard < 0)	
			discard = 0;
		
		return discard;
	}
	
	//Returns a card at an index to the deck, and deals a new card.
	public void discard(int index){
		//Return card
		deck.returnCard(hand.get(index));
		hand.remove(index);
		//Deal new card
		hand.add(deck.dealNext());
		sort();
	}

	// for testing purposes
	public String toString() {
		String output = "";
		for (int i = 0; i < 5; i++)
			output += hand.get(i).toString() + " ";
		return output;
	}

	// for testing purposes
	public String getHandType() {
		if (isRoyalFlush())
			return "Royal Flush\t";
		else if (isStraightFlush())
			return "Straight Flush\t";
		else if (isFourOfAKind())
			return "Four of a Kind\t";
		else if (isFullHouse())
			return "Full House\t";
		else if (isFlush())
			return "Flush\t\t";
		else if (isStraight())
			return "Straight\t";
		else if (isThreeOfAKind())
			return "Three of a Kind\t";
		else if (isTwoPair())
			return "Two Pair\t";
		else if (isOnePair())
			return "One Pair\t";
		else
			return "High Hand\t";
	}

}
