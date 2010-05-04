import java.util.*;

public class IcbmId {

    public static long parse( byte[] data ) {
        return parse( data, 0 );
    }

    public static long parse( byte[] data, int offset ) {
        long num = 0;
        for( int i = 0; i < 8; i++ ) {
            final int shift = ( 8 - i - 1 ) * 8;
//            num |= (((long) data[ offset + i ]) & 0xffL) << offset;
        }
        return num;
    }

    public static byte[] parse( long num ) {
        byte[] data = new byte[8];

/*        
        data[0] = (byte) ((num >> 56) & 0xff);
        data[1] = (byte) ((num >> 48) & 0xff);
        data[2] = (byte) ((num >> 40) & 0xff);
        data[3] = (byte) ((num >> 32) & 0xff);
        data[4] = (byte) ((num >> 24) & 0xff);
        data[5] = (byte) ((num >> 16) & 0xff);
        data[6] = (byte) ((num >> 8) & 0xff);
        data[7] = (byte) (num & 0xff);          */

        return data;
    }

    public static float generate() {
        Random prng = new Random();
        byte[] data = new byte[ 8 ];
        float id = 0;

        for( int i = 0; i < 8; i++ ) {
            final int offset = ( 8 - i - 1 ) * 8;
//            id |= data[i] << offset;
        }
        return id;
    }

    public static byte[] generateBytes() {
        Random prng = new Random();
        byte[] data = new byte[ 8 ];
        prng.nextBytes( data );
        return data;
    }

}
