package io.zenoh;


import java.nio.ByteBuffer;

/**
 * A callback interface to be implemented for the reception of subscribed resources.
 * See {@link Zenoh#declareSubscriber(String, SubMode, DataHandler)}.
 */
public interface DataHandler {

    /**
     * The method that will be called for each write of resource matching the subscription.
     * @param rname the resource name.
     * @param data the resrouce value.
     * @param info the DataInfo associated to the resource.
     */
    public void handleData(String rname, ByteBuffer data, DataInfo info);

}
