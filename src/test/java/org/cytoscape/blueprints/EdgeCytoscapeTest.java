package org.cytoscape.blueprints;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.AbstractCyEdgeTest;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraphFactory;

public class EdgeCytoscapeTest extends AbstractCyEdgeTest {

	@Mock CyEventHelper eventHelper;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		final Graph graph = TinkerGraphFactory.createTinkerGraph();
		graph.clear();
		net = new GraphCytoscape(graph, eventHelper);
	}
}
