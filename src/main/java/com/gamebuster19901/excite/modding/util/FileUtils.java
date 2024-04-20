package com.gamebuster19901.excite.modding.util;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class FileUtils {

	public static final Path TEMP;
	static {
		try {
			Files.createDirectories(Paths.get("./run/tmp"));
			TEMP = Files.createTempDirectory(Paths.get("./run/tmp").toAbsolutePath(), null);
		} catch (IOException e) {
			throw new IOError(e);
		}
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			if(Files.exists(TEMP)) {
				try {
					deleteRecursively(TEMP);
				} catch (IOException e) {
					e.printStackTrace(); //nothing we can do, so just print
				}
			}
		}));
	}
	
	public static ByteBuffer getByteBuffer(File file, ByteOrder order) throws IOException {
		return ByteBuffer.wrap(Files.readAllBytes(file.toPath())).asReadOnlyBuffer().order(order);
	}
	
	public static String readNullTerminatedString(ByteBuffer buffer) {
		String ret = "";
		while(true) {
			byte b = buffer.get();
			if(b == 0) {
				return ret;
			}
			ret = ret + (char)b;
		}
	}
	
	public static String readNullTerminatedString(ByteBuffer buffer, Charset charset) {
		int length = 0;
		int pos = buffer.position();
		while(buffer.get() != 0) {
			length++;
		}
		buffer.position(pos);
		byte[] str = new byte[length];
		for(int i = 0; i < length; i++) {
			str[i] = buffer.get();
		}
		return new String(str, charset);
	}
	
	public static Path createTempFile() throws IOException {
		Path f = Files.createTempFile(TEMP, null, null);
		System.out.println("Created temporary file " + f);
		return f;
	}
	
	public static Path createTempFile(String name) throws IOException {
		return Files.createTempFile(TEMP, name, null);
	}
	
	public static void deleteRecursively(Path path) throws IOException {	
		deleteRecursively(path, null);
	}
	
	public static void deleteRecursively(Path path, PrintStream o) throws IOException {	
		if (Files.exists(path)) {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if(o != null) {
						o.println("Deleting file: " + file.toAbsolutePath());
					}
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					if (Files.isSymbolicLink(dir)) {
						if(o != null) {
							o.println("Deleting symbolic link: " + dir.toAbsolutePath());
						}
						Files.delete(dir);
						return FileVisitResult.SKIP_SUBTREE;
					}
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					if (exc == null) {
						if(o != null) {
							o.println("Deleting directory: " + dir.toAbsolutePath());
						}
						Files.delete(dir);
						return FileVisitResult.CONTINUE;
					} else {
						throw exc;
					}
				}
			});
		} else {
			System.out.println("The specified path does not exist: " + path);
		}
	}
	
}
