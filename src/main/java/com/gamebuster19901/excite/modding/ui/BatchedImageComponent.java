package com.gamebuster19901.excite.modding.ui;

import java.awt.Color;
import java.awt.Image;
import java.util.Collection;
import java.util.LinkedHashMap;
import com.gamebuster19901.excite.modding.concurrent.BatchListener;
import com.gamebuster19901.excite.modding.concurrent.Batch;
import com.gamebuster19901.excite.modding.concurrent.Batch.BatchedCallable;
import com.gamebuster19901.excite.modding.concurrent.BatchContainer;

public class BatchedImageComponent extends ImageComponent implements BatchContainer {

	private final BatchContainer batch;
	
	public BatchedImageComponent() {
		this(new Batch());
	}
	
	public BatchedImageComponent(BatchContainer batch) {
		this.batch = batch;
	}
	
	@Override
	public Image getImage() {
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
	public Collection<BatchedCallable> getRunnables() {
		return batch.getRunnables();
	}

	@Override
	public Collection<BatchListener> getListeners() {
		return batch.getListeners();
	}
	
}
