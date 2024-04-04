package com.gamebuster19901.excite.modding.unarchiver;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import com.gamebuster19901.excite.modding.game.file.kaitai.TocMonster;

public class Toc {

	private final Path tocFile;
	private final TocMonster toc;
	
	public Toc(Path tocFile) throws IOException {
		this.tocFile = tocFile;
		this.toc = TocMonster.fromFile(tocFile.toAbsolutePath().toString());
	}
	
	public Path getFile() {
		return tocFile;
	}
	
	public Instant getCreationDate() {
		return Instant.ofEpochSecond(toc.header().unixTime());
	}
	
	public long getFileCount() {
		return toc.header().numFile();
	}
	
	public long getCompressedSize() {
		return toc.header().compressedResSize();
	}
	
	public long getUncompressedSize() {
		return toc.header().uncompressedResSize();
	}
	
	public List<TocMonster.Details> getFiles() {
		return toc.details();
	}
	
	public List<TocMonster.Filenames> getFileNames() {
		return toc.filenames();
	}
	
}
