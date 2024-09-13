package net.studioblueplanet.superset;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LocalHighScoreActivity extends Activity
{
	private static final String	TAG="LocalHighScoreActivity";
	LocalHighScoreList list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		TextView				statusText;
		ArrayList<String> 		itemList;
		String					item;
		ListView				view;
		ArrayAdapter<String>	adapter;
		boolean					finished;
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_local_high_score);
		
		list=LocalHighScoreList.getInstance();
		

		list.readScores(this.getBaseContext());
		list.resetScorePointer();
		
		statusText=(TextView) findViewById(R.id.local_highscore_activity_status);
		statusText.setText(getString(R.string.local_highscore_activity_status_ok));
		
		
		view=(ListView) findViewById(R.id.local_highscore_activity_list);
		itemList=new ArrayList<String>();
		finished=false;
		while (!finished)
		{
		
			item=list.nextScore();
			if (item!=null)
			{
				itemList.add(item);
			}
			else
			{
				finished=true;
			}
		}
		
	    adapter = new ArrayAdapter<String>(this, R.layout.listitem_high_score, itemList);
	    view.setAdapter(adapter);
		

		
	}

	
    /**
     * The action that is executed when OK button is pressed
     * @param view
     */
    public void buttonOkAction(View view)
    {
    	finish();
    }
    

    /**
     * The action that is executed when OK button is pressed
     * @param view
     */
    public void buttonResetAction(View view)
    {
    	AlertDialog.Builder alertDialogBuilder; 
    	AlertDialog 		alertDialog;
    	
		alertDialogBuilder = new AlertDialog.Builder(this);
		 
		// set title
		alertDialogBuilder.setTitle("Are you sure to delete the list?");

		// set dialog message
		alertDialogBuilder
			.setMessage("Click yes to delete, no to cancel.")
			.setCancelable(false)
			.setPositiveButton("Yes",new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog,int id) 
				{
					// if this button is clicked, close
					// current activity
					eraseHighScores();
				}
			})
			.setNegativeButton("No",new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, just close
					// the dialog box and do nothing
					dialog.cancel();

				}
			});

			// create alert dialog
			alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();    		
    	
    }
    
    
    /**
     * Execute the score reset
     */
    public void eraseHighScores()
    {
		ListView				view;
		
		list=LocalHighScoreList.getInstance();
		list.resetScores(this.getBaseContext());
		view=(ListView) findViewById(R.id.local_highscore_activity_list);
		view.setAdapter(null);

		
    }


}
