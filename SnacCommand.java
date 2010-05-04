import java.io.*;
import java.util.*;

public class SnacCommand {

    private int startOffset, endOffset;

    private int family, subtype;
    private int flag;
    private long reqId;
    private TlvChain prepended;
    private byte[] data;

    private static Random prng = new Random();

    private static Map<Long,SnacCommand> commands
        = new HashMap<Long,SnacCommand>();
    private static Map<Long,SnacCommand> responses
        = new HashMap<Long,SnacCommand>();

    public SnacCommand( byte[] data ) {
        this( data, 0 );
    }

    public SnacCommand( FlapPacket data ) {
        this( data.getData(), 0 );
    }

    public SnacCommand( byte[] data, int offset ) {
        startOffset = offset;

        this.family = UnsignedShort.parse( data, offset );
        this.subtype = UnsignedShort.parse( data, offset + 2 );
        this.flag = UnsignedShort.parse( data, offset + 4 );
        this.reqId = UnsignedInt.parse( data, offset + 6 );

        offset += 10;

        // extract prepended tlv
        if ( (this.flag & 0x8000) != 0 ) {
            int prependLength = UnsignedShort.parse( data, offset );
            byte[] prepended = new byte[ prependLength ];
            System.arraycopy( data, offset + 2, prepended, 0, prependLength );
            this.prepended = new TlvChain( prepended );
            offset += 2 + prependLength;
        }

        this.data = new byte[ data.length - offset ];
        System.arraycopy( data, offset, this.data, 0, data.length - offset );

        endOffset = offset;
    }

    public SnacCommand( int family, int subtype, byte[] data ) {
        this( family, subtype, 0, data );
    }

    public SnacCommand( int family, int subtype, int flags, byte[] data ) {
        this.family = family;
        this.subtype = subtype;
        this.flag = flags;
//        this.flag1 = (short)(( flags >> 8 ) & 0xFF);
//        this.flag2 = (short)(flags & 0xFF);
        this.reqId = newReqId();
        this.data = data;
    }

    public void sendSnac( FlapConnection flap ) {
        flap.sendPacket( FlapChannel.SNAC, getBytes() );
    }

    public byte[] getBytes() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            out.write( UnsignedShort.parse( family ) );
            out.write( UnsignedShort.parse( subtype ) );
            out.write( UnsignedShort.parse( flag ) );
            out.write( UnsignedInt.parse( reqId ) );

            return out.toByteArray();
        } catch ( IOException ioex ) {
            // shouldnt happen
            return new byte[0];
        }
    }

    public static void sendSnac( FlapConnection flap, SnacCommand snac ) {
        flap.sendPacket( FlapChannel.SNAC, snac.getBytes() );
    }

    public static SnacCommand getSnacResponse( long reqId ) {
        Long req = new Long( reqId );
        return responses.get( req );
    }

    public static void setResponse( byte[] data ) {
        setResponse( data, 0 );
    }

    public static void setResponse( byte[] data, int offset ) {
        setResponse( new SnacCommand( data, offset ) );
    }

    public static void setResponse( SnacCommand response ) {
        if ( response.getReqId() >= 0x80000000 ) {
            // if the reqId is greater than 0x80000000
            // it is not a response SNAC
        }
    }

    public static long newReqId() {
        return prng.nextLong() & 0x7FFFFFFF;
    }

    public int getFamily() {
        return this.family;
    }

    public int getSubtype() {
        return this.subtype;
    }

    public long getFlags() {
//        return ( flag1 << 8 ) | flag2;
        return flag;
    }

    public long getReqId() {
        return this.reqId;
    }

    public TlvChain getPrepended() {
        return this.prepended;
    }

    public byte[] getData() {
        return this.data;
    }
}
