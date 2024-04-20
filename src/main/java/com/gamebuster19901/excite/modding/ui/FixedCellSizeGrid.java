package com.gamebuster19901.excite.modding.ui;
import java.awt.*;

public class FixedCellSizeGrid extends FixedCellGrid {

	private int cellWidth;
	private int cellHeight;
	private int scroll;

    public FixedCellSizeGrid(Dimension gridDimension, Dimension cellDimension) {
    	this(gridDimension, cellDimension, 0);
    }
    
	public FixedCellSizeGrid(Dimension gridDimension, Dimension cellDimension, int padding) {
		super(padding);
		this.cellWidth = cellDimension.width;
		this.cellHeight = cellDimension.height;
		this.setPreferredSize(gridDimension);
		try {
			calculateCellX(1);
		}
		catch(ArithmeticException e) {
			throw new IllegalArgumentException("Grid size is too small, it cannot hold any cells!", e);
		}
	}
	
	@Override
	public void setPreferredSize(Dimension dimension) {
		super.setPreferredSize(dimension);
		onGridUpdate();
	}

	@Override
	protected void onGridUpdate() {
		components.forEach((index, component) -> {
			component.setBounds(calculateCellX(index), calculateCellY(index), getCellWidth(), getCellHeight());
		});
		System.out.println(this.getPreferredSize());
		System.out.println(cellWidth);
		invalidate();
	}
	
	protected void setCellSize(Dimension dimension) {
		this.cellWidth = dimension.width;
		this.cellHeight = dimension.height;
		onGridUpdate();
	}

	@Override
	protected int getCellWidth() {
		return cellWidth;
	}

	@Override
	protected int getCellHeight() {
		return cellHeight;
	}
    
	@Override
    protected int calculateCellX(int index) {
	    return ((((index % (this.getPreferredSize().width / (cellWidth + padding))) * (cellWidth + padding))) + (padding / 2));
    }
	
	private int calculateCellYRaw(int index) {
    	int rowIndex = index / (this.getPreferredSize().width / (cellWidth + padding));
    	return (rowIndex * (cellHeight + padding)) / (cellHeight + padding);
	}
	
	private int getVisibleRows() {
		return this.getPreferredSize().height / (cellHeight + padding);
	}
	
	private int getMaxScroll() {
		return Math.max(0, calculateCellYRaw(components.size()) - getVisibleRows());
	}
    
    protected int calculateCellY(int index) {
    	int rowIndex = index / (this.getPreferredSize().width / (cellWidth + padding));
    	return (rowIndex * (cellHeight + padding)) - (scroll * (cellHeight + padding));
    }
    
    public int getScroll() {
    	return scroll;
    }
    
    public void scroll(int offset) {
    	int newScroll = scroll + offset;
    	System.out.println("Max scroll: " + getMaxScroll());
    	if(newScroll < 0) {
    		scroll = 0;
    	}
    	else if(newScroll > getMaxScroll()) {
    		scroll = getMaxScroll();
    	}
    	else {
    		scroll = newScroll;
    	}
    	onGridUpdate();
    }
    
}
