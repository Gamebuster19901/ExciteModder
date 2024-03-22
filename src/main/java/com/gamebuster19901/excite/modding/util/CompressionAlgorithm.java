package com.gamebuster19901.excite.modding.util;

import java.nio.ByteBuffer;

import com.gamebuster19901.excite.modding.game.file.toc2.CompressionHeader;

public interface CompressionAlgorithm {
	
	public static int UNCOMPRESSED = 0;
	public static int rCMP = 0x72434D50;
	
	public ByteBuffer compress(byte[] bytes);
	
	public ByteBuffer decompress(byte[] bytes);

	public static CompressionAlgorithm get(CompressionHeader compressionHeader) {
		if(compressionHeader.header() == UNCOMPRESSED && compressionHeader.unknown() == 0 && compressionHeader.compressedLength() == 0 && compressionHeader.uncompressedLength() == 0) {
			//uncompressed
			return new CompressionAlgorithm() {

				@Override
				public ByteBuffer compress(byte[] bytes) {
					ByteBuffer ret = ByteBuffer.allocate(bytes.length + 0x80);
					ret.position(0x80);
					ret.put(bytes);
					return ret;
				}

				@Override
				public ByteBuffer decompress(byte[] bytes) {
					return ByteBuffer.wrap(bytes).position(0x80);
				}
				
			};
		}
		if(compressionHeader.header() == rCMP) {
			throw new UnsupportedOperationException("Not yet implemented.");
		}
		throw new UnsupportedOperationException("Not yet implemented.");
	}
	
}
