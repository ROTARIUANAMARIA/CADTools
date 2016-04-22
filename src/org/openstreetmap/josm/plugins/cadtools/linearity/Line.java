package org.openstreetmap.josm.plugins.cadtools.linearity;

import org.openstreetmap.josm.plugins.cadtools.Point;

/*
 * Class Line represents the line as we know from school. It is described by equation:
 * y = a*x + b.
 * If the line is parallel to Y axis then isToYAxisParallel is set on 1 and x0 has the value.
 */

public class Line {
	
	protected double a;
	protected double b;
	protected double x0;
	protected int isToYAxisParallel;
	
	public Line() {
		a = 0;
		b = 0;
		x0 = 0;
		isToYAxisParallel = 0;
	}
	
	public Line(double a, double b, double x0, int isToYAxisParallel) {
		this.a = a;
		this.b = b;
		this.x0 = x0;
		this.isToYAxisParallel = isToYAxisParallel;
	}
	
	public Line(double a, double b) {
		this(a, b, 0, 0);
	}
	/*
	 * This constructor create line which meets two points.
	 */
	public Line(Point p1, Point p2) {
		if (p1.x == p2.x) {
			a = 0;
			b = 0;
			x0 = p1.x;
			isToYAxisParallel = 1;
		}
		else {
			a = (p1.y - p2.y) / (p1.x - p2.x);
			b = (p1.x * p2.y - p2.x * p1.y) / (p1.x - p2.x);
			x0 = 0;
			isToYAxisParallel = 0;
		}
	}
	
	/*
	 * Method perpendicularLine returns the line which is perpendicular to this line and meets given point.
	 */
	
	public Line perpendicularLine(Point p) {
		Line perpendicularLine;
		if (isToYAxisParallel == 1) {
			perpendicularLine = new Line(0, p.y);
		}
		else {
			if (a==0) {
				perpendicularLine = new Line(0, 0, p.x, 1);
			}
			else {
				perpendicularLine = new Line(-1/a, p.y + p.x/a, 0, 0);				
			}
		}
		return perpendicularLine;
	}
	
	/*
	 * Method valueForX returns Y value according to equation y = a*x + b
	 */
	public double valueForX(double x) {
		if (isToYAxisParallel == 1) {
			return x0;
		}
		else {
			return a*x + b;
		}
	}

	/*
	 * Method crossPoint returns the point which this line and given line l2 meet.
	 */
	public Point crossPoint(Line l2) {
		Point p = new Point(0, 0);
		if (isToYAxisParallel == 1) {
			if (l2.isToYAxisParallel == 1) {
				throw new IllegalStateException("Lines are parallel!");
			}
			else {
				p.x = x0;
				p.y = l2.valueForX(p.x);
			}
		}
		else {
			if (l2.isToYAxisParallel == 1) {
				p.x = l2.x0;
				p.y = valueForX(p.x);
			}
			else {
				p.x = (l2.b - b) / (a - l2.a);
				p.y = a * p.x + b;
			}
		}
		return p;
	}
	
	/*
	 * Method symmetricalPoint returns the point which is symmetrical point when this line is symmetry axis. 
	 */
	public Point symmetricalPoint(Point p) {
		Point symmetricalPoint = new Point();
		Line perpendicularLine = perpendicularLine(p);
		Point crossPoint = crossPoint(perpendicularLine);
		if (p.x >= crossPoint.x) {
			if (p.y >= crossPoint.y) {
				symmetricalPoint = new Point(p.x - (p.x - crossPoint.x)*2, p.y - (p.y - crossPoint.y)*2);
			}
			else {
				symmetricalPoint = new Point(p.x - (p.x - crossPoint.x)*2, p.y + (p.y - crossPoint.y)*2);				
			}
		}
		else {
			if (p.x < crossPoint.x) {
				if (p.y >= crossPoint.y) {
					symmetricalPoint =  new Point(p.x + (crossPoint.x - p.x)*2, p.y - (p.y - crossPoint.y)*2);
				}
				else {
					symmetricalPoint = new Point(p.x + (crossPoint.x - p.x)*2, p.y + (p.y - crossPoint.y)*2);					
				}
			}
		}		
		return symmetricalPoint;
	}
}
