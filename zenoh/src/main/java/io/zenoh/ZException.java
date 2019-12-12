package io.zenoh;

public class ZException extends Exception {

    private static final long serialVersionUID = 8123269272149744936L;

    protected ZException(String message) {
        super(message);
    }

    protected ZException(String message, Throwable cause) {
        super(message, cause);
    }

}
