package net.studioblueplanet.superset;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import net.studioblueplanet.superset.util.Logger;

/**
 * This class implements the communication to the server.
 * It runs its own thread, because socket communication may not be
 * executed in the main thread.
 * @author jorgen
 *
 */
public class ServerCommunication implements Runnable
{
	/**
	 * Listener function that is being called 
	 * @author jorgen
	 *
	 */
	public interface CommunicationListener
	{
		public void onCommunicationFinished(ServerConnection.CommError communicationResult, byte response, byte[] responseData, int responseDataLength);
	}

	private static final String				TAG="ServerCommunication";
	
	/** The one and only instance of this class (Singleton) */
	private static ServerCommunication 		theInstance=null;

	private CommunicationListener 			theListener=null;
	
	private Thread							thread;
	private boolean							threadRunning;
	
	
	private ServerConnection 				connection;
	
	private String 							server;
	private String							username;
	private String							password;
	private int								port;
	
	private ByteBuffer						data;
	private int								dataLength;
	
	private byte							theRequest;
	

	
	/**
	 * Private constructor. Creates the one and only instance;
	 */
	private ServerCommunication()
	{
		data=ByteBuffer.allocate(16);
		data.order(ByteOrder.LITTLE_ENDIAN);
		threadRunning=false;
	}
	
	/**
	 * This method returns the one and only instance of this singleton class
	 * @return The instance
	 */
	public static ServerCommunication getInstance()
	{
		if (theInstance==null)
		{
			theInstance=new ServerCommunication();
		}
		return theInstance;
	}
	
	public void setCommunicationListener(CommunicationListener listener)
	{
		this.theListener=listener;
	}
	
	
	/**
	 * This method send the score to the server
	 * @param username
	 * @param password
	 * @param Server
	 * @param port
	 * @param score
	 */
	public void requestSendScore(String username, String password, String server, int port, int score)
	{
		boolean localThreadRunning;
		
		synchronized(this)
		{
			localThreadRunning=threadRunning;
		}
		
		if (!localThreadRunning)
		{
			this.theRequest = ServerConnection.REQ_STORESINGLEPLAYERSCORE;
			this.username	=username;
			this.password	=password;
			this.server		=server;
			this.port		=port;
			data.rewind();
			data.putInt(score);
			dataLength=Integer.SIZE/Byte.SIZE;
			
			thread=new Thread(this);
			threadRunning=true;
			thread.start();
		}
		else
		{
			// Apparently the thread is still running. Should not happen...
			if (theListener!=null)
			{
				theListener.onCommunicationFinished(ServerConnection.CommError.COMMERROR_IOERROR, ServerConnection.RES_IOERROR, null, 0);
				Logger.logError(TAG, "Communication thread still running while starting next request");
			}
		}
	}
	
	/**
	 * This method requests the server to check the user
	 * @param username Username to check
	 * @param password Password to check
	 * @param server Server to request to
	 * @param port Server port to be used
	 */
	public void requestCheckUser(String username, String password, String server, int port)
	{
		boolean localThreadRunning;
		
		synchronized(this)
		{
			localThreadRunning=threadRunning;
		}
		
		if (!localThreadRunning)
		{
			this.theRequest = ServerConnection.REQ_CHECKUSERANDPASSWORD;
			this.username	=username;
			this.password	=password;
			this.server		=server;
			this.port		=port;
			dataLength		=0;
			
			
			thread=new Thread(this);
			threadRunning=true;
			thread.start();
		}
		else
		{
			// Apparently the thread is still running. Should not happen...
			if (theListener!=null)
			{
				theListener.onCommunicationFinished(ServerConnection.CommError.COMMERROR_IOERROR, ServerConnection.RES_IOERROR, null, 0);
				Logger.logError(TAG, "Communication thread still running while starting next request");
			}
		}		
	}
	
	/**
	 * This method requests the server to register the user
	 * @param username Username to check
	 * @param password Password to check
	 * @param server Server to request to
	 * @param port Server port to be used
	 */
	public void requestRegisterUser(String username, String password, String server, int port)
	{
		boolean localThreadRunning;
		
		synchronized(this)
		{
			localThreadRunning=threadRunning;
		}
		
		if (!localThreadRunning)
		{
			this.theRequest = ServerConnection.REQ_SIGNUP;
			this.username	=username;
			this.password	=password;
			this.server		=server;
			this.port		=port;
			dataLength		=0;
			
			thread=new Thread(this);
			threadRunning=true;
			thread.start();
		}
		else
		{
			// Apparently the thread is still running. Should not happen...
			if (theListener!=null)
			{
				theListener.onCommunicationFinished(ServerConnection.CommError.COMMERROR_IOERROR, ServerConnection.RES_IOERROR, null, 0);
				Logger.logError(TAG, "Communication thread still running while starting next request");
			}
		}		
	}	
	
	/**
	 * This method requests the server to unregister the user
	 * @param username Username to check
	 * @param password Password to check
	 * @param server Server to request to
	 * @param port Server port to be used
	 */
	public void requestUnregisterUser(String username, String password, String server, int port)
	{
		boolean localThreadRunning;
		
		synchronized(this)
		{
			localThreadRunning=threadRunning;
		}
		
		if (!localThreadRunning)
		{
			this.theRequest = ServerConnection.REQ_DELETEUSER;
			this.username	=username;
			this.password	=password;
			this.server		=server;
			this.port		=port;
			dataLength		=0;
			
			thread=new Thread(this);
			threadRunning=true;
			thread.start();
		}
		else
		{
			// Apparently the thread is still running. Should not happen...
			if (theListener!=null)
			{
				theListener.onCommunicationFinished(ServerConnection.CommError.COMMERROR_IOERROR, ServerConnection.RES_IOERROR, null, 0);
				Logger.logError(TAG, "Communication thread still running while starting next request");
			}
		}		
	}
	
	/**
	 * This method requests the highscores
	 * @param username Username to check
	 * @param password Password to check
	 * @param server Server to request to
	 * @param port Server port to be used
	 */
	public void requestHighscores(String username, String password, String server, int port)
	{
		boolean localThreadRunning;
		
		synchronized(this)
		{
			localThreadRunning=threadRunning;
		}
		
		if (!localThreadRunning)
		{
			this.theRequest = ServerConnection.REQ_SENDSINGLEPLAYERHIGHSCORES;
			this.username	=username;
			this.password	=password;
			this.server		=server;
			this.port		=port;
			dataLength		=0;
			
			
			thread=new Thread(this);
			threadRunning=true;
			thread.start();
		}
		else
		{
			// Apparently the thread is still running. Should not happen...
			if (theListener!=null)
			{
				theListener.onCommunicationFinished(ServerConnection.CommError.COMMERROR_IOERROR, ServerConnection.RES_IOERROR, null, 0);
				Logger.logError(TAG, "Communication thread still running while starting next request");
			}
		}		
	}	
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		ServerConnection.CommError error;
		byte				response;
		
		
		// Some points of starting
		error=ServerConnection.CommError.COMMERROR_OK;
		response=ServerConnection.RES_IOERROR;

		connection=ServerConnection.getInstance();
	
		error=connection.connect(server, port);

		if (error== ServerConnection.CommError.COMMERROR_OK)
        {
            response=connection.sendRequest(theRequest, username.getBytes(), password.getBytes(), data.array(), dataLength);
            
			connection.close();
        }
	
		
		// Notify communication is finished
		if (theListener!=null)
		{
			theListener.onCommunicationFinished(error, response, connection.getResponseData(), connection.getResponseDataLength());
		}
		
		synchronized(this)
		{
			threadRunning=false;
		}
	}
}
