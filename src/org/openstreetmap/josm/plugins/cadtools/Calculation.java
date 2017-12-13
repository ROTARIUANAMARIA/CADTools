package org.openstreetmap.josm.plugins.cadtools;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JOptionPane;

import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.osm.WaySegment;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.plugins.cadtools.linearity.Line;
import org.openstreetmap.josm.tools.Geometry;
import org.openstreetmap.josm.tools.Logging;

public class Calculation {
    
    private List<Double> editedAngles;
    private double epsilon = Math.pow(10, -3);

    public Calculation() {
        
    }
    
    public void generalization(Collection<Way> ways) {
        for (Way way : ways) {
            if (way.isClosed() == true) {
                proceedGeneralization(way);
                while (editedAngles.isEmpty() == false) {
                    proceedGeneralization(way);
                }
                for (int i = 0; i < 5; i++) {
                    WaySegment waySegm = findSegment(way);
                    if (waySegm != null) {
                        WaySegment buildingSegment = ShapeMath.getClosestSegment(way, waySegm);
                        ShapeMath.align(waySegm, buildingSegment);
                    }
                }
            }
        }
    }
    
    public WaySegment findSegment(Way closedWay) {

        DataSet data = MainApplication.getLayerManager().getEditLayer().data;
        int nodeIndex = Integer.MAX_VALUE;
        double min = Double.MAX_VALUE;
        Collection<Way> ways = data.getWays();
        Collection<Way> roads = new ArrayList<>();
        Way minWay = new Way();
        for (Way w : ways)
            if (w.isClosed() == false)
                roads.add(w);
        EastNorth buildingCenter = ShapeMath.getCentroid(closedWay);
        for (Way road : roads) {
            for (int i = 0; i < road.getNodes().size(); i++) {
                double dist = Point.distance(
                        new Point(buildingCenter.east(), buildingCenter.north()), 
                        new Point(road.getNode(i).getEastNorth().east(), 
                                  road.getNode(i).getEastNorth().north()));
                if (dist < min) {
                    min = dist;
                    minWay = road;
                    nodeIndex = i;
                    ShapeMath.containingWay = minWay;
                }
            }
        }
        if (!roads.isEmpty()) {

            if (nodeIndex == 0)
                return new WaySegment(minWay, nodeIndex);
            else if (nodeIndex == minWay.getNodes().size() - 1)
                return new WaySegment(minWay, nodeIndex - 1);
            if (!(nodeIndex == 0 || nodeIndex == minWay.getNodes().size() - 1 || nodeIndex == Integer.MAX_VALUE)) {

                double firstDist  = Point.pointLineDistance(new Point(buildingCenter.east(), buildingCenter.north()), new WaySegment(minWay, nodeIndex - 1));
                double secondDist = Point.pointLineDistance(new Point(buildingCenter.east(), buildingCenter.north()), new WaySegment(minWay, nodeIndex));
                if (firstDist < secondDist) {
                    WaySegment test = new WaySegment(minWay, nodeIndex - 1);
                    if (Point.projectiontOnLine(
                            new Point(test.getFirstNode().getEastNorth().east(), test.getFirstNode().getEastNorth().north()), 
                            new Point(test.getSecondNode().getEastNorth().east(), test.getSecondNode().getEastNorth().north()), 
                            new Point(buildingCenter.east(), buildingCenter.north())) != null) {
                        return test;
                    } else
                        return new WaySegment(minWay, nodeIndex);
                } else {
                    WaySegment test = new WaySegment(minWay, nodeIndex);
                    if (Point.projectiontOnLine(
                            new Point(test.getFirstNode().getEastNorth().east(), test.getFirstNode().getEastNorth().north()), 
                            new Point(test.getSecondNode().getEastNorth().east(), test.getSecondNode().getEastNorth().north()), 
                            new Point(buildingCenter.east(), buildingCenter.north())) != null) {
                        return test;
                    } else
                        return new WaySegment(minWay, nodeIndex - 1);
                }
            }
        }
        return null;
    }
    
    public void proceedGeneralization(Way way) {

        editedAngles = new ArrayList<>();
        List<Node> currentWayNodes = way.getNodes();
        System.out.println(way.getNodes().size());
        for (int i = 0; i < currentWayNodes.size() - 1; i++) {
            if (i + 1 >= currentWayNodes.size() - 1) {
                double angle = Geometry.getCornerAngle(
                        currentWayNodes.get(i).getEastNorth(), 
                        currentWayNodes.get((i + 1) - (currentWayNodes.size() - 1)).getEastNorth(), 
                        currentWayNodes.get((i + 2) - (currentWayNodes.size() - 1)).getEastNorth());
                System.out.println(Math.toDegrees(angle));
                if (Math.abs(Math.toDegrees(angle)) >= 84 && Math.abs(Math.toDegrees(angle)) <= 96) {
                    if (Math.abs(90 - (Math.abs(Math.toDegrees(angle)))) > epsilon) {
                        if (Math.toDegrees(angle) < 0) {
                        	Logging.info("Rotation Angle is :" + (-90 - Math.toDegrees(angle)));
                            executeRotation(Math.toRadians(-1.0 * (-90 - Math.toDegrees(angle))), new WaySegment(way, (i + 1) - (currentWayNodes.size() - 1)));
                            editedAngles.add(Math.abs(Math.toDegrees(Geometry.getCornerAngle(
                                    currentWayNodes.get(i).getEastNorth(),
                                    currentWayNodes.get((i + 1) - (currentWayNodes.size() - 1)).getEastNorth(),
                                    currentWayNodes.get((i + 2) - (currentWayNodes.size() - 1)).getEastNorth()))));
                        } else {
                        	Logging.info("Rotation Angle is :" + (90 - Math.toDegrees(angle)));
                            executeRotation(Math.toRadians(-1.0 * (90 - Math.toDegrees(angle))), new WaySegment(way, (i + 1) - (currentWayNodes.size() - 1)));
                            editedAngles.add(Math.abs(Math.toDegrees(Geometry.getCornerAngle(
                                    currentWayNodes.get(i).getEastNorth(),
                                    currentWayNodes.get((i + 1) - (currentWayNodes.size() - 1)).getEastNorth(),
                                    currentWayNodes.get((i + 2) - (currentWayNodes.size() - 1)).getEastNorth()))));
                        }
                    }
                }
            } else {
                double angle = Geometry.getCornerAngle(
                        currentWayNodes.get(i).getEastNorth(), 
                        currentWayNodes.get(i + 1).getEastNorth(),
                        currentWayNodes.get(i + 2).getEastNorth());
                System.out.println(Math.toDegrees(angle));
                if (Math.abs(Math.toDegrees(angle)) >= 84 && Math.abs(Math.toDegrees(angle)) <= 96) {
                    if (Math.abs(90 - (Math.abs(Math.toDegrees(angle)))) > epsilon) {
                        if (Math.toDegrees(angle) < 0) {
                            System.out.println("Rotation Angle is :" + (-90 - Math.toDegrees(angle)));
                            executeRotation(Math.toRadians(-1.0 * (-90 - Math.toDegrees(angle))), new WaySegment(way, (i + 1)));
                            editedAngles.add(Math.abs(Math.toDegrees(Geometry.getCornerAngle(
                                    currentWayNodes.get(i).getEastNorth(), 
                                    currentWayNodes.get(i + 1).getEastNorth(), 
                                    currentWayNodes.get(i + 2).getEastNorth()))));
                        } else {
                            System.out.println("Rotation Angle is :" + (90 - Math.toDegrees(angle)));
                            executeRotation(Math.toRadians(-1.0 * (90 - Math.toDegrees(angle))), new WaySegment(way, (i + 1)));
                            editedAngles.add(Math.abs(Math.toDegrees(Geometry.getCornerAngle(
                                    currentWayNodes.get(i).getEastNorth(), 
                                    currentWayNodes.get(i + 1).getEastNorth(), 
                                    currentWayNodes.get(i + 2).getEastNorth()))));
                        }
                    }
                }
            }
        }
    }
    
    private void executeRotation(double angle, WaySegment segment) {
        double centerX = segment.getFirstNode().getEastNorth().east();
        double centerY = segment.getFirstNode().getEastNorth().north();
        double x = segment.getSecondNode().getEastNorth().east();
        double y = segment.getSecondNode().getEastNorth().north();

        double newX = centerX + (x - centerX) * Math.cos(angle) - (y - centerY) * Math.sin(angle);
        double newY = centerY + (x - centerX) * Math.sin(angle) + (y - centerY) * Math.cos(angle);
        EastNorth eastNorth = new EastNorth(newX, newY);
        segment.getSecondNode().setEastNorth(eastNorth);

        MainApplication.getMap().repaint();
    }
    
    public void changePolygon() {
        if (MainApplication.getMap() != null) {
            Collection<Way> selWays = getDataSet().getSelectedWays();
            for (Way w : selWays) {
                if (w.isClosed()) {
                    EastNorth center = ShapeMath.getCentroid(w);
                    double max = Double.MIN_VALUE;
                    for (Node n : w.getNodes()) {
                        double distance = Point.distance(
                                new Point(n.getEastNorth().east(), n.getEastNorth().north()), 
                                new Point(center.east(), center.north()));
                        if (distance > max)
                            max = distance;
                    }

                    drawCirclePoints(w.getNodes().size() - 1, max, new Node(center));
                    for (Node nd : w.getNodes())
                        getDataSet().removePrimitive(nd);
                    getDataSet().removePrimitive(w);
                }
            }
        }
    }
    
    private DataSet getDataSet() {
        return MainApplication.getLayerManager().getEditDataSet();
    }
    
    private void drawCirclePoints(int points, double radius, Node center) {
        Way polygon = new Way();
        double slice = 2 * Math.PI / points;
        for (int i = 0; i < points; i++) {
            double angle = slice * i;
            double newX = (center.getEastNorth().east() + radius * Math.cos(angle));
            double newY = (center.getEastNorth().north() + radius * Math.sin(angle));
            Node p = new Node(new EastNorth(newX, newY));
            getDataSet().addPrimitive(p);
            polygon.addNode(p);
        }
        polygon.addNode(polygon.getNodes().get(0));
        getDataSet().addPrimitive(polygon);
        MainApplication.getMap().repaint();
    }
    
    public void drawEllipse(String pointsNo) {
        Collection<Node> selNodes = getDataSet().getSelectedNodes();
        Collection<Way> selWays = getDataSet().getSelectedWays();
        if (selWays.isEmpty() == true) {
            if (selNodes.size() == 3) {
                List<Node> nodes = new ArrayList<>();
                for (Node n : selNodes)
                    nodes.add(n);
                Node A = nodes.get(0);
                Node B = nodes.get(1);
                Node center = nodes.get(2);
                double h = center.getEastNorth().east();
                double k = center.getEastNorth().north();
                double Ax = A.getEastNorth().east();
                double Ay = A.getEastNorth().north();
                double Bx = B.getEastNorth().east();
                double By = B.getEastNorth().north();
                double alpha = (Ay - k) * (Ay - k);
                double betha = (By - k) * (By - k);
                System.out.println(alpha+" "+betha);
                if (alpha > betha) {

                    double gamma = -1* (alpha / betha);
                    double a = Math.abs( Math.sqrt(Math.abs(((Ax - h) * (Ax - h) + gamma * (Bx - h) * (Bx - h)) / (gamma + 1))));
                    double b = Math.abs(Math.sqrt(Math.abs(((Ay - k) * (Ay - k)) / (1 - ((Ax - h) * (Ax - h)) / (a * a)))));
                    if (!pointsNo.equals("")) {
                        drawEllipsePoints(4 * Integer.parseInt(pointsNo), a, b, center);
                    }                    
                } else {
                    double gamma = -1 * (betha / alpha);
                    System.out.println(gamma);
                    double a = Math.abs(Math.sqrt(Math.abs(((Ax - h) * (Ax - h) * gamma + (Bx - h) * (Bx - h)) / (gamma + 1))));
                    double b = Math.abs(Math.sqrt(Math.abs(((Ay - k) * (Ay - k)) / (1 - ((Ax - h) * (Ax - h)) / (a * a)))));
                    if (!pointsNo.equals("")) {
                        drawEllipsePoints(4 * Integer.parseInt(pointsNo), a, b, center);
                    }
                }
                for (Node n : selNodes)
                    getDataSet().removePrimitive(n);
                MainApplication.getMap().repaint();
                
            } else {
                JOptionPane.showMessageDialog(null,
                        tr("Please select only three nodes that are representative for the imaginary ellipse (these nodes must not be connected) !"),
                        tr("Alert Message"), JOptionPane.WARNING_MESSAGE);
            }
        } else {
            for (Way way : selWays) {
                if (way.getNodes().size() == 5 && way.isClosed() == true) {
                    EastNorth center = ShapeMath.getCentroid(way);
                    double b = Point.distance(new Point(way.getNodes().get(0).getEastNorth().east(), way.getNodes().get(0).getEastNorth().north()), 
                                              new Point(way.getNodes().get(1).getEastNorth().east(), way.getNodes().get(1).getEastNorth().north())) / 2;
                    double a = Point.distance(new Point(way.getNodes().get(1).getEastNorth().east(), way.getNodes().get(1).getEastNorth().north()),
                                              new Point(way.getNodes().get(2).getEastNorth().east(), way.getNodes().get(2).getEastNorth().north())) / 2;
                    if (!pointsNo.equals("")){
                        drawEllipsePoints(4 * Integer.parseInt(pointsNo), a, b, new Node(center));
                    }
                }
            }
            MainApplication.getMap().repaint();
        }
    }

    public void drawCircle(String pointsNo) {
        Collection<Node> selNodes = getDataSet().getSelectedNodes();
        Collection<Way> selWays = getDataSet().getSelectedWays();
        if (selNodes.size() != 3 || !selWays.isEmpty())
            JOptionPane.showMessageDialog(null,
                    "Please select only three nodes that are represantative for the imaginary circle(these nodes must not be connected) !", "Allert Message",
                    JOptionPane.WARNING_MESSAGE);
        else {
            List<Node> nodes = new ArrayList<>();
            for (Node n : selNodes)
                nodes.add(n);
            Node A = nodes.get(0);
            Node B = nodes.get(1);
            Node C = nodes.get(2);
            double Ax = A.getEastNorth().east();
            double Ay = A.getEastNorth().north();
            double Bx = B.getEastNorth().east();
            double By = B.getEastNorth().north();
            double Cx = C.getEastNorth().east();
            double Cy = C.getEastNorth().north();
            double centerX = ((Ax * Ax + Ay * Ay) * (By - Cy) + (Bx * Bx + By * By) * (Cy - Ay) + (Cx * Cx + Cy * Cy) * (Ay - By))
                    / (2 * (Ax * (By - Cy) - Ay * (Bx - Cx) + Bx * Cy - Cx * By));
            double centerY = ((Ax * Ax + Ay * Ay) * (Cx - Bx) + (Bx * Bx + By * By) * (Ax - Cx) + (Cx * Cx + Cy * Cy) * (Bx - Ax))
                    / (2 * (Ax * (By - Cy) - Ay * (Bx - Cx) + Bx * Cy - Cx * By));
            double radius = Math.sqrt((centerX - Ax) * (centerX - Ax) + (centerY - Ay) * (centerY - Ay));
            if (!pointsNo.equals("")) {
                drawCirclePoints(Integer.parseInt(pointsNo), radius, new Node(new EastNorth(centerX, centerY)));
                for (Node n : selNodes)
                    getDataSet().removePrimitive(n);
                MainApplication.getMap().repaint();
            }
        }
    }

    private void drawEllipsePoints(int points, double a_radius, double b_radius, Node center) {
        Way ellipse = new Way();
        double slice = 2 * Math.PI / points;
        for (int i = 0; i < points; i++) {
            double angle = slice * i;
            double newX = center.getEastNorth().east() + a_radius * Math.cos(angle);
            double newY = center.getEastNorth().north() + b_radius * Math.sin(angle);
            Node p = new Node(new EastNorth(newX, newY));
            getDataSet().addPrimitive(p);
            ellipse.addNode(p);
        }
        ellipse.addNode(ellipse.getNodes().get(0));
        getDataSet().addPrimitive(ellipse);
        MainApplication.getMap().repaint();
    }

    public void makeWayStraight() {
        if (MainApplication.getMap() != null) {
            Collection<Node> selectedNodes = getDataSet().getSelectedNodes();
            List<Node> toDelete = new ArrayList<>();
            Way way = new Way();

            if (selectedNodes.size() > 2 || selectedNodes.size() < 2)
                JOptionPane.showMessageDialog(null, "Please select only two nodes that represent the extremities of the wall to edit !", "Allert Message",
                        JOptionPane.WARNING_MESSAGE);
            else {
                List<Node> nodes = new ArrayList<>();
                for (Node n : selectedNodes)
                    nodes.add(n);
                Node n1 = nodes.get(0);
                Node n2 = nodes.get(1);
                for (Way w : getDataSet().getWays())
                    if (w.containsNode(n1) && w.containsNode(n2)) {
                        way = w;
                        break;
                    }
                if (way.getNodesCount() == 0)
                    JOptionPane.showMessageDialog(null, "These two nodes must be in the same closed way !", "Allert Message", JOptionPane.WARNING_MESSAGE);
                else {
                    int i1 = way.getNodes().indexOf(n1);
                    int i2 = way.getNodes().indexOf(n2);
                    System.out.println(i1 + " " + i2);
                    if (i1 < i2) {
                        for (int index = i1 + 1; index < i2; index++) {
                            Node nodeToDelete = way.getNodes().get(index);
                            toDelete.add(nodeToDelete);
                        }
                    } else {
                        for (int index = i2 + 1; index < i1; index++) {
                            Node nodeToDelete = way.getNodes().get(index);
                            toDelete.add(nodeToDelete);
                        }
                    }
                }
            }
            for (Node nd : toDelete) {
                way.removeNode(nd);
                getDataSet().removePrimitive(nd);
            }

            MainApplication.getMap().repaint();
        }
    }

    public void cutCorners(CutCornersType type, int percent, int length) {
        if (MainApplication.getMap() != null) {
            Collection<Way> ways = new ArrayList<>();
            ways = getDataSet().getSelectedWays();
            for (Way w : ways)
                if (w.isClosed())
                    editCorners(w, type, percent, length);
        }
    }
    
    public int countMinDistanceBetweenCornersOfAllWays() {
        int distance = Integer.MAX_VALUE;
        int d;
        if (MainApplication.getMap() != null) {
            Collection<Way> ways = new ArrayList<>();
            ways = getDataSet().getSelectedWays();
            for (Way w : ways) {
                if (w.isClosed()) {
                    d = countMinDistanceBetweenCorners(w);
                    if (d < distance) {
                        distance = d;
                    }
                }
            }
        }        
        return (int)Math.ceil(distance / 2);
    }
    
    private int countMinDistanceBetweenCorners(Way building) {
        List<Node> currentWayNodes = building.getNodes();
        Double distance = Double.MAX_VALUE;
        for (int i = 0; i < currentWayNodes.size() - 1; i++) {
            if (i + 1 >= currentWayNodes.size() - 1) {
                Double r = Point.distance(
                        new Point(currentWayNodes.get(i).getEastNorth().east(), currentWayNodes.get(i).getEastNorth().north()),
                        new Point(currentWayNodes.get((i + 1) - (currentWayNodes.size() - 1)).getEastNorth().east(), currentWayNodes
                                .get((i + 1) - (currentWayNodes.size() - 1)).getEastNorth().north()));
                if (r < distance) {
                    distance = r;
                }
                r = Point.distance(
                        new Point(currentWayNodes.get((i + 1) - (currentWayNodes.size() - 1)).getEastNorth().east(), currentWayNodes
                                .get((i + 1) - (currentWayNodes.size() - 1)).getEastNorth().north()),
                        new Point(currentWayNodes.get((i + 2) - (currentWayNodes.size() - 1)).getEastNorth().east(), currentWayNodes
                                .get((i + 2) - (currentWayNodes.size() - 1)).getEastNorth().north()));
                if (r < distance) {
                    distance = r;
                }
            } else {
                Double r = Point.distance(new Point(currentWayNodes.get(i).getEastNorth().east(), currentWayNodes.get(i).getEastNorth().north()), new Point(
                        currentWayNodes.get(i + 1).getEastNorth().east(), currentWayNodes.get(i + 1).getEastNorth().north()));
                if (r < distance) {
                    distance = r;
                }
                r = Point.distance(new Point(currentWayNodes.get(i + 1).getEastNorth().east(), currentWayNodes.get(i + 1).getEastNorth().north()), new Point(
                        currentWayNodes.get(i + 2).getEastNorth().east(), currentWayNodes.get(i + 2).getEastNorth().north()));
                if (r < distance) {
                    distance = r;
                }
            }
        }    
        return (int)Math.ceil(distance);
    }
    
    private Double getRadius(Double distance, CutCornersType type, int percent, int length) {
        Double radius = 0.0;
        switch (type) {
            case BY_PERCENT:
                radius = distance * percent / 100;
                break;
            case BY_LENGHT:
                radius = (double) length;
                break;
        }
        return radius;
    }

    private void editCorners(Way building, CutCornersType type, int percent, int length) {
        List<Node> currWayNodes = new ArrayList<>();
        List<Node> currentWayNodes = building.getNodes();
        for (int i = 0; i < currentWayNodes.size() - 1; i++) {
            if (i + 1 >= currentWayNodes.size() - 1) {
                Double r = Point.distance(
                        new Point(currentWayNodes.get(i).getEastNorth().east(), currentWayNodes.get(i).getEastNorth().north()),
                        new Point(currentWayNodes.get((i + 1) - (currentWayNodes.size() - 1)).getEastNorth().east(), currentWayNodes
                                .get((i + 1) - (currentWayNodes.size() - 1)).getEastNorth().north()));

                Node newNode1 = getIntersectionNode(currentWayNodes.get((i + 1) - (currentWayNodes.size() - 1)), currentWayNodes.get(i),
                        currentWayNodes.get((i + 1) - (currentWayNodes.size() - 1)), getRadius(r, type, percent, length));
                System.out.println(getRadius(r, type, percent, length)+" "+ MainApplication.getMap().mapView.getScale());
                getDataSet().addPrimitive(newNode1);
                currWayNodes.add(newNode1);

                r = Point.distance(
                        new Point(currentWayNodes.get((i + 1) - (currentWayNodes.size() - 1)).getEastNorth().east(), currentWayNodes
                                .get((i + 1) - (currentWayNodes.size() - 1)).getEastNorth().north()),
                        new Point(currentWayNodes.get((i + 2) - (currentWayNodes.size() - 1)).getEastNorth().east(), currentWayNodes
                                .get((i + 2) - (currentWayNodes.size() - 1)).getEastNorth().north()));
                Node newNode2 = getIntersectionNode(currentWayNodes.get((i + 1) - (currentWayNodes.size() - 1)),
                        currentWayNodes.get((i + 1) - (currentWayNodes.size() - 1)), currentWayNodes.get((i + 2) - (currentWayNodes.size() - 1)), getRadius(r, type, percent, length));
                getDataSet().addPrimitive(newNode2);
                currWayNodes.add(newNode2);

            } else {
                Double r = Point.distance(new Point(currentWayNodes.get(i).getEastNorth().east(), currentWayNodes.get(i).getEastNorth().north()), new Point(
                        currentWayNodes.get(i + 1).getEastNorth().east(), currentWayNodes.get(i + 1).getEastNorth().north()));

                Node newNode1 = getIntersectionNode(currentWayNodes.get(i + 1), currentWayNodes.get(i), currentWayNodes.get(i + 1), getRadius(r, type, percent, length));
                getDataSet().addPrimitive(newNode1);
                currWayNodes.add(newNode1);
                r = Point.distance(new Point(currentWayNodes.get(i + 1).getEastNorth().east(), currentWayNodes.get(i + 1).getEastNorth().north()), new Point(
                        currentWayNodes.get(i + 2).getEastNorth().east(), currentWayNodes.get(i + 2).getEastNorth().north()));
                Node newNode2 = getIntersectionNode(currentWayNodes.get(i + 1), currentWayNodes.get(i + 1), currentWayNodes.get(i + 2), getRadius(r, type, percent, length));
                getDataSet().addPrimitive(newNode2);
                currWayNodes.add(newNode2);
            }
        }
        currWayNodes.add(currWayNodes.get(0));

        for (Node n : currentWayNodes) {
            getDataSet().removePrimitive(n);
        }

        building.setNodes(currWayNodes);
        MainApplication.getMap().repaint();
    }

    private Node getIntersectionNode(Node center, Node firstPoint, Node secondPoint, double radius) {
        double LAB = Point.distance(new Point(firstPoint.getEastNorth().east(), firstPoint.getEastNorth().north()), new Point(
                secondPoint.getEastNorth().east(), secondPoint.getEastNorth().north()));
        // compute the direction vector D from A to B
        double Dx = (secondPoint.getEastNorth().east() - firstPoint.getEastNorth().east()) / LAB;// (Bx-Ax)/LAB
        double Dy = (secondPoint.getEastNorth().north() - firstPoint.getEastNorth().north()) / LAB;// (By-Ay)/LAB
        // Now the line equation is x = Dx*t + Ax, y = Dy*t + Ay with 0 <= t <=
        // 1.

        // compute the value t of the closest point to the circle center (Cx, Cy)
        double t = Dx * (center.getEastNorth().east() - firstPoint.getEastNorth().east()) + Dy
                      * (center.getEastNorth().north() - firstPoint.getEastNorth().north());
        // This is the projection of C on the line from A to B.

        // compute the coordinates of the point E on line and closest to C
        double Ex = t * Dx + firstPoint.getEastNorth().east();
        double Ey = t * Dy + firstPoint.getEastNorth().north();
        double LEC = Point.distance(new Point(center.getEastNorth().east(), center.getEastNorth().north()), new Point(Ex, Ey));
        // test if the line intersects the circle
        if (LEC < radius) {
            // compute distance from t to circle intersection point
            double dt = Math.sqrt(radius * radius - LEC * LEC);

            // compute first intersection point
            double Fx = (t - dt) * Dx + firstPoint.getEastNorth().east();
            double Fy = (t - dt) * Dy + firstPoint.getEastNorth().north();

            // compute second intersection point
            double Gx = (t + dt) * Dx + firstPoint.getEastNorth().east();
            double Gy = (t + dt) * Dy + firstPoint.getEastNorth().north();

            double distance1, distance2;
            distance1 = Point.distance(new Point(Fx, Fy), new Point(firstPoint.getEastNorth().east(), firstPoint.getEastNorth().north()));
            distance2 = Point.distance(new Point(Fx, Fy), new Point(secondPoint.getEastNorth().east(), secondPoint.getEastNorth().north()));
            if (distance1 < LAB && distance2 < LAB)
                return new Node(new EastNorth(Fx, Fy));
            else
                return new Node(new EastNorth(Gx, Gy));
        }
        // else test if the line is tangent to circle
        else if (LEC == radius)
            return new Node(new EastNorth(Ex, Ey));
        else
            return null;
    }

    public void buildingsAlignment() {
        if (MainApplication.getMap() != null) {
            DataSet data = MainApplication.getLayerManager().getEditLayer().data;
            Collection<Way> ways = data.getSelectedWays();
            generalization(ways);
            for (Way way : ways) {
                if (way.isClosed() == true) {
                    List<Node> currentWayNodes = way.getNodes();
                    System.out.println("New angles are :");
                    for (int i = 0; i < currentWayNodes.size() - 1; i++) {
                        if (i + 1 >= currentWayNodes.size() - 1) {
                            double angle = Geometry.getCornerAngle(currentWayNodes.get(i).getEastNorth(),
                                    currentWayNodes.get((i + 1) - (currentWayNodes.size() - 1)).getEastNorth(),
                                    currentWayNodes.get((i + 2) - (currentWayNodes.size() - 1)).getEastNorth());
                            System.out.println(Math.toDegrees(angle));
                        } else {
                            double angle = Geometry.getCornerAngle(currentWayNodes.get(i).getEastNorth(), currentWayNodes.get(i + 1).getEastNorth(),
                                    currentWayNodes.get(i + 2).getEastNorth());
                            System.out.println(Math.toDegrees(angle));
                        }
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "There is no frame loaded !", "Allert Message", JOptionPane.PLAIN_MESSAGE);
        }        
    }
    
    public void mirrorReflection() {
        if (MainApplication.getMap() != null) {
            Collection<Way> ways = new ArrayList<>();
            ways = getDataSet().getSelectedWays();
            for (Way w : ways)
                if (!w.isClosed() && w.getNodesCount() > 2)
                    mirrorReflection(w);
        }    
        MainApplication.getMap().repaint();
    }
    
    private void mirrorReflection(Way w) {
        List<Node> newWayNodes = new ArrayList<>();
        Point givenPoint; 
        Point reflectedPoint;    
        Node reflectedNode;
        Point p1 = new Point(w.getNode(0).getEastNorth().east(), w.getNode(0).getEastNorth().north());
        Point p2 = new Point(w.getNode(w.getNodesCount()-1).getEastNorth().east(), w.getNode(w.getNodesCount()-1).getEastNorth().north());
        Line symmetryAxis = new Line(p1, p2);
        newWayNodes = w.getNodes();
        for (int i = w.getNodesCount()-2; i>=1; i--) {
            givenPoint = new Point(w.getNode(i).getEastNorth().east(), w.getNode(i).getEastNorth().north());
            reflectedPoint = symmetryAxis.symmetricalPoint(givenPoint);
            reflectedNode = new Node(new EastNorth(reflectedPoint.x, reflectedPoint.y));
            newWayNodes.add(reflectedNode);
            getDataSet().addPrimitive(reflectedNode);
        }
        newWayNodes.add(w.getNode(0));
        w.setNodes(newWayNodes);
    }
    
    public void multiplyCircular(int multiplyNumber) {
        if (MainApplication.getMap() != null) {
            Collection<Way> ways = new ArrayList<>();
            Collection<Node> nodes = new ArrayList<Node>();
            Collection<Node> centerNodes = new ArrayList<Node>();
            
            nodes = getDataSet().getSelectedNodes();
            ways = getDataSet().getSelectedWays();
            
            for (Node node : nodes) {
                boolean nodeBelongsToWay = false;
                for (Way way : ways) {
                    if (way.containsNode(node)) {
                        nodeBelongsToWay = true;
                        break;
                    }
                }
                if (!nodeBelongsToWay) {
                    centerNodes.add(node);
                }
            }
            if (centerNodes.size() != 1) {
                JOptionPane.showMessageDialog(null, "You should select one point which is the circle center.", "Allert Message", JOptionPane.PLAIN_MESSAGE);
                return;                
            }
            MultiplyCircularCalculation multiplyCircularCalculation = new MultiplyCircularCalculation(multiplyNumber);
            multiplyCircularCalculation.setCenterNode((Node)centerNodes.toArray()[0]);
            for (Way w : ways) {
                multiplyCircularCalculation.setPatern(w);
                multiplyCircularCalculation.multiplyCircular();
            }            
            MainApplication.getMap().repaint();
        }
    }
}
