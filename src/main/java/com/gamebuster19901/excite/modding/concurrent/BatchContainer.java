package com.gamebuster19901.excite.modding.concurrent;

import java.util.Collection;

import com.gamebuster19901.excite.modding.concurrent.Batch.BatchedCallable;

public interface BatchContainer<T> {
	
	public abstract String getName();

	public Collection<BatchedCallable<T>> getRunnables();
	
	public Collection<BatchListener> getListeners();
	
	public void addBatchListener(BatchListener listener);
	
	public default void updateListeners() {
		for(BatchListener l : getListeners()) {
			l.update();
		}
	}
	
	public void shutdownNow() throws InterruptedException;
	
}
