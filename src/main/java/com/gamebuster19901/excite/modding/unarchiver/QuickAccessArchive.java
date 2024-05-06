package com.gamebuster19901.excite.modding.unarchiver;

import java.io.IOException;
import java.nio.file.Path;

public class QuickAccessArchive {

	private final Toc toc;
	private final Path archivePath;
	private volatile Archive archive;
	private volatile boolean set;
	
	public QuickAccessArchive(Toc toc, Path archivePath) {
		this.toc = toc;
		this.archivePath = archivePath;
	}
	
	public synchronized void setArchive() throws IOException {
		if(set == false) {
			try {
				this.archive = new Archive(archivePath, toc);
			}
			catch(Throwable t) {
				throw t;
			}
			finally {
				set = true; //so we don't attempt to read the file multiple times if it fails
			}
		}
	}
	
	public Archive getArchive() throws IOException {
		setArchive();
		return archive;
	}
	
}
