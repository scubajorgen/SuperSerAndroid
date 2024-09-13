package net.studioblueplanet.superset.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;
import android.view.View;


import net.studioblueplanet.superset.gameplay.Card;
import net.studioblueplanet.superset.gameplay.Set;
import net.studioblueplanet.superset.Config;

import net.studioblueplanet.superset.util.SymbolSet;

/**
 * This class represents the playfield content
 * @author jorgen
 *
 */
/**
 * @author jorgen
 *
 */
public class PlayField extends VirtualPlayField
{
	/** The one and only singleton instance of this class */
	private static PlayField 				theInstance=null;
	
	public enum PlayFieldAction
	{
		ACTION_PLAYPRESSED,
		ACTION_PAUSEPRESSED,
		ACTION_STOPPRESSED,
		ACTION_NOSETPRESSED,
		ACTION_CARDTOUCHED,
		ACTION_THREECARDSTOUCHED,
		ACTION_NONE
	}
	
	private int 							canvasHeight;
	private int 							canvasWidth;
	private int								cardHeight;
	private int								cardWidth;
	private int								playFieldHeight;
	private int								playFieldWidth;
	private int								playFieldX;
	private int								playFieldY;


	private boolean							visible;

	private boolean							initGraphics;

	private PlayFieldBar					bar;
	
	/** The view that is being used for displaying. May be set to null if no view available!! */
	private View							view;

	private Bitmap							offlineBitmap;
	private Canvas							offlineCanvas;
	
	
	private BitmapFactory.Options 			options;
	private Bitmap 							backgroundImage;
	private Rect							canvasRect;
	private Rect							cardFieldRect;
	private Rect							barRect;
	private Paint							backgroundPaint;
	
	
	private static int						debugCount=0;
	private static long						averageTime=0;
	private static long						minTime=1000000000000L;
	private static long						maxTime=-1000000000000L;
	
	private Rect							dirtyRect;
	private boolean							isDirty;
	
	
	// Debuggging
	private int								invalidateCount=0;
	private int								redrawCount=0;
	private long							startTime;
	private long							endTime;
	
	
	/* *********************************************************************************************\
	 * Constructor
	\* *********************************************************************************************/


	/**
	 *  Constuctor. Initialises variables, resets (=empties) the play field
	 */
	private PlayField()
	{
		int 				row, col;
		PlayFieldPosition 	position;
	
		// Add the card positions
		row=0;
		while (row<Config.MAX_ROWS)
		{
			col=0;
			while (col<Config.MAX_COLS)
			{
				position=new PlayFieldPosition();
				position.setVisible(false);
				positions[row][col]=position;
				col++;
			}
			row++;
		}
		
		// Add the commandbar
		bar=new PlayFieldBar(this);
	
		canvasHeight	=0;
		canvasWidth		=0;
		cardHeight		=0;
		cardWidth		=0;
		playFieldHeight	=0;
		playFieldWidth	=0;
		playFieldX		=0;
		playFieldY		=0;
	
		
		view			=null;
		
		dirtyRect		=new Rect();
		isDirty			=false;

		// Trigger first time initialising of graphics
		initGraphics	=true;
		
	}
	
	


	/* *********************************************************************************************\
	 *  Private methods
	\* *********************************************************************************************/

	/**
	 *  This method loads the background image
	 */
	private void loadBackgroundImage(int screenWidth, int screenHeight)
	{
		Bitmap rawBackgroundImage;
		
		if (backgroundImage==null)
		{
			// Load the bitmap file
			options=new BitmapFactory.Options();
			rawBackgroundImage=BitmapFactory.decodeResource(view.getResources(), net.studioblueplanet.superset.R.drawable.background, options);
			
			// create a scaled bitmap exactly the size of the screen
			backgroundImage=Bitmap.createScaledBitmap(rawBackgroundImage, screenWidth, screenHeight, true);
			backgroundPaint=new Paint();
		}				
	}
	
	/**
	 * Create the offline image.
	 * @param width Width of the image
	 * @param height Height of the image
	 */
	private void createOfflineImage(int screenWidth, int screenHeight)
	{
		Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
		offlineBitmap = Bitmap.createBitmap(screenWidth, screenHeight, conf); // this creates a MUTABLE bitmap
		offlineCanvas = new Canvas(offlineBitmap);
		
		// Point of starting: draw the background image to the canvas
		if (backgroundImage!=null)
		{
			offlineCanvas.drawBitmap(backgroundImage, 0, 0, backgroundPaint);
		}
		
		bar.paintBar(offlineCanvas);
	}
	
	
	/**
	 * This method initialises the graphics parameters.
	 * @param screenCanvas The canvas representing the screen
	 */
	private void initialiseGraphicsEnvironment(Canvas screenCanvas)
	{
		int 						row;
		int 						col;
		Rect    					rt;
		PlayFieldPosition 			pos;
		int							barHeight;
		int							positionWidth;
		int							positionHeight;
	
		// If playfield dimensions changed, recalculate card coordinates
		// and recreate the 
	

	
		// The dimensions of the canvas
		// The Canvas holds the status bar at the top and the card field below it
		canvasHeight=screenCanvas.getHeight();
		canvasWidth=screenCanvas.getWidth();

		
		// The canvas
		canvasRect=new Rect();
		canvasRect.left=0;
		canvasRect.top=0;
		canvasRect.right=canvasWidth;
		canvasRect.bottom=canvasHeight;
		
		
		// The dimensions of the Card Field. It is chosen such that the card field
		// has a fixed aspect ratio, independent of the screen size. In other words:
		// the field is fit on the screen
		if (canvasHeight*(Config.CARDFIELD_RELATIVE_HEIGHT)*Config.CARDFIELD_ASPECT>canvasWidth)
		{
			playFieldWidth =canvasWidth;
			playFieldHeight=(int)(canvasWidth/Config.CARDFIELD_ASPECT);
			playFieldX=0;
			playFieldY=(int)(canvasHeight*Config.STATUSBAR_RELATIVE_HEIGHT);
		}
		else
		{
			playFieldHeight=(int)(canvasHeight*Config.CARDFIELD_RELATIVE_HEIGHT);
			playFieldWidth=(int)(Config.CARDFIELD_ASPECT*playFieldHeight);
			playFieldX=(canvasWidth-playFieldWidth)/2;
			playFieldY=(int)(canvasHeight*Config.STATUSBAR_RELATIVE_HEIGHT);
		}
		
		cardFieldRect			=new Rect();
		cardFieldRect.left		=playFieldX;
		cardFieldRect.right		=playFieldX+playFieldWidth;
		cardFieldRect.top		=playFieldX;
		cardFieldRect.bottom	=playFieldX+playFieldWidth;
		
		positionWidth=playFieldWidth/Config.MAX_COLS;
		positionHeight=playFieldHeight/Config.MAX_ROWS;
		
		// The actual card dimension
		cardWidth=(int)(playFieldWidth/Config.MAX_COLS*Config.CARD_RELATIVE_WIDTH);
		cardHeight=(int)(playFieldHeight/Config.MAX_ROWS*Config.CARD_RELATIVE_HEIGHT);
		

		PlayFieldPosition.setCardDimension(cardWidth, cardHeight);

		barHeight=(int)(canvasHeight*Config.STATUSBAR_RELATIVE_HEIGHT);
		barRect=new Rect();
		bar.left=0;
		bar.top=0;
		bar.right=canvasWidth;
		bar.bottom=barHeight;
		bar.setDimensions(	0					, 0, 
							canvasWidth	        , barHeight);
 
		row=0;
		while (row<Config.MAX_ROWS)
		{
			col=0;
			while (col<Config.MAX_COLS)
			{
				pos=(PlayFieldPosition)positions[row][col];
				
				rt=new Rect();
				rt.left		=(int)(playFieldX+col*playFieldWidth/Config.MAX_COLS);
				rt.top		=(int)(playFieldY+row*playFieldHeight/Config.MAX_ROWS);
				rt.right	=rt.left+positionWidth;
				rt.bottom	=rt.top+positionHeight;
				pos.setPositionDimension(rt);
				
				
				pos=(PlayFieldPosition)positions[row][col];
				pos.setCardDimension(rt);				rt=new Rect();
				rt.left		=(int)(playFieldX+(col+Config.CARD_RELATIVE_HOR_PADDING)*playFieldWidth/Config.MAX_COLS);
				rt.top		=(int)(playFieldY+(row+Config.CARD_RELATIVE_VER_PADDING)*playFieldHeight/Config.MAX_ROWS);
				rt.right	=rt.left+cardWidth;
				rt.bottom	=rt.top+cardHeight;
				pos.setCardDimension(rt);

				col++;
			}
			row++;
		}

		
		// Load the background image
		loadBackgroundImage(canvasWidth, canvasHeight);
		
		// Create the offline image. All drawing is done to this image
		// This image is displayed to the screen in onDraw()
		createOfflineImage(canvasWidth, canvasHeight);
		
		
		initGraphics=false;

	}
	
	
	/**
	 * Invalidates a patch of the screen
	 * @param dirtyPatch Rectangle to add to dirty area
	 */
	public void invalidate(Rect dirtyPatch)
	{
		synchronized(this)
		{
			if (view==null)
			{
				// If there is no view, invalidate the entire canvas
				// so it gets redrawn the next time a view is available
				dirtyRect.left=0;
				dirtyRect.top=0;
				dirtyRect.right=canvasWidth;
				dirtyRect.bottom=canvasHeight;
				isDirty=true;				
			}
			else
			{
				if (dirtyPatch==null)
				{
					dirtyRect.left=0;
					dirtyRect.top=0;
					dirtyRect.right=canvasWidth;
					dirtyRect.bottom=canvasHeight;
					isDirty=true;
					view.postInvalidate();
				}
				else
				{
					if (isDirty)
					{
						dirtyRect.left	=Math.min(dirtyRect.left, dirtyPatch.left);
						dirtyRect.top	=Math.min(dirtyRect.top, dirtyPatch.top);
						dirtyRect.right	=Math.max(dirtyRect.right, dirtyPatch.right);
						dirtyRect.bottom=Math.max(dirtyRect.bottom, dirtyPatch.bottom);
					}
					else
					{
						dirtyRect.left=dirtyPatch.left;
						dirtyRect.top=dirtyPatch.top;
						dirtyRect.right=dirtyPatch.right;
						dirtyRect.bottom=dirtyPatch.bottom;
						isDirty=true;
						view.postInvalidate();
					}
				}
			}
			invalidateCount++;
		}
		
		
	}

	
	/* *********************************************************************************************\
	 * Public methods
	\* *********************************************************************************************/	
	
	/**
	 * This method returns the one and only instance of this Singleton class.
	 * @return The instance.
	 */
	public static PlayField getInstance()
	{
		if (theInstance==null)
		{
			theInstance=new PlayField();
		}
		return theInstance;
	}
	
	/**
	 * Sets the current view that displays the PlayField. Set to null if no view available.
	 * @param view The view to use or null if no View available
	 */
	public void setCurrentView(View view)
	{
		this.view=view;
		if (view!=null)
		{
			PlayFieldPosition.setContext(view.getContext());
		}
		else
		{
			PlayFieldPosition.setContext(null);
		}
	}

	
	/**
	 * This method sets the color values to use
	 * @param redColor The color for 'red' cards
	 * @param blueColor The color for 'blue' cards
	 * @param purpleColor The color for 'purple' cards
	 */
	public void setColors(int redColor, int blueColor, int purpleColor)
	{
		PlayFieldPosition.setColors(redColor, blueColor, purpleColor);
	}
	
	
	/**
	 * This method sets the symbolset to use
	 * @param symbolSet The symbol set
	 */
	public void setSymbolSet(SymbolSet symbolSet)
	{
		PlayFieldPosition.setSymbolSet(symbolSet);
	}	
	
	
	// TODO: find a nice solution in overriding addCardToFreePosition...
	/**
	 * This method adds a card. It finds the most logical position to 
	 * add it to (first in the center, if center is full to the right
	 * or left)
	 * @param card Card to add
	 * @return True if added, false if not. False should never happen...
	 */
	public boolean addCard(Card card)
	{
		boolean found;
		PlayFieldPosition position;
		
		found=false;
		
		position=(PlayFieldPosition)this.addCardToFreePosition(card);
		
		if (position!=null)
		{
			position.drawCard(offlineCanvas);
			this.invalidate(position.getPositionDimension());
			found=true;
		}
		
		return found;
	}
	
	/**
	 *  This method resets the playfield. This means 
	 *  emptying the positions and untagging all positions.
	 */
	@Override
	public void resetPlayField()
	{
		super.resetPlayField();
	
		this.redrawCards();
	}
	
	
	/**
	 *  This method returns the number of cards on the playfield.
	 *  @return The number of cards on the playfield
	 */
	public int  getNumberOfCards()
	{
		int row;
		int col;
		int number;
	
		number=0;
		row=0;
		while (row<Config.MAX_ROWS)
		{
			col=0;
			while (col<Config.MAX_COLS)
			{
				if (positions[row][col].hasCard())
				{
					number++;
				}
				col++;
			}
			row++;
		}
		return number;
	}	
	
	/**
	 * Triggers the PlayField to inialises its graphics environment
	 * next time the PlayField is drawn.
	 */
	public void initialiseGraphics()
	{
		initGraphics=true;
	}
	
	
	
	
	/**
	 *  This routine draws the playfield. 
	 *  @param hWnd Reference to the window
	 *  @param ps   Paintstructure
	 *  @param hdc  hdc
	 */
	public void paintPlayfield(Canvas screenCanvas)
	{
		Rect localDirtyRect;
		
		synchronized(this)
		{
			localDirtyRect=this.dirtyRect;
			isDirty=false;
/*			
			redrawCount++;
			if ((redrawCount%10)==0)
			{
				System.out.println(String.format("Invalidates %d redraws %d\n", invalidateCount, redrawCount));
			}
*/	
		}
	
		if (initGraphics)
		{
			this.initialiseGraphicsEnvironment(screenCanvas);
		}
		
		// Display the offline image
		if (offlineBitmap!=null)
		{
			// This is the strictly necessary part
//			screenCanvas.drawBitmap(offlineBitmap, localDirtyRect, localDirtyRect, backgroundPaint);
			
			// This is the total screen
			screenCanvas.drawBitmap(offlineBitmap, 0, 0, backgroundPaint);
		}
	
	
	}
	
	
	/**
	 *  This method untags all tagged cards
	 */
	public void untagTaggedCards()
	{
		int					i;
		int					row;
		int					col;
		PlayFieldPosition	pos;
	
		i=0;													// parse tagged card
		while (i<Config.SET_SIZE)
		{
			row=taggedCards.row[i];
			col=taggedCards.col[i];
			if (row>=0 && col>=0)								// check if card really still tagged
			{
				this.setTagging(row, col, false);				// set tagging to false
				pos=(PlayFieldPosition)positions[row][col];
				pos.drawCard(offlineCanvas);					// redraw the card offline
				invalidate(pos.getPositionDimension());  		// invalidate the window for the card
			}
	
			i++;
		}
	}
	
	
	
	
	/**
	 *  This routine calculates the mouse/stylus event into some action
	 *  - A card may be tagged or untagged
	 *  - 3 cards may be tagged
	 *  @param mouseX
	 *  @param mouseY
	 *  @param event
	 *  @param taggedCards
	 *  @return The action taken
	 */
	public PlayFieldAction handleAction(int mouseX, int mouseY, int event)
	{
		PlayFieldAction		resultAction;
		int					row;
		int					col;
		boolean				hit;
		PlayFieldPosition	pos;
		Rect				rect;
		boolean				threeCardsTagged;
	

		resultAction=PlayFieldAction.ACTION_NONE;
		
		// First if the mouse is pressed down, check if a card is tagged
		if (event==MotionEvent.ACTION_DOWN || event==MotionEvent.ACTION_POINTER_DOWN)
		{
			hit=false;
			row=0;
			while ((row<Config.MAX_ROWS) && !hit && visible)
			{
				col=0;
				while ((col<Config.MAX_COLS) && !hit && visible)
				{
					// Check if the mouseclick was on this card
					pos=(PlayFieldPosition)positions[row][col];
					rect=pos.getPositionDimension();
					if (rect.left<mouseX && rect.right>mouseX &&
						rect.top<mouseY && rect.bottom>mouseY &&
						pos.hasCard())
					{
						hit=true;
	
						// toggle tagging
						threeCardsTagged=setTagging(row, col, !(pos.isTagged()));
					
						if (threeCardsTagged)
						{
							resultAction=PlayFieldAction.ACTION_THREECARDSTOUCHED;
						}

	
						pos.drawCard(offlineCanvas);	// redraw card offline
						invalidate(pos.getPositionDimension());	// Invalidate the position on the screen
					}
					col++;
				}
				row++;
			}
		}
	
		// If not, check if a bar button is pressed
		if (resultAction==PlayFieldAction.ACTION_NONE)
		{
			resultAction=bar.handleAction(offlineCanvas, mouseX, mouseY, event);
		}
	
		return resultAction;
	}
	
	
	/**
	 *  Draw the score string in the bar
	 *  @param scoreString The string to print
	 */
	public void drawScore(String scoreString)
	{
		bar.drawScore(offlineCanvas, scoreString);
		invalidate(barRect);
	}
	
	/**
	 * Overrules the default text size for the score string
	 * @param size The new size
	 */
	public void setScoreTextSize(float size)
	{
		bar.setScoreTextSize(size);
	}
	
	public BarButton getNoSetButton()
	{
		return bar.getNoSetButton();
	}
	
	/**
	 *  Redraw all cards and voids on the canvas
	 */
	public void redrawCards()
	{
		int					row;
		int					col;
		PlayFieldPosition	pos;
		Rect				dimension;
	
		// Draw the cards
		row=0;
		while (row<Config.MAX_ROWS)
		{
			col=0;
			while (col<Config.MAX_COLS)
			{
				pos=(PlayFieldPosition)positions[row][col];
				dimension=pos.getPositionDimension();
				
				// erase the old card, if any
				offlineCanvas.drawBitmap(backgroundImage, dimension,  dimension, backgroundPaint);
				
				// draw the card
				pos.drawCard(offlineCanvas);
				col++;
			}
			row++;
		}
	
		// Redraw cards
		invalidate(cardFieldRect);
	}
	
	/**
	 *  Remove the cards that are tagged as part of a SET. 
	 */
	@Override
	public void removeTaggedCards()
	{
		int					i;
		int					row;
		int					col;
		PlayFieldPosition	position;
		Rect				dimension;

		i=0;
		while (i<Config.SET_SIZE)
		{
			row=taggedCards.row[i];
			col=taggedCards.col[i];
			this.setTagging(row, col, false);
			position=(PlayFieldPosition)positions[row][col];
			position.removeCard();
			
			// Erase the position by painting the background to it
			dimension=position.getPositionDimension();
			offlineCanvas.drawBitmap(backgroundImage, dimension, dimension, backgroundPaint);
			this.invalidate(dimension);

			i++;
		}
	}
	
	
	/**
	 *  Make all cards visible
	 */
	public void showCards()
	{
		int row, col;
		PlayFieldPosition pos;
	
		visible=true;
		row=0;
		while (row<Config.MAX_ROWS)
		{
			col=0;
			while (col<Config.MAX_COLS)
			{
				pos=(PlayFieldPosition)positions[row][col];
				pos.setVisible(visible);
				col++;
			}
			row++;
		}
		redrawCards();

	}
	
	/**
	 *  Hide all cards
	 */
	public void hideCards()
	{
		int row, col;
		PlayFieldPosition pos;
	
		visible=false;
		row=0;
		while (row<Config.MAX_ROWS)
		{
			col=0;
			while (col<Config.MAX_COLS)
			{
				pos=(PlayFieldPosition)positions[row][col];
				pos.setVisible(visible);
				col++;
			}
			row++;
		}
		redrawCards();
	}
	
	
	
	/**
	 * This method returns the card position at indicated row/col
	 * @param row The row
	 * @param col The column
	 * @return The card position rectangle
	 */
	public Rect getCardPosition(int row, int col)
	{
		PlayFieldPosition  pos;
	
	    pos=(PlayFieldPosition)positions[row][col];
		return pos.getPositionDimension();
	}
	
	/**
	 *  Highlight the cards defined by the set parameter
	 *  @param set The set of cards to modify the highlight from
	 *  @param highlighted New highlighted state
	 */
	public void highlightSet(Set set, boolean highlighted)
	{
		int i;
		int row;
		int col;
		PlayFieldPosition pos;
	
		i=0;
		while (i<Config.SET_SIZE)
		{
			row=set.row[i];
			col=set.col[i];
			pos=(PlayFieldPosition)positions[row][col];
			pos.highlightCard(highlighted);
			pos.drawCard(offlineCanvas);
			invalidate(pos.getPositionDimension());
			i++;
		}
	}
	
	


	


	/**
	 * Sets whether the game is paused or playing
	 * @param isPaused True for pausing, false for playing
	 */
	public void setGamePaused(boolean isPaused)
	{

		bar.setGamePaused(offlineCanvas, isPaused);
		
	}

	/**
	 * Debugging function for timing: start stopwatch 
	 */
	public void startStopwatch()
	{
		startTime=System.nanoTime();		
	}
	
	
	/**
	 * Debugging function: Stop stopwatch. If repeatedly start/stop called, just print
	 * at given update frequency.
	 * @param updateFrequency Number of start/stops between subsequent println updates
	 */
	public void stopStopwatch(int updateFrequency)
	{
		endTime=System.nanoTime();
		averageTime=(long)(averageTime*0.8+(endTime-startTime)*0.2);
		if ((endTime-startTime)<minTime)
		{
			minTime=(endTime-startTime);
		}
		if ((endTime-startTime)>maxTime)
		{
			maxTime=(endTime-startTime);
		}
		
		debugCount++;
		if (debugCount>updateFrequency)
		{
			debugCount=0;
			System.out.println(String.format("Sample %d Ave %d, min %d, max %d", endTime-startTime ,averageTime, minTime, maxTime));
		}		
	}
}
