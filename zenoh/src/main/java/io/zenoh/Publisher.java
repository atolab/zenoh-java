package io.zenoh;

import java.nio.ByteBuffer;

import io.zenoh.swig.z_pub_t;
import io.zenoh.swig.zenohc;

public class Publisher {

    private z_pub_t pub;

    protected Publisher(z_pub_t pub) {
        this.pub = pub;
    }

    public void streamData(ByteBuffer data) throws ZException {
        int result = zenohc.z_stream_data(pub, data);
        if (result != 0) {
            throw new ZException("z_stream_data of "+data.capacity()+" bytes buffer failed", result);
        }
}
    
}