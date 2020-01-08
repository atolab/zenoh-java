package io.zenoh.net;


import io.zenoh.core.ZException;
import io.zenoh.swig.zenohc;
import io.zenoh.swig.zn_sub_t;

/**
 * A Subscriber (see {@link Session#declareSubscriber(String, SubMode, SubscriberCallback)}).
 */
public class Subscriber {

    private zn_sub_t sub;

    protected Subscriber(zn_sub_t sub) {
        this.sub = sub;
    }

    /**
     * Pull data for the {@link SubMode.Kind#ZN_PULL_MODE} or {@link SubMode.Kind#ZN_PERIODIC_PULL_MODE} subscribtion.
     * The pulled data will be provided by calling the {@link DataHandler#handleData(String, java.nio.ByteBuffer, DataInfo)}
     * function provided to the {@link Session#declareSubscriber(String, SubMode, DataHandler)} function.
     * @throws ZException if pull failed.
     */
    public void pull() throws ZException {
        int result = zenohc.zn_pull(sub);
        if (result != 0) {
            throw new ZException("zn_pull failed", result);
        }
    }

    /**
     * Undeclare the Subscriber.
     * @throws ZException if undeclaration failed.
     */
    public void undeclare() throws ZException {
        int error = zenohc.zn_undeclare_subscriber(sub);
        if (error != 0) {
            throw new ZException("zn_undeclare_subscriber failed ", error);
        }
    }

}