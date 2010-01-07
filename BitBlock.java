/*
 * $Id: BitBlock.java,v 1.3 2001/10/07 23:48:55 rayo Exp $
 */

/*
 * $Log: BitBlock.java,v $
 * Revision 1.3  2001/10/07 23:48:55  rayo
 * added author javadoc tag
 *
 */

/**
 * @author Ray Ontko
 */
public class BitBlock extends Block
{

  /**
   * Construct a bit block of the specified size in bytes.
   * @param blockSize the size of the block in bytes
   */
  public BitBlock( short blockSize )
  {
    super( blockSize ) ;
  }

  /**
   * Set a specified bit to 1 (true).
   * @param whichBit the bit to set
   */
  public void setBit( int whichBit )
  {
    bytes[whichBit/8] |= (byte)( 1 << ( whichBit%8 ) ) ;
  }

  /**
   * Set a specifed bit to a specified boolean value.
   * @param whichBit the bit to set
   * @param value the value to which the bit should be set
   */
  public void setBit( int whichBit , boolean value )
  {
    if( value )
      setBit( whichBit ) ;
    else
      resetBit( whichBit ) ;
  }

  /**
   * Checks to see if the specified bit of the block is set (1) or 
   * reset (0).
   * @param whichBit the bit to check.
   * @return true if set; false if reset.
   */
  public boolean isBitSet( int whichBit )
  {
    return ( bytes[whichBit/8] & (byte)( 1 << ( whichBit%8 ) ) ) != 0 ;
  }

  /**
   * Sets the specified bit of the block to 0 (false).
   * @param whichBit bit to set to 0 (false).
   */
  public void resetBit( int whichBit )
  {
    bytes[whichBit/8] &= ~ (byte)( 1 << ( whichBit%8 ) ) ;
  }
  
}
