package be.danielmertens.dungeonGenerator.algo.s3_selection;

import be.danielmertens.dungeonGenerator.algo.Algorithm;
import be.danielmertens.dungeonGenerator.controller.Controller;
import be.danielmertens.dungeonGenerator.model.Model;
import be.danielmertens.dungeonGenerator.model.Room;

public class MeanSelection extends Algorithm {

	public MeanSelection(Model m, Controller c) {
		super(m, c);
	}

	@Override
	protected void run(boolean fast) {
		double minBound = 2 * m.getAreaMean();
		int count = 0;
		setMessage("Selected rooms: " + count);
		for (Room r : m.getRooms()) {
			r.locked = false;
		}
		
		int widthCount = 0;
		int heightCount = 0;
		for (Room room : m.getRooms()) {
			widthCount += room.getWidth();
			heightCount += room.getHeight();
		}
		
		double widthMean = widthCount / (double) m.getRooms().size();
		double heightMean = heightCount / (double) m.getRooms().size();
		widthMean *= 1.50;
		heightMean *= 1.50;
		
		// Just a check for the difference between the current and previous selection process.
		for (Room room : m.getRooms()) {
			if(room.getArea()  > minBound) {
				count++;
			}
		}
		System.out.println("original selected " + count);
		count = 0;
		for (Room room : m.getRooms()) {
			if(room.getWidth() > widthMean
					&& room.getHeight() > heightMean) {
				room.setSelected(true);
				count++;
				setMessage("Selected rooms: " + count);
				if(!fast) {
					repaint(300);
				}
			}
		}
		setMessage("Press Enter to create the graph");
		m.addStat("Selected rooms: " + count);
		repaint(200);
	}

}
