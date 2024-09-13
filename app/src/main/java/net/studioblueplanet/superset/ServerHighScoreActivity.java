package net.studioblueplanet.superset;


import java.util.ArrayList;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.ArrayAdapter;

public class ServerHighScoreActivity extends Activity implements ServerCommunication.CommunicationListener, Runnable
{
	public static final int	HIGHSCORELISTITEMLENGTH=50;
	
	private ServerCommunication communication;
	
	private String 				server;
	private String				username;
	private String				password;
	private int					port;
	
	SharedPreferences 			prefs;
	
	private int					communicationResponse;
	private byte[]				responseData;
	private int					responseDataLength;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Button 		okButton;
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sever_high_score);
		
		
		// Disable OK button so user cannot interrupt the communication
		okButton= (Button) findViewById(R.id.server_highscore_activity_button_ok);
		okButton.setEnabled(false);		
		
		prefs=PreferenceManager.getDefaultSharedPreferences(this);
		server=prefs.getString("pref_user_server", "server");
		username=prefs.getString("pref_user_user", "user");
		password=prefs.getString("pref_user_password", "password");
		port=Integer.parseInt(prefs.getString("pref_user_port", "1501"));
		
		communication=ServerCommunication.getInstance();
		communication.setCommunicationListener(this);
	

		communicationResponse=ServerConnection.RES_UNKNOWN;
		communication.requestHighscores(username, password, server, port);
	}

	
    /**
     * The action that is executed when OK button is pressed
     * @param view
     */
    public void buttonOkAction(View view)
    {
    	finish();
    }
    
    
	/* (non-Javadoc)
	 * @see net.studioblueplanet.superset.ServerCommunication.CommunicationListener#onCommunicationFinished(net.studioblueplanet.superset.ServerConnection.CommError, byte)
	 */
	public void onCommunicationFinished(ServerConnection.CommError communicationResult, byte response, byte[] responseData, int responseDataLength)
	{
		synchronized(this)
		{
			communicationResponse=response;
			this.responseData=responseData;
			this.responseDataLength=responseDataLength;
		}
		
		runOnUiThread(this);
	}

	
	
	/**
	 * This runnable updates the widgets in this Activity.
	 * It is supposed to be executed by the UI Thread
	 */
	@Override
	public void run()
	{
		Button 					okButton;
		TextView				statusText;
		int						response;
		int 					i;
		ArrayList<String> 		itemList;
		String					item;
		ListView				view;
		ArrayAdapter<String>	adapter;
		
		synchronized(this)
		{
			response=communicationResponse;
		}
		
		// Enable the OK button again
		okButton= (Button) findViewById(R.id.server_highscore_activity_button_ok);
		okButton.setEnabled(true);
		
		statusText=(TextView) findViewById(R.id.server_highscore_activity_status);
		
		if (response==ServerConnection.RES_OK)
		{
			statusText.setText(getString(R.string.server_highscore_activity_status_ok));
			
		
			view=(ListView) findViewById(R.id.server_highscore_activity_list);
			itemList=new ArrayList<String>();
			i=0;
			while ((i+1)*HIGHSCORELISTITEMLENGTH<=responseDataLength)
			{
			
				item=new String(responseData, i*HIGHSCORELISTITEMLENGTH, HIGHSCORELISTITEMLENGTH);
				itemList.add(item);
				i++;
			}
			
		    adapter = new ArrayAdapter<String>(this, R.layout.listitem_high_score, itemList);
		    view.setAdapter(adapter);
			
		}
		else if (response==ServerConnection.RES_IOERROR)
		{
			statusText.setText(getString(R.string.server_highscore_activity_status_ioerror));
		}
		else
		{
			statusText.setText(getString(R.string.server_highscore_activity_status_error));
		}
		
	}    
	
	/* 
	 * We want to catch the back button and disable it.
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed()
	{
		int			response;
		
		synchronized(this)
		{
			response=communicationResponse;
		}		
		// Only if the communication has finished we allow to use the back button
		// If communication is busy, we won't allow, because the communication thread
		// must finish.
		if (response!=ServerConnection.RES_UNKNOWN)
		{
			super.onBackPressed();
		}
	}	

}
