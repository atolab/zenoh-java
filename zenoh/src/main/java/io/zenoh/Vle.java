package io.zenoh;

import java.nio.ByteBuffer;


public class Vle {

	public static void encode(ByteBuffer buf, int value) {
		while (value > 0x7f) {
			int c = (value & 0x7f) | 0x80;
			buf.put((byte) c);
			value = value >> 7;
		}
		buf.put((byte) value);
	}

    public static int decode(ByteBuffer buf) {
		int value = 0;
		int c = 0;
		int i = 0;
		do {
			c = buf.get() & 0xff;
			value = value | ((c & 0x7f) << i);
			i += 7;
		} while (c > 0x7f);
		return value;
	}

}
