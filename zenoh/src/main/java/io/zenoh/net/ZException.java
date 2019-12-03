package io.zenoh.net;

import java.util.HashMap;

/**
 * An Exception raised by Zenoh.
 */
public class ZException extends Exception {


    public static final Integer Z_VLE_PARSE_ERROR = 0x01;
    public static final Integer Z_ARRAY_PARSE_ERROR = 0x02;
    public static final Integer Z_STRING_PARSE_ERROR = 0x03;
    public static final Integer Z_PROPERTY_PARSE_ERROR = 0x04;
    public static final Integer Z_PROPERTIES_PARSE_ERROR = 0x05;
    public static final Integer Z_MESSAGE_PARSE_ERROR = 0x06;
    public static final Integer Z_INSUFFICIENT_IOBUF_SIZE = 0x07;
    public static final Integer Z_IO_ERROR = 0x08;
    public static final Integer Z_RESOURCE_DECL_ERROR = 0x09;
    public static final Integer Z_PAYLOAD_HEADER_PARSE_ERROR = 0x0a;
    public static final Integer Z_TX_CONNECTION_ERROR = 0x0b;
    public static final Integer Z_INVALID_ADDRESS_ERROR = 0x0c;
    public static final Integer Z_FAILED_TO_OPEN_SESSION = 0x0d;
    public static final Integer Z_UNEXPECTED_MESSAGE = 0x7f;

    private static final java.util.Map<Integer, String> errorCodeToString =
        new HashMap<Integer, String>();

    static {
        errorCodeToString.put(Z_VLE_PARSE_ERROR, "Z_VLE_PARSE_ERROR");
        errorCodeToString.put(Z_ARRAY_PARSE_ERROR, "Z_ARRAY_PARSE_ERROR");
        errorCodeToString.put(Z_STRING_PARSE_ERROR, "Z_STRING_PARSE_ERROR");
        errorCodeToString.put(Z_PROPERTY_PARSE_ERROR, "Z_PROPERTY_PARSE_ERROR");
        errorCodeToString.put(Z_PROPERTIES_PARSE_ERROR, "Z_PROPERTIES_PARSE_ERROR");
        errorCodeToString.put(Z_MESSAGE_PARSE_ERROR, "Z_MESSAGE_PARSE_ERROR");
        errorCodeToString.put(Z_INSUFFICIENT_IOBUF_SIZE, "Z_INSUFFICIENT_IOBUF_SIZE");
        errorCodeToString.put(Z_IO_ERROR, "Z_IO_ERROR");
        errorCodeToString.put(Z_RESOURCE_DECL_ERROR, "Z_RESOURCE_DECL_ERROR");
        errorCodeToString.put(Z_PAYLOAD_HEADER_PARSE_ERROR, "Z_PAYLOAD_HEADER_PARSE_ERROR");
        errorCodeToString.put(Z_TX_CONNECTION_ERROR, "Z_TX_CONNECTION_ERROR");
        errorCodeToString.put(Z_INVALID_ADDRESS_ERROR, "Z_INVALID_ADDRESS_ERROR");
        errorCodeToString.put(Z_FAILED_TO_OPEN_SESSION, "Z_FAILED_TO_OPEN_SESSION");
        errorCodeToString.put(Z_UNEXPECTED_MESSAGE, "Z_UNEXPECTED_MESSAGE");
    }


    private int errorCode;

    private static final long serialVersionUID = 402535504102337839L;
    
    protected ZException(String message) {
        this(message, -1);
    }

    protected ZException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    protected ZException(String message, int errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorCodeName() {
        String name = errorCodeToString.get(errorCode);
        return (name != null ? name : "UNKOWN_ERROR_CODE("+errorCode+")");
    }

    public String toString() {
        if (errorCode > 0) {
            return super.toString() + " (error code:" + getErrorCodeName() + ")";
        } else {
            return super.toString();
        }
    }
}
