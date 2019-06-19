package io.zenoh;

import java.nio.ByteBuffer;

public class Vle {

    public static void encode(ByteBuffer buf, int n) {
        // first find out how many bytes we need to represent the integer
		int length = ((32 - Integer.numberOfLeadingZeros(n)) + 6) / 7;
		// if the integer is 0, we still need 1 byte
		length = length > 0 ? length : 1;

		// for each byte of output ...		
		for(int i = 0; i < length; i++) 
		{
            // ... take the least significant 7 bits of input and set the MSB to 1 ...
            buf.put((byte) ((n & 0b1111111) | 0b10000000));
			// ... shift the input right by 7 places, discarding the 7 bits we just used			
			n >>= 7;
		}
        // finally reset the MSB on the last byte
        int lastIdx = buf.position()-1;
        buf.put(lastIdx, (byte) (buf.get(lastIdx) & 0b01111111));
    }

}
