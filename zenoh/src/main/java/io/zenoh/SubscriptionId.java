package io.zenoh;

import io.zenoh.net.Subscriber;

/**
 * The identifier of a subscription.
 * @see Workspace#subscribe(Selector, Listener)
 * @see Workspace#unsubscribe(SubscriptionId)
 */
public final class SubscriptionId {

    private Subscriber sub;

    protected SubscriptionId(Subscriber sub) {
        this.sub = sub;
    }

    protected Subscriber getZSubscriber() {
        return sub;
    }
}
