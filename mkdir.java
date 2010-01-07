/*
 * $Id: mkdir.java,v 1.7 2001/10/07 23:23:23 rayo Exp $
 */

/*
 * $Log: mkdir.java,v $
 * Revision 1.7  2001/10/07 23:23:23  rayo
 * added internal documentation, cleaned up javadoc
 *
 * Revision 1.6  2001/09/28 13:20:20  rayo
 * added error handling
 *
 * Revision 1.5  2001/09/28 03:06:17  rayo
 * fixed exit code to eliminate Kernel.EXIT_FAILURE and Kernel.EXIT_SUCCESS
 *
 * Revision 1.4  2001/09/27 03:04:46  rayo
 * added things needed for stat and fstat
 *
 * Revision 1.3  2001/09/26 02:58:29  rayo
 * added partial support for writing . and ..
 *
 * Revision 1.2  2001/09/11 04:02:53  rayo
 * fixed call to initialize
 *
 * Revision 1.1  2001/09/10 20:35:43  rayo
 * Initial revision
 *
 */

/**
 * An mkdir for a simulated file system.
 * <p>
 * Usage:
 * <pre>
 *   java mkdir directory-name ...
 * </pre>
 * @author Ray Ontko
 */
public class mkdir
{

  /**
   * The name of this program.  
   * This is the program name that is used 
   * when displaying error messages.
   */
  public static final String PROGRAM_NAME = "mkdir" ;

  /**
   * Creates the directories given as command line arguments.
   * @exception java.lang.Exception if an exception is thrown
   * by an underlying operation
   */
  public static void main( String[] args ) throws Exception
  {
    // initialize the file system simulator kernel
    Kernel.initialize() ;

    // print a helpful message if no command line arguments are given
    if( args.length < 1 )
    {
      System.err.println( PROGRAM_NAME + ": too few arguments" ) ;
      Kernel.exit( 1 ) ;
    }

    // create a buffer for writing directory entries
    byte[] directoryEntryBuffer = 
      new byte[DirectoryEntry.DIRECTORY_ENTRY_SIZE] ;

    // for each argument given on the command line
    for( int i = 0 ; i < args.length ; i ++ )
    {
      // given the argument a better name
      String name = args[i] ;
      int status = 0 ;

      // call creat() to create the file
      int newDir = Kernel.creat( name , Kernel.S_IFDIR ) ;
      if( newDir < 0 )
      {
        Kernel.perror( PROGRAM_NAME ) ;
        System.err.println( PROGRAM_NAME + ": \"" + name + "\"" ) ;
        Kernel.exit( 2 ) ;
      }

      // get file info for "."
      Stat selfStat = new Stat() ;
      status = Kernel.fstat( newDir , selfStat ) ;
      if( status < 0 )
      {
        Kernel.perror( PROGRAM_NAME ) ;
        Kernel.exit( 3 ) ;
      }

      // add entry for "."
      DirectoryEntry self = new DirectoryEntry( 
        selfStat.getIno() , "." ) ;
      self.write( directoryEntryBuffer , 0 ) ;
      status = Kernel.write( newDir , 
        directoryEntryBuffer , directoryEntryBuffer.length ) ;
      if( status < 0 )
      {
        Kernel.perror( PROGRAM_NAME ) ;
        Kernel.exit( 4 ) ;
      }

      // get file info for ".."
      Stat parentStat = new Stat() ;
      Kernel.stat( name + "/.." , parentStat ) ;

      // add entry for ".."
      DirectoryEntry parent = new DirectoryEntry( 
        parentStat.getIno() , ".." ) ;
      parent.write( directoryEntryBuffer , 0 ) ;
      status = Kernel.write( newDir , 
        directoryEntryBuffer , directoryEntryBuffer.length ) ;
      if( status < 0 )
      {
        Kernel.perror( PROGRAM_NAME ) ;
        Kernel.exit( 5 ) ;
      }

      // call close() to close the file
      status = Kernel.close( newDir ) ;
      if( status < 0 )
      {
        Kernel.perror( PROGRAM_NAME ) ;
        Kernel.exit( 6 ) ;
      }
    }

    // exit with success if we process all the arguments
    Kernel.exit( 0 ) ;
  }

}
