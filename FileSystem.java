/*
 * $Id: FileSystem.java,v 1.7 2001/10/07 23:48:55 rayo Exp $
 */

import java.io.RandomAccessFile ;
import java.io.IOException ;

/*
 * $Log: FileSystem.java,v $
 * Revision 1.7  2001/10/07 23:48:55  rayo
 * added author javadoc tag
 *
 * Revision 1.6  2001/09/28 13:18:20  rayo
 * added error codes for no more inodes and no more blocks
 *
 * Revision 1.5  2001/09/28 03:04:42  rayo
 * changed block numbers from short to int to accomodate 3-byte block numbers
 *
 * Revision 1.4  2001/09/25 12:53:15  rayo
 * fixed absolute block number problem.
 *
 * Revision 1.3  2001/09/24 06:16:45  rayo
 * added support for reading and writing of free bits, inodes, and
 * data blocks to support readdir(), writedir(), read(), write()
 * and parts of creat().
 *
 * Revision 1.2  2001/09/22 22:03:31  rayo
 * many changes related to getting readdir() working, and adding FileSystem
 *
 */

/**
 * A simulated file system.
 * @author Ray Ontko
 */
public class FileSystem
{
  private RandomAccessFile file = null ;
  private String filename = null ;
  private String mode = null ;
  private short blockSize = 0 ;
  private int blockCount = 0 ;
  private int freeListBlockOffset = 0 ;
  private int inodeBlockOffset = 0 ;
  private int dataBlockOffset = 0 ;

  private IndexNode rootIndexNode = null ;

  public static short ROOT_INDEX_NODE_NUMBER = 0 ;

  public static int NOT_A_BLOCK = 0x00FFFFFF ;
  
  /**
   * Construct a FileSystem and open a FileSystem file.
   * @param newFilename the name of the FileSystem file to open
   * @param newMode the mode ("r" or "rw") to use when opening the file
   * @exception java.io.IOException if any IOExceptions are thrown 
   * during the open.
   */
  public FileSystem( String newFilename , String newMode ) 
    throws IOException
  {
    super() ;
    filename = newFilename ;
    mode = newMode ;
    open() ;
  }

  /**
   * Get the blockSize for this FileSystem.
   * @return the block size in bytes
   */
  public short getBlockSize()
  {
    return blockSize ;
  }

  public int getFreeListBlockOffset()
  {
    return freeListBlockOffset ;
  }

  public int getInodeBlockOffset()
  {
    return inodeBlockOffset ;
  }

  public int getDataBlockOffset()
  {
    return dataBlockOffset ;
  }

  /**
   * Get the rootIndexNode for this FileSystem.
   * @return the root index node
   */
  public IndexNode getRootIndexNode()
  {
    return rootIndexNode ;
  }

  /**
   * Open a backing file for this FileSystem and read the superblock.
   * @exception java.io.IOException if the open or read causes 
   * IOException to be thrown
   */
  public void open() throws IOException
  {
    file = new RandomAccessFile( filename , mode ) ;

    // read the block size and other information from the superblock
    SuperBlock superBlock = new SuperBlock() ;
    superBlock.read( file ) ;
    blockSize = superBlock.getBlockSize() ;
    blockCount = superBlock.getBlocks() ;
    // ??? inodeCount
    freeListBlockOffset = superBlock.getFreeListBlockOffset() ;
    inodeBlockOffset = superBlock.getInodeBlockOffset() ;
    dataBlockOffset = superBlock.getDataBlockOffset() ;

    // initialize free list block buffer
    freeListBitBlock = new BitBlock(blockSize) ;

    // initialize index block buffer
    indexBlockBytes = new byte[blockSize] ;

    // read the root index node
    rootIndexNode = new IndexNode() ;
    readIndexNode( rootIndexNode , ROOT_INDEX_NODE_NUMBER ) ;
  }

  /**
   * Close the backing file for this FileSystem, if any.
   * @exception java.io.IOException if the closing the backing
   * file causes any IOException to be thrown
   */
  public void close() throws IOException
  {
    if( file != null )
      file.close() ;
  }

  /**
   * Read bytes into a buffer from the specified absolute block number
   * of the file system.
   * @param bytes the byte buffer into which the block should be read
   * @param blockNumber the absolute block number which should be read
   * @exception java.io.IOException if there are any exceptions during 
   * the read from the underlying "file system" file.
   */
  public void read( byte[] bytes , int blockNumber ) throws IOException
  {
    file.seek( blockNumber * blockSize ) ;
    file.readFully( bytes ) ;
  }

  /**
   * Write bytes from a buffer to the specified absolute block number
   * of the file system.
   * @param bytes the byte buffer from which the block should be written
   * @param blockNumber the absolute block number which should be written
   * @exception java.io.IOException if there are any exceptions during
   * the write to the underlying "file system" file.
   */
  public void write( byte[] bytes , int blockNumber ) throws IOException
  {
    file.seek( blockNumber * blockSize ) ;
    file.write( bytes ) ;
  }

  private int currentFreeListBitNumber = 0 ;
  private int currentFreeListBlock = -1 ;  
  private BitBlock freeListBitBlock = null ;

  /**
   * Mark a data block as being free in the free list.
   * @param dataBlockNumber the data block which is to be marked free
   * @exception java.io.IOException if any exception occurs during an
   * operation on the underlying "file system" file.
   */
  public void freeBlock( int dataBlockNumber )
    throws IOException
  {
    loadFreeListBlock( dataBlockNumber ) ;

    freeListBitBlock.resetBit( dataBlockNumber % ( blockSize * 8 ) ) ;

    file.seek( ( freeListBlockOffset + currentFreeListBlock ) *
      blockSize ) ;
    freeListBitBlock.write( file ) ;
  }

  /**
   * Allocate a data block from the list of free blocks.
   * @return the data block number which was allocated; -1 if no blocks 
   * are available
   * @exception java.io.IOException if any exception occurs during an
   * operation on the underlying "file system" file.
   */
  public int allocateBlock()
    throws IOException
  {
    // from our current position in the free list block, 
    // scan until we find an open position.  If we get back to 
    // where we started, there are no free blocks and we return
    // -1.
    int save = currentFreeListBitNumber ;
    while( true )
    {
      loadFreeListBlock( currentFreeListBitNumber ) ;
      boolean allocated = freeListBitBlock.isBitSet( 
        currentFreeListBitNumber % ( blockSize * 8 ) ) ;
      int previousFreeListBitNumber = currentFreeListBitNumber ;
      currentFreeListBitNumber ++ ;
      // if curr bit number >= data block count, set to 0
      if( currentFreeListBitNumber >= ( blockCount - dataBlockOffset ) )
        currentFreeListBitNumber = 0 ;
      if( ! allocated ) 
      {
        freeListBitBlock.setBit( previousFreeListBitNumber % 
          ( blockSize * 8 ) ) ;
        file.seek( ( freeListBlockOffset + currentFreeListBlock ) *
          blockSize ) ;
        freeListBitBlock.write( file ) ;
        return previousFreeListBitNumber ;
      }
      if( save == currentFreeListBitNumber )
      {
        Kernel.setErrno( Kernel.ENOSPC ) ;
        return -1 ; 
      }
    }
  }

  /**
   * Loads the block containing the specified data block bit into
   * the free list block buffer.  This is a convenience method.
   * @param dataBlockNumber the data block number
   * @exception java.io.IOException
   */
  private void loadFreeListBlock( int dataBlockNumber )
    throws IOException
  {
    int neededFreeListBlock = dataBlockNumber / ( blockSize * 8 ) ;

    if( currentFreeListBlock != neededFreeListBlock )
    {
      file.seek( ( freeListBlockOffset + neededFreeListBlock ) * 
        blockSize ) ;
      freeListBitBlock.read( file ) ;
      currentFreeListBlock = neededFreeListBlock ;
    }
  }

  /**
   * The index node number that will next be checked to see
   * if it is available.
   */
  private short currentIndexNodeNumber = 0 ;

  /**
   * The number of the index node block which is currently
   * loaded into indexBlockBytes.  If no block is loaded, 
   * this contains the value "-1".
   */
  private short currentIndexNodeBlock = -1 ;

  /**
   * The byte buffer used for reading and writing 
   * index node blocks.  You can think of this as
   * a one-block cache.
   */
  private byte[] indexBlockBytes = null ;

  /**
   * Allocate an index node for the file system.
   * @return the inode number for the next available index node; 
   * -1 if there are no index nodes available.
   * @exception java.io.IOException if there is an exception during
   * an operation on the underlying "file system" file.
   */
  public short allocateIndexNode() throws IOException
  {
    // from our current position in the index node block list, 
    // scan until we find an open position.  If we get back to 
    // where we started, there are no free inodes and we return
    // -1.
    short save = currentIndexNodeNumber ;
    IndexNode temp = new IndexNode() ;
    while( true )
    {
      readIndexNode( temp , currentIndexNodeNumber ) ;
      short previousIndexNodeNumber = currentIndexNodeNumber ;
      currentIndexNodeNumber ++ ;
      // if curr inode >= avail inode space, set to 0
      if( currentIndexNodeNumber >= 
        ( ( dataBlockOffset - inodeBlockOffset ) *
        ( blockSize / IndexNode.INDEX_NODE_SIZE ) ) ) 
        currentIndexNodeNumber = 0 ;
      if( temp.getNlink() == 0 )
      {
        // ??? should we update nlinks here?
        return previousIndexNodeNumber ;
      }
      if( save == currentIndexNodeNumber )
      {
        // ??? it seems like we should give a different error here
        Kernel.setErrno( Kernel.ENOSPC ) ;
        return -1 ; 
      }
    }
  }

  /**
   * Reads an index node at the index node location specified.
   * @param indexNode the index node
   * @param indexNodeNumber the location
   * @execption java.io.IOException if any exception occurs in an 
   * underlying operation on the "file system" file.
   */
  public void readIndexNode( IndexNode indexNode , short indexNodeNumber ) 
    throws IOException
  {
    loadIndexNodeBlock( indexNodeNumber ) ;

    indexNode.read( indexBlockBytes , 
      ( indexNodeNumber * IndexNode.INDEX_NODE_SIZE ) % 
      blockSize ) ;
  }

  /**
   * Writes an index node at the index node location specified.
   * @param indexNode the index node
   * @param indexNodeNumber the location
   * @execption java.io.IOException if any exception occurs in an 
   * underlying operation on the "file system" file.
   */
  public void writeIndexNode( IndexNode indexNode , short indexNodeNumber )
    throws IOException
  {
    loadIndexNodeBlock( indexNodeNumber ) ;

    indexNode.write( indexBlockBytes , 
      ( indexNodeNumber * IndexNode.INDEX_NODE_SIZE ) % 
      blockSize ) ;

    write( indexBlockBytes , inodeBlockOffset + currentIndexNodeBlock ) ;
  }

  /**
   * Loads the block containing the specified index node into
   * the index node block buffer.  This is a convenience method.
   * @param indexNodeNumber the index node number
   * @exception java.io.IOException
   */
  private void loadIndexNodeBlock( short indexNodeNumber )
    throws IOException
  {
    short neededIndexNodeBlock = (short)( indexNodeNumber / 
      ( blockSize / IndexNode.INDEX_NODE_SIZE ) ) ;

    if( currentIndexNodeBlock != neededIndexNodeBlock )
    {
      read( indexBlockBytes , inodeBlockOffset + neededIndexNodeBlock ) ;
      currentIndexNodeBlock = neededIndexNodeBlock ;
    }
  }
}
