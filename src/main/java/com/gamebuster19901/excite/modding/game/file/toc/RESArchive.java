package com.gamebuster19901.excite.modding.game.file.toc;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import com.gamebuster19901.excite.modding.Checked;
import com.gamebuster19901.excite.modding.FileUtils;
import com.gamebuster19901.excite.modding.Main;

import static com.gamebuster19901.excite.modding.Assert.*;

public class RESArchive implements Checked {

	private static final int HEADER = 0x52535430; //RST0
	private final TOCFile toc;
	private final File resourceFile;
	private final int header;
	private final int headerSize; //Always 64 for excitebots
	private final int unknown2; //14
	private final int unknown3; //1 - Probably ENUM for compression type
	private final int archiveLength;
	private final int date;
	private final int tocOffset; //Always 128 for excitebots
	private final int null1;
	private final int fileCount;
	private final int uncompressedLength;
	private final int compressedLength;
	private final long null2;
	private final int filenameDirLength; //resource ids
	private final int null3;
	private final int unknown4;
	private final byte[] null4 = new byte[64];
	
	public RESArchive(TOCFile toc) throws IOException {
		this.toc = toc;
		this.resourceFile = toc.getResourceBundle();
		ByteBuffer buf = FileUtils.getByteBuffer(resourceFile, ByteOrder.LITTLE_ENDIAN);
		header = buf.getInt();
		headerSize = buf.getInt();
		unknown2 = buf.getInt();
		unknown3 = buf.getInt();
		archiveLength = buf.getInt();
		date = buf.getInt();
		tocOffset = buf.getInt();
		null1 = buf.getInt();
		fileCount = buf.getInt();
		uncompressedLength = buf.getInt();
		compressedLength = buf.getInt();
		null2 = buf.getLong();
		filenameDirLength = buf.getInt();
		null3 = buf.getInt();
		unknown4 = buf.getInt();
		buf.get(null4);
		
		
	}

	@Override
	public void check() throws AssertionError {
		try {
			Main.CONSOLE.println("===============CHECKING " + resourceFile.getAbsolutePath() + "===============");
			assertEquals(header,  HEADER);
			Main.CONSOLE.println("PASSED header assertion: (" + HEADER + ")");
			assertEquals(headerSize, 64);
			Main.CONSOLE.println("PASSED unknown1 assertion: (" + 64 + ")");
			assertEquals(unknown2, 14);

			if(toc.unknown3 == 0) {
				try {
					assertEquals(unknown3, 1);
					Main.CONSOLE.println("PASSED unknown2 assertion: (" + 1 + ")");
				}
				catch(AssertionError e) {
					Main.CONSOLE.println("FAILED unknown2 assertion: Expected 1, got " + Long.toHexString(unknown3));
				}
			}
			else if (toc.unknown3 == 128 || toc.unknown3 == 1152) {
				try {
					assertEquals(unknown3, 3);
					Main.CONSOLE.println("PASSED unknown2 assertion: (" + 3 + ")");
				}
				catch(AssertionError e) {
					Main.CONSOLE.println("FAILED unknown2 assertion: Expected 3, got " + Long.toHexString(unknown3));
				}
			}
			else {
				Main.CONSOLE.println("UNEXPECTED UNEXPECTED UNEXPECTED unknown3 value in TOC file! (" + toc.unknown3 + ") res unknown3: " + unknown3);
			}
			
			assertEquals(archiveLength, (int)resourceFile.length());
			Main.CONSOLE.println("PASSED archiveLength assertion: (" + resourceFile.length() + ")");
			assertEquals(date, toc.date);
			Main.CONSOLE.println("PASSED date assertion: (" + toc.date + ") [" + toc.getDate() + "]");
			assertEquals(tocOffset, 128);
			Main.CONSOLE.println("PASSED fileDataOffset assertion: (" + 128 + ")");
			assertEquals(null1, 0);
			Main.CONSOLE.println("PASSED null1 assertion: (" + 0 + ")");
			assertEquals(fileCount, toc.fileCount);
			Main.CONSOLE.println("PASSED fileCount assertion: (" + fileCount + ")");
			//assertEquals(archiveLength2, archiveLength + 128);
			try {
				assertEquals(compressedLength + tocOffset, archiveLength);
				Main.CONSOLE.println("PASSED compressedLength + fileDataOffset assertion: (" + archiveLength + ")");
			}
			catch(AssertionError e) {
				Main.CONSOLE.println("FAILED compressedLength + fileDataOffset assertion: Expected " + Integer.toHexString(compressedLength + tocOffset) + ", got " + Integer.toHexString(archiveLength));
				Main.CONSOLE.println("\tCompressed Length: " + Integer.toHexString(compressedLength));
				Main.CONSOLE.println("\tUncompressed Length: " + Integer.toHexString(uncompressedLength));
				Main.CONSOLE.println("\tArchive Length: " + Integer.toHexString(archiveLength));
				e.printStackTrace(Main.CONSOLE);
			}
			
			assertEquals((int)null2, toc.unknown3);
			Main.CONSOLE.println("PASSED null2 assertion: (" + null2 + ")");
			
			assertEquals(filenameDirLength, toc.fileNameDirLength);
			Main.CONSOLE.println("PASSED filenameDirLength assertion: (" + toc.fileNameDirLength + ")");
			assertEquals(null3, 0);
			Main.CONSOLE.println("PASSED null3 assertion: (" + 0 + ")");
			assertNil(null4);
			Main.CONSOLE.println("PASSED null4 assertion: (" + Arrays.toString(null4) + ")");
		}
		finally {
			Main.CONSOLE.println("===============FINISHED " + resourceFile.getAbsolutePath() + "===============");
		}
	}
	
}
