package io.zenoh.test;

import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;

import io.zenoh.Vle;

public class VleTest {

    private void testEncodeDecode(int value, int expectedBufSize) {
        ByteBuffer buf = ByteBuffer.allocate(10);
        Vle.encode(buf, value);
        buf.flip();
        Assert.assertEquals("VLE encoding of "+value+" results in unexpected buffer length", expectedBufSize, buf.remaining());
        Assert.assertEquals("VLE encoding + decoding of "+value+" results in unexpected value", value, Vle.decode(buf));

    }

    @Test
    public final void testVle() {
        testEncodeDecode(0, 1);
        testEncodeDecode(1, 1);
        testEncodeDecode(0x7e, 1);
        testEncodeDecode(0x7f, 1);

        testEncodeDecode(0x80, 2);
        testEncodeDecode(0xff, 2);
        testEncodeDecode(0x2000, 2);
        testEncodeDecode(0x3fff, 2);    

    
        testEncodeDecode(0x4000, 3);    
        testEncodeDecode(0x7ffe, 3);    
        testEncodeDecode(0x7fff, 3);
        testEncodeDecode(0xffff, 3);
        testEncodeDecode(0x1fffff, 3);

        testEncodeDecode(0x200000, 4);
        testEncodeDecode(0x800000, 4);
        testEncodeDecode(0x0fffffff, 4);

        testEncodeDecode(0x10000000, 5);
        testEncodeDecode(0x7fffffff, 5);
    } 

}
