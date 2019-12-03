package io.zenoh.net;

import io.zenoh.swig.z_query_dest_t;

/**
 * An object defining which storages or evals should be destination of a query
 * (see {@link Zenoh#query(String, String, ReplyCallback, QueryDest, QueryDest)}).
 */
public class QueryDest extends z_query_dest_t {

    /**
     * The Query destination kind.
     */
    public enum Kind { 
        /**
         * The nearest complete storage/eval if there is one, all storages/evals if not.
         */
        Z_BEST_MATCH((short)0),

        /**
         * Only complete storages/evals. 
         */
        Z_COMPLETE((short)1),

        /**
         * All storages/evals.
         */
        Z_ALL((short)2),

        /**
         * no storages/evals.
         */
        Z_NONE((short)3);

        private short numVal;

        Kind(short numVal)
        {
           this.numVal = numVal;
        }
     
        public static Kind fromInt(short numVal) throws ZNetException {
            if (numVal == Z_BEST_MATCH.value()) {
                return Z_BEST_MATCH;
            }
            else if (numVal == Z_COMPLETE.value()) {
                return Z_COMPLETE;
            }
            else if (numVal == Z_ALL.value()) {
                return Z_ALL;
            }
            else if (numVal == Z_NONE.value()) {
                return Z_NONE;
            }
            else {
                throw new ZNetException("INTERNAL ERROR: cannot create QueryDest.Kind from int: "+numVal);
            }
        }

        public short value()
        {
           return numVal;
        }
    }

    private static QueryDest BEST_MATCH = new QueryDest(Kind.Z_BEST_MATCH);
    private static QueryDest COMPLETE = new QueryDest(Kind.Z_COMPLETE);
    private static QueryDest ALL = new QueryDest(Kind.Z_ALL);
    private static QueryDest NONE = new QueryDest(Kind.Z_NONE);

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
     * @return a {@link QueryDest} with kind {@link Kind#Z_BEST_MATCH}.
     */
    public static QueryDest bestMatch() {
        return BEST_MATCH;
    }

    /**
     * @return a {@link QueryDest} with kind {@link Kind#Z_COMPLETE}.
     */
    public static QueryDest complete() {
        return COMPLETE;
    }

    /**
     * @return a {@link QueryDest} with kind {@link Kind#Z_COMPLETE}.
     */
    public static QueryDest complete(short nb) {
        return new QueryDest(Kind.Z_COMPLETE, nb);
    }

    /**
     * @return a {@link QueryDest} with kind {@link Kind#Z_ALL}.
     */
    public static QueryDest all() {
        return ALL;
    }

    /**
     * @return a {@link QueryDest} with kind {@link Kind#Z_NONE}.
     */
    public static QueryDest none() {
        return NONE;
    }
}
