package org.cytoscape.blueprints;

import org.cytoscape.model.AbstractCyEdgeTest;
import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraphFactory;

public class EdgeCytoscapeTest extends AbstractCyEdgeTest {

	public void setUp() throws Exception {
		final Graph graph = TinkerGraphFactory.createTinkerGraph();
		graph.clear();
		net =  new GraphCytoscape(graph);
	}

	
	public void tearDown() throws Exception {
	}
	
}
