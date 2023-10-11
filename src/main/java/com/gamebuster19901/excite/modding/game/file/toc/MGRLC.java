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
			
			while(compressed.position() < compressed.limit()) {
				final int header = compressed.getInt();
				for(int i = 0; i < 31; i++) { //for each bit in 32 bit header, except the last one
					if((header & (1)) == 0) { //if bit at position i is 0
						decompressed.put(compressed.get()); //Read a single byte out of the compressed data buffer into the decompressed data buffer
					}
					else if ((header & (1)) == 1) { //else if bit at position i is 1
						//we need to read the indicator values to determine how to interperet the upcoming data
						byte indicator = (byte) (compressed.get() & 0b11);
						final int seekback;
						final int toRead;
						switch(indicator) {
							case 0b00:
								System.out.println("CASE 0");
								seekback = compressed.get() >> 2;
								toRead = 3;
								break;
							case 0b01:
								System.out.println("CASE 1");
								seekback = compressed.getShort() >> 2;
								toRead = 3;
								break;
							case 0b10:
								{
									System.out.println("CASE 10");
									short read = compressed.getShort();
									seekback = read >> 6;
									toRead = (((read & 0b111100) >> 2) + 3);
								}
								break;
							case 0b11:
								{
									System.out.println("CASE 11");
									//Java has no 24 bit primitives, so we must read an int
									int read = compressed.getInt();
									if((read & 0b1111100) != 0) { //if value is 24 bit
										seekback = read >> 15;
										toRead = ((((read >> 8) & 0b1111100) >> 2) + 2); //RShift 8 is due to being 24 bits;
										compressed.position(compressed.position() - 1); //go back one byte to be at the same place as if we only read 24 bits instead of 32
									}
									else { //value is 32 bit
										seekback = read >> 15;
										toRead = (((read >> 7) & 0b11111111) + 3);
									}
								}
								break;
							default:
								throw new AssertionError("Indicator flag is " + Integer.toBinaryString(indicator));
						}
						decompressed.mark();
						decompressed.position(decompressed.position() - seekback); //seek back
						byte[] readBytes = new byte[toRead];
						decompressed.get(new byte[toRead]);
						decompressed.put(readBytes);
						decompressed.reset(); //reset position to last mark
					}
					else {
						throw new AssertionError("bit should be 0 or 1, got " + (header & (1 << i)) + ". Buffer position: " + compressed.position() + ", bit " + i + ".");
					}
				}
			}
			decompressed.position(0);
		}
		catch(Throwable t) {
			t.printStackTrace();
		}
		return decompressed;
	}
	
}
