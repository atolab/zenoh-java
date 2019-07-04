package io.zenoh.swig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.zenoh.ReplyCallback;
import io.zenoh.ReplyValue;

public class JNIReplyCallback {

    public static final Logger LOG = LoggerFactory.getLogger("io.zenoh.swig");

    private ReplyCallback cb;

    public JNIReplyCallback(ReplyCallback cb) {
        this.cb = cb;
    }

    protected void handle(ReplyValue reply) {
        cb.handle(reply);
    }
}