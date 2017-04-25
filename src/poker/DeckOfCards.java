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

public class DeckOfCards { // deck of cards class
	
	static public final int SIZE_OF_DECK = 52; // constant for size of deck
	static public final int CARDS_IN_SUIT = 13;
	private ArrayList<PlayingCard> deck = new ArrayList<PlayingCard>(); // deck
	private int dealt;

	public DeckOfCards() {
		reset();
	}

	public void reset() {
		dealt = 0;
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

	// swaps two random positions in the deck
	public void shuffle() {

		Random rand = new Random();

		for (int i = 0; i <= SIZE_OF_DECK * SIZE_OF_DECK; i++) {
			int randomNum1 = rand.nextInt(SIZE_OF_DECK);
			int randomNum2 = rand.nextInt(SIZE_OF_DECK);
			Collections.swap(deck, randomNum1, randomNum2);
		}
	}

	/* this method removes the first card from the deck 
	 * and increments the dealt counter
	 * if dealt hits 52 we reset the deck
	 * */
	public synchronized PlayingCard dealNext() {
		synchronized (deck) {
			PlayingCard nextCard = deck.remove(0);
			dealt++;

			if (dealt == 52)
				reset();

			return nextCard;
		}
	}

	public synchronized void returnCard(PlayingCard discarded) {
		synchronized (deck) {
			deck.add(discarded);
		}
	}
}
