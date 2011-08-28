package org.cytoscape.blueprints.util;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;

import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraph;

public final class GraphConverter {
	
	
	public GraphConverter(final CyNetworkFactory factory) {
		
	}
	
	/**
	 * Convert any Blueprints graph to on-memory CyNetwork object.
	 * @param graph
	 * @return
	 */
	public CyNetwork toCyNetwork(final Graph graph) {
		//CyNetwork cyNet = new ArrayGraph();
		return null;
	}
	
	public Graph toGraph(CyNetwork network) {
		// TODO: Define factory interface for Blueprints graph.
		Graph graph = new TinkerGraph();
		for (CyNode v : network.getNodeList()) {
			graph.addVertex(v.getSUID());
		}
		for (CyEdge e : network.getEdgeList()) {
			Vertex s = graph.getVertex(e.getSource().getSUID());
			Vertex t = graph.getVertex(e.getTarget().getSUID());
			if (e.isDirected()) {
				graph.addEdge(e.getSUID(), s, t, "DIRECTED");
			} else {
				graph.addEdge(e.getSUID(), s, t, "UNDIRECTED");
			}
		}
		return null;
	}
}
