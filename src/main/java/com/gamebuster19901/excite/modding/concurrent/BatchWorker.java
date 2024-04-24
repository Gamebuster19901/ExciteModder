package com.gamebuster19901.excite.modding.concurrent;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public interface BatchWorker<T> extends BatchContainer<T> {
	
	public abstract void addBatch(Batcher<T> batcher);

	public abstract void startBatch() throws InterruptedException;
	
	public abstract boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException;
	
	public void shutdownNow() throws InterruptedException;
	
	public Collection<Batch<T>.BatchedCallable> getRunnables();
	
	public Collection<BatchListener> getListeners();
	
	public void addBatchListener(BatchListener listener);
	
}
