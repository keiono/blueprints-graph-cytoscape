package org.cytoscape.blueprints;

import static org.junit.Assert.assertNotNull;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.subnetwork.AbstractCyRootNetworkTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraphFactory;

public class GraphRootNetworkTest extends AbstractCyRootNetworkTest {

	@Mock
	CyEventHelper eventHelper;

	// Singleton.
	private static Graph graph;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		graph = TinkerGraphFactory.createTinkerGraph();
		graph.clear();
		root = new GraphCytoscape(graph, eventHelper);
	}
	

	@Test
	public void testGraphCytoscape() throws Exception {
		assertNotNull(root);
	}
}
