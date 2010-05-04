import java.io.*;
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
