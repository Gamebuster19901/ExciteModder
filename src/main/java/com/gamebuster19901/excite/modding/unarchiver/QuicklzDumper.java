package com.gamebuster19901.excite.modding.unarchiver;

import java.io.IOException;
import java.io.IOError;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.TimeUnit;

import com.gamebuster19901.excite.modding.FileUtils;

public class QuicklzDumper {
	
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
			Process process = QuickLZ.decompress(input, output);
			process.waitFor(5000, TimeUnit.SECONDS);
			return Files.readAllBytes(output);
		}
		catch(IOException | InterruptedException e) {
			throw new IOError(e);
		}
	}

}
