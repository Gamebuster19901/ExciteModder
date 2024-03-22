package com.gamebuster19901.excite.modding.game.file.toc2;

import java.io.FileNotFoundException;

public interface ResFilesystem {
	
	public FileEntry[] fileEntries();
	
	public default String[] getFileNames() {
		return nameBytes().getFileNames();
	}
	
	public NameBytes nameBytes();
	
	public default byte[] getRawNameBytes() {
		return nameBytes().bytes();
	}
	
	public default FileEntry getFileEntry(String name) throws FileNotFoundException {
		return fileEntries()[getFileIndex(name)];
	}
	
	public default FileEntry getFileEntry(int index) throws FileNotFoundException {
		int size = fileEntries().length;
		if(index < 0 || index >= size) {
			return fileEntries()[index];
		}
		throw new FileNotFoundException("index " + index);
	}
	
	public default String getFileName(int index) throws FileNotFoundException {
		return nameBytes().getName(index);
	}
	
	public default int getFileIndex(String name) throws FileNotFoundException {
		return nameBytes().getFileIndex(name);
	}
	
}
