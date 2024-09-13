package net.studioblueplanet.superset.view;

import java.util.ArrayList;

import net.studioblueplanet.superset.gameplay.Card;
import net.studioblueplanet.superset.gameplay.Set;
import net.studioblueplanet.superset.Config;


/**
 * This method represents a playfield, i.e. a grid containing card positions.
 * @author jorgen
 *
 */
public abstract class VirtualPlayField
{

	
	/** Number of tagged cards */
	protected int								numberOfTaggedCards;

	/** Collection of tagged cards */
	protected Set taggedCards;

	/** Collection of positions that make up the playfield */
	protected VirtualPlayFieldPosition[][]		positions;

	/** The cards that are exposed to the user */
	protected Card[]							cardsInPlay;


	
	/**********************************************************************************************\
	 * Constructor
	\**********************************************************************************************/	
	/**
	 * Constructors. Creates the arrays and initializes the variables.
	 */
	public VirtualPlayField()
	{
		int row, col;
		int	i;
		
		positions=new VirtualPlayFieldPosition[Config.MAX_ROWS][Config.MAX_COLS];
		cardsInPlay=new Card[Config.MAX_CARDS_IN_PLAY];
		
		row=0;
		while (row<Config.MAX_ROWS)
		{
			col=0;
			while (col<Config.MAX_COLS)
			{
				positions[row][col]=new VirtualPlayFieldPosition();
				col++;
			}
			row++;
		}
		
		i=0;
		while (i<Config.MAX_CARDS_IN_PLAY)				
		{
			cardsInPlay[i]=null;
			i++;
		}
		
		taggedCards=new Set();
		
	}

	/**********************************************************************************************\
	 * Private/Protected methods
	\**********************************************************************************************/
	

	/**
	 *  This function updates the list of tagged cards based on the new
	 *  tagged state of the card on position (row, col)
	 *  @param row    Row position of the card 
	 *  @param col    Column position of the card 
	 */
	protected void updatePlayFieldInfo(int row, int col)
	{
		int  i;
		boolean found;
		VirtualPlayFieldPosition pos;

		pos=positions[row][col];

		i=0;
		found=false;
		while ((i<Config.SET_SIZE) && (!found))
		{
			if (pos.isTagged())
			{
				if (taggedCards.row[i]<0)
				{
					taggedCards.row[i]=row;
					taggedCards.col[i]=col;
					numberOfTaggedCards++;
					found=true;
				}
			}
			else
			{
				if (taggedCards.row[i]==row && taggedCards.col[i]==col)
				{
					taggedCards.row[i]=-1;
					taggedCards.col[i]=-1;
					numberOfTaggedCards--;
				}
			}
			i++;
		}
	}


	/** 
	 *  This method adds a card to the position.
	 *  @param card Reference to the card to be added 
	 *  @param position Reference to the position the card must be added to
	 */
	protected void addCardToPosition(Card card, VirtualPlayFieldPosition position)
	{
		position.setCard(card);
	}

	/********************************************************************\
	*  
	\********************************************************************/
	protected boolean isSet(Card[] cards)
	{
		int		i, j;
		boolean	colorsSame;
		boolean	colorsDifferent;
		boolean	numbersSame;
		boolean	numbersDifferent;
		boolean	symbolsSame;
		boolean	symbolsDifferent;
		boolean	fillingsSame;
		boolean	fillingsDifferent;
		boolean	returnValue;

		colorsSame			=true;
		colorsDifferent		=true;
		numbersSame			=true;
		numbersDifferent	=true;
		symbolsSame			=true;
		symbolsDifferent	=true;
		fillingsSame		=true;
		fillingsDifferent	=true;

		i=0;
		while (i<Config.SET_SIZE-1)
		{
			j=i+1;
			while (j<Config.SET_SIZE)
			{
				if (cards[i].color==cards[j].color)
				{
					colorsDifferent	=false;
				}
				if (cards[i].color!=cards[j].color)
				{
					colorsSame		=false;
				}

				if (cards[i].number==cards[j].number)
				{
					numbersDifferent=false;
				}
				if (cards[i].number!=cards[j].number)
				{
					numbersSame=false;
				}
				
				if (cards[i].symbol==cards[j].symbol)
				{
					symbolsDifferent=false;
				}
				if (cards[i].symbol!=cards[j].symbol)
				{
					symbolsSame=false;
				}
				
				if (cards[i].filling==cards[j].filling)
				{
					fillingsDifferent=false;
				} 
				if (cards[i].filling!=cards[j].filling)
				{
					fillingsSame=false;
				}

				j++;
			}
			i++;
		}
		returnValue=	(colorsSame   || colorsDifferent  ) &&
						(numbersSame  || numbersDifferent ) &&
						(symbolsSame  || symbolsDifferent ) &&
						(fillingsSame || fillingsDifferent);

		return returnValue;
	}	
	
	/********************************************************************\
	*  
	\********************************************************************/
	protected void untagTaggedCards()
	{
		int					i;
		int					row;
		int					col;

		i=0;										// parse tagged card
		while (i<Config.SET_SIZE)
		{
			row=taggedCards.row[i];
			col=taggedCards.col[i];
			if (row>=0 && col>=0)					// check if card really still tagged
			{
				this.setTagging(row, col, false);	// set tagging to false
			}

			i++;
		}
	}	
	
	/**********************************************************************************************\
	 * Public methods
	\**********************************************************************************************/


	/**
	 *  This method adds a card to the playfield. The most logical position
	 *  is calculated.
	 *  @param card     Reference to the card to be added.
	 *  @return The position the card has been added to or null if the card could not be added
	 *          (this should never occur...)
	 */	
	protected VirtualPlayFieldPosition addCardToFreePosition(Card card)
	{
		int 		row;
		int 		col;
		boolean 	found;
		VirtualPlayFieldPosition foundPosition;
		
		foundPosition=null;
		found=false;

		if (Config.COL_BASED)
		{
			// Try to fit in the card in the center columns
			row=0;
			while (row<Config.MAX_ROWS && !found)
			{
				col=1;
				while (col<(Config.MAX_COLS-1) && !found)
				{
					if (!positions[row][col].hasCard())
					{
						addCardToPosition(card, positions[row][col]);
						foundPosition=positions[row][col];
						found=true;
					}
					col++;
				}
				row++;
			}
			// if not succeeded, try to fit the card in in last column
			if (!found)
			{
				row=0;
				col=Config.MAX_COLS-1;
				while (row<Config.MAX_ROWS && !found)
				{
					if (!positions[row][col].hasCard())
					{
						addCardToPosition(card, positions[row][col]);
						foundPosition=positions[row][col];
						found=true;
					}
					row++;
				}
			}
			// if still not succeeded, try to fit in the card in 1st column
			if (!found)
			{
				row=0;
				col=0;
				while (row<Config.MAX_ROWS && !found)
				{
					if (!positions[row][col].hasCard())
					{
						addCardToPosition(card, positions[row][col]);
						foundPosition=positions[row][col];
						found=true;
					}
					row++;
				}
			}
		}
		return foundPosition;
	}



	/**
	 *  This method returns the card given a (row, col) position on the play field 
	 *  @param row The row coordinate
	 *  @param col The column coordinate
	 *  @return The card
	 */	
	public Card getCard(int row, int col)
	{
		return positions[row][col].getCard();
	}

	/**
	 *  This method returns the card given a (row, col) position on the play field 
	 *  @param position The Playfield position
	 *  @return The card
	 */
	public Card getCard(VirtualPlayFieldPosition position)
	{
		return position.getCard();
	}




	/**
	 *  This method sets the tagging of a card on the play field
	 *  @param row    Row position of the card to tag
	 *  @param col    Column position of the card to tag
	 *  @param True if three cards have been tagged, false if not
	 */
	public boolean setTagging(int row, int col, boolean tagged)
	{
		boolean			threeCardsTagged=false;

		if (tagged)
		{
			if (this.numberOfTaggedCards<Config.SET_SIZE)
			{
				positions[row][col].setTagging(true);
				updatePlayFieldInfo(row, col);

				if (numberOfTaggedCards==Config.SET_SIZE)
				{
					threeCardsTagged=true;
				}
			}
		}
		else
		{
			positions[row][col].setTagging(false);
			updatePlayFieldInfo(row, col);
		}

		return threeCardsTagged;
	}





	/**
	 *  Remove the cards that are tagged as part of a SET. 
	 */
	public void removeTaggedCards()
	{
		int					i;
		int					row;
		int					col;

		i=0;
		while (i<Config.SET_SIZE)
		{
			row=taggedCards.row[i];
			col=taggedCards.col[i];
			this.setTagging(row, col, false);
			positions[row][col].removeCard();

			i++;
		}
	}



	/**
	 *  This method resets the playfield. This means 
	 *  emptying the positions and untagging all positions.
	 */
	public void resetPlayField()
	{
		int row;
		int col;
		int i;

		// Clear the postions for cards on the playfield
		row=0;
		while (row<Config.MAX_ROWS)
		{
			col=0;
			while (col<Config.MAX_COLS)
			{
				positions[row][col].reset();
				col++;
			}
			row++;
		}

		// no cards are tagged
		numberOfTaggedCards=0;

		// empty the tagged card list
		i=0;
		while (i<Config.SET_SIZE)
		{
			taggedCards.row[i]=-1;
			taggedCards.col[i]=-1;
			i++;
		}
	}

	/**
	 *  This method removes any cards that are on the  
	 *  playfield
	 */
	public void emptyPlayField()
	{
		int row;
		int col;

		row=0;
		while (row<Config.MAX_ROWS)
		{
			col=0;
			while (col<Config.MAX_COLS)
			{
				if (positions[row][col].hasCard())
				{
					positions[row][col].removeCard();
				}
				col++;
			}
			row++;
		}

	}


	/**
	 *  This method returns whether the tagged cards represent a SET.
	 *  @return true If three cards are tagged and they are a set. False if not.
	 */
	public boolean isSetTagged()
	{
		int		i;
		boolean	returnValue;
		Card[]	cards;

		
		cards=new Card[Config.SET_SIZE];
		
		if (numberOfTaggedCards==Config.SET_SIZE)
		{
			i=0;
			while (i<Config.SET_SIZE)
			{
				cards[i]=getCard(taggedCards.row[i], taggedCards.col[i]);

				i++;
			}

			returnValue=isSet(cards);

		}
		else
		{
			returnValue=false;
		}

		return returnValue;

	}



	/**
	 *  This method returns one of the SETs that are on the playfield
	 *  @return The total number of sets that are possible on the playfield
	 */
	public ArrayList<Set> findSet()
	{
		int				row;
		int				col;
		int				i,  j, k;
		Card[] 			cards;
		ArrayList<Set> 	theList;
		Set				newSet;


		cards=new Card[Config.SET_SIZE];
		theList=new ArrayList<Set>();
		

		// cards (and empty spaces) are put in a linear array. 
		// That makes things easier to calculate
		row=0;
		while (row<Config.MAX_ROWS)
		{
			col=0;
			while (col<Config.MAX_COLS)
			{
				cardsInPlay[row*Config.MAX_COLS+col]=getCard(row, col);
				col++;
			}
			row++;
		}

		// For each combination of SET_SIZE cards not being empty 
		// it is calculated whether it is a set or not
		// Possible sets are counted and put in an array
		i=0;
		while (i<Config.MAX_CARDS_IN_PLAY-2)
		{
			cards[0]=cardsInPlay[i];
			if (cards[0]!=null)
			{
				j=i+1;
				while (j<Config.MAX_CARDS_IN_PLAY-1) 
				{
					cards[1]=cardsInPlay[j];
					if (cards[1]!=null)
					{
						k=j+1;
						while (k<Config.MAX_CARDS_IN_PLAY)
						{
							cards[2]=cardsInPlay[k];
							if (cards[2]!=null)
							{
								if (isSet(cards))
								{
									newSet=new Set();
									
									newSet.row[0]=i/Config.MAX_COLS;
									newSet.col[0]=i%Config.MAX_COLS;
									newSet.row[1]=j/Config.MAX_COLS;
									newSet.col[1]=j%Config.MAX_COLS;
									newSet.row[2]=k/Config.MAX_COLS;
									newSet.col[2]=k%Config.MAX_COLS;
									theList.add(newSet);
								}
							}
							k++;
						}
					}
					j++;
				}
			}
			i++;
		}

		return theList;

	}




	
	
}
