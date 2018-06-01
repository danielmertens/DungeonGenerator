package be.danielmertens.dungeonGenerator.algo.s4_graphCreation;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import be.danielmertens.dungeonGenerator.algo.Algorithm;
import be.danielmertens.dungeonGenerator.controller.Controller;
import be.danielmertens.dungeonGenerator.model.DoublePoint;
import be.danielmertens.dungeonGenerator.model.Edge;
import be.danielmertens.dungeonGenerator.model.EdgeNode;
import be.danielmertens.dungeonGenerator.model.Model;
import be.danielmertens.dungeonGenerator.model.Room;

public class DelaunayGraph extends Algorithm {

	static final double TOL = 0.0000001;

	public DelaunayGraph(Model m, Controller c) {
		super(m, c);
	}

	@Override
	protected void run(boolean fast) {
		m.graph = createDelaunayGraph(m.getRooms());
		setMessage("Press ENTER to create the MST");
		repaint(200);
	}
	
	public Edge[] createDelaunayGraph(ArrayList<Room> rooms) {
		List<Room> selectedRooms = rooms.stream().filter(r -> r.isSelected()).collect(Collectors.toList());
		HashSet<Edge> edges = new HashSet<>();
		EdgeNode[] edgeNodes = new EdgeNode[selectedRooms.size()];
		
		for (int i = 0; i < edgeNodes.length; i++) {
			Room r = selectedRooms.get(i);
			double x = r.getX() + r.getWidth() / 2.0;
			double y = r.getY()	+ r.getHeight() / 2.0;
			edgeNodes[i] = new EdgeNode(x, y, r);
		}
		
		for (int i = 0; i < edgeNodes.length; i++) {
			for (int j = i + 1; j < edgeNodes.length; j++) {
				for (int k = j + 1; k < edgeNodes.length; k++) {
					boolean isTriangle = true;
					Circle c = circleFromPoints(edgeNodes[i].getPoint(), 
							edgeNodes[j].getPoint(), 
							edgeNodes[k].getPoint());
					if(c != null)
						for (int l = 0; l < edgeNodes.length && isTriangle; l++) {
							if(l == i || l == j || l == k) continue;
							if(c.inside(edgeNodes[l].getPoint())) {
								isTriangle = false;
							}
						}
					if(isTriangle) {
						edges.add(new Edge(edgeNodes[i].room.getNumber(), edgeNodes[j].room.getNumber(), edgeNodes[i].room.distaceFrom(edgeNodes[j].room)));
						edges.add(new Edge(edgeNodes[i].room.getNumber(), edgeNodes[k].room.getNumber(), edgeNodes[i].room.distaceFrom(edgeNodes[k].room)));
						edges.add(new Edge(edgeNodes[j].room.getNumber(), edgeNodes[k].room.getNumber(), edgeNodes[j].room.distaceFrom(edgeNodes[k].room)));
					}
				}
			}
		}
		
		return edges.toArray(new Edge[edges.size()]);
	}
	
	public Circle circleFromPoints(final DoublePoint p1, final DoublePoint p2, final DoublePoint p3)
	{
	    final double offset = Math.pow(p2.x,2) + Math.pow(p2.y,2);
	    final double bc =   ( Math.pow(p1.x,2) + Math.pow(p1.y,2) - offset )/2.0;
	    final double cd =   (offset - Math.pow(p3.x, 2) - Math.pow(p3.y, 2))/2.0;
	    final double det =  (p1.x - p2.x) * (p2.y - p3.y) - (p2.x - p3.x)* (p1.y - p2.y); 
	
	    if (Math.abs(det) < TOL) { 
	    	System.out.println(p1);
	    	System.out.println(p2);
	    	System.out.println(p3);
	    	System.out.println(det);
	    	System.out.println("Not all graph paths got created.");
	    	return null;
	    }
	
	    final double idet = 1/det;
	
	    final double centerx =  (bc * (p2.y - p3.y) - cd * (p1.y - p2.y)) * idet;
	    final double centery =  (cd * (p1.x - p2.x) - bc * (p2.x - p3.x)) * idet;
	    final double radius = 
	       Math.sqrt( Math.pow(p2.x - centerx,2) + Math.pow(p2.y-centery,2));
	    
	    Point center = new Point();
	    center.setLocation(centerx, centery);
	    return new Circle(center,radius);
	}

	class Circle
	{
	    final Point center;
	    final double radius;
	    
	    public Circle(Point center, double radius)
	    {
	    	this.center = center; this.radius = radius;
	    }
	    
	    public boolean inside(DoublePoint point) {
	    	return inside(point.getX(), point.getY());
	    }
	    
	    public boolean inside(double x, double y) {
	    	double xs = Math.pow(center.getX() - x, 2);
			double ys = Math.pow(center.getY() - y, 2);
			double dist = Math.sqrt(xs + ys);
			return dist < radius;
	    }
	    
	    @Override 
	    public String toString()
	    {
	    	return new StringBuilder().append("Center= ").append(center).append(", r=").append(radius).toString();
	    }
	}

}
