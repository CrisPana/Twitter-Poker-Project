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
import java.util.Collections;
import java.util.Comparator;

/**
 * A standard hand of playing cards. The size of the hand is given by {@link #HAND_SIZE}. The class contains an
 * {@link ArrayList} of {@link PlayingCard playing cards} and a reference to the {@link DeckOfCards deck} that
 * cards are dealt from. The class contains functionality for {@link #sort sorting} the hand based on card value,
 * determining the best type of poker hand that can be formed, {@link #getGameValue assigning a score} to the hand
 * and calculating a {@link #getDiscardProbability discard probability} for a card in the hand.
 * @author Dara Callinan
 * @author Jazheel Luna
 * @author Eoghan O'Donnell
 * @author Crischelle Pana
 */
public class HandOfCards {

	public static final int HAND_SIZE = 5;
	private static final int DECK_SIZE = 52;
	private static final int CARD_TYPES = 13;
	public static final int HIGH_HAND_DEFAULT = 0; // defaults are multiples of one million for easy identification
	public static final int ONE_PAIR_DEFAULT = 1000000;
	public static final int TWO_PAIR_DEFAULT = 2000000;
	public static final int THREE_OF_A_KIND_DEFAULT = 3000000;
	public static final int STRAIGHT_DEFAULT = 4000000;
	public static final int FLUSH_DEFAULT = 5000000;
	public static final int FULL_HOUSE_DEFAULT = 6000000;
	public static final int FOUR_OF_A_KIND_DEFAULT = 7000000;
	public static final int STRAIGHT_FLUSH_DEFAULT = 8000000;
	public static final int ROYAL_FLUSH_DEFAULT = 9000000;
	public static final int FOURTEEN_FIRST = 14; // multiplication constants are powers of 14 (as value of ace is 14)
	public static final int FOURTEEN_SECOND = 196; // they could be lower but this is a very easy way to do and it won't affect calculation time
	public static final int FOURTEEN_THIRD = 2744;
	public static final int FOURTEEN_FOURTH = 38416;
	
	private DeckOfCards deck;
	private ArrayList<PlayingCard> hand = new ArrayList<PlayingCard>();

	/**
	 * Class constructor. Initialises the {@link #hand} by dealing {@link PlayingCard playing cards}.
	 * @param deck   The deck from which cards will be dealt.
	 */
	public HandOfCards(DeckOfCards deck) {
		this.deck = deck;
		int i = 0;
		while (i != HAND_SIZE) {
			hand.add(deck.dealNext());
			i++;
		}
		sort();
	}

	/**
	 * Gets the {@link DeckOfCards deck} from which this hand was dealt.
	 * @return The {@link DeckOfCards deck} which dealt this hand.
	 */
	public DeckOfCards returnDeck() {
		return deck;
	}

	/**
	 * Sorts the hand based on the {@link PlayingCard card} game values.
	 */
	private void sort() {
		Collections.sort(hand, new Comparator<PlayingCard>() {
			@Override
			public int compare(PlayingCard card1, PlayingCard card2) {
				return card2.getGameValue() - card1.getGameValue();
			}
		});
	}

	/**
	 * Checks if the hand contains a straight.
	 * @return A {@code boolean}, true if the hand contains a straight, false otherwise.
	 */
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

	/**
	 * Checks if the hand contains a flush.
	 * @return A {@code boolean}, true if the hand contains a flush, false otherwise.
	 */
	private boolean containsFlush() {
		char suit = hand.get(0).getSuit();

		for (int i = 1; i < HAND_SIZE; i++) {
			if (hand.get(i).getSuit() != suit)
				return false;
		}

		return true;
	}

	/**
	 * Checks if the hand is a royal flush.
	 * @return A {@code boolean}, true if the hand is a royal flush, false otherwise.
	 */
	public boolean isRoyalFlush() {
		boolean hasAceKing = hand.get(0).getFaceValue() == 1 && hand.get(1).getFaceValue() == 13;
		return containsStraight() && containsFlush() && hasAceKing;
	}

	/**
	 * Checks if the hand is a straight flush but not a royal flush.
	 * @return A {@code boolean}, true if the hand is a straight flush, false otherwise.
	 */
	public boolean isStraightFlush() {
		boolean hasAceKing = hand.get(0).getFaceValue() == 1 && hand.get(1).getFaceValue() == 13;
		return containsStraight() && containsFlush() && !hasAceKing;
	}

	/**
	 * Checks if the hand is a four of a kind and not a more valuable hand.
	 * @return A {@code boolean}, true if the hand is a four of a kind, false otherwise.
	 */
	public boolean isFourOfAKind() {
		if (hand.get(0).getFaceValue() == hand.get(3).getFaceValue()
				|| hand.get(1).getFaceValue() == hand.get(4).getFaceValue())
			return true;
		else
			return false;
	}

	/**
	 * Checks if the hand is a full house and not a more valuable hand.
	 * @return A {@code boolean}, true if the hand is a full house, false otherwise.
	 */
	public boolean isFullHouse() {
		if ((hand.get(0).getFaceValue() == hand.get(2).getFaceValue()
				&& hand.get(3).getFaceValue() == hand.get(4).getFaceValue())
				|| (hand.get(0).getFaceValue() == hand.get(1).getFaceValue()
						&& hand.get(2).getFaceValue() == hand.get(4).getFaceValue()))
			return true;
		else
			return false;
	}

	/**
	 * Checks if the hand is a flush and not a more valuable hand.
	 * @return A {@code boolean}, true if the hand is a flush, false otherwise.
	 */
	public boolean isFlush() {
		return containsFlush() && !containsStraight();
	}

	/**
	 * Checks if the hand is a straight and not a more valuable hand.
	 * @return A {@code boolean}, true if the hand is a straight, false otherwise.
	 */
	public boolean isStraight() {
		return containsStraight() && !containsFlush();
	}

	/**
	 * Checks if the hand is a three of a kind and not a more valuable hand.
	 * @return A {@code boolean}, true if the hand is a three of a kind, false otherwise.
	 */
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

	/**
	 * Checks if the hand is a two pair and not a more valuable hand.
	 * @return A {@code boolean}, true if the hand is a two pair, false otherwise.
	 */
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

	/**
	 * Checks if the hand is a one pair and not a more valuable hand.
	 * @return A {@code boolean}, true if the hand is a one pair, false otherwise.
	 */
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

	/**
	 * Checks if the hand is a high hand and not a more valuable hand.
	 * @return A {@code boolean}, true if the hand is a high hand, false otherwise.
	 */
	public boolean isHighHand() {
		// If all other hand methods return false, then the hand is a high hand
		if (!isOnePair() && !isTwoPair() && !isThreeOfAKind() && !isFourOfAKind() && !isFullHouse()
				&& !containsStraight() && !containsFlush())
			return true;
		else
			return false;
	}

	/**
	 * Checks if the hand is a busted straight (One card needed to make a straight).
	 * @return A {@code boolean}, true if the hand is a busted straight, false otherwise.
	 */
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

	/**
	 * Checks if the hand is a busted flush (One card needed to make a flush).
	 * @return A {@code boolean}, true if the hand is a busted straight, false otherwise.
	 */
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

	/**
	 * Gets the position of the {@link PlayingCard card} that breaks the straight if one exists, otherwise returns -1.
	 * @return The index of the broken straight card in the {@link #hand}.
	 */
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

	/**
	 * Gets the position of the {@link PlayingCard card} that breaks the flush if one exists, otherwise returns -1.
	 * @return The index of the broken straight card in the {@link #hand}.
	 */
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

	/**
	 * Calculates the game value for the hand. This value is calculated by determining the type
	 * of the hand and relating that to a constant amount. Then depending on the hand type,
	 * value is added based on the values of the {@link PlayingCard cards} in various ways.
	 * The value is constructed such that a better type of hand will always have a higher game
	 * value than a lower type of hand, and individual cards are weighted such that the largest
	 * groups of cards are weighted highest and cards are weighted in order of decreasing game
	 * value, as in standard poker rules.
	 * @return An {@code int}, the game value of the hand.
	 */
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
	
	/**
	 * Calculates the discard probability for a card at the specified position in a high hand.
	 * @param cardPos   The position of the card to check.
	 * @return An {@code int} representing the probability of discarding the card.
	 * @see #getDiscardProbability(int)
	 */
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
	
	/**
	 * Calculates the discard probability for a card at the specified position in a one pair hand.
	 * @param cardPos   The position of the card to check.
	 * @return An {@code int} representing the probability of discarding the card.
	 * @see #getDiscardProbability(int)
	 */
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
	
	/**
	 * Calculates the discard probability for a card at the specified position in a two pair hand.
	 * @param cardPos   The position of the card to check.
	 * @return An {@code int} representing the probability of discarding the card.
	 * @see #getDiscardProbability(int)
	 */
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
	
	/**
	 * Calculates the discard probability for a card at the specified position in a three of a kind hand.
	 * @param cardPos   The position of the card to check.
	 * @return An {@code int} representing the probability of discarding the card.
	 * @see #getDiscardProbability(int)
	 */
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
	
	/**
	 * Calculates the discard probability for a card at the specified position in a straight.
	 * This will always be zero unless a flush can be made, as it is too difficult to make any
	 * other higher value hands.
	 * @param cardPos   The position of the card to check.
	 * @return An {@code int} representing the probability of discarding the card.
	 * @see #getDiscardProbability(int)
	 */
	private int getDiscardProbStraight(int cardPos) {
		if (isBustedFlush() && cardPos == getBustedFlushCard()) {
			// System.out.println("H");
			// Return approximate probability of making the flush (about 20%)
			double p = ((double) (CARD_TYPES - 4) / (DECK_SIZE - 5)) * 100;
			return (int) p;
		} 
		else return 0;
	}
	
	/**
	 * Calculates the discard probability for a card at the specified position in a flush.
	 * Can return a small non-zero chance if a straight flush can be made.
	 * @param cardPos   The position of the card to check.
	 * @return An {@code int} representing the probability of discarding the card.
	 * @see #getDiscardProbability(int)
	 */
	private int getDiscardProbFlush(int cardPos) {
		if (isBustedStraight() && cardPos == getBustedStraightCard()) {
			// Return approximate probability of making a straight flush (very
			// low)
			double p = ((double) 1 / (DECK_SIZE - 5)) * 100;
			return (int) p;
		} 
		else return 0;
	}
	
	/**
	 * Calculates the discard probability for a card at the specified position in a four of a kind.
	 * @param cardPos   The position of the card to check.
	 * @return An {@code int} representing the probability of discarding the card.
	 * @see #getDiscardProbability(int)
	 */
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
	
	/**
	 * Calculates an integer in the range {@code 0 - 100} which represents the probability of a player
	 * discarding the {@link PlayingCard card} at the specified position.
	 * @param cardPosition   The position of the card to check.
	 * @return The probability of discarding the card at this position.
	 */
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
	
	//Returns cards at an index to the deck, and deals new cards.
	/**
	 * Returns {@link PlayingCard cards} at the specified indices to the {@link #deck deck}, and deals
	 * new cards to replace them. Sorts the hand once the cards have been dealt.
	 * @param indices   An array of positions of the cards in the {@link #hand hand} to be discarded. 
	 * @param amountToDiscard   The amount of cards to be discarded.
	 */
	public void discard(int[] indices, int amountToDiscard){
		for(int i=0; i<amountToDiscard; i++){
			//Return card
			deck.returnCard(hand.get(indices[i]));
			hand.remove(indices[i]);
			//Deal new card
			hand.add(indices[i], deck.dealNext());
		}
		sort();
	}
	
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
