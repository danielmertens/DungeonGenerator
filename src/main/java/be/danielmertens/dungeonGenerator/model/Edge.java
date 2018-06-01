package be.danielmertens.dungeonGenerator.model;

import java.awt.Point;
import java.util.ArrayList;

public class Edge implements Comparable<Edge> {
	
	private final int v;
	private final int w;
	private final double weight;
	
	public Edge(int r1, int r2, double weight) {
		this.v = r1;
		this.w = r2;
		this.weight = weight;
	}
	
	public double weight() {
		return this.weight;
	}
	
	public int either() {
		return v;
	}
	
	public int other(int x) {
		if(x == v) return w;
		else if(x == w) return v;
		else throw new IllegalArgumentException();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Edge)) return false;
		Edge o = (Edge) obj;
		return (o.v == v && o.w == w)
				|| (o.w == v && o.v == w);
	}

	@Override
	public int compareTo(Edge that) {
		if(this.weight() < that.weight()) return -1;
		else if(this.weight() > that.weight()) return 1;
		else return 0;
	}
	
	@Override
	public int hashCode() {
		int x = Math.min(v, w);
		int y = Math.max(v, w);
		return ((x + y) * (x + y + 1) + y) / 2;
	}
}
