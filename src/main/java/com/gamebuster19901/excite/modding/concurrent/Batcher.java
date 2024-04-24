package com.gamebuster19901.excite.modding.concurrent;

import java.util.Collection;
import java.util.concurrent.Callable;

public interface Batcher<T> extends BatchContainer<T> {
	
	public abstract void addRunnable(Callable<T> runnable);

	public abstract void addRunnable(Runnable runnable);
	
	public abstract void addListener(BatchListener listener);
	
	public void shutdownNow() throws InterruptedException;
	
	public Collection<Batch<T>.BatchedCallable> getRunnables();
	
	public Collection<BatchListener> getListeners();
	
}
