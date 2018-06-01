package be.danielmertens.dungeonGenerator;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import be.danielmertens.dungeonGenerator.constants.Measurements;
import be.danielmertens.dungeonGenerator.model.DoublePoint;
import be.danielmertens.dungeonGenerator.model.Edge;
import be.danielmertens.dungeonGenerator.model.EdgeNode;
import be.danielmertens.dungeonGenerator.model.Node;
import be.danielmertens.dungeonGenerator.model.Position;
import be.danielmertens.dungeonGenerator.model.Room;

public class Algorithms {
	
	private Random random = new SecureRandom();
//	private CanvasWindow window;
	
//	public Algorithms(CanvasWindow window) {
//		this.window = window;
//	}

	public Room generateRoom(int width, int height) {
		//int rWidth = random.nextInt(Measurements.ROOM_WIDTH_MAX - Measurements.ROOM_WIDTH_MIN) + Measurements.ROOM_WIDTH_MIN;
		//int rHeight = random.nextInt(Measurements.ROOM_HEIGHT_MAX - Measurements.ROOM_HEIGHT_MIN) + Measurements.ROOM_HEIGHT_MIN;
//		int rWidth = (int) Math.random() * (Measurements.ROOM_WIDTH_MAX - Measurements.ROOM_WIDTH_MIN) + Measurements.ROOM_WIDTH_MIN;
//		int rHeight = (int) Math.random() * (Measurements.ROOM_HEIGHT_MAX - Measurements.ROOM_HEIGHT_MIN) + Measurements.ROOM_HEIGHT_MIN;
		Position pos = getRandomPointInCircle(height / 3);
		Position dim = getRoomWidthHeight();
		
		return new Room(pos.getX() + width / 2 - dim.getX() / 2, pos.getY() + height / 2 - dim.getY() / 2, dim.getX(), dim.getY());
	}
	
	private Position getRoomWidthHeight() {
		int width = 0;
		int height = 0;
		int diffWidth = Measurements.ROOM_WIDTH_MAX - Measurements.ROOM_WIDTH_MIN;
		int diffHeight = Measurements.ROOM_HEIGHT_MAX - Measurements.ROOM_HEIGHT_MIN;
		int heightWidthMaxDifferance = Math.min(Measurements.ROOM_WIDTH_MAX / 2, Measurements.ROOM_HEIGHT_MAX / 2);
		
		int differance = Measurements.ROOM_WIDTH_MAX + Measurements.ROOM_HEIGHT_MAX;
		
		while(differance > heightWidthMaxDifferance) {
			width = (int) (random.nextGaussian() * (Measurements.ROOM_WIDTH_MAX * 2 / 5)) + diffWidth / 4;
			height = (int) (random.nextGaussian() * (Measurements.ROOM_HEIGHT_MAX * 2 / 5)) + diffHeight / 4;
			
			if(width < Measurements.ROOM_WIDTH_MIN)
				width = Measurements.ROOM_WIDTH_MIN;
			if(height < Measurements.ROOM_HEIGHT_MIN)
				height = Measurements.ROOM_HEIGHT_MIN;
			differance = Math.abs(width - height);
		}
		
		return new Position(width, height);
	}

	private Position getRandomPointInCircle(int radius) {
		double t = 2 * Math.PI * Math.random();
		double u = Math.random() + Math.random();
		double r;
		if(u > 1)
			r = 2 - u;
		else
			r = u;
		return new Position((int)(radius * r * Math.cos(t)), (int)(radius * r * Math.sin(t)));
	}
	
	public LinkedList<Position> findLockLocation(Room room, ArrayList<Room> list) {
		List<Room> locked = list.stream().filter((r) -> r.locked).collect(Collectors.toList());
		boolean overlap = locked.stream().anyMatch((r) -> room.overlaps(r));
		if(!overlap) {
			//room.locked = true;
			return null;
		}
		
		Queue<Node> queue = new PriorityQueue<>();
		HashSet<Node> visited = new HashSet<Node>();
		Node start = new Node(room.getX(), room.getY(), null);
		queue.add(start);
		
		while(true) {
			Node n = queue.poll();
			room.setPosition(n.x, n.y);
			if(n == null) break;
			overlap = locked.stream().anyMatch((r) -> room.overlaps(r));
			if(!overlap) {
				//room.locked = true;
				return createPath(n);
			}
			if(!visited.contains(n)) {
				visited.add(n);
				Node[] neighbours = getNeighbours(n);
				for (int i = 0; i < neighbours.length; i++) {
					if(!visited.contains(neighbours[i]) && !queue.contains(neighbours[i]))
						queue.add(neighbours[i]);
				}
			}
		}
		return null;
	}

	private LinkedList<Position> createPath(Node n) {
		LinkedList<Position> path = new LinkedList();
		Node current = n;
		while(current != null) {
			path.addFirst(new Position(current.x, current.y));
			current = current.prev;
		}
		return path;
	}

	private Node[] getNeighbours(Node n) {
		Node[] arr = new Node[8];
		int count = 0;
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				if(i == 0 && j == 0) continue;
				arr[count] = new Node(n.x + i, n.y + j, n);
				count++;
			}
		}
		return arr;
	}
	
	private <T> void shuffleArray(T[] ar)
	  {
	    Random rnd = ThreadLocalRandom.current();
	    for (int i = ar.length - 1; i > 0; i--)
	    {
	      int index = rnd.nextInt(i + 1);
	      T a = ar[index];
	      ar[index] = ar[i];
	      ar[i] = a;
	    }
	  }

	private ArrayList<Room> getSuroundingRooms(Room room, ArrayList<Room> list, int radius) {
		ArrayList<Room> res = new ArrayList<>();
		for (Room r : list) {
			if(r.distaceFrom(room) <= radius + 1) {
				res.add(r);
			}
		}
		return res;
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
						
//						edges[i].addEdge(edges[j]);
//						edges[i].addEdge(edges[k]);
//						
//						edges[j].addEdge(edges[i]);
//						edges[j].addEdge(edges[k]);
//						
//						edges[k].addEdge(edges[j]);
//						edges[k].addEdge(edges[i]);
					}
				}
			}
		}
		
		return edges.toArray(new Edge[edges.size()]);
	}
	
	static final double TOL = 0.0000001;

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
	    	throw new IllegalArgumentException("Yeah, lazy."); 
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
