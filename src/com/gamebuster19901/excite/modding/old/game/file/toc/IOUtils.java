package com.gamebuster19901.excite.modding.old.game.file.toc;

public class IOUtils {

	public byte[] readAndSwapEndian(byte[] bytes, int index, int amountToRead) {
		byte[] ret = new byte[amountToRead];
		
		for(int i = 0, j = index + amountToRead; j > index; i++, j--) {
			ret[i] = bytes[j];
		}
		
		return ret;
	}
	
	public byte[] swapEndian(byte[] bytes) {
		byte[] ret = new byte[bytes.length];
		for(int i = 0, j = bytes.length; i < bytes.length; j--, i++) {
			ret[i] = bytes[j];
		}
		
		return ret;
	}
	
}
