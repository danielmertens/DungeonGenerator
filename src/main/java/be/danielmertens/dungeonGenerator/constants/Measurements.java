package be.danielmertens.dungeonGenerator.constants;

public class Measurements {

	public static final int GRIDSIZE = 16;
	
	public static int ROOM_HEIGHT_MAX = 15;
	public static int ROOM_HEIGHT_MIN = 2;
	
	public static int ROOM_WIDTH_MAX = 15;
	public static int ROOM_WIDTH_MIN = 2;
	
	public int getMeanHeight() {
		return (ROOM_HEIGHT_MAX + ROOM_HEIGHT_MIN) / 2;
	}
	
	public int getMeanWidth() {
		return (ROOM_WIDTH_MAX + ROOM_WIDTH_MIN) / 2;
	}
	
}
