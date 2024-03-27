package com.gamebuster19901.excite.modding.util;

import java.nio.ByteBuffer;

public class ByteBufUtils {

	public static byte[] getRemaining(ByteBuffer b) {
		if (b == null) {
			throw new IllegalArgumentException("Buffer cannot be null");
		}

		int remaining = b.remaining();
		byte[] result = new byte[remaining];
		b.get(result);
		return result;
	}
	
}
