package com.gamebuster19901.excite.modding.concurrent;

import java.util.Collection;
import java.util.concurrent.Callable;

import com.gamebuster19901.excite.modding.concurrent.Batch.BatchedCallable;

public interface Batcher extends BatchContainer {
	
	public abstract void addRunnable(Callable<Void> runnable);

	public abstract void addRunnable(Runnable runnable);
	
	public abstract void addListener(BatchListener listener);
	
	public void shutdownNow() throws InterruptedException;
	
	public Collection<BatchedCallable> getRunnables();
	
	public Collection<BatchListener> getListeners();
	
}
