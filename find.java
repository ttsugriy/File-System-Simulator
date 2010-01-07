public class find {

    private static String PROGRAM_NAME = "find";
    
    public static void main (String[] args) throws Exception {
        //initialize simulator kernel
        Kernel.initialize();
        if (args.length != 1) {
            Kernel.perror( "USAGE: find <entry_name>" );
            Kernel.exit( 1 );
        }

        traversePath(args[0]);

    }

    public static void traversePath( String name ) throws Exception {
        // stat the name to get information about the file or directory
        Stat stat = new Stat();
        int status = Kernel.stat( name , stat );
        if( status < 0 ) {
            Kernel.perror( PROGRAM_NAME ) ;
            Kernel.exit( 1 ) ;
        }

        // mask the file type from the mode
        short type = (short)( stat.getMode() & Kernel.S_IFMT ) ;

        // if name is a regular file, print the info
        if( type == Kernel.S_IFREG )
            System.out.println( name );            
        // if name is a directory open it and read the contents
        else if( type == Kernel.S_IFDIR ) {
            // open the directory
            int fd = Kernel.open( name , Kernel.O_RDONLY ) ;
            if( fd < 0 ) {
                Kernel.perror( PROGRAM_NAME );
                System.err.println( PROGRAM_NAME + 
                        ": unable to open \"" + name + "\" for reading" );
                Kernel.exit( 1 );
            }

            // print a heading for this directory
            System.out.println();
            System.out.println( name + ":" );

            // create a directory entry structure to hold data as we read
            DirectoryEntry directoryEntry = new DirectoryEntry();
            // while we can read, print the information on each entry
            while( true ) {
                // read an entry; quit loop if error or nothing read
                status = Kernel.readdir( fd , directoryEntry );
                if( status <= 0 ) break;

                // get the name from the entry
                String entryName = directoryEntry.getName();
                String separ = name.equals("/") ? "" : "/";
                String fullName = name + separ + entryName;
                if (!entryName.equals(".") && !entryName.equals("..")) {
                    System.out.println( fullName );
                    traversePath( fullName );
                }
            }

            // close the directory
            Kernel.close( fd ) ;
        }
    }
}
