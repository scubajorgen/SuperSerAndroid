package net.studioblueplanet.superset.gameplay;

import net.studioblueplanet.superset.Config;
import net.studioblueplanet.superset.CurrentActivityRegister;
import net.studioblueplanet.superset.view.PlayField;
import net.studioblueplanet.superset.view.BarButton;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;


/**
 * This class executes the Single Player Game logic
 * @author jorgen
 *
 */
public class SinglePlayerGame extends Game
{
	/**
	 * This subclass represents the timer task, implementing the Runnable interface
	 * @author jorgen
	 *
	 */
	public class PlayTimerTask extends TimerTask
	{
		private SinglePlayerGame game;
		
		public PlayTimerTask(SinglePlayerGame game)
		{
			this.game=game;
		}
		
		@Override
		public void run()
		{
			game.timerCallback();
		}
		
	}

	/**
	 * This subclass implements the thead function that executes the simulation.
	 * @author jorgen
	 *
	 */
	public class SimulationThread implements Runnable
	{
		private SinglePlayerGame game;
		private Thread thread;
		
		
		public SimulationThread(SinglePlayerGame game)
		{
			this.game=game;
			this.thread=new Thread(this);
		}
		
		@Override
		public void run()
		{
			game.simulationThreadMethod();
		}
		
		public void startThread()
		{
			thread.start();
		}
		
	}	
	
	
	/** Timer timeout in milliseconds */
	private static final int 	TIMER_TIMEOUT=100;

	/** The score increment at t=0 */
	private static final double	INITIALSCOREINCREMENT=100.0;
	
	/** The penalty when touching three cards that are not a set */
	private static final double	INCORRECTSET_PENALTY=25.0;
	

	/** The full deck of cards */
	private Deck				deck;

	/** The cards that are exposed to the user */
//		Card[MAX_CARDS_IN_PLAY]		cardsInPlay[MAX_CARDS_IN_PLAY];

	/** The score */
	private double				score;

	/** Points added to the score when user hits a set  */
	private double				scoreIncrement;
	private int					scoreIncrementAsInt;
	
	/** Previous value of the score increment	 */
	private int					previousScoreIncrementAsInt;


	private int					numberOfSets;
	private ArrayList<Set>		sets;
	private int					highlightedSet;


//	private int					timerTickCount;
	private long				gameStartTime;
	private long				gameEndTime;
	private long				pauseStartTime;
	private long				pauseEndTime;
	private long				accumulatedPauseTime;

	private boolean				noSetFoundPressed;

	
	private SimulationThread    simThread;
	private boolean				simulating;
	private int         		simulatedGames;
	private int					simNumberPlayedEmpty;
	private int					simNumberTwiceAdded;
	private boolean				simTwiceAdded;

	private int         		easterEggStopCount;
	private int         		easterEggNoSetCount;

	private Timer				playTimer;
	private PlayTimerTask		playTimerTask;
    
    private	String 				buffer;
    
    private ScoreListener		scoreListener;


    private View				theView;
    
    
    /**
     * Constructor. Initializes the game
     * @param playField The PlayField instance to use
     */
    public SinglePlayerGame(PlayField playField)
    {
    	super(playField);

    	deck		        =new Deck();
    	state		        =PlayState.PLAYSTATE_STOPPED;	// Default state is stopped
  	
        simulating          =false;
        simulatedGames      =0;
        easterEggStopCount  =0;
        easterEggNoSetCount =0;
        
        theView				=null;
    }
    
    /**
     * This method sets the current View that displays the PlayField.
     * May be set to null if there is no current View.
     * @param view The view to use
     */
    public void setCurrentView(View view)
    {
    	this.theView=view;
    }
    
    /**
     * Sets the score listener. The ScoreListener function is called when the game
     * is finished.
     * @param scoreListener The listener
     */
    public void setScoreListener(ScoreListener scoreListener)
    {
    	this.scoreListener=scoreListener;
    }
    

 
    /*
     * This method prepars a new game. It must be called when 
     * a new game is started. It resets and shuffles the deck.
     * It empties the playfield, sets the cards to hidden.
     */
    void resetGame()
    {
    	deck.resetDeck();						// reset the Deck
    	deck.resetCards();						// put the card index to the top
    	deck.shuffleDeck();						// shuffle the deck

    	
    	playField.emptyPlayField();				// remove any left cards (from previous game)
    	playField.resetPlayField();				// empty the playfield
    	playField.hideCards();					// since the state is paused, pause the playfield
    	
    	score=0;								// reset the score
    	scoreIncrement=INITIALSCOREINCREMENT;	// reset score increment	
    	scoreIncrementAsInt=(int)INITIALSCOREINCREMENT;
    	previousScoreIncrementAsInt=-1;			// reset the score increment


    	highlightedSet=-1;
    	numberOfSets=0;
    	noSetFoundPressed=false;

    }


    /**
     * This method starts the game. The 1st cards are put on the playfield
     */
    public void startGame()
    {
    	int							i;
    	Card						card;
    	boolean						cardAdded;

    	resetGame();								// reset the game

    	i=0;										// Add cards to the play field
    	while (i<Config.CARDS_IN_PLAY)
    	{
    		card=deck.nextCard();
    		cardAdded=playField.addCard(card);
    		if (!cardAdded)
    		{
//    			Tools::notifyMessage(NULL, IDS_NOCARDADDED);
    		}
    		i++;
    	}

    	playField.showCards();						// Show the cards
      	playField.setGamePaused(false);				// set play state to the view

      	
      	
    	// register the start time of the game.
//    	timerTickCount=0;	
    	this.gameStartTime=System.nanoTime();
    	this.accumulatedPauseTime=0;

    	// Create timer
    	playTimerTask=new PlayTimerTask(this);
    	playTimer=new Timer();
    	
    	// Start the associated timer
    	playTimer.scheduleAtFixedRate(playTimerTask, 0, TIMER_TIMEOUT);
    	

    	updatePlayState(PlayState.PLAYSTATE_PLAYING);


    }

    /**
     * Sets the game to the paused state. The cards are hidden, the timer stops
     */
    public void pauseGame()
    {
    	playField.hideCards();
    	playField.setGamePaused(true);
    	updatePlayState(PlayState.PLAYSTATE_PAUSED);
    	this.pauseStartTime=System.nanoTime();
    }

    /**
     * This method resumes the game.
     */
    public void resumeGame()
    {
    	playField.showCards();
    	playField.setGamePaused(false);
    	updatePlayState(PlayState.PLAYSTATE_PLAYING);
    	this.pauseEndTime=System.nanoTime();
    	this.accumulatedPauseTime+=(this.pauseEndTime-this.pauseStartTime);
    }

    /**
     * This method exits the simulation, if a simulation is running.
     * It prevents the simulation thread executing the simulation.
     * The simulation thread finishes. It must be called when the game looses
     * focus.
     */
    public void exitSimulation()
    {
    	synchronized (this)
    	{
    		if (simulating)
    		{
    			simulating=false;
    	    	state		        =PlayState.PLAYSTATE_STOPPED;	// Default state is stopped
    	      	
    	        simulating          =false;
    	        simulatedGames      =0;
    	        easterEggStopCount  =0;
    	        easterEggNoSetCount =0;
    		}
    	}
    	
    }
    
    /**
     *  This method handles the user call to stop the game
     */
    void stopGame()
    {
    	PlayState 			localState;
    	AlertDialog.Builder alertDialogBuilder; 
    	AlertDialog 		alertDialog;

    	localState=getState();

    	switch (localState)
    	{
    	case PLAYSTATE_INITIALISING:
    	case PLAYSTATE_IDLE:
    	case PLAYSTATE_FINISHED:
    		// do nothing
    		break;
    	case PLAYSTATE_PLAYING:
    		pauseGame();
    		
   
			alertDialogBuilder = new AlertDialog.Builder((CurrentActivityRegister.getInstance()).getCurrentActivity());
	 
			// set title
			alertDialogBuilder.setTitle("Stop Game?");
 
			// set dialog message
			alertDialogBuilder
				.setMessage("Click yes to exit!")
				.setCancelable(false)
				.setPositiveButton("Yes",new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog,int id) 
					{
						// if this button is clicked, close
						// current activity
						executeStopGame();
					}
				})
				.setNegativeButton("No",new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						dialog.cancel();
						resumeGame(); 
					}
				});
 
				// create alert dialog
				alertDialog = alertDialogBuilder.create();
 
				// show it
				alertDialog.show();    		


    		break;

    	case PLAYSTATE_PAUSED:
			alertDialogBuilder = new AlertDialog.Builder((CurrentActivityRegister.getInstance()).getCurrentActivity());
	 
			// set title
			alertDialogBuilder.setTitle("Stop Game?");
 
			// set dialog message
			alertDialogBuilder
				.setMessage("Click yes to exit!")
				.setCancelable(false)
				.setPositiveButton("Yes",new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog,int id) 
					{
						// if this button is clicked, close
						// current activity
						executeStopGame();
					}
				})
				.setNegativeButton("No",new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						dialog.cancel();
						
					}
				});
 
				// create alert dialog
				alertDialog = alertDialogBuilder.create();
 
				// show it
				alertDialog.show();    		

    		break;

    	case PLAYSTATE_STOPPED:
//    		notifyGameFinished();
    		break;
    	}
    }
    
    /**
     * Do the stopping
     */
    private void executeStopGame()
    {
		// If cards highlighted, remove highlighting
		if (highlightedSet>=0)
		{
			playField.highlightSet(sets.get(highlightedSet), false);
			highlightedSet=-1;
		}
		// if cards tagged, remove tagging
		playField.untagTaggedCards();

		// stop the timer
		playTimer.cancel();
		playTimer.purge();

		// update the state
		updatePlayState(PlayState.PLAYSTATE_STOPPED);
		
		playField.drawScore("Game Finished!");

		// Notify main program game has finished
//		notifyGameFinished();      	
    }
    

    /**
     *  This method finishes the games if no sets are left and no more
     *  cards are left in the deck to play. 
     */
    void finishGame()
    {
    	int		numberLeft;
        String	buffer;

    	numberLeft=playField.getNumberOfCards();

    	// for each remaining card a score is received equal to the
    	// average score per played card

    	synchronized(this)
    	{
    		score+=numberLeft*score/(Deck.MAX_CARDS-numberLeft);
    	}
    	
    	playTimer.cancel();
    	playTimer.purge();
    	
    	playField.hideCards();
    	playField.drawScore(null); 
    	playField.setGamePaused(true);

    	updatePlayState(PlayState.PLAYSTATE_STOPPED);
    	
        if (!simulating)
        {
        	// Notify the acitivity to send the score 
        	// Usually this is done by a new dialog box.
        	scoreListener.onScore((int)score);
        	playField.drawScore("Game Finished!");
        }
        else
        {
    		// check if entire playfield played empty
    		if (playField.getNumberOfCards()==0)
    		{
    			simNumberPlayedEmpty++;
    		}

    		if (simTwiceAdded)
    		{
    			simNumberTwiceAdded++;
    		}

    		buffer=String.format("Score %d, %d played, %d empty, %d twice", 
    								(int)score, simulatedGames, simNumberPlayedEmpty, simNumberTwiceAdded);
    		
        	playField.drawScore(buffer);
            try
            {
            	Thread.sleep(2000);
            }
            catch (InterruptedException e)
            {
            	
            }
        }
    }


    /**
     *  This routine repaints the playfield. It is delegated to the playfield.
     */
/*    
    void paintPlayfield()
    {
    	playField.paintPlayfield();
    }
*/



    /**
     * This method checks if the easteregg sequence has been called, i.e.
     * Press NOSET three times, followed by STOP two times
     * @param action The action that is handled
     */
    void easterEggCheck(PlayField.PlayFieldAction action)
    {
        PlayState localState;
        
        switch (action)
        {
        
        case ACTION_PLAYPRESSED:
    		break;
        case ACTION_STOPPRESSED:
            if (easterEggNoSetCount==3)
            {
                easterEggStopCount++;
                if (easterEggStopCount==2)
                {
    		        localState=getState();
                    if (localState==PlayState.PLAYSTATE_STOPPED && !simulating)
                    {
                        simulateGame();
                    }
                    easterEggStopCount=0;
                    easterEggNoSetCount=0;
                }
            }
            else
            {
                easterEggStopCount=0;
                easterEggNoSetCount=0;
            }
            break;
        case ACTION_NOSETPRESSED:
             easterEggNoSetCount++;
             break;
        case ACTION_NONE:
            break;
        default:
            easterEggStopCount=0;
            easterEggNoSetCount=0;
            break;
        }
    }

    
    /**
     * This method hanldes the touch action of the user. Basically, it handles
     * the playfield bar buttons and when three cads are touched.
     * @param action
     */
    public void handleAction(PlayField.PlayFieldAction action)
    {
    	PlayState		localState;
    	
    	switch (action)
    	{
    	case ACTION_THREECARDSTOUCHED:
    		processThreeCardsTagged();
    		break;

    	case ACTION_PLAYPRESSED:
    		localState=getState();
    		if (localState==PlayState.PLAYSTATE_STOPPED)
    		{
    			startGame();
    		}
    		else if (localState==PlayState.PLAYSTATE_PAUSED)
    		{
    			resumeGame();			
    		}
    		break;

    	case ACTION_PAUSEPRESSED:
    		localState=getState();
    		if (localState==PlayState.PLAYSTATE_PLAYING)
    		{
    			pauseGame();
    		}
    		break;

    		
    	case ACTION_STOPPRESSED:
    		stopGame();
    		break;

    	case ACTION_NOSETPRESSED:
    		localState=getState();
    		if (localState==PlayState.PLAYSTATE_PLAYING)
    		{
    			processNoSetFound();
    		}
    		break;
    		
    	case ACTION_NONE:
    		break;
    	case ACTION_CARDTOUCHED:
    		break;
    	}
        easterEggCheck(action);
    	
    }
    
    


    /**
     *  This method implements the process that has to be
     *  followed when the user selects 3 cards as a set
     *
     */
    void processThreeCardsTagged()
    {
    	Card			card;
    	int				i;
    	PlayState		localState;
    	boolean			cardAdded;

    	// Ok, set is tagged
    	if (playField.isSetTagged())
    	{
    		synchronized(this)
    		{
    			// Update the score, reset the timer
	    		score+=scoreIncrement;
//	    		timerTickCount=0;
	    		gameStartTime=System.nanoTime();
	    		accumulatedPauseTime=0;
	    		noSetFoundPressed=false;
	    		localState=state;
    		}
    		
    		
    		playField.removeTaggedCards();
    		if (playField.getNumberOfCards()<Config.CARDS_IN_PLAY)
    		{
    			i=0;
    			while ((i<Config.SET_SIZE) && (localState==PlayState.PLAYSTATE_PLAYING))
    			{
    				synchronized(this)
    				{
    					card=deck.nextCard();
    				}

    				if (card==null)
    				{
    					if (playField.getNumberOfCards()==0)
    					{
    						finishGame();
    						// finishGame changes the state, so re-acquire the state
    						synchronized(this)
    						{
    							localState=state;
    						}
    					}
    				}
    				else
    				{
    					cardAdded=playField.addCard(card);
    					if (!cardAdded)
    					{
//    						Tools::notifyMessage(NULL, IDS_NOCARDADDED);
    					}
    				}
    				i++;
    			}
    		}
    	}
    	else
    	{
    		playField.untagTaggedCards();
    		synchronized(this)
    		{
    			score=Math.max(0, score-INCORRECTSET_PENALTY);
    		}
    	}
    	if (highlightedSet>=0)
    	{
    		playField.highlightSet(sets.get(highlightedSet), false);
    		highlightedSet=-1;
    	}
    }

    /**
     *  This method implements the process that has to be
     *  followed when the user presses the no-set-found 
     *  button.
     */
    void processNoSetFound()
    {
    	int		i;
    	Card	card;
    	boolean	cardAdded;

    	// check if a set is already highlighted
    	// if so, do nothing
    	if (highlightedSet<0)								
    	{
    		// find the possible sets
    		/* findSet(); */
    		sets=playField.findSet();
    		numberOfSets=sets.size();
    		
    		if (numberOfSets>0)
    		{
    			// pick a random one
    			highlightedSet=(int)(Math.random()*numberOfSets);
    			playField.highlightSet(sets.get(highlightedSet), true);
    			// indicate that no-set-found has been pressed incorrectly
    			synchronized(this)
    			{
    				noSetFoundPressed=true;
    			}
    		}
    		else
    		{
    			// In an extremely rare case (perhaps impossible case) it may be possible that MAX_CARDS_IN_PLAY
    			// (18) cards do not contain a set. In that case the game ends, since the
    			// playfield simply cannot contain more cards
    			if (playField.getNumberOfCards()>=Config.MAX_CARDS_IN_PLAY)
    			{
//    				Tools::errorMessage(hWnd, IDS_PLAYFIELDTOSMALL);
    				if (simulating)
    				{
    					this.simulating=false;
    				}
    			}
    			else
    			{
    				i=0;
    				while ((i<Config.SET_SIZE) && (state==PlayState.PLAYSTATE_PLAYING))
    				{
    					synchronized(this)
    					{
    						card=deck.nextCard();
    					}
    					if (card==null)
    					{
    						finishGame();
    					}
    					else
    					{
    						cardAdded=playField.addCard(card);
    						if (!cardAdded)
    						{
//    							Tools::notifyMessage(NULL, IDS_NOCARDADDED);
    						}
    					}
    					i++;
    				}
    				// Reset the score increment
    				synchronized(this)
    				{
//    		    		timerTickCount=0;
    		    		gameStartTime=System.nanoTime();
    		    		accumulatedPauseTime=0;
    				}
    				if (playField.getNumberOfCards()==Config.MAX_CARDS_IN_PLAY)
    				{
    					simTwiceAdded=true;
    				}
    			}
    		}
    	}
    }


    /**
     *  This method should called during the playing every TIMER_TIMEOUT ms.
     *  It recalculates the score increment (which decreases in time).
     *  It displays the score and score increment
     *
     */
    private void timerCallback()
    {
    	boolean updateScreen=false;
/*   	
if ((timerTickCount%10)==0)
{
System.out.println(Long.toString(System.nanoTime())+", "+Double.toString(scoreIncrement)+", "+scoreIncrementAsInt);   	
}
*/
    	synchronized(this)
    	{
	    	if (state==PlayState.PLAYSTATE_PLAYING)
	    	{
//	    		timerTickCount++;
	    		gameEndTime=System.nanoTime();
	
	    		if (noSetFoundPressed)
	    		{
	    			scoreIncrement=0;
	    		}
	    		else
	    		{

	    			// Calculate score increment based on the tick count
//	    			scoreIncrement=99.0*Math.exp(-0.046*timerTickCount*TIMER_TIMEOUT/1000)+1.49;
	    			
	    			// Calculate score increment based on clock time. This should be more accurate, since
	    			// it may account for inaccuracies in the timer function. In practice this is neglegible
	    			scoreIncrement=99.0*Math.exp(-0.046*(gameEndTime-gameStartTime-accumulatedPauseTime)/1E9)+1.49;
	    			
	    		}
	
	    		scoreIncrementAsInt=(int)scoreIncrement;
	
	    		if (scoreIncrementAsInt!=previousScoreIncrementAsInt)
	    		{
		    		if (noSetFoundPressed)
		    		{
		    			buffer=String.format("Deck %2d    Score %4d+%3d  Sets %2d", deck.cardsInDeck(), (int)score, scoreIncrementAsInt, numberOfSets);
		    		}
		    		else
		    		{
		    			buffer=String.format("Deck %2d    Score %4d+%3d", deck.cardsInDeck(), (int)score, scoreIncrementAsInt);
		    		}
		    		previousScoreIncrementAsInt=scoreIncrementAsInt;
		    		updateScreen=true;
	    		}	    		
	    		
	    	}
	    	else
	    	{
	    		buffer=null;
	    	}
    	}
    		

    	if (updateScreen)
    	{
			playField.drawScore(buffer);
			
		}

    }








    /**
     * This method executes the simulation
     */
    private void simulationThreadMethod()
    {
        int             numberOfSets;
        boolean         finished;
        int             x, y;
        Set				set;
        Rect			cardRect;
        int				i;
        BarButton		noSetButton;
        boolean			isSimulating;
        
//        PlayFieldAction		resultAction; 
        MotionEvent motionEvent;

        playField.setScoreTextSize(30.0f);
        noSetButton=playField.getNoSetButton();
        
        synchronized(this)
        {
        	isSimulating=this.simulating;
        }
        
        while (isSimulating)
        {
            startGame();
            simulatedGames++;
    		simTwiceAdded=false;
        
            finished=false;
            while (!finished && state==PlayState.PLAYSTATE_PLAYING && isSimulating)
            {
                sets=playField.findSet();
                numberOfSets=sets.size();
                if (numberOfSets>0)
                {
            		set=sets.get(0);
                	i=0;
                	while (i<Config.SET_SIZE)
                	{
                		cardRect=playField.getCardPosition(set.row[i], set.col[i]); 
                		
                		// Simulate the user tagging three cards
                		x=cardRect.left+1;
                		y=cardRect.top+1;
                		

                		motionEvent = MotionEvent.obtain(
                		    0, 
                		    0, 
                		    MotionEvent.ACTION_DOWN, 
                		    x, 
                		    y, 
                		    0
                		);

                		// Dispatch touch event to view
                		if (theView!=null)
                		{
                			theView.dispatchTouchEvent(motionEvent);                		
                		}
                		/*
                		resultAction=playField.handleAction(x, y, MotionEvent.ACTION_DOWN);
                		
                		// Handle the action
                		this.handleAction(resultAction);
                		*/
                		i++;
                	}	
                    

                }
                else
                {
/*                	
                    processNoSetFound();
*/                	
                	
            		// Simulate the user tagging three cards
            		x=noSetButton.left+1;
            		y=noSetButton.top+1;
            		

            		motionEvent = MotionEvent.obtain(
            		    0, 
            		    0, 
            		    MotionEvent.ACTION_DOWN, 
            		    x, 
            		    y, 
            		    0
            		);

            		// Dispatch touch event to view
            		if (theView!=null)
            		{
            			theView.dispatchTouchEvent(motionEvent);                		
            		} 

                    // Sleep 200 ms to delay and give other processes some time 
                    try
                    {
                    	Thread.sleep(200);
                    }
                    catch (InterruptedException e)
                    {
                    	
                    }
                    
            		motionEvent = MotionEvent.obtain(
                		    0, 
                		    0, 
                		    MotionEvent.ACTION_UP, 
                		    x, 
                		    y, 
                		    0
                		);

                		// Dispatch touch event to view
                		if (theView!=null)
                		{
                			theView.dispatchTouchEvent(motionEvent);                		
                		}                	
            		
                }
                // Sleep 200 ms to delay and give other processes some time 
                try
                {
                	Thread.sleep(200);
                }
                catch (InterruptedException e)
                {
                	
                }
                
                synchronized(this)
                {
                	isSimulating=this.simulating;
                }

            }
        }


    }


    
    /**
     * This method starts the simulation (easter egg)
     */
    private void simulateGame()
    {
        simulating			=true;
        simulatedGames		=0;
    	simNumberPlayedEmpty=0;
    	simNumberTwiceAdded	=0;

    	simThread=new SimulationThread(this);
    	simThread.startThread();

        
    }

    
	
}
