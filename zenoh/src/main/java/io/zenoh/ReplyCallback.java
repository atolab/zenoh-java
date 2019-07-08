package io.zenoh;

/**
 * A callback interface to be implemented for the reception of replies for a query.
 * See {@link Zenoh#query(String, String, ReplyCallback)}.
 */
public interface ReplyCallback {

    /**
     * The method that will be called with all replies messages for a query.
     * Note that the last message will have a kind set to {@link io.zenoh.ReplyValue.Kind#Z_STORAGE_FINAL}. 
     * @param reply a reply
     */
    public void handle(ReplyValue reply);

}
