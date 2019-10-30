package io.zenoh;

/**
 * A callback interface to be implemented for the reception of replies for a query.
 * See {@link Zenoh#query(String, String, ReplyHandler)}.
 */
public interface ReplyHandler {

    /**
     * The method that will be called on reception of replies to the query sent by 
     * {@link Zenoh#query(String, String, ReplyHandler)} or {@link Zenoh#query(String, String, ReplyHandler, QueryDest, QueryDest)}. 
     * Note that the last message will have a kind set to {@link io.zenoh.ReplyValue.Kind#Z_STORAGE_FINAL}. 
     * @param reply a reply
     */
    public void handleReply(ReplyValue reply);

}
