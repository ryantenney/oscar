import java.io.*;
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
