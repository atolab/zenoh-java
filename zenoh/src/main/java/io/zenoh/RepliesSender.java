package io.zenoh;

/**
 * Class to be used in a {@link QueryHandler} implementation to send back replies to a query.
 */
public final class RepliesSender {

    private long send_replies_ptr;
    private long query_handle_ptr;

    private RepliesSender(
        long send_replies_ptr,
        long query_handle_ptr)
    {
        this.send_replies_ptr = send_replies_ptr;
        this.query_handle_ptr = query_handle_ptr;
    }

    /**
     * Send back the replies to the query associated with this {@link RepliesSender} object.
     * @param replies the replies.
     */
    public void sendReplies(Resource[] replies) {
        io.zenoh.swig.zenohc.call_replies_sender(send_replies_ptr, query_handle_ptr, replies);
    }
}
