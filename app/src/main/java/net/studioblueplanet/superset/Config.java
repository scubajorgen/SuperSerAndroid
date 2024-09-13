package net.studioblueplanet.superset;

public class Config
{
	// Set game
	

	// GENERIC SET GAME SETTINGS
	public static final int MAX_ROWS=3;
	public static final int MAX_COLS=6;

	public static final int SET_SIZE=3;

	public static final int CARDS_IN_PLAY=12;
	public static final int MAX_CARDS_IN_PLAY=(MAX_ROWS*MAX_COLS);
	public static final int MAX_SETS=27;


	// next define indicates whether new cards should be added in the middle
	// columns (true) or rows (false)
	public static final boolean COL_BASED=true;	

	public static final int CARD_SPACING				=5;	// free space between the cards
	public static final int SYMBOL_SPACING				=5;   // free space between symbols
	public static final int SYMBOL_HBORDER				=10;  // free space between symbol and card border - horizontal
	public static final int SYMBOL_VBORDER				=18;	// free space between symbol and card border - vertical
	public static final int STATUSBAR_HEIGHT			=40;	// status bar height

	public static final int BARBUTTON_SIZE				=30;		// button size (i.e. hight and width) 24
	public static final int BARBUTTON_PICTURESIZE		=21;		// the button picture size 16
	public static final int BARBUTTON_HSPACING			=8;		// distance between the buttons 10

	/** The aspect ratio of the Cardfield, i.e. that part of the playingfield that holds the cards */
	public static final double CARDFIELD_ASPECT			=(16.0/9.0);

	/** Part of the Canvas height occupied by the cardfield */
	public static final double CARDFIELD_RELATIVE_HEIGHT=0.85;

	/** Part of the Canvas height occupied by the status bar */
	public static final double STATUSBAR_RELATIVE_HEIGHT=1.0-CARDFIELD_RELATIVE_HEIGHT;
	
	/** Part of the Card Position width that is occupied by the card */
	public static final double CARD_RELATIVE_WIDTH       =0.95;
	
	/** Part of the Card Position width that is occupied padding left or right */
	public static final double CARD_RELATIVE_HOR_PADDING =(1.0-CARD_RELATIVE_WIDTH)/2;

	/** Part of the Card Position height that is occupied by the card */
	public static final double CARD_RELATIVE_HEIGHT      =0.95;

	/** Part of the Card Position width that is occupied padding top or bottom */
	public static final double CARD_RELATIVE_VER_PADDING =(1.0-CARD_RELATIVE_HEIGHT)/2.0;
			

	/** Part of the Card height that is occupied by one symbol. Max 0.3333. Rest is padding and spacing */
	public static final double CARD_SYMBOL_RELATIVE_HEIGHT =0.25;

	/** Part of the Card height that is in between symbol space */
	public static final double CARD_SYMBOL_RELATIVE_SPACING =0.05;
			
	/** Part of the Card height that is occupied by one symbol. Max 0.3333. Rest is padding */
	public static final double CARD_SYMBOL_RELATIVE_VER_PADDING =(1.0-3*CARD_SYMBOL_RELATIVE_HEIGHT+2*CARD_SYMBOL_RELATIVE_SPACING)/2.0;
	
	/** Part of the Card height that is occupied by one symbol. Max 0.3333. Rest is padding */
	public static final double CARD_SYMBOL_RELATIVE_WIDTH =0.80;
			
	/** Part of the Card height that is occupied by one symbol. Max 0.3333. Rest is padding */
	public static final double CARD_SYMBOL_RELATIVE_HOR_PADDING =(1.0-CARD_SYMBOL_RELATIVE_WIDTH)/2.0;
	

	/** Relative button size as fraction of the bar height/width (whichever is smallest) */
	public static final double BAR_BUTTON_RELATIVE_SIZE = 0.9;

	/** Left indent before first button */
	public static final double BAR_BUTTON_RELATIVE_PADDING = 0.1;
	
	/** Spacing between buttons */
	public static final double BAR_BUTTON_RELATIVE_SPACING = 0.02;
	
	/** Bar background color */
	public static final int BAR_BACKGROUND_COLOR=0xFF646464;
	
	/** Bar button background color when not clicked */
	public static final int BAR_BUTTON_NOTCLICKED_BACKGROUND_COLOR=0xFF0000FF;
	
	/** Bar button background color when clicked*/
	public static final int BAR_BUTTON_CLICKED_BACKGROUND_COLOR=0xFFFFFFFF;
	
	/** Relative button symbol size as fraction of the button size */
	public static final double BAR_BUTTON_SYMBOL_RELATIVE_SIZE = 0.6;

	/** Bar button symbol color clicked*/
	public static final int BAR_BUTTON_SYMBOL_COLOR=0xFF00FFFF;

	/** Bar text color */
	public static final int BAR_TEXT_COLOR=0xFFFFFFFF;

	
	
	/** The card outline color */
	public static final int CARD_OUTLINE_COLOR=0xFF000000;
		
	/** The stroke width */
	public static final float CARD_OUTLINE_STROKE_WIDTH=3.0f;
	
	/** The card symbolside fill color */
	public static final int CARD_FILL_FRONT_COLOR=0xFFFFFFFF;
	
	/** The card symbolside fill color */
	public static final int CARD_FILL_FRONT_COLOR_TAGGED=0xFFA0C8DC;
	
	/** The card symbolside fill color */
	public static final int CARD_FILL_FRONT_COLOR_HIGHLIGHTED=0xFFFFE650;
	
	/** The card backside fill color */
	public static final int CARD_FILL_BACK_COLOR=0xFFA0A0FF;
	
	/** The stroke width */
	public static final float CARD_SYMBOL_STROKE_WIDTH=5.0f;
	
	
	
	
	
	/********************************************************************\
	* Generic defines
	\********************************************************************/

	// This is the version string. It should have the format 'Version xx.y'
	// The digits x and y may be [0-9]
	// It is used to print the tag and to sign the user.dat file.
	public static final String VERSION_STRING	="Version 03.0";

	// communication buffers
	public static final int MAXBUFFERSIZE			= 3300;	

	// two string lengths
	public static final int GENERIC_MAX_STRING		= 20;
	public static final int GENERIC_MAX_LONGSTRING	= 50;
	
	
	// x - character, 9 - digit, b - byte, binary 

	public static final int  MAX_USERS				= 100;
	public static final int  MAX_GAMES				= ((MAX_USERS)/2);
	public static final int  HIGHSCORELISTITEMLENGTH= 32;									/* 999. xxxxxxxxxx 99-99-9999 9999 */
	public static final int  PLAYERLISTITEMLENGTH   =(GENERIC_MAX_STRING+Integer.SIZE);	/* b..bxxxxxxxxxxxxxxxxxxxxx  =GENERIC_MAX_STRING+4 */

	public static final int GAMESERVERPORT			= 1501;

	public static final int MSGCARDRECORDSIZE		= 7;		
	
}
