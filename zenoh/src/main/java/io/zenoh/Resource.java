package io.zenoh;

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
     * Return the resource name.
     */
    public String getRname() {
        return rname;
    }

    /**
     * return the resource value.
     */
    public ByteBuffer getData() {
        return data;
    }

    /**
     * Return the encoding of the resource value.
     */
    public int getEncoding() {
        return encoding;
    }

    /**
     * Return the kind of the resource.
     */
    public int getKind() {
        return kind;
    }
}
