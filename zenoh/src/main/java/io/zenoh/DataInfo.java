package io.zenoh;

/**
 * Some meta informations about the associated data.
 */
public class DataInfo {

    private long flags;
    private int encoding;
    private long time;
    private int kind;

    protected DataInfo(long flags, int encoding, long time, int kind) {
        this.flags = flags;
        this.encoding = encoding;
        this.time = time;
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
    public long getTime() {
        return time;
    }

    /**
     * @return the data kind.
     */
    public int getKind() {
        return kind;
    }
}
