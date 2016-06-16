package org.openstreetmap.josm.plugins.cadtools;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;

/**
 * CAD Tools plugin.
 */
public class CADToolsPlugin extends Plugin{

    /**
     * Constructs a new {@code CADToolsPlugin}.
     * @param info plugin information
     */
    public CADToolsPlugin(PluginInformation info) {
        super(info);
        CADToolsAction exportAction = new CADToolsAction();
        exportAction.setPluginInformation(info);
        Main.main.menu.toolsMenu.add(exportAction);
    }
}
