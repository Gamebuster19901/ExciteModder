package com.gamebuster19901.excite.modding.game.file.toc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import com.gamebuster19901.excite.modding.game.file.toc.TOCFile.Resource;

public class DecompressTest {
	
	public static void main(String[] args) throws IOException {
		TOCFile toc = new TOCFile(new File("./gameData/Bat.toc"));
		RESArchive archive = new RESArchive(toc);
		archive.check();
		
		System.out.println("Extracting " + toc + "...");
		
		LinkedHashSet<Resource> resources = toc.getResources();
		LinkedHashMap<Resource, ByteBuffer> resourceData;
		
		System.out.println("There are " + resources.size() + " resources in " + resources);
		
		for(Resource resource : resources) {
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
		}
		
	}
	
}
