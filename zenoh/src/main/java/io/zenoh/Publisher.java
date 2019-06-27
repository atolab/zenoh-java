package io.zenoh;

import java.nio.ByteBuffer;

import io.zenoh.swig.z_pub_t;
import io.zenoh.swig.zenohc;

public class Publisher {

    private z_pub_t pub;

    protected Publisher(z_pub_t pub) {
        this.pub = pub;
    }

    public void streamCompactData(ByteBuffer payload) throws ZException {
        int result = zenohc.z_stream_compact_data(pub, payload);
        if (result != 0) {
            throw new ZException("z_stream_compact_data of "+payload.capacity()+" bytes buffer failed", result);
        }
    }

    public void streamData(ByteBuffer payload) throws ZException {
        int result = zenohc.z_stream_data(pub, payload);
        if (result != 0) {
            throw new ZException("z_stream_data of "+payload.capacity()+" bytes buffer failed", result);
        }
    }

    public void streamData(ByteBuffer payload, short encoding, short kind) throws ZException {
        int result = zenohc.z_stream_data_wo(pub, payload, encoding, kind);
        if (result != 0) {
            throw new ZException("z_stream_data of "+payload.capacity()+" bytes buffer failed", result);
        }
    }

}