package net.studioblueplanet.superset;

import android.app.Activity;

/**
 * This class keeps track of current activity that is active
 * @author jorgen
 *
 */
public class CurrentActivityRegister
{
	private static CurrentActivityRegister theInstance=null;
	
	private Activity currentActivity;
	
	/**
	 * The constructor 
	 */
	private CurrentActivityRegister()
	{
		currentActivity=null;
	}
	
	/**
	 * This method returns the one and only instance of the singleton class
	 * @return
	 */
	public static CurrentActivityRegister getInstance()
	{
		if (theInstance==null)
		{
			theInstance=new CurrentActivityRegister();
		}
		return theInstance;
	}
	
	/**
	 * Registers current active activity
	 * @param activity
	 */
	public void registerCurrentActivity(Activity activity)
	{
		currentActivity=activity;
	}
	
	/**
	 * This method returns current activity
	 * @return
	 */
	public Activity getCurrentActivity()
	{
		return currentActivity;
	}
	
	

}
