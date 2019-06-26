package io.zenoh;


import io.zenoh.swig.z_sub_t;

public class Subscriber {

    private z_sub_t sub;

    protected Subscriber(z_sub_t sub) {
        this.sub = sub;
    }

}