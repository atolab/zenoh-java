package io.zenoh.net;

import java.nio.ByteBuffer;

import io.zenoh.swig.zn_pub_t;
import io.zenoh.ZException;
import io.zenoh.swig.zenohc;

/**
 * A Publisher (see {@link Zenoh#declarePublisher(String)}).
 */
public class Publisher {

    private zn_pub_t pub;

    protected Publisher(zn_pub_t pub) {
        this.pub = pub;
    }

    /**
     * Undeclare the Publisher.
     * @throws ZException if undeclaration failed.
     */
    public void undeclare() throws ZException {
        int error = zenohc.zn_undeclare_publisher(pub);
        if (error != 0) {
            throw new ZException("zn_undeclare_publisher failed ", error);
        }
    }

    /**
     * Write a data with default encoding (0) and kind (0), using a ZN_COMPACT_DATA message.
     * @param data the data
     * @throws ZException if write fails.
     */
    public void streamCompactData(ByteBuffer data) throws ZException {
        int result = zenohc.zn_stream_compact_data(pub, data);
        if (result != 0) {
            throw new ZException("zn_stream_compact_data of "+data.capacity()+" bytes buffer failed", result);
        }
    }

    /**
     * Write a data with default encoding (0) and kind (0), using a ZN_STREAM_DATA message.
     * @param data the data
     * @throws ZException if write fails.
     */
    public void streamData(ByteBuffer data) throws ZException {
        int result = zenohc.zn_stream_data(pub, data);
        if (result != 0) {
            throw new ZException("zn_stream_data of "+data.capacity()+" bytes buffer failed", result);
        }
    }

    /**
     * Write a data with specified encoding and kind, using a ZN_STREAM_DATA message.
     * @param data the data
     * @param encoding the data encoding.
     * @param kind the data kind.
     * @throws ZException if write fails.
     */
    public void streamData(ByteBuffer data, short encoding, short kind) throws ZException {
        int result = zenohc.zn_stream_data_wo(pub, data, encoding, kind);
        if (result != 0) {
            throw new ZException("zn_stream_data of "+data.capacity()+" bytes buffer failed", result);
        }
    }

}