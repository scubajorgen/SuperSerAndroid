package net.studioblueplanet.superset.view;

import net.studioblueplanet.superset.gameplay.Card;

/**
 * This class represents a position on the playfield. A position can hold a card.
 * The class is thread-safe
 * 
 * @author jorgen
 *
 */
public class VirtualPlayFieldPosition
{
	
	/* Data under protection of the guard */
	protected boolean	positionHasCard;
	protected boolean	cardIsTagged;
	protected Card card;
	/* End of data under protection of the guard */
	
	protected Object	guard;
	

	/* *********************************************************************************************\
	 * Constructor
	\* *********************************************************************************************/

	/**
	 * Constructor, resets the position 
	 */
	public VirtualPlayFieldPosition()
	{
		// Create the guard object
		guard=new Object();
		
		// Reset the playfield position
		this.reset();
	}
	
	
	/* *********************************************************************************************\
	 *  Private methods
	\**********************************************************************************************/
	
	/* *********************************************************************************************\
	 * Public methods
	\* *********************************************************************************************/

	
	
	/**
	 * Resets the position parameters: empties the position
	 */
	public void reset()
	{
		synchronized(guard)
		{
			this.card				=null;
			this.cardIsTagged		=false;
			this.positionHasCard	=false;
		}
	}
	
	
	
	/**
	 * This method adds a Set Card to the position
	 * @param card Reference of the card to add
	 */
	public void setCard(Card card)
	{
		synchronized(guard)
		{
			this.card				=card;
			this.positionHasCard	=(this.card!=null);
		}
	}
	
	
	
	/**
	 * This method empties the position, i.e. removes the card, if any
	 */
	public void removeCard()
	{
		synchronized(guard)
		{
			this.card				=null;
			this.positionHasCard	=false;
		}
	}
	
	
	
	/**
	 * This method returns the card on the position
	 * @return The card or null if the position is empty
	 */
	public Card getCard()
	{	
		Card returnCard;
		synchronized(guard)
		{
			returnCard=this.card;
		}
		return returnCard;
	}
	
	
	/**
	 * This method returns whether the card on the position is tagged, i.e. is 
	 * selected by the user
	 * @return True if tagged, false if no.
	 */
	public boolean isTagged()
	{
		boolean isTagged;
		
		synchronized(guard)
		{
			isTagged=this.cardIsTagged;
		}
		return isTagged;
	}
	
	public boolean hasCard()
	{
		boolean hasCard;
		
		synchronized(guard)
		{
			hasCard=this.card!=null;
		}
		return hasCard;
	}
	
	
	/**
	 * This method sets the tagging of the Card on the position
	 * @param tagged True if tagged, false removes tagging
	 */
	public void setTagging(boolean tagged)
	{
		synchronized(guard)
		{
			cardIsTagged=tagged;
		}
	}	
	
}
