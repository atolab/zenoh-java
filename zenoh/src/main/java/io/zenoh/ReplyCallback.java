package io.zenoh;

public interface ReplyCallback {

    public void handle(ReplyValue reply);

}
