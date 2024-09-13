package net.studioblueplanet.superset;


import net.studioblueplanet.superset.ServerCommunication.CommunicationListener;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DeleteUserActivity extends Activity  implements CommunicationListener, Runnable
{
	private String			username;
	private String 			password;
	private String			server;
	private int 			port;
	private byte			communicationResponse;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		EditText			editText;
		SharedPreferences 	prefs;
		TextView			statusText;
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delete_user);
		
		// Update status text: waiting for user input
		statusText=(TextView) findViewById(R.id.delete_user_status);
		statusText.setText(getString(R.string.delete_user_activity_status_waiting));
		
		prefs=PreferenceManager.getDefaultSharedPreferences(this);
		
		editText = (EditText) findViewById(R.id.delete_user_activity_value_user);
		editText.setText(prefs.getString("pref_user_user", "user").trim(), TextView.BufferType.EDITABLE);
		

		editText = (EditText) findViewById(R.id.delete_user_activity_value_password);
		editText.setText(prefs.getString("pref_user_password", "password").trim(), TextView.BufferType.EDITABLE);	
		
		server=prefs.getString("pref_user_server", "server").trim();


		port=Integer.parseInt(prefs.getString("pref_user_port", "1501"));
	}


	
	public void buttonCancelAction(View view)
	{
		finish();
	}

	public void buttonOkAction(View view)
	{
		Button 				okButton;
		Button 				cancelButton;
		EditText			editText;
		ServerCommunication communication;
		TextView			statusText;
		
		// Update status text: waiting for user input
		statusText=(TextView) findViewById(R.id.delete_user_status);
		statusText.setText(getString(R.string.delete_user_activity_status_contacting));
		
		// Disable buttons so user cannot interrupt the communication
		okButton= (Button) findViewById(R.id.delete_user_button_ok);
		okButton.setEnabled(false);
		cancelButton= (Button) findViewById(R.id.delete_user_button_cancel);
		cancelButton.setEnabled(false);

		// Get the user and password from the edit fields
		editText = (EditText) findViewById(R.id.delete_user_activity_value_user);
		username=editText.getText().toString().trim();
		editText = (EditText) findViewById(R.id.delete_user_activity_value_password);
		password=editText.getText().toString().trim();
		
		// Do the unregister
		communication=ServerCommunication.getInstance();
		communication.setCommunicationListener(this);
		
		communication.requestUnregisterUser(username, password, server, port);
		
	}
	
	public void onCommunicationFinished(ServerConnection.CommError communicationResult, byte response, byte[] responseData, int responseDataLength)
	{
		synchronized(this)
		{
			communicationResponse=response;
		}
		
		runOnUiThread(this);	
	}	
	
	/**
	 * This Runnable updates the status. It is expected to be executed by the UI Thread
	 */
	public void run()
	{
		Button 				okButton;
		Button				cancelButton;
		TextView			statusText;
		int					response;

	
		synchronized(this)
		{
			response=communicationResponse;
		}
		
		
		statusText=(TextView) findViewById(R.id.delete_user_status);
		
		if (response==ServerConnection.RES_OK)
		{
			statusText.setText(getString(R.string.delete_user_activity_status_userdeleted));
		}
		else if (response==ServerConnection.RES_USERDOESNOTEXIST)
		{
			statusText.setText(getString(R.string.delete_user_activity_status_unknownuser));
		}
		else if (response==ServerConnection.RES_IOERROR)
		{
			statusText.setText(getString(R.string.delete_user_activity_status_ioerror));
		}
		else
		{
			statusText.setText(getString(R.string.delete_user_activity_status_error));
		}		
		
		// Enable the buttons again
		okButton= (Button) findViewById(R.id.delete_user_button_ok);
		okButton.setEnabled(true);
		cancelButton= (Button) findViewById(R.id.delete_user_button_cancel);
		cancelButton.setEnabled(true);
		
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
