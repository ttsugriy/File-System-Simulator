/*
 * $Id: cp.java,v 1.7 2001/10/07 23:48:55 rayo Exp $
 */

/*
 * $Log: cp.java,v $
 * Revision 1.7  2001/10/07 23:48:55  rayo
 * added author javadoc tag
 *
 * Revision 1.6  2001/10/07 23:23:23  rayo
 * added internal documentation, cleaned up javadoc
 *
 * Revision 1.5  2001/10/06 15:39:10  rayo
 * added comments
 *
 * Revision 1.4  2001/09/27 21:52:56  rayo
 * minor cleanups
 *
 * Revision 1.3  2001/09/26 16:15:29  rayo
 * now throws Exception
 *
 * Revision 1.2  2001/09/17 01:43:49  rayo
 * added error handling
 *
 * Revision 1.1  2001/09/16 12:58:40  rayo
 * Initial revision
 *
 */

/**
 * A simple copy program for a simulated file system.
 * <p>
 * Usage:
 * <pre>
 *   java cp <i>input-file</i> <i>output-file</i>
 * </pre>
 * @author Ray Ontko
 */
public class cp
{
  /**
   * The name of this program.  
   * This is the program name that is used 
   * when displaying error messages.
   */
  public static final String PROGRAM_NAME = "cp" ;

  /**
   * The size of the buffer to be used when reading files.
   */
  public static final int BUF_SIZE = 4096 ;

  /**
   * The file mode to use when creating the output file.
   */
  // ??? perhaps this should be the same mode as the input file
  public static final short OUTPUT_MODE = 0700 ;

  /**
   * Copies an input file to an output file.
   * @exception java.lang.Exception if an exception is thrown by
   * an underlying operation
   */
  public static void main( String[] argv ) throws Exception
  {
    // initialize the file system simulator kernel
    Kernel.initialize() ;

    // make sure we got the correct number of parameters
    if( argv.length != 2 )
    {
      System.err.println( PROGRAM_NAME + ": usage: java " + 
        PROGRAM_NAME + " input-file output-file" ) ;
      Kernel.exit( 1 ) ;
    }

    // give the parameters more meaningful names
    String in_name = argv[0] ;
    String out_name = argv[1] ;

    // open the input file
    int in_fd = Kernel.open( in_name , Kernel.O_RDONLY ) ;
    if( in_fd < 0 )
    {
      Kernel.perror( PROGRAM_NAME ) ;
      System.err.println( PROGRAM_NAME + ": unable to open input file \"" +
        in_name + "\"" ) ;
      Kernel.exit( 2 ) ;
    }

    // open the output file
    int out_fd = Kernel.creat( out_name , OUTPUT_MODE ) ;
    if( out_fd < 0 )
    {
      Kernel.perror( PROGRAM_NAME ) ;
      System.err.println( PROGRAM_NAME + ": unable to open output file \"" +
        argv[1] + "\"" ) ;
      Kernel.exit( 3 ) ;
    }

    // read and write buffers full of data while we can
    int rd_count ;
    byte[] buffer = new byte[BUF_SIZE] ;
    while( true )
    {
      // read a buffer full from the input file
      rd_count = Kernel.read( in_fd , buffer , BUF_SIZE ) ;

      // if error or nothing read, quit the loop
      if( rd_count <= 0 )
        break ;

      // write whatever we read to the output file
      int wr_count = Kernel.write( out_fd , buffer , rd_count ) ;

      // if error or nothing written, give message and exit
      if( wr_count <= 0 )
      {
        Kernel.perror( PROGRAM_NAME ) ;
        System.err.println( PROGRAM_NAME + 
          ": error during write to output file" ) ;
        Kernel.exit( 4 ) ;
      }
    }

    // close the files
    Kernel.close( in_fd ) ;
    Kernel.close( out_fd ) ;

    // check to see if the final read was successful; exit accordingly
    if( rd_count == 0 )
      Kernel.exit( 0 ) ;
    else
    {
      Kernel.perror( PROGRAM_NAME ) ;
      System.err.println( PROGRAM_NAME + 
        ": error during read from input file" ) ;
      Kernel.exit( 5 ) ;
    }
  }

}
