package io.zenoh;

import org.scijava.nativelib.NativeLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.zenoh.swig.SWIGTYPE_p_z_zenoh_t;
import io.zenoh.swig.result_kind;
import io.zenoh.swig.z_pub_p_result_t;
import io.zenoh.swig.z_sto_p_result_t;
import io.zenoh.swig.z_sub_p_result_t;
import io.zenoh.swig.z_zenoh_p_result_t;
import io.zenoh.swig.zenohc;


/**
 * The Zenoh client API.
 */
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

    /**
     * Open a connection to a Zenoh broker.
     * @param locator the Zenoh broker's locator.
     * @return a Zenoh object to be used for requests on the Zenoh broker.
     * @throws ZException if connection fails.
     */
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

    /**
     * Declares a Publisher on a resource
     * @param resource the resource published by the Publisher
     * @return the Publisher to be used for publications.
     * @throws ZException if declaration fails.
     */
    public Publisher declarePublisher(String resource) throws ZException {
        LOG.debug("Call z_declare_publisher for {}", resource);
        z_pub_p_result_t pub_result = zenohc.z_declare_publisher(z, resource);
        if (pub_result.getTag().equals(result_kind.Z_ERROR_TAG)) {
            throw new ZException("z_declare_publisher on "+resource+" failed ", pub_result.getValue().getError());
        }
        return new Publisher(pub_result.getValue().getPub());
    }

    /**
     * Declares a Subscriber on a resource
     * @param resource the resource subscribed by the Subscriber.
     * @param mode the subscription mode.
     * @param callback the Subscriber's callback.
     * @return the Subscriber.
     * @throws ZException if declaration fails.
     */
    public Subscriber declareSubscriber(String resource, SubMode mode, SubscriberCallback callback) throws ZException {
        LOG.debug("Call z_declare_subscriber for {}", resource);
        z_sub_p_result_t sub_result = zenohc.z_declare_subscriber(z, resource, mode, callback);
        if (sub_result.getTag().equals(result_kind.Z_ERROR_TAG)) {
            throw new ZException("z_declare_subscriber on "+resource+" failed ", sub_result.getValue().getError());
        }
        return new Subscriber(sub_result.getValue().getSub());
    }

    /**
     * Declares a Storage on a resource
     * @param resource the resource stored by the Storage.
     * @param storage the Storage implementation.
     * @throws ZException if declaration fails.
     */
    public void declareStorage(String resource, Storage storage)
        throws ZException
    {
        LOG.debug("Call z_declare_storage for {}", resource);
        z_sto_p_result_t sto_result = zenohc.z_declare_storage(z, resource, storage);
        if (sto_result.getTag().equals(result_kind.Z_ERROR_TAG)) {
            throw new ZException("z_declare_subscriber on "+resource+" failed ", sto_result.getValue().getError());
        }
        storage.setZSto(sto_result.getValue().getSto());
    }

    /**
     * Write a data with default encoding (0) and kind (0) for a resource, using a Z_WRITE_DATA message.
     * @param resource the resource.
     * @param payload the data.
     * @throws ZException if write fails.
     */
    public void writeData(String resource, java.nio.ByteBuffer payload) throws ZException {
        int result = zenohc.z_write_data(z, resource, payload);
        if (result != 0) {
            throw new ZException("z_write_data of "+payload.capacity()+" bytes buffer on "+resource+"failed", result);
        }
    }

    /**
     * Write a data with specified encoding and kind for a resource, using a Z_WRITE_DATA message.
     * @param resource the resource.
     * @param payload the data.
     * @param encoding the data encoding.
     * @param kind the data kind.
     * @throws ZException if write fails.
     */
    public void writeData(String resource, java.nio.ByteBuffer payload, short encoding, short kind) throws ZException {
        int result = zenohc.z_write_data_wo(z, resource, payload, encoding, kind);
        if (result != 0) {
            throw new ZException("z_write_data_wo of "+payload.capacity()+" bytes buffer on "+resource+"failed", result);
        }
    }

    /**
     * Query a resource with a predicate.
     * @param resource the queried resource.
     * @param predicate the predicate.
     * @param callback the callback that will be called for the replies.
     * @throws ZException
     */
    public void query(String resource, String predicate, ReplyCallback callback) throws ZException {
        int result = zenohc.z_query(z, resource, predicate, callback);
        if (result != 0) {
            throw new ZException("z_query on "+resource+" failed", result);
        }
    }
}
