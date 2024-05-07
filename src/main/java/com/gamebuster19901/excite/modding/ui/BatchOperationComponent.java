package com.gamebuster19901.excite.modding.ui;

import javax.swing.JPanel;

import com.gamebuster19901.excite.modding.concurrent.Batch.BatchedCallable;
import com.gamebuster19901.excite.modding.concurrent.BatchContainer;
import com.gamebuster19901.excite.modding.concurrent.BatchListener;
import java.awt.BorderLayout;
import java.util.Collection;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class BatchOperationComponent extends JPanel implements BatchContainer {
	
	private BatchedImageComponent batch;
	private JLabel fileName;
	
	public BatchOperationComponent(BatchContainer batch) {
		this(new BatchedImageComponent(batch));
		setName(batch.getName());
	}
	
	/**
	@wbp.parser.constructor
	**/
	public BatchOperationComponent(BatchedImageComponent batch) {
		this.batch = batch;
		setLayout(new BorderLayout(0, 0));
		
		add(batch, BorderLayout.CENTER);
		
		String name = batch.getName();
		
		fileName = new JLabel(name);
		fileName.setHorizontalAlignment(SwingConstants.CENTER);
		add(fileName, BorderLayout.SOUTH);
		this.setName(name);
	}
	
	@Override
	public void setName(String name) {
		super.setName(name);
		this.setToolTipText(name);
		this.fileName.setText(name);
	}

	@Override
	public Collection<BatchedCallable> getRunnables() {
		return batch.getRunnables();
	}

	@Override
	public Collection<BatchListener> getListeners() {
		return batch.getListeners();
	}

	@Override
	public void update() {
		SwingUtilities.invokeLater(() -> {
			batch.update();
			revalidate();
			repaint();
		});

	}
	
}
