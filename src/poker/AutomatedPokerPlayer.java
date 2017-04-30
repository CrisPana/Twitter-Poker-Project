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
 * An subclass of {@link PokerPlayer} that forms an AI player capable of deciding
 * its actions with influence of an acquired nature.
 * @author Dara Callinan
 * @author Jazheel Luna
 * @author Eoghan O'Donnell
 * @author Crischelle Pana
 */
public class AutomatedPokerPlayer extends PokerPlayer{
	
	static public final int NUMBER_OF_PERSONALITIES = 3;
	static private final int LOW_BET = 1;			//Standard low bet multiplier (1 times the minimum bet)
	static private final int MID_BET = 2;			//Standard mid bet multiplier
	static private final int HIGH_BET = 4;			//Standard high bet multiplier
	static private final int STEAL_POT_CHANCE = 25;	//Chance to attempt to steal a pot (high bet) with a good hand
	static private final int BASE_BLUFF_CHANCE = 25;//Base chance for player to bluff
	//Blind/Ante/Round values for playable hands (a good high hand is worth a big blind)
	static private final double LOW_HIGH_HAND_BLIND_VALUE = 0.5;
	static private final double HIGH_HAND_BLIND_VALUE = 1;
	static private final double LOW_PAIR_BLIND_VALUE = 1.2;
	static private final double HIGH_PAIR_BLIND_VALUE = 1.6;
	static private final double TWO_PAIR_BLIND_VALUE = 2;
	static private final double SET_BLIND_VALUE = 4;
	static private final double MIDDLE_BLIND_VALUE = 6;
	static private final double TOP_BLIND_VALUE = 20;
	//Discard Values
	static public final int MAX_DISCARD = 3;
	static private final int MINIMUM_DISCARD_CHANCE = 0;
	static private final int MAXIMUM_DISCARD_CHANCE = 100;
	
	/** The chance that the AI will bluff when they have a bad hand. */
	private int bluffChance;
	/** The {@link HandOfCards#getDiscardProbability discard probability} that the player will ignore lower values of. */
	private int discardModifier;
	/** The amount of big blinds or antes that the player will try to preserve when betting. */
	private int inRed;
	/** The upper limit for generating hand values and bets. */
	private double upperBetModifier = 1.2;
	/** The lower limit for generating hand values and bets. */
	private double lowerBetModifier = 1.0;
	
	/**
	 * Class constructor. Calls the {@link PokerPlayer} {@link PokerPlayer#PokerPlayer constructor}
	 * and generates a random personality for the player.
	 * @param name
	 * @param deck   The {@link DeckOfCards deck} of cards being used to deal the player's hand.
	 * @see #generatePersonality(int) generatePersonality
	 */
	AutomatedPokerPlayer(String name, DeckOfCards deck) {
		super(name, deck);
		Random rand = new Random();
		int temp = rand.nextInt(NUMBER_OF_PERSONALITIES);
		this.generatePersonality(temp);
	}
	
	//Determines a betting value based on a blind/ante as a base value
	/**
	 * Determines a betting value for a player's hand. First, the hand is given a value
	 * in terms of how many big blinds/antes it is worth, then that value is multiplied
	 * by the blind value in chips.
	 * @param blind   The big blind.
	 * @return An {@code int} representing the betting value for the hand.
	 */
	private int handBetValue(int blind){
		int handVal = hand.getGameValue();
		double mod;
		if(handVal<HandOfCards.ONE_PAIR_DEFAULT){ //High hand
			if(handVal < (11*HandOfCards.FOURTEEN_FOURTH)){ //No face cards
				mod = LOW_HIGH_HAND_BLIND_VALUE;
			} else { //At least one face card
				mod = HIGH_HAND_BLIND_VALUE;
			}
		} else if(handVal<HandOfCards.TWO_PAIR_DEFAULT){		//One pair
			if(handVal < (10*HandOfCards.FOURTEEN_THIRD)){		//Pair is lower than 10
				mod = LOW_PAIR_BLIND_VALUE;
			} else {
				mod = HIGH_PAIR_BLIND_VALUE;
			}
		} else if(handVal<HandOfCards.THREE_OF_A_KIND_DEFAULT){	//Two pair
			mod = TWO_PAIR_BLIND_VALUE;
		} else if(handVal<HandOfCards.STRAIGHT_DEFAULT){		//Three of a kind
			mod = SET_BLIND_VALUE;
		} else if(handVal<HandOfCards.FOUR_OF_A_KIND_DEFAULT){	//Full house, straight, flush
			mod = MIDDLE_BLIND_VALUE;
		} else { //Four of a kind, straight flush, royal flush
			mod = TOP_BLIND_VALUE;
		}
		return (int) (blind * mod);
	}
	
	/**
	 * Gets the amount of chips the player is willing to bet in addition to the amount they
	 * need to call. The final amount is based on a bet multiplier and the minimum bet. The
	 * number is then influenced by a random value in the range of the player's
	 * {@link #lowerBetModifier} and {@link #upperBetModifier}. The player will attempt to
	 * conserve chips if they are nearing bankruptcy, so they can pay blinds for future
	 * rounds if necessary.
	 * @param toCall   The amount needed for the player to call the current bet.
	 * @param minimumBet   The minimum amount that can be bet or raised.
	 * @param blind   The big blind.
	 * @param betMult   The minimum bet multiplier to generate a base value.
	 * @return The amount of chips to be bet.
	 */
	private int getBettingChips(int toCall, int minimumBet, int blind, int betMult){
		int available = getChips() - toCall;		//Available betting chips
		int minBet = Math.max(minimumBet, blind/2);	//Lowest bet is big blind
		int base = minBet * betMult;				//Base bet
		
		//Try to save blinds in case round is lost
		for(int i=0; i<inRed && available > base && available-blind> minBet; i++){
			available -= blind;
		}
		
		//Bet range
		int lowerBet = (int) (base * lowerBetModifier);
		int upperBet = (int) (base * upperBetModifier);
		if(upperBet < available){			//Full bet range
			Random rand = new Random();
			return lowerBet + rand.nextInt(upperBet - lowerBet + 1);
		} else if(lowerBet < available){	//Limited bet range
			Random rand = new Random();
			return lowerBet + rand.nextInt(available - lowerBet + 1);
		} else { //Not enough chips for a full bet, return available betting chips.
			return available;
		}
	}
	
	/**
	 * Generates a value to bet or raise by if playing a good hand. Depending on the hand value
	 * and the nature of the player, the player can bet varying amounts to greaten the pot or
	 * fold players, bluff a low value hand by calling or checking, or try to steal the pot
	 * with a very large bet.
	 * @param toCall   The amount of chips needed to call the current bet.
	 * @param handVal   The value of the player's {@link PokerPlayer#hand hand}.
	 * @param betAmount   The current total bet amount.
	 * @param minimumBet   The minimum amount of chips that can be bet or raised.
	 * @param blind   The big blind.
	 * @return The amount to bet.
	 */
	private int decideBetValue(int toCall, int handVal, int betAmount, int minimumBet, int blind){
		int available = getChips() - toCall; //Available chips
		int remainingBlinds = toCall / blind;
		//Random value for decisions
		Random rand = new Random();
		int r = (int) (rand.nextDouble() * 100);
		
		if(handVal<3*betAmount){//Low hand value - low bet, high bluff, low bluff
			if(r<bluffChance){	//Bluff - 50% for a high hand bluff, 50% for low hand bluff
				if(rand.nextInt(2)==0){
					return 0;
				} else {
					return getBettingChips(toCall, minimumBet, blind, MID_BET);
				}
			} else { 			//Low bet
				return getBettingChips(toCall, minimumBet, blind, LOW_BET);
			}
		} if(handVal<5*betAmount){//Medium hand value - medium sized bet or a lower hand bluff
			if(r<bluffChance){	//Bluff - Bluff a lower hand with a low bet or call
				if(rand.nextInt(2)==0){
					return 0;
				} else {
					return getBettingChips(toCall, minimumBet, blind, LOW_BET);
				}
			} else { 			//Mid bet
				return getBettingChips(toCall, minimumBet, blind, MID_BET);
			}
		} else {//High hand value - disguise hand, value-bet or steal pot
			if(r<bluffChance){	//Bluff a low hand
				return 0;
			} else if(r<STEAL_POT_CHANCE+bluffChance && remainingBlinds > inRed){ //Try to steal pot
				return getBettingChips(toCall, minimumBet, blind, HIGH_BET);
			} else { //Value bet - a mid bet or low bet, possibly all-in.
				if(available >= MID_BET*minimumBet*lowerBetModifier){
					return getBettingChips(toCall, minimumBet, blind, MID_BET);
				} else {
					return getBettingChips(toCall, minimumBet, blind, LOW_BET);
				}
			}
		}
	}

	@Override
	int action(int betAmount, int minimumBet, int blind) { //Print statements are temporary for testing
		if(!round_active) return 0; //Player has folded
		
		boolean canCheck = betAmount==getChipsInPot();	//Can player check?
		int toCall = betAmount - getChipsInPot();		//Amount needed to call
		
		//Determine the hand value ranged (based on player's modifiers)
		int handBetVal = handBetValue(blind);
		int upperVal = (int) (handBetVal * upperBetModifier);
		int lowerVal = Math.min((int) (handBetVal * lowerBetModifier), getChips());
		
		//Decide action
		if(upperVal < toCall){ //Hand not good enough, bluff, check or fold
			Random rand = new Random();
			int r = (int) (rand.nextDouble() * 100);
			if(r<bluffChance){
				r = rand.nextInt(2);
				if(r==0){
					return bet(toCall + getBettingChips(toCall, minimumBet, blind, MID_BET));
				} else {
					return bet(toCall + getBettingChips(toCall, minimumBet, blind, MID_BET));
				}
			} else {	//Check or fold
				if(canCheck){
					return bet(0);
				} else {
					round_active = false;
					return 0;
				}
			}
		} else if(lowerVal == getChips() && (handBetVal > betAmount || getChips() < blind)){ //Forced all-in
			return bet(getChips());
		} else if((upperVal > betAmount + minimumBet) && (getChips() > toCall + minimumBet)){ //Raise
			//Generate the player's value of their hand
			Random rand = new Random();
			int handVal = lowerVal + rand.nextInt(upperVal - lowerVal + 1);
			//Get bet/raise amount
			int bet = decideBetValue(toCall, handVal, betAmount, minimumBet, blind);
			return bet(bet+toCall);
		} else { //Call
			return bet(toCall);
		}
	}

	@Override
	int discard() {
		//Amount of cards to be discarded
		int discarded = 0;
		
		//Get the discard value to decide which cards to discard:
		Random rand = new Random();
		int minChance = MINIMUM_DISCARD_CHANCE + discardModifier;
		int random = minChance + rand.nextInt(MAXIMUM_DISCARD_CHANCE - minChance);
		
		//Discard cards, but no more than MAX_DISCARD (3)
		ArrayList<Integer> toDiscard = new ArrayList<Integer>();
		for(int i=0; i<HandOfCards.HAND_SIZE; i++){
			int discardProb = hand.getDiscardProbability(i);
			//Discard if random < probability
			if(random<discardProb){
				if(discarded>=MAX_DISCARD){
					toDiscard.remove(0);
					discarded--;
				}
				toDiscard.add(i);
				Collections.sort(toDiscard);
				discarded++;
			}
		}
		//Integer[] discardArray = toDiscard.toArray(new Integer[MAX_DISCARD]);
		int[] discardArray = new int[MAX_DISCARD];
		for(int i=0; i<toDiscard.size() && i<MAX_DISCARD; i++){
			discardArray[i] = toDiscard.get(i);
		}
		hand.discard(discardArray, discarded);
		
		//Return amount of cards discarded
		return discarded;
	}
	
	/**
	 * Generates one preset personality for this player by assigning the nature attributes.
	 * The preset personalities range from very safe players to very aggressive and bluffing players.
	 * 
	 * @param t   The {@code int} to determine the personality.
	 * @see #bluffChance chance to bluff
	 * @see #discardModifier lower limit for discarding
	 * @see #inRed amount of big blinds the player tries to preserve
	 * @see #upperBetModifier the upper limit for betting
	 * @see #lowerBetModifier the lower limit for betting
	 */
	void generatePersonality(int t){
		switch(t){
			//strong bluffer
			case 0:
				bluffChance = 25 + BASE_BLUFF_CHANCE;	//Chance to bluff
				discardModifier = 10;		//Lower limit for discarding cards
				inRed = 1;					//The number of big blinds the player tries to preserve when playing a hand
				upperBetModifier = 2;		//The upper limit for valuing and betting.
				lowerBetModifier = 1.4;		//The lower limit for valuing and betting
				break;
			//bluffer
			case 1:
				bluffChance = 10 + BASE_BLUFF_CHANCE;				
				discardModifier = 15;		
				inRed = 2;					
				upperBetModifier = 1.5;		
				lowerBetModifier = 1.2;
				break;
			//neutral
			case 2:
				bluffChance = BASE_BLUFF_CHANCE;				
				discardModifier = 20;		
				inRed = 3;					
				upperBetModifier = 1.2;		
				lowerBetModifier = 1;
				break;
			//safe player
			case 3:
				bluffChance = 10;			
				discardModifier = 25;		
				inRed = 3;					
				upperBetModifier = 1.2;		
				lowerBetModifier = 0.9;
				break;
			//very safe player
			case 4:
				bluffChance = 5;		
				discardModifier = 30;		
				inRed = 4;					
				upperBetModifier = 1.1;		
				lowerBetModifier = 0.8;
				break;
		}
	}
}
