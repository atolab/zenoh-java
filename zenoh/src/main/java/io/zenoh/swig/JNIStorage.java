
package io.zenoh.swig;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.zenoh.Storage;
import io.zenoh.DataInfo;

public class JNIStorage {

    public static final Logger LOG = LoggerFactory.getLogger("io.zenoh.swig");

    private Storage sto;

    public JNIStorage(Storage sto) {
        this.sto = sto;
    }

    public void subscriberCallback(long ridPtr, ByteBuffer data, DataInfo info) {
        z_resource_id_t rid = new z_resource_id_t(ridPtr, true);
        sto.subscriberCallback(rid, data, info);

    }

    public z_array_z_resource_t queryHandler(String rname, String predicate) {
        return sto.queryHandler(rname, predicate);
    }

    public void repliesCleaner(long repliesPtr) {
        z_array_z_resource_t replies = new z_array_z_resource_t(repliesPtr, true);
        sto.repliesCleaner(replies);
    }

}