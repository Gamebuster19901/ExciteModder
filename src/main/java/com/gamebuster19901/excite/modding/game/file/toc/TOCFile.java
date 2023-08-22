package com.gamebuster19901.excite.modding.game.file.toc;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.gamebuster19901.excite.modding.Checked;
import com.gamebuster19901.excite.modding.FileUtils;
import com.gamebuster19901.excite.modding.Main;

import static com.gamebuster19901.excite.modding.Assert.*;

public class TOCFile implements Checked {

	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' kk:mm:ss");
	public static final int ARCHIVE_HEADER_LENGTH = 52;
	
	private static final String HEADER = "0SERCOTE";
	private static final String IGM = "!IGM";
	
	final File file;
	final String header; //should always be 0SERCOTE
	final int version; //should always be 3
	final int unknown; //should always be null
	final String igm; //should always be !IGM
	final int version2; //should always be 3
	final int unknown2; //should always be 32
	final int date; //creation/last modification date of the archive
	final int fileCount;
	final long resourceFileLength; // +128
	final int unknown3; //either 0, 128, or 1152
	final int fileNameDirLength;
	
	final byte[] fileData;
	
	final byte[] fileNameDir;
	
	public final File resourceBundle;
	
	private final RESArchive resourceArchive;
	
	
	public TOCFile(File file) throws IOException {
		this.file = file;
		ByteBuffer buf = FileUtils.getByteBuffer(file, ByteOrder.LITTLE_ENDIAN);
		
		byte[] header = new byte[8];
		buf.get(header);
		this.header = new String(header);
		this.version = buf.getInt();
		this.unknown = buf.getInt();
		byte[] igm = new byte[4];
		buf.get(igm);
		this.igm = new String(igm);
		this.version2 = buf.getInt();
		this.unknown2 = buf.getInt();
		this.date = buf.getInt();
		this.fileCount = buf.getInt();
		this.resourceFileLength = buf.getLong();
		this.unknown3 = buf.getInt();
		this.fileNameDirLength = buf.getInt();
		
		this.fileData = new byte[fileCount * 40];
		buf.get(fileData);
		
		this.fileNameDir = new byte[fileNameDirLength];
		buf.get(fileNameDir);
		
		assertFalse(buf.hasRemaining());
		
		this.resourceBundle = getResourceBundle();

		check();
		
		resourceArchive = new RESArchive(this);

	}
	
	public final void check() throws AssertionError {
		assertEquals(header, "0SERCOTE");
		assertEquals(version, 3);
		assertEquals(unknown, 0);
		assertEquals(igm, "!IGM");
		assertEquals(version2, 3);
		assertEquals(unknown2, 32);

		//Main.SYSOUT.println(unknown2 + " " + this);
		//assertEquals(unknown3, 0); //0, 128, or 1152
		System.out.println(unknown3 + " " + this);
		//assertNotNull(resourceBundle);
		String message;
		if(resourceBundle != null) {
			message = this + " has " + fileCount + " resources in " + this.resourceBundle.getName() + ". Last Edited " + getDate();
		}
		else {
			message = this + " has no resource file!";
		}
		System.out.println(message);
	}
	
	public String getDate() {
		return LocalDateTime.ofInstant(Instant.ofEpochSecond(date), ZoneId.of("America/Chicago")).format(DATE_FORMATTER);
	}
	
	
	public String toString() {
		return file.getName();
	}
	
	public File getResourceBundle() {
		for(File f : Main.GAME_FILES.listFiles()) {
			if(!f.getPath().endsWith(".toc") && f.getName().indexOf('.') != -1) {
				if(f.getName().substring(0, f.getName().indexOf('.')).equals(file.getName().substring(0, file.getName().indexOf('.')))) {
					return f;
				}
			}
		}
		return null;
	}
	
}
