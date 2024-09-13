package net.studioblueplanet.superset.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Path;
import android.view.MotionEvent;

import net.studioblueplanet.superset.Config;

/**
 * This class represents the action bar
 * @author jorgen
 *
 */
public class PlayFieldBar
{
	private static final int MAX_BUTTONS=3;
	int						left;
	int						top;
	int						right;
	int						bottom;
	int						width;
	int						height;

	boolean					isPaused;


	int 					buttonSize;
	BarButton[]  			buttons;
	int 					buttonSymbolSize;
	int 					textSize;
	Rect					textDimension;
	Rect					textAreaDimension;
	
	Paint					paintBarBackground;
	Paint					paintButtonBackground;
	Paint					paintButtonSymbol;
	Paint					paintText;
	
	String					scoreString;
/*	
	HPEN		backgroundPen;
	HBRUSH		backgroundBrush;
	HPEN		buttonPen;
	HBRUSH		buttonBrush;
	HBRUSH		buttonClickedBrush;
	HPEN		symbolPen;
	HBRUSH		symbolBrush;
	HPEN		buttonCrossPen;
*/

	
	private static final int[][]	playSybolPolygonTemplate=
	{
		{ -1000,  -1000},
		{  1000,	  0},
		{ -1000,   1000}
	};
	
	private static final Polygon 	playSymbolPolygon=new Polygon(playSybolPolygonTemplate);
	private static Polygon 			scaledPlaySymbolPolygon;
	
	private static Path				dash;

	private PlayField				thePlayField;
	
	/* *********************************************************************************************\
	 * Constructor
	\* *********************************************************************************************/


	public PlayFieldBar(PlayField playField)
	{
		int i;
		
		this.thePlayField=playField;
		
		paintBarBackground=new Paint();
		paintBarBackground.setStyle(Style.FILL);
		paintBarBackground.setColor(Config.BAR_BACKGROUND_COLOR);
		
		paintButtonBackground=new Paint();
		paintButtonBackground.setStyle(Style.FILL);
		
		paintButtonSymbol=new Paint();
		paintButtonSymbol.setStyle(Style.FILL);
		paintButtonSymbol.setColor(Config.BAR_BUTTON_SYMBOL_COLOR);
		paintButtonSymbol.setStrokeWidth(3);
	
		
		paintText=new Paint();
		paintText.setStyle(Style.FILL);
		paintText.setColor(Config.BAR_TEXT_COLOR);
		paintText.setTextAlign(Align.LEFT);
		paintText.setTypeface(Typeface.DEFAULT);
		paintText.setTextSize(10);
		
		
/*		
		backgroundPen		=CreatePen(PS_SOLID, 1, BARBACKGROUNDCOLOR		);
		buttonPen			=CreatePen(PS_SOLID, 1, BARBUTTONOUTLINECOLOR	);
		symbolPen			=CreatePen(PS_SOLID, 1, BARSYMBOLCOLOR			);
		buttonCrossPen		=CreatePen(PS_SOLID, 2, RGB(255, 0, 0));

		backgroundBrush		=CreateSolidBrush(BARBACKGROUNDCOLOR			);
		buttonBrush			=CreateSolidBrush(BARBUTTONFILLCOLOR			);
		buttonClickedBrush	=CreateSolidBrush(BARBUTTONCLICKEDCOLOR			);
		symbolBrush			=CreateSolidBrush(BARSYMBOLCOLOR				);
*/
		isPaused=true;
		
		buttons=new BarButton[MAX_BUTTONS];
		i=0;
		while (i<MAX_BUTTONS)
		{
			buttons[i]=new BarButton();
			i++;
		}
		
		textDimension=new Rect();
		textAreaDimension=new Rect();
		
		scoreString=null;

		dash=new Path();

	}



	/**
	 * Sets the position and dimensions of the bar
	 */
	public void setDimensions(int left, int top, int right, int bottom)
	{
		int minBarDimension;

		int buttonSpacing;
		int buttonPadding;
		
		this.left=left;
		this.top=top;
		this.right=right;
		this.bottom=bottom;
		width=right-left;
		height=bottom-top;

		int i;

		
		minBarDimension	=Math.min(width, height);
		buttonSize		=(int)(Config.BAR_BUTTON_RELATIVE_SIZE*minBarDimension);
		buttonSpacing	=(int)(Config.BAR_BUTTON_RELATIVE_SPACING*width);
		buttonPadding	=(int)(Config.BAR_BUTTON_RELATIVE_PADDING*width);
		buttonSymbolSize=(int)(buttonSize*Config.BAR_BUTTON_SYMBOL_RELATIVE_SIZE);
		
		i=0;
		while (i<MAX_BUTTONS)
		{
			buttons[i].left     =left + buttonPadding + i*(buttonSize+buttonSpacing);
			buttons[i].right	=buttons[i].left+buttonSize;
			buttons[i].top		=top+(height-buttonSize)/2; // center vertically in bar
			buttons[i].bottom	=buttons[i].top+buttonSize;
			
			buttons[i].buttonDimension			=new Rect();
			buttons[i].buttonDimension.left		=buttons[i].left;
			buttons[i].buttonDimension.right	=buttons[i].right;
			buttons[i].buttonDimension.top		=buttons[i].top;
			buttons[i].buttonDimension.bottom	=buttons[i].bottom;
			
			
			buttons[i].clicked	=false;
			i++;
		}
		buttons[0].type=BarButtonType.BUTTONTYPE_PLAYPAUSE;
		buttons[1].type=BarButtonType.BUTTONTYPE_STOP;
		buttons[2].type=BarButtonType.BUTTONTYPE_NOSET;
		
		textSize=buttonSize/2;
		textDimension.left		=left + buttonPadding + i*(buttonSize+buttonSpacing);
		textDimension.right		=right;
		textDimension.top		=top+(height-textSize)/2; // center vertically in bar
		textDimension.bottom	=textDimension.top+textSize;
		
		// Area to erase when erasing text. Allows for multiple textsizes
		textAreaDimension.left		=textDimension.left;
		textAreaDimension.right		=textDimension.right;
		textAreaDimension.top		=top; 
		textAreaDimension.bottom	=bottom;
		
		
		paintText.setTextSize(textSize);
		
		scaledPlaySymbolPolygon=Polygon.scalePolygon(playSymbolPolygon, buttonSymbolSize, buttonSymbolSize);
		
	}



	/**
	 * This method draws the bar
	 */
	public void paintBar(Canvas canvas)
	{
		int i;

		
    	canvas.drawRect(left, top, right, bottom, paintBarBackground);

		i=0;
		while (i<MAX_BUTTONS)
		{
			drawButton(canvas, i);
			i++;
		}
		
		
		
	}

	private void drawButton(Canvas canvas, int buttonIndex)
	{
		int i;
		int cX;
		int cY;

		
		if (buttons[buttonIndex].clicked)
		{
			paintButtonBackground.setColor(Config.BAR_BUTTON_CLICKED_BACKGROUND_COLOR);
		}
		else
		{
			paintButtonBackground.setColor(Config.BAR_BUTTON_NOTCLICKED_BACKGROUND_COLOR);
		}
		
		canvas.drawRect(buttons[buttonIndex].buttonDimension, paintButtonBackground);

		cX=buttons[buttonIndex].left+(buttons[buttonIndex].right -buttons[buttonIndex].left)/2;
		cY=buttons[buttonIndex].top +(buttons[buttonIndex].bottom-buttons[buttonIndex].top )/2;

		switch (buttons[buttonIndex].type)
		{
		case BUTTONTYPE_PLAYPAUSE:
			paintButtonSymbol.setStyle(Style.FILL_AND_STROKE);
			paintButtonSymbol.setColor(Config.BAR_BUTTON_SYMBOL_COLOR);
			if (this.isPaused)
			{
				scaledPlaySymbolPolygon.draw(canvas, cX, cY, paintButtonSymbol, null);
			}
			else
			{
				canvas.drawRect(cX-buttonSymbolSize/2, cY-buttonSymbolSize/2, 
								cX-buttonSymbolSize/6, cY+buttonSymbolSize/2,
								paintButtonSymbol);
				canvas.drawRect(cX+buttonSymbolSize/6, cY-buttonSymbolSize/2, 
								cX+buttonSymbolSize/2, cY+buttonSymbolSize/2,
								paintButtonSymbol);
			}
			break;
		case BUTTONTYPE_STOP:
			paintButtonSymbol.setStyle(Style.FILL_AND_STROKE);
			paintButtonSymbol.setColor(Config.BAR_BUTTON_SYMBOL_COLOR);
			canvas.drawRect(cX-buttonSymbolSize/2, cY-buttonSymbolSize/2, 
							cX+buttonSymbolSize/2, cY+buttonSymbolSize/2,
							paintButtonSymbol);
			break;
		case BUTTONTYPE_NOSET:
			int size;
			
			paintButtonSymbol.setStyle(Style.STROKE);
			paintButtonSymbol.setColor(Config.BAR_BUTTON_SYMBOL_COLOR);
			
			canvas.drawRect(cX-buttonSymbolSize/2, cY-buttonSymbolSize/2, 
							cX                        , cY              ,
							paintButtonSymbol);
			
			

			canvas.drawRect(cX-buttonSymbolSize/4, cY-buttonSymbolSize/4, 
							cX+buttonSymbolSize/4, cY+buttonSymbolSize/4,
							paintButtonBackground);
			canvas.drawRect(cX-buttonSymbolSize/4, cY-buttonSymbolSize/4, 
							cX+buttonSymbolSize/4, cY+buttonSymbolSize/4,
							paintButtonSymbol);
			
			
			canvas.drawRect(cX                        , cY              , 
							cX+buttonSymbolSize/2, cY+buttonSymbolSize/2,
							paintButtonBackground);
			canvas.drawRect(cX                        , cY              , 
							cX+buttonSymbolSize/2, cY+buttonSymbolSize/2,
							paintButtonSymbol);
			
			paintButtonSymbol.setColor(0xFFFF0000);
			dash.reset();
			dash.moveTo(cX-buttonSymbolSize/2, cY+buttonSymbolSize/2);
			dash.lineTo(cX+buttonSymbolSize/2, cY-buttonSymbolSize/2);
			canvas.drawPath(dash, paintButtonSymbol);
			
			break;

		}
		thePlayField.invalidate(buttons[buttonIndex].buttonDimension);
	}





	
	/**
	 * Sets the score string
	 * @param scoreString
	 */
	public void drawScore(Canvas canvas, String scoreString)
	{
		this.scoreString=scoreString;
		
		canvas.drawRect(textAreaDimension, paintBarBackground);
		
		if (scoreString!=null)
		{
			canvas.drawText(scoreString, textDimension.left, textDimension.bottom, paintText);
		}
		
		thePlayField.invalidate(textDimension);		
	}

	/**
	 * Handle the touch/motion event
	 * @param mouseX 
	 * @param mouseY
	 * @param event
	 * @return
	 */
	public PlayField.PlayFieldAction handleAction(Canvas canvas, int mouseX, int mouseY, int event)
	{
		int				i;
		PlayField.PlayFieldAction action;

		i=0;
		action= PlayField.PlayFieldAction.ACTION_NONE;
		
		while (i<MAX_BUTTONS)
		{
			if ((buttons[i].left<mouseX) && (mouseX<buttons[i].right) &&
				(buttons[i].top<mouseY) && (mouseY<buttons[i].bottom))
			{
				switch (buttons[i].type)
				{
				case BUTTONTYPE_PLAYPAUSE:
					if (isPaused)
					{
						// If the game is paused or stopped: regard as play being pressed
						action= PlayField.PlayFieldAction.ACTION_PLAYPRESSED;
					}
					else
					{
						// Else Pause pressed
						action= PlayField.PlayFieldAction.ACTION_PAUSEPRESSED;
					}
					break;
				case BUTTONTYPE_STOP:
					action= PlayField.PlayFieldAction.ACTION_STOPPRESSED;
					break;
				case BUTTONTYPE_NOSET:
					action= PlayField.PlayFieldAction.ACTION_NOSETPRESSED;
					break;
				}
				if ((event==MotionEvent.ACTION_DOWN) && (!buttons[i].clicked))
				{
					buttons[i].clicked=true;
					drawButton(canvas, i);
					action= PlayField.PlayFieldAction.ACTION_NONE;
				}
				else
				{
					// MOUSE UP when mouse pointer is on the button: action!
				}
			}
			if ((event==MotionEvent.ACTION_UP) && (buttons[i].clicked))
			{
				buttons[i].clicked=false;
				drawButton(canvas, i);
			}
			i++;
		}
		return action;
	}


	/**
	 * Sets the game to paused or to playing
	 * @param isPaused True sets the game to paused, false to playing
	 */
	public void setGamePaused(Canvas canvas, boolean isPaused)
	{
		this.isPaused=isPaused;
		drawButton(canvas, 0);
	}
	
	/**
	 * Sets the textsize of score string
	 * @param textSize The text size
	 */
	public void setScoreTextSize(float textSize)
	{
		paintText.setTextSize(textSize);
	}
	
	public BarButton getNoSetButton()
	{
		return this.buttons[2];
	}
}
