import java.io.*;
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
