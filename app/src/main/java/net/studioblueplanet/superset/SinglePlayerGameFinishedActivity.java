package net.studioblueplanet.superset;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.preference.PreferenceManager;

import net.studioblueplanet.superset.ServerCommunication.CommunicationListener;


/**
 * This class implements the activity that offers the option to send 
 * the score to the server
 * @author jorgen
 *
 */
public class SinglePlayerGameFinishedActivity extends Activity implements CommunicationListener, Runnable
{
	private int 				score;
	
	private ServerCommunication communication;
	
	private String 				server;
	private String				username;
	private String				password;
	private int					port;

	
	private byte				communicationResponse;
	

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		TextView 			textView;
		EditText			editText;
		Intent				intent;
		SharedPreferences 	prefs;
		
		intent = getIntent();
		this.score = intent.getIntExtra(SinglePlayerGameActivity.SCORE_MESSAGE, -1);
		
		
		// Set the content	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_player_game_finished);

		textView = (TextView) findViewById(R.id.sp_game_finished_activity_score_value);
		textView.setText(String.valueOf(score), TextView.BufferType.NORMAL);
		
		prefs=PreferenceManager.getDefaultSharedPreferences(this);
		
		editText = (EditText) findViewById(R.id.sp_game_finished_activity_user_value);
		editText.setText(prefs.getString("pref_user_user", "user").trim(), TextView.BufferType.EDITABLE);
		

		editText = (EditText) findViewById(R.id.sp_game_finished_activity_password_value);
		editText.setText(prefs.getString("pref_user_password", "password").trim(), TextView.BufferType.EDITABLE);


		editText = (EditText) findViewById(R.id.sp_game_finished_activity_server_value);
		editText.setText(prefs.getString("pref_user_server", "server").trim(), TextView.BufferType.EDITABLE);

		editText = (EditText) findViewById(R.id.sp_game_finished_activity_port_value);
		editText.setText(prefs.getString("pref_user_port", "1501").trim(), TextView.BufferType.EDITABLE);

		
		communicationResponse=ServerConnection.RES_IOERROR;
		
	}


	/**
	 * Button call back that sends the score
	 * @param view The view from which it was called
	 */
	public void sendScore(View view) 
	{
		Button 				sendButton;
		Button 				skipButton;
		EditText			editText;
		TextView			statusText;
		
		// Disable buttons so user cannot interrupt the communication
		sendButton= (Button) findViewById(R.id.sp_game_finished_activity_send_button);
		sendButton.setEnabled(false);
		skipButton= (Button) findViewById(R.id.sp_game_finished_activity_dontsend_button);
		skipButton.setEnabled(false);

		
		editText = (EditText) findViewById(R.id.sp_game_finished_activity_user_value);
		username=editText.getText().toString().trim();
		editText = (EditText) findViewById(R.id.sp_game_finished_activity_password_value);
		password=editText.getText().toString().trim();
		editText = (EditText) findViewById(R.id.sp_game_finished_activity_server_value);
		server=editText.getText().toString().trim();
		
		editText = (EditText) findViewById(R.id.sp_game_finished_activity_port_value);
		port=Integer.parseInt(editText.getText().toString());


		statusText=(TextView) findViewById(R.id.sp_game_finished_activity_status);
		statusText.setText(getString(R.string.sp_game_finished_activity_status_sending));
		
		communication=ServerCommunication.getInstance();
		communication.setCommunicationListener(this);
		
		communication.requestSendScore(username, password, server, port, score);
	}

	/**
	 * Button call back that doesn't sends the score
	 * @param view The view from which it was called
	 */
	public void dontSendScore(View view) 
	{
		// The page is always left via this button. So this is the place to
		// add the score to the local highscore list.
		// Add the score to the local highscore list
		this.addScoreToLocalList();

		this.finish();
	}


	
	/**
	 * This method adds the score to the local highscore list
	 */
	public void addScoreToLocalList()
	{
		LocalHighScoreList 	list;
		String				name;
		EditText			editText;
		
		editText = (EditText) findViewById(R.id.sp_game_finished_activity_user_value);
		name=editText.getText().toString().trim();
		
		list=LocalHighScoreList.getInstance();
		list.addScore(this.getBaseContext(), name, score);
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
		Button 		skipButton;
		Button		sendButton;
		TextView	statusText;
		int			response;
		boolean		scoreSent;
		
		scoreSent=false;
		
		synchronized(this)
		{
			response=communicationResponse;
		}
		
		
		statusText=(TextView) findViewById(R.id.sp_game_finished_activity_status);
		
		if (response==ServerConnection.RES_OK)
		{
			statusText.setText(getString(R.string.sp_game_finished_activity_status_ok));
			scoreSent=true;
		}
		else if (response==ServerConnection.RES_USERDOESNOTEXIST)
		{
			statusText.setText(getString(R.string.sp_game_finished_activity_status_unknownuser));
		}
		else if (response==ServerConnection.RES_IOERROR)
		{
			statusText.setText(getString(R.string.sp_game_finished_activity_status_ioerror));
		}
		else
		{
			statusText.setText(getString(R.string.sp_game_finished_activity_status_error));
		}		
		
		// Enable the Continue/Skip button again
		skipButton= (Button) findViewById(R.id.sp_game_finished_activity_dontsend_button);
		skipButton.setEnabled(true);
		// Enable the Send button again if the score was not sent. User may retry
		if (!scoreSent)
		{
			sendButton= (Button) findViewById(R.id.sp_game_finished_activity_send_button);
			sendButton.setEnabled(true);
		}	
		else
		{
			skipButton.setText(R.string.sp_game_finished_activity_button_continue);
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
