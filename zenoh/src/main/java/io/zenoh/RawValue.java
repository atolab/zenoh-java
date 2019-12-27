package io.zenoh;

import java.nio.ByteBuffer;

/**
 * A {@link Value} containing a {@link ByteBuffer}.
 */
public class RawValue implements Value {

    private static final short ENCODING_FLAG = 0x00;

    private ByteBuffer buf;


    /**
     * Creates a RawValue containing a {@link ByteBuffer}.
     * @param buf the bytes buffer.
     */
    public RawValue(ByteBuffer buf) {
        this.buf = buf;
    }

    /**
     * Returns the {@link ByteBuffer} from this RawValue
     * @return the bytes buffer.
     */
    public ByteBuffer getBuffer() {
        return buf;
    }

    @Override
    public Encoding getEncoding() {
        return Encoding.RAW;
    }

    @Override
    public ByteBuffer encode() {
        return buf;
    }

    @Override
    public String toString() {
        return buf.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (! (obj instanceof RawValue))
            return false;

        return buf.equals(((RawValue) obj).buf);
    }

    @Override
    public int hashCode() {
        return buf.hashCode();
    }

    /**
     * The {@link Value.Decoder} for {@link RawValue}s.
     */
    public static final Value.Decoder Decoder = new Value.Decoder() {
        @Override
        public short getEncodingFlag() {
            return ENCODING_FLAG;
        }

        @Override
        public Value decode(ByteBuffer buf) {
            return new RawValue(buf);
        }
    };
}
