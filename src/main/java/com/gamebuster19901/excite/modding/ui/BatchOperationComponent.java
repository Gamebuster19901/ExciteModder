package com.gamebuster19901.excite.modding.ui;

import javax.swing.JPanel;

import com.gamebuster19901.excite.modding.concurrent.BatchContainer;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class BatchOperationComponent extends JPanel {
	
	private BatchedImageComponent batch;
	private String name;
	private JLabel fileName;
	
	public BatchOperationComponent(BatchContainer batch, String name) {
		this(new BatchedImageComponent(batch), name);
	}
	
	/**
	@wbp.parser.constructor
	**/
	public BatchOperationComponent(BatchedImageComponent batch, String name) {
		this.batch = batch;
		setLayout(new BorderLayout(0, 0));
		
		add(batch, BorderLayout.CENTER);
		
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
	
}
