import java.io.*;
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
