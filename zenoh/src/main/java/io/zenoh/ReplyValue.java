package io.zenoh;

import java.nio.ByteBuffer;

/**
 * A reply to a query (see {@link Zenoh#query(String, String, ReplyCallback)}
 * and {@link ReplyCallback#handle(ReplyValue)})
 */
public class ReplyValue {

    /**
     * The reply message kind.
     */
    public enum Kind { 
        Z_STORAGE_DATA(0),
        Z_STORAGE_FINAL(1),
        Z_REPLY_FINAL(2);

        private int numVal;

        Kind(int numVal)
        {
           this.numVal = numVal;
        }
     
        public static Kind fromInt(int numVal) throws ZException {
            if (numVal == Z_STORAGE_DATA.value()) {
                return Z_STORAGE_DATA;
            }
            else if (numVal == Z_STORAGE_FINAL.value()) {
                return Z_STORAGE_FINAL;
            }
            else if (numVal == Z_REPLY_FINAL.value()) {
                return Z_REPLY_FINAL;
            }
            else {
                throw new ZException("INTERNAL ERROR: cannot create ReplyValue.Kind from int: "+numVal);
            }
        }

        public int value()
        {
           return numVal;
        }
    }


    private Kind kind;
    private byte[] stoid;
    private long rsn;
    private String rname;
    private ByteBuffer data;
    private DataInfo info;
 
    protected ReplyValue(int kind, byte[] stoid, long rsn, String rname, ByteBuffer data, DataInfo info) 
        throws ZException
    {
        this(Kind.fromInt(kind), stoid, rsn, rname, data, info);
    }

    protected ReplyValue(Kind kind, byte[] stoid, long rsn, String rname, ByteBuffer data, DataInfo info) {
        this.kind = kind;
        this.stoid = stoid;
        this.rname = rname;
        this.data = data;
        this.info = info;
    }

    /**
     * Return the Reply message kind.
     */
    public Kind getKind() {
        return kind;
    }

    /**
     * Return the StorageId of the storage that sent this reply.
     */
    public byte[] getStoid() {
        return stoid;
    }

    /**
     * Return the request sequence number.
     */
    public long getRsn() {
        return rsn;
    }

    /**
     * Return the resource name of this reply.
     */
    public String getRname() {
        return rname;
    }

    /**
     * If the Reply message kind is Z_STORAGE_DATA this operation returns the data of this reply.
     * Otherwise, it returns null.
     */
    public ByteBuffer getData() {
        return data;
    }

    /**
     * Return the DataInfo associated to this reply.
     */
    public DataInfo getInfo() {
        return info;
    }
}
