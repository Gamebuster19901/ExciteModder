package com.gamebuster19901.excite.modding.old.game.file.toc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TOCEntryType {

	public static final int LENGTH = 4;
	
	public static final List<TOCEntryType> TYPES = new ArrayList<TOCEntryType>();
	
	static {
		for(KnownType type : KnownType.values()) {
			TYPES.add(new TOCEntryType(type.name(), type.getReadableName(), type.getExtension()));
		}
	}
	
	byte[] entryType;
	String readableType;
	String fileExtension;
	boolean isNew;
	
	protected TOCEntryType(byte[] entryType, String typeName, String fileExtension) {
		if(entryType.length == 4) {
			this.entryType = entryType;
			this.isNew = true;
		}
		else {
			throw new IllegalArgumentException();
		}
	}
	
	protected TOCEntryType(String entryType, String typeName, String fileExtension) {
		if(entryType.length() == 4) {
			this.entryType = entryType.getBytes();
		}
		else {
			throw new IllegalArgumentException(entryType + " " + entryType.length());
		}
	}
	
	public static final TOCEntryType getEntryType(byte[] entryType) {
		final String typeString = new String(entryType, 0, 1);
		
		TOCEntryType type = new TOCEntryType(entryType, "UNKNOWN", "UNKNOWN");
		
		if(TYPES.contains(type)) {
			return TYPES.get(TYPES.indexOf(type));
		}
		else {
			TYPES.add(type);
			return type;
		}
	}
	
	public byte[] getType() {
		return Arrays.copyOf(entryType, 4);
	}
	
	public String getName() {
		return new String(getType()).replace('_', ' ');
	}
	
	public String getReadableType() {
		return readableType;
	}
	
	public String getFileExtension() {
		return fileExtension;
	}
	
	public boolean isNew() {
		return isNew;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof TOCEntryType) {
			if(((TOCEntryType) o).getName().equals(getName())) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return getName().hashCode();
	}
	
	private static enum KnownType {
		/*_XET("Texture", ".tex"),
		LDOM("Model", ".mod"),
		TLAV("Value", ".val"),
		nAhC("Shader/FX", ".tm0"),
		tcRT("UNKNOWN", "UNKNOWN"),
		TSLT("Tree List", ".trl"),
		rreT("Terrain", ".ter"),
		dnrG("Ground", ".gnd"),
		NILI("UNKNOWN", "UNKNOWN"),
		PAMM("Material Map", ".mmp"),
		FNIF("Fan Info", ".fnf"),
		LAWB("Boundry Wall", ".bwl");
		
		*/

		UNKN("unkn", ".nkn");
		
		String typeName;
		String fileExtension;
		
		KnownType(String typeName, String fileExtension) {
			this.typeName = typeName;
			this.fileExtension = fileExtension;
		}
		
		public String getReadableName() {
			return typeName;
		}
		
		public String getExtension() {
			return fileExtension;
		}
	}
	
}
