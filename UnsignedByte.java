public class UnsignedByte {

    public static short parse( byte[] data, int offset ) {
        return (short)(data[offset] & 0xff);
    }

    public static short parse( byte[] data ) {
        return parse( data, 0 );
    }

    public static byte[] parse( short number ) {
        return new byte[] { (byte)(number & 0xff) };
    }

}
