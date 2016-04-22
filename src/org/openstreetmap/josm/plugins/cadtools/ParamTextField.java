package org.openstreetmap.josm.plugins.cadtools;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JTextField;

public class ParamTextField extends JTextField {
	
	private static final long serialVersionUID = 965956287612675537L;

	public ParamTextField() {
		setFont(new Font("Tahoma", Font.PLAIN, 12));
	}

	public void setAllSizes(int width, int height) {
		Dimension dimension = new Dimension(width, height);
		setMinimumSize(dimension);
		setPreferredSize(dimension);
		setMaximumSize(dimension);				
	}
}
