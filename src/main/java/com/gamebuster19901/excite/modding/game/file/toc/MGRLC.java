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
				System.out.println("================================");
				final int header = compressed.getInt();
				System.out.println("HEADER INDEX: " + compressed.position());
				System.out.println("HEADER: " + Integer.toHexString(header));
				if((header & 0x80000000) != 0x80000000) {
					throw new AssertionError("Top bit is not 1!");
				}
				for(int i = 0; i < 31; i++) { //for each bit in 32 bit header, except the last one
					if(((header >>> i) & 1) == 0) { //if bit at position i is 0
						byte b = compressed.get();
						decompressed.put(b); //Read a single byte out of the compressed data buffer into the decompressed data buffer
						System.out.println("SINGLE BYTE AT 0x" + Integer.toHexString(compressed.position()) + ": " + Integer.toHexString(Byte.toUnsignedInt(b)));
					}
					else if(((header >>> i) & 1) == 1) { //else if bit at position i is 1
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
									System.out.println("READ " + Integer.toHexString(read));
									if((read & 0b1111100) != 0) { //if value is 24 bit
										System.out.println("VALUE IS 24 BIT ");
										read = read & 0xFFFFFF; //snip the value so we only accept the lower 24 bits
										System.out.println("24 BIT VALUE: " + binDebug(read));
										seekback = read >>> 7;
										System.out.println("SEEKBACK: " + binDebug(seekback));
										toRead = ((read & 0b1111100) >> 2) + 2; //RShift 8 is due to being 24 bits;
										System.out.println("TO READ: " + binDebug(toRead));
										compressed.position(compressed.position() - 1); //go back one byte to be at the same place as if we only read 24 bits instead of 32
									}
									else { //value is 32 bit
										System.out.println("VALUE IS 32 BIT");
										seekback = read >> 15;
										toRead = (((read >> 7) & 0b11111111) + 3);
									}
								}
								break;
							default:
								throw new AssertionError("Indicator flag is " + Integer.toBinaryString(indicator));
						}
						int mark = decompressed.position();
						System.out.println("MARK AT INDEX " + mark);
						decompressed.position(decompressed.position() + seekback); //seek back
						System.out.println("SEEKBACK: " + seekback);
						byte[] readBytes = new byte[toRead];
						decompressed.get(new byte[toRead]);
						decompressed.put(readBytes);
						decompressed.position(mark); //reset position to last mark
					}
					else {
						throw new AssertionError("bit should be 0 or 1, got " + ((header >> i) & 1) + ". Buffer position: " + compressed.position() + ", bit " + i + ".");
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
	
	public static String binDebug(int i) {
		return "0x" + Integer.toHexString(i) + " | " + Integer.toBinaryString(i);
	}
	
}
