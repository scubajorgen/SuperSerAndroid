package net.studioblueplanet.superset.view;

import android.view.View;
import android.view.MotionEvent;
import android.content.Context;
import android.graphics.Canvas;

import net.studioblueplanet.superset.gameplay.ScoreListener;
import net.studioblueplanet.superset.gameplay.SinglePlayerGame;
import net.studioblueplanet.superset.util.SymbolSet;

/**
 * This class represents the view to be used for the single player game
 * @author jorgen
 *
 */
public class SinglePlayerGameView extends View
{

	/** The PlayField. The table that holds the cards. It is persistent throughout the instances of the view. */
	private static	PlayField			playField=null;
	
	/** The SinglePlayerGame. Responsible for executing the game. It is persistent throughout the instances of the view. */
	private static SinglePlayerGame theGame=null;
	
	private int							activePointerId=-1;
	
	/**
	 * The constructor
	 * @param context Context of the activity
	 * @param scoreListener The activity to notify when the game is finsihed and there is 
	 *                      a score to send
	 */
	public SinglePlayerGameView(Context context, ScoreListener scoreListener)
	{
		super(context);

        if (playField!=null && theGame!=null)
        {
        	playField.setCurrentView(this);
        	theGame.setCurrentView(this);
        }
        else
        {
        	playField=PlayField.getInstance();
        	playField.setCurrentView(this);
        	theGame=new SinglePlayerGame(playField);
        	theGame.setCurrentView(this);
        }


        theGame.setScoreListener(scoreListener);
        
        // Prevent screen from sleeping
        this.setKeepScreenOn(true);
        
       
    }
	
	

	
	/**
	 * Sets the game to paused. Call when activity looses focus
	 */
	public void pause()
	{
		theGame.handleAction(PlayField.PlayFieldAction.ACTION_PAUSEPRESSED);
		
		// Signal current view has lost focus or has been destroyed
		playField.setCurrentView(null);
		theGame.setCurrentView(null);
		
		// Make sure the simulation thread does not keep running in the background
		theGame.exitSimulation();
	}
	
	
	/**
	 * Resumes the game, after focus has been lost. We leave the game in paused state
	 * but we update the view (the view may have been changed).
	 */
	public void resume()
	{
//		theGame.handleAction(PlayFieldAction.ACTION_PLAYPRESSED);
		playField.setCurrentView(this);
		theGame.setCurrentView(this);
	}

	
	/**
	 * This method sets the color values to use
	 * @param redColor The color for 'red' cards
	 * @param blueColor The color for 'blue' cards
	 * @param purpleColor The color for 'purple' cards
	 */
	public void setColors(int redColor, int blueColor, int purpleColor)
	{
		playField.setColors(redColor, blueColor, purpleColor);
	}
	
	
	/**
	 * This method sets the symbolset to use
	 * @param symbolSet The symbol set
	 */
	public void setSymbolSet(SymbolSet symbolSet)
	{
		playField.setSymbolSet(symbolSet);
	}
	
	
	
	/** 
	 * This overridden method redraws the canvas. It is delegated to the 
	 * PlayField, that contains the methods to paint the playfield
	 */
	@Override
	protected void onDraw(Canvas canvas)
	{
			
		playField.startStopwatch();		
//		super.onDraw(canvas);
		

		playField.paintPlayfield(canvas);

		
		playField.stopStopwatch(100);		
	}	

	
	
	/* (non-Javadoc)
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		boolean 					hasConsumedEvent;
		int 						x;
		int 						y;
		int 						motionEvent;
		PlayField.PlayFieldAction 	action;
		int							pointerIndex;
		int							pointerId;
		int							newPointerIndex;
		
		hasConsumedEvent=true;
		motionEvent=event.getAction();
		

		switch (motionEvent & MotionEvent.ACTION_MASK)
		{
		case MotionEvent.ACTION_DOWN:
	        // Save the ID of this pointer
	        activePointerId = event.getPointerId(0);
			x=(int)event.getX();
			y=(int)event.getY();
			action=playField.handleAction(x, y, motionEvent);
			theGame.handleAction(action);
			break;
		case MotionEvent.ACTION_UP:
			activePointerId=-1;
			x=(int)event.getX();
			y=(int)event.getY();
			action=playField.handleAction(x, y, motionEvent);
			theGame.handleAction(action);
			break;
			// next finger
		case MotionEvent.ACTION_POINTER_DOWN:
// TODO Check this piece of code...			
	        // Extract the index of the pointer touches the sensor
	        pointerIndex = (motionEvent & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
	        pointerId    = event.getPointerId(pointerIndex);
	        if (pointerId != activePointerId) 
	        {
	            x = (int)event.getX(pointerIndex);
	            y = (int)event.getY(pointerIndex);
	            activePointerId = event.getPointerId(pointerIndex);
				action=playField.handleAction(x, y, MotionEvent.ACTION_DOWN);
				theGame.handleAction(action);
	        }
			
			break;
		default:
			
			break;
		}
		
		
		return hasConsumedEvent;
	}
	

}
