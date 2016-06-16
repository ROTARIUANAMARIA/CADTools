package org.openstreetmap.josm.plugins.cadtools;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;

public class ActionButton extends JButton {

    private static final long serialVersionUID = -320589948505607361L;

    public ActionButton(String label) {
        super(label);
        setFont(new Font("Tahoma", 0, 13));
        setDefaultSize();
        setAlignmentX(Component.CENTER_ALIGNMENT);
    }
    
    private void setDefaultSize() {
        Dimension dimension = new Dimension(380, 30);
        setMinimumSize(dimension);
        setPreferredSize(dimension);
        setMaximumSize(dimension);        
    }
}
