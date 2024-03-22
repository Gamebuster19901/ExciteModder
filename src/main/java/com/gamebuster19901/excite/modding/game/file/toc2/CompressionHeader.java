package com.gamebuster19901.excite.modding.game.file.toc2;

import java.nio.ByteBuffer;

import com.gamebuster19901.excite.modding.util.CompressionAlgorithm;

public record CompressionHeader(int header, int unknown, int compressedLength, int uncompressedLength) implements CompressionAlgorithm {

	public CompressionHeader(ByteBuffer buffer) {
		this(buffer.getInt(), buffer.getInt(), buffer.getInt(), buffer.getInt());
	}
	
	public CompressionAlgorithm getCompressionAlgorithm() {
		return CompressionAlgorithm.get(this);
	}

	@Override
	public ByteBuffer compress(ByteBuffer bytes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ByteBuffer decompress(ByteBuffer bytes) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
