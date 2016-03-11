package org.openstreetmap.josm.plugins.cadtools;

import org.openstreetmap.josm.data.osm.WaySegment;

public class Point {
	
	double x;
	double y;
	
	public Point(){
		this.x = 0;
		this.y = 0;
	}
	public Point(double x, double y){
		this.x = x;
		this.y = y;
	}
	
//	public static Point project(Point x1, Point x2, Point toProject)
//	{
//	    double m = (x2.y - x1.y) / (x2.x - x1.x);
//	    double b = x1.y - (m * x1.y);
//	 
//	    double x = (m * toProject.y + toProject.x - m * b) / (m * m + 1);
//	    double y = (m * m * toProject.y + m * toProject.x + b) / (m * m + 1);
//	 
//	    return new Point(x,y);
//	}
	
	public static double distance(Point x1, Point x2){
		
		return Math.sqrt(Math.pow(x2.x - x1.x, 2) + Math.pow(x2.y - x1.y, 2)); // always positive
		
	}
	public static double pointLineDistance(Point p,WaySegment w)
	{   double a=w.getSecondNode().getEastNorth().north()-w.getFirstNode().getEastNorth().north();
	    double b=-1*(w.getSecondNode().getEastNorth().east()-w.getFirstNode().getEastNorth().east());
	    double c=w.getFirstNode().getEastNorth().east()*a-w.getFirstNode().getEastNorth().north()*b;
	    double first=Math.abs(a*p.x+b*p.y+c);
	    double second=Math.sqrt(a*a+b*b);
		return first/second;
	}
	public static Point projectiontOnLine(Point p1,Point p2,Point pt){
		boolean isValid = false;

	    Point r=new Point();
	    if (p1.x == p2.x && p1.y == p2.y) p1.x -= 0.00001;

	    double U = ((pt.x - p1.x) * (p2.x - p1.x)) + ((pt.y - p1.y) * (p2.y - p1.y));

	    double Udenom = Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2);

	    U /= Udenom;

	    r.x = p1.x + (U * (p2.x - p1.x));
	    r.y = p1.y + (U * (p2.y - p1.y));

	    double minx, maxx, miny, maxy;

	    minx = Math.min(p1.x, p2.x);
	    maxx = Math.max(p1.x, p2.x);

	    miny = Math.min(p1.y, p2.y);
	    maxy = Math.max(p1.y, p2.y);

	    isValid = (r.x >= minx && r.x <= maxx) && (r.y >= miny && r.y <= maxy);

	    return isValid ? r : null;
	}
	public static double distance(Point lineX1, Point lineX2, Point p){
		double A = p.x - lineX1.x;
		double B = p.y - lineX1.y;
		double C = lineX2.x - lineX1.x;
		double D = lineX2.y - lineX1.y;
		
		double dot = A * C + B * D;
		double len_sq = C*C + D*D;
		double param = dot/len_sq;
		double xx = lineX1.x + param * C;
		double yy = lineX1.y + param * D;
		double dx = p.x - xx;
		double dy = p.y - yy;
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + "]";
	}
	
}
