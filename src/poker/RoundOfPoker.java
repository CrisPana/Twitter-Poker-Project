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

public class RoundOfPoker {
	
	//player left of dealer gets small blind
	//plyer left of small blind guy gets big blind
	//big blind usully minimum bet 
	//small blind half of that
	//In most games of poker, cards are dealt clockwise, or to the dealer's left. 
	//In Texas Hold 'Em, a variation of poker, the dealer deals to the left but skips two players, 
	//the small blind and the big blind, and deals first to the third person on the left
	static public final int SMALL_BLIND = 1;
	static public final int BIG_BLIND = 2;
	public boolean everyoneBetted = false;
	public ArrayList<PokerPlayer> players = new ArrayList<PokerPlayer>();
	
}
