package be.danielmertens.dungeonGenerator;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;

import be.danielmertens.dungeonGenerator.constants.Measurements;
import be.danielmertens.dungeonGenerator.model.Edge;
import be.danielmertens.dungeonGenerator.model.Model;
import be.danielmertens.dungeonGenerator.model.Room;

public class View extends JPanel {
	
	private static final Font font = new Font("serif", Font.PLAIN, 20);

	private Model m;
	private Image buffer;
	private String message = "Press ENTER to start.";

	public View(Model m) {
		this.m = m;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(buffer == null || buffer.getWidth(this) != getWidth() || buffer.getHeight(this) != getHeight()) {
			buffer = createImage(getWidth(), getHeight());
		}
		Graphics gBuffer = buffer.getGraphics();
		gBuffer.setFont(font);
		gBuffer.setColor(Color.BLACK);
		gBuffer.fillRect(0, 0, getWidth(), getHeight());
		paintRooms(gBuffer);
		if(m.graph != null) {
			paintGraph(gBuffer);
		}
		paintStats(gBuffer);
		gBuffer.setColor(Color.WHITE);
		gBuffer.setFont(font);
		gBuffer.drawString(message, 10, gBuffer.getFontMetrics().getAscent() + 5);
		g.drawImage(buffer, 0, 0, this);
	}

	private void paintStats(Graphics g) {
		int x = 5;
		for (int i = 0; i < m.areaArray.length; i++) {
			x += 8;
			int h = m.areaArray[i] * 5;
			if(h == 0)
				h = 2;
			if(i == (int) m.getAreaMean())
				g.setColor(Color.RED);
			else if(i == (int) (2 * m.getAreaMean()))
				g.setColor(Color.WHITE);
			else
				g.setColor(Color.GREEN);
			g.fillRect(x, getHeight() - 10 - h, 4, h);
		}
		
		ArrayList<String> stats = m.getStats();
		int y = 10 + g.getFontMetrics().getHeight();
		g.setColor(Color.WHITE);
		for (String stat : stats) {
			g.drawString(stat, getWidth() - 10 - g.getFontMetrics().stringWidth(stat), y);
			y += g.getFontMetrics().getHeight() + 10;
		}
	}

	private void paintRooms(Graphics g) {
		m.getRooms().stream().filter(r -> !r.locked && !r.moving).forEach(r -> r.render(g));
		m.getRooms().stream().filter(r -> r.locked).forEach(r -> r.render(g));
		m.getRooms().stream().filter(r -> r.moving).forEach(r -> r.render(g));
	}
	
	private void paintGraph(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Stroke temp = g2d.getStroke();
		Stroke lineStroke = new BasicStroke(3);
		g2d.setColor(Color.GREEN);
		
		HashMap<Integer, Point> roomMap = new HashMap<>();
		
		for (Edge edge : m.graph) {
			Point start = roomMap.get(edge.either());
			Point end= roomMap.get(edge.other(edge.either()));
			if(start == null) {
				Room r = m.getRooms().stream().filter(rm -> rm.getNumber() == edge.either()).findFirst().get();
				start = new Point();
				start.setLocation((r.getX() + r.getWidth() / 2.0) * Measurements.GRIDSIZE, (r.getY() + r.getHeight() / 2.0) * Measurements.GRIDSIZE);
				roomMap.put(edge.either(), start);
				g2d.fillOval(start.x - 10, start.y - 10, 20, 20);
			}
			if(end == null) {
				final int number = edge.other(edge.either());
				Room r = m.getRooms().stream().filter(rm -> rm.getNumber() == number).findFirst().get();
				end = new Point();
				end.setLocation((r.getX() + r.getWidth() / 2.0) * Measurements.GRIDSIZE, (r.getY() + r.getHeight() / 2.0) * Measurements.GRIDSIZE);
				roomMap.put(number, end);
				g2d.fillOval(end.x - 10, end.y - 10, 20, 20);
			}
			
			g2d.setStroke(lineStroke);
			g2d.drawLine(start.x, start.y, end.x, end.y);
			g2d.setStroke(temp);
		}
	}

}
