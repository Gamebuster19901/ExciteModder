package com.gamebuster19901.excite.modding.concurrent;

import java.util.Collection;

public interface BatchContainer<T> {
	
	public abstract String getName();

	public Collection<Batch<T>.BatchedCallable> getRunnables();
	
	public Collection<BatchListener> getListeners();
	
}
