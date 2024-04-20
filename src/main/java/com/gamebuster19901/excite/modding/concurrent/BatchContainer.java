package com.gamebuster19901.excite.modding.concurrent;

import java.util.Collection;

import com.gamebuster19901.excite.modding.concurrent.Batch.BatchedCallable;

public interface BatchContainer {

	public Collection<BatchedCallable> getRunnables();
	
	public Collection<BatchListener> getListeners();
	
}
