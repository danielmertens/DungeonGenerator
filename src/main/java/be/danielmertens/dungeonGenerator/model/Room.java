package be.danielmertens.dungeonGenerator.model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import be.danielmertens.dungeonGenerator.constants.Measurements;

public class Room {
	
	private static final Color selectColor = new Color(30,144,255); 
	
	private int x;
	private int y;
	
	private int width;
	private int height;
	
	public boolean locked;
	public boolean moving;
	private boolean selected;

	private int number;
	
	
	public Room(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}
	
	public int getNumber() {
		return number;
	}
	
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setPosition(int x, int y) {
		setX(x);
		setY(y);
	}
	
	public void move(int xOffest, int yOffset) {
		this.x += xOffest;
		this.y += yOffset;
	}
	
	public Position getCenter() {
		return new Position(x + width / 2, y + height / 2);
	}
	
	public int getArea() {
		return width * height;
	}

	public boolean overlaps (Room r) {
	    return x < r.x + r.width && x + width > r.x && y < r.y + r.height && y + height > r.y;
	}
	
	public boolean inside(double px, double py) {
		return px > x && px < x + width
				&& py > y && py < y + height;
	}
	
	public void render(Graphics g) {
		//System.out.println("room " + x + " " + y + " " + width + " " + height);
		if(moving) {
			g.setColor(Color.BLUE);
		}
		else {
			g.setColor(Color.GRAY);
		}
		g.fillRect(x * Measurements.GRIDSIZE - 2, y * Measurements.GRIDSIZE - 2, width * Measurements.GRIDSIZE + 4, height * Measurements.GRIDSIZE + 4);
		
		g.setColor(Color.WHITE);
		g.fillRect(x * Measurements.GRIDSIZE + 2, y * Measurements.GRIDSIZE + 2, width * Measurements.GRIDSIZE - 4, height * Measurements.GRIDSIZE - 4);
		
		if(selected) {
			g.setColor(Color.RED);
		}
		else if(locked) {
			g.setColor(Color.GREEN);
		}
		else {
			g.setColor(selectColor);
		}
		
		int yline = y + 1;
		
		while(yline < y + height) {
			g.fillRect(x * Measurements.GRIDSIZE, yline * Measurements.GRIDSIZE - 1, width * Measurements.GRIDSIZE, 2);
			yline += 1;
		}
		
		int xline = x + 1;
		
		while(xline < x + width) {
			g.fillRect(xline * Measurements.GRIDSIZE - 1, y * Measurements.GRIDSIZE, 2, height * Measurements.GRIDSIZE);
			xline += 1;
		}
		//System.out.println("end");
	}

	public double distaceFrom(Room room) {
		return distaceFrom(room.getCenter());
	}
	
	public double distaceFrom(Position point) {
		return distaceFrom(point.getX(), point.getY());
	}
	
	public double distaceFrom(int x, int y) {
		Position center = getCenter();
		double xs = Math.pow(center.getX() - x, 2);
		double ys = Math.pow(center.getY() - y, 2);
		return Math.sqrt(xs + ys);
	}
	
	
}
