package com.gamebuster19901.excite.modding.game.file.mail;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Mail {
	
	public static final String APP_ID_HEADER = "X-Wii-AppId";
	public static final String EXCITEBOTS = "1-52583345-0001";

	public static boolean isExcitebotsMail(ByteBuffer bytes) throws IOException {
		int position = bytes.position();
		int i = 0;
		goToLine(bytes, 5);
		int toRead = i + APP_ID_HEADER.length() + 1 + EXCITEBOTS.length();
		if(toRead < bytes.capacity()) {
			byte[] b = new byte[toRead];
			bytes.get(b);
			String s = new String(b);
			return s.contains(EXCITEBOTS);
		}
		bytes.position(position);
		return false;
	}
	
	public static byte[] getMailAttachment(ByteBuffer bytes) {
		int i = 0;
		int start = bytes.position();
		int end = start;
		byte b1 = -1;
		byte b2 = -1;
		for(; i < bytes.capacity();) {
			b2 = b1;
			b1 = bytes.get();
			if(b1 == '\n') {
				i++;
				if(b2 == '\n') {
					end = bytes.position();
					break;
				}
			}
		}
		byte[] ret = new byte[end - start];
		bytes.position(start);
		for(i = 0; i < ret.length; i++) {
			ret[i] = bytes.get();
		}
		return ret;
	}
	
	public static void goToLine(ByteBuffer bytes, int line) {
		int i = 1;
		for(; i < line && i < bytes.capacity();) {
			byte b = bytes.get();
			if(b == '\n') {
				i++;
			}
		}
	}
	
	public static void skipLine(ByteBuffer bytes) {
		byte b;
		if(!bytes.hasRemaining()) {
			return;
		}
		do {
			b = bytes.get();
		}
		while(b != '\n' && bytes.hasRemaining());
	}
	
	public static String readLine(ByteBuffer bytes) {
		byte b;
		int start = bytes.position();
		int end = start;
		if(!bytes.hasRemaining()) {
			return "";
		}
		do {
			b = bytes.get();
			end++;
		}
		while(b != '\n' && bytes.hasRemaining());
		
		bytes.position(start);
		
		byte[] stringBytes = new byte[end - start];
		for(int i = start, j = 0; i < end; i++, j++) {
			stringBytes[j] = bytes.get();
		}
		return new String(stringBytes);
	}
	
}
