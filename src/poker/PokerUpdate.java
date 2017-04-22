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

import twitter4j.Status;



public class PokerUpdate {
	
	Status mostRecent;
	String toSend;
	
	PokerUpdate(Status status){
		mostRecent = status;
		toSend = "";
	}
	
	public void addToTweet(String string){
		toSend += string;
	}
	
	
	
	
	
}
