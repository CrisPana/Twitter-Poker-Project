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
	
	public int bluffChance;		//bot's tendency to bluff
	public int betmodifier;		//modifies bot's tendency to raise/call
	public int foldmodifier;	//not sure if this should be a separate value but we can think about it/change it pretty easily
	private int discardModifier;//Lower limit for discarding cards (only discard cards that return higher probability)
	private int inRed;			//Number of remaining big blinds/antes the player is comfortable with
	private double upperBetModifier = 1.2;
	private double lowerBetModifier = 1.0;
	
	AutomatedPokerPlayer(String name, DeckOfCards deck) {
		super(name, deck);
		Random rand = new Random();
		int temp = rand.nextInt(NUMBER_OF_PERSONALITIES);
		this.generatePersonality(temp);
		isBot = true;
	}
	
	//Determines a betting value based on a blind/ante as a base value
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
	
	//Returns a bet value using a multiplier for the minimum bet. 
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
	
	//Return a value to bet/raise by - can be zero if bluffing a low hand.
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
					return toCall + getBettingChips(toCall, minimumBet, blind, MID_BET);
				} else {
					return toCall + getBettingChips(toCall, minimumBet, blind, MID_BET);
				}
			} else {	//Check or fold
				if(canCheck){
					return bet(0);
				} else {
					fold();
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
		for(int i=0; i<HandOfCards.HAND_SIZE && discarded<MAX_DISCARD; i++){
			int discardProb = hand.getDiscardProbability(i);
			//Discard if random < probability
			if(random<discardProb){
				hand.discard(i);
				discarded++;
			}
		}
		
		//Return amount of cards discarded
		return discarded;
	}
	
	void generatePersonality(int t){
		switch(t){
		case 0:
			//discardmodifier=-2; 		//not sure how to do this. we could have the discard function do different things
			//							//for a range of small ints (e.g. -3->3) or just multiply/add/both a value (e.g. add 20)
			bluffChance=10 + BASE_BLUFF_CHANCE;		//this could just be the bot's chance to bluff with a bad hand
			foldmodifier=-1;			//same as the rest. we need to figure out the best way of doing this.	
			discardModifier = 10;		//Lower limit for discarding cards
			inRed = 2;					//The number of big blinds the player tries to preserve when playing a hand
			upperBetModifier = 1.2;		//This means the bot would bet between 1 and 1.2 times the expected bet value.
			lowerBetModifier = 1;
		case 1:
		case 2:
		}
	}
	
	
	//single blind, no pot, 2 player loop for testing (temporary)
	public static void main(String args[]){
		System.out.println("TEST RUN");
		DeckOfCards deck = new DeckOfCards();
		AutomatedPokerPlayer bot = new AutomatedPokerPlayer("Yugi", deck);
		AutomatedPokerPlayer bot2 = new AutomatedPokerPlayer("Kaiba", deck);
		bot.enterGame(0);
		int round = 0;
		while(bot.getChips()>0 && bot2.getChips()>0){
			bot.round_active = true;
			bot2.round_active = true;
			int bet = 10;
			int min = 10; int blind = 10;
			
			round++;
			//
			deck.reset();
			bot.hand = new HandOfCards(deck);
			bot.round_active = true;
			bot2.hand = new HandOfCards(deck);
			bot2.round_active = true;
			
			System.out.println(bot.player_name + " " + bot.getChips() + " - " + bot.hand + bot.handBetValue(10));
			System.out.println(bot2.player_name + " " + bot2.getChips() + " - " + bot2.hand + bot2.handBetValue(10));
			
			if(round%2 ==0){
				System.out.println(bot.player_name + " paid blind.");
				bot.bet(blind);
			} else {
				System.out.println(bot2.player_name + " paid blind.");
				bot2.bet(blind);
			}
			while(bot.round_active && bot2.round_active){
				//
				System.out.println("to play: " + bot.player_name + "  bank:" + bot.getChips() + "  bet:" + bet);
				int call = bet - bot.getChipsInPot();
				int t = bot.action(bet, min, blind);
				bet += t - call;
				min = Math.max(bet-call, min);
				//
				if(!bot.round_active || !bot2.round_active){
					break;
				}
				System.out.println("to play: " + bot2.player_name + "  bank:" + bot2.getChips() + "  bet:" + bet);
				call = bet - bot2.getChipsInPot();
				t = bot2.action(bet, min, blind);
				bet += t - call;
				min = Math.max(bet-call, min);
				if(bot.getChipsInPot() == bot2.getChipsInPot()){
					break;
				}
			}
			bot.resetChipsInPot();
			bot2.resetChipsInPot();
		}
		
		bot = new AutomatedPokerPlayer("Yugi", deck);
		int bet = bot.getBettingChips(10, 10, 10, LOW_BET);
		bot.bet(bet);
		System.out.println(bet);
		
		bet = bot.getBettingChips(10, 10, 10, MID_BET);
		bot.bet(bet);
		System.out.println(bet);
		
		bet = bot.getBettingChips(10, 10, 10, HIGH_BET);
		bot.bet(bet);
		System.out.println(bet);
		
		bet = bot.getBettingChips(0, 10, 10, HIGH_BET);
		bot.bet(bet);
		System.out.println(bet);
		
		System.out.println(bot.getChips());
		
		System.out.println("\n" + bot.hand);
		bot.discard();
		System.out.println("\n" + bot.hand);
		for(int i=0; i<100; i++){
			bot.discard();
		}
		System.out.println("\n" + bot.hand);
	}
}
