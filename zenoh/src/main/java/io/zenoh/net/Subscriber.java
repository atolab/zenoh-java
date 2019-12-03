package io.zenoh.net;


import io.zenoh.swig.zenohc;
import io.zenoh.swig.z_sub_t;

/**
 * A Subscriber (see {@link Zenoh#declareSubscriber(String, SubMode, SubscriberCallback)}).
 */
public class Subscriber {

    private z_sub_t sub;

    protected Subscriber(z_sub_t sub) {
        this.sub = sub;
    }

    /**
     * Retrives data for a given pull-mode subscription from 
     * the nearest infrastruture component (router).
     * @throws ZException if pull failed.
     */
    public void pull() throws ZException {
        int result = zenohc.z_pull(sub);
        if (result != 0) {
            throw new ZException("z_pull failed", result);
        }
    }

    /**
     * Undeclare the Subscriber.
     * @throws ZException if undeclaration failed.
     */
    public void undeclare() throws ZException {
        int error = zenohc.z_undeclare_subscriber(sub);
        if (error != 0) {
            throw new ZException("z_undeclare_subscriber failed ", error);
        }
    }

}