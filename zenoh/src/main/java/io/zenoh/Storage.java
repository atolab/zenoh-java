package io.zenoh;


import java.nio.ByteBuffer;

import io.zenoh.swig.z_array_z_resource_t;
import io.zenoh.swig.z_resource_id_t;
import io.zenoh.swig.z_sto_t;

public abstract class Storage {

    public abstract void subscriberCallback(z_resource_id_t rid, ByteBuffer data, DataInfo info);

    public abstract z_array_z_resource_t queryHandler(String rname, String predicate);

    public abstract void repliesCleaner(z_array_z_resource_t replies);

    // For internal management

    private z_sto_t zsto = null;

    protected void setZSto(z_sto_t zsto) {
        this.zsto = zsto;
    }

    protected z_sto_t getZSto(z_sto_t zsto) {
        return zsto;
    }

}