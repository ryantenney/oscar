import java.io.*;
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
