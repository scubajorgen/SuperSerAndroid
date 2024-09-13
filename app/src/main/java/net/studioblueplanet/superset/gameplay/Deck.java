package net.studioblueplanet.superset.gameplay;

/**
*
* This class represents the deck of Set cards
*
*/
public class Deck
{
	//The number of cards in the deck
	public static final int 		MAX_CARDS=81;
	
	private Card					cards[];
	private int						cardIndex;

	/* *********************************************************************************************\
	 * Constructor
	\* *********************************************************************************************/
	/**
	 *
	 * Constructor. Creates an ordered new deck of cards
	 *
	 */
	public Deck()
	{
		int i;
		
		cards=new Card[MAX_CARDS];
		i=0;
		while (i<MAX_CARDS)
		{
			cards[i]=new Card();
			i++;
		}
		
		cardIndex=0;
		resetDeck();
	}


	/* *********************************************************************************************\
	 * Private methods
	\* *********************************************************************************************/
	
	/* *********************************************************************************************\
	 * Public methods
	\* *********************************************************************************************/
	/**
	 *
	 * This method fills the deck. The result is an ordered deck with 81 cards	
	 *
	 */
	public void resetDeck()
	{
		int i;

		i=0;
		while (i<MAX_CARDS)
		{
			cards[i].stackIndex=i;
			switch(i%3)
			{
			case 0:
				cards[i].color=Card.CardColor.CARDCOLOR_BLUE;
				break;
			case 1:
				cards[i].color=Card.CardColor.CARDCOLOR_RED;
				break;
			case 2:
				cards[i].color=Card.CardColor.CARDCOLOR_PURPLE;
				break;
			}
			switch((i/3)%3)
			{
			case 0:
				cards[i].filling=Card.CardFilling.CARDFILLING_SOLID;
				break;
			case 1:
				cards[i].filling=Card.CardFilling.CARDFILLING_OPEN;
				break;
			case 2:
				cards[i].filling=Card.CardFilling.CARDFILLING_HALF;
				break;
			}
			switch((i/9)%3)
			{
			case 0:
				cards[i].number=Card.CardNumber.CARDNUMBER_ONE;
				break;
			case 1:
				cards[i].number=Card.CardNumber.CARDNUMBER_TWO;
				break;
			case 2:
				cards[i].number=Card.CardNumber.CARDNUMBER_THREE;
				break;
			}
			switch((i/27)%3)
			{
			case 0:
				cards[i].symbol=Card.CardSymbol.CARDSYMBOL_ELLIPSE;
				break;
			case 1:
				cards[i].symbol=Card.CardSymbol.CARDSYMBOL_RECTANGLE;
				break;
			case 2:
				cards[i].symbol=Card.CardSymbol.CARDSYMBOL_WAVE;
				break;
			}
			i++;
		}
	}

	/**
	 *	This method shuffles the deck
	 */
	public void shuffleDeck()
	{
		int		i;
		int		index1;
		int		index2;
		Card	swapCard;

		i=0;
		while (i<10000)
		{
			index1=(int)(Math.random()*MAX_CARDS);
			index2=(int)(Math.random()*MAX_CARDS);

			swapCard=cards[index1];
			cards[index1]=cards[index2];
			cards[index2]=swapCard;
			i++;
		}
	}

	/**
	 *	This method resets the card iterator. Use nextCard() to get the card
	 *  on top of the deck. Actually it resets the card index.
	 */
	public void resetCards()
	{
		cardIndex=0;
	}

	/**
	 * This method returns the card on top of the deck. Reset the deck index
	 * by calling resetCards(). After calling the card index points to the next
	 * card in the deck
	 * @returns The next card on the deck or null if Deck empty
	 */

	Card nextCard()
	{
		Card nextCard;

		if (cardIndex<MAX_CARDS)
		{
			nextCard=cards[cardIndex];
			cardIndex++;
		}
		else
		{
			nextCard=null;
		}

		return nextCard;
	}


	/**
	 *  This method returns the remaining cards in the deck
	 */ 
	int cardsInDeck()
	{
		return MAX_CARDS-cardIndex;
	}

	
	/**
	 * Returns the card in the deck identified by the index
	 * @param index
	 * @return
	 */
	public Card getCardByIndex(int index)
	{
		int			i;
		Card		card;
		boolean		found;

		found=false;
		i=0;
		card=null;
		while (i<MAX_CARDS && !found)
		{
			if (cards[i].stackIndex==index)
			{
				found=true;
				card=cards[i];
			}
			i++;
		}

		return card;
	}	
}
