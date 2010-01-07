/*
 * $Id: IndexNode.java,v 1.11 2001/09/28 13:18:58 rayo Exp $
 */

/*
 * $Log: IndexNode.java,v $
 * Revision 1.11  2001/09/28 13:18:58  rayo
 * added support for the maximum number of blocks
 *
 * Revision 1.10  2001/09/28 03:05:27  rayo
 * added copy()
 *
 * Revision 1.9  2001/09/27 16:56:12  rayo
 * numerous improvements to stat, fstat, etc so that
 * ls and mkdir now seem to be working correctly
 *
 * Revision 1.8  2001/09/25 12:53:15  rayo
 * fixed absolute block number problem.
 *
 * Revision 1.7  2001/09/24 06:15:08  rayo
 * added support for nlink, uid, and gid in read and write
 *
 * Revision 1.6  2001/09/18 03:08:24  rayo
 * added set/getNlink
 *
 * Revision 1.5  2001/09/17 01:43:06  rayo
 * rename INODE_SIZE to INDEX_NODE_SOZE
 *
 * Revision 1.4  2001/09/10 21:22:07  rayo
 * changed FileSystem to Kernel
 *
 * Revision 1.3  2001/09/10 20:32:27  rayo
 * added @exception s
 *
 * Revision 1.2  2001/09/10 00:51:40  rayo
 * added documentation
 *
 * Revision 1.1  2001/09/09 23:11:42  rayo
 * Initial revision
 *
 */

/**
 * An index node for a simulated file system.
 * @author Ray Ontko
 */
public class IndexNode
{

  /**
   * Size of each index node in bytes.
   */
  public static final int INDEX_NODE_SIZE = 64 ;

  /**
   * Maximum number of direct blocks in an index node.
   */
  public static final int MAX_DIRECT_BLOCKS = 10 ;

  /**
   * Maximum number of blocks in a file.  If indirect,
   * doubleIndirect, or tripleIndirect blocks are implemented,
   * this number will need to be increased.
   */
  public static final int MAX_FILE_BLOCKS = MAX_DIRECT_BLOCKS ;

  /**
   * Mode for this index node.  This includes file type and file protection
   * information.
   */
  private short mode = 0 ;

  /*
   * Not yet implemented.
   * Number of links to this file.  
   */
  private short nlink  = 0 ;

  /*
   * Not yet implemented.
   * Owner's user id. 
   */
  private short uid = 0 ;

  /*
   * Not yet implemented.
   * Owner's group id.
   */
  private short gid = 0 ;

  /**
   * Number of bytes in this file.
   */
  private int size = 0 ;

  /**
   * Array of direct blocks containing the block addresses for the 
   * first MAX_DIRECT_BLOCKS blocks of the file.  Note that each
   * element in the array is stored as a 3-byte number on disk.
   */
  private int directBlocks[] = 
    { FileSystem.NOT_A_BLOCK 
    , FileSystem.NOT_A_BLOCK 
    , FileSystem.NOT_A_BLOCK 
    , FileSystem.NOT_A_BLOCK 
    , FileSystem.NOT_A_BLOCK 
    , FileSystem.NOT_A_BLOCK 
    , FileSystem.NOT_A_BLOCK 
    , FileSystem.NOT_A_BLOCK 
    , FileSystem.NOT_A_BLOCK 
    , FileSystem.NOT_A_BLOCK } ;

  /*
   * Not yet implemented.
   *
   */
  private int indirectBlock = FileSystem.NOT_A_BLOCK ;

  /*
   * Not yet implemented.
   *
   */
  private int doubleIndirectBlock = FileSystem.NOT_A_BLOCK ;

  /*
   * Not yet implemented.
   *
   */
  private int tripleIndirectBlock = FileSystem.NOT_A_BLOCK ;

  /*
   * Not yet implemented.
   * The date and time at which this file was last accessed.  
   * This is traditionally implemented as the number of seconds 
   * past 1970/01/01 00:00:00
   */
  private int atime = 0 ;

  /*
   * Not yet implemented.
   * The date and time at which this file was last modified.  
   * This is traditionally implemented as the number of seconds 
   * past 1970/01/01 00:00:00
   */
  private int mtime = 0 ;

  /*
   * Not yet implemented.
   * The date and time at which this file was created.  
   * This is traditionally implemented as the number of seconds 
   * past 1970/01/01 00:00:00
   */
  private int ctime = 0 ;

  /**
   * Creates an index node.
   */
  public IndexNode()
  {
    super() ;
  }

  /**
   * Sets the mode for this IndexNode.
   * This is the file type and file protection information.
   */
  public void setMode( short newMode )
  {
    mode = newMode ;
  }

  /**
   * Gets the mode for this IndexNode.
   * This is the file type and file protection information.
   */
  public short getMode()
  {
    return mode ;
  }

  /**
   * Set the number of links for this IndedNode.
   * @param newNlink the number of links
   */
  public void setNlink( short newNlink )
  {
    nlink = newNlink ;
  }

  /**
   * Get the number of links for this IndexNode.
   * @return the number of links
   */
  public short getNlink()
  {
    return nlink ;
  }

  public void setUid( short newUid )
  {
    uid = newUid ;
  }

  public short getUid()
  {
    return uid ;
  }

  public short getGid()
  {
    return gid ;
  }

  public void setGid( short newGid )
  {
    gid = newGid ;
  }

  /**
   * Sets the size for this IndexNode.
   * This is the number of bytes in the file.
   */
  public void setSize( int newSize )
  {
    size = newSize ;
  }

  /**
   * Gets the size for this IndexNode.
   * This is the number of bytes in the file.
   */
  public int getSize()
  {
    return size ;
  }

  /**
   * Gets the address corresponding to the specified 
   * sequential block of the file.
   * @param block the sequential block number
   * @return the address of the block, a number between zero and one
   * less than the number of blocks in the file system
   * @exception java.lang.Exception if the block number is invalid
   */
  public int getBlockAddress( int block ) throws Exception
  {
    if( block >= 0 && block < MAX_DIRECT_BLOCKS )
      return( directBlocks[block] ) ;
    else
      throw new Exception( "invalid block address " + block ) ;
  }

  /**
   * Sets the address corresponding to the specified sequential
   * block of the file.
   * @param block the sequential block number
   * @param address the address of the block, a number between zero and one
   * less than the number of blocks in the file system
   * @exception java.lang.Exception if the block number is invalid
   */
  public void setBlockAddress( int block , int address ) throws Exception
  {
    if( block >= 0 && block < MAX_DIRECT_BLOCKS )
      directBlocks[block] = address ;
    else
      throw new Exception( "invalid block address " + block ) ;
  }

  public void setAtime( int newAtime )
  {
    atime = newAtime ;
  }

  public int getAtime()
  {
    return atime ;
  }

  public void setMtime( int newMtime )
  {
    mtime = newMtime ;
  }

  public int getMtime()
  {
    return mtime ;
  }

  public void setCtime( int newCtime )
  {
    ctime = newCtime ;
  }

  public int getCtime()
  {
    return ctime ;
  }

  /**
   * Writes the contents of an index node to a byte array.
   * This is used to copy the bytes which correspond to the 
   * disk image of the index node onto a block buffer so that
   * they may be written to the file system.
   * @param buffer the buffer to which bytes should be written
   * @param offset the offset from the beginning of the buffer
   * at which bytes should be written
   */
  public void write( byte[] buffer , int offset )
  {
    // write the mode info
    buffer[offset] = (byte)( mode >>> 8 ) ;
    buffer[offset+1] = (byte)mode ;

    // write nlink
    buffer[offset+2] = (byte)( nlink >>> 8 ) ;
    buffer[offset+3] = (byte)nlink ;

    // write uid
    buffer[offset+4] = (byte)( uid >>> 8 ) ;
    buffer[offset+5] = (byte)uid ;

    // write gid
    buffer[offset+6] = (byte)( gid >>> 8 ) ;
    buffer[offset+7] = (byte)gid ;

    // write the size info
    buffer[offset+8]   = (byte)( size >>> 24 ) ;
    buffer[offset+8+1] = (byte)( size >>> 16 ) ;
    buffer[offset+8+2] = (byte)( size >>> 8 ) ;
    buffer[offset+8+3] = (byte)( size ) ;

    // write the directBlocks info 3 bytes at a time
    for( int i = 0 ; i < MAX_DIRECT_BLOCKS ; i ++ )
    {
      buffer[offset+12+3*i]   = (byte)( directBlocks[i] >>> 16 ) ;
      buffer[offset+12+3*i+1] = (byte)( directBlocks[i] >>> 8 ) ;
      buffer[offset+12+3*i+2] = (byte)( directBlocks[i] ) ;
    }

    // leave room for indirectBlock, doubleIndirectBlock, tripleIndirectBlock

    // leave room for atime, mtime, ctime
  }

  /**
   * Reads the contents of an index node from a byte array.
   * This is used to copy the bytes which correspond to the 
   * disk image of the index node from a block buffer that
   * has been read from the file system.
   * @param buffer the buffer from which bytes should be read
   * @param offset the offset from the beginning of the buffer
   * at which bytes should be read
   */
  public void read( byte[] buffer , int offset )
  {
    int b3 ;
    int b2 ;
    int b1 ;
    int b0 ;

    // read the mode info
    b1 = buffer[offset] & 0xff ;
    b0 = buffer[offset+1] & 0xff ;
    mode = (short)( b1 << 8 | b0 ) ; 

    // read the nlink info
    b1 = buffer[offset+2] & 0xff ;
    b0 = buffer[offset+3] & 0xff ;
    nlink = (short)( b1 << 8 | b0 ) ; 

    // read the uid info
    b1 = buffer[offset+4] & 0xff ;
    b0 = buffer[offset+5] & 0xff ;
    uid = (short)( b1 << 8 | b0 ) ; 

    // read the gid info    
    b1 = buffer[offset+6] & 0xff ;
    b0 = buffer[offset+7] & 0xff ;
    gid = (short)( b1 << 8 | b0 ) ; 

    // read the size info
    b3 = buffer[offset+8] & 0xff ;
    b2 = buffer[offset+8+1] & 0xff ;
    b1 = buffer[offset+8+2] & 0xff ;
    b0 = buffer[offset+8+3] & 0xff ;
    size = b3 << 24 | b2 << 16 | b1 << 8 | b0 ; 

    // read the block address info 3 bytes at a time
    for( int i = 0 ; i < MAX_DIRECT_BLOCKS ; i ++ )
    {
      b2 = buffer[offset+12+i*3] & 0xff ;
      b1 = buffer[offset+12+i*3+1] & 0xff ;
      b0 = buffer[offset+12+i*3+2] & 0xff ;
      directBlocks[i] = b2 << 16 | b1 << 8 | b0 ; 
    }

    // leave room for indirectBlock, doubleIndirectBlock, tripleIndirectBlock

    // leave room for atime, mtime, ctime
  }

  /**
   * Converts an index node into a printable string.
   * @return the printable string
   */
  public String toString()
  {
    StringBuffer s = new StringBuffer( "IndexNode[" ) ;
    s.append( mode ) ;
    s.append( ',' ) ;
    s.append( '{' ) ;
    for( int i = 0 ; i < MAX_DIRECT_BLOCKS ; i ++ )
    {
      if( i > 0 )
        s.append( ',' ) ;
      s.append( directBlocks[i] ) ;
    }
    s.append( '}' ) ;
    s.append( ']' ) ;
    return s.toString() ;
  }

  public void copy( IndexNode indexNode )
  {
    indexNode.mode = mode ;
    indexNode.nlink = nlink ;
    indexNode.uid = uid ;
    indexNode.gid = gid ;
    indexNode.size = size ;
    for( int i = 0 ; i < MAX_DIRECT_BLOCKS ; i ++ )
      indexNode.directBlocks[i] = directBlocks[i] ;
    indexNode.indirectBlock = indirectBlock ;
    indexNode.doubleIndirectBlock = doubleIndirectBlock ;
    indexNode.tripleIndirectBlock = tripleIndirectBlock ;
    indexNode.atime = atime ;
    indexNode.mtime = mtime ;
    indexNode.ctime = ctime ;
  }

  /**
   * A test driver for IndexNode.
   * @exception java.lang.Exception any exception which may occur
   */
  public static void main( String[] args ) throws Exception
  {
    byte[] buffer = new byte[512] ;

    IndexNode root = new IndexNode() ;
    root.setMode( Kernel.S_IFDIR ) ;
    root.setBlockAddress( 0 , 33 ) ;
    System.out.println( root.toString() ) ;

    IndexNode copy = new IndexNode() ;
    root.write( buffer , 0 ) ;
    copy.read( buffer , 0 ) ;
    System.out.println( copy.toString() ) ;
  }

}
