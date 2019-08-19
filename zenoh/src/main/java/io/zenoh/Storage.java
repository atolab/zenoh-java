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
     * The callback called for each query that matches (partially or totally) the storage's resource
     * (see {@link Zenoh#query(String, String, ReplyCallback)}).
     * The Storage implementer shall call the {@link RepliesSender#sendReplies(Resource[])} operation
     * to return the results of the query. This call can be made in the current Thread or in a different Thread.
     * @param rname the queried resource.
     * @param predicate the query predicate.
     * @param repliesSender the RepliesSender object to be used for sending replies to the query.
     */
    public abstract void queryHandler(String rname, String predicate, RepliesSender repliesSender);


    // For internal management

    private z_sto_t zsto = null;

    protected void setZSto(z_sto_t zsto) {
        this.zsto = zsto;
    }

    protected z_sto_t getZSto(z_sto_t zsto) {
        return zsto;
    }

}