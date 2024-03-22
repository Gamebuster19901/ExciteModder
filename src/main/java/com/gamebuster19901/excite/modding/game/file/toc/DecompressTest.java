package com.gamebuster19901.excite.modding.game.file.toc;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;

import com.gamebuster19901.excite.modding.game.file.toc.TOCFile.Resource;
import com.thegamecommunity.excite.modding.util.foreign.c.dependency.ForeignDependencies;

public class DecompressTest {
	
	
	
	public static void main(String[] args) throws IOException, InterruptedException, LinkageError {
		
		ForeignDependencies.downloadAndCompileAllDeps(true);
		
		TOCFile toc = new TOCFile(new File("./gameData/Scorpion.toc"));
		RESArchive archive = new RESArchive(toc);
		archive.check(); //does assertion tests
		
		System.out.println("Analyzing " + toc + "...");
		
		LinkedHashSet<Resource> resources = toc.getResources();
		
		System.out.println("There are " + resources.size() + " resources in " + resources);
		System.out.println("TOC file says the decompressed size should be " + toc.resourceFileLength);
		
		archive.toResourceBytes();
		
	}
	
}
