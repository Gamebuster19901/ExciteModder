package com.gamebuster19901.excite.modding.game.file.toc;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.google.common.primitives.Ints;
import com.thegamecommunity.excite.modding.game.propritary.crc.CRCTester;

public class MGRLC { //Monster Games Run-Length Compression

	public static final int B_COOKIE = 0x72434D50; //rCMP - Cookie after being converted to big endian
	public static final int L_COOKIE = 0x504d4372; //PMCr - Little endian cookie, as written directly in file
	
	public static void decompress(final ByteBuffer compressed, final ByteBuffer decompressed) throws IOException {
		
		Header header = new Header(compressed);
		if(header.magic != B_COOKIE) {
			throw new IOException("[MGRLC] Bad Cookie: " + Ints.toByteArray(header.magic) + " [" + Integer.toHexString(header.magic()) + "]");
		}
		if(header.compressedSize != compressed.capacity()) {
			//throw new IOException("Compress header: Size mismatch (Expected " + header.compressedSize + " bytes, got " + compressed.capacity() + " bytes)");
		}
		int i = 0;
		System.out.println("[MGRLC] Buffer size: " + compressed.capacity() + " " + header.compressedSize);
		//if(compressed.capacity() != compressed.array().length) {
			//throw new AssertionError();
		//}
		//compressed.position(0x11);
		
		compressed.order(ByteOrder.BIG_ENDIAN);
		
		byte x = compressed.get();
		if(x != 0xb8) {
			//throw new AssertionError(x);
		}
		
		int actualCRC = CRCTester.test(compressed, 0x10, compressed.capacity());
		if(header.crc != actualCRC) {
			compressed.position(0);
			StringBuilder b = new StringBuilder();
			for(int j = 0; j < 8 && j < compressed.capacity(); j++) {
				b.append("0x");
				b.append(Integer.toHexString(compressed.getInt()));
				b.append(' ');
			}
			b.append('\n');
			b.append("...");
			compressed.position(compressed.capacity() - (8 * 4));
			for(int j = compressed.capacity() - 8; j < compressed.capacity(); j++) {
				b.append("0x");
				b.append(Integer.toHexString(compressed.getInt()));
				b.append(' ');
			}
			System.out.println(b);
			throw new IOException("[MGRLC]: Bad CRC. Expected 0x" + Integer.toHexString(header.crc) + " got 0x" + Integer.toHexString(actualCRC) + ". Size: " + Integer.toHexString(compressed.capacity()));
		}
		
		System.out.println("Wow! CRC MATCHED: " + header.crc);
		System.out.println("Position " + i);
		
		compressed.position(compressed.position() - 1);
		
		
		
	}
	
	private record Header(int magic, int crc, int compressedSize, int decompressedSize, byte unknown, int compressedSize2, int decompressedSize2) {
		
		public Header(ByteBuffer buffer) {
			this(checkBuffer(buffer).getInt(), buffer.getInt(), buffer.getInt(), buffer.getInt(), buffer.get(), buffer.getInt(), buffer.getInt());
		}
		
		private static ByteBuffer checkBuffer(ByteBuffer buffer) {
			if(buffer.order() != ByteOrder.LITTLE_ENDIAN) {
				throw new IllegalStateException("Buffer is not in little endian mode!");
			}
			return buffer;
		}
		
		public void updateBuffer(ByteBuffer buffer) {
			int pos = buffer.position();
			if(buffer.position() != 0) {
				System.out.println("[WARN] Buffer is at nonzero position, this is probably not intended!");
			}
			buffer.order(ByteOrder.BIG_ENDIAN);
			buffer.putInt(magic);
			buffer.putInt(crc);
			buffer.putInt(compressedSize);
			buffer.putInt(decompressedSize);
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			buffer.position(pos);
		}
		
	}
	
	
	/*
	
	public static ByteBuffer decompress(final ByteBuffer compressed, final ByteBuffer decompressed) {
		
		if(compressed.order() != ByteOrder.LITTLE_ENDIAN) {
			throw new IllegalStateException("Compressed buffer must be little endian!");
		}
		
		try {
			while(compressed.hasRemaining()) {
				int header = compressed.getInt();
				System.out.println("======== HEADER: " + Integer.toHexString(header));
				if((header & 0x80000000) != 0x80000000) {
					throw new AssertionError("Top bit is not 1! We must be misaligned!");
				}
				for(int i = 0; i < 31; i++) {
					if(((header >>> i) & 1) == 0) { //if bit at position i is 0
						byte b = compressed.get();
						System.out.println("SINGLE BYTE AT 0x" + (compressed.position() - 1) + " (0x" + (Integer.toHexString(b & 0xFF)) + ")");
						decompressed.put(compressed.get());
					}
					else {
						int toRead; //amount of bytes to read from decompressed buffer
						int seekback; //how far to seek into the decompressed buffer
						
						int indicator = header & 0b11;
						if(indicator == 0b00) {
							toRead = 3;
							seekback = compressed.get() >>> 2;
						}
						else if (indicator == 0b01) {
							toRead = 3;
							seekback = compressed.get() >>> 2;
						}
						else if (indicator == 0b10) {
							toRead = (header >>> 2 & 0xF) + 3;
							System.out.println(compressed.position());
							seekback = compressed.getShort() >>> 6;
						}
						else if (indicator == 0b11) {
							
							if((indicator & 0x7c) == 0) { //if bits 3-7 inclusive are zero, must be a 32 bit value
								toRead = (header >>> 7 & 0xFF) + 3; //bits 8 - 15 inclusive
								seekback = (compressed.getInt() >>> 15) + 3; //bits 16 to 32
							}
							else { //must be a 24 bit value
								toRead = (header >>> 2 & 0xFC) + 2;
								byte b1 = compressed.get();
								byte b2 = compressed.get();
								byte b3 = compressed.get();
								seekback = b3 << 16 | b2 << 8 << b1; //I think?
							}
						}
						else {
							throw new AssertionError();
						}
						
						byte[] bytes = new byte[toRead];
						decompressed.get(bytes);
						compressed.put(bytes);
						decompressed.position(decompressed.position() + seekback);
					}
					
				}
			}
		}
		catch(Throwable t) {
			t.printStackTrace();
		}
		
		return decompressed;
	}
	
	*/
	
	/*
	public static ByteBuffer decompress(final ByteBuffer compressed, final ByteBuffer decompressed) {
		try {
			if(compressed.order() != ByteOrder.LITTLE_ENDIAN) {
				throw new IllegalStateException("Compressed buffer is not little endian");
			}
			while(decompressed.position() < decompressed.limit()) {
				System.out.println("================================");
				final int header = compressed.getInt();
				System.out.println("HEADER INDEX: " + compressed.position());
				System.out.println("HEADER: " + Integer.toHexString(header));
				if((header & 0x80000000) != 0x80000000) {
					throw new AssertionError("Top bit is not 1! We must be offset!");
				}
				for(int i = 0; i < 31; i++) { //for each bit in 32 bit header, except the last one
					if(((header >>> i) & 1) == 0) { //if bit at position i is 0
						byte b = compressed.get();
						decompressed.put(b); //Read a single byte out of the compressed data buffer into the decompressed data buffer
						System.out.println("SINGLE BYTE AT 0x" + Integer.toHexString(compressed.position()) + ": 0x" + Integer.toHexString(Byte.toUnsignedInt(b)));
					}
					else if(((header >>> i) & 1) == 1) { //else if bit at position i is 1
						//we need to read the indicator values to determine how to interperet the upcoming data
						byte indicator = (byte) (compressed.get() & 0b11);
						final int seekback;
						final int toRead;
						switch(indicator) {
							case 0b00:
								System.out.println("CASE 0");
								seekback = compressed.get() >>> 2;
								toRead = 3;
								break;
							case 0b01:
								System.out.println("CASE 1");
								seekback = compressed.getShort() >>> 2;
								toRead = 3;
								break;
							case 0b10:
								{
									System.out.println("CASE 10");
									short read = compressed.getShort();
									System.out.println("READ " + Integer.toHexString(read));
									seekback = read >>> 6;
									toRead = (((read >>> 2) & 0b1111) + 3);
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
										seekback = read >>> 15;
										toRead = (((read >>> 7) & 0b11111111) + 3);
									}
								}
								break;
							default:
								throw new AssertionError("Indicator flag is " + Integer.toBinaryString(indicator));
						}
						int mark = decompressed.position();
						//System.out.println("MARK AT INDEX " + mark);
						decompressed.position(decompressed.position() + seekback); //seek back
						System.out.println("SEEKBACK: " + seekback);
						byte[] readBytes = new byte[toRead];
						decompressed.get(new byte[toRead]);
						decompressed.put(readBytes);
						//decompressed.position(mark); //reset position to last mark
					}
					else {
						throw new AssertionError("bit should be 0 or 1, got " + ((header >> i) & 1) + ". Buffer position: " + compressed.position() + ", bit " + i + ".");
					}
				}
			}
			decompressed.position(0);
		}
		catch(Throwable t) {
			IOException e = new IOException("EXCEPTION AT COMPRESSED POSITION " + compressed.position() + ", DECOMPRESSED POSITION " + decompressed.position());
			e.initCause(t);
			new IOError(e).printStackTrace();
		}
		return decompressed;
	}
	
	public static String binDebug(int i) {
		return "0x" + Integer.toHexString(i) + " | " + Integer.toBinaryString(i);
	}
	
	private int readUint24(ByteBuffer buf) {
		int b1 = 0xFF & buf.get();
		int b2 = 0xFF & buf.get();
		int b3 = 0xFF & buf.get();
		return b1 << 16 | b2 << 8 | b3;
	}
	
	*/
	
}
