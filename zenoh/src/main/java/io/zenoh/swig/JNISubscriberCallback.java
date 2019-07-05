package io.zenoh.swig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.ByteBuffer;

import io.zenoh.SubscriberCallback;
import io.zenoh.DataInfo;

public class JNISubscriberCallback {

    public static final Logger LOG = LoggerFactory.getLogger("io.zenoh.swig");

    private SubscriberCallback cb;

    public JNISubscriberCallback(SubscriberCallback cb) {
        this.cb = cb;
    }

    protected void handle(String rname, ByteBuffer data, DataInfo info) {
        cb.handle(rname, data, info);
    }

}