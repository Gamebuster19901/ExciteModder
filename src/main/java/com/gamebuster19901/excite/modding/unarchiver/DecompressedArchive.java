package com.gamebuster19901.excite.modding.unarchiver;

import java.io.IOException;
import java.nio.file.Path;

public class DecompressedArchive extends Archive {

	protected DecompressedArchive(Archive archive) {
		this(archive.getFile());
	}
	
	protected DecompressedArchive(Path file) {
		super(file);
	}

	public static DecompressedArchive of(Archive archive) throws IOException {
		return new DecompressedArchive(archive.getDecompDest());
	}

}
