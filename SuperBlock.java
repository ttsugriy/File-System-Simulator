/*
 * $Id: SuperBlock.java,v 1.9 2001/10/07 23:48:55 rayo Exp $
 */

import java.io.RandomAccessFile ;
import java.io.IOException ;
import java.util.*;

/*
 * $Log: SuperBlock.java,v $
 * Revision 1.9  2001/10/07 23:48:55  rayo
 * added author javadoc tag
 *
 * Revision 1.8  2001/09/28 14:07:33  rayo
 * removed import of Node; no longer in use
 *
 * Revision 1.7  2001/09/22 22:03:31  rayo
 * many changes related to getting readdir() working, and adding FileSystem
 *
 * Revision 1.6  2001/09/17 03:18:32  rayo
 * added support for offsets, removed boot block
 *
 * Revision 1.5  2001/09/09 23:12:09  rayo
 * added documentation and write, read
 *
 * Revision 1.4  2001/09/02 20:25:55  rayo
 * cleanup indentation and comments
 *
 * Revision 1.3  2001/08/31 03:01:06  rayo
 * general formatting cleanup
 *
 */

/**
 * @author Ray Ontko
 */
public class SuperBlock 
{

  /**
   * Size of each block in the file system.
   */
  private short blockSize ;

  /**
   * Total number of blocks in the file system.
   */
  private int blocks ;

  /**
   * Offset in blocks of the free list block region from the beginning 
   * of the file system.
   */
  private int freeListBlockOffset ;

  /**
   * Offset in blocks of the inode block region from the beginning 
   * of the file system.
   */
  private int inodeBlockOffset ;

  /**
   * Offset in blocks of the data block region from the beginning 
   * of the file system.
   */
  private int dataBlockOffset ;

  /**
   * Construct a SuperBlock.
   */
  public SuperBlock()
  {
    super();
  }

  public void setBlockSize( short newBlockSize )
  {
    blockSize = newBlockSize ;
  }

  public short getBlockSize()
  {
    return blockSize ;
  }

  public void setBlocks( int newBlocks )
  {
    blocks = newBlocks ;
  }

  public int getBlocks()
  {
    return blocks ;
  }

  /**
   * Set the freeListBlockOffset (in blocks)
   * @param newFreeListBlockOffset the new offset in blocks
   */
  public void setFreeListBlockOffset( int newFreeListBlockOffset )
  {
    freeListBlockOffset = newFreeListBlockOffset ;
  }

  /**
   * Get the free list block offset
   * @return the free list block offset
   */
  public int getFreeListBlockOffset()
  {
    return freeListBlockOffset ;
  }

  /**
   * Set the inodeBlockOffset (in blocks)
   * @param newInodeBlockOffset the new offset in blocks
   */
  public void setInodeBlockOffset( int newInodeBlockOffset )
  {
    inodeBlockOffset = newInodeBlockOffset ;
  }

  /**
   * Get the inode block offset (in blocks)
   * @return inode block offset in blocks
   */
  public int getInodeBlockOffset()
  {
    return inodeBlockOffset ;
  }

  /**
   * Set the dataBlockOffset (in blocks)
   * @param newDataBlockOffset the new offset in blocks
   */
  public void setDataBlockOffset( int newDataBlockOffset )
  {
    dataBlockOffset = newDataBlockOffset ;
  }

  /**
   * Get the dataBlockOffset (in blocks)
   * @return the offset in blocks to the data block region
   */
  public int getDataBlockOffset()
  {
    return dataBlockOffset ;
  }

  /**
   * writes this SuperBlock at the current position of the specified file.
   */
  public void write( RandomAccessFile file ) throws IOException
  {
    file.writeShort( blockSize ) ;
    file.writeInt( blocks ) ;
    file.writeInt( freeListBlockOffset) ;
    file.writeInt( inodeBlockOffset ) ;
    file.writeInt( dataBlockOffset ) ;
    for( int i = 0 ; i < blockSize - 18 ; i ++ )
      file.write( (byte) 0 ) ;
  }

  /**
   * reads this SuperBlock at the current position of the specified file.
   */
  public void read( RandomAccessFile file ) throws IOException
  {
    blockSize = file.readShort() ;
    blocks = file.readInt() ;
    freeListBlockOffset = file.readInt() ;
    inodeBlockOffset = file.readInt() ;
    dataBlockOffset = file.readInt() ;
    file.skipBytes( blockSize - 18 ) ;
  }

}
