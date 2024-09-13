package net.studioblueplanet.superset;



import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import android.content.Context;

import net.studioblueplanet.superset.util.Logger;


/**
 * This class represents the list of local highscores
 * @author jorgen
 *
 */
public class LocalHighScoreList
{
	private class HighScore
	{
		String name;
		String date;
		double score;
	}
	
	private static final String 			TAG 		= "LocalHighScoreList";

	private static final String 			FILENAME 	= "SuperSetHighScores.txt";
	private static final int				MAXSCORES	=100;
	
	private static LocalHighScoreList 		theInstance	=null;
	
	private ArrayList<HighScore> 			scores;
	
	private int								scorePointer;
	
	
	/**
	 * The constructor
	 */
	private LocalHighScoreList()
	{
		scores=new ArrayList<HighScore>();
		scorePointer=0;
	}

	/**
	 * Returns the one and only instance of this class (Singleton pattern)
	 * @return The instance
	 */
	public static LocalHighScoreList getInstance()
	{
		if (theInstance==null)
		{
			theInstance=new LocalHighScoreList();
		}
		return theInstance;
	}
	
	
	/**
	 * Add socre to the score list. If the score list is full, last score is purged
	 * @param context Context to use for file writing
	 * @param name Username
	 * @param score His score
	 */
	public void addScore(Context context, String name, double score)
	{
		HighScore   		newHighScore;
		HighScore			highScore;
		int					elements;
		boolean				found;
		int					i;
		SimpleDateFormat  	dateFormat;
		Date	    		date;
		
		// Just make sure the scores have been loaded
		if (scores.size()==0)
		{
			readScores(context);
		}
		
		elements=scores.size();
		
		
		newHighScore=new HighScore();
		newHighScore.name=name;
		
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		date = new Date();
		newHighScore.date=dateFormat.format(date);
		
		newHighScore.score=score;
		
		if (elements==0)
		{
			scores.add(newHighScore);
			Logger.logDebug(TAG, "Score added: "+name+" "+score);
		}
		else
		{
			i=0;
			found=false;
			while (i<elements && !found)
			{
				highScore=scores.get(i);
				if (highScore.score<=score)
				{
					scores.add(i, newHighScore);
					Logger.logDebug(TAG, "Score added: "+name+" "+score);
					found=true;
				}
				i++;
			}
			// If no place found, the score is the lowest. 
			// Append it at the bottom of the list
			if (!found)
			{
				Logger.logDebug(TAG, "Score added: "+name+" "+score);
				scores.add(newHighScore);
			}
		}

		
		// Now trim the list, so it does not exceed MAXSCORES elements
		elements=scores.size();
		
		// If the list is full, remove the last score
		while (elements>MAXSCORES)
		{
			scores.remove(elements-1);
			elements=scores.size();
		}
		
		
		// Write the scores to file
		writeScores(context);
	}
	
	
	/**
	 * Read the highscores file
	 * @param context
	 */
	public void readScores(Context context)
	{
		FileInputStream 	file;
		InputStreamReader 	reader;
		BufferedReader		bufferedReader;
		String				line;
		StringTokenizer		tokenizer;
		String				username;
		String				date;
		double				score;
		HighScore			highScore;
		ArrayList<HighScore>	newScores;
		
	
	
		newScores=new ArrayList<HighScore>();
		
		try
		{
			file = context.openFileInput(FILENAME);

		    reader = new InputStreamReader(file, "UTF-8");
		    bufferedReader=new BufferedReader(reader);
		    try 
		    {
		    	do
		    	{
		    		line=bufferedReader.readLine();
		    		
		    		if (line!=null)
		    		{
		    			tokenizer=new StringTokenizer(line, "|");
		    			if (tokenizer.countTokens()==3)
		    			{
		    				username=tokenizer.nextToken().trim();
		    				date=tokenizer.nextToken().trim();
		    				score=Double.parseDouble(tokenizer.nextToken());
		    				highScore=new HighScore();
		    				highScore.name=username;
		    				highScore.date=date;
		    				highScore.score=score;
		    				newScores.add(highScore);
		    			}
		    		}
		    	}
		    	while (line!=null);
		    }
		    finally 
		    {
		    	bufferedReader.close();
		    	reader.close();
		    }	
			
			file.close();
			
			scores=newScores;
			Logger.logDebug(TAG, "Scores read from "+FILENAME);
		}
		catch (FileNotFoundException e)
		{
			Logger.logError(TAG, "File not found "+FILENAME);
		}
		catch (IOException e)
		{
			Logger.logError(TAG, "Error reading file "+FILENAME);
		}		
	}
	
	/**
	 * Write the highscore file
	 * @param context
	 */
	public void writeScores(Context context)
	{
		FileOutputStream 	fos;

		int					elements;
		int					i;
		HighScore			score;

		
		try
		{
			fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
			
			elements=scores.size();
			i=0;
			while (i<elements)
			{
				score=scores.get(i);
				fos.write(String.format("%s| %s| %f\n", score.name, score.date, score.score).getBytes());
				i++;
			}
			
			fos.close();
			Logger.logDebug(TAG, "Scores written to "+FILENAME);
		}
		catch (FileNotFoundException e)
		{
			Logger.logError(TAG, "File not found "+FILENAME);
		}
		catch (IOException e)
		{
			Logger.logError(TAG, "Error reading file "+FILENAME);
		}	
	}
	
	/**
	 * This method resets the highscores
	 * @param context
	 */
	public void resetScores(Context context)
	{
		scores.clear();
		writeScores(context);
        Logger.logDebug(TAG, "Local scores reset");
	}
	
	
	
	/**
	 * Resets the score pointer to the first score
	 */
	public void resetScorePointer()
	{
		scorePointer=0;
	}
	
	/**
	 * Returns the next score in line, as string. Reset the score pointer first using
	 * resetScorePointer()
	 * @return Next score or null if the end of the list is reached
	 */
	public String nextScore()
	{
		HighScore 	score;
		String		scoreString;
	
		scoreString=null;
		
		if (scorePointer<scores.size())
		{
			score=scores.get(scorePointer);
			scoreString=String.format("%3d %20s - %s - %d", scorePointer+1, score.name, score.date, (int)score.score);
			scorePointer++;
		}
		return scoreString;
	}

}
