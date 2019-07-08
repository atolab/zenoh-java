package io.zenoh;

/**
 * The information associated to a received data.
 */
public class DataInfo {

    private long flags;
    private int encoding;
    private int kind;

    protected DataInfo(long flags, int encoding, int kind) {
        this.flags = flags;
        this.encoding = encoding;
        this.kind = kind;
    }

    /**
     * Return the data flags.
     */
    public long getFlags() {
        return flags;
    }

    /**
     * Return the data encoding.
     */
    public int getEncoding() {
        return encoding;
    }

    /**
     * Return the data kind.
     */
    public int getKind() {
        return kind;
    }
}
