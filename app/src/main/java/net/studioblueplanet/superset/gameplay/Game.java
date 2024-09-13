package net.studioblueplanet.superset.gameplay;

import net.studioblueplanet.superset.view.PlayField;

/**
 * Game class. Represents a game. Basically it implements the state behaviour.
 * @author jorgen
 *
 */
public class Game
{
	protected enum PlayState
	{
		PLAYSTATE_IDLE,
		PLAYSTATE_INITIALISING,
		PLAYSTATE_STOPPED,
		PLAYSTATE_PLAYING,
		PLAYSTATE_PAUSED,
		PLAYSTATE_FINISHED
	};


	/**
	 *   Message box caption. Scratch.
	 */
	protected static String		caption;

	/**
	 *   Message message. Scratch.
	 */
	protected static String		message;


	/** State of the game */
	protected PlayState			state;


	/** Playfield, containing bar and SET cards */
	PlayField					playField;	

	
	/**
	 * Constructor
	 * @param playField The PlayField to use.
	 */
	public Game(PlayField playField)
	{
		state					=PlayState.PLAYSTATE_IDLE;
		this.playField          =playField;
	}






	/**
	 * Update the state of the game
	 * @param newState New state of the game
	 */
	protected void updatePlayState(PlayState newState)
	{
		synchronized(this)
		{
			state=newState;
		}
	}


	
	/**
	 * This method returns the game state (thread safe)
	 * @return The state of the game.
	 */
	PlayState getState()
	{
		PlayState theState;


		synchronized(this)
		{
			theState=state;
		}

		return theState;
	}




	
}
