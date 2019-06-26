package io.zenoh.swig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.ByteBuffer;

import io.zenoh.SubscriberCallback;

public class JNISubscriberCallback {

    public static final Logger LOG = LoggerFactory.getLogger("io.zenoh.swig");

    private SubscriberCallback cb;

    public JNISubscriberCallback(SubscriberCallback cb) {
        this.cb = cb;
    }

    protected void handle(long ridPtr, ByteBuffer data, long infoPtr) {
        z_resource_id_t rid = new z_resource_id_t(ridPtr, true);
        z_data_info_t info = new z_data_info_t(infoPtr, true);
        cb.handle(rid, data, info);
    }
}