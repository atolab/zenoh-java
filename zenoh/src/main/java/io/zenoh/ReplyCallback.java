package io.zenoh;

import io.zenoh.swig.z_reply_value_t;

public interface ReplyCallback {

    public void handle(z_reply_value_t reply);

}