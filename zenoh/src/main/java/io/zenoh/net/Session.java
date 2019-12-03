package io.zenoh.net;

import org.scijava.nativelib.NativeLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.zenoh.swig.SWIGTYPE_p_z_zenoh_t;
import io.zenoh.swig.result_kind;
import io.zenoh.swig.z_eval_p_result_t;
import io.zenoh.swig.z_pub_p_result_t;
import io.zenoh.swig.z_sto_p_result_t;
import io.zenoh.swig.z_sub_p_result_t;
import io.zenoh.swig.z_zenoh_p_result_t;
import io.zenoh.swig.zenohc;

import java.util.Map;
import java.util.Map.Entry;

/**
 * The Zenoh client API.
 */
public class Session {

    private static final Logger LOG = LoggerFactory.getLogger("io.zenoh");
    private static final Map.Entry<Integer, byte[]>[] EMPTY = new Map.Entry[0];

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

    private Session(SWIGTYPE_p_z_zenoh_t z) {
        this.z = z;
    }

    /**
     * Open a zenoh session. 
     * @param locator a string representing the network endpoint to which establish the session. A typical locator looks like this : ``tcp/127.0.0.1:7447``. 
     * If ``null``, `open` will scout and try to establish the session automatically.
     * @return a Zenoh object representing the openned zenoh session..
     * @throws ZNetException if session etablishment fails.
     */
    public static Session open(String locator) throws ZNetException {
        return open(locator, null);
    }

    /**
     * Open a zenoh session. 
     * @param locator a string representing the network endpoint to which establish the session. A typical locator looks like this : ``tcp/127.0.0.1:7447``. 
     * If ``null``, `open` will scout and try to establish the session automatically.
     * @param properties a map of properties that will be used to establish and configure the zenoh session. 
     * **properties** will typically contain the ``username`` and ``password`` informations needed to establish the zenoh session with a secured infrastructure. 
     * It can be set to ``NULL``. 
     * @return a Zenoh object representing the openned zenoh session..
     * @throws ZNetException if session etablishment fails.
     */
    public static Session open(String locator, Map<Integer, byte[]> properties) throws ZNetException {
        LOG.debug("Call z_open on {}", locator);
        Entry<Integer, byte[]>[] entries = properties != null ? properties.entrySet().toArray(EMPTY) : null;
        z_zenoh_p_result_t zenoh_result = zenohc.z_open(locator,  entries);
        if (zenoh_result.getTag().equals(result_kind.Z_ERROR_TAG)) {
            throw new ZNetException("z_open failed", zenoh_result.getValue().getError());
        }
        SWIGTYPE_p_z_zenoh_t z = zenoh_result.getValue().getZenoh();

        new Thread(new Runnable(){
            @Override
            public void run() {
                {
                    LOG.debug("Run z_recv_loop");
                    zenohc.z_recv_loop(z);
                }
            }
        }).start();

        return new Session(z);
    }

    /**
     * Close the zenoh session.
     * @throws ZNetException if close failed.
     */
    public void close() throws ZNetException {
        LOG.debug("Call z_stop_recv_loop");
        int stop_result = zenohc.z_stop_recv_loop(z);
        if (stop_result != 0) {
            throw new ZNetException("z_stop_recv_loop failed", stop_result);
        }
        int close_result = zenohc.z_close(z);
        if (close_result != 0) {
            throw new ZNetException("close_result failed", stop_result);
        }
    }

    /**
     * @return a map pf properties containing various informations about the established zenoh session.
     */
    public Map<Integer, byte[]> info() {
        return zenohc.z_info(z);
    }

    /**
     * Declare a publication for resource seletor **resource**.
     * @param resource the resource seletor to publish.
     * @return the zenoh {@link Publisher}.
     * @throws ZNetException if declaration fails.
     */
    public Publisher declarePublisher(String resource) throws ZNetException {
        LOG.debug("Call z_declare_publisher for {}", resource);
        z_pub_p_result_t pub_result = zenohc.z_declare_publisher(z, resource);
        if (pub_result.getTag().equals(result_kind.Z_ERROR_TAG)) {
            throw new ZNetException("z_declare_publisher on "+resource+" failed ", pub_result.getValue().getError());
        }
        return new Publisher(pub_result.getValue().getPub());
    }

    /**
     * Declare a subscribtion for all published data matching the provided resource selector **resource**. 
     * @param resource the resource seletor to subscribe to.
     * @param mode the subscription mode.
     * @param handler a {@link DataHandler} subclass implementing the callback function that will be called each time
     * a data matching the subscribed **resource** is received.
     * @return the zenoh {@link Subscriber}.
     * @throws ZNetException if declaration fails.
     */
    public Subscriber declareSubscriber(String resource, SubMode mode, DataHandler handler) throws ZNetException {
        LOG.debug("Call z_declare_subscriber for {}", resource);
        z_sub_p_result_t sub_result = zenohc.z_declare_subscriber(z, resource, mode, handler);
        if (sub_result.getTag().equals(result_kind.Z_ERROR_TAG)) {
            throw new ZNetException("z_declare_subscriber on "+resource+" failed ", sub_result.getValue().getError());
        }
        return new Subscriber(sub_result.getValue().getSub());
    }

    /**
     * Declare a storage for all data matching the provided resource selector **resource**. 
     * @param resource the resource selector to store.
     * @param handler a {@link StorageHandler} subclass implementing the callback functions that will be called each time
     * a data matching the stored **resource** selector is received and each time a query for data matching the
     * stored **resource** selector is received.
     * @return the zenoh {@link Storage}.
     * @throws ZNetException if declaration fails.
     */
    public Storage declareStorage(String resource, StorageHandler handler)
        throws ZNetException
    {
        LOG.debug("Call z_declare_storage for {}", resource);
        z_sto_p_result_t sto_result = zenohc.z_declare_storage(z, resource, handler);
        if (sto_result.getTag().equals(result_kind.Z_ERROR_TAG)) {
            throw new ZNetException("z_declare_subscriber on "+resource+" failed ", sto_result.getValue().getError());
        }
        return new Storage(sto_result.getValue().getSto());
    }

    /**
     * Declare an eval able to provide data matching the provided resource selector **resource**. 
     * @param resource the resource selector to evaluate.
     * @param handler a {@link QueryHandler} subclass implementing the the callback function that will be called each time a
     * query for data matching the evaluated **resource** selector is received.
     * @return the Eval.
     * @throws ZNetException if declaration fails.
     */
    public Eval declareEval(String resource, QueryHandler handler)
        throws ZNetException
    {
        LOG.debug("Call z_declare_eval for {}", resource);
        z_eval_p_result_t eval_result = zenohc.z_declare_eval(z, resource, handler);
        if (eval_result.getTag().equals(result_kind.Z_ERROR_TAG)) {
            throw new ZNetException("z_declare_eval on "+resource+" failed ", eval_result.getValue().getError());
        }
        return new Eval(eval_result.getValue().getEval());
    }

    /**
     * Send data in a *write_data* message for the resource selector **resource**.
     * @param resource the resource selector of the data to be sent.
     * @param payload the data.
     * @throws ZNetException if write fails.
     */
    public void writeData(String resource, java.nio.ByteBuffer payload) throws ZNetException {
        int result = zenohc.z_write_data(z, resource, payload);
        if (result != 0) {
            throw new ZNetException("z_write_data of "+payload.capacity()+" bytes buffer on "+resource+"failed", result);
        }
    }

    /**
     * Send data in a *write_data* message for the resource selector **resource**.
     * @param resource the resource selector of the data to be sent.
     * @param payload the data.
     * @param encoding a metadata information associated with the published data that represents the encoding of the published data. 
     * @param kind a metadata information associated with the published data that represents the kind of publication.
     * @throws ZNetException if write fails.
     */
    public void writeData(String resource, java.nio.ByteBuffer payload, short encoding, short kind) throws ZNetException {
        int result = zenohc.z_write_data_wo(z, resource, payload, encoding, kind);
        if (result != 0) {
            throw new ZNetException("z_write_data_wo of "+payload.capacity()+" bytes buffer on "+resource+"failed", result);
        }
    }

    /**
     * Query data matching resource selector **resource**.
     * @param resource the resource selector to query.
     * @param predicate a string that will be propagated to the storages and evals that should provide the queried data. 
     * It may allow them to filter, transform and/or compute the queried data. .
     * @param handler a {@link ReplyHandler} subclass implementing the callback function that will be called on reception
     * of the replies of the query. 
     * @throws ZNetException if fails.
     */
    public void query(String resource, String predicate, ReplyHandler handler) throws ZNetException {
        query(resource, predicate, handler, QueryDest.bestMatch(), QueryDest.bestMatch());
    }

    /**
     * Query data matching resource selector **resource**.
     * @param resource the resource selector to query.
     * @param predicate a string that will be propagated to the storages and evals that should provide the queried data. 
     * It may allow them to filter, transform and/or compute the queried data. .
     * @param handler a {@link ReplyHandler} subclass implementing the callback function that will be called on reception
     * of the replies of the query.
     * @param dest_storages a {@link QueryDest} indicating which matching storages should be destination of the query.
     * @param dest_evals a {@link QueryDest} indicating which matching evals should be destination of the query.
     * @throws ZNetException if fails.
     */
    public void query(String resource, String predicate, ReplyHandler handler, QueryDest dest_storages, QueryDest dest_evals) throws ZNetException {
        int result = zenohc.z_query_wo(z, resource, predicate, handler, dest_storages, dest_evals);
        if (result != 0) {
            throw new ZNetException("z_query on "+resource+" failed", result);
        }
    }

    protected static void LogException(Throwable e, String message) {
        LOG.warn(message, e);
    }


}
