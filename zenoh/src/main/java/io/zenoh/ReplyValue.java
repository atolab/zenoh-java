package io.zenoh;

import java.nio.ByteBuffer;

public class ReplyValue {

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
 
    public ReplyValue(int kind, byte[] stoid, long rsn, String rname, ByteBuffer data, DataInfo info) 
        throws ZException
    {
        this(Kind.fromInt(kind), stoid, rsn, rname, data, info);
    }

    public ReplyValue(Kind kind, byte[] stoid, long rsn, String rname, ByteBuffer data, DataInfo info) {
        this.kind = kind;
        this.stoid = stoid;
        this.rname = rname;
        this.data = data;
        this.info = info;
    }

    public Kind getKind() {
        return kind;
    }

    public byte[] getStoid() {
        return stoid;
    }

    public long getRsn() {
        return rsn;
    }

    public String getRname() {
        return rname;
    }

    public ByteBuffer getData() {
        return data;
    }

    public DataInfo getInfo() {
        return info;
    }
}
