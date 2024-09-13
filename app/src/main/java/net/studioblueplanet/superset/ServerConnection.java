package net.studioblueplanet.superset;

import java.net.Socket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.io.IOException;

import net.studioblueplanet.superset.util.Logger;

/**
 * Connection to the server to exchange information
 * @author jorgen
 *
 */
public class ServerConnection
{
	public enum CommError
	{
		COMMERROR_OK,
		COMMERROR_UNKNOWNHOST,
		COMMERROR_IOERROR
	};
	
	private static final String		TAG="ServerConnection";
	
	public static final byte COMMTYPE_MESSAGE					=1;
	public static final byte COMMTYPE_REQUEST					=2;
	public static final byte COMMTYPE_RESPONSE					=3;


	// messages during setup from server to client
	public static final byte MSG_RECEIVEPLAYERLIST				=3;
	
    public static final byte MSG_INVITEMULTIPLAYER				=4;
    public static final byte MSG_CANCELINVITATION				=5;
    public static final byte MSG_ACCEPTINVITATION				=6;
    public static final byte MSG_REJECTINVITATION				=7;
	
    public static final byte MSG_COUNTDOWNFORPLAY				=8;
    public static final byte MSG_STARTGAMEMASTER				=9;
    public static final byte MSG_STARTGAMESLAVE					=10;
	
	// messages during game from server to client
    public static final byte MSG_RECEIVECARDS					=11;
    public static final byte MSG_GAMEFINISHED					=12;
	public static final byte MSG_PAUSEGAME						=13;
	public static final byte MSG_RESUMEGAME						=14;
	
	// messages during game from client to server
	public static final byte MSG_SETFOUND                       =15;
	public static final byte MSG_NOSETFOUND                     =16;
	public static final byte MSG_STOPPRESSED                    =17;
	public static final byte MSG_PAUSEPRESSED                   =18;
	public static final byte MSG_RESUMEPRESSED                  =19;
	
	
	public static final byte REQ_SIGNUP							=1;		// sign up as user
	public static final byte REQ_STORESINGLEPLAYERSCORE			=2;		// add a single player score to the score list
	public static final byte REQ_SENDSINGLEPLAYERHIGHSCORES		=3;		// request for the single player high score list
	public static final byte REQ_CHECKUSER						=4;		// verify if the user name exists
	public static final byte REQ_DELETEUSER						=5;		// remove a user from the user database
	public static final byte REQ_CHECKUSERANDPASSWORD			=6;		// check if the username/password combination existis
	public static final byte REQ_MULTI_ATTACHMULTIPLAYER		=7;		// multiplay: subscribe player to multiplay info
	public static final byte REQ_MULTI_DETACHMULTIPLAYER		=8;		// multiplay: desubscribe player to multiplay info
	public static final byte REQ_MULTI_ADDMULTIPLAYER			=9;		// multiplay: set player in waiting for game state
	public static final byte REQ_MULTI_REMOVEMULTIPLAYER		=10;	// multiplay: remove player from waiting list
	public static final byte REQ_MULTI_INVITEMULTIPLAYER		=11;	// multiplay: invite another player for a multiplay
	public static final byte REQ_MULTI_CANCELINVITATION			=12;	// multiplay: request to cancel an invitation made earlier
	public static final byte REQ_MULTI_ACCEPTINVITATION			=13;	// multiplay: accept invitation: let's play
	public static final byte REQ_MULTI_REJECTINVITATION			=14;	// multiplay: reject invitation, invitor sodd off!
	public static final byte REQ_MULTI_SENDPLAYERLIST			=15;	// multiplay: request to send the list of available players
	
	
	
	public static final byte RES_OK								=0;		// response: ok, request handled as should be
	public static final byte RES_USEREXISTS						=1;		// user already exists when trying to subscribe 
	public static final byte RES_USERDOESNOTEXIST				=2;		// user does not exist
	public static final byte RES_MULTI_INVITATIONACCEPTED		=3;
	public static final byte RES_MULTI_INVITATIONREJECTED		=4;
	public static final byte RES_MULTI_PLAYERNOTACCEPTED		=5;
	public static final byte RES_MULTI_PLAYERNOTKNOWN			=6;
	public static final byte RES_IOERROR            			=-1;    // Communication error
	public static final byte RES_UNKNOWN           				=-2;    // Response unknown yet...

	
	
	private Socket 				socket;
    private InputStream         in;
	private OutputStream 		out;
//	private BufferedReader 		in;
	
	private byte[]				inBuffer;
	private byte[]				responseData;
	private int					responseDataLength;
	
	private LinearBlock			linearBlock;
	
	/** The one and only instance of this class */
	private static ServerConnection theConnection=null;
	
	private ServerConnection()
	{
		inBuffer=new byte[Config.MAXBUFFERSIZE];
		linearBlock=new LinearBlock(Config.MAXBUFFERSIZE);
	}
	
	/**
	 * This method returns the one and only singleton instance of this class.
	 * Note that it this instance is not thread save, i.e. it may be used
	 * by one threat at a time.
	 * @return The instance
	 */
	public static ServerConnection getInstance()
	{
		if (theConnection==null)
		{
			theConnection=new ServerConnection();
		}
		
		return theConnection;
	}
	
	/**
	 * Connect to the server
	 * @param server The server
	 * @param port The port
	 * @return True if an error, false if not.
	 */
	public CommError connect(String server, int port)
	{
		CommError error;
		error=CommError.COMMERROR_OK;
		
		try
		{
//		     socket = new Socket(server, port);
		    socket=new Socket();
		    socket.connect(new InetSocketAddress(server, port), 3000);
		     
		     out = socket.getOutputStream();
//		     in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             in=socket.getInputStream();
		} 
		catch (SocketTimeoutException e)
		{
			Logger.logError(TAG, "Connection timed out");
			error=CommError.COMMERROR_IOERROR;			
		}
		catch (UnknownHostException e) 
		{
			Logger.logError(TAG, "Connecting to unknonwn host "+server);
			error=CommError.COMMERROR_UNKNOWNHOST;
		 
		} 
		catch  (IOException e) 
		{
			Logger.logError(TAG, "I/O error while connecting "+e.getMessage());
			error=CommError.COMMERROR_IOERROR;
		}
		return error;
	}
	
	
	
	/**
	 * This methods sends indicated request to the server and waits for the response.
	 * @param request The request
	 * @param username Username
	 * @param password Password
	 * @param data Data array
	 * @param length Length of data array
	 * @return
	 */
	public byte sendRequest(byte request, byte[] username, byte[] password, byte[] data, int length)
	{
        byte        messageType;
		byte        response;
		int			bytesRead;
		int			dataLength;
		byte[]      outBytes;
                
		response=RES_OK;
        
		linearBlock.reset();
		linearBlock.addData(COMMTYPE_REQUEST);
		linearBlock.addData(request);
		dataLength=Config.GENERIC_MAX_STRING*2+length;
		linearBlock.addData(dataLength);
		linearBlock.addData(username, Config.GENERIC_MAX_STRING);
		linearBlock.addData(password, Config.GENERIC_MAX_STRING);
		
        if (data!=null)
        {
            linearBlock.addData(data, length);
        }
        linearBlock.setVirtualBlockSize(length+(Byte.SIZE*2*Config.GENERIC_MAX_STRING+Integer.SIZE)/8);

        linearBlock.obfuscateData();

        outBytes=linearBlock.getDataBlock();

        try
        {
            out.write(outBytes, 0, linearBlock.getDataBlockSize());
            Logger.logDebug(TAG, "Request sent: "+ request);
            
            bytesRead=in.read(inBuffer);
            if (bytesRead>0)
            {
            	responseData		=null;
            	responseDataLength	=0;
            	
                linearBlock.reset();
                linearBlock.importBlockData(inBuffer, bytesRead);
                linearBlock.deobfuscateData();

                // read message type
                messageType	=linearBlock.getDataByte();
                // read ID
                linearBlock.getDataByte();
                // read datalength
                dataLength	=linearBlock.getDataInt();
                // read response
                response	=linearBlock.getDataByte();

                Logger.logDebug(TAG, "Response received: "+ response);
                
                if (messageType!=COMMTYPE_RESPONSE || dataLength<1)
                {
                    response=RES_IOERROR;
                }
                else
                {
                    // Check if the response contains response data
                    if (dataLength>1)
                    {
                    	this.responseData		=linearBlock.getData(dataLength-1);
                    	this.responseDataLength	=dataLength-1;
                    }

                }
            }
        }
        catch (IOException e)
        {
			Logger.logError(TAG, "I/O error while sending request/receiving response");
            response=RES_IOERROR;
        }
		
                
		
		
		return response;
	}
	
	/**
	 * This method closes the socket connection
	 */
	public void close()
    {
        try
        {
            this.socket.close();
        }
        catch (IOException e)
        {
			Logger.logError(TAG, "I/O error while closing connection");
        }
    }
	

	/**
	 * This method returns the response data in case of successful request. Returns
	 * null if no data has been received.
	 * @return The response data, without overhead
	 */
	public byte[] getResponseData()
	{
		return this.responseData;
	}
	
	/**
	 * Returns the response data length in case of a successful request.
	 * @return The data length or 0 of no data
	 */
	public int getResponseDataLength()
	{
		return this.responseDataLength;
	}
	
}
