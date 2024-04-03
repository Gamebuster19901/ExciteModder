package com.gamebuster19901.excite.modding.unarchiver;

import java.io.IOException;
import java.io.IOError;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import com.gamebuster19901.excite.modding.FileUtils;

public class QuicklzDumper {
	
	public static final Path TEMP;
	static {
		try {
			TEMP = Files.createTempDirectory(Paths.get("./run/tmp").toAbsolutePath(), null);
		} catch (IOException e) {
			throw new IOError(e);
		}
	}
	
	private final Path input;
	private final Path output;
	
	{
		try {
			input = FileUtils.createTempFile();
			output = FileUtils.createTempFile();
		}
		catch(IOException e) {
			throw new IOError(e);
		}
	}
	
	public byte[] decode(byte[] _raw_data) {
		try {
			Files.write(input, _raw_data, StandardOpenOption.CREATE);
			QuickLZ.decompress(input, output);
			return Files.readAllBytes(output);
		}
		catch(IOException e) {
			throw new IOError(e);
		}
	}

}
