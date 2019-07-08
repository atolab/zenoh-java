package io.zenoh;


import java.nio.ByteBuffer;

import io.zenoh.swig.z_sto_t;

/**
 * The interface of a Storage.
 */
public abstract class Storage {

    /**
     * The callback called for each write matching the storage's resource.
     * @param rname the written resource name.
     * @param data the written resource data.
     * @param info the DataInfo associated to the ritten resource.
     */
    public abstract void subscriberCallback(String rname, ByteBuffer data, DataInfo info);

    /**
     * The callback called for each query that matvches (partially or totally) the storage's resource
     * (see {@link Zenoh#query(String, String, ReplyCallback)})
     * @param rname the queried resource.
     * @param predicate the query predicate.
     */
    public abstract Resource[] queryHandler(String rname, String predicate);

    /**
     * The callback called after each call to {@link #queryHandler(String, String)},
     * for possible cleanup of Resources created in the queryHandler.
     * @param replies
     */
    public void repliesCleaner(Resource[] replies) {
        // Do nothing by default.
        // Overwrite this method if required.
    }

    // For internal management

    private z_sto_t zsto = null;

    protected void setZSto(z_sto_t zsto) {
        this.zsto = zsto;
    }

    protected z_sto_t getZSto(z_sto_t zsto) {
        return zsto;
    }

}