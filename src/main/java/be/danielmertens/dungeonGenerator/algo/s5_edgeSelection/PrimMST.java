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
		for (Edge edge : m.graph) {
			ewg.addEdge(edge);
		}
		LazyPrimMST mst = new LazyPrimMST(ewg);
		ArrayList<Edge> mstEdges = new ArrayList<>();
		Iterator<Edge> it = mst.edges().iterator();
		while(it.hasNext()) {
			mstEdges.add(it.next());
		}
		int mstSize = mstEdges.size();
		int addNumber = (int) ((m.graph.length - mstSize) * 0.1);
		while(addNumber > 0) {
			int index = random().nextInt(m.graph.length);
			if(!mstEdges.contains(m.graph[index])) {
				mstEdges.add(m.graph[index]);
				addNumber--;
			}
		}
		m.graph = mstEdges.toArray(new Edge[mstEdges.size()]);
		setMessage("Next step");
		repaint(200);
	}

}
