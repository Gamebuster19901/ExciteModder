package com.gamebuster19901.excite.modding.game.file.toc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import com.gamebuster19901.excite.modding.Checked;
import com.gamebuster19901.excite.modding.FileUtils;
import com.gamebuster19901.excite.modding.Main;
import com.gamebuster19901.excite.modding.game.file.ResourceType;

import static com.gamebuster19901.excite.modding.Assert.*;

public class ResourceFiles {

	private final TOCFile toc;
	private final ByteBuffer buf;
	public static ArrayList<ResourceDetails> resourceDetails = new ArrayList<ResourceDetails>();
	public static ArrayList<Resource> resources = new ArrayList<Resource>();
	
	public ResourceFiles(TOCFile tocFile, ByteBuffer buf) throws IOException {
		this.toc = tocFile;
		this.buf = buf;
		
		for(int i = 0; i < toc.fileCount; i++) {
			resourceDetails.add(new ResourceDetails(toc, i, buf));
		}
		
	}

	public static final class ResourceDetails implements Checked {

		private static final int RESOURCE_ENTRY_LENGTH = 40;
		
		public final TOCFile toc;
		private final int index;
		
		private final int fileNameOffset;
		private final String typeCode;
		private final int version; //should be 1
		private final int fileLength;
		private final int fileOffset;
		private final int hash; //might not actually be a hash
		private final long unknown; //should be 0
		private final long unknown2; //should be 0
		private final int fileNameDirStart;
		private final String name;
		
		private final File resourceFile;
		
		public ResourceDetails(TOCFile toc, int i, ByteBuffer buf) {
			fileNameDirStart = TOCFile.ARCHIVE_HEADER_LENGTH + (RESOURCE_ENTRY_LENGTH * toc.fileCount);
			
			this.toc = toc;
			this.index = i;
			
			fileNameOffset = buf.getInt();
			byte[] typeCode = new byte[4];
			buf.get(typeCode);
			this.typeCode = new String(typeCode);
			this.version = buf.getInt();
			this.fileLength = buf.getInt();
			this.fileOffset = buf.getInt() + 128;
			this.hash = buf.getInt();
			this.unknown = buf.getLong();
			this.unknown2 = buf.getLong();
			this.name = retrieveName(buf);
			this.resourceFile = toc.resourceBundle;
			check();
		}

		@Override
		public void check() throws AssertionError {
			Main.SYSOUT.println(name);
			try {
				assertTrue(ResourceType.isCodeKnown(typeCode));
				//Main.SYSOUT.println("Valid typecode found: " + typeCode);
				//Assert.assertEquals(version, 1);
				assertTrue(fileLength >= 0);
				assertTrue(fileOffset >= 0);
				assertTrue(unknown == 0);
				assertTrue(unknown2 == 0);
			}
			catch(AssertionError e) {
				Main.SYSOUT.println("Unknown typeCode " + typeCode);
				throw e;
			}
		}
		
		private String retrieveName(ByteBuffer buf) {
			int pos = buf.position();
			buf.position(fileNameDirStart + fileNameOffset);
			String ret = FileUtils.readNullTerminatedString(buf);
			buf.position(pos);
			return ret;
		}
		
		public String getName() {
			return name;
		}
		
		/*private byte[] getResource(ByteBuffer buf) {
			int pos = buf.position();
				
			
			
			buf.position(pos);
		}*/
		
	}
	
	public static final class Resource implements Checked {
		
		public static final String OTSR = "0TSR";
		
		public final ResourceDetails resourceDetails;
		
		private final String header;
		private byte[] unknown = new byte[79]; //79 bytes
		private int length;
		private int unknown2; //same as toc file... link?
		private int offset;
		private int nil;
		private int fileCount;
		private int length2; //+ 128
		private int length3; //+ 128
		private long nil2;
		private int filenameDirLength;
		private int nil3;
		private int unknown3;
		private byte[] unknown4 = new byte[64]; //64 bytes
		
		
		public Resource(ResourceDetails resourceDetails) throws IOException {
			this.resourceDetails = resourceDetails;
			
			ByteBuffer buf = FileUtils.getByteBuffer(resourceDetails.resourceFile, ByteOrder.LITTLE_ENDIAN);
			
			byte[] header = new byte[4];
			buf.get(header);
			this.header = new String(header);
			
			buf.get(unknown);
			
			length = buf.getInt();
			unknown2 = buf.getInt();
			offset = buf.getInt() + 128;
			nil = buf.getInt();
			fileCount = buf.getInt();
			length2 = buf.getInt() + 128;
			length3 = buf.getInt() + 128;
			nil2 = buf.getLong();
			filenameDirLength = buf.getInt();
			nil3 = buf.getInt();
			unknown3 = buf.getInt();
			buf.get(unknown4);
			
			check();
			
			resources.add(this);
		}


		@Override
		public void check() throws AssertionError {
			assertEquals(header, OTSR);
			//assertNil(unknown);
			//assertEquals(unknown2, resourceDetails.toc.unknown2);
			//System.out.println((int)offset);
			//assertEquals(offset, 128);
			assertEquals(nil, 0);
			assertEquals(length2, length3);
			assertEquals(nil2, (long)0);
			assertEquals(nil3, 0);
		}
		
		public void extract(File dir) throws IOException {
			try {
				if(dir.exists()) {
					if(dir.isDirectory()) {
						File extractionDir = new File(dir.getCanonicalPath() + File.separator + resourceDetails.toc);
						File extractedFile = new File(extractionDir.getCanonicalPath() + File.separator + resourceDetails.getName());
						Main.CONSOLE.println(extractionDir);
						if(!extractionDir.exists()) {
							extractionDir.mkdir();
						}
						if(extractionDir.isDirectory()) {
							if(!extractedFile.exists()) {
								extractedFile.createNewFile();
							}
							FileOutputStream writer = new FileOutputStream(extractedFile);
							
							ByteBuffer byteBuffer = FileUtils.getByteBuffer(resourceDetails.resourceFile, ByteOrder.LITTLE_ENDIAN);
							
							System.out.println(resourceDetails.fileLength);
							System.out.println(resourceDetails.fileNameOffset);
							System.out.println(resourceDetails.resourceFile.length());
							
							byte[] data = new byte[resourceDetails.fileLength];
							byteBuffer.get(data, resourceDetails.fileNameOffset, resourceDetails.fileLength);
							
							writer.write(data);
							writer.close();
							
						}
						else {
							throw new IOException(extractionDir.getCanonicalPath() + " must be a directory, not a file!");
						}
					}
					else {
						throw new IOException(dir.getCanonicalPath() + " must be a directory, not a file");
					}
				}
				else {
					throw new FileNotFoundException(dir.getCanonicalPath());
				}
			}
			catch (IndexOutOfBoundsException e) {
				System.exit(1);
			}
		}
		
		@Override
		public String toString() {
			return resourceDetails.toc + File.separator + resourceDetails.getName();
		}
		
	}
	
}
