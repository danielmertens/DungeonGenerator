package be.danielmertens.dungeonGenerator.model;

import java.awt.geom.Point2D;

public class DoublePoint extends Point2D {

	public double x;
	public double y;

	public DoublePoint(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public void setLocation(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String toString() {
		return x + " - " + y;
	}

}
