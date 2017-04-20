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
	public int discardmodifier;	//parameter to modify discard probability
	public int bluffChance;		//bot's tendency to bluff
	public int betmodifier;		//modifies bot's tendency to raise/call
	public int foldmodifier;	//not sure if this should be a separate value but we can think about it/change it pretty easily
	private double upperBetModifier = 1.2;
	private double lowerBetModifier = 1.0;
	AutomatedPokerPlayer(String name, DeckOfCards deck) {
		super(name, deck);
		Random rand = new Random();
		int temp = rand.nextInt(NUMBER_OF_PERSONALITIES);
		this.generatePersonality(temp);
	}
	
	//Determines a betting value based on a blind/ante as a base value, might change to be bet-based
	private int handBetValue(int blind){
		int handVal = hand.getGameValue();
		double mod;
		if(handVal<HandOfCards.ONE_PAIR_DEFAULT){ //High hand
			if(handVal < (8*HandOfCards.FOURTEEN_FOURTH)){ //Highest card 7 or lower
				mod = 0.25;
			} else if(handVal < (11*HandOfCards.FOURTEEN_FOURTH)){ //No face cards
				mod = 0.5;
			} else { //At least one face card
				mod = 1;
			}
		} else if(handVal<HandOfCards.TWO_PAIR_DEFAULT){		//One pair
			if(handVal < (10*HandOfCards.FOURTEEN_THIRD)){		//Pair is lower than 10
				mod = 1.2;
			} else {
				mod = 1.6;
			}
		} else if(handVal<HandOfCards.THREE_OF_A_KIND_DEFAULT){	//Two pair
			mod = 2;
		} else if(handVal<HandOfCards.STRAIGHT_DEFAULT){		//Three of a kind
			mod = 4;
		} else if(handVal<HandOfCards.FOUR_OF_A_KIND_DEFAULT){	//Full house, straight, flush
			mod = 6;
		} else { //Four of a kind, straight flush, royal flush
			mod = 20;
		}
		return (int) (blind * mod);
	}

	@Override
	int action(int betAmount, int minimumBet, int blind) { //Print statements are temporary for testing
		if(!round_active) return 0; //Player has folded
		
		boolean canCheck = betAmount==chipsInPot;	//Can player check?
		int toCall = betAmount - chipsInPot;
		
		//Determine possible bet values
		int handBetVal = handBetValue(blind);
		int upperBet = Math.min((int) (handBetVal * upperBetModifier), getChips());
		int lowerBet = Math.min((int) (handBetVal * lowerBetModifier), getChips());
		//Try to save a blind
		if((getChips() - upperBet) < blind && (upperBet - blind) > lowerBet){
			upperBet -= blind;
		}
		//Decide action
		if(upperBet < toCall){ //Hand not good enough, check or fold
			if(canCheck){
				System.out.println(player_name + " checked.");
				return bet(0);
			} else {
				System.out.println(player_name + " folded.");
				fold();
				return 0;
			}
		} else if(lowerBet == getChips() && (handBetVal > betAmount || getChips() < blind)){ //Forced all-in
			System.out.println(player_name + " all in.");
			return bet(getChips());
		} else if((upperBet > betAmount + minimumBet) && (getChips() > toCall + minimumBet)){ //Raise
			Random rand = new Random();
			//if((rand.nextDouble() * 100) < bluffChance);
			int bet = lowerBet + rand.nextInt(upperBet - lowerBet + 1);
			System.out.println(player_name + " raised to " + bet);
			return bet(bet);
		} else { //Call
			if(toCall == 0) {System.out.println(player_name + " checked.");}
			else {System.out.println(player_name + " called.");}
			return bet(toCall);
		}
	}

	@Override
	int discard() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	void generatePersonality(int t){
		switch(t){
		case 0:
			discardmodifier=-2; //not sure how to do this. we could have the discard function do different things
								//for a range of small ints (e.g. -3->3) or just multiply/add/both a value (e.g. add 20)
			bluffChance=20;		//this could just be the bot's chance to bluff with a bad hand
			foldmodifier=-1;	//same as the rest. we need to figure out the best way of doing this.	
			upperBetModifier = 1.2;	//This means the bot would bet between 1 and 1.2 times the expected bet value.
			lowerBetModifier = 1;
		case 1:
		case 2:
		}
	}
	
	
	//single blind, no pot, 2 player loop for testing (temporary)
	public static void main(String args[]){
		DeckOfCards deck = new DeckOfCards();
		AutomatedPokerPlayer bot = new AutomatedPokerPlayer("Yugi", deck);
		AutomatedPokerPlayer bot2 = new AutomatedPokerPlayer("Kaiba", deck);
		bot.enterGame(0);
		int round = 0;
		while(bot.getChips()>0 && bot2.getChips()>0){
			bot.round_active = true;
			bot2.round_active = true;
			System.out.println(bot.player_name + " " + bot.getChips() + " - " + bot.hand + bot.handBetValue(10));
			System.out.println(bot2.player_name + " " + bot2.getChips() + " - " + bot2.hand + bot2.handBetValue(10));
			int bet = 10;
			int min = 10; int blind = 10;
			
			round++;
			//
			deck.reset();
			bot.hand = new HandOfCards(deck);
			bot.round_active = true;
			bot2.hand = new HandOfCards(deck);
			bot2.round_active = true;
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
				int call = bet - bot.chipsInPot;
				int t = bot.action(bet, min, blind);
				bet += t - call;
				min = Math.max(bet-call, min);
				//
				if(!bot.round_active || !bot2.round_active){
					break;
				}
				System.out.println("to play: " + bot2.player_name + "  bank:" + bot2.getChips() + "  bet:" + bet);
				call = bet - bot2.chipsInPot;
				t = bot2.action(bet, min, blind);
				bet += t - call;
				min = Math.max(bet-call, min);
				if(bot.chipsInPot == bot2.chipsInPot){
					break;
				}
			}
			bot.chipsInPot = 0; 
			bot2.chipsInPot = 0;
		}
	}
}
