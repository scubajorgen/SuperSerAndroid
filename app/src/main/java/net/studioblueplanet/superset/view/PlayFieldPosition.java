package net.studioblueplanet.superset.view;

import net.studioblueplanet.superset.gameplay.Card;
import net.studioblueplanet.superset.util.SymbolSet;
import net.studioblueplanet.superset.Config;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Shader;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.ColorMatrix;


/**
 * This class represents the actual playfield position. It is responsible 
 * for drawing the position and card upon it.
 * 
 * @author jorgen
 *
 */
public class PlayFieldPosition extends VirtualPlayFieldPosition
{
	public static final int X=0;
	public static final int Y=1;

	public static final int CARDPOLYGONPOINTS		=28;
	public static final int WAVEPOLYGONPOINTS		=20;
	public static final int DIAMONDPOLYGONPOINTS 	=4;
	public static final int TANKPOLYGONPOINTS		=22;
	
	// This value should be the max of the above values
	public static final int MAXPOLYGONPOINTS		=28;	


	private static final int[][]		cardPolygonTemplate	=	
		{
		{10000,  9000},
		{ 9966,  9259},
		{ 9866,  9500},
		{ 9707,  9707},
		{ 9500,  9866},
		{ 9259,  9966},
		{ 9000, 10000},

		{ 1000, 10000},
		{  741,  9966},
		{  500,  9866},
		{  293,  9707},
		{  134,  9500},
		{	34,  9259},
		{	 0,  9000},

		{	 0,  1000},
		{	34,   741},
		{  134,   500},
		{  293,   293},
		{  500,   134},
		{  741,    34},
		{ 1000, 	0},

		{  9000,	0},
		{  9259,   34},
		{  9500,  134},
		{  9707,  293},
		{  9866,  500},
		{  9966,  741},
		{ 10000, 1000}

	};	
	
	private static final int[][]	wavePolygonTemplate=
	{
		{-47, 24},
		{-52, 18},
		{-54,  4},
		{-46,-15},
		{-35,-23},
		{-18,-23},
		{ 13,-15}, 
		{ 23,-14}, 
		{ 32,-16}, 
		{ 40,-22}, 
		{ 47,-24}, 

		{ 52,-18},
		{ 54, -4},
		{ 46, 15},
		{ 35, 23},
		{ 18, 23},
		{-13, 15}, 
		{-23, 14}, 
		{-32, 16}, 
		{-40, 22} 


	};
	
	private static final int[][]	diamondPolygonTemplate=
	{
		{-500,	  0},
		{	0, -500},
		{ 500,	  0},
		{	0,	500}
	};
	
	
	
	private static final int[][]	tankPolygonTemplate=
	{
		{ 2000,-1000},
		{ 2309, -951},
		{ 2588, -809},
		{ 2809, -588},
		{ 2951, -309},
		{ 3000,    0},
		{ 2951,  309},
		{ 2809,  588},
		{ 2588,  809},
		{ 2309,  951},
		{ 2000, 1000},

		{-2000, 1000},
		{-2309,  951},
		{-2588,  809},
		{-2809,  588},
		{-2951,  309},
		{-3000,    0},
		{-2951, -309},
		{-2809, -588},
		{-2588, -809},
		{-2309, -951},
		{-2000,-1000}


	};
	
	private static final int[][]	rectanglePolygonTemplate=
	{
		{ 1000,  1000},
		{ 1000, -1000},
		{-1000, -1000},
		{-1000,  1000}
	};

	private static final int[][]	ellipsePolygonTemplate=
	{
		{ 1000,	    0},
		{  985,	  174},
		{  940,	  342},
		{  866,	  500},
		{  766,   643},
		{  643,	  766},
		{  500,	  866},
		{  342,	  940},
		{  174,	  985},
		{    0,	 1000},
		{ -174,	  985},
		{ -342,	  940},
		{ -500,	  866},
		{ -643,	  766},
		{ -766,	  643},
		{ -866,	  500},
		{ -940,	  342},
		{ -985,	  174},
		{-1000,	    0},
		{ -985,	 -174},
		{ -940,	 -342},
		{ -866,	 -500},
		{ -766,	 -643},
		{ -643,	 -766},
		{ -500,	 -866},
		{ -342,	 -940},
		{ -174,	 -985},
		{    0,	-1000},
		{  174,	 -985},
		{  342,	 -940},
		{  500,	 -866},
		{  643,	 -766},
		{  766,	 -643},
		{  866,	 -500},
		{  940,	 -342},
		{  985,	 -174}
	};



	private static final Polygon  cardPolygon		=new Polygon(cardPolygonTemplate);
	private static final Polygon  wavePolygon		=new Polygon(wavePolygonTemplate);
	private static final Polygon  diamondPolygon	=new Polygon(diamondPolygonTemplate);
	private static final Polygon  rectanglePolygon	=new Polygon(rectanglePolygonTemplate);
	private static final Polygon  ellipsePolygon	=new Polygon(ellipsePolygonTemplate);
	private static final Polygon  tankPolygon		=new Polygon(tankPolygonTemplate);

	
	
	private static Polygon 			scaledCardPolygon;
	private static Polygon 			scaledWavePolygon;
	private static Polygon 			scaledDiamondPolygon;
	private static Polygon 			scaledRectanglePolygon;
	private static Polygon 			scaledEllipsePolygon;
	private static Polygon 			scaledTankPolygon;

	
	

	private static boolean			pensNBrushesInitialized=false;
	
	
	private static Paint			paintCardOutline;
	private static Paint			paintCardBackFill;
	private static Paint			paintCardFrontFillNormal;
	private static Paint			paintCardFrontFillTagged;
	private static Paint			paintCardFrontFillHighlighted;
	
	private static Paint			paintSymbolOutlineRed;
	private static Paint			paintSymbolHashFillRed;
	private static Paint			paintSymbolOutlineBlue;
	private static Paint			paintSymbolHashFillBlue;
	private static Paint			paintSymbolOutlinePurple;
	private static Paint			paintSymbolHashFillPurple;
	
	private Paint					paintCardFrontFill;
	private Paint					paintSymbolOutline;
	private Paint					paintSymbolFill;
	
	private boolean					visible;
	private boolean					cardIsHighlighted;
	
	private Rect					cardDimension;
	private Rect					positionDimension;

	static int						cardHeight;
	static int						cardWidth;
	
	/*
	 *   ________________________
	 *   |   symbolVertPadding   |
	 *   |  ___________________  |
	 *   | |    symbolHeight   | |
	 *   | |___________________| |
	 *   |  symbolVertSpacing    |
	 *   |  ___________________  |
	 *   | |                   | |
	 *   | |___________________| |
	 *   |                       |
	 *   |  ___________________  |
	 *   | |                   | |
	 *   | |___________________| |
	 *   |                       |
	 *   
	 *     |<-- symbolWidth -->| 
	 */
	
	
	static int						symbolWidth;
	static int						symbolHeight;
	static int						symbolVerSpacing;
	static int						symbolVerPadding;
	static int						symbolHorPadding;


    static SymbolSet theSymbolSet=SymbolSet.SYMBOLSET_CARDGAME;
    static int						theRedColor;
    static int						theBlueColor;
    static int						thePurpleColor;
    
	
    /**
     * The playfield canvas. The canvas is needed here for redrawing
     */
    private Canvas					canvas;
	
    private static Context			theContext;
    
	/* *********************************************************************************************\
	 * Constructor
	\* *********************************************************************************************/	
    
    
    /**
     * Constructor. Initializes the variables and resets the playfield position
     */
    public PlayFieldPosition()
    {
    	super();
    	
   	
    	reset();
    	
    }
    
    
	/* *********************************************************************************************\
	 *  Private methods
	\* *********************************************************************************************/
	
	/* *********************************************************************************************\
	 * Public methods
	\* *********************************************************************************************/
    
    /**
     * This method sets the reference to the view to which the PlayFieldPosition instances are
     * drawn. The reference is needed to get access to the resources.
     * @param theView
     */
    public static void setContext(Context context)
    {
    	theContext=context;
    	
    }

    
	/**
	 * This method sets the color values to use
	 * @param redColor The color for 'red' cards
	 * @param blueColor The color for 'blue' cards
	 * @param purpleColor The color for 'purple' cards
	 */
	public static void setColors(int redColor, int blueColor, int purpleColor)
	{
		theRedColor				=redColor;
		theBlueColor			=blueColor;
		thePurpleColor			=purpleColor;
		pensNBrushesInitialized	=false;
	}
	
	
	/**
	 * This method sets the symbolset to use
	 * @param symbolSet The symbol set
	 */
	public static void setSymbolSet(SymbolSet symbolSet)
	{
		theSymbolSet			=symbolSet;
	}    


    /**
     *  This function resets the position. I.e. it empties it
     */
    public void reset()
    {
    	super.reset();
    	cardIsHighlighted 		=false;
    	visible					=false;
    }


    /**
     *  This method defines the dimension of the card 
     *  @param dimension Rectangle describing the position
     */
    public void setCardDimension(Rect dimension)
    {
    	this.cardDimension=dimension;
    }
    
    
    /**
     *  This method returns the location of the card dimension as Rect instance
     *  @return Rectangle representing the position
     */
    public Rect getCardDimension()
    {
    	return cardDimension;
    }


    /**
     *  This method defines the dimension of the position (i.e. card+padding) 
     *  @param dimension Rectangle describing the position
     */
    public void setPositionDimension(Rect dimension)
    {
    	this.positionDimension=dimension;
    }
    
    
    /**
     *  This method returns the location of the position dimension as Rect instance
     *  @return Rectangle representing the position
     */
    public Rect getPositionDimension()
    {
    	return positionDimension;
    }


    /**
     *  This method removes the card from the playfield. It triggers erasure
     */
   
    @Override
    public void removeCard()
    {
    	super.removeCard();
    	eraseCard();
    }


    /**
     *  This method sets or unsets the highlighting state of the card
     *  @param tagged The new state of highlighting
     */
    public void highlightCard(boolean highlighted)
    {
    	this.cardIsHighlighted=highlighted;
    }


    /**
     *  This method sets the dimensions of the card
     *  @param width  Width of the card
     *  @param height Height of the card
     */
    public static void setCardDimension(int width, int height)
    {
    	cardHeight		 =height;
    	cardWidth		 =width;
/*    	
    	symbolHeight	=(cardHeight-2*Config.SYMBOL_VBORDER-2*Config.SYMBOL_SPACING)/3;
    	symbolWidth 	=cardWidth-2*Config.SYMBOL_HBORDER;
*/
    	symbolHeight	 =(int)(cardHeight*Config.CARD_SYMBOL_RELATIVE_HEIGHT);
    	symbolVerPadding =(int)(cardWidth*Config.CARD_SYMBOL_RELATIVE_VER_PADDING);
    	symbolVerSpacing =(int)(cardWidth*Config.CARD_SYMBOL_RELATIVE_SPACING);

    	symbolWidth 	 =(int)(cardWidth*Config.CARD_SYMBOL_RELATIVE_WIDTH);
    	symbolHorPadding =(int)(cardWidth*Config.CARD_SYMBOL_RELATIVE_HOR_PADDING);
    	
    	
    	scaledCardPolygon		=Polygon.scalePolygon(cardPolygon, cardWidth, cardHeight);
    	scaledWavePolygon		=Polygon.scalePolygon(wavePolygon, symbolWidth, symbolHeight);
    	scaledDiamondPolygon	=Polygon.scalePolygon(diamondPolygon, symbolWidth, symbolHeight);
    	scaledRectanglePolygon	=Polygon.scalePolygon(rectanglePolygon, symbolWidth, symbolHeight);
    	scaledTankPolygon		=Polygon.scalePolygon(tankPolygon, symbolWidth, symbolHeight);
    	scaledEllipsePolygon	=Polygon.scalePolygon(ellipsePolygon, symbolWidth, symbolHeight);
			
    }
    /**
     * This method intialises all pens and brushes. Since there are 
     * quite a couple of them, they should only be initialised once.
     *
     */
    private static void initPensNBrushes()
    {
    	Bitmap  				hashBitmap;
    	BitmapShader			hashFillShader;
    	ColorMatrix 			colorMatrix;
    	ColorMatrixColorFilter	colorMatrixFilter;
    	float					red;
    	float					green;
    	float					blue;

    	if (!pensNBrushesInitialized)
    	{
    		paintCardOutline=new Paint(Paint.ANTI_ALIAS_FLAG);
    		paintCardOutline.setColor(Config.CARD_OUTLINE_COLOR);
    		paintCardOutline.setStyle(Style.STROKE);
    		paintCardOutline.setStrokeWidth(Config.CARD_OUTLINE_STROKE_WIDTH);
    		
    		
    		paintCardBackFill=new Paint(Paint.ANTI_ALIAS_FLAG);
    		paintCardBackFill.setColor(Config.CARD_FILL_BACK_COLOR);
    		paintCardBackFill.setStyle(Style.FILL);


    		paintCardFrontFillNormal=new Paint(Paint.ANTI_ALIAS_FLAG);
    		paintCardFrontFillNormal.setColor(Config.CARD_FILL_FRONT_COLOR);
    		paintCardFrontFillNormal.setStyle(Style.FILL);
    		
    		paintCardFrontFillHighlighted=new Paint(Paint.ANTI_ALIAS_FLAG);
    		paintCardFrontFillHighlighted.setColor(Config.CARD_FILL_FRONT_COLOR_HIGHLIGHTED);
    		paintCardFrontFillHighlighted.setStyle(Style.FILL);
    		
    		paintCardFrontFillTagged=new Paint(Paint.ANTI_ALIAS_FLAG);
    		paintCardFrontFillTagged.setColor(Config.CARD_FILL_FRONT_COLOR_TAGGED);
    		paintCardFrontFillTagged.setStyle(Style.FILL);
    		
    		paintSymbolOutlineRed=new Paint(Paint.ANTI_ALIAS_FLAG);
    		paintSymbolOutlineRed.setColor(theRedColor);
    		paintSymbolOutlineRed.setStyle(Style.STROKE);
    		paintSymbolOutlineRed.setStrokeWidth(Config.CARD_SYMBOL_STROKE_WIDTH);    		
    		
    		paintSymbolOutlineBlue=new Paint(Paint.ANTI_ALIAS_FLAG);
    		paintSymbolOutlineBlue.setColor(theBlueColor);
    		paintSymbolOutlineBlue.setStyle(Style.STROKE);
    		paintSymbolOutlineBlue.setStrokeWidth(Config.CARD_SYMBOL_STROKE_WIDTH);    		
    		
    		paintSymbolOutlinePurple=new Paint(Paint.ANTI_ALIAS_FLAG);
    		paintSymbolOutlinePurple.setColor(thePurpleColor);
    		paintSymbolOutlinePurple.setStyle(Style.STROKE);
    		paintSymbolOutlinePurple.setStrokeWidth(Config.CARD_SYMBOL_STROKE_WIDTH);    		
    		
    		// TODO: get colors from settings
    		paintSymbolHashFillRed=new Paint(Paint.ANTI_ALIAS_FLAG);
    		paintSymbolHashFillRed.setStyle(Style.FILL);
    		paintSymbolHashFillRed.setColor(theRedColor);    		
    		
    		paintSymbolHashFillBlue=new Paint(Paint.ANTI_ALIAS_FLAG);
    		paintSymbolHashFillBlue.setStyle(Style.FILL);
    		paintSymbolHashFillBlue.setColor(theBlueColor);    		
    		
    		paintSymbolHashFillPurple=new Paint(Paint.ANTI_ALIAS_FLAG);
    		paintSymbolHashFillPurple.setStyle(Style.FILL);
    		paintSymbolHashFillPurple.setColor(thePurpleColor);    		
    		
   		    //Initialize the bitmap object by loading an image from the resources folder  
    		// TODO Choose a inSampleSize based on symbolsize. Must be 1, 2, 4, 8, 16. Choose anti aliased hash?
    		BitmapFactory.Options options = new BitmapFactory.Options();
    		options.inSampleSize=4;
    		
    	    hashBitmap = BitmapFactory.decodeResource(theContext.getResources(), net.studioblueplanet.superset.R.drawable.hash2, options);
    	    //Initialize the BitmapShader with the Bitmap object and set the texture tile mode  
    	    hashFillShader = new BitmapShader(hashBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);  
    	    paintSymbolHashFillRed.setShader(hashFillShader);
    	    paintSymbolHashFillBlue.setShader(hashFillShader);
    	    paintSymbolHashFillPurple.setShader(hashFillShader);
    	    
    	    colorMatrix=new ColorMatrix();
    	    colorMatrix.reset();
    	    
    	    red  =(float)((theRedColor>>16) & 0xFF)/255.0f;
    	    green=(float)((theRedColor>> 8) & 0xFF)/255.0f;
    	    blue =(float)((theRedColor>> 0) & 0xFF)/255.0f;
    	    colorMatrix.setScale(red, green, blue, 1.0f);
    	    colorMatrixFilter=new ColorMatrixColorFilter(colorMatrix);
    	    paintSymbolHashFillRed.setColorFilter(colorMatrixFilter);

    	    red  =(float)((theBlueColor>>16) & 0xFF)/255.0f;
    	    green=(float)((theBlueColor>> 8) & 0xFF)/255.0f;
    	    blue =(float)((theBlueColor>> 0) & 0xFF)/255.0f;
    	    colorMatrix.setScale(red, green, blue, 1.0f);
    	    colorMatrixFilter=new ColorMatrixColorFilter(colorMatrix);
    	    paintSymbolHashFillBlue.setColorFilter(colorMatrixFilter);

    	    red  =(float)((thePurpleColor>>16) & 0xFF)/255.0f;
    	    green=(float)((thePurpleColor>> 8) & 0xFF)/255.0f;
    	    blue =(float)((thePurpleColor>> 0) & 0xFF)/255.0f;
    	    colorMatrix.setScale(red, green, blue, 1.0f);
    	    colorMatrixFilter=new ColorMatrixColorFilter(colorMatrix);
    	    paintSymbolHashFillPurple.setColorFilter(colorMatrixFilter);
    	    

    		pensNBrushesInitialized=true;
    	}
    }


    /**
     *  Cleans up the pens and brushes
     * 
     */
   private void destroyPensNBrushes()
    {
    	if (pensNBrushesInitialized)
    	{
    		paintCardOutline=null;
    		paintCardBackFill=null;
    		paintCardFrontFillNormal=null;
    		paintCardFrontFillTagged=null;
    		paintCardFrontFillHighlighted=null;

    		paintSymbolOutlineRed=null;
    		paintSymbolOutlineBlue=null;
    		paintSymbolOutlinePurple=null;
    		
    		paintSymbolHashFillRed=null;
    		paintSymbolHashFillBlue=null;
    		paintSymbolHashFillPurple=null;

    		pensNBrushesInitialized=false;
    	}
    }

    /**
     *  This method chooses the right pen, depending on the color, fill and
     *  whether the card is tagged or highlighted
     *  @param color Color of the card
     *  @param fill Filling of the card
     */
    private void setColor(Card.CardColor color, Card.CardFilling fill)
    {
    	boolean isTagged;
    	boolean isVisible;

    	switch (color)
    	{
    	case CARDCOLOR_BLUE:
    		this.paintSymbolOutline=paintSymbolOutlineBlue; 
    		switch (fill)
    		{
    		case CARDFILLING_SOLID:
    			this.paintSymbolOutline.setStyle(Style.FILL_AND_STROKE);
    			this.paintSymbolFill=null;
    			break;
    		case CARDFILLING_OPEN:
    			this.paintSymbolOutline.setStyle(Style.STROKE);
    			this.paintSymbolFill=null;
    			break;
    		case CARDFILLING_HALF:
    			this.paintSymbolOutline.setStyle(Style.STROKE);
    			this.paintSymbolFill=paintSymbolHashFillBlue;
    			break;
    		}

    		break;
    	case CARDCOLOR_RED:
    		this.paintSymbolOutline=paintSymbolOutlineRed; 
    		switch (fill)
    		{
    		case CARDFILLING_SOLID:
    			this.paintSymbolOutline.setStyle(Style.FILL_AND_STROKE);
    			this.paintSymbolFill=null;
    			break;
    		case CARDFILLING_OPEN:
    			this.paintSymbolOutline.setStyle(Style.STROKE);
    			this.paintSymbolFill=null;
    			break;
    		case CARDFILLING_HALF:
    			this.paintSymbolOutline.setStyle(Style.STROKE);
    			this.paintSymbolFill=paintSymbolHashFillRed;
    			break;
    		}

    		break;
    	case CARDCOLOR_PURPLE:
    		this.paintSymbolOutline=paintSymbolOutlinePurple; 
    		switch (fill)
    		{
    		case CARDFILLING_SOLID:
    			this.paintSymbolOutline.setStyle(Style.FILL_AND_STROKE);
    			this.paintSymbolFill=null;
    			break;
    		case CARDFILLING_OPEN:
    			this.paintSymbolOutline.setStyle(Style.STROKE);
    			this.paintSymbolFill=null;
    			break;
    		case CARDFILLING_HALF:
    			this.paintSymbolOutline.setStyle(Style.STROKE);
    			this.paintSymbolFill=paintSymbolHashFillPurple;
    			break;
    		}

    		break;

    	}

    	synchronized(guard)
    	{
    		isTagged=this.cardIsTagged;
    		isVisible=this.visible;
    	}
    	
    	if (isVisible)
    	{
			if (isTagged)
			{
				this.paintCardFrontFill=paintCardFrontFillTagged;
			}
			else if (this.cardIsHighlighted)
			{
				this.paintCardFrontFill=paintCardFrontFillHighlighted;
			}
			else
			{
				this.paintCardFrontFill=paintCardFrontFillNormal;
	
			}    	
    	}
    	else
    	{
    		this.paintCardFrontFill=paintCardBackFill;
    	}
    	// TODO
    }

    /**
     *  Draws the card symbol
     *  @param x X coordinate of the position to draw the symbol
     *  @param y Y coordinate of the position to draw the symbol
     *  @param symbol Symbol to draw
     */
    private void drawSymbol(int x, int y, Card.CardSymbol symbol)
    {
    	boolean isVisible;
    	
    	synchronized(guard)
    	{
    		isVisible=this.visible;
    	}
    	if (isVisible)
    	{
    		switch (symbol)
    		{
    		case CARDSYMBOL_ELLIPSE:
    			drawEllipse(x, y);
    			break;
    		case CARDSYMBOL_RECTANGLE:
    			drawRectangle(x, y);
    			break;
    		case CARDSYMBOL_WAVE:
    			drawWave(x, y);
    			break;
    		}
    	}
    }

    /**
     *  Draws the ellipse symbol. Based on the selected symbol set a tank or ellipse is drawn.
     *  @param x X coordinate of the position to draw the symbol
     *  @param y Y coordinate of the position to draw the symbol
     */
    private void drawEllipse 	(int x, int y)
    {
    	switch (theSymbolSet)
    	{
    	case SYMBOLSET_CLASSIC:
    		scaledTankPolygon.draw(canvas, x, y, paintSymbolOutline, paintSymbolFill);
    		break;
    	case SYMBOLSET_CARDGAME:
    		scaledEllipsePolygon.draw(canvas, x, y, paintSymbolOutline, paintSymbolFill);
    		break;
    	}
    }

    /**
     *  Draws the rectangle symbol. Based on the selected symbol set a rectangle or diamond is drawn.
     *  @param x X coordinate of the position to draw the symbol
     *  @param y Y coordinate of the position to draw the symbol
     */
    private void drawRectangle	(int x, int y)
    {
    	switch (theSymbolSet)
    	{
    	case SYMBOLSET_CLASSIC:
    		scaledDiamondPolygon.draw(canvas, x, y, paintSymbolOutline, paintSymbolFill);
    		break;
    	case SYMBOLSET_CARDGAME:
    		scaledRectanglePolygon.draw(canvas, x, y, paintSymbolOutline, paintSymbolFill);    		
    		break;
    	}
    }

    /**
     *  Draws the wave symbol. Symbol is identical between symbol sets
     *  @param x X coordinate of the position to draw the symbol
     *  @param y Y coordinate of the position to draw the symbol
     */
    private void drawWave(int x, int y)
    {
    	switch (theSymbolSet)
    	{
    	case SYMBOLSET_CLASSIC:
    		scaledWavePolygon.draw(canvas, x, y, paintSymbolOutline, paintSymbolFill);
    		break;
    	case SYMBOLSET_CARDGAME:
    		scaledWavePolygon.draw(canvas, x, y, paintSymbolOutline, paintSymbolFill);
    		break;
    	}

    }


    /**
     *  This routine draws the card 
     */
    public void drawCard(Canvas canvas)
    {
    	Card theCard;
    	
    	this.canvas=canvas;
    	
    	synchronized(guard)
    	{
    		theCard=this.card;
    	}
    	
    	initPensNBrushes();
    	if (theCard!=null)
    	{

    		setColor(theCard.color, theCard.filling);


    		scaledCardPolygon.draw(canvas, cardDimension.left, cardDimension.top, paintCardOutline, paintCardFrontFill);

      
    		if (theCard.number==Card.CardNumber.CARDNUMBER_ONE)
    		{
    			drawSymbol( cardDimension.left+(cardDimension.right -cardDimension.left)/2,
    						cardDimension.top +(cardDimension.bottom-cardDimension.top )/2,
    						theCard.symbol);
    		}
  		
    		else if (theCard.number==Card.CardNumber.CARDNUMBER_TWO)
    		{
    			drawSymbol( cardDimension.left+(cardDimension.right -cardDimension.left)/2,
    						cardDimension.top+(cardDimension.bottom-cardDimension.top )/2+symbolHeight/2+symbolVerSpacing/2,
    						theCard.symbol);
    			drawSymbol( cardDimension.left+(cardDimension.right -cardDimension.left)/2,
    						cardDimension.top+(cardDimension.bottom-cardDimension.top )/2-symbolHeight/2-symbolVerSpacing/2,
    						theCard.symbol);
    		}

    		else if (theCard.number==Card.CardNumber.CARDNUMBER_THREE)
    		{
    			drawSymbol( cardDimension.left+(cardDimension.right -cardDimension.left)/2,
    						cardDimension.top+(cardDimension.bottom-cardDimension.top )/2,
    						theCard.symbol);
    			drawSymbol( cardDimension.left+(cardDimension.right -cardDimension.left)/2,
    						cardDimension.top+(cardDimension.bottom-cardDimension.top )/2+symbolHeight+symbolVerSpacing,
    						theCard.symbol);
    			drawSymbol( cardDimension.left+(cardDimension.right -cardDimension.left)/2,
    						cardDimension.top+(cardDimension.bottom-cardDimension.top )/2-symbolHeight-symbolVerSpacing,
    						theCard.symbol);
    		}
    		
    	}
    }

    /**
     *  This method erases the card
     */
    public void eraseCard()
    {
/*    	
    	SelectObject(hdc, backgroundPen);
    	SelectObject(hdc, backgroundBrush);
*/
//    	drawPolygon(scaledCardPolygon, CARDPOLYGONPOINTS, pos.left, pos.top);
    }


    /**
     *  Sets the visibility of this position
     *  @param state New state for the visibility
     */
    public void setVisible(boolean state)
    {
    	synchronized(guard)
    	{
    		visible=state;
    	}
    }


	
}
