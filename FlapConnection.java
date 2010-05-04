import java.io.*;
import java.net.*;

public class FlapConnection {

    private int seqnum = 0;

    private Socket sock;
    private OutputStream out;
    private InputStream in;

    public FlapConnection( String host, int port ) {
        try {
            sock = new Socket( host, port );
            out = sock.getOutputStream();
            in = sock.getInputStream();
        } catch ( IOException ioex ) {
            
        }
    }

    public FlapConnection( Socket sock ) {
        try {
            this.sock = sock;
            out = sock.getOutputStream();
            in = sock.getInputStream();
        } catch ( IOException ioex ) {
            
        }
    }

    public int sendSnac( int family, int subtype, byte[] data ) {
        return sendSnac( new SnacCommand( family, subtype, data ) );
    }

    public int sendSnac( SnacCommand data ) {
        sendPacket( FlapChannel.SNAC, data.getBytes() );
//temporary
        return 0;
    }

    public void sendPacket( byte channel, byte[] data ) {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();

        try {
            bOut.write( UnsignedByte.parse( (short)0x2a ) );
            bOut.write( UnsignedByte.parse( channel ) );
            bOut.write( UnsignedShort.parse( ++seqnum ) );
            bOut.write( UnsignedShort.parse( data.length ) );
            bOut.write( data );
    
            bOut.writeTo( out );
            bOut.close();
        } catch ( IOException ioex ) {
            // who the hell cares
        }
    }

    public SnacCommand waitForResponse( long reqId ) {
        return null;
    }
}
