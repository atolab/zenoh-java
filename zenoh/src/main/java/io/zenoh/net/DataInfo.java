package io.zenoh.net;

/**
 * Some meta informations about the associated data.
 */
public class DataInfo {

    private long flags;
    private Timestamp tstamp;
    private int encoding;
    private int kind;

    protected DataInfo(long flags, Timestamp tstamp , int encoding, int kind) {
        this.flags = flags;
        this.tstamp = tstamp;
        this.encoding = encoding;
        this.kind = kind;
    }

    /**
     * @return flags indicating which meta information is present in the {@link DataInfo}.
     */
    public long getFlags() {
        return flags;
    }

    /**
     * @return the data encoding.
     */
    public int getEncoding() {
        return encoding;
    }

    /**
     * @return the unique timestamp at which the data was produced.
     */
    public Timestamp getTimestamp() {
        return tstamp;
    }

    /**
     * @return the data kind.
     */
    public int getKind() {
        return kind;
    }
}
