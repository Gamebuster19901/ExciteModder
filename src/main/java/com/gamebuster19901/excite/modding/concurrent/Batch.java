package com.gamebuster19901.excite.modding.concurrent;

import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import static java.lang.Thread.State;

public class Batch implements Batcher {

	private final String name;
    private final Set<BatchedCallable> runnables = new HashSet<>();
    private final LinkedHashSet<BatchListener> listeners = new LinkedHashSet<>();
    private volatile boolean accepting = true;
    
    public Batch(String name) {
    	this.name = name;
    }
    
    @Override
    public String getName() {
    	return name;
    }
    
    @Override
    public void addRunnable(Callable<Void> runnable) {
    	if(accepting) {
	    	BatchedCallable b = new BatchedCallable(runnable);
	    	runnables.add(new BatchedCallable(runnable));
	    	updateListeners();
    	}
    	else {
    		notAccepting();
    	}
    }

    @Override
    public void addRunnable(Runnable runnable) {
    	if(accepting) {
	        BatchedCallable b = new BatchedCallable(runnable);
	        runnables.add(new BatchedCallable(runnable));
	        updateListeners();
    	}
    	else {
    		notAccepting();
    	}
    }
    
    @Override
    public void addListener(BatchListener listener) {
    	if(accepting) {
	    	if(!listeners.add(listener)) {
	    		System.out.println("Warning: duplicate batch listener ignored.");
	    	}
    	}
    	else {
    		notAccepting();
    	}
    }
    
	@Override
	public Collection<BatchedCallable> getRunnables() {
		return Set.copyOf(runnables);
	}

	@Override
	public Collection<BatchListener> getListeners() {
		return (Collection<BatchListener>) listeners.clone();
	}

    public void shutdownNow() throws InterruptedException {
    	accepting = false;
    	Shutdown shutdown = new Shutdown();
        for(BatchedCallable r : runnables) {
        	if(r.getState() == State.NEW) {
        		r.shutdown(shutdown);
        	}
        }
        System.err.println(runnables.size());
    }
    
    public void updateListeners() {
    	for(BatchListener listener : listeners) {
    		listener.update();
    	}
    }
    
    private void notAccepting() {
    	throw new IllegalStateException("Batch is not accepting new tasks or listeners.");
    }
    
    public class BatchedCallable implements Callable<Void> {
    	
    	private final Callable<Void> child;
    	private volatile SoftReference<Thread> threadRef;
    	protected volatile Throwable thrown;
    	protected volatile boolean finished = false;
    	
    	public BatchedCallable(Runnable r) {
    		this(() -> {
    			r.run();
    			return null;
    		});
    	}
    	
    	public BatchedCallable(Callable<Void> c) {
    		this.child = c;
    		updateListeners();
    	}
    	
    	@Override
		public Void call() {
			Thread thread;
			try {
				thread = Thread.currentThread();
				threadRef = new SoftReference<>(thread);
				updateListeners();
				child.call();
			}
			catch(Throwable t) {
				this.thrown = t;
			}
			finally {
				finished = true;
				updateListeners();
			}
			return null;
		}
		
		public State getState() {
			if(finished) { //the thread is no longer working on this runnable
				return State.TERMINATED;
			}
			if(threadRef == null) {
				return State.NEW;
			}
			Thread thread = threadRef.get();
			if(thread == null) {
				return State.TERMINATED; //thread has been garbage collected, so it has been terminated.
			}
			return thread.getState();
		}
		
		public Throwable getThrown() {
			return thrown;
		}
		
		protected void shutdown(Shutdown shutdown) {
			finished = true;
			thrown = shutdown;
		}
    }
 }
