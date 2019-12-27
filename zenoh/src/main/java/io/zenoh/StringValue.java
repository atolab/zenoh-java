package io.zenoh;

import java.nio.charset.Charset;
import java.nio.ByteBuffer;

/**
 * A {@link Value} containing an UTF-8 {@link String}.
 */
public class StringValue implements Value{

    private static final short ENCODING_FLAG = 0x02;

    private static final Charset utf8 = Charset.forName("UTF-8");

    private String s;


    /**
     * Creates a StringValue containing a {@link String}.
     * @param s the string
     */
    public StringValue(String s) {
        this.s = s;
    }

    /**
     * Returns the string from this StringValue
     * @return the string
     */
    public String getString() {
        return s;
    }

    @Override
    public Encoding getEncoding() {
        return Encoding.STRING;
    }

    @Override
    public ByteBuffer encode() {
        return ByteBuffer.wrap(s.getBytes(utf8));
    }

    @Override
    public String toString() {
        return s;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (! (obj instanceof StringValue))
            return false;

        return s.equals(((StringValue) obj).s);
    }

    @Override
    public int hashCode() {
        return s.hashCode();
    }

    /**
     * The {@link Value.Decoder} for {@link StringValue}s.
     */
    public static final Value.Decoder Decoder = new Value.Decoder() {
        @Override
        public short getEncodingFlag() {
            return ENCODING_FLAG;
        }

        @Override
       public Value decode(ByteBuffer buf) {
            return new StringValue(new String(buf.array(), utf8));
        }
    };

}
