/*
 * $Id: Kernel.java,v 1.23 2001/10/08 01:15:46 rayo Exp $
 * 456789012345678901234567890123456789012345678901234567890123456789012
 */

import java.util.StringTokenizer ;
import java.util.Properties ;
import java.io.FileInputStream ;
import java.io.IOException ;
import java.io.FileNotFoundException ;

/*
 * $Log: Kernel.java,v $
 * Revision 1.23  2001/10/08 01:15:46  rayo
 * modified directory entry to be more like dirent
 *
 * Revision 1.22  2001/10/07 23:48:55  rayo
 * added author javadoc tag
 *
 */

/**
 * Simulates a unix-like file system.  Provides basic directory
 * and file operations and implements them in terms of the underlying
 * disk block structures.
 * @author Ray Ontko
 */
public class Kernel
{

  /**
   * The name this program uses when displaying any error messages 
   * it generates internally.
   */
  public static final String PROGRAM_NAME = "Kernel" ;

  /* Errors */

  /**
   * Not owner.
   */
  public static final int EPERM = 1 ;

  /**
   * No such file or directory.
   */
  public static final int ENOENT = 2 ;

  /**
   * Bad file number.
   */
  public static final int EBADF = 9 ;

  /**
   * Permission denied.
   */
  public static final int EACCES = 13 ;

  /**
   * File exists.
   */
  public static final int EEXIST = 17 ;

  /**
   * Cross-device link.
   */
  public static final int EXDEV = 18 ;

  /**
   * Not a directory.
   */
  public static final int ENOTDIR = 20 ;

  /**
   * Is a directory.
   */
  public static final int EISDIR = 21 ;

  /**
   * Invalid argument.
   */
  public static final int EINVAL = 22 ;

  /**
   * File table overflow.
   */
  public static final int ENFILE = 23 ;

  /**
   * Too many open files.
   */
  public static final int EMFILE = 24 ;

  /**
   * File too large.
   */
  public static final int EFBIG = 27 ;

  /**
   * No space left on device.
   */
  public static final int ENOSPC = 28 ;

  /**
   * Read-only file system.
   */
  public static final int EROFS = 30 ;

  /**
   * Too many links.
   */
  public static final int EMLINK = 31 ;

  /**
   * Number of errors messages defined in sys_errlist
   * <p>
   * Simulates unix system variable:
   * <pre>
   *   int sys_nerr;
   * </pre>
   */
  public static final int sys_nerr = 32 ;

  /**
   * The array of kernel error messages.
   * <p>
   * Simulates unix system variable:
   * <pre>
   *   const char *sys_errlist[];
   * </pre>
   */
  public static final String[] sys_errlist = 
  { 
    null
  , "Not owner"
  , "No such file or directory"
  , null
  , null
  , null
  , null
  , null
  , null
  , "Bad file number"
  , null
  , null
  , null
  , "Permission denied"
  , null
  , null
  , null
  , "File exists"
  , "Cross-device link"
  , null
  , "Not a directory"
  , "Is a directory"
  , "Invalid argument"
  , "File table overflow"
  , "Too many open files"
  , null
  , null
  , "File too large"
  , "No space left on device"
  , null
  , "Read-only file system"
  , "Too many links"
  } ;

  /**
   * Prints a system error message.  The actual text written
   * to stderr is the
   * given string, followed by a colon, a space, the message
   * text, and a newline.  It is customary to give the name of
   * the program as the argument to perror.
   * @param s the program name
   */
  public static void perror( String s )
  {
    String message = null ;
    if ( ( process.errno > 0 ) && ( process.errno < sys_nerr ) )
      message = sys_errlist[process.errno] ;
    if ( message == null )
      System.err.println( s + ": unknown errno " + process.errno ) ;
    else
      System.err.println( s + ": " + message ) ;
  }

  /**
   * Set the value of errno for the current process.
   * <p>
   * Simulates the unix variable:
   * <pre>
   *   extern int errno ;
   * </pre>
   * @see getErrno
   */
  public static void setErrno( int newErrno )
  {
    if( process == null )
    {
      System.err.println( PROGRAM_NAME + 
        ": no current process in setErrno()" ) ;
      System.exit( EXIT_FAILURE ) ;
    }
    process.errno = newErrno ;
  }

  /**
   * Get the value of errno for the current process.
   * <p>
   * Simulates the unix variable:
   * <pre>
   *   extern int errno ;
   * </pre>
   * @see setErrno
   */
  public static int getErrno()
  {
    if( process == null )
    {
      System.err.println( PROGRAM_NAME + 
        ": no current process in getErrno()" ) ;
      System.exit( EXIT_FAILURE ) ;
    }
    return process.errno ;
  }

  /* Modes */

  /**
   * File type mask
   */
  public static final short S_IFMT = (short)0170000 ;

  /**
   * Regular file
   */
  public static final short S_IFREG = (short)0100000 ;

  /**
   * Multiplexed block special
   */
  public static final short S_IFMPB = 070000 ;

  /**
   * Block Special
   */
  public static final short S_IFBLK = 060000 ;

  /**
   * Directory
   */
  public static final short S_IFDIR = 040000 ;

  /**
   * Multiplexed character special
   */
  public static final short S_IFMPC = 030000 ;

  /**
   * Character special
   */
  public static final short S_IFCHR = 020000 ;

  /**
   * Set user id on execution
   */
  public static final short S_ISUID = 04000 ;

  /**
   * Set group id on execution
   */
  public static final short S_ISGID = 02000 ;

  /**
   * Save swapped text even after use
   */
  public static final short S_ISVTX = 01000 ;

  /**
   * User (file owner) has read, write and execute permission
   */
  public static final short S_IRWXU = 0700 ;

  /**
   * User has read permission
   */
  public static final short S_IRUSR = 0400 ;

  /**
   * User has read permission
   */
  public static final short S_IREAD = 0400 ;

  /**
   * User has write permission
   */
  public static final short S_IWUSR = 0200 ;

  /**
   * User has write permission
   */
  public static final short S_IWRITE = 0200 ;

  /**
   * User has execute permission
   */
  public static final short S_IXUSR = 0100 ;

  /**
   * User has execute permission
   */
  public static final short S_IEXEC = 0100 ;

  /**
   * Group has read, write and execute permission
   */
  public static final short S_IRWXG = 070 ;

  /**
   * Group has read permission
   */
  public static final short S_IRGRP = 040 ;

  /**
   * Group has write permission
   */
  public static final short S_IWGRP = 020 ;

  /**
   * Group has execute permission
   */
  public static final short S_IXGRP = 010 ;

  /**
   * Others have read, write and execute permission
   */
  public static final short S_IRWXO = 07 ;

  /**
   * Others have read permission
   */
  public static final short S_IROTH = 04 ;

  /**
   * Others have write permisson
   */
  public static final short S_IWOTH = 02 ;

  /**
   * Others have execute permission
   */
  public static final short S_IXOTH = 01 ;

  /**
   * Closes the specified file descriptor.
   * <p>
   * Simulates the unix system call:
   * <pre>
   *   int close(int fd);
   * </pre>
   * @param fd the file descriptor of the file to close
   * @return Zero if the file is closed; -1 if the file descriptor 
   * is invalid.
   */
  public static int close(int fd)
  {
    // check fd
    int status = check_fd( fd ) ;
    if( status < 0 )
      return status ;

    // remove the file descriptor from the kernel's list of open files
    for( int i = 0 ; i < MAX_OPEN_FILES ; i ++ )
      if( openFiles[i] == process.openFiles[fd] )
      {
        openFiles[i] = null ;
        break ;
      }
   // ??? is it an error if we didn't find the open file?

    // remove the file descriptor from the list.
    process.openFiles[fd] = null ;
    return 0 ;
  }

  /**
   * Creates a file or directory with the specified mode.  
   * <p>
   * Creates a new file or prepares to rewrite an existing file.
   * If the file does not exist, it is given the mode specified.
   * If the file does exist, it is truncated to length zero.
   * The file is opened for writing and its file descriptor is 
   * returned.
   * <p>
   * Simulates the unix system call:
   * <pre>
   *   int creat(const char *pathname, mode_t mode);
   * </pre>
   * @param pathname the name of the file or directory to create
   * @param mode the file or directory protection mode for the new file
   * @return the file descriptor (a non-negative integer); -1 if 
   * a needed directory is not searchable, if the file does not 
   * exist and the directory in which it is to be created is not 
   * writable, if the file does exist and is unwritable, if the 
   * file is a directory, or if there are already too many open 
   * files.
   * @exception java.lang.Exception if any underlying action causes
   * an exception to be thrown
   */
  public static int creat( String pathname , short mode )
    throws Exception
  {
    // get the full path
    String fullPath = getFullPath( pathname ) ;

    StringBuffer dirname = new StringBuffer( "/" ) ;
    FileSystem fileSystem = openFileSystems[ROOT_FILE_SYSTEM] ;
    IndexNode currIndexNode = getRootIndexNode() ;
    IndexNode prevIndexNode = null ;
    short indexNodeNumber = FileSystem.ROOT_INDEX_NODE_NUMBER ;

    StringTokenizer st = new StringTokenizer( fullPath , "/" ) ;
    String name = "." ; // start at root node
    while( st.hasMoreTokens() )
    {
      name = st.nextToken() ;
      if ( ! name.equals("") )
      {
        // check to see if the current node is a directory
        if( ( currIndexNode.getMode() & S_IFMT ) != S_IFDIR )
        {
          // return (ENOTDIR) if a needed directory is not a directory
          process.errno = ENOTDIR ;
          return -1 ;
        }

        // check to see if it is readable by the user
        // ??? tbd
        // return (EACCES) if a needed directory is not readable

        if( st.hasMoreTokens() )
        {
          dirname.append( name ) ;
          dirname.append( '/' ) ;
        }

        // get the next inode corresponding to the token
        prevIndexNode = currIndexNode ;
        currIndexNode = new IndexNode() ;
        indexNodeNumber = findNextIndexNode(
          fileSystem , prevIndexNode , name , currIndexNode ) ;
      }
    }

    // ??? we need to set some fields in the file descriptor
    int flags = O_WRONLY ; // ???
    FileDescriptor fileDescriptor = null ;

    if ( indexNodeNumber < 0 )
    {
      // file does not exist.  We check to see if we can create it.

      // check to see if the prevIndexNode (a directory) is writeable
      // ??? tbd
      // return (EACCES) if the file does not exist and the directory
      // in which it is to be created is not writable

      currIndexNode.setMode( mode ) ;
      currIndexNode.setNlink( (short)1 ) ;

      // allocate the next available inode from the file system
      short newInode = fileSystem.allocateIndexNode() ;
      if( newInode == -1 )
        return -1 ;

      fileDescriptor = 
        new FileDescriptor( fileSystem , currIndexNode , flags ) ;
      // assign inode for the new file
      fileDescriptor.setIndexNodeNumber( newInode ) ;

// System.out.println( "newInode = " + newInode ) ;
      fileSystem.writeIndexNode( currIndexNode , newInode ) ;

      // open the directory
      // ??? it would be nice if we had an "open" that took an inode 
      // instead of a name for the dir
// System.out.println( "dirname = " + dirname.toString() ) ;
      int dir = open( dirname.toString() , O_RDWR ) ;
      if( dir < 0 )
      {
        Kernel.perror( PROGRAM_NAME ) ;
        System.err.println( PROGRAM_NAME + 
          ": unable to open directory for writing" );
        Kernel.exit( 1 ) ; // ??? is this correct
      }

      // scan past the directory entries less than the current entry
      // and insert the new element immediately following
      int status ;
      DirectoryEntry newDirectoryEntry = 
        new DirectoryEntry( newInode , name ) ;
      DirectoryEntry currentDirectoryEntry = new DirectoryEntry() ;
      while( true )
      {
        // read an entry from the directory
        status = readdir( dir , currentDirectoryEntry ) ;
        if( status < 0 )
        {
          System.err.println( PROGRAM_NAME + 
            ": error reading directory in creat" ) ;
          System.exit( EXIT_FAILURE ) ;
        }
        else if( status == 0 )
        {
          // if no entry read, write the new item at the current 
          // location and break
          writedir( dir , newDirectoryEntry ) ;
          break ;
        }
        else
        {
          // if current item > new item, write the new item in 
          // place of the old one and break
          if( currentDirectoryEntry.getName().compareTo( 
            newDirectoryEntry.getName() ) > 0 )
          {
            int seek_status = 
              lseek( dir , - DirectoryEntry.DIRECTORY_ENTRY_SIZE , 1 ) ;
            if( seek_status < 0 )
            {
              System.err.println( PROGRAM_NAME + 
                ": error during seek in creat" ) ;
              System.exit( EXIT_FAILURE ) ;
            }
            writedir( dir , newDirectoryEntry ) ;
            break ;
          }
        }
      }
      // copy the rest of the directory entries out to the file
      while ( status > 0 )
      {
        DirectoryEntry nextDirectoryEntry = new DirectoryEntry() ;
        // read next item
        status = readdir( dir , nextDirectoryEntry ) ;
        if( status > 0 )
        {
          // in its place
          int seek_status = 
            lseek( dir , - DirectoryEntry.DIRECTORY_ENTRY_SIZE , 1 ) ;
          if( seek_status < 0 )
          {
            System.err.println( PROGRAM_NAME + 
              ": error during seek in creat" ) ;
            System.exit( EXIT_FAILURE ) ;
          }
        }
        // write current item
        writedir( dir , currentDirectoryEntry ) ;
        // current item = next item
        currentDirectoryEntry = nextDirectoryEntry ;
      }

      // close the directory
      close( dir ) ;
    }
    else
    {
      // file does exist ( indexNodeNumber >= 0 )

      // if it's a directory, we can't truncate it
      if( ( currIndexNode.getMode() & S_IFMT ) == S_IFDIR )
      {
        // return (EISDIR) if the file is a directory
        process.errno = EISDIR ;
        return -1 ;
      }

      // check to see if the file is writeable by the user
      // ??? tbd
      // return (EACCES) if the file does exist and is unwritable

      // free any blocks currently allocated to the file
      int blockSize = fileSystem.getBlockSize() ;
      int blocks = ( currIndexNode.getSize() + blockSize - 1 ) /
        blockSize ;
      for( int i = 0 ; i < blocks ; i ++ )
      {
        int address = currIndexNode.getBlockAddress( i ) ;
        if( address != FileSystem.NOT_A_BLOCK )
        {
          fileSystem.freeBlock( address ) ;
          currIndexNode.setBlockAddress( i , FileSystem.NOT_A_BLOCK ) ;
        }
      }

      // update the inode to size 0
      currIndexNode.setSize( 0 ) ;

      // write the inode to the file system.
      fileSystem.writeIndexNode( currIndexNode , indexNodeNumber ) ;

      // set up the file descriptor
      fileDescriptor = 
        new FileDescriptor( fileSystem , currIndexNode , flags ) ;
      // assign inode for the new file
      fileDescriptor.setIndexNodeNumber( indexNodeNumber ) ;

    }

    return open( fileDescriptor ) ;
  }

  /**
   * Terminate the current "process".  Any open files will be closed.
   * <p>
   * Simulates the unix system call:
   * <pre>
   *   exit(int status);
   * </pre>
   * <p>
   * Note: If this is the last process to terminate, this method
   * calls finalize().
   * @param status the exit status
   * @exception java.lang.Exception if any underlying 
   * Exception is thrown
   */
  public static void exit( int status )
    throws Exception
  {
    // close anything that might be open for the current process
    for( int i = 0 ; i < process.openFiles.length ; i ++ )
      if( process.openFiles[i] != null )
      {
        close( i ) ;
      }

    // terminate the process
    process = null ;
    processCount -- ;

    // if this is the last process to end, call finalize
    if( processCount <= 0 )
      finalize( status ) ;
  }

  /**
   * Set the current file pointer for a file.
   * The current file position is updated based on the values of
   * offset and whence.  If whence is 0, the new position is 
   * offset bytes from the beginning of the file.  If whence is
   * 1, the new position is the current position plus the value 
   * of offset.  If whence is 2, the new position is the size
   * of the file plus the offset value.  Note that offset may be
   * negative if whence is 1 or 2, as long as the resulting 
   * position is not less than zero.  It is valid to position
   * past the end of the file, but it is not valid to read
   * past the end of the file.
   * <p>
   * Simulates the unix system call:
   * <pre>
   *   lseek( int filedes , int offset , int whence );
   * </pre>
   * @param fd the file descriptor
   * @param offset the offset
   * @param whence 0 = from beginning of file; 1 = from 
   * current position ; 2 = from end of file
   */
  public static int lseek( int fd , int offset , int whence )
  {
    // check fd
    int status = check_fd( fd ) ;
    if( status < 0 )
      return status ;

    FileDescriptor file = process.openFiles[fd] ;

    int newOffset ;
    if( whence == 0 )
      newOffset = offset ;
    else if( whence == 1 )
      newOffset = file.getOffset() + offset ;
    else if ( whence == 2 )
      newOffset = file.getSize() + offset ;
    else
    {
      // bad whence value
      process.errno = EINVAL ;
      return -1 ;
    }

    if( newOffset < 0 )
    {
      // bad offset value
      process.errno = EINVAL ;
      return -1 ;
    }

    file.setOffset( newOffset ) ;
    return newOffset ;
  }

  /*
   * Open flags.
   */

  /**
   * Open with read-only access.
   */
  public static final int O_RDONLY = 0 ;

  /**
   * Open with write-only access.
   */
  public static final int O_WRONLY = 1 ;

  /**
   * Open for read or write access.
   */
  public static final int O_RDWR = 2 ;

  /**
   * Opens a file or directory for reading, writing, or 
   * both reading and writing.
   * <p>
   * The file is positioned at the beginning (byte 0).
   * The returned file descriptor must be used for subsequent
   * calls for other input and output functions on the file.
   * <p>
   * Simulates the unix system call:
   * <pre>
   *   int open(const char *pathname, int flags );
   * </pre>
   * @param pathname the name of the file or directory to create
   * @param flags the flags to use when opening the file: O_RDONLY,
   * O_WRONLY, or O_RDWR.
   * @return the file descriptor (a non-negative integer); -1 if 
   * the file does not exist, if one of the necessary directories 
   * does not exist or is unreadable, if the file is not readable 
   * (resp. writable), or if too many files are open.
   * @exception java.lang.Exception if any underlying action causes
   * an exception to be thrown
   */
  public static int open( String pathname , int flags )
    throws Exception
  {
    // get the full path name
    String fullPath = getFullPath( pathname ) ;

    IndexNode indexNode = new IndexNode() ;
    short indexNodeNumber = findIndexNode( fullPath , indexNode ) ;
    if( indexNodeNumber < 0 )
      return -1 ;

    // ??? return (Exxx) if the file is not readable 
    // and was opened O_RDONLY or O_RDWR

    // ??? return (Exxx) if the file is not writable 
    // and was opened O_WRONLY or O_RDWR

    // set up the file descriptor
    FileDescriptor fileDescriptor = new FileDescriptor( 
      openFileSystems[ ROOT_FILE_SYSTEM ] , indexNode , flags ) ;
    fileDescriptor.setIndexNodeNumber( indexNodeNumber ) ;

    return open( fileDescriptor ) ;
  }

  /**
   * Open a file using a FileDescriptor.  The open and create
   * methods build a file descriptor and then invoke this method
   * to complete the open process.
   * <p>
   * This is a convenience method for the simulator kernel.
   * @param fileDescriptor the file descriptor
   * @return the file descriptor index in the process open file 
   * list assigned to this open file
   */
  private static int open( FileDescriptor fileDescriptor )
  {
    // scan the kernel open file list for a slot 
    // and add our new file descriptor
    int kfd = -1 ;
    for( int i = 0 ; i < MAX_OPEN_FILES ; i ++ )
      if( openFiles[i] == null )
      {
        kfd = i ;
        openFiles[kfd] = fileDescriptor ;
        break ;
      }
    if( kfd == -1 )
    { 
      // return (ENFILE) if there are already too many open files
      process.errno = ENFILE ;
      return -1 ;
    }

    // scan the list of open files for a slot 
    // and add our new file descriptor
    int fd = -1 ;
    for( int i = 0 ; i < ProcessContext.MAX_OPEN_FILES ; i ++ )
      if( process.openFiles[i] == null )
      {
        fd = i ;
        process.openFiles[fd] = fileDescriptor ;
        break ;
      }
    if( fd == -1 )
    {
      // remove the file from the kernel list
      openFiles[kfd] = null ;
      // return (EMFILE) if there isn't room left
      process.errno = EMFILE ;
      return -1 ;
    }

    // return the index of the file descriptor for now open file
    return fd ;
  }

  /**
   * Read bytes from a file.
   * <p>
   * Simulates the unix system call:
   * <pre>
   *   int read(int fd, void *buf, size_t count);
   * </pre>
   * @param fd the file descriptor of a file open for reading
   * @param buf an array of bytes into which bytes are read
   * @param count the number of bytes to read from the file
   * @return the number of bytes actually read; or -1 if an error occurs.
   * @exception java.lang.Exception if any underlying action causes
   * Exception to be thrown
   */
  public static int read( int fd , byte[] buf , int count )
    throws Exception
  {
    // check fd
    int status = check_fd_for_read( fd ) ;
    if( status < 0 )
      return status ;

    FileDescriptor file = process.openFiles[fd] ;
    int offset = file.getOffset() ;
    int size = file.getSize() ;
    int blockSize = file.getBlockSize() ;
    byte[] bytes = file.getBytes() ;
    int readCount = 0 ;
    for( int i = 0 ; i < count ; i ++ )
    {
      // if we read to the end of the file, stop reading
      if( offset >= size )
        break ;
      // if this is the first time through the loop,
      // or if we're at the beginning of a block, load the data block
      if( ( i == 0 ) || ( ( offset % blockSize ) == 0 ) )
      {
        status = file.readBlock( (short)( offset / blockSize ) ) ;
        if( status < 0 )
          return status ;
      }
      // copy a byte from the file buffer to the read buffer
      buf[i] = bytes[ offset % blockSize ] ;
      offset ++ ;
      readCount ++ ;
    }
    // update the offset
    file.setOffset( offset ) ;

    // return the count of bytes read
    return readCount ;
  }

  /**
   * Reads a directory entry from a file descriptor for an open directory.
   * <p>
   * Simulates the unix system call:
   * <pre>
   *   int readdir(unsigned int fd, struct dirent *dirp ) ;
   * </pre>
   * Note that count is ignored in the unix call.
   * @param fd the file descriptor for the directory being read
   * @param dirp the directory entry into which data should be copied
   * @return number of bytes read; 0 if end of directory; -1 if the file
   * descriptor is invalid, or if the file is not opened for read access.
   * @exception java.lang.Exception if any underlying action causes
   * Exception to be thrown
   */
  public static int readdir( int fd , DirectoryEntry dirp ) 
    throws Exception
  {
    // check fd
    int status = check_fd_for_read( fd ) ;
    if( status < 0 )
      return status ;

    FileDescriptor file = process.openFiles[fd] ;

    // check to see if the file is a directory
    if( ( file.getMode() & S_IFMT ) != S_IFDIR )
    {
      // return (ENOTDIR) if a needed directory is not a directory
      process.errno = ENOTDIR ;
      return -1 ;
    }

    // return 0 if at end of directory
    if( file.getOffset() >= file.getSize() )
      return 0 ;

    // read a block, if needed
    status = file.readBlock( (short)( file.getOffset() / file.getBlockSize() ) ) ;
    if( status < 0 )
      return status ;

    // read bytes from the block into the DirectoryEntry
    dirp.read( file.getBytes() , 
      file.getOffset() % file.getBlockSize() ) ;
    file.setOffset( file.getOffset() + 
      DirectoryEntry.DIRECTORY_ENTRY_SIZE ) ;

    // return the size of a DirectoryEntry
    return DirectoryEntry.DIRECTORY_ENTRY_SIZE ;
  }

  /**
   * Obtain information for an open file.
   * <p>
   * Simulates the unix system call:
   * <pre>
   *   int fstat(int filedes, struct stat *buf);
   * </pre>
   * @exception java.lang.Exception if any underlying action causes
   * Exception to be thrown
   */
  public static int fstat( int fd , Stat buf )
    throws Exception
  {
    // check fd
    int status = check_fd( fd ) ;
    if( status < 0 )
      return status ;

    FileDescriptor fileDescriptor = process.openFiles[fd] ;
    short deviceNumber = fileDescriptor.getDeviceNumber() ;
    short indexNodeNumber = fileDescriptor.getIndexNodeNumber() ;
    IndexNode indexNode = fileDescriptor.getIndexNode() ;

    // copy information to buf
    buf.st_dev = deviceNumber ;
    buf.st_ino = indexNodeNumber ;
    buf.copyIndexNode( indexNode ) ;

    return 0 ;
  }

  /**
   * Obtain information about a named file.
   * <p>
   * Simulates the unix system call:
   * <pre>
   *   int stat(const char *name, struct stat *buf);
   * </pre>
   * @exception java.lang.Exception if any underlying action causes
   * Exception to be thrown
   */
  public static int stat( String name , Stat buf )
    throws Exception
  {
    // a buffer for reading directory entries
    DirectoryEntry directoryEntry = new DirectoryEntry() ;

    // get the full path
    String path = getFullPath( name ) ;

    // find the index node
    IndexNode indexNode = new IndexNode() ;
    short indexNodeNumber = findIndexNode( path , indexNode ) ; 
    if( indexNodeNumber < 0 )
    {
      // return ENOENT
      process.errno = ENOENT ;
      return -1 ;
    }

    // copy information to buf
    buf.st_dev = ROOT_FILE_SYSTEM ;
    buf.st_ino = indexNodeNumber ;
    buf.copyIndexNode( indexNode ) ;

    return 0 ;
  }

  /**
   * First commits inodes to buffers, and then buffers to disk.
   * <p>
   * Simulates unix system call:
   * <pre>
   *   int sync(void);
   * </pre>
   */
  public static void sync()
  {
    // write out superblock if updated
    // write out free list blocks if updated
    // write out inode blocks if updated
    // write out data blocks if updated

    // at present, all changes to inodes, data blocks, 
    // and free list blocks
    // are written as they go, so this method does nothing.
  }

  /**
   * Write bytes to a file.
   * <p>
   * Simulates the unix system call:
   * <pre>
   *   int write(int fd, const void *buf, size_t count);
   * </pre>
   * @exception java.lang.Exception if any underlying action causes
   * Exception to be thrown
   */
  public static int write( int fd , byte[] buf , int count )
    throws Exception
  {
    // check fd
    int status = check_fd_for_write( fd ) ;
    if( status < 0 )
      return status ;

    FileDescriptor file = process.openFiles[fd] ;

    // return (ENOSPC) if the device containing the file system
    // referred to by fd has not room for the data

    int offset = file.getOffset() ;
    int size = file.getSize() ;
    int blockSize = file.getBlockSize() ;
    byte[] bytes = file.getBytes() ;
    int writeCount = 0 ;
    for( int i = 0 ; i < count ; i ++ )
    {
      // if this is the first time through the loop,
      // or if we're at the beginning of a block, 
      // load or allocate a data block
      if( ( i == 0 ) || ( ( offset % blockSize ) == 0 ) )
      {
        status = file.readBlock( (short)( offset / blockSize ) ) ;
        if( status < 0 )
          return status ;
      }
      // copy a byte from the write buffer to the file buffer
      bytes[ offset % blockSize ] = buf[i] ;
      offset ++ ;
      // if we get to the end of a block, write it out
      if( ( offset % blockSize ) == 0 )
      {
        status = 
          file.writeBlock( (short)( ( offset - 1 ) / blockSize ) ) ;
        if( status < 0 )
          return status ;
        // update the file size if it grew
        if( offset > size )
        {
          file.setSize( offset ) ;
          size = offset ;
        }
      }
      writeCount ++ ;
    }

    // write the last block if we wrote anything to it
    if( ( offset % blockSize ) > 0 )
    {
      status = file.writeBlock( (short)( ( offset - 1 ) / blockSize ) ) ;
      if( status < 0 )
        return status ;
    }
    
    // update the file size if it grew
    if( offset > size )
      file.setSize( offset ) ;

    // update the offset
    file.setOffset( offset ) ;

    // return the count of bytes written
    return writeCount ;
  }

  /**
   * Writes a directory entry from a file descriptor for an 
   * open directory.
   * <p>
   * Simulates the unix system call:
   * <pre>
   *   int readdir(unsigned int fd, struct dirent *dirp ) ;
   * </pre>
   * Note that count is ignored in the unix call.
   * @param fd the file descriptor for the directory being read
   * @param dirp the directory entry into which data should be copied
   * @return number of bytes read; 0 if end of directory; -1 if the file
   * descriptor is invalid, or if the file is not opened for read access.
   * @exception java.lang.Exception if any underlying action causes
   * Exception to be thrown
   */
  public static int writedir( int fd , DirectoryEntry dirp ) 
    throws Exception
  {
    // check fd
    int status = check_fd_for_write( fd ) ;
    if( status < 0 )
      return status ;

    FileDescriptor file = process.openFiles[fd] ;

    // check to see if the file is a directory
    if( ( file.getMode() & S_IFMT ) != S_IFDIR )
    {
      // return (ENOTDIR) if a needed directory is not a directory
      process.errno = ENOTDIR ;
      return -1 ;
    }

    short blockSize = file.getBlockSize() ;
    // allocate or read a block
    status = file.readBlock( (short)( file.getOffset() / blockSize ) ) ;
    if( status < 0 )
      return status ;

    // write bytes from the DirectoryEntry into the block
    dirp.write( file.getBytes() , file.getOffset() % blockSize ) ;

    // write the updated block
    status = file.writeBlock( (short)( file.getOffset() / blockSize ) ) ;
    if( status < 0 )
      return status ;

    // update the file size
    file.setOffset( file.getOffset() + 
      DirectoryEntry.DIRECTORY_ENTRY_SIZE ) ;
    if( file.getOffset() > file.getSize() )
      file.setSize( file.getOffset() ) ;

    // return the size of a DirectoryEntry
    return DirectoryEntry.DIRECTORY_ENTRY_SIZE ;
  }

/*
to be done:
       int access(const char *pathname, int mode);
       int link(const char *oldpath, const char *newpath);
       int unlink(const char *pathname);
       int rename(const char *oldpath, const char *newpath);
       int symlink(const char *oldpath, const char *newpath);
       int lstat(const char *file_name, struct stat *buf);
       int chmod(const char *path, mode_t mode);
       int fchmod(int fildes, mode_t mode);
       int chown(const char *path, uid_t owner, gid_t group);
       int fchown(int fd, uid_t owner, gid_t group);
       int lchown(const char *path, uid_t owner, gid_t group);
       int utime(const char *filename, struct utimbuf *buf);
       int readlink(const char *path, char *buf, size_t bufsiz);
       int chdir(const char *path);
       mode_t umask(mode_t mask);
*/

  //TODO Write chown
  public int chown(String path, short uid, short guid) {
      
  }

  /**
   * This is an internal variable for the simulator which always 
   * points to the 
   * current ProcessContext.  If multiple processes are implemented,
   * then this variable will "point" to different processes at
   * different times.
   */
  private static ProcessContext process = null ;

  /**
   * The number of processes.
   */
  private static int processCount = 0 ;

  private static int MAX_OPEN_FILES = 0 ;

  private static FileDescriptor[] openFiles = null ;

  // ??? should be private?
  public static int MAX_OPEN_FILE_SYSTEMS = 1 ;

  // ??? should be private?
  public static FileSystem[] openFileSystems = 
    new FileSystem[MAX_OPEN_FILE_SYSTEMS] ;

  // ??? should be private?
  public static short ROOT_FILE_SYSTEM = 0 ;

  /**
   * Initialize the file simulator kernel.  This should be the
   * first call in any simulation program.  You can think of this
   * as the method which "boots" the kernel.
   * This method opens the "filesys.conf" file (or the file named
   * by the system property "filesys.conf") and reads any properties
   * given in that file, including the filesystem.root.filename and
   * filesystem.root.mode ("r", "rw").
   */
  public static void initialize()
  {
    // check to see if the name of an alternate configuration
    // file has been specified.  This can be done, for example,
    //   java -Dfilesys.conf=myfile.txt program-name parameter ...
    String propertyFileName = System.getProperty( "filesys.conf" ) ;
    if ( propertyFileName == null )
      propertyFileName = "filesys.conf" ;
    Properties properties = new Properties() ;
    try
    {
      FileInputStream in = new FileInputStream( propertyFileName ) ;
      properties.load( in ) ; 
      in.close() ;
    }
    catch( FileNotFoundException e )
    {
      System.err.println( PROGRAM_NAME + ": error opening properties file" ) ;
      System.exit( EXIT_FAILURE ) ;
    }
    catch( IOException e )
    {
      System.err.println( PROGRAM_NAME + ": error reading properties file" ) ;
      System.exit( EXIT_FAILURE ) ;
    }

    // get the root file system properties
    String rootFileSystemFilename = 
      properties.getProperty( "filesystem.root.filename" , "filesys.dat" ) ;
    String rootFileSystemMode = 
      properties.getProperty( "filesystem.root.mode" , "rw" ) ;

    // get the current process properties
    short uid = 1 ;
    try
    {
      uid = Short.parseShort( properties.getProperty( "process.uid" , "1" ) ) ;
    }
    catch ( NumberFormatException e )
    {
      System.err.println( PROGRAM_NAME + 
        ": invalid number for property process.uid in configuration file" ) ;
      System.exit( EXIT_FAILURE ) ;
    }

    short gid = 1 ;
    try
    {
    gid = Short.parseShort( properties.getProperty( "process.gid" , "1" ) ) ;
    }
    catch ( NumberFormatException e )
    {
      System.err.println( PROGRAM_NAME + 
        ": invalid number for property process.gid in configuration file" ) ;
      System.exit( EXIT_FAILURE ) ;
    }

    short umask = 0002 ;
    try
    {
      umask = Short.parseShort( 
        properties.getProperty( "process.umask" , "002" ) , 8 ) ;
    }
    catch ( NumberFormatException e )
    {
      System.err.println( PROGRAM_NAME +
        ": invalid number for property process.umask in configuration file" ) ;
      System.exit( EXIT_FAILURE ) ;
    }

    String dir = "/root" ;
    dir = properties.getProperty( "process.dir" , "/root" ) ;

    try
    {
      MAX_OPEN_FILES = Integer.parseInt( properties.getProperty(
        "kernel.max_open_files" , "20" ) ) ;
    }
    catch( NumberFormatException e )
    {
      System.err.println( PROGRAM_NAME + 
        ": invalid number for property kernel.max_open_files in configuration file" ) ;
      System.exit( EXIT_FAILURE );
    }

    try
    {
      ProcessContext.MAX_OPEN_FILES = Integer.parseInt( 
        properties.getProperty( "process.max_open_files" , "10" ) ) ;
    }
    catch( NumberFormatException e )
    {
      System.err.println( PROGRAM_NAME + 
        ": invalid number for property process.max_open_files in configuration file" ) ;
      System.exit( EXIT_FAILURE );
    }

    // create open file array
    openFiles = new FileDescriptor[MAX_OPEN_FILES] ;

    // create the first process
    process = new ProcessContext( uid , gid , dir , umask ) ;
    processCount ++ ;

    // open the root file system
    try
    {
      openFileSystems[ROOT_FILE_SYSTEM] = new FileSystem( 
        rootFileSystemFilename , rootFileSystemMode ) ;
    }
    catch( IOException e )
    {
      System.err.println( PROGRAM_NAME + ": unable to open root file system" ) ;
      System.exit( EXIT_FAILURE ) ;
    }

  }

  /**
   * Failure exit status.
   */
  private static int EXIT_FAILURE = 1 ; 

  /**
   * Success exit status.
   */
  private static int EXIT_SUCCESS = 0 ;

  /**
   * End the simulation and exit.
   * Terminates any remaining "processes", flushes all file system blocks
   * to "disk", and exit the simulation program.  This method is generally
   * called by exit() when the last process terminates.  However,
   * it may also be called directly to gracefully end the simlation.
   * @param status the status to use with System.exit() 
   * @exception java.lang.Exception if any underlying operation
   * causes and exception to be thrown.
   */
  public static void finalize( int status )
    throws Exception
  {
    // exit() any remaining processes
    if( process != null )
      exit( 0 ) ;

    // flush file system blocks
    sync() ;

    // close the root file system
    openFileSystems[ROOT_FILE_SYSTEM].close() ;

    // terminate the program
    System.exit( status ) ;
  }

/*
Some internal methods.
*/

  /**
   * Check to see if the integer given is a valid file descriptor
   * index for the current process.  Sets errno to EBADF if invalid. 
   * <p>
   * This is a convenience method for the simulator kernel;
   * it should not be called by user programs.
   * @param fd the file descriptor index
   * @return zero if the file descriptor index is valid; -1 if the file
   * descriptor index is not valid
   */
  private static int check_fd( int fd )
  {
    // look for the file descriptor in the open file list
    if ( fd < 0 || 
         fd >= process.openFiles.length || 
         process.openFiles[fd] == null )
    {
      // return (EBADF) if file descriptor is invalid
      process.errno = EBADF ;
      return -1 ;
    }

    return 0 ;
  }

  /**
   * Check to see if the integer given is a valid file descriptor
   * index for the current process, and if so, whether the file is
   * open for reading.  Sets errno to EBADF if invalid or not open
   * for reading.
   * <p>
   * This is a convenience method for the simulator kernel;
   * it should not be called by user programs.
   * @param fd the file descriptor index
   * @return zero if the file descriptor index is valid; -1 if the file
   * descriptor index is not valid or is not open for reading.
   */
  private static int check_fd_for_read( int fd )
  {
    int status = check_fd( fd ) ;
    if( status < 0 )
      return -1 ;

    FileDescriptor fileDescriptor = process.openFiles[fd] ;
    int flags = fileDescriptor.getFlags() ;
    if( ( flags != O_RDONLY ) && 
        ( flags != O_RDWR ) )
    {
      // return (EBADF) if the file is not open for reading
      process.errno = EBADF ;
      return -1 ;
    }

    return 0 ;
  }

  /**
   * Check to see if the integer given is a valid file descriptor
   * index for the current process, and if so, whether the file is
   * open for writing.  Sets errno to EBADF if invalid or not open
   * for writing.
   * <p>
   * This is a convenience method for the simulator kernel;
   * it should not be called by user programs.
   * @param fd the file descriptor index
   * @return zero if the file descriptor index is valid; -1 if the file
   * descriptor index is not valid or is not open for writing.
   */
  private static int check_fd_for_write( int fd )
  {
    int status = check_fd( fd ) ;
    if( status < 0 )
      return -1 ;

    FileDescriptor fileDescriptor = process.openFiles[fd] ;
    int flags = fileDescriptor.getFlags() ;
    if( ( flags != O_WRONLY ) && 
        ( flags != O_RDWR ) )
    {
      // return (EBADF) if the file is not open for writing
      process.errno = EBADF ;
      return -1 ;
    }

    return 0 ;
  }

  /** 
   * Get the full path for a file by adding
   * the working directory for the current process
   * to the beginning of the given path name
   * if necessary.
   * @param pathname the given path name
   * @return the resulting fully qualified path name
   */
  private static String getFullPath( String pathname )
  {
    String fullPath = null ;

    // make sure the path starts with a slash
    if( pathname.startsWith( "/" ) )
      fullPath = pathname ;
    else
      fullPath = process.getDir() + "/" + pathname ;
    return fullPath ;
  }

  private static IndexNode rootIndexNode = null ;

  private static IndexNode getRootIndexNode()
  {
    if( rootIndexNode == null )
      rootIndexNode = openFileSystems[ROOT_FILE_SYSTEM].getRootIndexNode() ;
    return rootIndexNode ;
  }

  private static short findNextIndexNode( 
    FileSystem fileSystem , IndexNode indexNode , String name , 
    IndexNode nextIndexNode )
    throws Exception
  {
    // if stat isn't a directory give an error
    if( ( indexNode.getMode() & S_IFMT ) != S_IFDIR )
    {
      // return (ENOTDIR) if a needed directory is not a directory
      process.errno = ENOTDIR ;
      return -1 ;
    }

    // if user isn't alowed to read directory, give an error
    // ??? tbd
    // return (EACCES) if a needed directory is not readable

    FileDescriptor fileDescriptor = 
      new FileDescriptor( fileSystem , indexNode , O_RDONLY ) ;
    int fd = open( fileDescriptor ) ;
    if( fd < 0 )
    {
      // process.errno = ???
      return -1 ;
    }

    // create a buffer for reading directory entries
    DirectoryEntry directoryEntry = new DirectoryEntry() ;

    int status = 0 ;
    short indexNodeNumber = -1 ;
    // while there are more directory blocks to be read
    while( true )
    {
      // read a directory entry
      status = readdir( fd , directoryEntry ) ;
      if( status <= 0 )
      {
        // we got to the end of the directory, or 
        // encountered an error, so quit
        break ;
      }
      if( directoryEntry.getName().equals( name ) )
      {
        indexNodeNumber = directoryEntry.getIno() ;
        // read the inode block
        fileSystem.readIndexNode( nextIndexNode , indexNodeNumber ) ;
        // we're done searching
        break ;
      }
    }

    // close the file since we're done with it
    int close_status = close( fd ) ;
    if( close_status < 0 )
    {
      // process.errno = ???
      return -1 ;
    }

    // if we encountered an error reading, return error
    if( status < 0 )
    {
      // process.errno = ???
      return -1 ;
    }

    // if we got to the directory without finding the name, return error
    if( status == 0 )
    {
      process.errno = ENOENT ;
      return -1 ;
    }

    // return index node number if success
    return indexNodeNumber ;
  }

  // get the inode for a file which is expected to exist
  private static short findIndexNode( String path , IndexNode inode )
    throws Exception
  {
    // start with the root file system, root inode
    FileSystem fileSystem = openFileSystems[ ROOT_FILE_SYSTEM ] ;
    IndexNode indexNode = getRootIndexNode( ) ;
    short indexNodeNumber = FileSystem.ROOT_INDEX_NODE_NUMBER ;

    // parse the path until we get to the end
    StringTokenizer st = new StringTokenizer( path , "/" ) ;
    while( st.hasMoreTokens() )
    {
      String s = st.nextToken() ;
      if ( ! s.equals("") )
      {
        // check to see if it is a directory
        if( ( indexNode.getMode() & S_IFMT ) != S_IFDIR )
        {
          // return (ENOTDIR) if a needed directory is not a directory
          process.errno = ENOTDIR ;
          return -1 ;
        }

        // check to see if it is readable by the user
        // ??? tbd
        // return (EACCES) if a needed directory is not readable

        IndexNode nextIndexNode = new IndexNode() ;
        // get the next index node corresponding to the token
        indexNodeNumber = findNextIndexNode( 
          fileSystem , indexNode , s , nextIndexNode ) ;
        if( indexNodeNumber < 0 )
        {
          // return ENOENT
          process.errno = ENOENT ;
          return -1 ;
        }
        indexNode = nextIndexNode ;
      }
    }
    // copy indexNode to inode
    indexNode.copy( inode ) ;
    return indexNodeNumber ;
  }

}

