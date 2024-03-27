package com.gamebuster19901.excite.modding.unarchiver;

import com.thegamecommunity.excite.modding.util.Math;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.stream.Stream;

public class Unarchiver {
	
	public LinkedHashSet<Path> tocs = new LinkedHashSet<>();
	public LinkedHashSet<Path> archives = new LinkedHashSet<>();

	public void unarchiveDir(Path dir) throws IOException {
		try(Stream<Path> fileStream = Files.walk(dir)) {
			fileStream.filter(Files::isRegularFile).forEach((f) -> {
				if(f.getFileName().toString().endsWith(".toc")) {
					tocs.add(f);
				}
				else {
					archives.add(f);
				}
			});
		}
	}
	
	public void unarchive(Path toc) throws IOException {
		byte[] tocBytes = Files.readAllBytes(toc);
		LinkedHashSet<FileDetails> fileDetails = new LinkedHashSet<>();
		Path archive = null;
		if(tocBytes.length >= 52) {
			
			
			{
				ByteBuffer b = ByteBuffer.wrap(tocBytes).order(ByteOrder.LITTLE_ENDIAN);
				long header = b.getLong();
				int version = b.getInt();
				b.getInt(); //unused
				int mgiHeader = b.getInt();
				int mgiVersion = b.getInt();
				int unknown = b.getInt();
				int timestamp = b.getInt();
				int fileCount = b.getInt();
				int uncompressedLength = b.getInt();
				int compressedLength = b.getInt();
				int unknown2 = b.getInt();
				int filenameDirLength = b.getInt();
				
				b.mark();
				//System.out.println("File Count: " + fileCount);
				b.position(b.position() + (fileCount * 40));
				//System.out.println(b.capacity() - b.position());
				//System.out.println(Math.nearestMultiple(b.capacity() - b.position(), 16));
				//System.out.println("NameBytes at " + b.position());
				byte[] nameBytes = new byte[Math.nearestMultiple(b.capacity() - b.position(), 16)];
				//System.out.println("Want to read to position " + (b.position() + nameBytes.length));
				//System.out.println("Buffer size is " + b.capacity());
				b.get(nameBytes);
				NameBytes names = new NameBytes(nameBytes);
				
				b.reset();
				
				for(int i = 0; i < fileCount; i++) {
					b.mark();
					int nameOffset = b.getInt();
					b.reset();
					fileDetails.add(new FileDetails(b, names.getName(nameOffset)));
				}
				
				
				for(Path a : archives) {
					if(getFileName(toc).equals(getFileName(a))) {
						archive = a;
						break;
					}
				}
				if(archive == null) {
					throw new FileNotFoundException("Resource file for " + toc);
				}
			}
			{
				
				Archive a = Archive.of(archive);
				a.toDecompressedArchive();
				
			}
			
		}
		else {
			throw new IOException("File " + toc + " is too small?!");
		}
		
		
	}
	
	public static record FileDetails(int fileNameOffset, int typeCode, int intTypeCode, int decompFileLength, int fileOffset, int hash, String name) {
		
		public FileDetails(ByteBuffer b, String name) {
			this(b.getInt(), b.getInt(), b.getInt(), b.getInt(), b.getInt(), b.getInt(), name);
			b.position(b.position() + 16); //skip the 16 null bytes
		}
		
	}
	
	private static String getFileName(Path f) {
		 String fileName = f.getFileName().toString();
		 int i = fileName.lastIndexOf('.');
		 return (i == -1) ? fileName : fileName.substring(0, i);
	}
	
}
