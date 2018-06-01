package be.danielmertens.dungeonGenerator.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.SwingUtilities;

import be.danielmertens.dungeonGenerator.View;
import be.danielmertens.dungeonGenerator.algo.Algorithm;
import be.danielmertens.dungeonGenerator.algo.s1_creation.CircleCreation;
import be.danielmertens.dungeonGenerator.algo.s2_positioning.Dijkstra_FirstAvailable;
import be.danielmertens.dungeonGenerator.algo.s3_selection.MeanSelection;
import be.danielmertens.dungeonGenerator.algo.s4_graphCreation.DelaunayGraph;
import be.danielmertens.dungeonGenerator.algo.s5_edgeSelection.PrimMST;
import be.danielmertens.dungeonGenerator.constants.Measurements;
import be.danielmertens.dungeonGenerator.model.Model;
import be.danielmertens.dungeonGenerator.model.Room;

public class Controller implements KeyListener, MouseListener {

	private Model m;
	private View v;
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
}
