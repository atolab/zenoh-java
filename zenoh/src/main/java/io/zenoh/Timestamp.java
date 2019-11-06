package io.zenoh;

import java.time.Instant;
import java.util.Arrays;

/**
 * A Zenoh timestamp.
 */
public class Timestamp implements Comparable<Timestamp> {

    private long time;
    private byte[] clockId;
    private String asString;

    protected Timestamp(long time, byte[] clockId) {
        this.time = time;
        this.clockId = clockId;
        this.asString = null;
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

    /* number of NTP fraction per second (2^32) */
    private static final long FRAC_PER_SEC = 0x100000000L;

    /* number of nanoseconds per second (10^9) */
    private static final long NANO_PER_SEC = 1000000000L;

    /**
     * 
     * @return the Timestamp's creation time as a {@link java.time.Instant}.
     */
    public Instant getTimeAsInstant() {
        Instant i = Instant.ofEpochSecond(time >> 32);
        long frac = time & 0xffffffffL;
        return i.plusNanos((frac * NANO_PER_SEC) / FRAC_PER_SEC);
    }

    @Override
    public int compareTo(Timestamp o) {
        int tcmp = Long.compare(this.time, o.time);
        if (tcmp != 0) {
            return tcmp;
        }

        for (int i = 0; i < this.clockId.length; i++) {
            int bcmp = Byte.compare(this.clockId[i], o.clockId[i]);
            if (bcmp != 0) {
                return bcmp;
            }
        }

        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (! (obj instanceof Timestamp))
            return false;

        Timestamp o = (Timestamp) obj;
        return this.time == o.time && Arrays.equals(this.clockId, o.clockId);
    }

    @Override
    public int hashCode() {
        int h = Long.hashCode(time);
        for (byte b : clockId)
            h = 31 * h + b;

        return h;
    }

    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();

    @Override
    public String toString() {
        synchronized (this) {
            if (asString == null) {
                StringBuffer sb = new StringBuffer();
                sb.append(getTimeAsInstant().toString())
                  .append('/');
                  for (byte b : clockId) {
                    sb.append(HEX_DIGITS[(b & 0xF0) >> 4])
                      .append(HEX_DIGITS[b & 0x0F]);
                }
                asString = sb.toString();
            }
        }
        return asString;
    }

}
