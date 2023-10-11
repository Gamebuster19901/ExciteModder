package com.gamebuster19901.excite.modding.game.file.toc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.google.common.primitives.Ints;

public class ByteBufferTest {

	public static final byte[] data = Ints.toByteArray(MGRLC.COOKIE);
	
	public static void main(String[] args) {
		System.out.println("String value: " + new String(data));
		ByteBuffer little = ByteBuffer.wrap(data);
		ByteBuffer big = ByteBuffer.wrap(data);
		little.order(ByteOrder.LITTLE_ENDIAN);
		big.order(ByteOrder.BIG_ENDIAN);
		
		System.out.println(little.capacity());
		System.out.print("Little endian: "); 
		while(little.hasRemaining()) {
			int i = little.getInt();
			System.out.print(i);
			byte[] data = Ints.toByteArray(i);
			for(byte b : data) {
				System.out.print((char)b);
			}
		}
		System.out.println();
		System.out.print("Big endian: "); 
		while(big.hasRemaining()) {
			int i = big.getInt();
			System.out.print(i);
			byte[] data = Ints.toByteArray(i);
			for(byte b : data) {
				System.out.print((char)b);
			}
		}
		
		System.out.println();
	}
	
}
