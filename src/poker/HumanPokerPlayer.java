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

import twitter4j.User;

//import twitter4j.*;

public class HumanPokerPlayer extends PokerPlayer {
	
	User user;
	TwitterStream twitter;

	HumanPokerPlayer(String name, DeckOfCards deck, User twitterUser) {
		super(name, deck);
		isBot = false;
		user = twitterUser;
		
	}

	@Override
	int action(int betAmount, int minimumBet, int blind) {
		int toCall = betAmount - getChipsInPot();
		
		String actionMessage = user + "'s turn to act. Blinds are  " + blind + "/" + (blind/2) + ". ";
		actionMessage += "Current bet is " + betAmount + ". ";
		if(toCall > 0){
			actionMessage += "You need " + toCall + " to call.";
		}
		
		boolean needPlayerAction = true;	//loops until something is returned
		
		while(needPlayerAction){
			twitter.addToTweet(actionMessage);
			twitter.completeMessage();
			String playerAction = twitter.parseResponse();
			boolean validAction = true;		//set to false if a player bet is invalid
			if(playerAction.equals("fold")){	//checks for the word fold for a fold
				fold();
				return 0;
			}
			else if(playerAction.equals("check"))return bet(0);	//if check, bets nothing
			else if(playerAction.equals("call")){				//call is simply toCall unless
				if(getChips()>=toCall)return bet(toCall);		//player doesn't have enough chips
				else return bet(getChips());					//then they all in
			}
			else if(playerAction.contains("raise")){			//raise is raise amount + toCall
				int playerBet = Integer.parseInt(playerAction.substring(6))+toCall;
				if(playerBet<=getChips())return bet(playerBet);
				else validAction = false;
			}
			else if(playerAction.contains("bet")){				//bet is bet amount, checks that bet is high enough
				int playerBet = Integer.parseInt(playerAction.substring(4));
				if(playerBet<=getChips()&&(playerBet+getChipsInPot())>=betAmount)return bet(playerBet);
				else validAction = false;
			}
			else if(playerAction.equals("all in"))return bet(getChips());	//all in simply all ins
			else validAction = false;							//we should never hit this
			if(!validAction){									//sets action message for next loop
				actionMessage = "You entered an invalid command. Please enter your action, followed by a space and integer if applicable";
			}
		}
		return 0; //this should never be reached but it thinks it's not returning an integer otherwise
					//and I can''t be bothered to fix that rn
	}

	@Override
	int discard() {
		// TODO Auto-generated method stub
		return 0;
	}



}
