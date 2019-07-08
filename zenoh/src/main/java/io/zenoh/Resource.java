package io.zenoh;

import java.nio.ByteBuffer;

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

    public String getRname() {
        return rname;
    }

    public ByteBuffer getData() {
        return data;
    }

    public int getEncoding() {
        return encoding;
    }

    public int getKind() {
        return kind;
    }
}
