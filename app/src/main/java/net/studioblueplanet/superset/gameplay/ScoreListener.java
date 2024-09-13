package net.studioblueplanet.superset.gameplay;

public interface ScoreListener
{
	/**
	 * This method must be called when the game is finished, resulting in a score to be send
	 * @param score The score to be send to the server
	 */
	public void onScore(int score);
}
