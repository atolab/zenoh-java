package io.zenoh;

import io.zenoh.swig.z_query_dest_t;

/**
 * Query destination mode (used in {@link Zenoh#query(String, String, ReplyCallback, QueryDest, QueryDest)}).
 */
public class QueryDest extends z_query_dest_t {

    /**
     * The Query destination kind.
     */
    public enum Kind { 

        Z_BEST_MATCH((short)0),
        Z_COMPLETE((short)1),
        Z_ALL((short)2),
        Z_NONE((short)3);

        private short numVal;

        Kind(short numVal)
        {
           this.numVal = numVal;
        }
     
        public static Kind fromInt(short numVal) throws ZException {
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
                throw new ZException("INTERNAL ERROR: cannot create QueryDest.Kind from int: "+numVal);
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
     * Return the bestMatch query destination.
     */
    public static QueryDest bestMatch() {
        return BEST_MATCH;
    }

    /**
     * Return the complete query destination with 1 destination.
     */
    public static QueryDest complete() {
        return COMPLETE;
    }

    /**
     * Return the complete query destination with the specified number of destinations.
     */
    public static QueryDest complete(short nb) {
        return new QueryDest(Kind.Z_COMPLETE, nb);
    }

    /**
     * Return the all query destination.
     */
    public static QueryDest all() {
        return ALL;
    }

    /**
     * Return the none query destination.
     */
    public static QueryDest none() {
        return NONE;
    }
}
