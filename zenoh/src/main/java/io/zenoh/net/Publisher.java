package io.zenoh.net;

import java.nio.ByteBuffer;

import io.zenoh.swig.z_pub_t;
import io.zenoh.swig.zenohc;

/**
 * A Publisher (see {@link Zenoh#declarePublisher(String)}).
 */
public class Publisher {

    private z_pub_t pub;

    protected Publisher(z_pub_t pub) {
        this.pub = pub;
    }

    /**
     * Undeclare the Publisher.
     * @throws ZNetException if undeclaration failed.
     */
    public void undeclare() throws ZNetException {
        int error = zenohc.z_undeclare_publisher(pub);
        if (error != 0) {
            throw new ZNetException("z_undeclare_publisher failed ", error);
        }
    }

    /**
     * Write a data with default encoding (0) and kind (0), using a Z_COMPACT_DATA message.
     * @param data the data
     * @throws ZNetException if write fails.
     */
    public void streamCompactData(ByteBuffer data) throws ZNetException {
        int result = zenohc.z_stream_compact_data(pub, data);
        if (result != 0) {
            throw new ZNetException("z_stream_compact_data of "+data.capacity()+" bytes buffer failed", result);
        }
    }

    /**
     * Write a data with default encoding (0) and kind (0), using a Z_STREAM_DATA message.
     * @param data the data
     * @throws ZNetException if write fails.
     */
    public void streamData(ByteBuffer data) throws ZNetException {
        int result = zenohc.z_stream_data(pub, data);
        if (result != 0) {
            throw new ZNetException("z_stream_data of "+data.capacity()+" bytes buffer failed", result);
        }
    }

    /**
     * Write a data with specified encoding and kind, using a Z_STREAM_DATA message.
     * @param data the data
     * @param encoding the data encoding.
     * @param kind the data kind.
     * @throws ZNetException if write fails.
     */
    public void streamData(ByteBuffer data, short encoding, short kind) throws ZNetException {
        int result = zenohc.z_stream_data_wo(pub, data, encoding, kind);
        if (result != 0) {
            throw new ZNetException("z_stream_data of "+data.capacity()+" bytes buffer failed", result);
        }
    }

}