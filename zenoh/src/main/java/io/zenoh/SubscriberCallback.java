package io.zenoh;


import java.nio.ByteBuffer;

public interface SubscriberCallback {

    public void handle(String rname, ByteBuffer data, DataInfo info);

}