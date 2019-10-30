package io.zenoh;


import java.nio.ByteBuffer;

/**
 * A callback interface to be implemented for the reception of subscribed resources.
 * See {@link Zenoh#declareSubscriber(String, SubMode, DataHandler)}.
 */
public interface DataHandler {

    /**
     * The method that will be called on reception of data matching the subscribed or stored resource.
     * @param rname the resource name of the received data.
     * @param data the received data.
     * @param info the {@link DataInfo} associated to the received data.
     */
    public void handleData(String rname, ByteBuffer data, DataInfo info);

}
