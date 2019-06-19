package io.zenoh;

public class ZException extends Exception {

    private int errorCode;

    private static final long serialVersionUID = 402535504102337839L;
    
    public ZException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ZException(String message, int errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String toString() {
        return super.toString() + " (error code:" + this.errorCode + ")";
    }
}