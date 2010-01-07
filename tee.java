/*
 * $Id: tee.java,v 1.4 2001/10/07 23:48:55 rayo Exp $
 */

/*
 * $Log: tee.java,v $
 * Revision 1.4  2001/10/07 23:48:55  rayo
 * added author javadoc tag
 *
 * Revision 1.3  2001/10/07 23:29:49  rayo
 * fixed error
 *
 * Revision 1.2  2001/10/07 23:23:23  rayo
 * added internal documentation, cleaned up javadoc
 *
 * Revision 1.1  2001/09/27 21:52:56  rayo
 * Initial revision
 *
 */

/**
 * Reads standard input and writes to standard output 
 * and the named file.
 * A simple tee program for a simulated file system.
 * <p>
 * Usage:
 * <pre>
 *   java tee <i>output-file</i>
 * </pre>
 * @author Ray Ontko
 */
public class tee
{
  /**
   * The name of this program.  
   * This is the program name that is used 
   * when displaying error messages.
   */
  public static final String PROGRAM_NAME = "tee" ;

  /**
   * The size of the buffer to be used for reading from the 
   * file.  A buffer of this size is filled before writing
   * to the output file.
   */
  public static final int BUF_SIZE = 4096 ;

  /**
   * The file mode to use when creating the output file.
   */
  public static final short OUTPUT_MODE = 0700 ;

  /**
   * Copies standard input to standard output and to a file.
   * @exception java.lang.Exception if an exception is thrown
   * by an underlying operation
   */
  public static void main( String[] argv ) throws Exception
  {
    // initialize the file system simulator kernel
    Kernel.initialize() ;

    // print a helpful message if the number of arguments is not correct
    if( argv.length != 1 )
    {
      System.err.println( PROGRAM_NAME + ": usage: java " + PROGRAM_NAME + 
        " output-file" ) ;
      Kernel.exit( 1 ) ;
    }

    // give the command line argument a better name
    String name = argv[0] ;

    // create the output file
    int out_fd = Kernel.creat( name , OUTPUT_MODE ) ;
    if( out_fd < 0 )
    {
      Kernel.perror( PROGRAM_NAME ) ;
      System.err.println( PROGRAM_NAME + ": unable to open output file \"" +
        name + "\"" ) ;
      Kernel.exit( 2 ) ;
    }

    // create a buffer for reading from standard input
    byte[] buffer = new byte[BUF_SIZE] ;

    // while we can, read from standard input
    int rd_count ;
    while( true )
    {
      // read a buffer full of data from standard input
      rd_count = System.in.read( buffer ) ;

      // if we reach the end (-1), quit the loop
      if( rd_count <= 0 )
        break ;

      // write what we read to the output file; if error, exit
      int wr_count = Kernel.write( out_fd , buffer , rd_count ) ;
      if( wr_count <= 0 )
      {
        Kernel.perror( PROGRAM_NAME ) ;
        System.err.println( PROGRAM_NAME +
          ": error during write to output file" ) ;
        Kernel.exit( 3 ) ;
      }

      // write what we read to standard output
      System.out.write( buffer , 0 , rd_count ) ;
    }

    // close the output file
    Kernel.close( out_fd ) ;

    // exit with success
    Kernel.exit( 0 ) ;
  }

}
