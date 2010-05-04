public class AsciiStringBlock {

    public static String parse( byte[] data ) {
        return parse( data, 0 );
    }

    public static String parse( byte[] data, int offset ) {
        short length = UnsignedByte.parse( data, offset );
        byte[] string = new byte[length];

        System.arraycopy( data, offset + 1, string, 0, length );
        return Ascii.parse( string );
    }

    public static byte[] parse( String string ) {
        byte[] sData = Ascii.parse( string );
        byte[] bLen = UnsignedByte.parse( (short)sData.length );
        byte[] rData = new byte[sData.length + 1];

        System.arraycopy( bLen, 0, rData, 0, 1 );
        System.arraycopy( sData, 0, rData, 1, sData.length );

        return rData;
    }

}
