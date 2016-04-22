package org.openstreetmap.josm.plugins.cadtools;



import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class OneActionPanel extends JPanel {
	
	private static final long serialVersionUID = 935692923998416843L;

	public OneActionPanel() {
		super();
		setXAxisLayout();
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(""), BorderFactory.createEmptyBorder(5,5,5,5)));
		setAlignmentX(Component.CENTER_ALIGNMENT);
	}
	
	public void cleanBorder() {
		setBorder(null);
	}
	
	public void setXAxisLayout() {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));		
	}

	public void setYAxisLayout() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));		
	}
	
	public void setAllSizes(int width, int height) {
		Dimension dimension = new Dimension(width, height);
		setMinimumSize(dimension);
		setPreferredSize(dimension);
		setMaximumSize(dimension);				
	}	
}
