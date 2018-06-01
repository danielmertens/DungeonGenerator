package be.danielmertens.dungeonGenerator.algo.s2_positioning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import be.danielmertens.dungeonGenerator.algo.Algorithm;
import be.danielmertens.dungeonGenerator.constants.Measurements;
import be.danielmertens.dungeonGenerator.controller.Controller;
import be.danielmertens.dungeonGenerator.model.Model;
import be.danielmertens.dungeonGenerator.model.Node;
import be.danielmertens.dungeonGenerator.model.Position;
import be.danielmertens.dungeonGenerator.model.Room;

public class Dijkstra_FirstAvailable extends Algorithm {

	private int height;
	private int width;

	public Dijkstra_FirstAvailable(Model m, Controller c, int width, int height) {
		super(m, c);
		this.height = height;
		this.width = width;
	}

	@Override
	protected void run(boolean fast) {
		System.out.println("start positioning");
		
		HashMap<Room, Double> distanceMap = new HashMap<>();
		for (Room room : m.getRooms()) {
			distanceMap.put(room, room.distaceFrom(width / Measurements.GRIDSIZE / 2, height / Measurements.GRIDSIZE / 2));
		}
		
		while(m.getRooms().stream().anyMatch(r -> !r.locked)) {
			List<Room> freeRoooms = m.getRooms()
					.stream()
					.filter(r -> !r.locked)
					.sorted((o1, o2) -> {
						return Double.compare(distanceMap.get(o1), distanceMap.get(o2));
					})
					.collect(Collectors.toList());
			int max = freeRoooms.size() > 10 ? freeRoooms.size() / 4 : freeRoooms.size();
			Room current = freeRoooms.get(random().nextInt(max));
			setMessage("Rooms in queue: " + freeRoooms.size());
			LinkedList<Position> path = findLockLocation(current, m.getRooms());
			
			if(path == null) {
				current.locked = true;
				if(!fast) {
					repaint(200);
				}
				continue;
			}
			
			current.moving = true;
			for (Position position : path) {
				current.setPosition(position.getX(), position.getY());
				if(!fast) {
					repaint(50);
				}
			}
			current.locked = true;
			current.moving = false;
			repaint(100);
		}
		setMessage("Press ENTER to select main rooms.");
		repaint(100);
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
}
