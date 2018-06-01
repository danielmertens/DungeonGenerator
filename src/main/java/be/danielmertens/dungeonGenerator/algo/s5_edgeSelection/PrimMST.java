package be.danielmertens.dungeonGenerator.algo.s5_edgeSelection;

import java.util.ArrayList;
import java.util.Iterator;

import be.danielmertens.dungeonGenerator.algo.Algorithm;
import be.danielmertens.dungeonGenerator.controller.Controller;
import be.danielmertens.dungeonGenerator.model.Edge;
import be.danielmertens.dungeonGenerator.model.EdgeWeightedGraph;
import be.danielmertens.dungeonGenerator.model.Model;

public class PrimMST extends Algorithm {

	public PrimMST(Model m, Controller c) {
		super(m, c);
	}

	@Override
	protected void run(boolean fast) {
		EdgeWeightedGraph ewg = new EdgeWeightedGraph(m.getRooms().size());
		Edge[] originalEdges = m.graph;
		
		for (Edge edge : originalEdges) {
			ewg.addEdge(edge);
		}
		
		LazyPrimMST mst = new LazyPrimMST(ewg);
		ArrayList<Edge> mstEdges = new ArrayList<>();
		Iterator<Edge> it = mst.edges().iterator();
		while(it.hasNext()) {
			mstEdges.add(it.next());
		}
		m.graph = mstEdges.toArray(new Edge[mstEdges.size()]);
		repaint(4000);
		
		// Adding some edges back to the pool
		int mstSize = mstEdges.size();
		int addNumber = (int) (originalEdges.length * 0.1);
		System.out.println("Adding " + addNumber + " edges");
		while(addNumber > 0) {
			int index = random().nextInt(originalEdges.length);
			if(!mstEdges.contains(originalEdges[index])) {
				mstEdges.add(originalEdges[index]);
				addNumber--;
			}
		}
		m.graph = mstEdges.toArray(new Edge[mstEdges.size()]);
		setMessage("Next step");
		repaint(200);
	}

}
