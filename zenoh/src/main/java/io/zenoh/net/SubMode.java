package io.zenoh.net;

import io.zenoh.swig.z_temporal_property_t;

/**
 * Subscription mode (used in {@link Zenoh#declareSubscriber(String, SubMode, SubscriberCallback)}).
 */
public class SubMode extends io.zenoh.swig.z_sub_mode_t {

    /**
     * The subscription mode kind.
     */
    public enum Kind { 
        Z_PUSH_MODE((short)1),
        Z_PULL_MODE((short)2),
        Z_PERIODIC_PUSH_MODE((short)3),
        Z_PERIODIC_PULL_MODE((short)4);

        private short numVal;

        Kind(short numVal)
        {
           this.numVal = numVal;
        }
     
        public static Kind fromInt(short numVal) throws ZException {
            if (numVal == Z_PUSH_MODE.value()) {
                return Z_PUSH_MODE;
            }
            else if (numVal == Z_PULL_MODE.value()) {
                return Z_PULL_MODE;
            }
            else if (numVal == Z_PERIODIC_PUSH_MODE.value()) {
                return Z_PERIODIC_PUSH_MODE;
            }
            else if (numVal == Z_PERIODIC_PULL_MODE.value()) {
                return Z_PERIODIC_PULL_MODE;
            }
            else {
                throw new ZException("INTERNAL ERROR: cannot create SubMode.Kind from int: "+numVal);
            }
        }

        public short value()
        {
           return numVal;
        }
    }

    private static SubMode PUSH_MODE = new SubMode(Kind.Z_PUSH_MODE);
    private static SubMode PULL_MODE = new SubMode(Kind.Z_PULL_MODE);

    private SubMode(Kind kind) {
        super();
        setKind(kind.value());
    }

    private SubMode(Kind kind, int origin, int period, int duration) {
        super();
        setKind(kind.value());
        z_temporal_property_t tprop = new z_temporal_property_t();
        tprop.setOrigin(origin);
        tprop.setPeriod(period);
        tprop.setDuration(duration);
        setTprop(tprop);
    }

    /**
     * @return the push subscription mode.
     */
    public static SubMode push() {
        return PUSH_MODE;
    }

    /**
     * @return the pull subscription mode.
     */
    public static SubMode pull() {
        return PULL_MODE;
    }

    /**
     * @return a periodic push subscription mode with the specified temporal properties.
     */
    public static SubMode periodicPush(int origin, int period, int duration) {
        return new SubMode(Kind.Z_PERIODIC_PUSH_MODE, origin, period, duration);
    }

    /**
     * @return a periodic pull subscription mode with the specified temporal properties.
     */
    public static SubMode periodicPull(int origin, int period, int duration) {
        return new SubMode(Kind.Z_PERIODIC_PULL_MODE, origin, period, duration);
    }
}
