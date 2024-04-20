package com.gamebuster19901.excite.modding.unarchiver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.gamebuster19901.excite.modding.game.file.kaitai.TocMonster;

public class ArchivedFile {

	private final TocMonster.Details fileDetails;
	private final Archive archive;
	private final byte[] bytes;
	
	public ArchivedFile(TocMonster.Details fileDetails, Archive archive) {
		try {
			this.fileDetails = fileDetails;
			this.archive = archive;
			System.out.println("Archive size: " + archive.getUncompressedSize());
			System.out.println("File offset: " + fileDetails.fileOffset());
			System.out.println("File size: " + fileDetails.fileSize());
			System.out.println("File end: " + ((int)fileDetails.fileOffset() + (int)fileDetails.fileSize()));
			byte[] bytes = archive.getBytes();
			System.out.println("Array size: " + bytes.length);
			this.bytes = copyOfRange(archive.getBytes(), (int)fileDetails.fileOffset(), (int)(fileDetails.fileOffset() + (int)fileDetails.fileSize()));
		}
		catch(Throwable t) {
			System.err.println("Could not extract resource " + getName() + " from " + archive.getArchiveFile().getFileName());
			t.printStackTrace();
			throw t;
		}
	}
	
	public String getName() {
		return fileDetails.name();
	}
	
	public byte[] getBytes() {
		return bytes;
	}
	
	public void writeTo(Path directory) throws IOException {
		Path dir = Files.createDirectories(directory);
		Path f = dir.resolve(getName());
		Files.deleteIfExists(f);
		Files.createFile(f);
		Files.write(f, getBytes(), StandardOpenOption.CREATE);
	}
	
    /**
     * Copies the specified range of the specified array into a new array.
     * The initial index of the range ({@code from}) must lie between zero
     * and {@code original.length}, inclusive.  The value at
     * {@code original[from]} is placed into the initial element of the copy
     * (unless {@code from == original.length} or {@code from == to}).
     * Values from subsequent elements in the original array are placed into
     * subsequent elements in the copy.  The final index of the range
     * ({@code to}), which must be greater than or equal to {@code from},
     * may be greater than {@code original.length}, in which case
     * {@code (byte)0} is placed in all elements of the copy whose index is
     * greater than or equal to {@code original.length - from}.  The length
     * of the returned array will be {@code to - from}.
     *
     * @param original the array from which a range is to be copied
     * @param from the initial index of the range to be copied, inclusive
     * @param to the final index of the range to be copied, exclusive.
     *     (This index may lie outside the array.)
     * @return a new array containing the specified range from the original array,
     *     truncated or padded with zeros to obtain the required length
     * @throws ArrayIndexOutOfBoundsException if {@code from < 0}
     *     or {@code from > original.length}
     * @throws IllegalArgumentException if {@code from > to}
     * @throws NullPointerException if {@code original} is null
     * @since 1.6
     */
    public static byte[] copyOfRange(byte[] original, int from, int to) {
        // Tickle the JIT to fold special cases optimally
        if (from != 0 || to != original.length)
            return copyOfRangeByte(original, from, to);
        else // from == 0 && to == original.length
            return original.clone();
    }

    private static byte[] copyOfRangeByte(byte[] original, int from, int to) {
        checkLength(from, to);
        int newLength = to - from;
        int calcedLength = original.length - from;
        System.out.println("Original Length: " + original.length);
        System.out.println("From: " + from);
        System.out.println("To: " + to);
        byte[] copy = new byte[newLength];
        try {
	        System.arraycopy(original, from, copy, 0,
	                         Math.min(original.length - from, newLength));
        }
        catch(ArrayIndexOutOfBoundsException e) {
        	e.printStackTrace();
	        System.arraycopy(original, from, copy, 0,
                    Math.min(original.length - from, newLength));
        }
        return copy;
    }
    
    private static void checkLength(int from, int to) {
        if (to < from) {
            throw new IllegalArgumentException(from + " > " + to);
        }
    }
	
}
