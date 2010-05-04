AsciiStringBlock.java                                                                               0000644 0012335 0000226 00000001356 10213747570 015412  0                                                                                                    ustar   rwt5629                         ugrad                           0000000 0000000                                                                                                                                                                        public class AsciiStringBlock {

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
                                                                                                                                                                                                                                                                                  Authenticate.java                                                                                   0000644 0012335 0000226 00000002706 10216715123 014626  0                                                                                                    ustar   rwt5629                         ugrad                           0000000 0000000                                                                                                                                                                        import java.io.*;
import java.security.*;

public class Authenticate {

    FlapConnection flap;
    String screenname, password;

    public Authenticate( FlapConnection flap, String screenname, String password ) {
        flap.sendPacket( FlapChannel.LOGIN, new byte[0] );
        this.flap = flap;
        this.screenname = screenname;
        this.password = password;
    }

    public byte[] requestKey() {
        TlvChain chain = new TlvChain();
        chain.addBlock( 0x0001, Ascii.parse( screenname ) );
        chain.addBlock( 0x004b );
        chain.addBlock( 0x005a );

        int reqid = flap.sendSnac( 0x0017, 0x0006, chain.getBytes() );
        flap.waitForResponse( reqid );
//temporary
        return new byte[0];
    }

    public static byte[] getHash( String pass, byte[] key ) {
        byte[] passBytes = new byte[0];
        byte[] aimsmBytes = new byte[0];

        passBytes = Ascii.parse( pass );
        aimsmBytes = Ascii.parse( "AOL Instant Messenger (SM)" );

        MessageDigest md5a, md5b;

        try {
            md5a = MessageDigest.getInstance( "MD5" );
            md5b = MessageDigest.getInstance( "MD5" );

            passBytes = md5a.digest( passBytes );

            md5b.update( key );
            md5b.update( passBytes );
            md5b.update( aimsmBytes );

            return md5b.digest();
        } catch ( NoSuchAlgorithmException nsaex ) {
            // impossible
            return new byte[0];
        }
    }

}
                                                          Concat.java                                                                                         0000600 0012335 0000226 00000001352 10221026343 013376  0                                                                                                    ustar   rwt5629                         ugrad                           0000000 0000000                                                                                                                                                                        import java.io.*;

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
                                                                                                                                                                                                                                                                                      EncodedString.java                                                                                  0000600 0012335 0000226 00000002107 10225323215 014720  0                                                                                                    ustar   rwt5629                         ugrad                           0000000 0000000                                                                                                                                                                        import java.io.*;

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
                                                                                                                                                                                                                                                                                                                                                                                                                                                         FlapChannel.java                                                                                    0000644 0012335 0000226 00000000373 10216705613 014364  0                                                                                                    ustar   rwt5629                         ugrad                           0000000 0000000                                                                                                                                                                        public class FlapChannel {

    public static final byte LOGIN = 0x01;
    public static final byte SNAC = 0x02;
    public static final byte ERROR = 0x03;
    public static final byte CLOSING = 0x04;
    public static final byte KEEPALIVE = 0x05;

}
                                                                                                                                                                                                                                                                     FlapConnection.java                                                                                 0000644 0012335 0000226 00000003115 10216715165 015113  0                                                                                                    ustar   rwt5629                         ugrad                           0000000 0000000                                                                                                                                                                        import java.io.*;
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
                                                                                                                                                                                                                                                                                                                                                                                                                                                   FlapPacket.java                                                                                     0000644 0012335 0000226 00000005323 10225101050 014204  0                                                                                                    ustar   rwt5629                         ugrad                           0000000 0000000                                                                                                                                                                        import java.io.*;
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
                                                                                                                                                                                                                                                                                                             Icbm.java                                                                                           0000600 0012335 0000226 00000002324 10225106025 013041  0                                                                                                    ustar   rwt5629                         ugrad                           0000000 0000000                                                                                                                                                                        import java.io.*;

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
                                                                                                                                                                                                                                                                                                            IcbmId.java                                                                                         0000600 0012335 0000226 00000002544 10225110221 013313  0                                                                                                    ustar   rwt5629                         ugrad                           0000000 0000000                                                                                                                                                                        import java.util.*;

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
                                                                                                                                                            MessageBlock.java                                                                                   0000600 0012335 0000226 00000002547 10225324411 014536  0                                                                                                    ustar   rwt5629                         ugrad                           0000000 0000000                                                                                                                                                                        import java.io.*;
import java.util.*;

public class MessageBlock {

    private TlvChain tlv = new TlvChain();
    private List<MessagePart> messageParts = new ArrayList<MessagePart>();

    public MessageBlock( byte[] data ) {
        this( data, 0 );
    }

    public MessageBlock( byte[] data, int offset ) {
        
    }

    public void addMessagePart( String text, int charset ) {
        messageParts.add( new MessagePart( text, charset ) );
    }

    public void addMessagePart( byte[] text ) {
        messageParts.add( new MessagePart( text ) );
    }

    public void addMessagePart( MessagePart text ) {
        messageParts.add( text );
    }

    public String getMessage() {
        String message = "";
        for( MessagePart part : messageParts ) {
            message += part.getString();
        }
        return message;
    }

    public TlvBlock getBlock() {
        return new TlvBlock( 0x0001, getBytes() );
    }

    public byte[] getBytes() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write( new byte[] { 0x05, 0x01, 0x00, 0x03, 0x01, 0x01, 0x02 } );
            for( MessagePart part : messageParts ) {
                out.write( part.getBytes() );
            }
            return out.toByteArray();
        } catch ( IOException ioex ) {
            return new byte[ 0 ];
        }
    }
}
                                                                                                                                                         MessagePart.java                                                                                    0000600 0012335 0000226 00000001317 10225324140 014403  0                                                                                                    ustar   rwt5629                         ugrad                           0000000 0000000                                                                                                                                                                        public class MessagePart {

    private int charset = 0;
    private int subset = 0;
    private byte[] bytes;
    private String text;

    public MessagePart( String text, int charset ) {
        this.charset = charset;
        this.text = text;
        this.bytes = EncodedString.parse( charset, text );
    }

    public MessagePart( byte[] bytes ) {
        this.charset = UnsignedShort.parse( bytes, 0 );
        this.bytes = bytes;
        this.text = EncodedString.parse( bytes );
    }

    public String getString() {
        return this.text;
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    public TlvBlock getBlock() {
        return new TlvBlock( 0x0101, getBytes() );
    }

}
                                                                                                                                                                                                                                                                                                                 OscarWrapper.java                                                                                   0000600 0012335 0000226 00000000677 10216715567 014631  0                                                                                                    ustar   rwt5629                         ugrad                           0000000 0000000                                                                                                                                                                        import java.io.*;
import java.net.*;

public class OscarWrapper implements Runnable {

    private FlapConnection flap;
//    private 

    public OscarWrapper( Socket sock ) {
        this( new FlapConnection( sock ) );
    }

    public OscarWrapper( FlapConnection flap ) {
        this.flap = flap;
    }

    public void run() {
//        try {
//            flap.
//        } catch ( IOException ioex ) {
//            
//        }
    }

}
                                                                 OutgoingIcbm.java                                                                                   0000600 0012335 0000226 00000000275 10216714515 014571  0                                                                                                    ustar   rwt5629                         ugrad                           0000000 0000000                                                                                                                                                                        public class OutgoingIcbm {

//    private 

    public OutgoingIcbm( byte[] data ) {
        this( data, 0 );
    }

    public OutgoingIcbm( byte[] data, int offset ) {
        
    }

}
                                                                                                                                                                                                                                                                                                                                   Parse.java                                                                                          0000600 0012335 0000226 00000026311 10225071312 013243  0                                                                                                    ustar   rwt5629                         ugrad                           0000000 0000000                                                                                                                                                                        import java.io.*;
import java.util.*;

public class Parse {

    public static void main( String[] args ) {
        if ( args.length != 1 ) System.exit( -1 );

        byte[] data = readFile( args[0] );
//        FlapPacket flap = new FlapPacket( data, 0 );
        List<FlapPacket> flaps = FlapPacket.getPackets( data );

        for ( FlapPacket flap : flaps ) {
            String channel;
            channel = toHex( flap.getChannel(), 2 );

            System.out.println( "FLAP --" );
            System.out.println( "  channel: 0x" + channel );
            System.out.println( "  seqnum : " + flap.getSeqNum() );
            System.out.println( "  length : " + flap.getLength() );
            System.out.println();

            data = flap.getData();

            if ( flap.getChannel() == FlapChannel.LOGIN ) {
                System.out.println( "Login FLAP" );
                System.out.println( "Cookie: " + 
                        UnsignedInt.parse( data, 0 ) );
                if ( flap.getLength() > 4 ) {
                    TlvChain chain = new TlvChain( data, 4 );
                    System.out.print( "Cookie: " );
                    byteDump( chain.getBlock( 0x0006 ) );
                }                
            } else if ( flap.getChannel() == FlapChannel.SNAC ) {
                SnacCommand snac = flap.getSNAC();

                String family, subtype, flags;
                family = toHex( snac.getFamily(), 4 );
                subtype = toHex( snac.getSubtype(), 4 );
                flags = toBin( snac.getFlags(), 16 );

                System.out.println( "SNAC --" );
                System.out.println( "  family : 0x" + family );
                System.out.println( "  subtype: 0x" + subtype );
                System.out.println( "  flags  : " + flags );
                System.out.println( "  reqid  : " + snac.getReqId() );
                System.out.println();

                data = snac.getData();

                handleSnac( snac );
            }

//            byteDump( data );
//            System.out.println( Ascii.parse( data ) );

            System.out.println( "\r\n" );
        }
            
    }

    public static void handleSnac( SnacCommand snac ) {
        int family = snac.getFamily();
        int subtype = snac.getSubtype();
        byte[] data = snac.getData();

        TlvChain chain = null;  // alot of constructs contain a tlv

        if ( family == 0x0001 ) {         // basic connection management

//            if 

        } else if ( family == 0x0002 ) {  // user information

            

        } else if ( family == 0x0003 ) {  // buddy status notifications

            

        } else if ( family == 0x0004 ) {  // instant messaging

            if ( subtype == 0x0006 ) {        // outgoing icbm

                long id = IcbmId.parse( data, 0 );
                int channel = UnsignedShort.parse( data, 8 );
                String screenname = AsciiStringBlock.parse( data, 10 );
                chain = new TlvChain( data, screenname.length() + 10 );
                MessageBlock message = new MessageBlock( chain.getBlock( 0x0002 ) );
                

            } else if ( subtype == 0x0007 ) { // incoming icbm

                

            } else if ( subtype == 0x0014 ) { // mini typing notification

                String screenname = AsciiStringBlock.parse( data, 10 );
                System.out.println( "Typing Notification" );
                System.out.print( "Screenname: " );
                System.out.println( screenname );
                System.out.print( "Typing State: " );
                switch ( UnsignedShort.parse( data, 11 + screenname.length() ) ) {
                    case 0:
                        System.out.println( "Not Typing" );
                        break;
                    case 1:
                        System.out.println( "Entered Text" );
                        break;
                    case 2:
                        System.out.println( "Typing" );
                        break;
                }

            }

        } else if ( family == 0x0007 ) {  // account admin

            

        } else if ( family == 0x0013 ) {  // server-stored information

            

        } else if ( family == 0x0015 ) {  // icq-specific

            

        } else if ( family == 0x0017 ) {  // initial authentication

            if ( subtype == 0x0002 ) {         // auth request snac
                chain = new TlvChain( snac.getData() );
                System.out.println( "Auth Request Snac" );
                System.out.print( "Screenname: " );
                System.out.println( Ascii.parse( chain.getBlock( 0x0001 ) ) );
                System.out.print( "Encrypted Pass: " );
                byteDump( chain.getBlock( 0x0025 ) );
                System.out.println();
                if ( chain.containsBlock( 0x004c ) )
                    System.out.println( "AIM 5.5+ Password Encryption" );
                System.out.print( "Country: " );
                System.out.println( Ascii.parse( chain.getBlock( 0x000e ) ) );
                System.out.print( "Language: " );
                System.out.println( Ascii.parse( chain.getBlock( 0x000f ) ) );
//                TlvChain cvi = new TlvChain( chain.getBlock( 0x0
            } else if ( subtype == 0x0003 ) {  // auth response snac
                chain = new TlvChain( snac.getData() );
                System.out.println( "Auth Response Snac" );
                System.out.print( "Screenname: " );
                System.out.println( Ascii.parse( chain.getBlock( 0x0001 ) ) );
                if ( chain.containsBlock( 0x0006 ) ) {
                    // cookie
                    System.out.println( "Login Successful" );
                    System.out.print( "BOS Server: " );
                    System.out.println( Ascii.parse( chain.getBlock( 0x0005 ) ) );
                    System.out.print( "Login Cookie: " );
                    byteDump( chain.getBlock( 0x0006 ) );
                    System.out.println();
                    System.out.print( "Account Email: " );
                    System.out.println( Ascii.parse( chain.getBlock( 0x0011 ) ) );
                    System.out.print( "Users who know my screename can find out " );
                    switch ( UnsignedShort.parse( chain.getBlock( 0x0013 ) ) ) {
                        case 0x0001:
                            System.out.println( "nothing about me." );
                            break;
                        case 0x0002:
                            System.out.println( "only that I have an account." );
                            break;
                        case 0x0003:
                            System.out.println( "my screename." );
                            break;
                        default:
                            System.out.println( UnsignedShort.parse( chain.getBlock( 0x0011 ) ) );
                            break;
                    }
                } else if ( chain.containsBlock( 0x0008 ) ) {
                    // error code type
                    System.out.println( "Login Failed" );
                    switch ( UnsignedShort.parse( chain.getBlock( 0x0013 ) ) ) {
                        case 0x0005:
                            System.out.println( "Invalid screenname or wrong password" );
                            break;
                        case 0x0011:
                            System.out.println( "Account has been suspended temporarily" );
                            break;
                        case 0x0014:
                            System.out.println( "Account temporarily unavailable" );
                            break;
                        case 0x0018:
                            System.out.println( "Connecting too frequently" );
                            break;
                        case 0x001c:
                            System.out.println( "Client software is too old to connect" );
                            break;
                        default:
                            break;
                    }
                    System.out.print( "Error URL: " );
                    System.out.println( Ascii.parse( chain.getBlock( 0x0004 ) ) );
                }
            } else if ( subtype == 0x0006 ) {  // key request snac
                chain = new TlvChain( snac.getData() );
                System.out.println( "Key Request Snac" );
                System.out.print( "Screenname: " );
                System.out.println( Ascii.parse( chain.getBlock( 0x0001 ) ) );
            } else if ( subtype == 0x0007 ) {  // key response snac
                System.out.println( "Key Response Snac" );
                System.out.print( "Key: " );
                System.out.println( UnsignedInt.parse( snac.getData(), 2 ) );
            } else {
                // unsupported 0x0017 family
            }
        } else {            
            // unrecognized/unsupported snac family
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

    public static void byteDump( byte[] data ) {
        byteDump( data, 0 );
    }

    public static void byteDump( byte[] data, int offset ) {
        for ( int x = offset; x < data.length; x++ ) {
            System.out.print( toHex( data[ x ] ) + " " );
        }
    }

    public static String toHex( byte val ) {
        final String hex = "0123456789ABCDEF";
        String retVal = "";
        retVal += hex.charAt( (val & 0xF0) >> 4 );
        retVal += hex.charAt( val & 0x0F );
        return retVal;
    }

    public static String toHex( long val, int len ) {
        final String hex = "0123456789ABCDEF";
        String retVal = "";
        for ( int i = len - 1; i >= 0; i-- ) {
            int mask = 0x0F << i;
            retVal += hex.charAt( (int)((val & mask) >> (4 * i)) );
        }
        return retVal;
    }

    public static String toBin( long val, int len ) {
        String retVal = "";
        for ( int i = len - 1; i >= 0; i-- ) {
            int mask = 0x01 << i;
            retVal += ((val & mask) > 0) ? 1 : 0;
        }
        return retVal;
    }

    public static String stripHtml( String text ) {

        final byte lt = 0x3C;
        final byte gt = 0x3E;

        try {
            ByteArrayInputStream in = new ByteArrayInputStream(
                 text.getBytes( "US-ASCII" ) );
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int htmlLevel = 0;

            while( in.available() != 0 ) {
                int thisByte = in.read();
                if ( thisByte == lt ) {
                    htmlLevel++;
                } else if ( thisByte == gt ) {
                    htmlLevel--;
                } else {
                    if ( htmlLevel == 0 ) // if not encased in < >
                        out.write( thisByte );
                }
            }
            return new String( out.toByteArray(), "US-ASCII" );
        } catch ( IOException ioex ) {
            // shouldnt happen
            return new String( "" );
        }
    }
}
                                                                                                                                                                                                                                                                                                                       Proxy.java                                                                                          0000600 0012335 0000226 00000001354 10216714565 013330  0                                                                                                    ustar   rwt5629                         ugrad                           0000000 0000000                                                                                                                                                                        import java.io.*;
import java.net.*;
import java.util.*;

public class Proxy implements Runnable {

    private Thread t;
    private ServerSocket serv;
    private FlapConnection flap;
    private OscarWrapper oscar;

    private Set<FlapConnection> conxions;
    
    public static void main( String args[] ) {
        
    }
    
    public Proxy( String username, String password ) {
        this();
        
    }

    private Proxy() {
        try {
            serv = new ServerSocket( 5190 );
            
        } catch ( Exception ex ) {
            
        }
    }

    public void run() {
        try {
            while ( true ) {
                
            }
        } catch ( Exception ex ) {
            
        }
    }
    
}
                                                                                                                                                                                                                                                                                    SnacCommand.java                                                                                    0000644 0012335 0000226 00000007405 10225111134 014364  0                                                                                                    ustar   rwt5629                         ugrad                           0000000 0000000                                                                                                                                                                        import java.io.*;
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
                                                                                                                                                                                                                                                           SocketEvent.java                                                                                    0000600 0012335 0000226 00000000220 10227115735 014424  0                                                                                                    ustar   rwt5629                         ugrad                           0000000 0000000                                                                                                                                                                        import java.io.*;
import java.net.*;

public class SocketEvent {

    private SocketWrapper sock;

    public SocketEvent() {
        
    }

}
                                                                                                                                                                                                                                                                                                                                                                                SocketListener.java                                                                                 0000600 0012335 0000226 00000000177 10225627407 015145  0                                                                                                    ustar   rwt5629                         ugrad                           0000000 0000000                                                                                                                                                                        import java.util.*;

public interface SocketListener extends EventListener {

    public void socketEvent( SocketEvent e );

}
                                                                                                                                                                                                                                                                                                                                                                                                 SocketWrapper.java                                                                                  0000600 0012335 0000226 00000001073 10230534165 014766  0                                                                                                    ustar   rwt5629                         ugrad                           0000000 0000000                                                                                                                                                                        import java.io.*;
import java.net.*;
import java.util.*;

public class SocketWrapper {

    private Set<SocketListener> listeners = new HashSet<SocketListener>();

    public SocketWrapper( String host, int port ) {
        
    }

    

    public void addSocketListener( SocketListener e ) {
        listeners.add( e );
    }

    public void removeSocketListener( SocketListener e ) {
        listeners.remove( e );
    }

    private void raiseEvent( SocketEvent e ) {
        for ( SocketListener l : listeners ) {
            l.socketEvent( e );
        }
    }

}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                     TlvBlock.java                                                                                       0000644 0012335 0000226 00000003256 10225330430 013723  0                                                                                                    ustar   rwt5629                         ugrad                           0000000 0000000                                                                                                                                                                        import java.io.*;

public class TlvBlock {

    private int type;
    private int length;
    private byte[] data;

    public TlvBlock( byte[] data ) {
        this( data, 0 );
    }

    public TlvBlock( byte[] data, int offset ) {
        this.type = UnsignedShort.parse( data, offset + 0 );
        this.length = UnsignedShort.parse( data, offset + 2 );
        this.data = new byte[ length ];
System.out.println( this.length );
System.out.println( data.length );
        System.arraycopy( data, offset + 4, this.data, 0, length );
    }

    public TlvBlock( int type ) {
        this( type, new byte[0] );
    }

    public TlvBlock( int type, byte[] data ) {
        this.type = type;
        this.data = data;
        
        try {
            this.length = data.length;
        } catch ( NullPointerException npex ) {
            this.length = 0;
        }
    }

    public int getType() {
        return this.type;
    }

    public void setType( int value ) {
        this.type = value;
    }

    public int getLength() {
        return this.length;
    }

    public byte[] getValue() {
        return this.data;
    }

    public void setValue( byte[] value ) {
        this.data = value;
    }

    public byte[] getBytes() {
        byte[] block;

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            out.write( UnsignedShort.parse( this.type ) );
            out.write( UnsignedShort.parse( this.length ) );
            out.write( this.data );
            
            block = out.toByteArray();
            
            out.close();
        } catch ( IOException ioex ) {
            block = new byte[0];
        }

        return block;
    }

}
                                                                                                                                                                                                                                                                                                                                                  TlvChain.java                                                                                       0000644 0012335 0000226 00000006577 10225234730 013732  0                                                                                                    ustar   rwt5629                         ugrad                           0000000 0000000                                                                                                                                                                        import java.io.*;
import java.util.*;

public class TlvChain implements Iterable {

    private class ByteArray {

        byte[] data = null;

        public ByteArray( byte[] data ) {
            this.data = data;
        }

        public byte[] getBytes() {
            return this.data;
        }
    }

    private int startOffset, endOffset;

    private List<TlvBlock> chain = new ArrayList<TlvBlock>();
    private Map<Integer,ByteArray> blocks = new HashMap<Integer,ByteArray>();

    public TlvChain() {
        
    }

    public TlvChain( byte[] data ) {
        this( data, 0 );
    }

    public TlvChain( byte[] data, int offset ) {
        startOffset = offset;
        while( offset < data.length ) {
            offset += 4 + addBlock( data, offset ).getLength();
        }
        endOffset = offset;
    }

    public TlvChain( byte[] data, short count ) {
        this( data, 0, count );
    }

    public TlvChain( byte[] data, int offset, short count ) {
        short incr = 0;
        startOffset = offset;
        while ( incr < count && offset < data.length ) {
            offset += 4 + addBlock( data, offset ).getLength();
        }
        endOffset = offset;
    }

    public TlvBlock addBlock( int type ) {
        TlvBlock block = new TlvBlock( type );
        addBlock( block );
        return block;
    }

    public TlvBlock addBlock( int type, byte[] data ) {
        TlvBlock block = new TlvBlock( type, data );
        addBlock( block );
        return block;
    }

    public TlvBlock addBlock( byte[] data ) {
        TlvBlock block = new TlvBlock( data );
        addBlock( block );
        return block;
    }

    public TlvBlock addBlock( byte[] data, int offset ) {
        TlvBlock block = new TlvBlock( data, offset );
        addBlock( block );
        return block;
    }

    public void addBlock( TlvBlock block ) {
        chain.add( block );
        blocks.put( new Integer( block.getType() ),
                  new ByteArray( block.getValue() ) );
    }

    public byte[] getBlock( int type ) {
        Integer blockType = new Integer( type );
        if ( blocks.containsKey( blockType ) ) {
            return blocks.get( blockType ).getBytes();
        } else {
            return new byte[0];
        }
    }

    public List<TlvBlock> getBlocks() {
        return chain;
    }

    public boolean containsBlock( int type ) {
        return blocks.containsKey( new Integer( type ) );
    }

    public byte[] getBytes() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            for( TlvBlock block : chain ) {
                out.write( block.getBytes() );
            }
            return out.toByteArray();
        } catch ( IOException ioex ) {
            return new byte[0];
        }
    }

    public int getCount() {
        return chain.size();
    }

    public int getChainLength() {
        return endOffset - startOffset;
    }

    public Iterator iterator() {
        return chain.iterator();
    }

    public void dump() {
        for ( TlvBlock block : chain ) {
            String type = Parse.toHex( block.getType(), 4 );

            System.out.println( "  TlvBlock --" );
            System.out.println( "    type  : 0x" + type );
            System.out.println( "    length: " + block.getLength() );
            System.out.println( "    value : " 
                         + new String( block.getValue() ) );
        }
    }

}
                                                                                                                                 UnixDate.java                                                                                       0000600 0012335 0000226 00000000633 10216707573 013730  0                                                                                                    ustar   rwt5629                         ugrad                           0000000 0000000                                                                                                                                                                        import java.util.*;

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
                                                                                                     UnsignedByte.java                                                                                   0000644 0012335 0000226 00000000522 10212714576 014611  0                                                                                                    ustar   rwt5629                         ugrad                           0000000 0000000                                                                                                                                                                        public class UnsignedByte {

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
                                                                                                                                                                              UnsignedInt.java                                                                                    0000644 0012335 0000226 00000001300 10212714737 014432  0                                                                                                    ustar   rwt5629                         ugrad                           0000000 0000000                                                                                                                                                                        public class UnsignedInt {

    public static long parse( byte[] data ) {
        return parse( data, 0 );
    }

    public static long parse( byte[] data, int offset ) {
        return (((long) data[offset] & 0xffL) << 24)
             | (((long) data[offset+1] & 0xffL) << 16)
             | (((long) data[offset+2] & 0xffL) << 8)
             |  ((long) data[offset+3] & 0xffL);
    }

    public static byte[] parse( long number ) {
        byte[] data = new byte[4];
        
        data[0] = (byte) ((number >> 24) & 0xff);
        data[1] = (byte) ((number >> 16) & 0xff);
        data[2] = (byte) ((number >> 8) & 0xff);
        data[3] = (byte) (number & 0xff);

        return data;
    }

}
                                                                                                                                                                                                                                                                                                                                UnsignedShort.java                                                                                  0000644 0012335 0000226 00000000705 10222117553 015001  0                                                                                                    ustar   rwt5629                         ugrad                           0000000 0000000                                                                                                                                                                        public class UnsignedShort {

    public static int parse( byte[] data ) {
        return parse( data, 0 );
    }

    public static int parse( byte[] data, int offset ) {
        return ((data[offset] & 0xff) << 8) | (data[offset+1] & 0xff);
    }

    public static byte[] parse( int number ) {
        byte[] data = new byte[2];

        data[0] = (byte)((number >> 8) & 0xff);
        data[1] = (byte)(number & 0xff);

        return data;
    }

}
                                                           UserInfoBlock.java                                                                                  0000644 0012335 0000226 00000001763 10221031274 014711  0                                                                                                    ustar   rwt5629                         ugrad                           0000000 0000000                                                                                                                                                                        public class UserInfoBlock {

    int startOffset, endOffset;
    String screenname;
    int warningLevel, count;
    TlvChain info;

    public UserInfoBlock( byte[] data ) {
        this( data, 0 );
    }

    public UserInfoBlock( byte[] data, int offset ) {
        startOffset = offset;
        screenname = AsciiStringBlock.parse( data, offset );
        offset += screenname.length() + 1;
        warningLevel = UnsignedShort.parse( data, offset );
        count = UnsignedShort.parse( data, offset + 2 );
        info = new TlvChain( data, offset + 4 );
        
    }

    public int getOffset() {
        return startOffset;
    }

    public int getLength() {
        return endOffset - startOffset;
    }

    public String getScreenname() {
        return screenname;
    }

    public float getWarningLevel() {
        return warningLevel / 10;
    }

    public int getCount() {
        return count;
    }

    public byte[] getBlock( int type ) {
        return info.getBlock( type );
    }

}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             