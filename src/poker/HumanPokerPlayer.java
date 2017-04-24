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
	static private final String FOLD_ACTION = "fold";
	static private final String CHECK_ACTION = "check";
	static private final String CALL_ACTION = "call";
	static private final String BET_ACTION = "bet";
	static private final String RAISE_ACTION = "raise";
	static private final String ALL_IN_ACTION = "allin";
	
	TwitterStream twitter;

	HumanPokerPlayer(TwitterStream tw, DeckOfCards deck) {
		super(tw.user.getScreenName(), deck);
		isBot = false;
		twitter = tw;
	}

	@Override
	int action(int betAmount, int minimumBet, int blind) {
		int toCall = betAmount - getChipsInPot();
		boolean canCheck = toCall == 0;
		
		String actionMessage = "@" + player_name + "'s turn to act. Blinds are  " + blind + "/" + (blind/2) + ". ";
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
			String[] actionWords;
			try {
				actionWords = twitter.parseResponse().split("\\s+");
			} catch (TwitterException | InterruptedException e) {
				e.printStackTrace();
				fold();
				return 0;
			}
			//Remove punctuation
			int wordIgnore = 0;
			//Ignore '@'s
			if(actionWords[0].startsWith("@")) wordIgnore = 1;
			for (int i = 0; i < actionWords.length; i++) {
				actionWords[i] = actionWords[i].replaceAll("[^\\w]", "");
			}
			String playerAction = actionWords[wordIgnore];
			//Parse action
			if(playerAction.equalsIgnoreCase(FOLD_ACTION)){
				fold();
				return 0;
			} else if(playerAction.equalsIgnoreCase(CHECK_ACTION)){	//if check, bets nothing
				if(canCheck) return bet(0);
				validAction = false;
			} else if(playerAction.equalsIgnoreCase(CALL_ACTION)){	//call is simply toCall unless player
				return bet(Math.min(toCall, getChips()));			//doesn't have enough chips then they all in
			} else if(playerAction.equalsIgnoreCase(BET_ACTION) || playerAction.equalsIgnoreCase(RAISE_ACTION)){
				//bet/raise ensures integer argument is present
				if(actionWords.length-wordIgnore<2){
					validAction = false;
					continue;
				}
				int playerBet;
				try {
					playerBet = Integer.parseInt(actionWords[wordIgnore+1]);
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
			} else if(actionWords.length-wordIgnore>1 && (playerAction+actionWords[wordIgnore+1]).equalsIgnoreCase(ALL_IN_ACTION)){
				//all in simply all ins
				return bet(getChips());
			} else {
				validAction = false;//we should never hit this
			}
		}
		return 0;
	}

	@Override
	int discard() {
		// TODO Auto-generated method stub
		return 0;
	}
}
