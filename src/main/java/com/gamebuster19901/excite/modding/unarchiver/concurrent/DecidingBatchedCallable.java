package com.gamebuster19901.excite.modding.unarchiver.concurrent;

import java.util.concurrent.Callable;

import com.gamebuster19901.excite.modding.concurrent.Batch.BatchedCallable;
import com.gamebuster19901.excite.modding.concurrent.Batcher;

public class DecidingBatchedCallable<T extends Skippable> extends BatchedCallable<T> {

	private volatile DecisionType decision;
	
	public DecidingBatchedCallable(Batcher<T> batch, Callable<T> c) {
		super(batch, c);
	}
	
	@Override
	public T call() {
		T ret = super.call();
		processDecision();
		return ret;
	}
	
	private void processDecision() {
		if(this.getThrown() != null) {
			decision = DecisionType.IGNORE;
		}
		else if(this.result.shouldSkip()) {
			decision = DecisionType.SKIP;
		}
		else {
			decision = DecisionType.PROCEED;
		}
	}

	public DecisionType getDecisionType() {
		return decision;
	}

}
