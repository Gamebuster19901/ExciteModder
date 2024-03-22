package com.gamebuster19901.excite.modding.game.file.toc2;

import java.io.IOException;
import java.nio.ByteBuffer;

public record TOCFile2(long identifier, int version, int null1, int timestamp, int fileCount, int length, int length2, int unknown, int filenameDirLength, ResFilesystem files) {

	public TOCFile2(ByteBuffer bytes) throws IOException {
		this(validateInput(bytes).getLong(), bytes.getInt(), bytes.getInt(), bytes.getInt(), bytes.getInt(), bytes.getInt(), bytes.getInt(), bytes.getInt(), bytes.getInt(), new RResFileSystem(bytes));
	}
	
	private static ByteBuffer validateInput(ByteBuffer buffer) throws IOException {
		if(buffer.limit() - buffer.position() < 52) {
			throw new IOException("Insufficient buffer space");
		}
		return buffer;
	}
	
}
