package com.gamebuster19901.excite.modding.unarchiver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import com.gamebuster19901.excite.modding.concurrent.Batch;
import com.gamebuster19901.excite.modding.game.file.kaitai.TocMonster.Details;

public class Unarchiver {

	private final Path sourceDir;
	private final Path destDir;
	
	public LinkedHashSet<Path> tocs = new LinkedHashSet<>();
	public LinkedHashSet<Path> archives = new LinkedHashSet<>();
	
	public Unarchiver(Path sourceDir, Path destDir) throws IOException {
		this.sourceDir = sourceDir;
		this.destDir = destDir;
		refresh();
	}
	
	public Collection<Batch<Callable<Void>>> getCopyBatches() {
		LinkedHashSet<Batch<Callable<Void>>> batches = new LinkedHashSet<>();
		for(Path toc : tocs) {
			batches.add(getCopyBatch(toc));
		}
		return batches;
	}
	
	public Batch<Callable<Void>> getCopyBatch(Path tocFile) {
		Batch<Callable<Void>> batch = new Batch<>(tocFile.getFileName().toString());
		try {
			Toc toc = new Toc(tocFile.toAbsolutePath());
			List<Details> details = toc.getFiles();
			
			final QuickAccessArchive archive = getArchive(toc);
			for(Details resource : details) {
				batch.addRunnable(() -> {
					try {
						String resourceName = resource.name();
						System.out.println(tocFile.getFileName() + "/" + resourceName);
						archive.getArchive().getFile(resourceName).writeTo(destDir.resolve(resourceName));
						if(resource.name().endsWith("tex")) {
							return () -> {
								System.out.println("This is an example of processing a texture!");
								return null;
							};
						}
					}
					catch(Throwable t) {
						throw t;
					}
					return null;
				});
			}

		}
		catch(Throwable t) {
			batch.addRunnable(() -> {
				throw t; //let the batchrunner know that an error occurred
			});
		}
		return batch;
	}
	
	private QuickAccessArchive getArchive(Toc toc) throws IOException {
		for(Path archivePath : archives) {
			if(getFileName(toc.getFile()).equals(getFileName(archivePath))) {
				return new QuickAccessArchive(toc, archivePath);
			}
		}
		throw new FileNotFoundException("Resource file for toc " + toc.getFile().getFileName());
	}
	
	private static String getFileName(Path f) {
		 String fileName = f.getFileName().toString();
		 int i = fileName.lastIndexOf('.');
		 return (i == -1) ? fileName : fileName.substring(0, i);
	}
	
	private void refresh() throws IOException {
		tocs.clear();
		archives.clear();
		try(Stream<Path> fileStream = Files.walk(sourceDir)) {
			fileStream.filter(Files::isRegularFile).forEach((f) -> {
				if(f.getFileName().toString().endsWith(".toc")) {
					tocs.add(f);
				}
				else {
					archives.add(f);
				}
			});
		}
	}
	
}
