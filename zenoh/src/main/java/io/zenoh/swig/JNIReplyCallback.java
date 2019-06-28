package io.zenoh.swig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.zenoh.ReplyCallback;

public class JNIReplyCallback {

    public static final Logger LOG = LoggerFactory.getLogger("io.zenoh.swig");

    private ReplyCallback cb;

    public JNIReplyCallback(ReplyCallback cb) {
        this.cb = cb;
    }

    protected void handle(long replyPtr) {
        z_reply_value_t reply = new z_reply_value_t(replyPtr, false);
        cb.handle(reply);
    }
}