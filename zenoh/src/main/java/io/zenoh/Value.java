package io.zenoh;

import java.nio.ByteBuffer;

/**
 * Interface of a Value that, associated to a {@link Path}, can be published into zenoh
 * via {@link Workspace#put(Path, Value)}, or retrieved via {@link Workspace#get(Selector)} or
 * via a subscription ({@link Workspace#subscribe(Selector, Listener)}).
 */
public interface Value {

    /**
     * Interface of a Value decoder, able to transform a {@link ByteBuffer} into a Value. 
     */
    public interface Decoder {
        /**
         * Returns the flag of the {@link Encoding} that this Decoder supports.
         * (@see Encoding#getFlag()).
         * @return the encoding flag.
         */
        public short getEncodingFlag();

        /**
         * Decode a Value that is encoded in a {@link ByteBuffer}.
         * @param buf the ByteBuffer containing the encoded Value.
         * @return the Value.
         */
        public Value decode(ByteBuffer buf);
    }

    /**
     * Return the {@link Encoding} of this Value.
     * @return the encoding.
     */
    public Encoding getEncoding();

    /**
     * Returns a new {@link ByteBuffer} containing the Value encoded
     * as a sequence of bytes.
     * @return the encoded Value.
     */
    public ByteBuffer encode();
}
