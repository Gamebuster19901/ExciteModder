package com.gamebuster19901.excite.modding.quicklz;

import java.nio.file.Path;

import com.gamebuster19901.excite.modding.quicklz.QuickLZ.Mode;
import com.thegamecommunity.excite.modding.util.foreign.c.dependency.ForeignDependencies;

public class LinuxQuickLZImpl implements QuickLZImpl {

	@Override
	public ProcessBuilder getProcess(Mode mode, Path file, Path dest) {
		return new ProcessBuilder(
				ForeignDependencies.EQUICKLZ.getCompiledLocation().toAbsolutePath().toString(),
				mode.toString(),
				file.toAbsolutePath().toString(),
				dest.toAbsolutePath().toString()
		);
	}

}
