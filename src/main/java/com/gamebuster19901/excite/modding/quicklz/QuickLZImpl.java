package com.gamebuster19901.excite.modding.quicklz;

import java.nio.file.Path;

import org.apache.commons.lang3.SystemUtils;

public interface QuickLZImpl {
	
	public ProcessBuilder getProcess(QuickLZ.Mode mode, Path file, Path dest);

	public static QuickLZImpl get() {
		if(SystemUtils.IS_OS_WINDOWS) {
			throw new UnsupportedOperationException("Not Yet Implemented.");
		}
		else if (SystemUtils.IS_OS_LINUX) {
			return new LinuxQuickLZImpl();
		}
		else {
			throw new LinkageError("Unknown Operating System.");
		}
	}
	
}
