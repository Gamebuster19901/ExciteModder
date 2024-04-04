package com.gamebuster19901.excite.modding.unarchiver2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import com.gamebuster19901.excite.modding.game.file.kaitai.TocMonster;

public class ArchivedFile {

	private final TocMonster.Details fileDetails;
	private final Archive archive;
	private final byte[] bytes;
	
	public ArchivedFile(TocMonster.Details fileDetails, Archive archive) {
		try {
			this.fileDetails = fileDetails;
			this.archive = archive;
			System.out.println("File offset: " + fileDetails.fileOffset());
			System.out.println("File size: " + fileDetails.fileSize());
			System.out.println("File end: " + ((int)fileDetails.fileOffset() + (int)fileDetails.fileSize()));
			System.out.println("Thread: " + Thread.currentThread().getName());
			System.out.println(archive.getBytes().length);
			this.bytes = Arrays.copyOfRange(archive.getBytes(), (int)fileDetails.fileOffset(), (int)(fileDetails.fileOffset() + (int)fileDetails.fileSize()));
		}
		catch(Throwable t) {
			System.err.println("Could not extract resource " + getName() + " from " + archive.getArchiveFile().getFileName());
		    java.util.Collection<java.lang.StackTraceElement[]> a1 = java.lang.Thread.getAllStackTraces().values();
		    for (java.lang.StackTraceElement[] a2 : a1){
		        System.out.println("========== ");
		        for (java.lang.StackTraceElement a3 : a2){
		            System.out.println(a3.toString());
		        }
		    }
			throw t;
		}
	}
	
	public String getName() {
		return fileDetails.name();
	}
	
	public byte[] getBytes() {
		return bytes;
	}
	
	public void writeTo(Path directory) throws IOException {
		Path dir = Files.createDirectories(directory);
		Path f = dir.resolve(getName());
		Files.deleteIfExists(f);
		Files.createFile(f);
		Files.write(f, getBytes(), StandardOpenOption.CREATE);
	}
	
}
