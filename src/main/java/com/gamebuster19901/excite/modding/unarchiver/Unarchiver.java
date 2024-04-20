package com.gamebuster19901.excite.modding.unarchiver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.stream.Stream;

import com.gamebuster19901.excite.modding.game.file.kaitai.TocMonster;
import com.gamebuster19901.excite.modding.game.file.kaitai.TocMonster.Details;

public class Unarchiver {

	private static final Path runDir = Path.of(".").resolve("run");
	private static final Path ripDir = runDir.resolve("rip");
	
	public LinkedHashSet<Path> tocs = new LinkedHashSet<>();
	public LinkedHashSet<Path> archives = new LinkedHashSet<>();

	public void unarchiveDir(Path dir) throws IOException {
		try(Stream<Path> fileStream = Files.walk(dir)) {
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
	
	public void unarchive(Path tocFile) throws IOException {
		TocMonster toc = TocMonster.fromFile(tocFile.toAbsolutePath().toString());
		ArrayList<Details> details = toc.details();
		for(Details fileDetails : details) {
			System.out.println(tocFile.getFileName() + "/" + fileDetails.name());
		}
		Archive archive = null;
		for(Path archivePath : archives) {
			if(getFileName(tocFile).equals(getFileName(archivePath))) {
				archive = new Archive(archivePath, tocFile);
				break;
			}
		}
		if(archive == null) {
			throw new FileNotFoundException("Resource file for toc " + tocFile);
		}
		
		archive.writeTo(ripDir);
	}
	
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		Unarchiver unarchiver = new Unarchiver();
		unarchiver.unarchiveDir(Path.of("./gameData"));
		
		for(Path toc : unarchiver.tocs) {
			System.out.println("Unarchiving " + toc);
			unarchiver.unarchive(toc);
		}
		new Scanner(System.in).nextLine(); //wait to exit
	}
	
	private static String getFileName(Path f) {
		 String fileName = f.getFileName().toString();
		 int i = fileName.lastIndexOf('.');
		 return (i == -1) ? fileName : fileName.substring(0, i);
	}
	
}
