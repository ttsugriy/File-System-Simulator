/*
 * $Id: FileDescriptor.java,v 1.10 2001/10/07 23:48:55 rayo Exp $
 */

import java.io.IOException ;

/*
 * $Log: FileDescriptor.java,v $
 * Revision 1.10  2001/10/07 23:48:55  rayo
 * added author javadoc tag
 *
 */

/**
 * A file descriptor for an open file in a simulated file system.
 * @author Ray Ontko
 */
public class FileDescriptor
{
  private FileSystem fileSystem = null ;
  private IndexNode indexNode = null ;
  private short deviceNumber = -1 ;
  private short indexNodeNumber = -1 ;
  private int flags = 0 ;
  private int offset = 0 ;
  private byte[] bytes = null ;

  FileDescriptor( short newDeviceNumber , short newIndexNodeNumber , int newFlags )
    throws IOException
  {
    super() ;
    deviceNumber = newDeviceNumber ;
    indexNodeNumber = newIndexNodeNumber ;
    flags = newFlags ;
    fileSystem = Kernel.openFileSystems[ deviceNumber ] ;
    indexNode = new IndexNode() ;
    fileSystem.readIndexNode( indexNode , indexNodeNumber ) ;
    bytes = new byte[fileSystem.getBlockSize()] ;
  }

  FileDescriptor( FileSystem newFileSystem , IndexNode newIndexNode ,
    int newFlags )
  {
    super() ;
    fileSystem = newFileSystem ;
    indexNode = newIndexNode ;
    flags = newFlags ;
    bytes = new byte[fileSystem.getBlockSize()] ;
  }

  public void setDeviceNumber( short newDeviceNumber )
  {
    deviceNumber = newDeviceNumber ;
  }
 
  public short getDeviceNumber()
  {
    return deviceNumber ;
  }

  public IndexNode getIndexNode()
  {
    return indexNode ;
  }

  public void setIndexNodeNumber( short newIndexNodeNumber )
  {
    indexNodeNumber = newIndexNodeNumber ;
  }

  public short getIndexNodeNumber()
  {
    return indexNodeNumber ;
  }

  public int getFlags()
  {
    return flags ;
  }

  public byte[] getBytes()
  {
    return bytes ;
  }

  public short getMode()
  {
    return indexNode.getMode() ;
  }

  public int getSize()
  {
    return indexNode.getSize() ;
  }

  public void setSize( int newSize ) throws IOException
  {
    indexNode.setSize( newSize ) ;

    // write the inode
    fileSystem.writeIndexNode( indexNode , indexNodeNumber ) ;
  }

  public short getBlockSize()
  {
    return fileSystem.getBlockSize() ;
  }

  public int getOffset()
  {
    return offset ;
  }

  public void setOffset( int newOffset )
  {
    offset = newOffset ; 
  }

  public int readBlock( short relativeBlockNumber ) 
    throws Exception
  {
    if( relativeBlockNumber >= IndexNode.MAX_FILE_BLOCKS )
    {
      Kernel.setErrno( Kernel.EFBIG ) ;
      return -1 ;
    }
    // ask the IndexNode for the actual block number 
    // given the relative block number
    int blockOffset = 
      indexNode.getBlockAddress( relativeBlockNumber ) ;

    if( blockOffset == FileSystem.NOT_A_BLOCK )
    {
      // clear the bytes if it's a block that was never written
      int blockSize = fileSystem.getBlockSize() ;
      for( int i = 0 ; i < blockSize ; i ++ )
        bytes[i] = (byte)0 ;
    }
    else
    {
      // read the actual block into bytes
      fileSystem.read( bytes , 
        fileSystem.getDataBlockOffset() + blockOffset ) ;
    }
    return 0 ;
  }

  public int writeBlock( short relativeBlockNumber ) 
    throws Exception
  {
    if( relativeBlockNumber >= IndexNode.MAX_FILE_BLOCKS )
    {
      Kernel.setErrno( Kernel.EFBIG ) ;
      return -1 ;
    }
    // ask the IndexNode for the actual block number 
    // given the relative block number
    int blockOffset = 
      indexNode.getBlockAddress( relativeBlockNumber ) ;

    if( blockOffset == FileSystem.NOT_A_BLOCK )
    {
      // allocate a block; quit if we can't
      blockOffset = fileSystem.allocateBlock() ;
      if( blockOffset < 0 )
        return -1 ;

      // update the inode
      indexNode.setBlockAddress( relativeBlockNumber , blockOffset ) ;
      // write the inode
      fileSystem.writeIndexNode( indexNode , indexNodeNumber ) ;
    }

    // write the actual block from bytes
    fileSystem.write( bytes ,  
      fileSystem.getDataBlockOffset() + blockOffset ) ;

    return 0 ;
  }

}
