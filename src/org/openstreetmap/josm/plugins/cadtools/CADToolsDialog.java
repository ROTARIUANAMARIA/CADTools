package org.openstreetmap.josm.plugins.cadtools;

import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class CADToolsDialog extends JPanel {

    private static final long serialVersionUID = -8881292887908764738L;
    // the JOptionPane that contains this dialog. required for the closeDialog() method.
    @SuppressWarnings("unused")
    private JOptionPane optionPane;
    
    private Calculation calculation;
    private Configuration configuration;
    
    private ParamTextField circleSegments;
    private ParamTextField ellipseSegments;
    private ParamTextField cutCornersPercent;
    private ParamTextField cutCornersLength;
    private ParamTextField cutCornersSegments;
    private ParamTextField multiplyCircularCopies;

    public CADToolsDialog(String localVersion) {
        calculation = new Calculation();
        configuration = Configuration.getInstance();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(createOneActionPanel(createBuildingsAligmentButton()));
        addPanelSeparator();
        add(createOneActionPanel(createIrrPolygonToRegPolygonButton()));
        addPanelSeparator();
        add(createOneActionPanel(createStraightWayButton()));
        addPanelSeparator();
        add(createCutCornersPanel());
        addPanelSeparator();
        add(createFromCircleToPolygonPanel());
        addPanelSeparator();
        add(createFromEllipseToPolygonPanel());
        addPanelSeparator();
        add(createOneActionPanel(createMirrorReflectionButton()));
        addPanelSeparator();
        add(createMultiplyCircularPanel());
        addPanelSeparator();
        add(createVersionPanel(localVersion));
        restoreConfiguration();
    }
    
    private JPanel createOneActionPanel(JButton button) {
        NoParamActionPanel panel = new NoParamActionPanel();
        panel.setYAxisLayout();
        panel.add(button);
        return panel;        
    }
        
    private JButton createBuildingsAligmentButton() {
        ActionButton actionButton = new ActionButton("Buildings alignment");
        actionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                buildingsAlignmentButtonPressed();
            }
        });
        return actionButton;
    }

    private JButton createIrrPolygonToRegPolygonButton() {
        ActionButton actionButton = new ActionButton("From irregular polygon to regular polygon");
        actionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                irrPolygonToRegPolygonButtonPressed();
            }
        });
        return actionButton;
    }

    private JPanel createCutCornersPanel() {
        OneActionPanel panel = new OneActionPanel();
        panel.setAllSizes(400, 70);
        panel.setYAxisLayout();
        panel.add(createCutCornersButton());
        Dimension rigidAreaDimension = new Dimension(5,5);
        panel.add(Box.createRigidArea(rigidAreaDimension));
        OneActionPanel paramPanel = new OneActionPanel();
        paramPanel.cleanBorder();
        paramPanel.add(new ParameterLabel("Percent:"));
        paramPanel.add(Box.createRigidArea(rigidAreaDimension));
        cutCornersPercent = new ParamTextField();
        cutCornersPercent.setAllSizes(30, 20);
        paramPanel.add(cutCornersPercent);
        paramPanel.add(Box.createRigidArea(rigidAreaDimension));
        paramPanel.add(Box.createRigidArea(rigidAreaDimension));
        paramPanel.add(Box.createRigidArea(rigidAreaDimension));
        paramPanel.add(new ParameterLabel("Length:"));
        paramPanel.add(Box.createRigidArea(rigidAreaDimension));
        cutCornersLength = new ParamTextField();
        cutCornersLength.setAllSizes(30, 20);
        paramPanel.add(cutCornersLength);
        paramPanel.add(Box.createRigidArea(rigidAreaDimension));
        paramPanel.add(Box.createRigidArea(rigidAreaDimension));
        paramPanel.add(Box.createRigidArea(rigidAreaDimension));
        paramPanel.add(new ParameterLabel("Segments:"));
        paramPanel.add(Box.createRigidArea(rigidAreaDimension));
        cutCornersSegments = new ParamTextField();
        cutCornersSegments.setAllSizes(30, 20);
        cutCornersSegments.setEnabled(false);
        paramPanel.add(cutCornersSegments);
        panel.add(paramPanel);        
        return panel;                
    }
    
    private JPanel createVersionPanel(String localVersion) {
        NoParamActionPanel panel = new NoParamActionPanel();
        panel.setYAxisLayout();
        panel.cleanBorder();
        ParameterLabel versionLabel = new ParameterLabel("Version: "+localVersion);
        versionLabel.setAlignmentX(RIGHT_ALIGNMENT);
        Dimension rigidAreaDimension = new Dimension(5,10);
        panel.add(Box.createRigidArea(rigidAreaDimension));
        panel.add(versionLabel);
        return panel;                 
    }
    
    private JButton createCutCornersButton() {
        ActionButton actionButton = new ActionButton("'Cut' corners");
        actionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                cutCornersButtonPressed();
            }
        });
        return actionButton;
    }
    
    private JButton createStraightWayButton() {
        ActionButton actionButton = new ActionButton("Straight way");
        actionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                straightWayButtonPressed();
            }
        });
        return actionButton;
    }
    
    private JPanel createFromCircleToPolygonPanel() {
        OneActionPanel panel = new OneActionPanel();
        panel.setAllSizes(400, 70);
        panel.setYAxisLayout();
        panel.add(createFromPolygonToCircleButton());
        Dimension rigidAreaDimension = new Dimension(5,5);
        panel.add(Box.createRigidArea(rigidAreaDimension));
        OneActionPanel paramPanel = new OneActionPanel();
        paramPanel.cleanBorder();
        paramPanel.add(new ParameterLabel("Segments:"));
        paramPanel.add(Box.createRigidArea(rigidAreaDimension));
        circleSegments = new ParamTextField();
        circleSegments.setAllSizes(30, 20);
        paramPanel.add(circleSegments);
        panel.add(paramPanel);        
        return panel;                
    }

    private JButton createFromPolygonToCircleButton() {
        ActionButton actionButton = new ActionButton("From polygon to circle");
        actionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                fromPolygonToCircleButtonPressed();
            }
        });
        return actionButton;
    }
    
    private JPanel createFromEllipseToPolygonPanel() {
        OneActionPanel panel = new OneActionPanel();
        panel.setAllSizes(400, 70);
        panel.setYAxisLayout();
        panel.add(createFromPolygonToEllipseButton());
        Dimension rigidAreaDimension = new Dimension(5,5);
        panel.add(Box.createRigidArea(rigidAreaDimension));
        OneActionPanel paramPanel = new OneActionPanel();
        paramPanel.cleanBorder();
        paramPanel.add(new ParameterLabel("Segments:"));
        paramPanel.add(Box.createRigidArea(rigidAreaDimension));
        ellipseSegments = new ParamTextField();
        ellipseSegments.setAllSizes(30, 20);
        paramPanel.add(ellipseSegments);
        panel.add(paramPanel);        
        return panel;                
    }
    
    private JButton createFromPolygonToEllipseButton() {
        ActionButton actionButton = new ActionButton("From polygon to ellipse");
        actionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                fromPolygonToEllipseButtonPressed();
            }
        });
        return actionButton;
    }
    
    private JButton createMirrorReflectionButton() {
        ActionButton actionButton = new ActionButton("Mirror reflection");
        actionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                mirrorReflectionButtonPressed();
            }
        });
        return actionButton;
    }

    private JPanel createMultiplyCircularPanel() {
        OneActionPanel panel = new OneActionPanel();
        panel.setAllSizes(400, 70);
        panel.setYAxisLayout();
        panel.add(createMultiplyCircularButton());
        Dimension rigidAreaDimension = new Dimension(5,5);
        panel.add(Box.createRigidArea(rigidAreaDimension));
        OneActionPanel paramPanel = new OneActionPanel();
        paramPanel.cleanBorder();
        paramPanel.add(new ParameterLabel("Numer of copies:"));
        paramPanel.add(Box.createRigidArea(rigidAreaDimension));
        multiplyCircularCopies = new ParamTextField();
        multiplyCircularCopies.setAllSizes(30, 20);
        paramPanel.add(multiplyCircularCopies);
        panel.add(paramPanel);        
        return panel;                
    }

    private JButton createMultiplyCircularButton() {
        ActionButton actionButton = new ActionButton("Multiply circular");
        actionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                MultiplyCircularButtonPressed();
            }
        });
        return actionButton;
    }
    
    private void restoreConfiguration() {
        cutCornersPercent.setText(configuration.getCutCornersPercent());
        cutCornersLength.setText(configuration.getCutCornetsLenght());
        cutCornersSegments.setText(configuration.getCurCornersSegments());
        circleSegments.setText(configuration.getCircleSegments());
        ellipseSegments.setText(configuration.getEllipseSegments());
        multiplyCircularCopies.setText(configuration.getMultiplyCircularCopies());
    }
    
    public void storeConfiguration() {
        configuration.setCutCornersPercent(cutCornersPercent.getText());
        configuration.setCutCornetsLenght(cutCornersLength.getText());
        configuration.setCurCornersSegments(cutCornersSegments.getText());
        configuration.setCircleSegments(circleSegments.getText());
        configuration.setEllipseSegments(ellipseSegments.getText());
        configuration.setMultiplyCircularCopies(multiplyCircularCopies.getText());
    }

    private void addPanelSeparator() {        
        Dimension rigidAreaDimension = new Dimension(0,10);
        add(Box.createRigidArea(rigidAreaDimension));        
    }
    
    private void buildingsAlignmentButtonPressed() {
        calculation.buildingsAlignment();
    }
    
    private void irrPolygonToRegPolygonButtonPressed() {
        calculation.changePolygon();
    }
    
    private void straightWayButtonPressed() {
        calculation.makeWayStraight();
    }
    
    private void cutCornersButtonPressed() {
        CutCornersType cutCornersType = CutCornersType.BY_PERCENT;
        int percent = 0;
        int lenght = 0;
        if (cutCornersPercent.getText().equals("") && cutCornersLength.getText().equals("")) {
            showValidatorWarning("Percent or length must be entered.");
            return;
        }
        if (!cutCornersPercent.getText().equals("") && !cutCornersLength.getText().equals("")) {
            showValidatorWarning("Only one of percent and length can be entered.");
            return;
        }
        if (!cutCornersPercent.getText().equals("")) {
            if (!validateInteger(cutCornersPercent.getText(), "Percent", 49)) {
                return;
            }
            cutCornersType = CutCornersType.BY_PERCENT;
            percent = Integer.parseInt(cutCornersPercent.getText());
        }
        if (!cutCornersLength.getText().equals("")) {
            if (!validateInteger(cutCornersLength.getText(), "Lenght", calculation.countMinDistanceBetweenCornersOfAllWays())) {
                return;
            }    
            cutCornersType = CutCornersType.BY_LENGHT;
            lenght = Integer.parseInt(cutCornersLength.getText());
        }
        calculation.cutCorners(cutCornersType, percent, lenght);
    }
    
    private void fromPolygonToCircleButtonPressed() {
        if (validateInteger(circleSegments.getText(), "Number of segments", Integer.MAX_VALUE)) {
            calculation.drawCircle(circleSegments.getText());            
        }
    }

    private void fromPolygonToEllipseButtonPressed() {
        if (validateInteger(ellipseSegments.getText(), "Number of segments", Integer.MAX_VALUE)) {
            calculation.drawEllipse(ellipseSegments.getText());
        }
    }
    
    private void mirrorReflectionButtonPressed() {
        calculation.mirrorReflection();
    }

    private void MultiplyCircularButtonPressed() {
        if (validateInteger(multiplyCircularCopies.getText(), "Number of copies", Integer.MAX_VALUE)) {
            calculation.multiplyCircular(Integer.parseInt(multiplyCircularCopies.getText()));            
        }
    }
    
    private boolean validateInteger(String value, String label, int maxValue) {
        int i;
        if (value.equals("")) {
            showValidatorWarning(label+" must be entered.");
            return false;
        }
        try {
            i = Integer.parseInt(value);        
        }
        catch (NumberFormatException e) {
            showValidatorWarning(label+" must be integer.");
            return false;            
        }
        if (i <= 0) {
            showValidatorWarning(label+" must be greater than 0.");
            return false;                        
        }
        if (i > maxValue) {
            showValidatorWarning(label+" cannot be greater than "+maxValue+".");            
            return false;                        
        }
        return true;
    }
    
    private void showValidatorWarning(String warningText) {
        JOptionPane.showMessageDialog(null,
                warningText, "Allert Message",
                JOptionPane.WARNING_MESSAGE);        
    }

    /**
     * Has to be called after this dialog has been added to a JOptionPane.
     * 
     * @param optionPane
     */
    public void setOptionPane(JOptionPane optionPane) {
        this.optionPane = optionPane;
    }
}
