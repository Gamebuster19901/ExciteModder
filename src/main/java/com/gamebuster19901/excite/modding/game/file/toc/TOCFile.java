package com.gamebuster19901.excite.modding.game.file.toc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.UUID;

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
	
	final String fileNameDir;
	
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
		
		byte[] fileNameDirBytes = new byte[fileNameDirLength];
		buf.get(fileNameDirBytes);
		this.fileNameDir = new String(fileNameDirBytes);
		
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
	
	public LinkedHashSet<Resource> getResources() {
		LinkedHashSet<Resource> resources = new LinkedHashSet<Resource>();
		ByteBuffer reader = ByteBuffer.wrap(fileData).order(ByteOrder.LITTLE_ENDIAN);
		//reader.position(0x28);
		while(reader.position() < reader.capacity()) {
			assertTrue(resources.add(new Resource(new TOCEntry(reader))));
		}
		return resources;
	}
	
	public class TOCEntry {
		
		protected final int filenameOffset;
		protected final int typeCode;
		protected final int typeInt;
		protected final int fileLength;
		protected final int fileOffset;
		protected final int unknown;
		protected final UUID uuid;
		
		TOCEntry(int filenameOffset, int typeCode, int typeInt, int fileLength, int fileOffset, int unknown, UUID uuid) {
			this.filenameOffset = filenameOffset;
			this.typeCode = typeCode;
			this.typeInt = typeInt;
			this.fileLength = fileLength;
			this.fileOffset = fileOffset;
			this.unknown = unknown;
			this.uuid = uuid;
		}
		
		public TOCEntry(ByteBuffer buffer) {
			this(assertOrder(testIndex(buffer), ByteOrder.LITTLE_ENDIAN).getInt(), buffer.getInt(), buffer.getInt(), buffer.getInt(), buffer.getInt(), buffer.getInt(), new UUID(buffer.getLong(), buffer.getLong()));
		}
		
		public Resource getResource() {
			return new Resource(this);
		}
		
		public static TOCEntry fromBytes(TOCFile file, byte[] bytes) {
			if(bytes.length != 40) {
				throw new IllegalArgumentException("TOC Entry must be exactly 40 bytes! (got " + bytes.length + ")");
			}
			return file.new TOCEntry(ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN));
		}
		
		public int getFilenameOffset() {
			return filenameOffset;
		}
		
		public int getTypeCode() {
			return typeCode;
		}
		
		public int getTypeInt() {
			return typeInt;
		}
		
		public int getFileLength() {
			return fileLength;
		}
		
		public int getFileOffset() {
			return fileOffset;
		}
		
		public int getUnknown() {
			return unknown;
		}
		
		public UUID getUUID() {
			return uuid;
		}
		
		public final byte[] toDirectoryBytes() {
			final byte[] ret = new byte[40];
			final ByteBuffer writer = ByteBuffer.allocate(40).order(ByteOrder.BIG_ENDIAN);
			writer.putInt(filenameOffset);
			writer.putInt(typeCode);
			writer.putInt(typeInt);
			writer.putInt(fileLength);
			writer.putInt(fileOffset);
			writer.putInt(unknown);
			final byte nil = 0;
			for(int i = writer.position(); i < 40; i++) {
				writer.put(nil);
			}
			return ret;
		}
		
		public String toDebugString() {
			return 
				"Filename Offset: " + Integer.toHexString(filenameOffset) + "\n" +
				"Type Code: " + Integer.toHexString(typeCode) + "\n" +
				"Type Int" + Integer.toHexString(typeInt)
					
			;
			
		}
		
		public String toString() {
			return TOCFile.this.toString();
		}
		
	}
	
	public class Resource extends TOCEntry {
		
		private final TOCEntry entry;
		private final String name;
		
		public Resource(TOCEntry entry) {
			super(entry.filenameOffset, entry.typeCode, entry.typeInt, entry.fileLength, entry.fileOffset, entry.unknown, entry.uuid);
			this.entry = entry;
			this.name = name();
		}
		
		public String getName() {
			return name;
		}
		
		private String name() {
			//System.out.println(toDebugString());
			String name = TOCFile.this.fileNameDir.substring(this.entry.filenameOffset);
			name = name.substring(0, name.indexOf('\0'));
			System.out.println("Found " + name);
			return name;
		}
		
		public byte[] toResourceBytes() throws IOException {
			final byte[] ret = new byte[fileLength];
			final File bundle = TOCFile.this.getResourceBundle();
			FileInputStream fis = new FileInputStream(bundle);
			fis.skip(0x84); //skip the OTSR header and the PMCr magic indicator
			System.out.println("N: " + ret.length);
			System.out.println("OFFSET: " + fileOffset);
			System.out.println("LENGTH: " + fileLength);
			fis.read(ret, fileOffset, fileLength - fileOffset);
			fis.close();
			dump(ret);
			return ret;
		}
		
		public String toString() {
			return name;
		}
		
		private void dump(byte[] bytes) {
			try {
				File file = new File("./run/" + name + ".dump");
				if(!file.exists()) {
					file.getParentFile().mkdirs();
					file.createNewFile();
				}
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(bytes);
				fos.close();
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private ByteBuffer assertOrder(ByteBuffer buf, ByteOrder order) {
		assertTrue(buf.order() == order);
		return buf;
	}
	
	private ByteBuffer testIndex(ByteBuffer buf) {
		//System.out.println("POSITION:" + buf.position());
		return buf;
	}
	
}
