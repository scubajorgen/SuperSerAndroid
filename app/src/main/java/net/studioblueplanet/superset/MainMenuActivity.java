package net.studioblueplanet.superset;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

public class MainMenuActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// Set the content
		setContentView(R.layout.activity_main_menu);

		// Always landscape
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	/**
	 * Exit button action
	 * @param view View that called the action
	 */
	public void buttonActionExit(View view)
	{
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);		
		finish();
	}

	/**
	 * Single Player Game button action
	 * @param view View that called the action
	 */
	public void buttonActionSinglePlayerGame(View view)
	{
	    Intent intent; 
	    intent = new Intent(this, SinglePlayerGameActivity.class);
	    startActivity(intent);	
	}
	
	/**
	 * Settings button action
	 * @param view View that called the action
	 */
	public void buttonActionSettings(View view)
	{
	    Intent intent; 
	    intent = new Intent(this, SettingsNewActivity.class);
	    startActivity(intent);		
	}
	
	/**
	 * Scores button action
	 * @param view View that called the action
	 */
	public void buttonActionHighScores(View view)
	{
	    Intent intent; 
	    intent = new Intent(this, ServerHighScoreActivity.class);
	    startActivity(intent);			
	}
	
	/**
	 * Scores button action
	 * @param view View that called the action
	 */
	public void buttonActionLocalHighScores(View view)
	{
	    Intent intent; 
	    intent = new Intent(this, LocalHighScoreActivity.class);
	    startActivity(intent);			
	}
}
