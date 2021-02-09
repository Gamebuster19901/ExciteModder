package com.gamebuster19901.excite.modding;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.file.Files;

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
	
}
