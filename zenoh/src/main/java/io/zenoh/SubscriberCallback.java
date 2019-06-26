package io.zenoh;


import java.nio.ByteBuffer;

import io.zenoh.swig.z_data_info_t;
import io.zenoh.swig.z_resource_id_t;

public interface SubscriberCallback {

    public void handle(z_resource_id_t rid, ByteBuffer data, z_data_info_t info);

}