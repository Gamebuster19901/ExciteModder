package com.gamebuster19901.excite.modding.ui;

import java.awt.Color;
import java.awt.Image;
import java.util.Collection;
import java.util.LinkedHashMap;

import com.gamebuster19901.excite.modding.concurrent.BatchListener;
import com.gamebuster19901.excite.modding.concurrent.Batch.BatchedCallable;
import com.gamebuster19901.excite.modding.concurrent.BatchContainer;

public class BatchedImageComponent<T> extends ImageComponent implements BatchContainer<T>, BatchListener {

	private final BatchContainer<T> batch;
	
	public BatchedImageComponent(BatchContainer<T> batch) {
		super(batch.getName());
		this.batch = batch;
		addBatchListener(this);
	}
	
	@Override
	public Image getImage() {
		System.out.println("Image");
		int _new = 0;
        int working = 0;
        int success = 0;
        int failure = 0;
        int other = 0;
        
        for (BatchedCallable runnable : getRunnables()) {
            switch (runnable.getState()) {
                case NEW:
                    _new++;
                    continue;
                case RUNNABLE:
                    working++;
                    continue;
                case TERMINATED:
                    if(runnable.getThrown() == null) {
                    	success++;
                    	continue;
                    }
                    failure++;
                    continue;
                default:
                	other++;
            }
        }
        
        LinkedHashMap<Color, Integer> colors = new LinkedHashMap<>();
        colors.put(Color.GREEN, success);
        colors.put(Color.RED, failure);
        colors.put(Color.WHITE, working);
        colors.put(Color.ORANGE, other);
        colors.put(Color.GRAY, _new);

        return StripedImageGenerator.generateImage(getWidth(), getHeight(), (LinkedHashMap<Color, Integer>) colors);
	}

	@Override
	public Collection<BatchedCallable<T>> getRunnables() {
		return batch.getRunnables();
	}

	@Override
	public Collection<BatchListener> getListeners() {
		return batch.getListeners();
	}

	@Override
	public void addBatchListener(BatchListener listener) {
		batch.addBatchListener(listener);
	}

	@Override
	public void update() {
		repaint();
		System.out.println("Updated");
	}

	@Override
	public void shutdownNow() throws InterruptedException {
		batch.shutdownNow();
	}
	
}
