package be.danielmertens.dungeonGenerator.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.swing.SwingUtilities;

import be.danielmertens.dungeonGenerator.Algorithms;
import be.danielmertens.dungeonGenerator.View;
import be.danielmertens.dungeonGenerator.algo.Algorithm;
import be.danielmertens.dungeonGenerator.algo.s1_creation.CircleCreation;
import be.danielmertens.dungeonGenerator.algo.s2_positioning.Dijkstra_FirstAvailable;
import be.danielmertens.dungeonGenerator.algo.s3_selection.MeanSelection;
import be.danielmertens.dungeonGenerator.algo.s4_graphCreation.DelaunayGraph;
import be.danielmertens.dungeonGenerator.algo.s5_edgeSelection.LazyPrimMST;
import be.danielmertens.dungeonGenerator.algo.s5_edgeSelection.PrimMST;
import be.danielmertens.dungeonGenerator.constants.Measurements;
import be.danielmertens.dungeonGenerator.model.Edge;
import be.danielmertens.dungeonGenerator.model.EdgeWeightedGraph;
import be.danielmertens.dungeonGenerator.model.Model;
import be.danielmertens.dungeonGenerator.model.Position;
import be.danielmertens.dungeonGenerator.model.Room;

public class Controller implements KeyListener, MouseListener {

	private Model m;
	private View v;
	private boolean running;
	private Algorithms algo;
	private int step;
	private boolean fast;
	private boolean repaintBlock;
	private boolean autorun;
	private Algorithm algorithm;
	
	public Controller(Model m, View v) {
		this.m = m;
		this.v = v;
		//this.algo = new Algorithms();
		this.step = 0;
	}

	@Override
	public void keyPressed(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}
	
	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER)
			startActions();
		if(e.getKeyCode() == KeyEvent.VK_F)
			fast = !fast;
		if(e.getKeyCode() == KeyEvent.VK_A)
			autoRun();
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(step == 3) {
			double x = e.getX() / (double) Measurements.GRIDSIZE;
			double y = e.getY() / (double) Measurements.GRIDSIZE;
			for (Room room : m.getRooms()) {
				if(room.inside(x, y)) {
					room.setSelected(!room.isSelected());
					v.repaint();
					break;
				}
			}
		}
	}
	
	private void autoRun() {
		autorun = true;
		fast = true;
		repaintBlock = true;
		Thread t = null;
		do {
			t = startActions();
			if(t != null)
				try {
					synchronized(t){
					    t.wait();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		} while(t != null);
		repaintBlock = false;
		repaint(100);
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}
	
//	public Thread startActions() {
//		if(running) return null;
//		running = true;
//		Thread t;
//		switch (step) {
//		case 0:
//			t = new Thread(() -> generateRooms());
//			break;
//		case 1:
//			t = new Thread(() -> positionRooms());
//			break;
//		case 2:
//			t = new Thread(() -> selectMainRooms());
//			break;
//		case 3:
//			t = new Thread(() -> createGraph());
//			break;
//		case 4:
//			t = new Thread(() -> minimumSpanningGraph());
//			break;
//			
//		default:
//			running = false;
//			return null;
//		}
//		
//		t.start();
//		return t;
//	}
	
	private boolean isRunning() {
		if(algorithm == null) return false;
		return algorithm.isRunning();
	}
	
	public Thread startActions() {
		if(isRunning()) return null;
		
		switch (step) {
		case 0:
			algorithm = new CircleCreation(m, this, v.getWidth(), v.getHeight());
			break;
		case 1:
			algorithm = new Dijkstra_FirstAvailable(m, this, v.getWidth(), v.getHeight());
			break;
		case 2:
			algorithm = new MeanSelection(m, this);
			break;
		case 3:
			algorithm = new DelaunayGraph(m, this);
			break;
		case 4:
			algorithm = new PrimMST(m, this);
			break;
		default:
			algorithm = null;
			return null;
		}
		Thread t = new Thread(() -> algorithm.execute(fast));
		t.start();
		step++;
		return t;
	}
	
	public void repaint(int time) {
		if(repaintBlock) return;
		SwingUtilities.invokeLater(() -> v.repaint());
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void setMessage(String mess) {
		if(fast)
			mess = "FAST | " + mess;
		if(autorun)
			mess = "AutoRun | " + mess;
		
		v.setMessage(mess);
	}
	
	private void generateRooms() {
		System.out.println("start algo.");
		int count = 0;
		while(count < 150) {
			//System.out.println(count);
			Room r = algo.generateRoom(v.getWidth() / Measurements.GRIDSIZE, v.getHeight() / Measurements.GRIDSIZE);
			r.setNumber(count);
			m.addRoom(r);
			setMessage("Creating rooms: " + (count + 1));
			if(!fast) {
				repaint(100);
			}
			count++;
		}
		m.addStat("Rooms: " + count);
		running = false;
		setMessage("Press ENTER to start positioning.");
		step++;
		repaint(100);
	}
	
	private void positionRooms() {
		System.out.println("start positioning");
		
		HashMap<Room, Double> distanceMap = new HashMap<>();
		for (Room room : m.getRooms()) {
			distanceMap.put(room, room.distaceFrom(v.getWidth() / Measurements.GRIDSIZE / 2, v.getHeight() / Measurements.GRIDSIZE / 2));
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
			Room current = freeRoooms.get(ThreadLocalRandom.current().nextInt(max));
			setMessage("Rooms in queue: " + freeRoooms.size());
			LinkedList<Position> path = algo.findLockLocation(current, m.getRooms());
			
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
		running = false;
		setMessage("Press ENTER to select main rooms.");
		step++;
		repaint(100);
	}

	private void selectMainRooms() {
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
		
		for (Room room : m.getRooms()) {
			if(room.getArea()  > minBound) {
				//room.setSelected(true);
				count++;
				//v.setMessage("Selected rooms: " + count);
//				if(!fast) {
//					repaint(300);
//				}
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
		running = false;
		step++;
		repaint(200);
	}
	
	private void createGraph() {
		m.graph = algo.createDelaunayGraph(m.getRooms());
		setMessage("Press ENTER to create the MST");
		running = false;
		step++;
		repaint(200);
	}
	
	private void minimumSpanningGraph() {
		EdgeWeightedGraph ewg = new EdgeWeightedGraph(m.getRooms().size());
		for (Edge edge : m.graph) {
			ewg.addEdge(edge);
		}
		LazyPrimMST mst = new LazyPrimMST(ewg);
		ArrayList<Edge> mstEdges = new ArrayList<>();
		Iterator<Edge> it = mst.edges().iterator();
		while(it.hasNext()) {
			mstEdges.add(it.next());
		}
		m.graph = mstEdges.toArray(new Edge[mstEdges.size()]);
		setMessage("Next step");
		running = false;
		step++;
		repaint(200);
	}

}
