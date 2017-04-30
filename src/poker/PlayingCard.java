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

/**
 * Represents a standard playing card with a {@link #suit}, {@link #faceValue face value}
 * and {@link #gameValue game value}. The game value is used to value the Ace as the highest
 * card. Also has a {@link #type} attribute, a {@link String} of the card type.
 * @author Dara Callinan
 * @author Jazheel Luna
 * @author Eoghan O'Donnell
 * @author Crischelle Pana
 */
public class PlayingCard {

	static public final char[] SUITS = {'H','S','D','C'};
	static public final String[] TYPES = {"A","2","3","4","5","6","7","8","9","10","J","Q","K"};
	
	private String type;
	private char suit;
	private int faceValue;
	private int gameValue;
	
	/**
	 * Class constructor. Initialises the {@link #type}, {@link #suit}, {@link #faceValue} and {@link #gameValue}.
	 * @param type   The {@link String} containing the card type.
	 * @param suit   The {@code char} representing the card suit.
	 * @param facevalue   The {@code int} representing the card face value.
	 * @param gamevalue   The {@code int} representing the card game value.
	 */
	public PlayingCard(String type,char suit,int facevalue,int gamevalue){
		this.type = type;
		this.suit = suit;
		this.faceValue = facevalue;
		this.gameValue = gamevalue;
	}
	
	/**
	 * Get the suit of the card.
	 * @return   The card {@link #suit}.
	 */
	public char getSuit(){	
		return suit;
	}
	
	/**
	 * Get the face value of the card.
	 * @return   The card {@link #faceValue}.
	 */
	public int getFaceValue(){
		return faceValue;
	}
	
	/**
	 * Get the game value of the card.
	 * @return   The card {@link #gameValue}.
	 */
	public int getGameValue(){
		return gameValue;
	}
	
	public String toString(){
		return this.type + this.suit;
	}
	
}
