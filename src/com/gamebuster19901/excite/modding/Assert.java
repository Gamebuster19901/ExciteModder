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
	
}
