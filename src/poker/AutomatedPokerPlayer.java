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
	public int bluffchance;		//bot's tendency to bluff
	public int betmodifier;		//modifies bot's tendency to raise/call
	public int foldmodifier;	//not sure if this should be a separate value but we can think about it/change it pretty easily
	AutomatedPokerPlayer(String name, DeckOfCards deck) {
		super(name, deck);
		Random rand = new Random();
		int temp = rand.nextInt(NUMBER_OF_PERSONALITIES);
		this.generatePersonality(temp);
	}

	@Override
	int action() {
		// TODO Auto-generated method stub
		return 0;
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
			bluffchance=20;		//this could just be the bot's chance to bluff with a bad hand
			betmodifier=-1;		//this could mean the bot is slightly reserved when raising
			foldmodifier=-1;;	//same as the rest. we need to figure out the best way of doing this.	
		case 1:
		case 2:
		}
	}
}
