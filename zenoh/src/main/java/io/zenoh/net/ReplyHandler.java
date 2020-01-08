package io.zenoh.net;

/**
 * A callback interface to be implemented for the reception of replies for a query.
 * See {@link Session#query(String, String, ReplyHandler)} and
 * {@link Session#query(String, String, ReplyHandler, QueryDest, QueryDest)}.
 */
public interface ReplyHandler {

    /**
     * The method that will be called on reception of replies to the query sent by 
     * {@link Session#query(String, String, ReplyHandler)} or {@link Session#query(String, String, ReplyHandler, QueryDest, QueryDest)}. 
     * @param reply is the actual reply.
     */
    public void handleReply(ReplyValue reply);

}
