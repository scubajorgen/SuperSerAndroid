package net.studioblueplanet.superset;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

import net.studioblueplanet.superset.view.SinglePlayerGameView;
import net.studioblueplanet.superset.gameplay.ScoreListener;
import net.studioblueplanet.superset.util.SymbolSet;


public class SinglePlayerGameActivity extends Activity  implements ScoreListener
{
	public static final String 		SCORE_MESSAGE="score";
	
	private SinglePlayerGameView 	theView;
	
	private String					symbolSetString;
	private SymbolSet 				symbolSet;
	private String[]				symbolSetValues;
	
	private int						redColor;
	private int						blueColor;
	private int						purpleColor;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		SharedPreferences 	prefs;
		
		super.onCreate(savedInstanceState);
		
		CurrentActivityRegister.getInstance().registerCurrentActivity(this);
		
		// Create a new view.
		theView=new SinglePlayerGameView(this.getBaseContext(), this);
		theView.setPadding(0, 0, 0, 0);
		setContentView(theView);
		
		
		
		prefs=PreferenceManager.getDefaultSharedPreferences(this);
		
		symbolSetValues=getResources().getStringArray(R.array.pref_layout_symbolset_entries);
		
		symbolSetString=prefs.getString("pref_layout_symbolset", symbolSetValues[0]);
		if (symbolSetString.equals(symbolSetValues[0]))
		{
			symbolSet=SymbolSet.SYMBOLSET_CARDGAME;
		}
		if (symbolSetString.equals(symbolSetValues[1]))
		{
			symbolSet=SymbolSet.SYMBOLSET_CLASSIC;
		}
		else
		{
			symbolSet=SymbolSet.SYMBOLSET_CARDGAME;
		}
		theView.setSymbolSet(symbolSet);
		
		// Retrieve the Hex RGB values
		redColor=Integer.parseInt(prefs.getString("pref_layout_symbolcolorred", "FF0000"), 16);
		blueColor=Integer.parseInt(prefs.getString("pref_layout_symbolcolorblue", "009600"), 16);
		purpleColor=Integer.parseInt(prefs.getString("pref_layout_symbolcolorpurple", "FF00FF"), 16);
		
		// Add the alpha channel
		redColor |= 0xFF000000;
		blueColor |= 0xFF000000;
		purpleColor |= 0xFF000000;
		theView.setColors(redColor, blueColor, purpleColor);
	}
	
	
	@Override
	protected void onStart()
	{
		super.onStart();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		theView.resume();

	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
		
		// Signal the game to go to paused state. Otherwise the score keeps running
		theView.pause();

	}
	
	
	

	
/*	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.single_player_game, menu);
		return true;
	}
*/	
	public void onScore(int score)
	{
	    Intent intent; 
	    intent = new Intent(this, SinglePlayerGameFinishedActivity.class);
	    intent.putExtra(SCORE_MESSAGE, score);
	    startActivity(intent);			
	}
	


}
