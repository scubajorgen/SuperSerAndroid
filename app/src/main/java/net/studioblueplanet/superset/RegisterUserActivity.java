package net.studioblueplanet.superset;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterUserActivity extends Activity implements ServerCommunication.CommunicationListener, Runnable
{
	private String 				server;
	private String				username;
	private String				password;
	private int					port;
	
	SharedPreferences 			prefs;
	
	private int					communicationResponse;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		EditText			editText;
		SharedPreferences 	prefs;
		TextView			statusText;
		
		
		super.onCreate(savedInstanceState);

		
		// Set the content			
		setContentView(R.layout.activity_register_user);

		// Update status: waiting for user input
		statusText=(TextView) findViewById(R.id.register_user_status);
		statusText.setText(getString(R.string.register_user_activity_status_waiting));		
		
		// Set user and password, so the user may edit it before registering
		prefs=PreferenceManager.getDefaultSharedPreferences(this);
		
		editText = (EditText) findViewById(R.id.register_user_activity_value_user);
		editText.setText(prefs.getString("pref_user_user", "user"), TextView.BufferType.EDITABLE);
		
		editText = (EditText) findViewById(R.id.register_user_activity_value_password);
		editText.setText(prefs.getString("pref_user_password", "password"), TextView.BufferType.EDITABLE);	
		
		// Get the server and port
		server=prefs.getString("pref_user_server", "server");

		port=Integer.parseInt(prefs.getString("pref_user_port", "1501"));		
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
		
		// Signal the UI thread to update the screen according to the response
		runOnUiThread(this);
	}

	
	/**
	 * This method handles the OK button
	 * @param view The view from which it was called.
	 */
	public void buttonActionOk(View view)
	{
		Button 				okButton;
		Button 				cancelButton;
		EditText			editText;
		ServerCommunication communication;
		TextView			statusText;
		
		// Update status text: contacting server
		statusText=(TextView) findViewById(R.id.register_user_status);
		statusText.setText(getString(R.string.register_user_activity_status_contacting));
		
		// Disable buttons so user cannot interrupt the communication
		okButton= (Button) findViewById(R.id.register_user_button_ok);
		okButton.setEnabled(false);
		cancelButton= (Button) findViewById(R.id.register_user_button_cancel);
		cancelButton.setEnabled(false);

		// Get the user and password from the edit fields
		editText = (EditText) findViewById(R.id.register_user_activity_value_user);
		username=editText.getText().toString();
		editText = (EditText) findViewById(R.id.register_user_activity_value_password);
		password=editText.getText().toString();
		
		
		// Register
		communication=ServerCommunication.getInstance();
		communication.setCommunicationListener(this);
		
		communication.requestRegisterUser(username, password, server, port);
	}
	
	/**
	 * This method handles the Cancel/Continue button
	 * @param view The view from which it was called.
	 */
	public void buttonActionCancel(View view)
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
		Button		cancelButton;
		TextView	statusText;
		int			response;
		
		synchronized(this)
		{
			response=communicationResponse;
		}
		
		// Enable the OK button again
		okButton= (Button) findViewById(R.id.register_user_button_ok);
		okButton.setEnabled(true);
		
		cancelButton= (Button) findViewById(R.id.register_user_button_cancel);
		cancelButton.setEnabled(true);
		
		statusText=(TextView) findViewById(R.id.register_user_status);
		
		if (response==ServerConnection.RES_OK)
		{
			statusText.setText(getString(R.string.register_user_activity_status_userok));
		}
		else if (response==ServerConnection.RES_USEREXISTS)
		{
			statusText.setText(getString(R.string.register_user_activity_status_knownuser));
		}
		else if (response==ServerConnection.RES_IOERROR)
		{
			statusText.setText(getString(R.string.register_user_activity_status_ioerror));
		}
		else
		{
			statusText.setText(getString(R.string.register_user_activity_status_error));
		}
		
	}
	

	/* 
	 * We want to catch the back button and disable it.
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed()
	{

	}
}
