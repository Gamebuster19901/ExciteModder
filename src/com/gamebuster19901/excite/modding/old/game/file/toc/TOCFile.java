package com.gamebuster19901.excite.modding.old.game.file.toc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

public class TOCFile extends File {

	public static final byte[] FILE_HEADER = new byte[] {0x30, 0x53, 0x45, 0x52, 0x43, 0x4F, 0x54, 0x45, 0x03, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
	public static final String FILE_HEADER_STRING = new String(FILE_HEADER);
	
	private final byte[] IGMData = new byte[40];
	
	private final byte EntryCount;
	
	public TOCFile(File file) throws IOException {
		super(file.getCanonicalPath());
		
		assert file.getName().endsWith(".toc");
		
		byte[] bytes = Files.readAllBytes(toPath());
		
		int i = checkFileHeader(bytes);
		
		i = setIGMData(bytes, i);
		
		assert i == FILE_HEADER.length + IGMData.length;
		
		EntryCount = IGMData[16];
		
		System.out.println(file.getCanonicalPath());
		
		for(TOCEntryType entry : TOCEntryType.TYPES) {
			System.out.println(entry.getName() + " " + entry.isNew());
		}
		
		for(int j = 0; j < EntryCount; j++) {
			TOCEntry entry = new TOCEntry(bytes, i);
			if(entry.getEntryType().isNew()) {
				System.out.println("TOC entry at index" + i);
				System.out.println(" contains new TOCEntryType: " + entry.getEntryType().getName());
				System.out.println(Arrays.toString(entry.getEntryType().getType()));
			}
			i = entry.getNextIndex();
		}
	}
	
	private int checkFileHeader(byte[] bytes) {
		int i = 0;
		for(; i < FILE_HEADER.length; i++) {
			if(bytes[i] != FILE_HEADER[i]) {
				throw new AssertionError();
			}
		}
		return i;
	}
	
	private int setIGMData(byte[] bytes, int index) {
		for(int j = 0; j < IGMData.length; index++, j++) {
			IGMData[j] = bytes[index];
		}
		
		return index;
	}
}
