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

import twitter4j.TwitterException;

//import twitter4j.*;

public class HumanPokerPlayer extends PokerPlayer {
	
	//Player actions
	private static final String FOLD_ACTION = "fold";
	private static final String CHECK_ACTION = "check";
	private static final String CALL_ACTION = "call";
	private static final String BET_ACTION = "bet";
	private static final String RAISE_ACTION = "raise";
	private static final String ALL_IN_ACTION = "allin";
	private static final String LEAVE_GAME = "leave";
	private static final int MAX_DISCARDS = 3;
	
	TwitterStream twitter;

	HumanPokerPlayer(TwitterStream tw, DeckOfCards deck) {
		super(tw.user.getScreenName(), deck);
		isBot = false;
		game_active = true;
		twitter = tw;
	}
	
	//Returns an array of words from twitter to be used as input
	private String[] getTwitterInput(){
		//Get action string from twitter
		String[] twitterWords;
		try {
			twitterWords = twitter.parseResponse().split("\\s+");
		} catch (TwitterException | InterruptedException e) {
			e.printStackTrace();
			return new String[] {"fold"};
		}
		//Remove punctuation, remove '@'s
		int wordIndex = 0;
		for (int i = 0; i < twitterWords.length; i++) {
			if(twitterWords[i].startsWith("@")) continue;
			twitterWords[wordIndex] = twitterWords[i].replaceAll("[^\\w]", "");
			wordIndex++;
		}
		String[] actionWords = new String[wordIndex];
		for (int i = 0; i < actionWords.length; i++) {
			actionWords[i] = twitterWords[i];
		}
		return actionWords;
	}

	@Override
	public int action(int betAmount, int minimumBet, int blind) {
		int toCall = betAmount - getChipsInPot();
		boolean canCheck = toCall == 0;
		
		twitter.addToTweet("\n" + player_name + "'s hand: " + hand);
		twitter.completeMessage();
		
		String actionMessage = player_name + "'s turn to act. Blinds are  " + blind + "/" + (blind/2) + ". ";
		actionMessage += "Current bet is " + betAmount + ". ";
		if(toCall > 0){
			actionMessage += "You need " + toCall + " to call.";
		}
		
		boolean needPlayerAction = true;	//loops until something is returned
		boolean validAction = true;			//set to false if a player bet is invalid
		while(needPlayerAction){
			
			if(!validAction){
				actionMessage = "You entered an invalid command. Please enter your action, followed by a space and integer if applicable";
			}
			
			twitter.addToTweet(actionMessage);
			twitter.completeMessage();
			//Get action string from twitter
			String[] actionWords = getTwitterInput();
			String playerAction = actionWords[0];
			//Parse action
			if (playerAction.equalsIgnoreCase(LEAVE_GAME)){
				game_active = false;
				return -1;
			} else if(playerAction.equalsIgnoreCase(FOLD_ACTION)){
				fold();
				return 0;
			} else if(playerAction.equalsIgnoreCase(CHECK_ACTION)){	//if check, bets nothing
				if(canCheck) return bet(0);
				validAction = false;
			} else if(playerAction.equalsIgnoreCase(CALL_ACTION)){	//call is simply toCall unless player
				return bet(Math.min(toCall, getChips()));			//doesn't have enough chips then they all in
			} else if(playerAction.equalsIgnoreCase(BET_ACTION) || playerAction.equalsIgnoreCase(RAISE_ACTION)){
				//bet/raise ensures integer argument is present
				if(actionWords.length<2){
					validAction = false;
					continue;
				}
				int playerBet;
				try {
					playerBet = Integer.parseInt(actionWords[1]);
				} catch (NumberFormatException e) {
					validAction = false;
					continue;
				}
				//Negative bet/raise is not valid
				if(playerBet<0) validAction = false;
				//If 'raise' was used, add the amount needed for call
				if(playerAction.equalsIgnoreCase(RAISE_ACTION)) playerBet += toCall;
				if(getChips()<playerBet){							//Make sure player has enough chips
					actionMessage = "You don't have enough chips.";
					continue;
				} else if(playerBet-toCall<minimumBet){				//Make sure bet is large enough
					actionMessage = "The minimum raise is " + minimumBet + " chips.";
					continue;
				}
				return bet(playerBet);
			} else if(actionWords.length>1 && (playerAction+actionWords[1]).equalsIgnoreCase(ALL_IN_ACTION)){
				//all in simply all ins
				return bet(getChips());
			} 
			else {
				validAction = false;//we should never hit this
			}
		}
		return 0;
	}

	@Override
	int discard() {
		//Tweet message
		String actionMessage = player_name + "'s turn to discard. Your hand is: " + hand;
		actionMessage += "Enter positions of cards (1-5, Max " + MAX_DISCARDS + ") ";
		twitter.addToTweet(actionMessage);
		twitter.completeMessage();
		
		//Get input
		String[] discardWords = getTwitterInput();
		String playerAction = discardWords[0];
		
		//Parse action
		if (playerAction.equalsIgnoreCase(LEAVE_GAME)){
			game_active = false;
			return -1;
		}
		
		//Get discards
		int discarded = 0;//Amount discarded
		int[] toDiscard = new int[MAX_DISCARDS];
		for(int i=0; i<discardWords.length && discarded<=MAX_DISCARDS; i++){
			if(discardWords[i].matches("\\d+")){//Is string numeric?
				int discard = Integer.parseInt(discardWords[i]);
				if(discard>0 && discard<6){
					toDiscard[discarded] = discard-1;
					discarded++;
				}
			}
		}
		hand.discard(toDiscard, discarded);
		
		return discarded;
	}
}
