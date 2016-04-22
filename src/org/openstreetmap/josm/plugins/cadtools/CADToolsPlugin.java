package org.openstreetmap.josm.plugins.cadtools;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;



public class CADToolsPlugin extends Plugin{
    

	private CADToolsAction exportAction;
	
    public CADToolsPlugin(PluginInformation info) {
        super(info);
        exportAction = new CADToolsAction();
        exportAction.setPluginInformation(info);
        Main.main.menu.toolsMenu.add(exportAction);
        System.out.println(getPluginDir());
    }
    
    /**
     * Called when the JOSM map frame is created or destroyed. 
     */
    @Override
    public void mapFrameInitialized(MapFrame oldFrame, MapFrame newFrame) {             
        if (oldFrame == null && newFrame != null) { // map frame added
        	
            //TurnRestrictionsListDialog dialog  = new TurnRestrictionsListDialog();
            //add the dialog
            //newFrame.addToggleDialog(dialog);
            //CreateOrEditTurnRestrictionAction.getInstance();
        }
    }

  //  @Override
   // public PreferenceSetting getPreferenceSetting() {
      //  return new PreferenceEditor();
   // }
}
