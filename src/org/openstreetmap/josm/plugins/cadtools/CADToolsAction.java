/**
 * 
 */
package org.openstreetmap.josm.plugins.cadtools;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.tools.Shortcut;


/**
 * @author ignacio_palermo
 *
 */
public class CADToolsAction extends JosmAction {
	
	public CADToolsAction(){
        super(tr("CAD Tools"), "images/dialogs/iconita.png",
        tr("Allows the user to make small changes to some selected buildings ."),
        Shortcut.registerShortcut("menu:buildingsedittools", tr("Menu: {0}", tr("CAD Tools")),
        KeyEvent.VK_G, Shortcut.ALT_CTRL), false);
    }

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		CADToolsDialog dialog = new CADToolsDialog();
		dialog.setSize(new Dimension(700,700));
        JOptionPane pane = new JOptionPane(dialog, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION);
        pane.setPreferredSize(new Dimension(450,450));
        JDialog dlg = pane.createDialog(Main.parent, tr("CAD Tools"));
        dialog.setOptionPane(pane);
        dlg.setVisible(true);
       
        dlg.dispose();
	}

}
