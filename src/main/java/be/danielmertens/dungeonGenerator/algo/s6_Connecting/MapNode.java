package be.danielmertens.dungeonGenerator.algo.s6_Connecting;

public abstract class MapNode {
	
	public abstract TileType getType();
	
	
	public enum TileType {
		WALL, PATH, ROOM, DOOR, SECRETDOOR
	}	
}

