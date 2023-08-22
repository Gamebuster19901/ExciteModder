package com.gamebuster19901.excite.modding.game.file.toc;

import java.io.File;
import java.io.IOException;

public class DecompressTest {

	private static final byte[] bytes = new byte[] {0x30, 0x54, 0x53, 0x52, 0x40};
	
	public static void main(String[] args) throws IOException {
		TOCFile tocFile = new TOCFile(new File("./gameData/bat.toc"));
	}
	
}
