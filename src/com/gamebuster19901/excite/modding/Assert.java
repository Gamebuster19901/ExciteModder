package com.gamebuster19901.excite.modding;

public class Assert {

	public static void assertEquals(Object a, Object b) {
		if(a.equals(b)) {
			return;
		}
		throw new AssertionError(a.toString() + " != " + b);
	}
	
	public static void assertTrue(boolean expression) {
		if(expression) {
			return;
		}
		throw new AssertionError();
	}
	
	public static void assertFalse(boolean expression) {
		if(!expression) {
			return;
		}
		throw new AssertionError();
	}
	
	public static void assertNotNull(Object o) {
		if(o != null) {
			return;
		}
		throw new AssertionError();
	}
	
	public static void assertNil(byte[] bytes) {
		for(int i = 0; i < bytes.length; i++) {
			if(bytes[i] != 0) {
				throw new AssertionError("index " + i + " is " + bytes[i]);
			}
		}
	}
	
}
