package com.gamebuster19901.excite.modding.game.file.toc2;

import java.io.IOException;
import java.nio.ByteBuffer;

public record RResFileSystem(FileEntry[] fileEntries, NameBytes nameBytes) implements ResFilesystem {
	
	public RResFileSystem(ByteBuffer bytes) throws IOException {
		this(bytes, bytes.duplicate().position(bytes.position() - 20).get());
	}
	
	public RResFileSystem(ByteBuffer bytes, int fileCount) throws IOException {
		this(FileEntry.getFileEntries(bytes, fileCount), NameBytes.toNameBytes(bytes));
	}
	
	public RResFileSystem(FileEntry[] entries, byte[] names) {
		this(entries, new NameBytes(names));
	}
	
	public RResFileSystem(FileEntry[] entries, String[] names) {
		this(entries, NameBytes.toNameBytes(names));
	}

}
