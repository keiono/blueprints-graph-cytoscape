package org.cytoscape.blueprints.util;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;

import com.tinkerpop.blueprints.pgm.Graph;

public final class GraphConverter {
	
	
	public GraphConverter(final CyNetworkFactory factory) {
		
	}
	
	/**
	 * Convert any Blueprints graph to on-memory CyNetwork object.
	 * @param graph
	 * @return
	 */
	public CyNetwork toCyNetwork(final Graph graph) {
		return null;
	}
	
	public Graph toGraph(CyNetwork network) {
		// TODO: Define factory interface for Blueprints graph.
		
		return null;
	}
}
