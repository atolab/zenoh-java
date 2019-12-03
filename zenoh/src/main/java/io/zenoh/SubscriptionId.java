package io.zenoh;

import io.zenoh.net.Subscriber;

public final class SubscriptionId {

    private Subscriber sub;

    protected SubscriptionId(Subscriber sub) {
        this.sub = sub;
    }

    protected Subscriber getZSubscriber() {
        return sub;
    }
}
