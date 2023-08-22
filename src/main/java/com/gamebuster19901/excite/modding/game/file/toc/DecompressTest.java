package com.gamebuster19901.excite.modding.game.file.toc;

import java.nio.ByteBuffer;

public class DecompressTest {

	private static final byte[] bytes = new byte[] {0x30, 0x54, 0x53, 0x52, 0x40};
	
	public static void main(String[] args) {
		ByteBuffer buffer = ByteBuffer.allocate(5);
		buffer.mark();
		buffer.put(bytes);
		buffer.reset();
		
		for(Byte b : bytes) {
			byte read = buffer.get();
			System.out.println("0x" + Integer.toHexString(b) + ": 0x" + Integer.toHexString(read) + ": " + Integer.toBinaryString(read));
		}
	}
	
}
