import java.io.*;

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
