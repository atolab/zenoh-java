package io.zenoh.net;

import io.zenoh.core.Timestamp;

/**
 * Data structure containing meta informations about the associated data.
 */
public class DataInfo {

    private Timestamp tstamp = null;
    private int encoding = 0;
    private int kind = 0;

    private static final long ZN_T_STAMP  = 0x10;
    private static final long ZN_KIND = 0x20;
    private static final long ZN_ENCODING = 0x40;
    

    protected DataInfo(long flags, Timestamp tstamp , int encoding, int kind) {
        if ((flags & ZN_T_STAMP) != 0L) {
            this.tstamp = tstamp;
        }
        if ((flags & ZN_KIND) != 0L) {
            this.kind = kind;
        }
        if ((flags & ZN_ENCODING) != 0L) {
            this.encoding = encoding;
        }
    }

    /**
     * @return the encoding of the data.
     */
    public int getEncoding() {
        return encoding;
    }

    /**
     * @return the unique timestamp at which the data has been produced.
     */
    public Timestamp getTimestamp() {
        return tstamp;
    }

    /**
     * @return the kind of the data.
     */
    public int getKind() {
        return kind;
    }
}
