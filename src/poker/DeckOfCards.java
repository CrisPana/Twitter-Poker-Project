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
import java.util.Random;

/**
 * Defines a standard deck of cards using an {@link ArrayList} of {@link PlayingCard}
 * objects. Contains functionality for shuffling the deck, dealing cards and returning
 * cards to the deck.
 * @author Dara Callinan
 * @author Jazheel Luna
 * @author Eoghan O'Donnell
 * @author Crischelle Pana
 */
public class DeckOfCards {
	
	static public final int SIZE_OF_DECK = 52; // constant for size of deck
	static public final int CARDS_IN_SUIT = 13;
	
	private ArrayList<PlayingCard> deck = new ArrayList<PlayingCard>(); // deck
	private int dealt;

	/**
	 * Class constructor. Calls {@link #reset()} which assigns a new list of {@link PlayingCard}
	 * objects to the {@link #deck} list.
	 */
	public DeckOfCards() {
		reset();
	}

	/**
	 * Resets the deck by constructing a brand new deck of {@link PlayingCard playing cards}.
	 */
	public synchronized void reset() {
		dealt = 0;
		deck.clear();
		for (int i = 0; i < CARDS_IN_SUIT; i++) { // iterates through card types
			for (int j = 0; j < 4; j++) { // iterates through suits
				if (i == 0)
					deck.add(new PlayingCard(PlayingCard.TYPES[i], PlayingCard.SUITS[j], 1, 14));
				else
					deck.add(new PlayingCard(PlayingCard.TYPES[i], PlayingCard.SUITS[j], i + 1, i + 1));
			}
		}
		shuffle(); // shuffles deck
	}

	/**
	 * Shuffles the deck. Two random cards in the deck have their positions swapped.
	 * This is done as many times as the square of the size of the deck.
	 */
	public void shuffle() {

		Random rand = new Random();

		for (int i = 0; i <= SIZE_OF_DECK * SIZE_OF_DECK; i++) {
			int randomNum1 = rand.nextInt(SIZE_OF_DECK);
			int randomNum2 = rand.nextInt(SIZE_OF_DECK);
			Collections.swap(deck, randomNum1, randomNum2);
		}
	}
	
	/**
	 * Removes the first card from the deck and returns it.
	 * @return The first {@link PlayingCard} object in the {@link #deck} list.
	 */
	public synchronized PlayingCard dealNext() {
		synchronized (deck) {
			PlayingCard nextCard = deck.remove(0);
			dealt++;

			if (dealt == 52)
				reset();

			return nextCard;
		}
	}

	/**
	 * Returns a card to the deck.
	 * @param discarded   The discarded card to be returned to the deck.
	 */
	public synchronized void returnCard(PlayingCard discarded) {
		synchronized (deck) {
			deck.add(discarded);
		}
	}
	
	public String toString(){
		String output = "";
		for(int i=0;i<deck.size();i++) output += deck.get(i).toString() + " ";
		return output;
	}
}
