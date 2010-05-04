public class MessagePart {

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
