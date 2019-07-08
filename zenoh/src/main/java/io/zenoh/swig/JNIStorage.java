
package io.zenoh.swig;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.zenoh.Storage;
import io.zenoh.DataInfo;
import io.zenoh.Resource;

public class JNIStorage {

    public static final Logger LOG = LoggerFactory.getLogger("io.zenoh.swig");

    private Storage sto;

    public JNIStorage(Storage sto) {
        this.sto = sto;
    }

    public void subscriberCallback(String rname, ByteBuffer data, DataInfo info) {
        sto.subscriberCallback(rname, data, info);
    }

    public Resource[] queryHandler(String rname, String predicate) {
        return sto.queryHandler(rname, predicate);
    }

    public void repliesCleaner(Resource[] replies) {
        sto.repliesCleaner(replies);
    }

}