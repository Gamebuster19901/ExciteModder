package com.gamebuster19901.excite.modding.unarchiver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import com.thegamecommunity.excite.modding.game.file.ResourceDecompressor;

public class CompressedArchive extends Archive implements ResourceDecompressor {

	public CompressedArchive(Archive parent) {
		super(parent.getFile());
	}
	
	public ByteBuffer getCompressionBytes() throws IOException {
		return this.getResourceBytes().slice(0x80, 0x90);
	}
	
	@Override
	public ByteBuffer getResourceBytes() throws IOException {
		return this.getRawBytes().position(0x90).slice();
	}

	@Override
	public Path decompress() throws IOException {
		strip();
		try {
			QuickLZ.decompress(this.getStrippedDest(), this.getDecompDest()).waitFor();
		} catch (InterruptedException e) {
			throw new IOException(e);
		}
		return getDecompDest();
	}
	
	@Override
	public DecompressedArchive toDecompressedArchive() throws IOException {
		decompress();
		return DecompressedArchive.of(this);
	}
	

}
