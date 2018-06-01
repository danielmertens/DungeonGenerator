package be.danielmertens.dungeonGenerator.model;

import java.awt.Point;
import java.awt.geom.Point2D;

public class EdgeNode {

	public double x;
	public double y;
	public Room room;
	
	public EdgeNode(double x, double y, Room room) {
		super();
		this.x = x;
		this.y = y;
		this.room = room;
	}

	public DoublePoint getPoint() {
		return new DoublePoint(x, y);
	}
	
	
}
