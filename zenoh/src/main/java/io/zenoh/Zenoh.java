package io.zenoh;

import java.io.IOException;

import org.scijava.nativelib.NativeLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.zenoh.swig.SWIGTYPE_p_z_zenoh_t;
import io.zenoh.swig.result_kind;
import io.zenoh.swig.z_pub_p_result_t;
import io.zenoh.swig.z_sto_p_result_t;
import io.zenoh.swig.z_sub_mode_t;
import io.zenoh.swig.z_sub_p_result_t;
import io.zenoh.swig.z_zenoh_p_result_t;
import io.zenoh.swig.zenohc;
import io.zenoh.swig.JNIReplyCallback;
import io.zenoh.swig.JNIStorage;
import io.zenoh.swig.JNISubscriberCallback;


public class Zenoh {

    public static final char Z_STORAGE_DATA  = 0;
    public static final char Z_STORAGE_FINAL = 1;
    public static final char Z_REPLY_FINAL   = 2;

   
    private static final Logger LOG = LoggerFactory.getLogger("io.zenoh");

    static {
        try {
            LOG.debug("Load native library: zenohc_java");
            NativeLoader.loadLibrary("zenohc_java");
        } catch (Exception e) {
            LOG.error("Failed to load native library zenohc_java: {}", e.toString());
            if (LOG.isDebugEnabled()) {
                e.printStackTrace();
            }
            System.exit(-1);
        }
    }

    private SWIGTYPE_p_z_zenoh_t z;

    private Zenoh(SWIGTYPE_p_z_zenoh_t z) {
        this.z = z;
    }

    public static Zenoh open(String locator) throws ZException {
        LOG.debug("Call z_open on {}", locator);
        z_zenoh_p_result_t zenoh_result = zenohc.z_open(locator, null);
        if (zenoh_result.getTag().equals(result_kind.Z_ERROR_TAG)) {
            throw new ZException("z_open on " + locator + " failed", zenoh_result.getValue().getError());
        }
        SWIGTYPE_p_z_zenoh_t z = zenoh_result.getValue().getZenoh();

        LOG.debug("Call z_start_recv_loop");
        int loop_result = zenohc.z_start_recv_loop(z);
        if (loop_result != 0) {
            throw new ZException("z_start_recv_loop failed", loop_result);
        }

        return new Zenoh(z);
    }

    public Publisher declarePublisher(String resource) throws ZException {
        LOG.debug("Call z_declare_publisher for {}", resource);
        z_pub_p_result_t pub_result = zenohc.z_declare_publisher(z, resource);
        if (pub_result.getTag().equals(result_kind.Z_ERROR_TAG)) {
            throw new ZException("z_declare_publisher on "+resource+" failed ", pub_result.getValue().getError());
        }
        return new Publisher(pub_result.getValue().getPub());
    }

    public Subscriber declareSubscriber(String resource, z_sub_mode_t mode, SubscriberCallback callback) throws ZException {
        LOG.debug("Call z_declare_subscriber for {}", resource);
        z_sub_p_result_t sub_result = zenohc.z_declare_subscriber(z, resource, mode, new JNISubscriberCallback(callback));
        if (sub_result.getTag().equals(result_kind.Z_ERROR_TAG)) {
            throw new ZException("z_declare_subscriber on "+resource+" failed ", sub_result.getValue().getError());
        }
        return new Subscriber(sub_result.getValue().getSub());
    }

    public void declareStorage(String resource, Storage storage)
        throws ZException
    {
        LOG.debug("Call z_declare_storage for {}", resource);
        z_sto_p_result_t sto_result = zenohc.z_declare_storage(z, resource, new JNIStorage(storage));
        if (sto_result.getTag().equals(result_kind.Z_ERROR_TAG)) {
            throw new ZException("z_declare_subscriber on "+resource+" failed ", sto_result.getValue().getError());
        }
        storage.setZSto(sto_result.getValue().getSto());
    }

    public void writeData(String resource, java.nio.ByteBuffer payload) throws ZException {
        int result = zenohc.z_write_data(z, resource, payload);
        if (result != 0) {
            throw new ZException("z_write_data of "+payload.capacity()+" bytes buffer on "+resource+"failed", result);
        }
    }

    public void writeData(String resource, java.nio.ByteBuffer payload, short encoding, short kind) throws ZException {
        int result = zenohc.z_write_data_wo(z, resource, payload, encoding, kind);
        if (result != 0) {
            throw new ZException("z_write_data_wo of "+payload.capacity()+" bytes buffer on "+resource+"failed", result);
        }
    }

    public void query(String resource, String predicate, ReplyCallback callback) throws ZException {
        int result = zenohc.z_query(z, resource, predicate, new JNIReplyCallback(callback));
        if (result != 0) {
            throw new ZException("z_query on "+resource+" failed", result);
        }

    }
}
