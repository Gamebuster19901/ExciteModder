package com.gamebuster19901.excite.modding.unarchiver;

import java.io.IOException;
import java.nio.file.Path;

public class Main {

	public static void main(String[] args) throws IOException {
		Unarchiver unarchiver = new Unarchiver();
		unarchiver.unarchiveDir(Path.of("./gameData"));
		
		for(Path toc : unarchiver.tocs) {
			System.out.println("Unarchiving " + toc);
			unarchiver.unarchive(toc);
		}
	}
	
}
