package com.gamebuster19901.excite.modding;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class FileUtils {

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
	
	public static void deleteRecursively(Path path) throws IOException {	
		if (Files.exists(path)) {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					if (Files.isSymbolicLink(dir)) {
						Files.delete(dir);
						return FileVisitResult.SKIP_SUBTREE;
					}
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					if (exc == null) {
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
