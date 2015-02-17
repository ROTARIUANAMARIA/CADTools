/**
 * 
 */
package org.openstreetmap.josm.plugins.cadtools;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.osm.WaySegment;
import org.openstreetmap.josm.tools.Geometry;

import java.awt.Font;

import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class CADToolsDialog extends JPanel {
	// the JOptionPane that contains this dialog. required for the closeDialog()
	// method.
	private JOptionPane optionPane;
	private JCheckBox delete;
	private JComboBox portCombo;
	private JTextField textField;
	private JLabel lblNoOfSegments;
	private JButton btnDraw;
	private JTextField textFieldEllipse;
	private JLabel label;
	private JButton btnDrawE;
	private JButton btnNewButton_2;
	private List<Double> editedAngles;
	double epsilon = Math.pow(10, -3);

	public CADToolsDialog() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);
        
		Icon wallIcon = new ImageIcon("/CADTools/resources/images/symbolicbrickwall.jpg");
		JButton btnMakeWallStraight = new JButton("Straight wall");
		btnMakeWallStraight.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 13));
		btnMakeWallStraight.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				makeWallStraight();
			}
		});

		JButton btnNewButton_1 = new JButton("From irregular polygon to regular polygon");
		btnNewButton_1.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 13));
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				changePolygon();
			}
		});

		btnNewButton_2 = new JButton("Buildings alignment");
		btnNewButton_2.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 13));
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				if (Main.map != null) {
					DataSet data = Main.map.mapView.getEditLayer().data;
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
		});
		GridBagConstraints gbc_btnNewButton_2 = new GridBagConstraints();
		gbc_btnNewButton_2.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton_2.gridx = 4;
		gbc_btnNewButton_2.gridy = 0;
		add(btnNewButton_2, gbc_btnNewButton_2);
		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton_1.gridx = 4;
		gbc_btnNewButton_1.gridy = 1;
		add(btnNewButton_1, gbc_btnNewButton_1);
		GridBagConstraints gbc_btnMakeWallStraight = new GridBagConstraints();
		gbc_btnMakeWallStraight.insets = new Insets(0, 0, 5, 0);
		gbc_btnMakeWallStraight.gridx = 4;
		gbc_btnMakeWallStraight.gridy = 2;
		add(btnMakeWallStraight, gbc_btnMakeWallStraight);

		JButton btnCutCorners = new JButton("' Cut ' corners");
		btnCutCorners.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 13));
		btnCutCorners.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				cutCorners();
			}
		});
		GridBagConstraints gbc_btnCutCorners = new GridBagConstraints();
		gbc_btnCutCorners.insets = new Insets(0, 0, 5, 0);
		gbc_btnCutCorners.gridx = 4;
		gbc_btnCutCorners.gridy = 3;
		add(btnCutCorners, gbc_btnCutCorners);

		JButton btnNewButton = new JButton("From circle to polygon");
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 13));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				enableBtn();

			}
		});
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton.gridx = 4;
		gbc_btnNewButton.gridy = 4;
		add(btnNewButton, gbc_btnNewButton);

		lblNoOfSegments = new JLabel("No. of segments :");
		lblNoOfSegments.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_lblNoOfSegments = new GridBagConstraints();
		gbc_lblNoOfSegments.insets = new Insets(0, 0, 5, 5);
		gbc_lblNoOfSegments.gridx = 3;
		gbc_lblNoOfSegments.gridy = 5;
		add(lblNoOfSegments, gbc_lblNoOfSegments);
		lblNoOfSegments.setEnabled(false);

		textField = new JTextField();
		textField.setFont(new Font("Tahoma", Font.BOLD, 12));
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 4;
		gbc_textField.gridy = 5;
		add(textField, gbc_textField);
		textField.setColumns(10);
		textField.setEnabled(false);

		btnDraw = new JButton("Draw");
		btnDraw.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 13));
		btnDraw.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				drawPolygon();
			}
		});
		GridBagConstraints gbc_btnDraw = new GridBagConstraints();
		gbc_btnDraw.insets = new Insets(0, 0, 5, 0);
		gbc_btnDraw.gridx = 4;
		gbc_btnDraw.gridy = 6;
		add(btnDraw, gbc_btnDraw);
		btnDraw.setEnabled(false);

		JButton btnDrawEllipse = new JButton("From ellipse to polygon");
		btnDrawEllipse.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 13));
		btnDrawEllipse.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				enableBtnE();

			}
		});
		GridBagConstraints gbc_btnDrawEllipse = new GridBagConstraints();
		gbc_btnDrawEllipse.insets = new Insets(0, 0, 5, 0);
		gbc_btnDrawEllipse.gridx = 4;
		gbc_btnDrawEllipse.gridy = 7;
		add(btnDrawEllipse, gbc_btnDrawEllipse);

		label = new JLabel("No. of segments :");
		label.setFont(new Font("Tahoma", Font.BOLD, 12));
		label.setEnabled(false);
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.anchor = GridBagConstraints.EAST;
		gbc_label.insets = new Insets(0, 0, 5, 5);
		gbc_label.gridx = 3;
		gbc_label.gridy = 8;
		add(label, gbc_label);

		textFieldEllipse = new JTextField();
		textFieldEllipse.setFont(new Font("Tahoma", Font.BOLD, 12));
		textFieldEllipse.setEnabled(false);
		textFieldEllipse.setColumns(10);
		GridBagConstraints gbc_textFieldEllipse = new GridBagConstraints();
		gbc_textFieldEllipse.insets = new Insets(0, 0, 5, 0);
		gbc_textFieldEllipse.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldEllipse.gridx = 4;
		gbc_textFieldEllipse.gridy = 8;
		add(textFieldEllipse, gbc_textFieldEllipse);

		btnDrawE = new JButton("Draw");
		btnDrawE.setEnabled(false);
		btnDrawE.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				drawEllipse();

			}
		});
		btnDrawE.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 13));
		GridBagConstraints gbc_btnDrawE = new GridBagConstraints();
		gbc_btnDrawE.gridx = 4;
		gbc_btnDrawE.gridy = 9;
		add(btnDrawE, gbc_btnDrawE);

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
						// if
						// ((buildingSegment.getFirstNode().getEastNorth().east()
						// >= waySegm.getFirstNode().getEastNorth().east() &&
						// buildingSegment
						// .getSecondNode().getEastNorth().east() <=
						// waySegm.getSecondNode().getEastNorth().east())
						// ||
						// (buildingSegment.getFirstNode().getEastNorth().north()
						// <= waySegm.getFirstNode().getEastNorth().north() &&
						// buildingSegment
						// .getSecondNode().getEastNorth().north() >=
						// waySegm.getSecondNode().getEastNorth().north()))
						ShapeMath.align(waySegm, buildingSegment);
					}
				}
			}
		}

	}

	public void proceedGeneralization(Way way) {

		editedAngles = new ArrayList<>();
		List<Node> currentWayNodes = way.getNodes();
		System.out.println(way.getNodes().size());
		for (int i = 0; i < currentWayNodes.size() - 1; i++) {
			if (i + 1 >= currentWayNodes.size() - 1) {
				double angle = Geometry.getCornerAngle(currentWayNodes.get(i).getEastNorth(), currentWayNodes.get((i + 1) - (currentWayNodes.size() - 1))
						.getEastNorth(), currentWayNodes.get((i + 2) - (currentWayNodes.size() - 1)).getEastNorth());
				System.out.println(Math.toDegrees(angle));
				if (Math.abs(Math.toDegrees(angle)) >= 84 && Math.abs(Math.toDegrees(angle)) <= 96) {
					if (Math.abs(90 - (Math.abs(Math.toDegrees(angle)))) > epsilon) {
						if (Math.toDegrees(angle) < 0) {
							System.out.println("Rotation Angle is :" + (-90 - Math.toDegrees(angle)));
							executeRotation(Math.toRadians(-1.0 * (-90 - Math.toDegrees(angle))), new WaySegment(way, (i + 1) - (currentWayNodes.size() - 1)));
							editedAngles.add(Math.abs(Math.toDegrees(Geometry.getCornerAngle(currentWayNodes.get(i).getEastNorth(),
									currentWayNodes.get((i + 1) - (currentWayNodes.size() - 1)).getEastNorth(),
									currentWayNodes.get((i + 2) - (currentWayNodes.size() - 1)).getEastNorth()))));
						} else {
							System.out.println("Rotation Angle is :" + (90 - Math.toDegrees(angle)));
							executeRotation(Math.toRadians(-1.0 * (90 - Math.toDegrees(angle))), new WaySegment(way, (i + 1) - (currentWayNodes.size() - 1)));
							editedAngles.add(Math.abs(Math.toDegrees(Geometry.getCornerAngle(currentWayNodes.get(i).getEastNorth(),
									currentWayNodes.get((i + 1) - (currentWayNodes.size() - 1)).getEastNorth(),
									currentWayNodes.get((i + 2) - (currentWayNodes.size() - 1)).getEastNorth()))));
						}

					}
				}
			} else {
				double angle = Geometry.getCornerAngle(currentWayNodes.get(i).getEastNorth(), currentWayNodes.get(i + 1).getEastNorth(),
						currentWayNodes.get(i + 2).getEastNorth());
				System.out.println(Math.toDegrees(angle));
				if (Math.abs(Math.toDegrees(angle)) >= 84 && Math.abs(Math.toDegrees(angle)) <= 96) {
					// editedAngles.add(Math.abs(Math.toDegrees(angle)));
					if (Math.abs(90 - (Math.abs(Math.toDegrees(angle)))) > epsilon) {
						if (Math.toDegrees(angle) < 0) {
							System.out.println("Rotation Angle is :" + (-90 - Math.toDegrees(angle)));
							executeRotation(Math.toRadians(-1.0 * (-90 - Math.toDegrees(angle))), new WaySegment(way, (i + 1)));
							editedAngles.add(Math.abs(Math.toDegrees(Geometry.getCornerAngle(currentWayNodes.get(i).getEastNorth(), currentWayNodes.get(i + 1)
									.getEastNorth(), currentWayNodes.get(i + 2).getEastNorth()))));
						} else {
							System.out.println("Rotation Angle is :" + (90 - Math.toDegrees(angle)));
							executeRotation(Math.toRadians(-1.0 * (90 - Math.toDegrees(angle))), new WaySegment(way, (i + 1)));
							editedAngles.add(Math.abs(Math.toDegrees(Geometry.getCornerAngle(currentWayNodes.get(i).getEastNorth(), currentWayNodes.get(i + 1)
									.getEastNorth(), currentWayNodes.get(i + 2).getEastNorth()))));
						}

					}
				}
			}
		}
	}

	public WaySegment findSegment(Way closedWay) {

		DataSet data = Main.map.mapView.getEditLayer().data;
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
				double dist = Point.distance(new Point(buildingCenter.east(), buildingCenter.north()), new Point(road.getNode(i).getEastNorth().east(), road
						.getNode(i).getEastNorth().north()));
				if (dist < min) {
					min = dist;
					minWay = road;
					nodeIndex = i;
					ShapeMath.containingWay = minWay;

				}
			}
		}
		if (roads.isEmpty() == false) {

			if (nodeIndex == 0)
				return new WaySegment(minWay, nodeIndex);
			else if (nodeIndex == minWay.getNodes().size() - 1)
				return new WaySegment(minWay, nodeIndex - 1);
			if (!(nodeIndex == 0 || nodeIndex == minWay.getNodes().size() - 1 || nodeIndex == Integer.MAX_VALUE)) {

				double firstDist = Point.pointLineDistance(new Point(buildingCenter.east(), buildingCenter.north()), new WaySegment(minWay, nodeIndex - 1));
				double secondDist = Point.pointLineDistance(new Point(buildingCenter.east(), buildingCenter.north()), new WaySegment(minWay, nodeIndex));
				if (firstDist < secondDist) {
					WaySegment test = new WaySegment(minWay, nodeIndex - 1);
					if (Point.projectiontOnLine(new Point(test.getFirstNode().getEastNorth().east(), test.getFirstNode().getEastNorth().north()), new Point(
							test.getSecondNode().getEastNorth().east(), test.getSecondNode().getEastNorth().north()), new Point(buildingCenter.east(),
							buildingCenter.north())) != null)
					// return new WaySegment(minWay,nodeIndex-1);
					{
						return test;
					} else
						return new WaySegment(minWay, nodeIndex);
				} else {
					WaySegment test = new WaySegment(minWay, nodeIndex);
					if (Point.projectiontOnLine(new Point(test.getFirstNode().getEastNorth().east(), test.getFirstNode().getEastNorth().north()), new Point(
							test.getSecondNode().getEastNorth().east(), test.getSecondNode().getEastNorth().north()), new Point(buildingCenter.east(),
							buildingCenter.north())) != null)
					// return new WaySegment(minWay,nodeIndex-1);
					{
						return test;
					} else
						return new WaySegment(minWay, nodeIndex - 1);

					// return new WaySegment(minWay,nodeIndex);
				}
			}

		}
		return null;
	}

	public boolean allAnglesInInterval84_96(Way way) {

		List<Node> currentWayNodes = way.getNodes();
		for (int i = 0; i < currentWayNodes.size() - 1; i++) {
			if (i + 1 >= currentWayNodes.size() - 1) {
				double angle = Geometry.getCornerAngle(currentWayNodes.get(i).getEastNorth(), currentWayNodes.get((i + 1) - (currentWayNodes.size() - 1))
						.getEastNorth(), currentWayNodes.get((i + 2) - (currentWayNodes.size() - 1)).getEastNorth());
				if ((Math.abs(Math.toDegrees(angle)) >= 84 && Math.abs(Math.toDegrees(angle)) <= 96)) {
					return true;
				}

			} else {
				double angle = Geometry.getCornerAngle(currentWayNodes.get(i).getEastNorth(), currentWayNodes.get(i + 1).getEastNorth(),
						currentWayNodes.get(i + 2).getEastNorth());

				if ((Math.abs(Math.toDegrees(angle)) >= 84 && Math.abs(Math.toDegrees(angle)) <= 96)) {
					return true;

				}
			}
		}

		return false;
	}

	public boolean allAnglesAreCorrected(List<Double> angles) {

		for (Double ang : angles)
			if (!(ang >= 89 && ang <= 91))
				return false;
		return true;
	}

	public void executeRotation(double angle, WaySegment segment) {
		double centerX = segment.getFirstNode().getEastNorth().east();
		double centerY = segment.getFirstNode().getEastNorth().north();
		double x = segment.getSecondNode().getEastNorth().east();
		double y = segment.getSecondNode().getEastNorth().north();

		double newX = centerX + (x - centerX) * Math.cos(angle) - (y - centerY) * Math.sin(angle);
		double newY = centerY + (x - centerX) * Math.sin(angle) + (y - centerY) * Math.cos(angle);
		EastNorth eastNorth = new EastNorth(newX, newY);
		segment.getSecondNode().setEastNorth(eastNorth);

		Main.map.repaint();
	}

	private void changePolygon() {
		if (Main.map != null) {
			Collection<Way> selWays = getDataSet().getSelectedWays();
			for (Way w : selWays) {
				if (w.isClosed()) {
					EastNorth center = ShapeMath.getCentroid(w);
					double max = Double.MIN_VALUE;
					for (Node n : w.getNodes()) {
						double distance = Point
								.distance(new Point(n.getEastNorth().east(), n.getEastNorth().north()), new Point(center.east(), center.north()));
						if (distance > max)
							max = distance;
					}

					drawCirclePoints(w.getNodes().size() - 1, max, new Node(center));
					for (Node nd : w.getNodes())
						getDataSet().removePrimitive(nd);
					getDataSet().removePrimitive(w);
				}
				// drawCirclePoints();
			}
		}
	}

	private void enableBtnE() {
		if (Main.map != null) {
			label.setEnabled(true);
			btnDrawE.setEnabled(true);
			textFieldEllipse.setEnabled(true);
		}
	}

	private void enableBtn() {
		if (Main.map != null) {
			lblNoOfSegments.setEnabled(true);
			btnDraw.setEnabled(true);
			textField.setEnabled(true);
		}
	}

	private void drawEllipse() {
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
					double a =Math.abs( Math.sqrt(Math.abs(((Ax - h) * (Ax - h) + gamma * (Bx - h) * (Bx - h)) / (gamma + 1))));
					double b =Math.abs(Math.sqrt(Math.abs(((Ay - k) * (Ay - k)) / (1 - ((Ax - h) * (Ax - h)) / (a * a)))));
					//System.out.println(a+" "+b);
					if(textFieldEllipse.getText().equals("")==false){
						//new Node(new EastNorth(h, k))
					drawEllipsePoints(4 * Integer.parseInt(textFieldEllipse.getText()), a, b, center);
					
				} }else {
					double gamma = -1 * (betha / alpha);
					System.out.println(gamma);
					double a =Math.abs(Math.sqrt(Math.abs(((Ax - h) * (Ax - h) * gamma + (Bx - h) * (Bx - h)) / (gamma + 1))));
					double b =Math.abs(Math.sqrt(Math.abs(((Ay - k) * (Ay - k)) / (1 - ((Ax - h) * (Ax - h)) / (a * a)))));
				//	System.out.println(a+" "+b);
					if(textFieldEllipse.getText().equals("")==false){
					drawEllipsePoints(4 * Integer.parseInt(textFieldEllipse.getText()), a, b, center);
					
				}
					}
				//
				for (Node n : selNodes)
					getDataSet().removePrimitive(n);
				Main.map.repaint();
				label.setEnabled(false);
				label.setEnabled(false);
				btnDrawE.setEnabled(false);
				textFieldEllipse.setText("");
				textFieldEllipse.setEnabled(false);
				
			} else {
				JOptionPane.showMessageDialog(null,
						"Please select only three nodes that are represantative for the imaginary ellipse(these nodes must not be connected) !",
						"Allert Message", JOptionPane.WARNING_MESSAGE);
			}

		} else {
			for (Way way : selWays) {
				if (way.getNodes().size() == 5 && way.isClosed() == true) {
					EastNorth center = ShapeMath.getCentroid(way);
					double b = Point.distance(new Point(way.getNodes().get(0).getEastNorth().east(), way.getNodes().get(0).getEastNorth().north()), new Point(
							way.getNodes().get(1).getEastNorth().east(), way.getNodes().get(1).getEastNorth().north())) / 2;
					double a = Point.distance(new Point(way.getNodes().get(1).getEastNorth().east(), way.getNodes().get(1).getEastNorth().north()), new Point(
							way.getNodes().get(2).getEastNorth().east(), way.getNodes().get(2).getEastNorth().north())) / 2;
					if(textFieldEllipse.getText().equals("")==false){
					drawEllipsePoints(4 * Integer.parseInt(textFieldEllipse.getText()), a, b, new Node(center));
					
				}
			}
			}
			Main.map.repaint();
			label.setEnabled(false);
			label.setEnabled(false);
			btnDrawE.setEnabled(false);
			textFieldEllipse.setText("");
			textFieldEllipse.setEnabled(false);
		}
	}

	private void drawPolygon() {
		Collection<Node> selNodes = getDataSet().getSelectedNodes();
		Collection<Way> selWays = getDataSet().getSelectedWays();
		if (selNodes.size() != 3 || selWays.isEmpty() == false)
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
			if(textField.getText().equals("")==false){
			drawCirclePoints(Integer.parseInt(textField.getText()), radius, new Node(new EastNorth(centerX, centerY)));
			for (Node n : selNodes)
				getDataSet().removePrimitive(n);
			Main.map.repaint();
			lblNoOfSegments.setEnabled(false);
			btnDraw.setEnabled(false);
			textField.setText("");
			textField.setEnabled(false);
			}
		}
	}

	private void drawEllipsePoints(int points, double a_radius, double b_radius, Node center) {
		Way ellipse = new Way();
		double slice = 2 * Math.PI / points;
		for (int i = 0; i < points; i++) {
			double angle = slice * i;
			double newX = (center.getEastNorth().east() + a_radius * Math.cos(angle));
			double newY = (center.getEastNorth().north() + b_radius * Math.sin(angle));
			Node p = new Node(new EastNorth(newX, newY));
			getDataSet().addPrimitive(p);
			ellipse.addNode(p);
		}
		ellipse.addNode(ellipse.getNodes().get(0));
		getDataSet().addPrimitive(ellipse);
		Main.map.repaint();
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
		Main.map.repaint();
	}

	private void makeWallStraight() {
		if (Main.map != null) {
			Collection<Node> selectedNodes = getDataSet().getSelectedNodes();
			Collection<Way> ways = getDataSet().getWays();
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

			Main.map.repaint();
		}
	}

	private void cutCorners() {
		if (Main.map != null) {
			Collection<Way> ways = new ArrayList<>();
			ways = getDataSet().getSelectedWays();
			for (Way w : ways)
				if (w.isClosed())
					editCorners(w);

		}
	}

	private void editCorners(Way building) {
		List<Node> currWayNodes = new ArrayList<>();
		List<Node> currentWayNodes = building.getNodes();
		List<Node> newNodes = new ArrayList<>();
		for (int i = 0; i < currentWayNodes.size() - 1; i++) {
			if (i + 1 >= currentWayNodes.size() - 1) {
				Double r = Point.distance(
						new Point(currentWayNodes.get(i).getEastNorth().east(), currentWayNodes.get(i).getEastNorth().north()),
						new Point(currentWayNodes.get((i + 1) - (currentWayNodes.size() - 1)).getEastNorth().east(), currentWayNodes
								.get((i + 1) - (currentWayNodes.size() - 1)).getEastNorth().north()));

				Node newNode1 = getIntersectionNode(currentWayNodes.get((i + 1) - (currentWayNodes.size() - 1)), currentWayNodes.get(i),
						currentWayNodes.get((i + 1) - (currentWayNodes.size() - 1)), r / 5);
				System.out.println(r/5+" "+ Main.map.mapView.getScale());
				getDataSet().addPrimitive(newNode1);
				currWayNodes.add(newNode1);

				r = Point.distance(
						new Point(currentWayNodes.get((i + 1) - (currentWayNodes.size() - 1)).getEastNorth().east(), currentWayNodes
								.get((i + 1) - (currentWayNodes.size() - 1)).getEastNorth().north()),
						new Point(currentWayNodes.get((i + 2) - (currentWayNodes.size() - 1)).getEastNorth().east(), currentWayNodes
								.get((i + 2) - (currentWayNodes.size() - 1)).getEastNorth().north()));
				Node newNode2 = getIntersectionNode(currentWayNodes.get((i + 1) - (currentWayNodes.size() - 1)),
						currentWayNodes.get((i + 1) - (currentWayNodes.size() - 1)), currentWayNodes.get((i + 2) - (currentWayNodes.size() - 1)), r / 5);
				getDataSet().addPrimitive(newNode2);
				currWayNodes.add(newNode2);

			} else {
				Double r = Point.distance(new Point(currentWayNodes.get(i).getEastNorth().east(), currentWayNodes.get(i).getEastNorth().north()), new Point(
						currentWayNodes.get(i + 1).getEastNorth().east(), currentWayNodes.get(i + 1).getEastNorth().north()));

				Node newNode1 = getIntersectionNode(currentWayNodes.get(i + 1), currentWayNodes.get(i), currentWayNodes.get(i + 1), r / 5);
				getDataSet().addPrimitive(newNode1);
				currWayNodes.add(newNode1);
				r = Point.distance(new Point(currentWayNodes.get(i + 1).getEastNorth().east(), currentWayNodes.get(i + 1).getEastNorth().north()), new Point(
						currentWayNodes.get(i + 2).getEastNorth().east(), currentWayNodes.get(i + 2).getEastNorth().north()));
				Node newNode2 = getIntersectionNode(currentWayNodes.get(i + 1), currentWayNodes.get(i + 1), currentWayNodes.get(i + 2), r / 5);
				getDataSet().addPrimitive(newNode2);
				currWayNodes.add(newNode2);

			}

		}
		currWayNodes.add(currWayNodes.get(0));

		for (Node n : currentWayNodes) {
			getDataSet().removePrimitive(n);
		}

		building.setNodes(currWayNodes);
		Main.map.repaint();

	}

	private Node getIntersectionNode(Node center, Node firstPoint, Node secondPoint, double radius) {
		double LAB = Point.distance(new Point(firstPoint.getEastNorth().east(), firstPoint.getEastNorth().north()), new Point(
				secondPoint.getEastNorth().east(), secondPoint.getEastNorth().north()));
		// compute the direction vector D from A to B
		double Dx = (secondPoint.getEastNorth().east() - firstPoint.getEastNorth().east()) / LAB;// (Bx-Ax)/LAB
		double Dy = (secondPoint.getEastNorth().north() - firstPoint.getEastNorth().north()) / LAB;// (By-Ay)/LAB
		// Now the line equation is x = Dx*t + Ax, y = Dy*t + Ay with 0 <= t <=
		// 1.

		// compute the value t of the closest point to the circle center (Cx,
		// Cy)
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

	public DataSet getDataSet() {
		// return Main.main.getCurrentDataSet();
		return Main.main.getEditLayer().data;
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
