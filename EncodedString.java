import java.io.*;

public class EncodedString {

    public static final int ASCII = 0;
    public static final int UTF16 = 2;
    public static final int ISO8859 = 3;

    public static final String[] charsets = new String[] {
             "US-ASCII", null, "UTF-16BE", "ISO-8859-1" };

    public static String parse( byte[] data ) {
        int encoding = UnsignedShort.parse( data, 0 );
//      int subtype = UnsignedShort.parse( data, 2 );
        try {
            return new String( data, 4, data.length - 4, charsets[ encoding ] );
        } catch ( UnsupportedEncodingException ueex ) {
            return new String( "" );
        }
    }

    public static byte[] parse( int charset, String data ) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write( UnsignedShort.parse( charset ) );
            out.write( UnsignedShort.parse( 0 ) );
            out.write( data.getBytes( charsets[ charset ] ) );
            return out.toByteArray();
        } catch ( IOException ioex ) {
            return new byte[ 0 ];
        }
    }

}
