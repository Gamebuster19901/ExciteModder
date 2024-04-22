package com.gamebuster19901.excite.modding.concurrent;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import com.gamebuster19901.excite.modding.concurrent.Batch.BatchedCallable;

public class BatchRunner implements BatchWorker {

	private final String name;
	private final ExecutorService executor;
	private final LinkedHashSet<Batcher> batches = new LinkedHashSet<Batcher>();
	private volatile boolean started = false;
	
    public BatchRunner(String name) {
    	this(name, Runtime.getRuntime().availableProcessors());
    }
    
    public BatchRunner(String name, int threads) throws IllegalArgumentException {
    	this(name, Executors.newFixedThreadPool(threads));
    }
	
	public BatchRunner(String name, ExecutorService executor) {
		this.name = name;
		this.executor = executor;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void addBatch(Batcher batcher) {
		synchronized(executor) {
			if(executor.isShutdown()) {
				throw new IllegalStateException("Cannot add more batches after the batch runner has shut down!");
			}
			synchronized(batches) {
				batches.add(batcher);
			}
		}
	}

	@Override
	public void startBatch() throws InterruptedException {
		synchronized(executor) {
			if(executor.isShutdown()) {
				throw new IllegalStateException("BatchRunner has already been started!");
			}
			synchronized(batches) {
				started = true;
				executor.invokeAll(getRunnables());
			}
		}
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return executor.awaitTermination(timeout, unit);
	}

	@Override
	public void shutdownNow() throws InterruptedException {
        executor.shutdownNow(); // Force shutdown of any remaining tasks
        try {
        	executor.awaitTermination(5, TimeUnit.SECONDS);
        }
        finally {
	        for(Batcher batch : batches) {
	        	batch.shutdownNow();
	        }
        }
	}

	@Override
	public Collection<BatchedCallable> getRunnables() {
		synchronized(batches) {
			LinkedHashSet<BatchedCallable> ret = new LinkedHashSet<>();
			for(Batcher batch : batches) {
				ret.addAll(batch.getRunnables());
			}
			return ret;
		}
	}

	@Override
	public Collection<BatchListener> getListeners() {
		synchronized(batches) {
			LinkedHashSet<BatchListener> ret = new LinkedHashSet<>();
			for(Batcher batch : batches) {
				ret.addAll(batch.getListeners());
			}
			return ret;
		}
	}
	
	public Collection<Batcher> getBatches() {
		return (Collection<Batcher>) batches.clone();
	}

	public int getCompleted() {
		int ret = 0;
		Collection<BatchedCallable> callables = getRunnables();
		for(BatchedCallable callable : callables) {ret++;}
		return ret;
	}
	
}
