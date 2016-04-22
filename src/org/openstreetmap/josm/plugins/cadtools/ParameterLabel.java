package org.openstreetmap.josm.plugins.cadtools;

import java.awt.Font;

import javax.swing.JLabel;

public class ParameterLabel extends JLabel {
	
	private static final long serialVersionUID = -4958259778284958924L;

	public ParameterLabel(String text) {
		super(text);
		setFont(new Font("Tahoma", Font.PLAIN, 12));		
	}

}
