package be.danielmertens.dungeonGenerator.algo.s6_Connecting;

import be.danielmertens.dungeonGenerator.algo.Algorithm;
import be.danielmertens.dungeonGenerator.controller.Controller;
import be.danielmertens.dungeonGenerator.model.Model;
import be.danielmertens.dungeonGenerator.model.Room;

public class AStar_Path extends Algorithm {
	
	private MapNode[][] map;

	public AStar_Path(Model m, Controller c) {
		super(m, c);
	}

	@Override
	protected void run(boolean fast) {
		
	}
	
//	private void checkAndPrepareRooms() {
//		for (Room room : m.getRooms()) {
//			if(room.getX() < 0)
//		}
//	}

	
	
}
