import java.io.*;

public class Concat {

    public static void main( String[] args ) {
        try {
            for ( String file : args ) {
                System.out.write( readFile( file ) );
            }
        } catch ( IOException ioex ) {
            System.err.println( "Error occured" );
        }
    }
    
    public static byte[] readFile( String path ) {
        try {
            File file = new File( path );
            InputStream in = new BufferedInputStream( 
                             new FileInputStream( file ) );
            byte[] data = new byte[ (int)file.length() ];
            in.read( data );
            return data;
        } catch ( IOException ioex ) {
            return new byte[0];
        }
    }
}
