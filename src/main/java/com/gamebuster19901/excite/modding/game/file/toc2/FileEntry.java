package com.gamebuster19901.excite.modding.game.file.toc2;

import java.io.IOException;
import java.nio.ByteBuffer;

public record FileEntry(int filenameOffset, int typeCode, int typeCodeInt, int length, int offset, int hash, byte[] unused) {
	
	public FileEntry(ByteBuffer bytes) throws IOException {
		this(validateInput(bytes).getInt(), bytes.getInt(), bytes.getInt(), bytes.getInt(), bytes.getInt(), bytes.getInt(), createUnused(bytes));
	}
	
	private static ByteBuffer validateInput(ByteBuffer buffer) throws IOException {
		int start = buffer.position();
		if(buffer.limit() - start < 40) {
			throw new IOException("Insufficient buffer space");
		}
		buffer.position(start + 24);
		byte[] unused = new byte[16];
		buffer.get(unused);
		for(byte b : unused) {
			if(b != 0) {
				throw new IOException("value 0 expected in unused area, got nonzero value instead.");
			}
		}
		buffer.position(start);
		return buffer;
	}
	
	private static byte[] createUnused(ByteBuffer buffer) {
		byte[] ret = new byte[16];
		buffer.get(ret);
		return ret;
	}
	
	public static FileEntry[] getFileEntries(ByteBuffer bytes, int fileCount) throws IOException {
		if (bytes.remaining() < fileCount * 40) {
		    throw new IOException("Insufficient buffer space for all entries! " + bytes.remaining() + " remaining, need a total of " + (fileCount * 40));
		}
		FileEntry[] ret = new FileEntry[fileCount];
		for(int i = 0; i < fileCount; i++) {
			ret[i] = new FileEntry(bytes);
		}
		return ret;
	}
	
}
