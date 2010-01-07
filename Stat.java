/*
 * $Id: Stat.java,v 1.3 2001/10/07 23:48:55 rayo Exp $
 */

/*
 * $Log: Stat.java,v $
 * Revision 1.3  2001/10/07 23:48:55  rayo
 * added author javadoc tag
 *
 * Revision 1.2  2001/09/27 16:56:12  rayo
 * numerous improvements to stat, fstat, etc so that
 * ls and mkdir now seem to be working correctly
 *
 * Revision 1.1  2001/09/27 03:04:22  rayo
 * Initial revision
 *
 */

/**
 * This simulates the unix struct "stat".
 * @author Ray Ontko
 */
public class Stat
{

  public short st_dev = 0 ;

  public short st_ino = 0 ;

  public short st_mode = 0 ;

  public short st_nlink = 0 ;

  public short st_uid = 0 ;

  public short st_gid = 0 ;

  public short st_rdev = 0 ;

  public int st_size = 0 ;

  public int st_atime = 0 ;

  public int st_mtime = 0 ;

  public int st_ctime = 0 ;

    public void setDev( short newDev )
  {
    st_dev = newDev ;
  }

  public short getDev()
  {
    return st_dev ;
  }

  public void setIno( short newIno )
  {
    st_ino = newIno ;
  }

  public short getIno()
  {
    return st_ino ;
  }

  public void setMode( short newMode )
  {
    st_mode = newMode ;
  }

  public short getMode()
  {
    return st_mode ;
  }

  public void setNlink( short newNlink )
  {
    st_nlink = newNlink ;
  }

  public short getNlink()
  {
    return st_nlink ;
  }

  public void setUid( short newUid )
  {
    st_uid = newUid ;
  }

  public short getUid()
  {
    return st_uid ;
  }

  public void setGid( short newGid )
  {
    st_gid = newGid ;
  }

  public short getGid()
  {
    return st_gid ;
  }

  public void setRdev( short newRdev )
  {
    st_rdev = newRdev ;
  }

  public short getRdev()
  {
    return st_rdev ;
  }

  public void setSize( int newSize )
  {
    st_size = newSize ;
  }

  public int getSize()
  {
    return st_size ;
  }

  public void setAtime( int newAtime )
  {
    st_atime = newAtime ;
  }

  public int getAtime()
  {
    return st_atime ;
  }

  public void setMtime( int newMtime )
  {
    st_mtime = newMtime ;
  }

  public int getMtime()
  {
    return st_mtime ;
  }

  public void setCtime( int newCtime )
  {
    st_ctime = newCtime ;
  }

  public int getCtime()
  {
    return st_ctime ;
  }

  public void copyIndexNode( IndexNode indexNode )
  {
    st_mode = indexNode.getMode() ;
    st_nlink = indexNode.getNlink() ;
    st_uid = indexNode.getUid() ;
    st_uid = indexNode.getGid() ;
    st_size = indexNode.getSize() ;
    st_atime = indexNode.getAtime() ;
    st_mtime = indexNode.getMtime() ;
    st_ctime = indexNode.getCtime() ;
  }
}
