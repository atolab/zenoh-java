package io.zenoh;


import java.nio.ByteBuffer;

import io.zenoh.swig.z_sto_t;

public abstract class Storage {

    public abstract void subscriberCallback(String rname, ByteBuffer data, DataInfo info);

    public abstract Resource[] queryHandler(String rname, String predicate);

    public abstract void repliesCleaner(Resource[] replies);

    // For internal management

    private z_sto_t zsto = null;

    protected void setZSto(z_sto_t zsto) {
        this.zsto = zsto;
    }

    protected z_sto_t getZSto(z_sto_t zsto) {
        return zsto;
    }

}