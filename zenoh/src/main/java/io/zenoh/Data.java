package io.zenoh;

import org.slf4j.LoggerFactory;

import io.zenoh.core.Timestamp;

public class Data implements Comparable<Data> {

    private Path path;
    private Value value;
    private Timestamp timestamp;

    protected Data(Path path, Value value, Timestamp timestamp) {
        this.path = path;
        this.value = value;
        this.timestamp = timestamp;
    }

    public Path getPath() {
        return path;
    }

    public Value getValue() {
        return value;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public int compareTo(Data o) {
        // Order entires according to their timestamps
        return timestamp.compareTo(o.timestamp);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (! (obj instanceof Data))
            return false;

        Data o = (Data) obj;
        // As timestamp is unique per data, only compare timestamps.
        if (!timestamp.equals(o.timestamp))
            return false;

        // however, log a warning if path and values are different...
        if (!path.equals(o.path)) {
            LoggerFactory.getLogger("io.zenoh").warn(
                "INTERNAL ERROR: 2 entries with same timestamp {} have different paths: {} vs. {}",
                timestamp, path, o.path);
        }
        if (!value.equals(o.value)) {
            LoggerFactory.getLogger("io.zenoh").warn(
                "INTERNAL ERROR: 2 entries with same timestamp {} have different values: {} vs. {}",
                timestamp, value, o.value);
        }

        return true;
    }

    @Override
    public int hashCode() {
        return timestamp.hashCode();
    }
}