package be.danielmertens.dungeonGenerator.algo.s1_creation;

import be.danielmertens.dungeonGenerator.algo.Algorithm;
import be.danielmertens.dungeonGenerator.constants.Measurements;
import be.danielmertens.dungeonGenerator.controller.Controller;
import be.danielmertens.dungeonGenerator.model.Model;
import be.danielmertens.dungeonGenerator.model.Position;
import be.danielmertens.dungeonGenerator.model.Room;

public class CircleCreation extends Algorithm {

	private int height;
	private int width;

	public CircleCreation(Model m, Controller c, int width, int height) {
		super(m, c);
		this.height = height;
		this.width = width;
	}

	@Override
	public void run(boolean fast) {
		System.out.println("start algo.");
		int count = 0;
		while(count < 150) {
			Room r = generateRoom(width / Measurements.GRIDSIZE, height / Measurements.GRIDSIZE);
			r.setNumber(count);
			m.addRoom(r);
			setMessage("Creating rooms: " + (count + 1));
			if(!fast) {
				repaint(100);
			}
			count++;
		}
		m.addStat("Rooms: " + count);
		setMessage("Press ENTER to start positioning.");
		repaint(100);
	}
	
	public Room generateRoom(int width, int height) {
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
			width = (int) (random().nextGaussian() * (Measurements.ROOM_WIDTH_MAX * 2 / 5)) + diffWidth / 4;
			height = (int) (random().nextGaussian() * (Measurements.ROOM_HEIGHT_MAX * 2 / 5)) + diffHeight / 4;
			
			if(width < Measurements.ROOM_WIDTH_MIN)
				width = Measurements.ROOM_WIDTH_MIN;
			if(height < Measurements.ROOM_HEIGHT_MIN)
				height = Measurements.ROOM_HEIGHT_MIN;
			differance = Math.abs(width - height);
		}
		
		return new Position(width, height);
	}

	private Position getRandomPointInCircle(int radius) {
		double t = 2 * Math.PI * random().nextDouble();
		double u = random().nextDouble() + random().nextDouble();
		double r;
		if(u > 1)
			r = 2 - u;
		else
			r = u;
		return new Position((int)(radius * r * Math.cos(t)), (int)(radius * r * Math.sin(t)));
	}

}
