package com.gamebuster19901.excite.modding.ui;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JComponent;

public class ImageComponent extends JComponent {

	Image image = null;
	
	public ImageComponent() {
		this(null);
	}
	
	public ImageComponent(Image image) {
		this.image = image;
	}
	
	public Image getImage() {
		return image;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Image image = getImage();
		if(image != null) {
			g.drawImage(image, 0, 0, this);
		}
	}
	
}
