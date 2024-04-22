package com.gamebuster19901.excite.modding.ui;

import java.awt.Component;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.TreeMap;

import javax.swing.JComponent;
import java.awt.*;

public abstract class FixedCellGrid extends JComponent {

	protected final int padding;
	protected final TreeMap<Integer, Component> components = new TreeMap<>();
	protected final NavigableSet<Integer> navigator = components.navigableKeySet();
    
	public FixedCellGrid(int padding) {
		this.padding = padding;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		components.forEach((index, component) -> {
			component.setBounds(calculateCellX(index), calculateCellY(index), getCellWidth(), getCellHeight());
		});
		
	}
	
	@Override
	public void setPreferredSize(Dimension dimension) {
		super.setPreferredSize(dimension);
	}
	
	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		this.setPreferredSize(new Dimension(width - x, height - y));
	}
	
	protected abstract void onGridUpdate();
    
    protected abstract int calculateCellX(int index);
    
    protected abstract int getCellWidth();
    
    protected abstract int calculateCellY(int index);
    
    protected abstract int getCellHeight();
    
    public final Component getComponent(Integer index) {
    	return components.get(index);
    }
    
    public final Component putComponent(Integer index, Component component) {
    	super.add(component);
    	System.out.println("Adding component " + component + " at index " + index);
    	return components.put(index, component);
    }
    
    public final void removeComponent(Integer index) {
    	Component toRemove = getComponent(index);
    	if(toRemove != null) {
    		components.remove(index); //we want to remove from the grid directly since it will be much faster than the overridden FixedCellGrid.remove()
    		super.remove(toRemove); //Then use super to remove the component reference from swing
    	}
    }
    
    @Override
    public void removeAll() {
    	components.clear();
    	super.removeAll();
    }
    
    public final int getLowestFreeCell() {
    	Integer prevIndex = 0;
    	Integer index = -1;
    	while(index != null) {
    		index = navigator.higher(index);
    		if(index == null) {
    			return components.size();
    		}
    		if(index - prevIndex > 1) { //we've found a gap in the defined cells
    			return prevIndex + 1;
    		}
    		prevIndex = index;
    	}
    	
    	return components.size();
    }
    
    public final Component getFirstComponent() {
    	return components.firstEntry().getValue();
    }
    
    public final Component getLastComponent() {
    	return components.lastEntry().getValue();
    }
    
    public final TreeMap<Integer, Component> getComponentMap() {
    	return components;
    }

    @Override
    @Deprecated
    public Component add(Component component) {
    	//super.add(component);
    	System.out.println("Adding component at index " + getLowestFreeCell());
        putComponent(getLowestFreeCell(), component);
        return component;
    }
    
    @Override
    @Deprecated
    public Component add(Component component, int index) {
    	putComponent(index, component);
    	return component;
    }
    
    @Override
    @Deprecated
    public void remove(Component component) {
    	Iterator<Entry<Integer, Component>> set = components.entrySet().iterator();
    	while(set.hasNext()) {
    		Entry<Integer, Component> e = set.next();
    		if(e.getValue() == component) {
    			set.remove();
    			break;
    		}
    	}
    	super.remove(component);
    }
    
    @Override
    @Deprecated
    public void remove(int index) {
    	removeComponent(index);
    }

    @Override
    @Deprecated
    public void add(Component component, Object constraints) {
    	add(component); // Ignore constraints, components are positioned based on the cell location and size
    }
    
}
