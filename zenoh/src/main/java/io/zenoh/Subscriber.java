package io.zenoh;


import java.nio.ByteBuffer;

import io.zenoh.swig.z_data_info_t;
import io.zenoh.swig.z_resource_id_t;
import io.zenoh.swig.z_sub_t;

public class Subscriber {

    public interface Callback {
        public void handle(z_resource_id_t rid, ByteBuffer data, z_data_info_t info);
    }

    private z_sub_t sub;

    protected Subscriber(z_sub_t sub) {
        this.sub = sub;
    }

}