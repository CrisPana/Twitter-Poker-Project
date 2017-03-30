package poker;

public class PlayingCard {

	static public final char[] SUITS = {'H','S','D','C'};
	static public final String[] TYPES = {"A","2","3","4","5","6","7","8","9","10","J","Q","K"};
	
	private String type;
	private char suit;
	private int faceValue;
	private int gameValue;
	
	public PlayingCard(String type,char suit,int facevalue,int gamevalue){	//constructor
		this.type = type;
		this.suit = suit;
		this.faceValue = facevalue;
		this.gameValue = gamevalue;
	}
	
	public char getSuit(){	
		return suit;
	}
	
	public int getFaceValue(){
		return faceValue;
	}
	
	public int getGameValue(){
		return gameValue;
	}
	
	public String toString(){
		return this.type + this.suit;
	}
	
}
