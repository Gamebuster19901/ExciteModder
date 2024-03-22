package com.gamebuster19901.excite.modding.game.file.toc2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.LinkedHashSet;
import com.thegamecommunity.excite.modding.util.Math;


/**
 * A record representing a collection of file names stored as byte sequences with null terminators.
 * This record provides methods for retrieving file names by index or iterating through all file names.
 *
 * <p>The file names are stored as null-terminated strings within a byte array. This record handles
 * the decoding and encoding of file names to and from this format.
 *
 * <p>Instances of this record are immutable and thread-safe.
 */
public record NameBytes(byte[] bytes) {

	private static final Charset ASCII = Charset.forName("US-ASCII");
	
	public NameBytes(byte[] bytes) {
	    if (bytes.length % 16 != 0) {
	        throw new IllegalArgumentException("Array size (" + bytes.length + ") not a multiple of 16 bytes");
	    }
		this.bytes = bytes;
	}
	
	/**
	 * Retrieves the file name at the specified index within the byte sequence.
	 *
	 * @param index the index of the file name to retrieve (zero-based)
	 * @return the file name as a String
	 * @throws FileNotFoundException if the index is invalid or the corresponding file name cannot be found
	 */
	public String getName(int index) throws FileNotFoundException {
		if (index < 0 || index >= bytes.length) {
			throw new FileNotFoundException("index " + index);
		}

		// Find the start of the name at the specified index
		int nameStart = 0;
		for (int currentName = 0; currentName <= index; currentName++) {
			if (bytes[currentName] == '\0') {
				nameStart = currentName + 1;
			}
		}

		int nameEnd = nameStart;
		while (nameEnd < bytes.length && bytes[nameEnd] != '\0') {
			nameEnd++;
		}

		return new String(bytes, nameStart, nameEnd - nameStart, ASCII);
	}
	
	/**
	 * Retrieves the index of a file name within the byte sequence.
	 *
	 * @param name the name of the file to search for
	 * @return the index of the file name within the byte sequence
	 * @throws FileNotFoundException if the name is not found
	 */
	public int getFileIndex(String name) throws FileNotFoundException {
		try {
			for (int i = 0; i < bytes.length; i++) {
				if (name.equals(getName(i))) {
					return i;
				}
			}
		} catch (FileNotFoundException e) {
			// Re-throw the exception with additional context
			FileNotFoundException e2 = new FileNotFoundException("File not found: " + name);
			e2.initCause(e);
			throw e2;
		}
		throw new FileNotFoundException("File not found: " + name);
	}
	
	/**
	 * Retrieves all file names stored within the byte sequence as an array of Strings.
	 *
	 * @return an array of Strings representing all file names
	 */
	public String[] getFileNames() {
		LinkedHashSet<String> names = new LinkedHashSet<>();

		try {
			names.add(getName(0));
		} catch (FileNotFoundException e) {
			return new String[0]; // No filenames found, return empty array
		}

		int i = names.getFirst().length() + 1; // Start after the first name and null terminator
		while (i < bytes.length) {
			if (bytes[i] == '\0') {
				int start = i + 1; // Start of the next name (after the null terminator)
				int end = i; // Current position is the end of the current name
				names.add(new String(bytes, start, end - start, ASCII));
			}
			i++;
		}

		return names.toArray(new String[0]);
	}
	
	/**
	 * Creates a new NameBytes record from an array of Strings.
	 *
	 * @param strings an array of Strings representing file names
	 * @return a new NameBytes record containing the provided file names as byte sequences with null terminators
	 */
	public static NameBytes toNameBytes(String[] strings) {
		int size = 0;
		StringBuilder b = new StringBuilder();

		for (String str : strings) {
			b.append(str).append('\0');
			size += str.length() + 1;
		}

		byte[] combinedBytes = b.toString().getBytes(ASCII);
		int paddedSize = Math.nearestMultiple(size, 16);
		byte[] bytes = new byte[paddedSize];

		System.arraycopy(combinedBytes, 0, bytes, 0, combinedBytes.length);

		return new NameBytes(bytes);
	}
	
	/**
	 * Creates a new NameBytes record from the remaining contents of a ByteBuffer.
	 *
	 * @param buffer a ByteBuffer containing a collection of file names stored as byte sequences with null terminators.
	 * @return a new NameBytes record representing the decoded file names
	 * @throws IOException if the buffer does not contain valid NameBytes data,
	 * 
	 */
	public static NameBytes toNameBytes(ByteBuffer buffer) throws IOException {
	    int size = buffer.remaining();

	    byte[] bytes = new byte[size];
	    buffer.get(bytes);
	    try {
	    	return new NameBytes(bytes);
	    }
	    catch(Throwable t) {
	    	throw new IOException(t);
	    }
	}
	
}
