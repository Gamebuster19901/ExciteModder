package com.gamebuster19901.excite.modding.game.file.toc;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import com.gamebuster19901.excite.modding.game.file.toc.TOCFile.Resource;

public class DecompressTest {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		int gameDataCount = 0;
		int messageCount = 0;
		
		int gameDataSuccess = 0;
		int messageDataSuccess = 0;
		int gameDataFail = 0;
		int messageDataFail = 0;
		
			try {
				gameDataCount++;
				TOCFile toc = new TOCFile(new File("./gameData/mitch9_test_opt1.toc"));
				RESArchive archive = new RESArchive(toc);
				archive.check();
				
				System.out.println("Extracting " + toc + "...");
				
				LinkedHashSet<Resource> resources = toc.getResources();
				LinkedHashMap<Resource, ByteBuffer> resourceData;
				
				System.out.println("There are " + resources.size() + " resources in " + resources);
				Thread.sleep(1000);
				System.out.println("Decompressing " + archive);
				
				ByteBuffer in = ByteBuffer.wrap(archive.getCompressedBytes()).order(ByteOrder.LITTLE_ENDIAN);
				System.err.println(in.capacity());
				ByteBuffer out = ByteBuffer.allocate(archive.getUncompressedLength());
				
				System.out.println("COMPRESSED SIZE: " + archive.getCompressedLength() + " BYTES");
				System.out.println("UNCOMPRESSED SIZE: " + archive.getUncompressedLength() + " BYTES");
				MGRLC.decompress(in, out);
				gameDataSuccess++;
			}
			catch(Throwable t) {
				t.printStackTrace();
				gameDataFail++;
			}
		}
		
		
		/*for(Resource resource : resources) {
			ByteBuffer in = ByteBuffer.wrap(resource.toResourceBytes());
			in.order(ByteOrder.LITTLE_ENDIAN);
			ByteBuffer out = ByteBuffer.allocate(resource.fileLength);
			MGRLC.decompress(in, out);
			File file = new File("./run/" + resource);
			if(!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			try(FileOutputStream fos = new FileOutputStream(file)) {
				fos.write(out.array());
			}

			break;
		}*/
	
}
