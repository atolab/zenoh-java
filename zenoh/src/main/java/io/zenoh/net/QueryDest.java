package io.zenoh.net;

import io.zenoh.ZException;
import io.zenoh.swig.zn_query_dest_t;

/**
 * An object defining which storages or evals should be destination of a query
 * (see {@link Zenoh#query(String, String, ReplyCallback, QueryDest, QueryDest)}).
 */
public class QueryDest extends zn_query_dest_t {

    /**
     * The Query destination kind.
     */
    public enum Kind { 
        /**
         * The nearest complete storage/eval if there is one, all storages/evals if not.
         */
        ZN_BEST_MATCH((short)0),

        /**
         * Only complete storages/evals. 
         */
        ZN_COMPLETE((short)1),

        /**
         * All storages/evals.
         */
        ZN_ALL((short)2),

        /**
         * no storages/evals.
         */
        ZN_NONE((short)3);

        private short numVal;

        Kind(short numVal)
        {
           this.numVal = numVal;
        }
     
        public static Kind fromInt(short numVal) throws ZException {
            if (numVal == ZN_BEST_MATCH.value()) {
                return ZN_BEST_MATCH;
            }
            else if (numVal == ZN_COMPLETE.value()) {
                return ZN_COMPLETE;
            }
            else if (numVal == ZN_ALL.value()) {
                return ZN_ALL;
            }
            else if (numVal == ZN_NONE.value()) {
                return ZN_NONE;
            }
            else {
                throw new ZException("INTERNAL ERROR: cannot create QueryDest.Kind from int: "+numVal);
            }
        }

        public short value()
        {
           return numVal;
        }
    }

    private static QueryDest BEST_MATCH = new QueryDest(Kind.ZN_BEST_MATCH);
    private static QueryDest COMPLETE = new QueryDest(Kind.ZN_COMPLETE);
    private static QueryDest ALL = new QueryDest(Kind.ZN_ALL);
    private static QueryDest NONE = new QueryDest(Kind.ZN_NONE);

    private QueryDest(Kind kind) {
        super();
        setKind(kind.value());
        setNb((short)1);
    }

    private QueryDest(Kind kind, short nb) {
        super();
        setKind(kind.value());
        setNb(nb);
    }

    /**
     * @return a {@link QueryDest} with kind {@link Kind#ZN_BEST_MATCH}.
     */
    public static QueryDest bestMatch() {
        return BEST_MATCH;
    }

    /**
     * @return a {@link QueryDest} with kind {@link Kind#ZN_COMPLETE}.
     */
    public static QueryDest complete() {
        return COMPLETE;
    }

    /**
     * @return a {@link QueryDest} with kind {@link Kind#ZN_COMPLETE}.
     */
    public static QueryDest complete(short nb) {
        return new QueryDest(Kind.ZN_COMPLETE, nb);
    }

    /**
     * @return a {@link QueryDest} with kind {@link Kind#ZN_ALL}.
     */
    public static QueryDest all() {
        return ALL;
    }

    /**
     * @return a {@link QueryDest} with kind {@link Kind#ZN_NONE}.
     */
    public static QueryDest none() {
        return NONE;
    }
}
