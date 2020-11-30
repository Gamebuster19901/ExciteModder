package com.gamebuster19901.excite.modding.game.file.toc;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.gamebuster19901.excite.modding.Assert;
import com.gamebuster19901.excite.modding.Checked;
import com.gamebuster19901.excite.modding.FileUtils;
import com.gamebuster19901.excite.modding.Main;
import com.gamebuster19901.excite.modding.game.file.ResourceType;

public class Resources {

	private final TOCFile toc;
	private final ByteBuffer buf;
	private ArrayList<Resource> resources = new ArrayList<Resource>();
	
	public Resources(TOCFile tocFile, ByteBuffer buf) {
		this.toc = tocFile;
		this.buf = buf;
		
		for(int i = 0; i < toc.fileCount; i++) {
			resources.add(new Resource(toc, i, buf));
		}
	}

	public static final class Resource implements Checked {

		private static final int RESOURCE_ENTRY_LENGTH = 40;
		
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
		
		public Resource(TOCFile toc, int i, ByteBuffer buf) {
			fileNameDirStart = TOCFile.ARCHIVE_HEADER_LENGTH + (RESOURCE_ENTRY_LENGTH * toc.fileCount);
			
			this.index = i;
			fileNameOffset = buf.getInt();
			byte[] typeCode = new byte[4];
			buf.get(typeCode);
			this.typeCode = new String(typeCode);
			this.version = buf.getInt();
			this.fileLength = buf.getInt();
			this.fileOffset = buf.getInt();
			this.hash = buf.getInt();
			this.unknown = buf.getLong();
			this.unknown2 = buf.getLong();
			this.name = retrieveName(buf);
			check();
		}

		@Override
		public void check() throws AssertionError {
			Main.SYSOUT.println(name);
			try {
				Assert.assertTrue(ResourceType.isCodeKnown(typeCode));
				//Main.SYSOUT.println("Valid typecode found: " + typeCode);
				//Assert.assertEquals(version, 1);
				Assert.assertTrue(fileLength >= 0);
				Assert.assertTrue(fileOffset >= 0);
				Assert.assertTrue(unknown == 0);
				Assert.assertTrue(unknown2 == 0);
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
		
	}
	
}
