/*
 * $Id: Block.java,v 1.4 2001/10/07 23:48:55 rayo Exp $
 */

import java.io.RandomAccessFile ;
import java.io.IOException ;
import java.io.EOFException ;

/*
 * $Log: Block.java,v $
 * Revision 1.4  2001/10/07 23:48:55  rayo
 * added author javadoc tag
 *
 * Revision 1.3  2001/09/17 01:42:00  rayo
 * rewritten
 *
 * Revision 1.2  2001/08/31 03:01:06  rayo
 * general formatting cleanup
 *
 */

/**
 * An array of bytes.
 * @author Ray Ontko
 */
public class Block 
{

  /**
   * The block size in bytes for this Block.
   */
  private short blockSize = 0 ;

  /**
   * The array of bytes for this block.
   */
  public byte[] bytes = null ;

  /**
   * Construct a block.
   */
  public Block()
  {
    super() ;
  }

  /**
   * Construct a block with a given block size.
   * @param blockSize the block size in bytes
   */
  public Block( short blockSize )
  {
    super() ;
    setBlockSize( blockSize ) ;
  }

  /**
   * Set the block size in bytes for this Block.
   * @param newBlockSize the new block size in bytes
   */
  public void setBlockSize( short newBlockSize )
  {
    blockSize = newBlockSize ;
    bytes = new byte[blockSize] ;
  } 

  /**
   * Get the block size in bytes for this Block.
   * @return the block size in bytes
   */
  public short getBlockSize( )
  {
    return blockSize ;
  }

  /**
   * Read a block from a file at the current position.
   * @param file the random access file from which to read
   * @exception java.io.EOFException if attempt to read past end of file
   * @exception java.io.IOException if an I/O error occurs
   */
  public void read( RandomAccessFile file ) throws IOException , EOFException
  {
    file.readFully( bytes ) ;
  }

  /**
   * Write a block to a file at the current position.
   * @param file the random access file to which to write
   * @exception java.io.IOException if an I/O error occurs
   */
  public void write( RandomAccessFile file ) throws IOException
  {
    file.write( bytes ) ;
  }

}
