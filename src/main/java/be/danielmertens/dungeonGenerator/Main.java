package be.danielmertens.dungeonGenerator;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import be.danielmertens.dungeonGenerator.controller.Controller;
import be.danielmertens.dungeonGenerator.model.Model;

public class Main extends JFrame {
	
	private int width = 2000;
	private int height = 2000;
	
	private Model m;
	private View v;
	private Controller c;
	
	public Main() {
		super("Dungeon generator");
		
		JFrame temp = new JFrame();
		temp.pack();
		Insets insets = temp.getInsets();
		
		setSize(insets.left + insets.right + width, insets.top + insets.bottom + height);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screenSize.width / 2 - width / 4, 30);
		
		m = new Model();
		v = new View(m);
		this.add(v);
		c = new Controller(m, v);
		this.addKeyListener(c);
		
		this.addMouseListener(c);
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});
		setVisible(true);
	}
	
	
	
}
