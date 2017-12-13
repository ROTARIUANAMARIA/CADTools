package org.openstreetmap.josm.plugins.cadtools;

import java.util.ArrayList;
import java.util.List;

import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.plugins.cadtools.linearity.Line;

public class MultiplyCircularCalculation {
    
    private Way pattern;
    private Node centerNode;
    private Point centerPoint;    
    private int multiplyNumber;
    private Node[][] multipledNodes;
    
    public MultiplyCircularCalculation(int multiplyNumber) {
        this.multiplyNumber = multiplyNumber;
    }
    
    public void setPatern(Way patern) {
        this.pattern = patern;
    }
    
    public void setCenterNode(Node node) {
        this.centerNode = node;
    }
    
    private void setCenterPoint() {
        centerPoint = new Point(centerNode.getEastNorth().east(), centerNode.getEastNorth().north());
    }
    
    private void setMultipledNodes() {
        multipledNodes = new Node[pattern.getNodesCount()][multiplyNumber];
    }
    
    private double countAngle(Node node) {
        Line line;
        double angle;
        Point nodePoint = new Point(node.getEastNorth().east(), node.getEastNorth().north());
        line = new Line(nodePoint, centerPoint);
        angle = line.getAngle();
        if (nodePoint.x < centerPoint.x) {
            angle = angle + Math.PI;
        }
        return angle;
    }
    
    private double getDistanceFromCenterPoint(Node node) {
        return Point.distance(centerPoint, new Point(node.getEastNorth().east(), node.getEastNorth().north()));
    }
    
    private void mutiplyNode(Node node, int nodeNo) {
        double slice = 2 * Math.PI / multiplyNumber;
        double nodeAngle = countAngle(node);            
        double radius = getDistanceFromCenterPoint(node);
        for (int i = 0; i < multiplyNumber; i++) {
            double angleSlice = slice * i;
            double newX = (centerPoint.x + radius * Math.cos(nodeAngle + angleSlice));
            double newY = (centerPoint.y + radius * Math.sin(nodeAngle + angleSlice));
            multipledNodes[nodeNo][i] = new Node(new EastNorth(newX, newY));    
        }        
    }
    
    private void displayMultipledNodes() {
        List<Node> mutipledNodesList;
        Way multipledWay;
        for (int m = 1; m < multiplyNumber; m++) {
            mutipledNodesList = new ArrayList<>();
            for (int n = 0; n < multipledNodes.length; n++) {
                mutipledNodesList.add(multipledNodes[n][m]);
                getDataSet().addPrimitive(multipledNodes[n][m]);
            }
            multipledWay = new Way();
            multipledWay.setNodes(mutipledNodesList);
            getDataSet().addPrimitive(multipledWay);
        }
    }
    
    private DataSet getDataSet() {
        return MainApplication.getLayerManager().getEditDataSet();
    }

    public void multiplyCircular() {
        setCenterPoint();
        setMultipledNodes();
        for (int i = 0; i < pattern.getNodesCount(); i++){            
            mutiplyNode(pattern.getNode(i), i);
        }
        displayMultipledNodes();
    }

}
