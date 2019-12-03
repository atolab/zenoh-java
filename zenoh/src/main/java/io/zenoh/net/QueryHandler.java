package io.zenoh.net;


/**
 * A callback interface to be implemented for handling of queries.
 */
public interface QueryHandler {

    /**
     * The method that will be called on reception of query matching the stored/evaluated resource. 
     * The implementation must provide the data matching the resource selector *rname* by calling 
     * the {@link RepliesSender#sendReplies} method with the data to provide. The {@link RepliesSender#sendReplies}
     * method MUST be called but accepts empty data array. This call can be made in the current Thread
     * or in a different Thread.
     * @param rname the queried resource selector.
     * @param predicate a string provided by the querier refining the data to be provided.
     * @param repliesSender a {@link RepliesSender} implementation. 
     */
    public void handleQuery(String rname, String predicate, RepliesSender repliesSender);

}