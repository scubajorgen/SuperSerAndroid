package net.studioblueplanet.superset.gameplay;

/**
 * This class represents a Set card. The card is identified by:
 * - Color
 * - Symbol
 * - Number (1, 2, 3)
 * - filling (empty, hashed, filled)
 * 
 * @author jorgen
 *
 */
public class Card
{
	public enum CardColor
	{
		CARDCOLOR_RED, CARDCOLOR_BLUE, CARDCOLOR_PURPLE
	};
	
	public enum CardFilling
	{
		CARDFILLING_OPEN, CARDFILLING_SOLID, CARDFILLING_HALF
	};
	
	public enum CardNumber
	{
		CARDNUMBER_ONE, CARDNUMBER_TWO, CARDNUMBER_THREE
	};
	
	public enum CardSymbol
	{
		CARDSYMBOL_ELLIPSE, CARDSYMBOL_RECTANGLE, CARDSYMBOL_WAVE
	};	
	
	public int				stackIndex;
	public CardColor		color;
	public CardSymbol		symbol;
	public CardNumber		number;
	public CardFilling		filling;
}
