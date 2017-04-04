import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import poker.PlayingCard;
public class DeckOfCards{	//deck of cards class
	static public final int SIZE_OF_DECK = 52;	//constant for size of deck
	static public final int CARDS_IN_SUIT = 13;
	private ArrayList<PlayingCard> deck = new ArrayList<PlayingCard>(); //deck array list
	private int dealingLocation;								//stores next card to be dealt
	private int returnLocation;									//stores next position card can be returned to after being discarded
	private boolean endOfDeck=false;
	
	public DeckOfCards(){										//constructor
	}
	
	public void reset(){										//resets/initialises new deck
		dealingLocation = 0;									//resets pointers to 0
		returnLocation = 0;
		for(int i=0;i<CARDS_IN_SUIT;i++){						//iterates through card types
			for(int j=0;j<4;j++){					//iterates through suits
				if(i==0) deck.add(new PlayingCard(PlayingCard.TYPES[i],PlayingCard.SUITS[j],1,14));
				else deck.add(new PlayingCard(PlayingCard.TYPES[i],PlayingCard.SUITS[j],i+1,i+1));
			}
		}
		shuffle();														//shuffles deck
	}
	//swaps two random positions in the deck
		public void shuffle() {
			
			Random rand = new Random();
			
			for (int i = 0 ; i <= SIZE_OF_DECK * SIZE_OF_DECK; i++) {
				int randomNum1 = rand.nextInt(SIZE_OF_DECK);
				int randomNum2 = rand.nextInt(SIZE_OF_DECK);
				Collections.swap(deck, randomNum1, randomNum2);
			}		
		}
		
	public synchronized PlayingCard dealNext(){										//method to deal a card
		synchronized (deck){
			dealingLocation++;												//increments pointer
			if(dealingLocation<=SIZE_OF_DECK&&!endOfDeck) return deck.get(dealingLocation-1);		//if within range of deck, returns card
			else if(!endOfDeck){
				endOfDeck=true;
				dealingLocation=1;
			}
			if(dealingLocation<=returnLocation&&endOfDeck) return deck.get(dealingLocation-1);
			else return null;				//otherwise returns null
		}
	}
	public synchronized void returnCard(PlayingCard discarded){						//method to "return" discarded card
		synchronized (deck){
			deck.add(returnLocation, discarded);
			System.out.println("returning " + discarded + " | " +deck.get(returnLocation).toString());
			returnLocation++;												//increments pointer to next free position
		}
	}
}

