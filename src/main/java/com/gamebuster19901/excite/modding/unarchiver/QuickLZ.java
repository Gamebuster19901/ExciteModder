package com.gamebuster19901.excite.modding.unarchiver;

import java.io.IOException;
import java.nio.file.Path;

import com.thegamecommunity.excite.modding.util.foreign.c.dependency.ForeignDependencies;

public class QuickLZ {

	static {
		if(!ForeignDependencies.EQUICKLZ.isAvailable()) {
			ForeignDependencies.downloadAndCompileAllDeps();
		}
	}
	
	public static enum Mode {
		help,
		compress,
		decompress
	}
	
	private static final QuickLZImpl impl = QuickLZImpl.get();
	
	public static void compress(Path file, Path dest) {
		throw new UnsupportedOperationException("Not Yet Implemented");
	}
	
	public static Process decompress(Path file, Path dest) throws IOException {
		return impl.getProcess(Mode.decompress, file, dest).start();
	}
	
}
