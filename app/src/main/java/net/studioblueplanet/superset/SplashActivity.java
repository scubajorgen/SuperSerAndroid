package net.studioblueplanet.superset;


import android.app.Application;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import net.studioblueplanet.superset.util.Logger;

/**
 * Splash screen showing a nice image. First activity in the App
 */
public class SplashActivity extends Activity implements Runnable, View.OnClickListener
{
	Handler nextStepHandler = new Handler();

	private boolean done;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Logger.setApplicationContext(getApplicationContext());
		Logger.enable(true);
		Logger.setErrorEnabled(true);
		Logger.setWarningEnabled(true);
		Logger.setDebugEnabled(true);
		Logger.logDebug("SplashActivity", "Application started");

		super.onCreate(savedInstanceState);

		// Always landscape
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		// The view
		setContentView(R.layout.activity_splash);

		done=false;
		
		nextStepHandler = new Handler();
		nextStepHandler.removeCallbacks(this);
		nextStepHandler.postDelayed(this, 5000);

		final View contentView = findViewById(R.id.splashImageView);
		
		contentView.setOnClickListener(this);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
	}
	
	@Override
	public void run()
	{
		nextActivity();
	}
	
	@Override
	public void onClick(View view)
	{
		nextActivity();
	}
	
	private void nextActivity()
	{
		// Synchronize: because this function can be called simultaneously from multiple
		// threads and we want only one transition to next activity, we must synchronize
		synchronized(this)
		{
			if (!done)
			{
			    Intent intent; 
			    intent = new Intent(this, MainMenuActivity.class);
			    startActivity(intent);
			    finish();		
			    done=true;
			}
		}
	}
}
