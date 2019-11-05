package io.zenoh;

/**
 * A Zenoh timestamp.
 */
public class Timestamp {

    private long time;
    private byte[] clockId;

    protected Timestamp(long time, byte[] clockId) {
        this.time = time;
        this.clockId = clockId;
    }

    /**
     * @return the Timestamp's creation time.
     */
    public long getTime() {
        return time;
    }

    /**
     * @return the unique identifier of the clock that created this Timestamp.
     */
    public byte[] getClockId() {
        return clockId;
    }
}
