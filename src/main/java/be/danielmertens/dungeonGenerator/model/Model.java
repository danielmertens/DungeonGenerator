package be.danielmertens.dungeonGenerator.model;

import java.util.ArrayList;
import java.util.Random;

public class Model {
	
	private ArrayList<Room> rooms = new ArrayList<>();
	private ArrayList<String> stats = new ArrayList<>();
	public int[] areaArray = new int[100];
	public Edge[] graph;
	private Random random;
	
	public Model() {
		this.random = new Random();
		long seed = random.nextLong();
		random.setSeed(seed);
		addStat("Seed : " + seed);
	}
	
	public Model(long seed) {
		this.random = new Random(seed);
		addStat("Seed : " + seed);
	}
	
	public void addRoom(Room room) {
		this.rooms.add(room);
		int area = room.getArea();
		if(area > 99)
			area = 99;
		areaArray[area]++;
	}
	
	public ArrayList<Room> getRooms() {
		return this.rooms;
	}

	public void addStat(String string) {
		stats.add(string);
	}

	public ArrayList<String> getStats() {
		return stats;
	}
	
	public double getAreaMean() {
		int sum = 0;
		int count = 0;
		for (int i = 0; i < areaArray.length; i++) {
			if(areaArray[i] != 0) {
				sum += i * areaArray[i];
				count += areaArray[i];
			}
		}
		return sum / (double) count;
	}
	
	public int countSelected() {
		return (int) rooms.stream().filter(r -> r.isSelected()).count();
	}

	public Random getRandom() {
		return random;
	}
	
}
