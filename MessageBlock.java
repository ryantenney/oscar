import java.io.*;
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
