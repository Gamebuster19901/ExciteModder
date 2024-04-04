package com.gamebuster19901.excite.modding.unarchiver;

import java.io.IOError;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import com.gamebuster19901.excite.modding.FileUtils;
import com.gamebuster19901.excite.modding.util.ByteBufUtils;
import com.thegamecommunity.excite.modding.game.file.ResourceDecompressor;
import com.thegamecommunity.excite.modding.game.file.ResourceFile;

public class Archive implements ResourceFile {
	
	private static final Path CURRENT_DIR = Paths.get("").resolve("run").toAbsolutePath();
	
	public static final Path DECOMPRESS_LOC = CURRENT_DIR.resolve("decomp");
	public static final Path OUTPUT = CURRENT_DIR.resolve("out");
	
	static {
		try {
			FileUtils.deleteRecursively(DECOMPRESS_LOC);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			FileUtils.deleteRecursively(OUTPUT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			Files.createDirectories(DECOMPRESS_LOC);
			Files.createDirectories(OUTPUT);
		} catch (IOException e) {
			IOError e2 = new IOError(e);
			throw e2;
		}
		
	}

	private final String name;
	private final Path file;
	
	protected Archive(Path file) {
		this.file = file;
		this.name = file.getFileName().toString();
	}
	
	@Override
	public ByteBuffer getRawBytes() throws IOException {
		return ByteBuffer.wrap(Files.readAllBytes(file));
	}

	@Override
	public ByteBuffer getResourceBytes() throws IOException {
		return this.getRawBytes().position(0x100).slice();
	}

	@Override
	public ByteBuffer getHeaderBytes() throws IOException {
		return this.getRawBytes().slice(0, 0x40);
	}

	@Override
	public boolean isCompressedArchive() throws IOException {
		int compress = this.getHeaderBytes().position(44).getInt();
		System.out.println("compress: " + compress) ;
		return compress != 0;
	}
	
	public final Path getStrippedDest() throws IOException {
		Path dest = Archive.DECOMPRESS_LOC.resolve(this.getName());
		Path strippedDest = dest.resolve(this.getName() + ".stripped");
		return strippedDest;
	}
	
	public final Path getDecompDest() throws IOException {
		Path dest = Archive.DECOMPRESS_LOC.resolve(this.getName());
		Path decompDest = dest.resolve(this.getName() + ".decomp");
		return decompDest;
	}
	
	public void strip() throws IOException {
		Files.createDirectories(getDecompDest().getParent());
		Files.write(getStrippedDest(), ByteBufUtils.getRemaining(getResourceBytes()), StandardOpenOption.CREATE);
	}
	
	public DecompressedArchive toDecompressedArchive() throws IOException {
		strip();
		Files.copy(getStrippedDest(), getDecompDest(), StandardCopyOption.REPLACE_EXISTING);
		return DecompressedArchive.of(this);
	}
	
	public static Archive of(Path archive) throws IOException {
		Archive ret;
		if(Files.isRegularFile(archive)) {
			ret = new Archive(archive);
			if(ret.isCompressedArchive()) {
				System.out.println(archive.getFileName() + " is compressed!");
				ret = new CompressedArchive(ret);
			}
			else {
				System.out.println(archive.getFileName() + " is not compressed!");
			}
			return ret;
		}
		throw new IllegalArgumentException(archive + " is not a file!");
	}
	
	public Path getFile() {
		return file;
	}
	
	public String getName() {
		return name;
	}
	
	private static String getFileName(Path f) {
		 String fileName = f.getFileName().toString();
		 int i = fileName.lastIndexOf('.');
		 return (i == -1) ? fileName : fileName.substring(0, i);
	}

	@Override
	public ResourceDecompressor getDecompressor() {
		return () -> {
			return toDecompressedArchive().getDecompDest();
		};
	}

}
