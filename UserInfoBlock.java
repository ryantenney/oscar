public class UserInfoBlock {

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
