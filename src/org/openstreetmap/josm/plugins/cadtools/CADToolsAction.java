/**
 * 
 */
package org.openstreetmap.josm.plugins.cadtools;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.tools.Shortcut;

/**
 * @author ignacio_palermo
 *
 */
public class CADToolsAction extends JosmAction {

    private static final long serialVersionUID = 4134741433851551032L;

    private PluginInformation pluginInformation;

    /**
     * Constructs a new {@code CADToolsAction}.
     */
    public CADToolsAction() {
        super(tr("CAD Tools"), "dialogs/iconita.png",
              tr("Allows the user to make small changes to some selected buildings ."),
              Shortcut.registerShortcut("menu:CADTools", tr("Menu: {0}", tr("CAD Tools")), KeyEvent.VK_G, Shortcut.ALT_CTRL),
              false);
    }

    public void setPluginInformation(PluginInformation pluginInformation) {
        this.pluginInformation = pluginInformation;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        final CADToolsDialog dialog = new CADToolsDialog(pluginInformation.localversion);
        dialog.setSize(new Dimension(700, 750));
        JOptionPane pane = new JOptionPane(dialog, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION);
        pane.setPreferredSize(new Dimension(450, 600));
        JDialog dlg = pane.createDialog(MainApplication.getMainFrame(), tr("CAD Tools"));
        dialog.setOptionPane(pane);
        dlg.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent windowEvent) {
                dialog.storeConfiguration();
            }
        });
        dlg.setVisible(true);
        dlg.dispose();
    }
}
