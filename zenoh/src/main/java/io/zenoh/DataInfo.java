package io.zenoh;

public class DataInfo {

    private long flags;
    private int encoding;
    private int kind;

    DataInfo(long flags, int encoding, int kind) {
        this.flags = flags;
        this.encoding = encoding;
        this.kind = kind;
    }

    public long getFlags() {
        return flags;
    }

    public int getEncoding() {
        return encoding;
    }

    public int getKind() {
        return kind;
    }
}
