package org.cytoscape.blueprints;


import static org.junit.Assert.assertNotNull;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.AbstractCyNetworkTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraphFactory;

public class GraphCytoscapeTest extends AbstractCyNetworkTest{

	@Mock
	CyEventHelper eventHelper;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		final Graph graph = TinkerGraphFactory.createTinkerGraph();
		graph.clear();
		net = new GraphCytoscape(graph, eventHelper);
	}
	

	@Test
	public void testGraphCytoscape() throws Exception {
		assertNotNull(net);
	}
}
