package net.studioblueplanet.superset;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class LinearBlock
{
    public static final int BITS_PER_BYTE=8;
	private static final int MAX_ENCRYPTION_BYTES=6;

	private static final int MINSUBKEYLENGTH=8;
	private static final int MAXSUBKEYLENGTH=16;	
	
	/**
	 *  Key used for encryption first stage
	 */
	private static String   encryptionKeyString1=
			"It is the question that drives us, Neo. It is the question that brought you here. You know the question, just as I did.";
	private static char[]	encryptionKey1=encryptionKeyString1.toCharArray();

	/**
	 *  Key used for encryption second stage
	 */
	private static String   encryptionKeyString2=
			"One ring to rule them all, one ring to find them, one ring to bring them all,and in darkness bind them";

	private static char[]	encryptionKey2=encryptionKeyString2.toCharArray();

	
	private int				blockPointer;
	private byte[]			block;
	private int				blockSize;
	private int				virtualBlockSize;

	

	/**
	 * Creates the instance. Allocates the buffer
	 * @param maxSize
	 */
	public LinearBlock(int maxSize)
	{
		blockSize		=maxSize+MAX_ENCRYPTION_BYTES;
		block			=new byte[blockSize];
		
		virtualBlockSize=blockSize;
		reset();
	}
	
	



	/**
	 * Sets the size of the virtual block
	 * @param size The size
	 * @return True if an error occurred (size to large) or false if no error 
	 */
	boolean setVirtualBlockSize(int size)
	{
		boolean error;

		if ((size+MAX_ENCRYPTION_BYTES<=blockSize) || (size<1))
		{
			virtualBlockSize	=size+MAX_ENCRYPTION_BYTES;
			error=false;
		}
		else
		{
			error=true;
		}
		return error;
	}




	/**
	 *  Resets the block
	 */
	public void  reset()
	{
		int i;
		
		// Reset block to 0
		i=0;
		while (i<block.length)
		{
			block[i]=0;
			i++;
		}
		// Reset block pointer
		blockPointer=MAX_ENCRYPTION_BYTES;
	}



	/**
	 * Returns a reference to the block
	 * @return The reference
	 */
	byte[] getDataBlock()
	{
		return block;
	}



	/**
	 * Returns the block size
	 * @return The block size in bytes
	 */
	int getDataBlockSize()
	{
		return virtualBlockSize;
	}

	/********************************************************************\
	*
	\********************************************************************/
	boolean  importBlockData(byte[] data, int dataLength)
	{
		boolean error;

		if (dataLength<=blockSize)
		{
			System.arraycopy(data, 0, block, 0, dataLength);
			virtualBlockSize=dataLength;
			error=false;
		}
		else
		{
			error=true;
		}
		return error;
	}


	/********************************************************************\
	*
	\********************************************************************/
	boolean  addData(byte theByte)
	{
		boolean error;
		
		error=false;
		if (blockPointer<blockSize)
		{
		    block[blockPointer]=theByte;
		    blockPointer++;
		}
		else
		{
		    error=true;
		}
		return error;
	}


	/********************************************************************\
	*
	\********************************************************************/
	boolean  addData(int integer)
	{
		ByteBuffer 	buf;
		byte[] 		data;
		
		buf=ByteBuffer.allocate(Integer.SIZE/BITS_PER_BYTE);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		buf.putInt(integer);
		
		data=buf.array();

		
		return addData(data, Integer.SIZE/BITS_PER_BYTE);
	}


	/********************************************************************\
	*
	\********************************************************************/
	boolean  addData(byte[] data, int numberOfBytes)
	{
		boolean error;

		if (blockPointer+numberOfBytes<=blockSize)
		{
                        // Copy the data. If length of the data is smaller than numberOfBytes
                        // copy the available data. Rest will be undefined 
			System.arraycopy(data, 0, block, blockPointer, Math.min(numberOfBytes, data.length));
			blockPointer+=numberOfBytes;
			error=false;
		}
		else
		{
			error=true;
		}
		return error;
	}


	/**
	 * Reads the next byte from the block
	 * @return The byte read
	 */
	byte getDataByte()
	{
		byte[] theBytes;
		
		theBytes=getData(Byte.SIZE/BITS_PER_BYTE);
		return theBytes[0];
	}


	/**
	 * Read an int from the block
	 * @return The int
	 */
	int getDataInt()
	{
		byte[] 		theBytes;
		ByteBuffer 	buf;
		
		theBytes=getData(Integer.SIZE/BITS_PER_BYTE);

		buf=ByteBuffer.wrap(theBytes);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		return buf.getInt();

	}



	/**
	 * This method reads the indicated number of bytes from the block
	 * and increases the pointer
	 * @param numberOfBytes Number of bytes to read
	 * @return The bytes or null if you try to read beyond the end of the block
	 */
	byte[]  getData(int numberOfBytes)
	{
		byte[] theBytes;
		
		if (blockPointer+numberOfBytes<=blockSize)
		{
			theBytes=new byte[numberOfBytes];
			System.arraycopy(block, blockPointer, theBytes, 0, numberOfBytes);
			blockPointer+=numberOfBytes;
		}
		else
		{
			theBytes=null;
		}
		return theBytes;
	}




	/**
	 * This method returns a random integer between and inclusive 
	 * minValue and maxValue
	 * @param minValue The minimum value the random int can assume
	 * @param maxValue The maximum value the random int can assume
	 * @return
	 */
	int randomInt(int minValue, int maxValue)
	{
		int randomInt;

//		randomInt=(rand()%(maxValue-minValue+1))+minValue;
		randomInt=(int)((Math.random()*(maxValue-minValue+1))+minValue);

		return randomInt;
	}




	/**
	 * This method obfuscate the block of bytes 
	 */
	void  obfuscateData()
	{
	    int  subKeyStart1;
	    int  subKeyLength1;
	    int  subKeyStart2;
	    int  subKeyLength2;
	    int  shift;
	    int  i;
	    int  subKeyIndex1;
	    int  subKeyIndex2;
	    int  value;
	    byte checksum;
	        
	    checksum='J';
	    subKeyLength1=randomInt(MINSUBKEYLENGTH, MAXSUBKEYLENGTH);
	    subKeyStart1 =randomInt(0, encryptionKey1.length-subKeyLength1); 
	    subKeyLength2=randomInt(MINSUBKEYLENGTH, MAXSUBKEYLENGTH);
	    subKeyStart2 =randomInt(0, encryptionKey2.length-subKeyLength2); 
		shift		 = randomInt(0,7);

	    block[0]=(byte)subKeyLength2;
	    block[1]=(byte)shift;
	    block[2]=(byte)subKeyStart1;
	    block[3]=(byte)subKeyLength1;
	    block[4]=(byte)subKeyStart2;


	    i=MAX_ENCRYPTION_BYTES;
	    subKeyIndex1=subKeyStart1;
	    subKeyIndex2=subKeyStart2;
	    while (i<virtualBlockSize)
		{
	        value=block[i];
			value&=0xff;
	        checksum ^= block[i];
	        value=(value<<shift);
	        value=value | (value>>8);
	        value=value ^ encryptionKey1[subKeyIndex1];
	        value=value ^ encryptionKey2[subKeyIndex2];
	        value=value & 0xff;
	        block[i]=(byte)value;

	        subKeyIndex1++;
	        if (subKeyIndex1>=subKeyStart1+subKeyLength1)
	        {
	            subKeyIndex1=subKeyStart1;
	        }
	        subKeyIndex2++;
	        if (subKeyIndex2>=subKeyStart2+subKeyLength2)
	        {
	            subKeyIndex2=subKeyStart2;
	        }
	        i++;
	    }
	    block[5]=checksum;

	}


	/**
	 * This method deobfuscates the block of bytes
	 * @return True if the checksum did not match, false if all went ok
	 */
	boolean   deobfuscateData()
	{
	    int  subKeyStart1;
	    int  subKeyLength1;
	    int  subKeyStart2;
	    int  subKeyLength2;
	    int  shift;
	    int  i;
	    int  subKeyIndex1;
	    int  subKeyIndex2;
	    int  value;
	    boolean error;
	    char checksum;

	    checksum='J';
	    subKeyLength2   =block[0];
	    shift           =block[1];
	    subKeyStart1    =block[2];
	    subKeyLength1   =block[3];
	    subKeyStart2    =block[4];

	    i=MAX_ENCRYPTION_BYTES;
	    subKeyIndex1=subKeyStart1;
	    subKeyIndex2=subKeyStart2;
	    while (i<virtualBlockSize)
	    {
	        value=block[i];
			value&=0xff;

	        value=value ^ encryptionKey2[subKeyIndex2];
	        value=value ^ encryptionKey1[subKeyIndex1];
	        value=value & 0xff;
	        value=value<<8;
	        value=value>>shift;
	        value=value | (value>>8);
	        block[i]=(byte)value;
	        checksum ^= block[i];

	        subKeyIndex1++;
	        if (subKeyIndex1>=subKeyStart1+subKeyLength1)
	        {
	            subKeyIndex1=subKeyStart1;
	        }
	        subKeyIndex2++;
	        if (subKeyIndex2>=subKeyStart2+subKeyLength2)
	        {
	            subKeyIndex2=subKeyStart2;
	        }
	        i++;
	    }

	    if (checksum==block[5])
	    {
	        error=false;   // data ok
	    }
	    else
	    {
	        error=true;   // messed with the data
	    }
	    return error;

	}
	
	
	
}
