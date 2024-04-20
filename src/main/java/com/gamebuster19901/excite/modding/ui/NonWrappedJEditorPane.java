package com.gamebuster19901.excite.modding.ui;

import java.awt.Component;
import javax.swing.JEditorPane;
import javax.swing.plaf.ComponentUI;

public class NonWrappedJEditorPane extends JEditorPane {

	@Override
	public boolean getScrollableTracksViewportWidth() {
		Component parent = getParent();
		ComponentUI ui = getUI();
		
		return true;
	}
	
}
