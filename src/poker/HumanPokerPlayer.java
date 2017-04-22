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
		
		twitter.addToTweet(actionMessage);
		twitter.completeMessage();
		
		//Parse response
		//TO DO
		twitter.parseResponse();
		
		return 0;
	}

	@Override
	int discard() {
		// TODO Auto-generated method stub
		return 0;
	}



}
