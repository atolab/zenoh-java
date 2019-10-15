package io.zenoh;


/**
 * A callback interface to be implemented by the implementation of Storages and Evals.
 */
public interface QueryHandler {

    /**
     * The callback called for each query that matches (partially or totally) the sotrage/eval's resource
     * (see {@link Zenoh#query(String, String, ReplyHandler)}).
     * The Storage/Eval implementer shall call the {@link RepliesSender#sendReplies(Resource[])} operation
     * to return the results of the query. This call can be made in the current Thread or in a different Thread.
     * @param rname the queried resource.
     * @param predicate the query predicate.
     * @param repliesSender the RepliesSender object to be used for sending replies of the query.
     */
    public void handleQuery(String rname, String predicate, RepliesSender repliesSender);

}