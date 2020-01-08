package io.zenoh.net;


import java.nio.ByteBuffer;

/**
 * A callback interface to be implemented for the reception of subscribed/stored resources.
 * See {@link Session#declareSubscriber(String, SubMode, DataHandler)} and
 * {@link Session#declareStorage(String, StorageHandler)}.
 */
public interface DataHandler {

    /**
     * The method that will be called on reception of data matching the subscribed or stored resource.
     * @param rname the resource name of the received data.
     * @param data the received data.
     * @param info the {@link DataInfo} associated with the received data.
     */
    public void handleData(String rname, ByteBuffer data, DataInfo info);

}
