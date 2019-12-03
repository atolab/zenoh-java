package io.zenoh.net;

import java.nio.ByteBuffer;

/**
 * A resource with a name and a value (data).
 */
public class Resource {

    private String rname;
    private ByteBuffer data;
    private int encoding;
    private int kind;

    public Resource(String rname, ByteBuffer data, int encoding, int kind) {
        this.rname = rname;
        this.data = data;
        this.encoding = encoding;
        this.kind = kind;
    }

    /**
     * @return the resource name.
     */
    public String getRname() {
        return rname;
    }

    /**
     * @return the resource value.
     */
    public ByteBuffer getData() {
        return data;
    }

    /**
     * @return the encoding of the resource value.
     */
    public int getEncoding() {
        return encoding;
    }

    /**
     * @return the kind of the resource.
     */
    public int getKind() {
        return kind;
    }
}
