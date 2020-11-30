package com.gamebuster19901.excite.modding.old.game.file.toc;

public class TOCEntry {

	public static final int TOC_ENTRY_SIZE = 40;
	
	private final int index;
	
	
	private TOCEntryType entryType;
	
	private final byte[] UNKNOWN_DATA_2 = new byte[36];
	
	public TOCEntry(byte[] bytes, int index) {
		final int starting = index;
		this.index = index;
		
		index = setEntryType(bytes, index);
		index = setUnknownData2(bytes, index);
		
		try {
			assert index == starting + TOC_ENTRY_SIZE;
		}
		catch(AssertionError e) {
			System.out.println("Starting: " + starting);
			System.out.println("Expected index to be " + (starting + TOC_ENTRY_SIZE) + ", was actually" + index);
			throw e;
		}
	}
	
	public int getIndex() {
		return index;
	}
	
	public int getNextIndex() {
		return index + TOC_ENTRY_SIZE;
	}
	
	public TOCEntryType getEntryType() {
		return entryType;
	}
	
	private int setEntryType(byte[] bytes, int index) {
		byte[] type = new byte[TOCEntryType.LENGTH];
		for(int j = 0; j < TOCEntryType.LENGTH; j++, index++) {
			type[j] = bytes[index];
		}
		this.entryType = TOCEntryType.getEntryType(type);
		return index;
	}
	
	private int setUnknownData2(byte[] bytes, int index) {
		for(int j = 0; j < UNKNOWN_DATA_2.length; j++, index++) {
			UNKNOWN_DATA_2[j] = bytes[index];
		}
		return index;
	}
	
}
