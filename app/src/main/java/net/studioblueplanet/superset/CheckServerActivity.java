package net.studioblueplanet.superset;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CheckServerActivity extends Activity implements ServerCommunication.CommunicationListener, Runnable
{
	private ServerCommunication communication;
	
	private String 				server;
	private String				username;
	private String				password;
	private int					port;
	
	SharedPreferences 			prefs;
	
	private int					communicationResponse;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Button 		okButton;

		super.onCreate(savedInstanceState);

		
		// Set the content			
		setContentView(R.layout.activity_check_server);

		
		// Disable OK button so user cannot interrupt the communication
		okButton= (Button) findViewById(R.id.check_server_button);
		okButton.setEnabled(false);		
		
		prefs=PreferenceManager.getDefaultSharedPreferences(this);
		server=prefs.getString("pref_user_server", "server").trim();
		username=prefs.getString("pref_user_user", "user").trim();
		password=prefs.getString("pref_user_password", "password").trim();
		port=Integer.parseInt(prefs.getString("pref_user_port", "1501"));
		
		communication=ServerCommunication.getInstance();
		communication.setCommunicationListener(this);
	

		communicationResponse=ServerConnection.RES_UNKNOWN;
		communication.requestCheckUser(username, password, server, port);

		
	}
	
	
	/* (non-Javadoc)
	 * @see net.studioblueplanet.superset.ServerCommunication.CommunicationListener#onCommunicationFinished(net.studioblueplanet.superset.ServerConnection.CommError, byte)
	 */
	public void onCommunicationFinished(ServerConnection.CommError communicationResult, byte response, byte[] responseData, int responseDataLength)
	{
		synchronized(this)
		{
			communicationResponse=response;
		}
		
		runOnUiThread(this);
	}

	
	/**
	 * This method handles the OK button
	 * @param view The view from which it was called.
	 */
	public void buttonActionOk(View view)
	{
		finish();
	}
	
	
	/**
	 * This runnable updates the widgets in this Activity.
	 * It is supposed to be executed by the UI Thread
	 */
	@Override
	public void run()
	{
		Button 		okButton;
		TextView	statusText;
		int			response;
		
		synchronized(this)
		{
			response=communicationResponse;
		}
		
		// Enable the OK button again
		okButton= (Button) findViewById(R.id.check_server_button);
		okButton.setEnabled(true);
		
		statusText=(TextView) findViewById(R.id.check_server_status);
		
		if (response==ServerConnection.RES_OK)
		{
			statusText.setText(getString(R.string.check_server_activity_status_userok));
		}
		else if (response==ServerConnection.RES_USERDOESNOTEXIST)
		{
			statusText.setText(getString(R.string.check_server_activity_status_unknownuser));
		}
		else if (response==ServerConnection.RES_IOERROR)
		{
			statusText.setText(getString(R.string.check_server_activity_status_ioerror));
		}
		else
		{
			statusText.setText(getString(R.string.check_server_activity_status_error));
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
