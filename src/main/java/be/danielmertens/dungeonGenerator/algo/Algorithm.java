package be.danielmertens.dungeonGenerator.algo;

import java.util.Random;

import be.danielmertens.dungeonGenerator.controller.Controller;
import be.danielmertens.dungeonGenerator.model.Model;

public abstract class Algorithm {

	protected Model m;
	private Controller c;
	
	private boolean running;
	
	public Algorithm(Model m, Controller c) {
		this.m = m;
		this.c = c;
	}
	
	protected final void repaint(int sleepTime) {
		c.repaint(sleepTime);
	}
	
	protected final void setMessage(String message) {
		c.setMessage(message);
	}
	
	protected final Random random() {
		return m.getRandom();
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public final void execute(boolean fast) {
		running = true;
		run(fast);
		running = false;
	}
	
	protected abstract void run(boolean fast);
	
}
