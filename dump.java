/*
*
*  dump.java
*
*  mic1 microarchitecture simulator 
*  Copyright (C) 1999, Prentice-Hall, Inc. 
* 
*  This program is free software; you can redistribute it and/or modify 
*  it under the terms of the GNU General Public License as published by 
*  the Free Software Foundation; either version 2 of the License, or 
*  (at your option) any later version. 
* 
*  This program is distributed in the hope that it will be useful, but 
*  WITHOUT ANY WARRANTY; without even the implied warranty of 
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General 
*  Public License for more details. 
* 
*  You should have received a copy of the GNU General Public License along with 
*  this program; if not, write to: 
* 
*    Free Software Foundation, Inc. 
*    59 Temple Place - Suite 330 
*    Boston, MA 02111-1307, USA. 
* 
*  A copy of the GPL is available online the GNU web site: 
* 
*    http://www.gnu.org/copyleft/gpl.html
* 
*/ 

import java.io.* ;
import java.lang.Integer ;

/**
* a simple dump program
* prints the offset, hexvalue, and decimal value for each byte in a
* file, for all files mentioned on the command line.
* <p>
* Usage:
* <pre>
*   java dump <i>input-file</i>
* </pre>
* @author Ray Ontko
*/
public class dump
{

  public static void main( String[] args )
  {
    for ( int i = 0 ; i < args.length ; i ++ )
    {
      // open a file
      try
      {
        FileInputStream ifile = new FileInputStream( args[i] ) ;
        BufferedInputStream in = new BufferedInputStream( ifile ) ;
        // while we are able to read bytes from it
        int c ;
        for ( int j = 0 ; ( c = in.read() ) != -1 ; j ++ )
        {
          if( c > 0 )
          {
            System.out.print( j + " " + Integer.toHexString( c ) + " " + c ) ;
            if( c >= 32 && c < 127 )
              System.out.print( " " + (char)c ) ;
            System.out.println() ;
          }
        }
        in.close() ;
      }
      catch ( FileNotFoundException e ) 
      {
        System.out.println( "error: unable to open input file " + args[i] ) ;
      }
      catch ( IOException e )
      {
        System.out.println( "error: unable to read from file " + args[i] ) ;
      }
    }
  }

}
