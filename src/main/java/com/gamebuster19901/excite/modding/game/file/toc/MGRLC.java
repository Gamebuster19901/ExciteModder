package com.gamebuster19901.excite.modding.game.file.toc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MGRLC { //Monster Games Run-Length Compression

	public static final int COOKIE = 0x72434D50; //rCMP
	
	
	public static ByteBuffer decompress(final ByteBuffer compressed, final ByteBuffer decompressed) {
		try {
			if(compressed.order() != ByteOrder.LITTLE_ENDIAN) {
				throw new IllegalStateException("Compressed buffer is not little endian");
			}
			int header = 1;
			int toRead = 0;
			int seekback = 0;
			while(compressed.position() < compressed.limit()) {
				binDebug(compressed.getInt());
			}
			decompressed.position(0);
		}
		catch(Throwable t) {
			t.printStackTrace();
		}
		return decompressed;
	}
	
	public static String binDebug(int i) {
		return "0x" + Integer.toHexString(i) + " | " + Integer.toBinaryString(i);
	}
	
}
