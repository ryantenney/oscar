import java.util.*;

public class UnixDate {

    public static Date parse( byte[] data ) {
        return new Date( UnsignedInt.parse( data, 0 ) * 1000 );
    }

    public static Date parse( byte[] data, int offset ) {
        return new Date( UnsignedInt.parse( data, offset ) * 1000 );
    }

    public static byte[] parse( Date time ) {
        return UnsignedInt.parse( time.getTime() / 1000 );
    }

}
