package io.zenoh;

import java.util.HashMap;

/**
 * An Exception raised by Zenoh-net.
 */
public class ZException extends Exception {

    public static final Integer Z_NO_ERROR_CODE = 0x00;

    public static final Integer Z_VLE_PARSE_ERROR = 0x01;
    public static final Integer Z_ARRAY_PARSE_ERROR = 0x02;
    public static final Integer Z_STRING_PARSE_ERROR = 0x03;

    public static final Integer ZN_PROPERTY_PARSE_ERROR = 0x81;
    public static final Integer ZN_PROPERTIES_PARSE_ERROR = 0x82;
    public static final Integer ZN_MESSAGE_PARSE_ERROR = 0x83;
    public static final Integer ZN_INSUFFICIENT_IOBUF_SIZE = 0x84;
    public static final Integer ZN_IO_ERROR = 0x85;
    public static final Integer ZN_RESOURCE_DECL_ERROR = 0x86;
    public static final Integer ZN_PAYLOAD_HEADER_PARSE_ERROR = 0x87;
    public static final Integer ZN_TX_CONNECTION_ERROR = 0x89;
    public static final Integer ZN_INVALID_ADDRESS_ERROR = 0x8a;
    public static final Integer ZN_FAILED_TO_OPEN_SESSION = 0x8b;
    public static final Integer ZN_UNEXPECTED_MESSAGE = 0x8c;

    private static final java.util.Map<Integer, String> errorCodeToString =
        new HashMap<Integer, String>();

    static {
        errorCodeToString.put(Z_ARRAY_PARSE_ERROR, "Z_ARRAY_PARSE_ERROR");
        errorCodeToString.put(Z_STRING_PARSE_ERROR, "Z_STRING_PARSE_ERROR");
        errorCodeToString.put(ZN_PROPERTY_PARSE_ERROR, "ZN_PROPERTY_PARSE_ERROR");
        errorCodeToString.put(ZN_PROPERTIES_PARSE_ERROR, "ZN_PROPERTIES_PARSE_ERROR");
        errorCodeToString.put(ZN_MESSAGE_PARSE_ERROR, "ZN_MESSAGE_PARSE_ERROR");
        errorCodeToString.put(ZN_INSUFFICIENT_IOBUF_SIZE, "ZN_INSUFFICIENT_IOBUF_SIZE");
        errorCodeToString.put(ZN_IO_ERROR, "ZN_IO_ERROR");
        errorCodeToString.put(ZN_RESOURCE_DECL_ERROR, "ZN_RESOURCE_DECL_ERROR");
        errorCodeToString.put(ZN_PAYLOAD_HEADER_PARSE_ERROR, "ZN_PAYLOAD_HEADER_PARSE_ERROR");
        errorCodeToString.put(ZN_TX_CONNECTION_ERROR, "ZN_TX_CONNECTION_ERROR");
        errorCodeToString.put(ZN_INVALID_ADDRESS_ERROR, "ZN_INVALID_ADDRESS_ERROR");
        errorCodeToString.put(ZN_FAILED_TO_OPEN_SESSION, "ZN_FAILED_TO_OPEN_SESSION");
        errorCodeToString.put(ZN_UNEXPECTED_MESSAGE, "ZN_UNEXPECTED_MESSAGE");
    }


    private int errorCode;

    private static final long serialVersionUID = 402535504102337839L;
    
    public ZException(String message) {
        this(message, Z_NO_ERROR_CODE);
    }

    public ZException(String message, Throwable cause) {
        this(message, Z_NO_ERROR_CODE, cause);
    }

    public ZException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ZException(String message, int errorCode, Throwable cause) {
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
        if (errorCode != Z_NO_ERROR_CODE) {
            return super.toString() + " (error code:" + getErrorCodeName() + ")";
        } else {
            return super.toString();
        }
    }
}
