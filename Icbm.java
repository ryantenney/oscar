import java.io.*;

public class Icbm {

    public static final byte OUTGOING = 0x0006;
    public static final byte INCOMING = 0x0007;

    private long id;
    private int channel;
    private byte type;
    private byte[] data;

    public Icbm( long id, int channel, byte[] data ) {
        this.id = id;
        this.channel = channel;
        this.data = data;
    }

    public Icbm( SnacCommand snac ) {
        if ( snac.getFamily() == 0x0004 ) {
            if ( snac.getSubtype() == 0x0006 || snac.getSubtype() == 0x0007 ) {
                byte[] snacData = snac.getData();
                int len = snacData.length - 10;

                this.id = IcbmId.parse( snacData, 0 );
                this.channel = UnsignedShort.parse( snacData, 8 );
                System.arraycopy( snacData, 10, this.data, 0, len );
            }
        }
    }

    public byte[] getBytes() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            out.write( IcbmId.generateBytes() );
            out.write( UnsignedShort.parse( 1 ) );
            out.write( this.data );

            return out.toByteArray();
        } catch ( IOException ioex ) {
            return new byte[0];
        }
    }
}
