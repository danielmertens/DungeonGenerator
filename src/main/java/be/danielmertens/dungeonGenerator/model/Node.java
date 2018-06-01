package be.danielmertens.dungeonGenerator.model;

import java.util.concurrent.ThreadLocalRandom;

public class Node implements Comparable<Node>{

	public int x;
	public int y;
	public Node prev;
	private int random;
	
	public Node(int x, int y, Node prev) {
		this.x = x;
		this.y = y;
		this.prev = prev;
		//this.random = ThreadLocalRandom.current().nextInt(10);
		this.random = 2;
	}
	
	public int countToRoot() {
		if(prev == null) return 0;
		return prev.countToRoot() + random;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Node)) return false;
		Node o = (Node) obj;
		return o.x == x && o.y == y;
	}

	@Override
	public int compareTo(Node n) {
		return Integer.compare(countToRoot(), n.countToRoot());
	}
	
	
}
