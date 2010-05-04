import java.io.*;
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
