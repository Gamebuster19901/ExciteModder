package com.gamebuster19901.excite.modding.game.file;

import java.util.HashMap;

public enum ResourceType {
	UNKNOWN("\0\0\0\0", ".ild"),
	TEXTURE(" XET", ".tex"),
	MODEL("LDOM", ".mod"),
	VALUE("TLAV", ".val"),
	FX("nAhC", ".tm0"),
	tcRT("tcRT", null), //unknown
	TREE_LIST("TSLT", ".trl"),
	TERRAIN("rreT", ".ter"),
	GROUND("dnrG", ".gnd"),
	NILI("NILI", null), //unknown
	MATERIAL_MAP("PAMM", ".mmp"),
	FAN_INFO("FNIF", ".fnf"),
	BOUNDRY("LAWB", ".bwl"),
	LBOS("LBOS", ".sol"), //unknown
	TRACK_OBJECT("jbot", ".tro"),
	PATH("htPP", ".pth"),
	SOUND("0XFS", ".sfx"),
	TRACK_WOBBLE("htpp", ".msh"),
	SONG("gnoS", ".sng"),
	GUI_LAYOUT("TYAL", ".lyt"),
	hsMP("hsMP", null), //unknown
	NBVR("NBVR", ".bin"), //unknown
	XETG("XETG", ".gtex"), //grayscale texture?
	LANGUAGE("GNAL", ".lng")
	
	;
	


	ResourceType(String code, String extension) {
		register(code, extension, this);
	}
	
	private static void register(String code, String extension, ResourceType resource) {
		inner.types.put(code, resource);
	}
	
	public static boolean isCodeKnown(String code) {
		return inner.types.containsKey(code);
	}
	
	private static final class inner {
		private static final HashMap<String, ResourceType> types = new HashMap<String, ResourceType>();
	}
}
