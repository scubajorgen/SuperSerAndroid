package net.studioblueplanet.superset.util;

import android.content.Context;
import android.os.Environment;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Logger class that can be used to write messages to a file
 */
public class Logger
{
	private final static String			LOGFOLDER="/superset/";
	private final static String			LOGFILENAME="superset.txt";
	
	private static boolean				isEnabled=true;
	private static boolean				isErrorEnabled=true;
	private static boolean				isWarningEnabled=false;
	private static boolean				isDebugEnabled=false;

	private static final FileWriter 	writer=new FileWriter();

	private static Context 				applicationContext;



	/**
	 * Enable the logger
	 * @param enable
	 */
	public static void enable(boolean enable)
	{
		if (checkExternalMedia())
		{
			isEnabled=enable;
		}
		else
		{
			isEnabled=false;
		}
	}

	/**
	 * Sets the application context
	 * @param appContext The application context
	 */
	public static void setApplicationContext(Context appContext)
	{
		applicationContext=appContext;
	}
	
	/** Method to check whether external media available and writable. This is adapted from
	   http://developer.android.com/guide/topics/data/data-storage.html#filesExternal */

	 private static boolean checkExternalMedia()
	 {
		 boolean mExternalStorageAvailable = false;
		 boolean mExternalStorageWriteable = false;
		 String state = Environment.getExternalStorageState();

		 if (Environment.MEDIA_MOUNTED.equals(state)) 
		 {
	        // Can read and write the media
	        mExternalStorageAvailable = mExternalStorageWriteable = true;
		 }
		 else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) 
		 {
	        // Can only read the media
	        mExternalStorageAvailable = true;
	        mExternalStorageWriteable = false;
		 } 
		 else 
		 {
	        // Can't read or write
	        mExternalStorageAvailable = mExternalStorageWriteable = false;
		 }   
		 return (mExternalStorageAvailable && mExternalStorageWriteable);
	}

	private static String getTimeString()
	{
		DateFormat df = new SimpleDateFormat("yyyy-mm-dd HH:mm");
		return df.format(Calendar.getInstance().getTime());
	}

	/**
	 * Log an error message
	 * @param tag Reference to the class
	 * @param message Error message
	 */
	public static void logError(String tag, String message)
	{
		String logMessage;
		
		if (isEnabled && isErrorEnabled)
		{
			logMessage=String.format("%s ERROR %s: %s\n", getTimeString(), tag, message);
			writer.write(LOGFOLDER, LOGFILENAME, logMessage, applicationContext, true);
		}
	}
	
	/**
	 * Log an error message
	 * @param tag Reference to the class
	 * @param message Error message
	 */
	public static void logWarning(String tag, String message)
	{
		String logMessage;
		
		if (isEnabled && isWarningEnabled)
		{
			logMessage=String.format("%s WARNING %s: %s\n", getTimeString(), tag, message);
			writer.write(LOGFOLDER, LOGFILENAME, logMessage, applicationContext, true);
		}
	}
	
	/**
	 * Log an error message
	 * @param tag Reference to the class
	 * @param message Error message
	 */
	public static void logDebug(String tag, String message)
	{
		String logMessage;
		
		if (isEnabled && isDebugEnabled)
		{
			logMessage=String.format("%s DEBUG %s: %s\n", getTimeString(), tag, message);
			writer.write(LOGFOLDER, LOGFILENAME, logMessage, applicationContext, true);
		}
	}
	
	/**
	 * Sets error logging to enabled
	 * @param enabled True to enable, false to disable
	 */
	public static void setErrorEnabled(boolean enabled)
	{
		isErrorEnabled=enabled;
	}
	
	/**
	 * Sets error logging to enabled
	 * @param enabled True to enable, false to disable
	 */
	public static void setWarningEnabled(boolean enabled)
	{
		isWarningEnabled=enabled;
	}
	
	/**
	 * Sets error logging to enabled
	 * @param enabled True to enable, false to disable
	 */
	public static void setDebugEnabled(boolean enabled)
	{
		isDebugEnabled=enabled;
	}
	
	
}
