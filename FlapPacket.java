import java.io.*;
import java.util.*;

public class FlapPacket {

    private int startOffset, endOffset;

    private static int sequence = 0;

    private short channel;
    private int seqnum;
    private int length;

    private byte[] data;

    public FlapPacket( byte[] data, int offset ) {
        try {
            startOffset = offset;
            assert ( UnsignedByte.parse( data, offset ) == 0x2a );
            this.channel = UnsignedByte.parse( data, offset + 1 );
            this.seqnum = UnsignedShort.parse( data, offset + 2 );
            this.length = UnsignedShort.parse( data, offset + 4 );

            this.data = new byte[ length ];
            System.arraycopy( data, offset + 6, this.data, 0, this.length );
            endOffset = offset + this.length + 6;
        } catch ( AssertionError ae ) {
            // NOT A FLAP PACKET
            // throw new NoFlapException();
        }
    }

    public FlapPacket( SnacCommand snac ) {
        this( FlapChannel.SNAC, snac.getBytes() );
    }

    public FlapPacket( byte channel, byte[] data ) {
        this.channel = channel;
        this.seqnum = ++sequence;
        this.length = data.length;
        this.data = data;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public int getEndOffset() {
        return endOffset;
    }

    public short getChannel() {
        return this.channel;
    }

    public int getSeqNum() {
        return this.seqnum;
    }

    public int getLength() {
        return this.data.length;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setChannel( short value ) {
        this.channel = value;
    }

    public void setSeqNum( int value ) {
        this.seqnum = value;
    }

    public SnacCommand getSNAC() throws AssertionError {
        if ( this.channel == FlapChannel.SNAC ) {
            return new SnacCommand( this.data );
        } else {
            throw new AssertionError( "Flap Packet does not contain SNAC" );
        }
    }

    public TlvChain getError() throws AssertionError {
        if ( this.channel == FlapChannel.ERROR ) {
            return new TlvChain( this.data );
        } else {
            throw new AssertionError( "Flap Packet does not contain error data" );
        }
    }

    public static List<FlapPacket> getPackets( byte[] data ) {
        List<FlapPacket> packets = new ArrayList<FlapPacket>();
        try {
            int offset = 0;
            while ( offset < data.length ) {
                FlapPacket thisPacket = new FlapPacket( data, offset );
                offset = thisPacket.getEndOffset();
                packets.add( thisPacket );
            }
        } catch ( AssertionError ae ) { }
        return packets;
    }

}
