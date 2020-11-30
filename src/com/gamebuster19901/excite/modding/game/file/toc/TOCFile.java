package com.gamebuster19901.excite.modding.game.file.toc;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.gamebuster19901.excite.modding.Checked;
import com.gamebuster19901.excite.modding.FileUtils;
import com.gamebuster19901.excite.modding.Main;

import static com.gamebuster19901.excite.modding.Assert.*;

public class TOCFile implements Checked {

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
	final int link; //same as in RES file
	final int fileCount;
	final long resourceFileLength; // +128
	final int unknown3; //either 0, 128, or 1152
	final int fileNameDirLength;
	
	private final Resources resources;
	
	
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
		this.link = buf.getInt();
		this.fileCount = buf.getInt();
		this.resourceFileLength = buf.getLong();
		this.unknown3 = buf.getInt();
		this.fileNameDirLength = buf.getInt();
		
		check();
		
		this.resources = new Resources(this, buf);
	}
	
	public final void check() throws AssertionError {
		assertEquals(header, "0SERCOTE");
		assertEquals(version, 3);
		assertEquals(unknown, 0);
		assertEquals(igm, "!IGM");
		assertEquals(version2, 3);
		assertEquals(unknown2, 32);
		//Main.SYSOUT.println(unknown2 + " " + this);
		//assertEquals(unknown3, 0);
		//Main.SYSOUT.println(unknown3 + " " + this);
		Main.SYSOUT.println(this + " has " + fileCount + " resources: ");
	}
	
	
	public String toString() {
		return file.getName();
	}
	
}
